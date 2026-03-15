<template>
	<view>
		<!-- #ifdef APP-PLUS || MP-WEIXIN-->
		<view class="background-color"></view>
		<!-- #endif -->
		<view class="tips color_fff size_12 align_c" :class="{ 'show':ajax.loading }">{{ajax.loadText}}</view>
		<view class="box-1" id="list-box" :style="{'padding-bottom':bottomHeight+'rpx'}">
			<view class="talk-list" :style="{'padding-bottom':keyboardHeight+'rpx'}">
				<view class="message-list" v-for="(item,index) in personMessage[s_index].list" :key="index"
					:id="`msg-${item.id}`">
					<view
						v-if="index===0||timeShowAble(personMessage[s_index].list[index-1].sendTime,personMessage[s_index].list[index].sendTime)"
						class="time">{{timeShow(item.sendTime)}}</view>
					<view v-if="item.isWithdrawn===1" class="time">
						{{item.senderId == loginUserInfo.uid ? '你撤回了一条消息':'对方撤回了一条消息'}}
					</view>
					<view v-if="item.isWithdrawn===0" class="item flex_col"
						:class=" item.senderId == loginUserInfo.uid ? 'push':'pull' ">
						<image @tap="gotoPersonInfo(item.senderId)"
							:src="item.senderId == loginUserInfo.uid ? loginUserInfo.avatar:(chattingUserInfo?.avatar || '')"
							mode="aspectFill" class="pic"></image>
						<view class="content" @longpress="onLongPress($event, item)">
							<rich-text v-if="item.messageType==='text'" :nodes="replaceEmoji(item.content)"></rich-text>

							<image v-if="item.messageType==='image'" mode="aspectFill" class="message-image"
								:src="showImage(item.content,'compress')" @tap="showImage(item.content,'origin')">
							</image>
							<video v-if="item.messageType==='video'" class="message-video" :src="item.content"></video>
							<view v-if="item.messageType==='file'">
								<image :src="$IMG+'/im/clip.png'" style="width: 40rpx;height: 40rpx;"></image>
								<text style="display: block;">附件：{{JSON.parse(item.content).name}}</text>
								<text>大小：{{JSON.parse(item.content).size}}</text>
								<button style="background-color: #2b85e4;"
									@tap="downloadFile(item.content)">点击下载</button>
							</view>
						</view>
					</view>
				</view>
			</view>
			<!-- 底部占位，确保消息列表不会被输入框遮挡 -->
			<view class="bottom-spacer" :style="{'height': bottomHeight + 'rpx'}"></view>
		</view>


		<view class="shade" v-if="showShade" @tap="hidePop">
			<view class="pop" :style="popStyle" :class="{'show':showPop}">
				<view v-for="(item,index) in popButton" :key="index" @tap="pickerMenu" :data-index="index">{{item}}
				</view>
			</view>
		</view>

	</view>
</template>

<script setup>
	import {
		ref,
		computed,
		watch,
		getCurrentInstance,
		onMounted,
		nextTick
	} from 'vue'
	import {
		useStore
	} from 'vuex'
	import EmojiUtil from '@/static/emoji/replaceEmoji.js'
	import timeUtil from '@/utils/timeUtil.js'

	const {
		proxy
	} = getCurrentInstance()
	const $IMG = ref(proxy.$IMG)
	const $H = proxy.$H
	const $c = proxy.$c
	const store = useStore()

	// 响应式数据
	const ajax = ref({
		rows: 20, //每页数量
		page: 1, //页码
		flag: true, // 请求开关
		loading: true, // 加载中
		loadText: '正在获取消息'
	})
	const content = ref('')
	const bottomHeight = ref(150)
	const keyboardHeight = ref(20)
	const s_index = ref(0)
	const winSize = ref({})
	const showShade = ref(false)
	const showPop = ref(false)
	const popButton = ref(["复制", "撤回"])
	const popStyle = ref("")
	const pickerUserIndex = ref(-1)
	const selectedMessage = ref({})
	const lastPosition = ref(null)

	// 计算属性
	const personMessage = computed(() => store.getters.personMessage)
	const chattingUserInfo = computed(() => store.getters.chattingUserInfo)
	const loginUserInfo = computed(() => store.getters.loginUserInfo)
	const lastUpdateSession = computed(() => store.getters.lastUpdateSession)

	// 方法
	/* 获取窗口尺寸 */
	const getWindowSize = () => {
		uni.getSystemInfo({
			success: (res) => {
				winSize.value = {
					"witdh": res.windowWidth,
					"height": res.windowHeight
				}
			}
		})
	}

	const goToBottom = () => {
		uni.pageScrollTo({
			scrollTop: 999999,
			duration: 0
		})
		hideLoadTips(true)
	}

	// 监听
	watch(lastUpdateSession, (newSession, oldSession) => {
		if (newSession.id == chattingUserInfo.value.sessionId) {
			nextTick(() => {
				goToBottom()
			})
		}
	})

	watch(lastPosition, (newData, oldData) => {
		if (oldData == null) return
		let selector = `#msg-${oldData}`
		nextTick(() => {
			// 设置当前滚动的位置
			setPageScrollTo(selector)
		})
	})

	// 在 created 逻辑中执行的代码
	getWindowSize()
	for (let i = 0; i < personMessage.value.length; i++) {
		if (personMessage.value[i].sessionId == chattingUserInfo.value.sessionId) {
			s_index.value = i
			break
		}
	}
	if (personMessage.value[s_index.value].list.length != 0) {
		lastPosition.value = personMessage.value[s_index.value].list[0].id
	}

	// 初始化
	onMounted(() => {
		nextTick(() => {
			goToBottom()
		})
	})

	// 其他方法
	const gotoPersonInfo = (senderId) => {
		if (senderId == loginUserInfo.value.uid) {
			uni.navigateTo({
				url: '/pages/user/home'
			})
		} else {
			uni.navigateTo({
				url: '/pages/user/home?uid=' + chattingUserInfo.value.chattingUserId
			})
		}
	}

	// 设置页面滚动位置
	const setPageScrollTo = (selector) => {
		let view = uni.createSelectorQuery().in(proxy).select(selector)
		view.boundingClientRect((res) => {
			uni.pageScrollTo({
				scrollTop: res.top - 30, // -30 为多显示出大半个消息的高度，示意上面还有信息。
				duration: 0
			})
		}).exec()
	}

	const showLoadTips = (str) => {
		ajax.value.loadText = str
		ajax.value.loading = true
		setTimeout(() => {
			ajax.value.loading = false
		}, 800)
	}

	const getHistoryMsg = () => {
		if (!personMessage.value[s_index.value].hasNextPage) {
			showLoadTips('到顶啦')
			return
		}
		showLoadTips('正在加载')

		$H.post('chat/list', {
			sessionId: chattingUserInfo.value.sessionId,
			pageNum: personMessage.value[s_index.value].pageNum + 1,
			pageSize: personMessage.value[s_index.value].pageSize,
			lastMessageId: personMessage.value[s_index.value].lastMessageId
		}).then(res => {
			let page = res.result.pageInfo
			let current = Number(page.current)
			let total = Number(page.total)
			let size = Number(page.size)
			store.state.personMessage[s_index.value].hasNextPage = current * size < total
			store.state.personMessage[s_index.value].pageNum = current
			lastPosition.value = page.records[0].id
			store.state.personMessage[s_index.value].list = page.records.reverse().concat(store.state
				.personMessage[s_index.value].list)
		})
	}

	//文件下载
	const downloadFile = (content) => {
		let t = JSON.parse(content)
		// console.log('==>', content)
		uni.showModal({
			title: '提示',
			content: '是否下载此文件',
			confirmText: '确定',
			confirmColor: '#aaaaff',
			cancelText: '取消',
			success: (res) => {
				if (res.confirm) {
					if (res.confirm) {
						const domain = $c.domain || ''
						const imfile = $c.imfile || ''
						//#ifdef H5				
						window.location.href = domain + 'im/file/download?url=' + t.url + '&name=' + t.name
						//#endif
						//#ifdef APP-PLUS
						plus.runtime.openURL(domain + 'im/file/download?url=' + t.url + '&name=' + t.name)
						//#endif
						// #ifdef MP-WEIXIN
						//方法一：外链跳转   如果用跳转外链的方式 需要微信公众平台配置跳转域名
						// proxy.$f.jump('/pages/webview/webview?src='+imfile+'/resource/file/'+t.url)
						//方法二：下载
						uni.downloadFile({
							url: imfile + '/resource/file/' + t.url,
							success: (res2) => {
								if (res2.statusCode === 200) {
									console.log('下载成功', res2)
									uni.saveFile({
										tempFilePath: res2.tempFilePath,
										success: function(res3) {
											console.log('res3', res3)
											uni.openDocument({
												filePath: res3
													.savedFilePath,
												success(ok) {
													console.log('成功打开文档',
														ok)
												},
												fail(err) {
													console.log(err)
												}
											})
										}
									})
								}
							}
						})
						// #endif
					}
				}
			}
		})
	}

	//显示图片showImage
	const showImage = (imageString, type) => {
		let image = JSON.parse(imageString)
		if (type === 'compress') return image.compressUrl
		else {
			uni.previewImage({
				urls: [image.originUrl]
			})
		}
	}

	const replaceEmoji = (text) => {
		if (!EmojiUtil || !EmojiUtil.replaceEmoji) {
			return text
		}
		return EmojiUtil.replaceEmoji(text)
	}

	const timeShow = (time) => {
		return timeUtil.timeShowFormat(time)
	}

	const timeShowAble = (time1, time2) => {
		if (timeUtil.TimeDifferenceSeconds(time1, time2) > 300) {
			return true
		} else {
			return false
		}
	}

	const setBottomHeigth = (height) => {
		bottomHeight.value = height
		// 延迟滚动，确保布局完成（兼容各端键盘/弹窗动画）
		nextTick(() => {
			setTimeout(() => {
				// 尝试滚动到最后一条消息的位置，若失败则回退到最大滚动值
				try {
					const list = personMessage.value[s_index.value]?.list || []
					if (list.length > 0) {
						const lastId = list[list.length - 1].id
						setPageScrollTo(`#msg-${lastId}`)
					} else {
						uni.pageScrollTo({ scrollTop: 99999999, duration: 0 })
					}
				} catch (e) {
					uni.pageScrollTo({ scrollTop: 99999999, duration: 0 })
				}
			}, 60)
		})
	}

	const setKeyboardHeight = (height) => {
		keyboardHeight.value = height
		// 键盘高度变化后也尝试滚动到底部，延迟以等待输入框动画完成
		nextTick(() => {
			setTimeout(() => {
				try {
					const list = personMessage.value[s_index.value]?.list || []
					if (list.length > 0) {
						const lastId = list[list.length - 1].id
						setPageScrollTo(`#msg-${lastId}`)
					} else {
						uni.pageScrollTo({ scrollTop: 99999999, duration: 0 })
					}
				} catch (e) {
					uni.pageScrollTo({ scrollTop: 99999999, duration: 0 })
				}
			}, 60)
		})
	}

	// 隐藏加载提示
	const hideLoadTips = (flag) => {
		if (flag) {
			ajax.value.loadText = '消息获取成功'
			setTimeout(() => {
				ajax.value.loading = false
			}, 300)
		} else {
			ajax.value.loading = true
			ajax.value.loadText = '正在获取消息'
		}
	}

	/* 长按监听 */
	const onLongPress = (e, message) => {
		selectedMessage.value = message
		let [touches, style] = [e.touches[0], ""]
		/* 因 非H5端不兼容 style 属性绑定 Object ，所以拼接字符 */
		if (touches.clientY > (winSize.value.height / 2)) {
			style = `bottom:${winSize.value.height-touches.clientY}px;`
		} else {
			style = `top:${touches.clientY}px;`
		}
		if (touches.clientX > (winSize.value.witdh / 2)) {
			style += `right:${winSize.value.witdh-touches.clientX}px`
		} else {
			style += `left:${touches.clientX}px`
		}

		popStyle.value = style
		showShade.value = true
		nextTick(() => {
			setTimeout(() => {
				showPop.value = true
			}, 10)
		})
	}

	/* 隐藏弹窗 */
	const hidePop = () => {
		showPop.value = false
		pickerUserIndex.value = -1
		setTimeout(() => {
			showShade.value = false
		}, 250)
	}

	/* 选择菜单 */
	const pickerMenu = (e) => {
		let index = Number(e.currentTarget.dataset.index)
		switch (index) {
			case 0:
				uni.setClipboardData({
					data: selectedMessage.value.content,
					success: function() {
						uni.showToast({
							title: '复制成功',
							icon: 'none'
						})
					}
				})
				break
			case 1:
				withdrawMessage()
				break
		}

		hidePop()
	}

	const withdrawMessage = () => {
		if (selectedMessage.value.senderId != loginUserInfo.value.uid) {
			uni.showToast({
				title: '只能撤回自己的消息哦',
				icon: 'none'
			})
			return
		}
		let seconds = timeUtil.TimeDifferenceSeconds(selectedMessage.value.sendTime, timeUtil.getFormatTime(new Date()))
		//超过3分钟不可以撤回
		if (seconds > 180) {
			uni.showToast({
				icon: 'none',
				title: '只能撤回三分钟以内的消息哦'
			})
			return
		}
		let message = {
			type: 'person-withdraw',
			data: {
				sessionId: chattingUserInfo.value.sessionId,
				messageId: selectedMessage.value.id,
				senderId: loginUserInfo.value.uid,
				receiverId: chattingUserInfo.value.chattingUserId
			}
		}
		uni.sendSocketMessage({
			data: JSON.stringify(message),
			fail() {
				uni.showToast({
					icon: 'error',
					title: '撤回失败'
				})
			}
		})
	}

	// 暴露方法给父组件
	defineExpose({
		getHistoryMsg,
		setBottomHeigth,
		setKeyboardHeight
	})
</script>
<style lang="scss">
	page {
		background-color: #f7f7f7;
		font-size: 28rpx;
		box-sizing: border-box;
		color: #000000;
		letter-spacing: 0;
		word-wrap: break-word;
	}
</style>
<style lang="scss" scoped>
	/* 列式弹性盒子 */
	.flex_col {
		display: flex;
		flex-direction: row;
		flex-wrap: nowrap;
		justify-content: flex-start;
		align-items: center;
		align-content: center;
	}

	/* 行式弹性盒子 */
	.flex_row {
		display: flex;
		flex-direction: column;
		flex-wrap: nowrap;
		justify-content: flex-start;
		align-items: flex-start;
		align-content: flex-start;
	}

	/* 弹性盒子弹性容器 */
	.flex_col .flex_grow {
		width: 0;
		flex-grow: 1;
	}

	.flex_row .flex_grow {
		flex-grow: 1;
	}

	/* 弹性盒子允许换行 */
	.flex_col.flex_wrap {
		flex-wrap: wrap;
	}

	/* 弹性盒子居中对齐 */
	.flex_col.flex_center,
	.flex_row.flex_center {
		justify-content: center;
	}

	/* 列式弹性盒子两端对齐 */
	.flex_col.flex_space {
		justify-content: space-between;
	}

	/* 弹性盒子快速分栏 */
	.flex_col.flex_col_2>view {
		width: 50%;
	}

	.flex_col.flex_col_3>view {
		width: 33.33333%;
	}

	.flex_col.flex_col_4>view {
		width: 25%;
	}

	.flex_col.flex_col_5>view {
		width: 20%;
	}

	.flex_col.flex_col_6>view {
		width: 16.66666%;
	}

	.color_fff {
		color: #fff;
	}


	/* 背景色*/
	.bg_fff {
		background-color: #ffffff;
	}

	/* 字体大小 */
	.size_10 {
		font-size: 20rpx;
	}

	.size_12 {
		font-size: 24rpx;
	}

	.size_14 {
		font-size: 28rpx;
	}

	.size_16 {
		font-size: 32rpx;
	}

	.size_18 {
		font-size: 36rpx;
	}

	.size_20 {
		font-size: 40rpx;
	}

	/* 字体加粗 */
	.font_b {
		font-weight: bold;
	}

	/* 对齐方式 */
	.align_c {
		text-align: center;
	}

	.align_l {
		text-align: left;
	}

	.align_r {
		text-align: right;
	}

	/* 遮罩 */
	.shade {
		position: fixed;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		background-color: rgba(0, 0, 0, 0.8);
		z-index: 100;
	}

	/* 弹窗 */
	.shade_box {
		position: fixed;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		margin: auto;
		z-index: 101;
		min-width: 200rpx;
		min-height: 200rpx;
	}

	.background-color {
		position: fixed;
		background-color: #f7f7f7;
		z-index: -1;
		width: 100%;
		height: 100%;
		top: -1rpx;
	}

	.text-show {
		display: -webkit-box;
		overflow: hidden;
		text-overflow: ellipsis;
		word-wrap: break-word;
		white-space: normal !important;
		-webkit-box-orient: vertical;
		height: 100%;
		vertical-align: text-bottom;
	}

	.text-show .emoji-text {
		width: 50rpx;
		height: auto;
		margin-bottom: -12rpx;
	}

	/* 加载数据提示 */
	.tips {
		position: fixed;
		left: 0;
		top: var(--window-top);
		width: 100%;
		z-index: 9;
		background-color: #939090;
		height: 72rpx;
		line-height: 72rpx;
		transform: translateY(-80rpx);
		transition: transform 0.3s ease-in-out 0s;
	}

	.tips.show {
		transform: translateY(0);
	}

	.box-1 {
		z-index: 1;
		width: 100%;
		height: auto;
		padding-bottom: 150rpx;
		box-sizing: content-box;
		/* 兼容iPhoneX */
		margin-bottom: 0;
		margin-bottom: constant(safe-area-inset-bottom);
		margin-bottom: env(safe-area-inset-bottom);
	}

	.talk-list {
		padding-bottom: 20rpx;
		z-index: 1;
		/* 消息项，基础类 */
	}

	.talk-list .message-image {
		width: 300rpx;
		height: 200rpx;
	}

	.talk-list .message-video {
		width: 400rpx;
		height: 300rpx;
		z-index: 0;
	}

	.talk-list .time {
		color: #323232;
		margin-left: auto;
		margin-right: auto;
		text-align: center;
		font-size: 25rpx;
		margin-top: 20rpx;
		z-index: 2;
	}

	.talk-list .item {
		padding: 20rpx 20rpx 0 20rpx;
		align-items: flex-start;
		align-content: flex-start;
		color: #333;
		/* 收到的消息 */
		/* 发出的消息 */
	}

	.talk-list .item .pic {
		width: 92rpx;
		height: 92rpx;
		border-radius: 50%;
		border: #fff solid 1px;
	}

	.talk-list .item .content {
		padding: 20rpx;
		border-radius: 4px;
		max-width: 500rpx;
		word-break: break-all;
		line-height: 52rpx;
		position: relative;
	}

	.talk-list .item.pull .content {
		margin-left: 32rpx;
		background-color: #fff;
	}

	.talk-list .item.pull .content::after {
		content: '';
		display: block;
		width: 0;
		height: 0;
		border-top: 16rpx solid transparent;
		border-bottom: 16rpx solid transparent;
		border-right: 20rpx solid #fff;
		position: absolute;
		top: 30rpx;
		left: -18rpx;
	}

	.talk-list .item.push {
		/* 主轴为水平方向，起点在右端。使不修改DOM结构，也能改变元素排列顺序 */
		flex-direction: row-reverse;
	}

	.talk-list .item.push .content {
		margin-right: 32rpx;
		background-color: #75a0ea;
	}

	.talk-list .item.push .content::after {
		content: '';
		display: block;
		width: 0;
		height: 0;
		border-top: 16rpx solid transparent;
		border-bottom: 16rpx solid transparent;
		border-left: 20rpx solid #75a0ea;
		position: absolute;
		top: 30rpx;
		right: -18rpx;
	}

	/* 遮罩 */
	.shade {
		position: fixed;
		z-index: 100;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		-webkit-touch-callout: none;
	}

	.shade .pop {
		position: fixed;
		z-index: 101;
		width: 200upx;
		box-sizing: border-box;
		font-size: 28upx;
		text-align: left;
		color: #333;
		background-color: #fff;
		box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
		line-height: 80upx;
		transition: transform 0.15s ease-in-out 0s;
		user-select: none;
		-webkit-touch-callout: none;
		transform: scale(0, 0);
	}

	.shade .pop.show {
		transform: scale(1, 1);
	}

	.shade .pop>view {
		padding: 0 20upx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		user-select: none;
		-webkit-touch-callout: none;
	}

	.shade .pop>view:active {
		background-color: #f3f3f3;
	}
</style>