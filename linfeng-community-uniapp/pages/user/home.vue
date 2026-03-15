<template>
	<view>
		<!-- Add background image -->
		<image class="background-image" :src="backgroundImage"></image>

		<view class="back-wrap">
			<!-- 头部 -->
			<view class="page-top" :style="{ paddingTop: statusBarHeight+'px',}">
				<view class="setting setting-o" :style="{ paddingTop: statusBarHeight+'px'}">
					<view class="setting-inner" :style="{height: navHeight-statusBarHeight+'px'}">
						<image @click="onBack()" src="/static/images/back.png"
							style="width: 32rpx;height: 28rpx;opacity: 1;"></image>
					</view>
				</view>
				<view class="setting" :style="{ paddingTop: statusBarHeight+'px', opacity: navOpacity}">
					<view class="setting-inner" :style="{height: navHeight-statusBarHeight+'px'}">
						<image @click="onBack()" :src="$IMG+'/img/back.png'"
							style="width: 32rpx;height: 28rpx;opacity: 1;"></image>
						<view class="nav-nickname" :style="{ opacity: navOpacity}">
							{{ userInfo.username }}
						</view>
					</view>
				</view>

				<view class="avatar">
					<image :src="userInfo.avatar"
						style="width: 120rpx;height: 120rpx;border-radius: 128rpx;border: 1rpx solid #ffffff;">
					</image>
				</view>
				<view>
					<view style="font-size: 34rpx;font-weight: 600;line-height: 48rpx;margin-top: 30rpx;">
						{{ userInfo.username }}
						<image src="@/static/vip/vipIcon.png" class="image3" v-if="userInfo.vip==1" />
					</view>


					<view style="font-size: 24rpx;line-height: 33rpx;margin-top: 16rpx;">{{ userInfo.intro }}</view>
				</view>
				<!-- 圈子&关注&粉丝&编辑资料 按钮 -->
				<view class="some-btn">
					<view class="num-name">
						<view class="num">
							{{userInfo.postNum || 0}}
						</view>
						<view class="name">
							帖子
						</view>
					</view>
					<view class="num-name" @click="getFollow">
						<view class="num">
							{{ userInfo.follow || 0 }}
						</view>
						<view class="name">
							关注
						</view>
					</view>
					<view class="num-name" @click="getFans">
						<view class="num">
							{{ userInfo.fans || 0 }}
						</view>
						<view class="name">
							粉丝
						</view>
					</view>

					<view v-if="currUid==uid" @click="goPersonalData()" class="edit-btn">
						编辑资料
					</view>
					<view v-else>
						<view v-if="!userInfo.isFollow" @click="follow" class="watch-btn">
							关注
						</view>
						<view v-else @click="cancelFollow" class="follow-btn">
							已关注
						</view>
					</view>
					<view v-if="currUid!=uid&&!userInfo.isFriend&&userInfo.socialOpen" class="chatButton"
						@click="open()">
						加好友
					</view>
					<view v-if="currUid!=uid&&userInfo.isFriend&&userInfo.socialOpen" class="chatButton"
						@click="gotoChat()">
						聊天
					</view>

				</view>

			</view>
			<!-- 中间 -->
			<view class="page-mid" v-if="currUid==uid">
				<view @click="goFollow()" class="one-btn">
					<view class="top">
						我的关注
					</view>
					<view class="bottom">
						关注的人
					</view>
				</view>
				<view @click="goFan()" class="one-btn">
					<view class="top">
						我的粉丝
					</view>
					<view class="bottom">
						粉丝列表
					</view>
				</view>
				<view @click="goIntegral()" class="one-btn">
					<view class="top">
						我的积分
					</view>
					<view class="bottom">
						签到/消耗
					</view>
				</view>
			</view>
			<view v-if="userInfo.status === 1" @click="goProtocol">
				<u-notice-bar type="error" mode="vertical" :list="banInfo"></u-notice-bar>
			</view>
			<view v-if="userInfo.status === 2" @click="goProtocol">
				<u-notice-bar type="warning" mode="vertical" :list="forbiddenInfo"></u-notice-bar>
			</view>
			<view v-if="userInfo.status === 3">
				<u-notice-bar type="error" mode="vertical" :list="unuseInfo"></u-notice-bar>
			</view>
			<!-- 底部 -->
			<view class="page-bottom">
				<view class="tabs" :style="{top: navHeight*2 + 'rpx'}">
					<!-- #ifdef APP-PLUS -->
					<u-tabs :list="tabList" font-size="28" name="tabName" active-color="#000000" bg-color="#fff"
						:current="current" @change="tabChange" inactive-color="#949494" bar-height="6" bar-width="20">
					</u-tabs>
					<!-- #endif -->
					<!-- #ifndef APP-PLUS -->
					<u-tabs :list="tabList" font-size="28" name="tabName" active-color="#000000" bg-color="#fff"
						:current="current" @change="tabChange" inactive-color="#949494" bar-height="6" bar-width="20"
						height="40">
					</u-tabs>
					<!-- #endif -->

				</view>
				<view v-if="current === 0">
					<view class="f-wrap u-skeleton-fillet">
						<view class="title">基本信息</view>
						<view class="info-c">
							<view v-if="userInfo.type === 1" class="level-box">
								<view class="level">
									<u-icon name="level"></u-icon>
								</view>
								<text>{{appName}}官方账号</text>
							</view>
							<text>性别：{{userInfo.gender}}</text>


							<text v-if="userInfo.city">IP属地：{{userInfo.city}}</text>
							<text>个性签名：{{userInfo.intro}}</text>
							<text>
								经验等级：<text style="color:#ffb254;">LV{{ userInfo.level }}</text>
								<image style="height: 22rpx;margin-left:10rpx ;" mode="heightFix" v-if="userInfo.level"
									:src="$IMG+'/vip/level_'+userInfo.level+'.png'" class="level-img-2" />
							</text>

						</view>
					</view>

					<view class="f-wrap u-skeleton-fillet">
						<view class="title">标签</view>
						<view class="tag-box">
							<view v-for="(item,index) in userInfo.tagStr" :key="index" class="tag">{{item}}</view>
						</view>
					</view>
					<view v-if="createTopicList.length > 0" class="f-wrap u-skeleton-fillet">
						<view class="title">创建的圈子</view>
						<TopicListComp :list="userInfo.createTopicList" loadStatus="none"></TopicListComp>
					</view>
				</view>
				<view v-if="current === 1">
					<!-- #ifdef H5 -->
					<view v-if="postListData.length ==0" style="background-color: #f7f7f7;height: 160rpx;"></view>
					<!-- #endif -->
					<PostListComp :list="postListData" :loadStatus="loadStatus"></PostListComp>
				</view>
				<view v-if="current === 2">
					<TopicListComp :list="topicListData"></TopicListComp>
				</view>
			</view>
		</view>

		<!-- 填写信息弹窗 -->
		<u-popup v-model="openPop" mode="center" border-radius="14">
			<view class="informationShow-box">
				<view class="flex-items flex-column informationShow-centent">
					<view class="mar-top-60">
						<label class="title">申请好友</label>
					</view>
					<view class="flex-items flex-row mar-top-60">
						<view class="fs26">备注</view>
						<input class="remark mar-left-20 fs26" v-model="message" />
					</view>
					<view class="flex-items flex-row mar-top-30 ">
						<label class="fs26">对方</label>
						<input class="nick mar-left-20 fs26" v-model="notation" disabled="disabled" />
					</view>
					<view class="flex-row-plus massageDes-but">
						<view class="exitBut" @click="outPop">取消</view>
						<view class="submitbut" @click="submitPop">提交</view>
					</view>
				</view>
			</view>
		</u-popup>
		<!-- 自定义加载 -->
		<toast color="#fff" type="round"></toast>
	</view>
</template>

<script setup>
	import {
		ref,
		computed,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad,
		onShow,
		onReachBottom,
		onPageScroll
	} from '@dcloudio/uni-app'
	import {
		useStore
	} from 'vuex'
	import PostListComp from '@/components/post-list/post-list.vue'
	import TopicListComp from '@/components/topic-list/topic-list.vue'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H
	const $c = proxy.$c
	const $u = proxy.$u
	const $f = proxy.$f
	const $loading = proxy.$loading
	const $IMG = ref(proxy.$IMG)

	const store = useStore()


	const friendList = computed(() => store.getters.friendList)
	const loginUserInfo = computed(() => store.getters.loginUserInfo)
	const sessionList = computed(() => store.getters.sessionList)

	const loading = ref(true)
	const background = ref({
		backgroundColor: 'unset'
	})
	const tabList = ref([{
		tabName: '主页'
	}, {
		tabName: '帖子'
	}, {
		tabName: '加入圈子'
	}])
	const banInfo = ref(['该用户由于违反《社区公约》已被封号'])
	const forbiddenInfo = ref(['该用户由于违反《社区公约》已被禁言'])
	const unuseInfo = ref(['该用户已注销'])
	const current = ref(0)
	const uid = ref(0)
	const postListData = ref([])
	const topicListData = ref([])
	const userInfo = ref({})
	const userJson = ref("")
	const loadStatus = ref("loading")
	const page = ref(1)
	const currUid = ref(0)
	const statusBarHeight = ref(0)
	const navHeight = ref(0)
	const navOpacity = ref(0)
	const createTopicList = ref([])
	const notation = ref('')
	const message = ref('')
	const openPop = ref(false)
	const appName = ref($c.miniappName)
	const backgroundImage = ref('https://demo.linfeng.tech/resource/images/user-bg.png')

	onLoad((options) => {
		navHeight.value = getApp().globalData.navHeight
		statusBarHeight.value = getApp().globalData.statusBarHeight
		if (options.uid) {
			uid.value = options.uid
		} else {
			if (uni.getStorageSync('userInfo').uid) {
				uid.value = uni.getStorageSync('userInfo').uid
			}
		}
		if (uni.getStorageSync('userInfo').uid) {
			currUid.value = uni.getStorageSync('userInfo').uid
		}
	})

	onShow(() => {
		getUserInfo()
		if (uni.getStorageSync('hasLogin')) {
			if (uni.getStorageSync('userInfo').uid) {
				currUid.value = uni.getStorageSync('userInfo').uid
			}
		}
	})

	onReachBottom(() => {
		if (current.value == 1) {
			page.value++
			getPostList()
		}
	})

	onPageScroll((e) => {
		navOpacity.value = e.scrollTop / 50
	})

	function getFollow() {
		if (userInfo.value.isWatch) {
			$f.toast('TA设置了私密哦')
			return
		}
		$f.jump('/pages/my/follow?uid=' + userInfo.value.uid)
	}

	function getFans() {
		if (userInfo.value.isFan) {
			$f.toast('TA设置了私密哦')
			return
		}
		$f.jump('/pages/my/fans?uid=' + userInfo.value.uid)
	}

	function onBack() {
		let pages = getCurrentPages()
		if (pages.length > 1) {
			uni.navigateBack()
		} else {
			uni.switchTab({
				url: '/pages/index/index'
			})
		}
	}

	function follow() {
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
			return;
		}
		$H.post('user/addFollow', {
			id: userInfo.value.uid
		}).then(res => {
			if (res.code === 0) {
				userInfo.value.isFollow = true
			} else {
				$u.toast(res.msg)
			}
		})
	}

	function cancelFollow() {
		$H.post('user/cancelFollow', {
			id: userInfo.value.uid
		}).then(res => {
			if (res.code === 0) {
				userInfo.value.isFollow = false
			}
		})
	}

	function tabChange(index) {
		current.value = index
		if (index === 1) {
			postListData.value = []
			getPostList()
		}
		if (index === 2) {
			getTopicList()
		}
	}

	function getPostList() {
		if (userInfo.value.isPost) {
			$f.toast('TA设置了私密哦')
			postListData.value = []
			loadStatus.value = "nomore"
			return
		}
		if (!uni.getStorageSync('hasLogin')) {
			currUid.value = -1
		}
		loadStatus.value = "loading"
		$H.post('post/getListByUid', {
			page: page.value,
			uid: uid.value,
			myUid: currUid.value
		}).then(res => {
			postListData.value = postListData.value.concat(res.result.data)
			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = "nomore"
			} else {
				loadStatus.value = "loadmore"
			}
			//如果包含私密圈帖子 进行提示
			if (res.result.total < userInfo.value.postNum && res.result.current_page === 1) {
				$u.toast('仅展示可见帖子')
			}
		})
	}

	function getTopicList() {
		$H.post('topic/userJoinTopic', {
			uid: uid.value
		}).then(res => {
			topicListData.value = res.result.data
		})
	}

	function getUserInfo() {
		$loading(true)
		$H.post('user/userInfoById', {
			uid: uid.value
		}).then(res => {
			userInfo.value = res.result
			if (res.result.bgImg) {
				backgroundImage.value = res.result.bgImg
			}
			if (res.result.gender === 1) {
				userInfo.value.gender = '男'
			} else if (res.result.gender === 2) {
				userInfo.value.gender = '女'
			} else {
				userInfo.value.gender = '保密'
			}
			if (userInfo.value.createTopicList != null) {
				createTopicList.value = userInfo.value.createTopicList
			}

			let user = {
				uid: res.result.uid,
				username: res.result.username,
				avatar: res.result.avatar,
			}
			userJson.value = JSON.stringify(user)
			uni.setNavigationBarTitle({
				title: userInfo.value.username
			})
			loading.value = false
			$loading(false)
		})
	}

	function goPersonalData() {
		uni.navigateTo({
			url: '/pages/user/edit-info/edit'
		})
	}

	function goIntegral() {
		uni.navigateTo({
			url: '/pages/sign/integral'
		})
	}

	function goAccount() {
		uni.navigateTo({
			url: '/pages/account/account'
		})
	}

	function goFollow() {
		uni.navigateTo({
			url: '/pages/my/follow'
		})
	}

	function goFan() {
		uni.navigateTo({
			url: '/pages/my/fans'
		})
	}

	function open() {
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
			return;
		}
		openPop.value = true
		notation.value = userInfo.value.username
		//处理偶尔取不到当前用户信息的问题
		if (!loginUserInfo.value.username) {
			if (uni.getStorageSync('userInfo').username) {
				store.state.loginUserInfo = uni.getStorageSync('userInfo')
			} else {
				$H.get('user/userInfo').then(res => {
					store.state.loginUserInfo = res.result
					message.value = '你好,我是' + res.result.username
				})
			}
		}
		message.value = '你好,我是' + store.state.loginUserInfo.username
	}

	function outPop() {
		openPop.value = false
	}

	//发送好友申请
	function submitPop() {
		if (notation.value.trim() === '') {
			uni.showToast({
				title: '备注不允许为空',
				icon: 'none'
			})
			return
		}
		if (notation.value.trim().length > 20 || message.value.trim().length > 20) {
			uni.showToast({
				title: '字数不要超过20个字',
				icon: 'none'
			})
			return
		}

		if (!loginUserInfo.value.uid) {
			uni.showToast({
				title: '你的账号信息过期请重新登录',
				icon: 'none'
			})
			return
		}
		//构造websocket消息
		let m = {
			senderId: loginUserInfo.value.uid,
			senderName: loginUserInfo.value.username,
			senderAvatar: loginUserInfo.value.avatar,
			receiverId: userInfo.value.uid,
			notation: notation.value.trim(),
			applyMessage: message.value.trim()
		}
		let msg = {
			type: 'person-apply',
			data: m
		}
		uni.sendSocketMessage({
			data: JSON.stringify(msg),
			success() {
				uni.showToast({
					icon: 'success',
					title: '发送成功'
				})
				openPop.value = false
			},
			fail(res) {
				setTimeout(function() {
					retrySubmit()
				}, 1200)
			}
		})
	}

	//尝试重连 直接请求接口
	function retrySubmit() {
		console.log('发起申请好友接口请求')
		let m = {
			senderId: loginUserInfo.value.uid,
			senderName: loginUserInfo.value.username,
			senderAvatar: loginUserInfo.value.avatar,
			receiverId: userInfo.value.uid,
			notation: notation.value.trim(),
			applyMessage: message.value.trim()
		}
		let msg = {
			type: 'person-apply',
			data: m
		}
		$H.post('friend/applyFriend', msg).then(res => {
			if (res.code == 0) {
				uni.showToast({
					icon: 'success',
					title: '发送成功'
				})
			}
			openPop.value = false
		})
	}

	//跳转聊天页面
	function gotoChat() {
		for (let i = 0; i < sessionList.value.length; i++) {
			if (sessionList.value[i].chattingUserId == userInfo.value.uid) {
				store.state.chattingUserInfo = sessionList.value[i]
				break
			}
		}
		$H.post('chat/list', {
			sessionId: store.state.chattingUserInfo.sessionId,
			pageNum: 1,
			pageSize: 20
		}).then(res => {
			if (res.code == 0) {
				let data = res.result
				for (let i = 0; i < store.state.personMessage.length; i++) {
					if (store.state.personMessage[i].sessionId == data.sessionId) {
						let current = Number(data.pageInfo.current)
						let total = Number(data.pageInfo.total)
						let size = Number(data.pageInfo.size)
						store.state.personMessage[i].lastMessageId = data.pageInfo.records
							.length != 0 ? data.pageInfo.records[0].id : 0
						store.state.personMessage[i].list = data.pageInfo.records.reverse()
						store.state.personMessage[i].pageNum = current
						store.state.personMessage[i].hasNextPage = current * size < total
						break
					}
				}
				uni.navigateTo({
					url: '/pages/im/chat/chat'
				})
			} else {
				uni.showToast({
					icon: 'none',
					mask: '消息加载失败'
				})
			}
		})
	}
	
	function goProtocol(){
		uni.navigateTo({
			url:"/pages/user/protocol?type=app_standard_context"
		})
	}
	
</script>
<style>
	page {
		background-color: #f5f5f5;
	}
</style>
<style lang="scss" scoped>
	.background-image {
		position: absolute;
		top: 0;
		left: 0;
		width: 750rpx;
		height: 700rpx;
		z-index: 0;
	}

	.back-wrap {
		padding-bottom: 80rpx;
		position: relative;
		z-index: 1;
	}

	.page-top {
		width: 750rpx;
		// height: 700rpx;
		padding: 30rpx;
		color: #ffffff;

		.setting {
			z-index: 999;
			background-color: #ffffff;
			width: 750rpx;
			position: fixed;
			top: 0;
			left: 0;
			padding-left: 50rpx;

			.setting-inner {
				display: flex;
				align-items: center;

				.nav-nickname {
					display: flex;
					justify-content: center;
					align-items: center;
					width: 590rpx;
					font-size: 24rpx;
					font-weight: 500;
					color: #000000;
				}
			}

		}

		.setting-o {
			background-color: transparent;
		}

		.avatar {
			margin-top: 115rpx;
		}

		.some-btn {
			display: flex;
			align-items: center;
			margin-top: 100rpx;

			.num-name {
				width: 116rpx;

				.num {
					font-size: 32rpx;
					font-weight: 600;
					line-height: 45rpx;
				}

				.name {
					margin-top: 2rpx;
					font-size: 20rpx;
					line-height: 28rpx;
				}
			}

			.edit-btn {
				display: flex;
				align-items: center;
				justify-content: center;
				margin-left: 150rpx;
				width: 184rpx;
				height: 64rpx;
				background-color: rgba(0, 0, 0, 0.6);
				border: 2rpx solid rgba(255, 255, 255, 0.6);
				border-radius: 100rpx;
				font-size: 24rpx;
				font-weight: 600;
			}

			.watch-btn {
				display: flex;
				align-items: center;
				justify-content: center;
				margin-left: 60rpx;
				width: 124rpx;
				height: 54rpx;
				background-color: rgba(0, 0, 0, 0.6);
				border: 2rpx solid rgba(255, 255, 255, 0.6);
				border-radius: 100rpx;
				font-size: 24rpx;
				font-weight: 600;
			}

			.follow-btn {
				display: flex;
				align-items: center;
				justify-content: center;
				margin-left: 60rpx;
				width: 124rpx;
				height: 54rpx;
				background-color: #f5f5f5;
				color: #544949;
				border: 2rpx solid rgba(255, 255, 255, 0.6);
				border-radius: 100rpx;
				font-size: 24rpx;
				font-weight: 600;
			}

			.chatButton {
				display: flex;
				align-items: center;
				justify-content: center;
				margin-left: 10rpx;
				width: 124rpx;
				height: 54rpx;
				background-color: #5f6166;
				border: 2rpx solid rgba(172, 172, 172, 0.6);
				border-radius: 100rpx;
				font-size: 24rpx;
				font-weight: 600;
			}
		}
	}

	.page-mid {
		display: flex;
		justify-content: space-around;
		padding-top: 50rpx;
		margin-top: 42rpx;
		width: 750rpx;
		height: 180rpx;
		background-color: #F9F9F9;
		border-top-left-radius: 30rpx;
		border-top-right-radius: 30rpx;

		// transform: translateY(-108rpx);
		.one-btn {
			display: flex;
			flex-direction: column;
			align-items: center;

			.top {
				font-size: 28rpx;
				font-weight: 600;
				color: #333333;
				line-height: 40rpx;
			}

			.bottom {
				margin-top: 10rpx;
				font-size: 24rpx;
				line-height: 33rpx;
				color: #999999;
			}
		}
	}

	.page-bottom {

		// transform: translateY(-108rpx);
		.tabs {
			background-color: #ffffff;
			position: sticky;
			top: 82rpx;
			z-index: 99;
			display: flex;
			justify-content: center;
			padding-top: 60rpx;
			width: 750rpx;
		}
	}

	.f-wrap {
		margin-bottom: 30rpx;
	}

	.avatar {
		width: 130rpx;
		height: 130rpx;
		border-radius: 50%;
		border: 2px solid #FFFFFF;
		z-index: 999;
	}

	.tab-box {
		margin-top: 30rpx;
		margin-bottom: 30rpx;
	}

	.info-c {
		display: flex;
		flex-direction: column;
	}

	.info-c>text {
		margin-bottom: 20rpx;
		color: #999;
	}

	.info-c .level-box {
		margin-bottom: 20rpx;
		display: flex;
		align-items: center;
		color: #999;

		.level {
			font-size: 20rpx;
			color: #fff;
			padding: 5rpx 10rpx;
			background-color: $themes-color;
			border-radius: 10rpx;
			margin-right: 10rpx;
		}
	}

	/* 标签 */
	.tag-box {}

	.tag-box .tag {
		padding: 10rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		display: inline-block;
		margin-right: 20rpx;
		margin-bottom: 20rpx;
		background-color: #ffebe5;
	}

	.tag-box .tag:nth-child(2n) {
		background-color: #ecf9f2;
	}

	.tag-box .tag:nth-child(3n) {
		background-color: #fff7e5;
	}

	.tag-box .tag:nth-child(5n) {
		background-color: #b3e0ff;
	}

	.image3 {
		width: 75rpx;
		height: 30rpx;
		margin-left: 5rpx;
	}

	.title {
		font-size: 36upx;
		font-weight: 200;
	}



	.informationShow-box {
		.flex-items {
			input {
				padding-left: 16upx;
			}
		}

		.phoneStyle {
			input {
				background: #EEEEEE;
				color: #999999;
			}
		}

		.informationShow-centent {
			width: 520upx;
			height: 550upx;

			.remark {
				border: 1upx solid #DDDDDD;
				width: 300upx;
				height: 48upx;
			}

			.nick {
				border: 1upx solid #DDDDDD;
				width: 300upx;
				height: 48upx;
			}

			.massageDes-but {
				position: absolute;
				bottom: 0;
			}

			.exitBut {
				border: 1upx solid #E5E5E5;
				width: 260upx;
				height: 90upx;
				text-align: center;
				line-height: 90upx;
			}

			.submitbut {
				background-color: #323232;
				width: 260upx;
				height: 90upx;
				text-align: center;
				line-height: 90upx;
				color: #FFFFFF;
			}
		}
	}
</style>