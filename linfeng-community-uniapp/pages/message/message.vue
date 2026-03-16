<template>
	<view class="page-container">
		<!-- 顶部背景渐变 -->
		<view class="top-gradient"></view>

		<!-- 自定义顶部导航 -->
		<view class="custom-navbar">
			<view class="nav-tabs">
				<view class="tab-item" :class="{ active: pageCurrent == 0 }" @click="pageTabChange(0)">
					<text>消息</text>
					<view class="active-dot" v-if="pageCurrent == 0"></view>
				</view>
				<view class="tab-item" :class="{ active: pageCurrent == 1 }" @click="pageTabChange(1)" v-if="socialOpen">
					<text>联系人</text>
					<view class="active-dot" v-if="pageCurrent == 1"></view>
				</view>
			</view>
			<!-- #ifndef MP -->
			<view class="nav-actions" v-if="pageCurrent == 0">
				<u-icon name="chat" color="#333" size="40" style="margin-right: 30rpx;"></u-icon>
			</view>
			<view class="nav-actions" v-if="pageCurrent == 1">
				<u-icon name="account" color="#333" size="40" style="margin-right: 30rpx;"></u-icon>
			</view>
			<!-- #endif -->
		</view>

		<!-- 消息页面内容 -->
		<view v-if="pageCurrent == 0" class="content-area">
			
			<!-- 金刚区：核心功能入口 (点赞、评论、粉丝) -->
			<view class="grid-menu" :key="gridRefreshKey">
				<!-- 互动消息 (映射为评论) -->
				<view class="grid-item" @click="$f.jump('/pages/message/list?type=3')">
					<view class="icon-box bg-pink">
						<u-icon name="chat-fill" color="#ffffff" size="44"></u-icon>
					</view>
					<u-badge :count="msgNum.comment" :offset="[-10, -10]" absolute></u-badge>
					<text class="grid-text">互动评论</text>
				</view>
				<view class="grid-item" @click="$f.jump('/pages/message/list?type=1')">
					<view class="icon-box bg-blue">
						<u-icon name="thumb-up-fill" color="#ffffff" size="44"></u-icon>
					</view>
					<u-badge :count="msgNum.thumbCount" :offset="[-10, -10]" absolute></u-badge>
					<text class="grid-text">收到的赞</text>
				</view>
				<view class="grid-item" @click="$f.jump('/pages/message/list?type=2')">
					<view class="icon-box bg-cyan">
						<u-icon name="account-fill" color="#ffffff" size="44"></u-icon>
					</view>
					<u-badge :count="msgNum.follow" :offset="[-10, -10]" absolute></u-badge>
					<text class="grid-text">新增粉丝</text>
				</view>
			</view>
			<!-- 列表区域 -->
			<view class="message-list-box">
				
				<!-- 系统通知 -->
				<view @click="goSys" @longpress="onPressArticle()" class="msg-card">
					<view class="card-left">
						<view class="avatar-box bg-purple-grad">
							<u-icon name="bell-fill" color="#ffffff" size="40"></u-icon>
						</view>
						<view class="card-info">
							<view class="card-title-row">
								<text class="card-title">系统通知</text>
							</view>
							<view class="card-desc" v-if="msgNum.systemUnreadCount>0">你有{{msgNum.systemUnreadCount}}条系统通知未读</view>
							<view class="card-desc" v-else>暂无系统通知</view>
						</view>
					</view>
					<view class="card-right">
						<view class="unread-dot" v-if="msgNum.systemUnreadCount>0">
							{{ msgNum.systemUnreadCount > 99 ? '99+' : msgNum.systemUnreadCount }}
						</view>
						<u-icon name="arrow-right" color="#ccc" size="28"></u-icon>
					</view>
				</view>
				
				<!-- 新的朋友 -->
				<view @click="goNotice" class="msg-card" v-if="socialOpen">
					<view class="card-left">
						<view class="avatar-box bg-orange-grad">
							<u-icon name="plus-people-fill" color="#ffffff" size="40"></u-icon>
						</view>
						<view class="card-info">
							<view class="card-title-row">
								<text class="card-title">新的朋友</text>
							</view>
							<view class="card-desc" v-if="totalUnread.notice>0">你有{{totalUnread.notice}}条新朋友申请通知未处理</view>
							<view class="card-desc" v-else>暂无新朋友申请</view>
						</view>
					</view>
					<view class="card-right">
						<view class="unread-dot" v-if="totalUnread.notice>0">
							{{ totalUnread.notice > 99 ? '99+' : totalUnread.notice }}
						</view>
						<u-icon name="arrow-right" color="#ccc" size="28"></u-icon>
					</view>
				</view>

				<!-- 私信列表 -->
				<view @click="goChatList" class="msg-card" v-if="socialOpen">
					<view class="card-left">
						<view class="avatar-box bg-blue-grad">
							<u-icon name="email-fill" color="#ffffff" size="40"></u-icon>
						</view>
						<view class="card-info">
							<view class="card-title-row">
								<text class="card-title">私信列表</text>
							</view>
							<view class="card-desc" v-if="totalUnread.message>0">你有{{totalUnread.message}}条私信未读</view>
							<view class="card-desc" v-else>暂无新私信</view>
						</view>
					</view>
					<view class="card-right">
						<view class="unread-dot" v-if="totalUnread.message>0">
							{{ totalUnread.message > 99 ? '99+' : totalUnread.message }}
						</view>
						<u-icon name="arrow-right" color="#ccc" size="28"></u-icon>
					</view>
				</view>
			</view>
		</view>

		<!-- 好友列表-->
		<view v-if="pageCurrent == 1" class="content-area">
			<u-index-list :indexList="indexList" :scrollTop="scrollTop">
				<view v-for="(item, index) in friendListShow" :key="index">
					<u-index-anchor v-if="item.length!==0" :use-slot="true" class="custom-anchor">
						<text class="anchor-text">{{indexList[index]}}</text>
					</u-index-anchor>
					<view class="friend-list-item" v-for="(item1, index1) in item" :key="index1" @tap="gotoFriendInfo(item1.id)">
						<image class="friend-avatar" :src="item1.url" mode="aspectFill"></image>
						<view class="friend-info">
							<text class="friend-name">{{item1.name}}</text>
						</view>
						<view class="friend-action" @click.stop="showFriend(item1.id)">
							<u-icon name="more-dot-fill" color="#b3b3b3" size="40"></u-icon>
						</view>
					</view>
				</view>
			</u-index-list>
			
			<block v-if="friendList.length == 0">
				<view class="msg-empty">
					<image class="img" mode="widthFix" src="/static/empty.png"></image>
					<text class="txt">暂无好友哦~</text>
				</view>
			</block>
			<view class="count-desc" v-if="friendList.length > 0">
				<text class="list__footer">共{{friendList.length}}位好友</text>
			</view>
		</view>

		<!-- 删除系统消息弹窗 -->
		<u-action-sheet :list="sheetList" v-model="showSheet" @click="onSheetItem"></u-action-sheet>
		
		<!-- tabbar -->
		<lf-tabbar :active="3" :count="messegeNum"></lf-tabbar>
		
		<!-- 发布弹窗 -->
		<u-popup v-model="showPlusPost" mode="center" border-radius="20" width="80%">
			<view class="share-type">
				<view @click="toPost(1)" class="type-item">
					<u-icon class="icon" size="40" name="photo" color="#aaaaff"></u-icon>
					<text>发布帖子</text>
				</view>
				<view @click="toPost(2)" class="type-item" v-if="isOpen==1">
					<u-icon class="icon" size="40" name="play-circle-fill" color="#aa55ff"></u-icon>
					<text>发布视频</text>
				</view>
				<view @click="toPost(4)" class="type-item">
					<u-icon class="icon" size="36" name="order" color="#e34b00"></u-icon>
					<text>发布长文</text>
				</view>
				<view @click="toPost(3)" class="type-item">
					<image class="icon" src="/static/h_1.png"></image>
					<text>发布投票</text>
				</view>
			</view>
		</u-popup>
		
		<!-- 好友操作弹窗 -->
		<u-popup v-model="showFriendPop" mode="center" border-radius="20" width="80%">
			<view class="share-type">
				<view @click="gotoChat" class="type-item">
					<u-icon class="icon" size="40" name="email-fill"></u-icon>
					<text>发送私信</text>
				</view>
				<view @click="doDeleteFriend" class="type-item">
					<u-icon class="icon" size="40" name="trash-fill"></u-icon>
					<text>删除好友</text>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import { ref, reactive, computed, getCurrentInstance } from 'vue'
	import { onLoad, onShow, onPullDownRefresh, onPageScroll } from '@dcloudio/uni-app'
	import $store from '@/store/index.js'

	const buildIndexList = () => {
		const indexList = []
		const charCodeOfA = 'A'.charCodeAt(0)
		indexList.push('↑')
		indexList.push('☆')
		for (let i = 0; i < 26; i++) {
			indexList.push(String.fromCharCode(charCodeOfA + i))
		}
		indexList.push('#')
		return indexList
	}

	const { proxy } = getCurrentInstance()

	// state
	const isOpen = ref(getApp().globalData.isOpen)
	const showPlusPost = ref(false)
	const showSheet = ref(false)
	const showChatSheet = ref(false)
	const sheetList = ref([{ text: '删除', color: 'red' }])
	const msgNum = reactive({
		thumbCount: 0,
		follow: 0,
		comment: 0,
		systemUnreadCount: 0,
		articleMsgList: [],
		chatMsgList: [],
	})
	const checkedMsgId = ref('')
	const checkedIndex = ref('')
	const checkedMsgUid = ref('')
	const adminInfo = reactive({
		username: '系统通知',
		avatar: `${proxy.$c.imgResource}/images/sys.png`,
		uid: 1,
	})
	const pageTab = ref([{ name: '消息' }, { name: '好友' }])
	const pageCurrent = ref(0)
	const indexList = ref(buildIndexList())
	const scrollTop = ref(0)
	const showFriendPop = ref(false)
	const currentFriendId = ref('')
	const selectUser = ref({})
	const socialOpen = ref(false)
	const list = ref([{ name: '消息' }, { name: '好友' }])
	const gridRefreshKey = ref(0) // 用于强制刷新金刚区

	// getters from store
	const loginUserInfo = computed(() => $store.getters.loginUserInfo)
	const isSocketOpen = computed(() => $store.getters.isSocketOpen)
	const friendListShow = computed(() => $store.getters.friendListShow)
	const friendList = computed(() => $store.getters.friendList)
	const totalUnread = computed(() => $store.getters.totalUnread)
	const messegeNum = computed(() => $store.getters.messegeNum)
	const sessionList = computed(() => $store.getters.sessionList)

	// lifecycle
	onLoad(() => {
		checkOpen()
	})

	onShow(() => {
		if (uni.getStorageSync('userInfo').uid) {
			getMsgNum()
		}
		if (!$store.state.isSocketOpen && uni.getStorageSync('hasLogin')) {
			console.log('message.vue: WebSocket未连接，等待App.vue中的连接')
		}
		// 强制刷新金刚区，修复页面切换时icon变形问题
		gridRefreshKey.value++
	})

	onPullDownRefresh(() => {
		getMsgNum()
		uni.stopPullDownRefresh()
	})

	onPageScroll((e) => {
		scrollTop.value = e.scrollTop
	})

	// methods
	const checkOpen = () => {
		proxy.$H.get('system/socialOpen').then((res) => {
			if (res.code == 0) {
				socialOpen.value = res.result
			}
		})
	}

	const pageTabChange = (index) => {
		pageCurrent.value = index
	}

	const goSys = () => {
		if (uni.getStorageSync('hasLogin')) {
			proxy.$f.jump('/pages/system/system')
		} else {
			proxy.$f.toast('请先登录哦')
		}
	}

	const goChatList = () => {
		if (uni.getStorageSync('hasLogin')) {
			proxy.$f.jump('/pages/im/chat-list/chat-list')
		} else {
			proxy.$f.toast('请先登录哦')
		}
	}

	const goNotice = () => {
		if (uni.getStorageSync('hasLogin')) {
			proxy.$f.jump('/pages/im/notice-list/notice-list')
		} else {
			proxy.$f.toast('请先登录哦')
		}
	}

	const articleMsgState = (id) => {
		proxy.$H.post('message/articleMsgState', { id })
	}

	const onPressArticle = () => {
		showSheet.value = true
	}

	const onSheetItem = (index) => {
		if (index == 0) {
			proxy.$H.post('message/delSystemMsg').then((res) => {
				if (res.code == 0) {
					msgNum.systemUnreadCount = 0
					proxy.$f.toast('删除成功')
				}
			})
		}
	}

	const getMsgNum = () => {
		proxy.$H.post('message/num').then((res) => {
			Object.assign(msgNum, res.result)
			let all = $store.state.totalUnread.message + $store.state.totalUnread.notice + res.result.allCount
			$store.state.messegeNum = [0, 0, 0, all, 0]
		})
	}

	const gotoFriendInfo = (id) => {
		uni.navigateTo({
			url: '/pages/user/home?uid=' + id,
		})
	}

	const toPlus = () => {
		showPlusPost.value = true
	}

	const toPost = (i) => {
		showPlusPost.value = false
		if (i == 3) {
			proxy.$f.jump('/pages/vote/vote')
		} else if (i == 4) {
			proxy.$f.jump('/subpages/content/article/add')
		} else {
			proxy.$f.jump('/pages/post/add?type=' + i)
		}
	}

	const showFriend = (uid) => {
		showFriendPop.value = true
		currentFriendId.value = uid
		proxy.$H.post('user/userInfoById', { uid }).then((res) => {
			selectUser.value = res.result
		})
	}

	const doDeleteFriend = () => {
		if (!uni.getStorageSync('hasLogin')) {
			proxy.$f.toast('请先登录哦')
			return
		}
		showFriendPop.value = false
		uni.showModal({
			title: '提示',
			content: '确定删除该好友吗？',
			success: function (res) {
				if (res.confirm) {
					proxy.$H.post('friend/deleteFriend', { id: currentFriendId.value }).then((res) => {
						if (res.code == 0) {
							showFriendPop.value = false
							proxy.$f.toast('操作成功')
							$store.dispatch('getFriendList')
						}
					})
				} else if (res.cancel) {
				}
			},
		})
	}

	const gotoChat = () => {
		if (!uni.getStorageSync('hasLogin')) {
			proxy.$f.toast('请先登录哦')
			return
		}
		for (let i = 0; i < sessionList.value.length; i++) {
			if (sessionList.value[i].chattingUserId == selectUser.value.uid) {
				$store.state.chattingUserInfo = sessionList.value[i]
				break
			}
		}
		proxy.$H
			.post('chat/list', {
				sessionId: $store.state.chattingUserInfo.sessionId,
				pageNum: 1,
				pageSize: 20,
			})
			.then((res) => {
				if (res.code == 0) {
					let data = res.result
					for (let i = 0; i < $store.state.personMessage.length; i++) {
						if ($store.state.personMessage[i].sessionId == data.sessionId) {
							let current = Number(data.pageInfo.current)
							let total = Number(data.pageInfo.total)
							let size = Number(data.pageInfo.size)
							$store.state.personMessage[i].lastMessageId =
								data.pageInfo.records.length != 0 ? data.pageInfo.records[0].id : 0
							$store.state.personMessage[i].list = data.pageInfo.records.reverse()
							$store.state.personMessage[i].pageNum = current
							$store.state.personMessage[i].hasNextPage = current * size < total
							break
						}
					}
					showFriendPop.value = false
					uni.navigateTo({ url: '/pages/im/chat/chat' })
				} else {
					uni.showToast({
						icon: 'none',
						mask: '消息加载失败',
					})
				}
			})
	}
</script>

<style lang="scss" scoped>

	.page-container {
		min-height: 100vh;
		background-color: #ffffff;
		position: relative;
	}

	/* 顶部淡雅渐变背景 */
	.top-gradient {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 500rpx;
		background: linear-gradient(180deg, #eef2ff 0%, #ffffff 100%);
		z-index: 0;
	}

	/* 自定义导航栏 */
	.custom-navbar {
		position: relative;
		z-index: 10;
		/* #ifndef MP */
		padding: 70rpx 30rpx 30rpx; /* 适配刘海屏 */
		/* #endif */
		/* #ifdef MP */
		padding: 100rpx 30rpx 30rpx;
		/* #endif */
		display: flex;
		justify-content: space-between;
		align-items: center;
		
		.nav-tabs {
			display: flex;
			align-items: center;
			
			.tab-item {
				margin-right: 40rpx;
				font-size: 32rpx;
				color: #999;
				font-weight: 500;
				position: relative;
				transition: all 0.3s;
				
				&.active {
					color: #000;
					font-size: 36rpx;
					font-weight: bold;
				}
				
				.active-dot {
					position: absolute;
					top: 0;
					right: -10rpx;
					width: 12rpx;
					height: 12rpx;
					background-color: #fa3534;
					border-radius: 50%;
				}
			}
		}
		
		.nav-actions {
			display: flex;
			align-items: center;
		}
	}

	.content-area {
		position: relative;
		z-index: 10;
		padding: 0 30rpx;
	}

	/* 通知横幅 */
	.notice-banner {
		display: flex;
		justify-content: space-between;
		align-items: center;
		background-color: #fff;
		padding: 20rpx 30rpx;
		border-radius: 100rpx;
		box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.05);
		margin-bottom: 40rpx;
		
		.nb-left {
			display: flex;
			align-items: center;
			
			.nb-text {
				font-size: 26rpx;
				color: #666;
				margin-left: 16rpx;
			}
		}
		
		.nb-btn {
			background-color: #7e5fff;
			color: #fff;
			font-size: 24rpx;
			padding: 8rpx 24rpx;
			border-radius: 30rpx;
		}
	}

	/* 金刚区 Grid */
	.grid-menu {
		display: flex;
		justify-content: center;
		margin-bottom: 50rpx;
		width: 100%;
		flex-wrap: nowrap;
		
		.grid-item {
			display: flex;
			flex-direction: column;
			align-items: center;
			margin: 0 60rpx;
			flex-shrink: 0;
			position: relative;
			
			.icon-box {
				width: 100rpx;
				height: 100rpx;
				min-width: 100rpx;
				min-height: 100rpx;
				border-radius: 50%;
				display: flex;
				justify-content: center;
				align-items: center;
				margin-bottom: 16rpx;
				position: relative;
				box-shadow: 0 8rpx 16rpx rgba(0,0,0,0.1);
				box-sizing: border-box;
				overflow: hidden;
				flex-shrink: 0;
				transform: translateZ(0);
				-webkit-transform: translateZ(0);
				will-change: transform;
				aspect-ratio: 1;
				backface-visibility: hidden;
				-webkit-backface-visibility: hidden;
				
				&.bg-purple { background: linear-gradient(135deg, #bfaaff, #9370ff); }
				&.bg-pink { background: linear-gradient(135deg, #ffb6d9, #ff70a6); }
				&.bg-blue { background: linear-gradient(135deg, #a6e3ff, #55aaff); }
				&.bg-cyan { background: linear-gradient(135deg, #d9b6ff, #aa55ff); }
				
				// 确保内部图标不变形
				::v-deep .u-icon {
					flex-shrink: 0;
					width: auto;
					height: auto;
				}
			}
			
			.grid-text {
				font-size: 26rpx;
				color: #333;
				font-weight: 500;
			}
		}
	}

	/* 消息列表卡片 */
	.message-list-box {
		.msg-card {
			display: flex;
			align-items: center;
			justify-content: space-between;
			padding: 30rpx 0;
			border-bottom: 1rpx solid #f5f5f5;
			
			&:active {
				opacity: 0.8;
			}
			
			.card-left {
				display: flex;
				align-items: center;
				
				.avatar-box {
					width: 90rpx;
					height: 90rpx;
					border-radius: 50%;
					display: flex;
					justify-content: center;
					align-items: center;
					margin-right: 24rpx;
					
					&.bg-purple-grad { background: linear-gradient(135deg, #bfaaff, #9370ff); }
					&.bg-orange-grad { background: linear-gradient(135deg, #ffe0b2, #ffb74d); }
					&.bg-blue-grad { background: linear-gradient(135deg, #b39ddb, #7e57c2); }
				}
				
				.card-info {
					.card-title-row {
						display: flex;
						align-items: center;
						margin-bottom: 8rpx;
						
						.card-title {
							font-size: 32rpx;
							font-weight: 600;
							color: #333;
							margin-right: 10rpx;
						}
					}
					
					.card-desc {
						font-size: 26rpx;
						color: #999;
					}
				}
			}

			.card-right {
				display: flex;
				align-items: center;
				gap: 12rpx;

				.unread-dot {
					min-width: 32rpx;
					height: 32rpx;
					padding: 0 6rpx;
					border-radius: 999rpx;
					background-color: #fa3534;
					color: #fff;
					font-size: 22rpx;
					line-height: 32rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					box-sizing: border-box;
				}
			}
		}
	}

	/* 好友列表样式优化 */
	.friend-list-item {
		display: flex;
		align-items: center;
		padding: 8rpx 12rpx;
		border-bottom: 1rpx solid #f8f8f8;
		background-color: #fff;
		box-sizing: border-box;
		width: 100%;
		
		&:active {
			background-color: #fafafa;
		}
		
		.friend-avatar {
			width: 90rpx;
			height: 90rpx;
			border-radius: 16rpx;
			margin-right: 24rpx;
			background-color: #f0f0f0;
			flex-shrink: 0;
			/* #ifdef MP-WEIXIN */
			overflow: hidden;
			display: block;
			object-fit: cover;
			/* #endif */
		}
		
		.friend-info {
			flex: 1;
			min-width: 0;
			overflow: hidden;
			display: flex;
			align-items: center;
			
			.friend-name {
				font-size: 32rpx;
				color: #333;
				font-weight: 600;
				display: block;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
				width: 100%;
			}
		}
		
		.friend-action {
			padding: 10rpx;
			flex-shrink: 0;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-left: 10rpx;
		}
	}
	
	.custom-anchor {
		padding: 8rpx 12rpx;
		font-weight: bold;
		color: #666;
		/* #ifdef MP-WEIXIN */
		box-sizing: border-box;
		width: 100%;
		/* #endif */
		
		.anchor-text {
			display: block;
		}
	}

	.msg-empty {
		display: flex;
		flex-direction: column;
		align-items: center;
		min-height: 800rpx;

		.img {
			width: 200rpx;
			margin-top: 100rpx;
		}

		.txt {
			color: #999;
			font-size: 26rpx;
			margin-top: 20rpx;
		}
	}

	.count-desc {
		height: 150rpx;
		text-align: center;
		padding-top: 40rpx;
		
		.list__footer {
			color: #ccc;
			font-size: 24rpx;
		}
	}

	.share-type {
		padding: 50rpx 30rpx;

		.type-item {
			background-color: #F5F5F5;
			padding: 20rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			border-radius: 12rpx;
			margin-bottom: 20rpx;

			.icon {
				margin-right: 20rpx;
			}
		}
	}
</style>
