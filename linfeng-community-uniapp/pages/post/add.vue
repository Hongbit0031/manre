<template>
	<view class="container">
		<view @click="protocol('app_standard_context')">
			<h-tips content="请遵守平台的《社区规范协议》,文明发帖"></h-tips>
		</view>
		<view class="one-line" style="margin-top: 38rpx;">
			<view class="top">
				<view class="title">
					标题
				</view>
				<view class="num">
					{{form.title.length}}/20
				</view>
			</view>
			<view class="content">
				<input class="txt" type="text" placeholder="起个标题叭~" maxlength="20" v-model="form.title">
			</view>
			<u-line margin="25rpx 0 0 0" length="690rpx" color="#F6F6F6"></u-line>
		</view>
		<u-line color="#c8c8c8"></u-line>
		<!-- 付费可见简介 -->
		<view class="one-line" style="margin-top: 38rpx;" v-if="form.cut==1">
			<view class="top">
				<view class="title">
					付费简介
				</view>
				<view class="num">
					{{form.brief.length}}/250
				</view>
			</view>
			<view class="content">
				<textarea placeholder="介绍付费内容,对用户可见.内容部分用户付费后可见" class="txt input-text post-txt" maxlength="250"
					:auto-height="true" v-model="form.brief"></textarea>
			</view>
			<u-line length="690rpx" color="#F6F6F6" margin="20rpx 0 32rpx 0"></u-line>
		</view>
		<!-- 内容 -->
		<view class="one-line" style="margin-top: 38rpx;">
			<view class="top">
				<view class="title">
					内容
				</view>
				<view class="num">
					{{form.content.length}}/400
				</view>
			</view>
			<view class="content">
				<textarea placeholder="说些什么叭..." class="txt input-text post-txt" maxlength="1000" :auto-height="true"
					v-model="form.content"></textarea>
			</view>
			<u-line length="690rpx" color="#F6F6F6" margin="20rpx 0 32rpx 0"></u-line>
		</view>
		<!-- 上传图片 -->
		<view v-if="form.type == 1" style="transform: translateX(-10rpx);">
			<u-upload ref="uploadRef" :size-type="['original']" name="Image" :max-count="9" :header="header"
				:action="uploadImgUrl" @on-success="onImageSuccess" @on-error="onUploadError"
				@on-progress="onUploadProgress" @on-choose-complete="onChooseComplete" @on-remove="onImageRemove"
				:auto-upload="true" upload-text="上传图片">
			</u-upload>
			<u-line length="690rpx" color="#F6F6F6" margin="30rpx 0 33rpx 0"></u-line>
		</view>
		<!-- 上传视频 -->
		<block v-if="form.type == 2">
			<view v-if="form.media.length == 0" class="upload-wrap" @click="chooseVideo">
				<image class="icon" src="/static/video.png"></image>
				<text>上传视频</text>
			</view>
			<video v-if="form.media.length > 0" @click="chooseVideo" :controls="false" :show-center-play-btn="false"
				class="upload-video" :src="form.media[0]"></video>
		</block>
		<!-- 选择圈子 -->
		<view v-if="isTopic" @click="navigateToChooseTopic" class="choose-item">
			<image class="icon" src="/static/p_1.png"></image>
			<text class="txt">{{ topicName }}</text>
			<u-icon class="u-icon" name="arrow-right"></u-icon>
		</view>
		<!-- 选择话题 -->
		<view v-if="form.topicId" @click="navigateToChooseDiscuss" class="choose-item">
			<image class="icon" src="/static/m_1.png"></image>
			<text class="txt">{{ disName }}</text>
			<u-icon class="u-icon" name="arrow-right"></u-icon>
		</view>
		<!-- 帖子类型 -->
		<view class="choose-item" v-if="payPostBtn">
			<image class="icon" src="/static/m_3.png"></image>
			<text class="txt">{{typeName}}</text>
			<u-radio-group class="radio" v-model="radiovalue1" placement="row" @change="groupChange">
				<u-radio v-for="(item, index) in radiolist1" :key="index" :label="item.name" :name="item.name"
					active-color="#333333" @change="radioChange">
				</u-radio>
			</u-radio-group>
		</view>
		<!-- 付费贴价格 -->
		<view class="choose-item" v-if="form.cut==1">
			<image class="icon" src="/static/price.png"></image>
			<text class="txt">付费贴单价</text>
			<u-input class="inputStyle" placeholder="查看详情内容需支付金额" border="surround" type="digit" clearable
				v-model="form.pay" @change="inputChange"></u-input>
		</view>
		<!-- 所在位置 -->
		<!-- #ifndef APP-PLUS -->
		<view @click="chooseLocation" class="choose-item" style="margin-bottom: 60rpx;">
			<u-icon class="icon add-icon" name="map" color="#999" size="40"></u-icon>
			<text class="txt">{{ form.address || '所在位置' }}</text>
			<u-icon class="u-icon" name="arrow-right"></u-icon>
		</view>
		<!-- #endif -->
		<!-- #ifdef APP-PLUS -->
		<view class="choose-item" style="margin-bottom: 60rpx;">	
		</view>
		<!-- #endif -->
		<!-- 发布按钮 -->
		<view class="release-btn">
			<view class="btn" v-if="form.type == 1" @click="uploadImg">
				发布
			</view>
			<view class="btn" v-if="form.type == 2" @click="videoSubmit">
				发布
			</view>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		reactive,
		computed,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad,
		onShow,
		onBackPress,
		onUnload
	} from '@dcloudio/uni-app'
	import hTips from '@/components/h-tips/h-tips.vue'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H
	const $c = proxy.$c
	const $f = proxy.$f
	const $u = proxy.$u

	// 响应式数据
	const uploadRef = ref(null)
	const uploadImgUrl = ref($c.domain + 'common/upload')
	const topicName = ref('选择圈子')
	const disName = ref('选择话题')
	const typeName = ref('普通贴')

	const form = reactive({
		title: '',
		type: 1,
		topicId: '',
		discussId: '',
		content: '',
		media: [],
		longitude: 0,
		latitude: 0,
		address: '',
		cut: 0,
		pay: '',
		brief: ''
	})

	const uploadingCount = ref(0) // 正在上传的图片数量
	const isUploading = ref(false) // 是否正在上传图片的状态标记
	const uploadTimer = ref(null) // 上传超时定时器
	const imageList = ref([]) // 独立管理图片列表，避免递归更新


	const header = ref({
		token: uni.getStorageSync('token')
	})

	const isTopic = ref(true)
	const switch2 = ref(0)

	const radiolist1 = ref([{
			name: '普通贴',
			disabled: false
		},
		{
			name: '付费贴',
			disabled: false
		}
	])

	const radiovalue1 = ref('普通贴')
	const payPostBtn = ref(false)

	// 只缓存标题和内容这两项：视频和图文贴公用同一缓存
	const tmpForm = reactive({
		title: '',
		content: '',
		media: '' // 注意这里只对视频生效，文本图片是临时路径未上传 所以不能保存
	})

	const isDraft = ref(false)

	// 生命周期
	onLoad((options) => {
		// 仅在传入 type 时才覆盖默认值，避免覆盖初始的 1
		if (options && options.type !== undefined && options.type !== null && options.type !== '') {
			form.type = Number(options.type)
			if (form.type == 2) {
				checkVideoOpen()
			}
		}
		if (uni.getStorageSync('hasLogin')) {
			checkPayPostBtn()
		}
		checkDraft()
		if (options.isTopic) {
			isTopic.value = options.isTopic
		}

		if (options.topicId) {
			form.topicId = options.topicId
			isTopic.value = false
		}
		if (options.discussId) {
			form.discussId = options.discussId
			disName.value = options.disName
		}

		let location = uni.getStorageSync('location_info')
		form.longitude = location.longitude
		form.latitude = location.latitude

		// 监听选择圈子事件（来自 choose-topic 页面）
		uni.$on('chooseTopicForPost', (data) => {
			form.topicId = data.id
			topicName.value = data.name
			// 清空话题选择
			form.discussId = ''
			disName.value = '选择话题'
		})

		// 监听选择话题事件（来自 choose-discuss 页面）
		uni.$on('chooseDiscussForPost', (data) => {
			form.discussId = data.id
			disName.value = data.name
		})
	})

	onShow(() => {
		// #ifdef MP-WEIXIN
		if (!uni.getStorageSync('hasLogin')) {
			$f.toast('登录后再发帖哦')
		}
		// #endif
		// #ifndef MP-WEIXIN
		if (!uni.getStorageSync('hasLogin')) {
			uni.navigateTo({
				url: "/pages/user/go-login"
			})
		}
		// #endif
	})

	// onBackPress在微信小程序中不生效 所以单独放到onUnload处理
	onBackPress((options) => {
		// #ifdef APP-PLUS || H5
		if (options.from === 'backbutton') {
			// 检查表单是否有更改
			if (hasFormChanged()) {
				uni.showModal({
					content: "保留此次草稿编辑？",
					confirmText: '保留',
					cancelText: '不保留',
					success: (res) => {
						if (res.confirm) {
							saveDraft()
						} else if (res.cancel) {
							if (form.type == 1) {
								uni.removeStorageSync('linfengTextTmpData')
							} else {
								uni.removeStorageSync('linfengVideoTmpData')
							}
							uni.navigateBack()
						}
					}
				})
				return true
			} else {
				return false
			}
		} else {
			return false
		}
		// #endif
	})

	// onBackPress在微信小程序中不生效 所以单独放到onUnload处理
	onUnload(() => {
		// 移除事件监听，防止内存泄漏
		uni.$off('chooseTopicForPost')
		uni.$off('chooseDiscussForPost')

		// #ifdef MP-WEIXIN
		// 检查表单是否有更改
		if (hasFormChanged() && !isDraft.value) {
			uni.showModal({
				content: "保留此次草稿编辑？",
				confirmText: '保留',
				cancelText: '不保留',
				success: (res) => {
					if (res.confirm) {
						saveDraft()
					} else if (res.cancel) {
						if (form.type == 1) {
							uni.removeStorageSync('linfengTextTmpData')
						} else {
							uni.removeStorageSync('linfengVideoTmpData')
						}
						uni.navigateBack()
					}
				}
			})
			return true
		} else {
			return false
		}
		// #endif	
	})

	// 方法
	const hasFormChanged = () => {
		tmpForm.title = form.title
		tmpForm.content = form.content
		if (form.type == 2) {
			tmpForm.media = form.media
		}
		const changedKeys = Object.keys(tmpForm).filter(key => {
			return !$f.isEmpty(tmpForm[key])
		})
		return changedKeys.length > 0
	}

	const saveDraft = () => {
		if (form.type == 1) {
			uni.setStorage({
				key: 'linfengTextTmpData',
				data: tmpForm,
				success: function() {},
				fail: function(err) {},
				complete() {
					uni.navigateBack()
				}
			})
		} else {
			uni.setStorage({
				key: 'linfengVideoTmpData',
				data: tmpForm,
				success: function() {},
				fail: function(err) {},
				complete() {
					uni.navigateBack()
				}
			})
		}
	}

	const checkDraft = () => {
		if (form.type == 1) {
			uni.getStorage({
				key: 'linfengTextTmpData',
				success: function(res) {
					const retrievedTmpForm = res.data
					form.title = retrievedTmpForm.title
					form.content = retrievedTmpForm.content
				},
				fail: function(err) {}
			})
		} else {
			uni.getStorage({
				key: 'linfengVideoTmpData',
				success: function(res) {
					const retrievedTmpForm = res.data
					form.title = retrievedTmpForm.title
					form.content = retrievedTmpForm.content
					if (form.type == 2) {
						form.media = retrievedTmpForm.media
					}
				},
				fail: function(err) {}
			})
		}
	}

	const checkVideoOpen = () => {
		$H.get('user/isOpen').then(res => {
			if (res.result == 0) {
				$f.toast("模块未开启")
				setTimeout(function() {
					uni.switchTab({
						url: '/pages/index/index'
					})
				}, 1500)
			}
		})
	}

	const inputChange = (n) => {}

	const groupChange = (n) => {}

	const radioChange = (n) => {
		if (n == '普通贴') {
			form.cut = 0
			typeName.value = '普通贴'
		} else if (n == '付费贴') {
			form.cut = 1
			typeName.value = '付费贴'
		}
	}

	const chooseVideo = () => {
		if (!uni.getStorageSync('hasLogin')) {
			$f.toast('登录后再发帖哦')
			return
		}
		uni.chooseVideo({
			count: 1,
			sourceType: ['camera', 'album'],
			success: function(res) {
				let viedoPath = res.tempFilePath
				if (res.size > 62914560) {
					uni.showToast({
						title: "视频不得大于60M",
						icon: 'none'
					})
					return
				}
				uni.showLoading({
					mask: true,
					title: '上传中'
				})

				uni.uploadFile({
					url: $c.domain + 'common/upload',
					filePath: viedoPath,
					name: 'Image',
					header: {
						token: uni.getStorageSync('token')
					},
					success: uploadFileRes => {
						let upData = JSON.parse(uploadFileRes.data)
						if (upData.code == 0) {
							form.media.push(upData.result)
						} else if (upData.code == 500) {
							uni.showToast({
								title: upData.msg,
								icon: 'none'
							})
						}
					},
					complete() {
						uni.hideLoading()
					}
				})
			}
		})
	}

	const uploadImg = () => {
		if (!uni.getStorageSync('hasLogin')) {
			$f.toast('登录后再发帖哦')
			return
		}
		if (!form.topicId) {
			$u.toast('请选择圈子')
			return
		}

		if (!form.content) {
			$u.toast('内容不能为空')
			return
		}
		if (!form.title) {
			$u.toast('标题不能为空')
			return
		}
		if (form.cut == 1) {
			var ret = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/
			if (!ret.test(form.pay)) {
				$u.toast('输入金额不合法')
				return
			}
			if (form.pay <= 0) {
				$u.toast('输入金额必须大于0')
				return
			}
			if (form.pay > 100) {
				$u.toast('输入金额必须小于100')
				return
			}
			if (!form.brief) {
				$u.toast('简介必须填写')
				return
			}
		}

		// 检查是否还有图片正在上传
		if (uploadingCount.value > 0) {
			$u.toast('图片正在上传中，请稍后...')
			return
		}

		// 将imageList同步到form.media，然后提交
		form.media = [...imageList.value]
		submitPost()
	}

	const chooseLocation = () => {
		uni.chooseLocation({
			success: function(res) {
				form.address = res.name
				form.latitude = res.latitude
				form.longitude = res.longitude
			},
			fail(err) {}
		})
	}

	const videoSubmit = () => {
		if (!uni.getStorageSync('hasLogin')) {
			$f.toast('登录后再发帖哦')
			return
		}
		if (form.media.length == 0) {
			$u.toast('请上传视频')
			return
		}

		if (!form.topicId) {
			$u.toast('请选择圈子')
			return
		}
		if (!form.content) {
			$u.toast('内容不能为空')
			return
		}
		if (!form.title) {
			$u.toast('标题不能为空')
			return
		}
		uni.showLoading({
			mask: true,
			title: '发布中'
		})
		$H.post('post/addPost', form).then(res => {
			uni.hideLoading()
			if (res.code == 0) {
				isDraft.value = true
				uni.removeStorageSync('linfengVideoTmpData')
				uni.redirectTo({
					url: '/pages/post/video-detail?id=' + res.result
				})
			} else {
				$f.toast(res.msg)
			}
		})
	}

	// 图片上传成功回调 - 使用独立的imageList避免递归
	const onImageSuccess = (response, index, list, name) => {
		uploadingCount.value--

		// 解析响应数据并添加到独立的imageList
		if (response && response.msg === 'success' && response.result) {
			imageList.value.push(response.result)
		} else {
			$u.toast('图片上传响应异常')
		}

		// 只有当所有图片都上传完成时才隐藏loading
		if (uploadingCount.value === 0) {
			isUploading.value = false
			if (uploadTimer.value) {
				clearTimeout(uploadTimer.value)
				uploadTimer.value = null
			}
			uni.hideLoading()
		}
		if (!uni.getStorageSync('hasLogin')) {
			// #ifdef APP-PLUS || H5
			uni.navigateTo({
				url: '/pages/user/go-login'
			});
			// #endif
			// #ifdef MP-WEIXIN
			uni.navigateTo({
				url: '/pages/user/login'
			});
			// #endif
		}
	}

	// 图片上传失败回调
	const onUploadError = (index, err, name) => {
		uploadingCount.value--
		$u.toast('图片上传失败，请重试')

		// 只有当所有图片都处理完成时才隐藏loading
		if (uploadingCount.value === 0) {
			isUploading.value = false
			if (uploadTimer.value) {
				clearTimeout(uploadTimer.value)
				uploadTimer.value = null
			}
			uni.hideLoading()
		}
	}

	// 图片上传进度回调
	const onUploadProgress = (index, event, name) => {
		// 确保显示loading提示（避免重复显示）
		if (uploadingCount.value > 0 && !isUploading.value) {
			isUploading.value = true
			uni.showLoading({
				mask: true,
				title: '图片上传中...'
			})

			// 设置超时保护
			uploadTimer.value = setTimeout(() => {
				if (isUploading.value) {
					isUploading.value = false
					uploadingCount.value = 0
					uni.hideLoading()
					$u.toast('上传超时，请重试')
				}
			}, 30000)
		}
	}

	// 图片选择完成回调
	const onChooseComplete = (lists, index) => {
		// 计算需要上传的图片数量
		const needUploadCount = lists.filter(item => item.progress === 0).length
		uploadingCount.value = needUploadCount
		isUploading.value = false
	}

	// 图片删除回调
	const onImageRemove = (index, lists) => {
		// 从imageList中移除对应的图片
		if (index >= 0 && index < imageList.value.length) {
			imageList.value.splice(index, 1)
		}
	}

	// 提交帖子
	const submitPost = () => {
		uni.showLoading({
			mask: true,
			title: '发布中'
		})

		$H.post('post/addPost', form).then(res => {
			uni.hideLoading()
			if (res.code == 0) {
				isDraft.value = true
				uni.removeStorageSync('linfengTextTmpData')
				uni.redirectTo({
					url: '/pages/post/detail?id=' + res.result
				})
			} else {
				$f.toast(res.msg)
			}
		})
	}

	const checkPayPostBtn = () => {
		$H.get('system/checkPayPostBtn').then(res => {
			if (res.code == 0) {
				payPostBtn.value = res.isOpen
			}
		})
	}

	const protocol = (type) => {
		uni.navigateTo({
			url: '/pages/user/protocol?type=' + type
		})
	}

	// 导航到选择圈子页面
	const navigateToChooseTopic = () => {
		uni.navigateTo({
			url: '/pages/topic/choose-topic/choose-topic?from=post'
		})
	}

	// 导航到选择话题页面
	const navigateToChooseDiscuss = () => {
		uni.navigateTo({
			url: '/pages/discuss/choose-discuss/choose-discuss?topicId=' + form.topicId + '&from=post'
		})
	}
</script>

<style lang="scss" scoped>
	.container {
		padding: 20rpx;
		overflow: scroll;
	}

	.title-input {
		border-bottom: 1px solid #F5F5F5;
		margin: 20rpx 0;
		padding: 20rpx 0;
	}

	.post-txt {
		width: 100%;
		padding: 20rpx 0;
		min-height: 300rpx;
	}

	.upload-wrap {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		width: 180rpx;
		height: 180rpx;
		background-color: #F5F5F5;
		margin-top: 30rpx;
		border-radius: 10rpx;

		.icon {
			width: 50rpx;
			height: 50rpx;
		}

		text {
			font-size: 24rpx;
		}
	}

	.upload-video {
		width: 180rpx;
		height: 180rpx;
		margin-top: 30rpx;
	}

	.choose-item {
		display: flex;
		align-items: center;
		padding: 20rpx;
		border-bottom: 1px solid #F5F5F5;

		&:last-child {
			border: 0;
		}

		.txt {
			margin-left: 20rpx;
			font-size: 30rpx;
			color: #000000;
		}

		.sw {
			margin-left: 300rpx;
		}

		.inputStyle {
			margin-left: 60rpx;
			width: 400rpx;
		}

		.radio {
			margin-right: auto;
			position: absolute;
			right: 0;
		}

		.icon {
			width: 40rpx;
			height: 40rpx;
		}

		.u-icon {
			margin-left: auto;
			color: #999;
		}

		.add-icon {
			margin-left: 0;
		}
	}

	.one-line {
		.top {
			display: flex;
			justify-content: space-between;
			align-items: center;

			.title {
				font-size: 30rpx;
				font-weight: 500;
				color: #000000;
				line-height: 42rpx;
			}

			.num {
				font-size: 24rpx;
				color: #000000;
				line-height: 33rpx;
				opacity: 0.5;
			}
		}

		.content {
			margin-top: 26rpx;

			.txt {
				font-size: 30rpx;
				color: #000000;
				line-height: 42rpx;
			}
		}
	}

	.slot-btn {
		margin: 10rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		width: 200rpx;
		height: 200rpx;
		border-radius: 10rpx;
		background-color: #F5F5F5;

	}

	.slot-txt {
		margin-top: 17rpx;
		font-size: 24rpx;
		color: #000000;
		line-height: 33rpx;
		opacity: 0.5;
	}

	.release-btn {
		position: fixed;
		display: flex;
		justify-content: center;
		left: 0;
		bottom: 0;
		width: 750rpx;
		// height: 20rpx;
		background-color: #ffffff;

		.btn {
			width: 690rpx;
			height: 90rpx;
			// background: #323233;
			background: linear-gradient(#121212, #666666);
			border-radius: 10rpx;
			font-size: 28rpx;
			font-weight: 500;
			color: #FFFFFF;
			line-height: 90rpx;
			text-align: center;
		}
	}
</style>