<template>
	<view class="message-container">
		<!-- 顶部操作栏 -->
		<view class="header-section">
			<text class="header-title">消息通知</text>
			<text class="read-all-btn" @click="readStatus">全部已读</text>
		</view>

		<!-- 消息列表 -->
		<view class="message-list">
			<block v-for="(item,index) in msgList" :key="index">
				<view class="message-item" :class="{ 'unread': item.status === 0 }">
					<!-- 用户头像 -->
					<view @click="toUserHome(item.fromUid)" hover-class="none">
						<view class="avatar-wrapper">
							<u-avatar class="user-avatar" :src="item.userInfo.avatar" size="88"></u-avatar>
							<view v-if="item.status === 0" class="unread-dot"></view>
						</view>
					</view>

					<!-- 消息内容区域 -->
					<view class="message-content">
						<!-- 用户信息和时间 -->
						<view class="message-header">
							<text class="username">{{item.userInfo.username}}</text>
							<text class="timestamp">{{timeFormat(item.createTime)}}</text>
						</view>

						<!-- 消息文本 -->
						<view class="message-text">
							<text>{{item.content}}</text>
						</view>

						<!-- 关联帖子 -->
						<view v-if="msgType != 2" @click="toPostDetail(item.postId, item.mid)" hover-class="none">
							<view class="post-preview">
								<view class="post-icon">
									<u-icon name="file-text" color="#ffffff" size="28"></u-icon>
								</view>
								<view class="post-content">
									<text class="post-text">{{item.postContent.substring(0,50)}}...</text>
								</view>
								<view class="arrow-icon">
									<u-icon name="arrow-right" color="#adb5bd" size="32"></u-icon>
								</view>
							</view>
						</view>
					</view>
				</view>
			</block>
		</view>

		<!-- 空状态和加载状态 -->
		<view class="footer-section">
			<block v-if="msgList.length === 0 && loadStatus == 'nomore'">
				<view class="empty-state">
					<text class="empty-icon">📭</text>
					<text class="empty-text">暂无消息</text>
					<text class="empty-desc">当有新消息时会显示在这里</text>
				</view>
			</block>
			<block v-else>
				<view class="load-more">
					<u-loadmore :status="loadStatus" />
				</view>
			</block>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad,
		onShow,
		onReachBottom
	} from '@dcloudio/uni-app'
	import {
		timeFormat
	} from '@/utils/filters.js'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H

	const page = ref(1)
	const current = ref(0)
	const msgType = ref(0)
	const msgList = ref([])
	const loadStatus = ref('loadmore')

	onLoad((options) => {
		msgType.value = options.type
		if (options.type == 2) {
			readAllWatchInfo()
		}
	})

	onShow(() => {
		msgList.value = []
		page.value = 1
		getMsgList()
	})

	onReachBottom(() => {
		page.value++
		getMsgList()
	})

	function readAllWatchInfo() {
		$H.get("message/readAllWatchInfo", {}).then(res => {
			// 读取所有关注信息
		})
	}

	function getMsgList() {
		$H.get("message/list", {
			type: msgType.value,
			page: page.value,
		}).then(res => {
			msgList.value = msgList.value.concat(res.result.data)

			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = 'nomore'
			} else {
				loadStatus.value = 'loadmore'
			}
		})
	}

	function readStatus() {
		$H.get("message/status", {
			type: msgType.value
		}).then(res => {
			msgList.value.forEach((item, index) => {
				msgList.value[index].status = 1
			})
		})
	}

	function toUserHome(uid) {
		uni.navigateTo({
			url: '/pages/user/home?uid=' + uid
		})
	}

	function toPostDetail(postId, mid) {
		uni.navigateTo({
			url: '/pages/post/detail?id=' + postId + '&mid=' + mid
		})
	}
</script>

<style lang="scss" scoped>
	.message-container {
		background-color: #f8f9fa;
		min-height: 100vh;
	}

	// 顶部操作栏
	.header-section {
		background: #ffffff;
		padding: 28rpx 32rpx 36rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.06);
		border-bottom: 1rpx solid #f1f3f4;

		.header-title {
			font-size: 30rpx;
			font-weight: 500;
			color: #2d3748;
			letter-spacing: 0.5rpx;
		}

		.read-all-btn {
			background: #f7f8fa;
			color: #6b7280;
			padding: 12rpx 20rpx;
			border-radius: 40rpx;
			font-size: 24rpx;
			font-weight: 400;
			border: 1rpx solid #e5e7eb;
			transition: all 0.3s ease;

			&:active {
				background: #e5e7eb;
				transform: scale(0.96);
			}
		}
	}

	// 消息列表
	.message-list {
		padding: 24rpx 0;
	}

	.message-item {
		background-color: #ffffff;
		margin: 0 24rpx 20rpx;
		border-radius: 24rpx;
		padding: 32rpx;
		display: flex;
		box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.04);
		border: 1rpx solid #f0f0f0;
		transition: all 0.3s ease;
		position: relative;
		overflow: hidden;

		&.unread {
			border-left: 6rpx solid #6366f1;
			background: linear-gradient(90deg, rgba(99, 102, 241, 0.02) 0%, #ffffff 100%);

			&::before {
				content: '';
				position: absolute;
				top: 0;
				left: 0;
				right: 0;
				height: 2rpx;
				background: #6366f1;
			}
		}

		&:active {
			transform: translateY(-2rpx);
			box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
		}
	}

	// 头像区域
	.avatar-wrapper {
		position: relative;
		margin-right: 24rpx;
		flex-shrink: 0;

		.user-avatar {
			border: 3rpx solid #ffffff;
			box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
		}

		.unread-dot {
			position: absolute;
			top: -4rpx;
			right: -4rpx;
			width: 24rpx;
			height: 24rpx;
			background: linear-gradient(135deg, #ff6b6b, #ee5a52);
			border-radius: 50%;
			border: 3rpx solid #ffffff;
			box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.4);
		}
	}

	// 消息内容区域
	.message-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		min-width: 0;
	}

	.message-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 12rpx;

		.username {
			font-size: 32rpx;
			font-weight: 600;
			color: #2c3e50;
			flex-shrink: 0;
		}

		.timestamp {
			font-size: 24rpx;
			color: #95a5a6;
			margin-left: 16rpx;
		}
	}

	.message-text {
		margin-bottom: 20rpx;

		text {
			font-size: 28rpx;
			color: #5a6c7d;
			line-height: 1.6;
			word-break: break-word;
		}
	}

	// 帖子预览
	.post-preview {
		background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
		border-radius: 16rpx;
		padding: 24rpx;
		display: flex;
		align-items: center;
		border: 1rpx solid #e9ecef;
		transition: all 0.3s ease;

		&:active {
			background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
			transform: scale(0.98);
		}

		.post-icon {
			width: 56rpx;
			height: 56rpx;
			background: #9ca3af;
			border-radius: 12rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-right: 20rpx;
			flex-shrink: 0;
		}

		.post-content {
			flex: 1;
			min-width: 0;

			.post-text {
				font-size: 26rpx;
				color: #6c757d;
				line-height: 1.5;
				display: -webkit-box;
				-webkit-box-orient: vertical;
				-webkit-line-clamp: 2;
				overflow: hidden;
				text-overflow: ellipsis;
			}
		}

		.arrow-icon {
			margin-left: 16rpx;
			display: flex;
			align-items: center;
		}
	}

	// 底部区域
	.footer-section {
		padding: 40rpx 24rpx;
	}

	// 空状态
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 80rpx 40rpx;

		.empty-icon {
			font-size: 120rpx;
			margin-bottom: 32rpx;
			opacity: 0.8;
		}

		.empty-text {
			font-size: 32rpx;
			font-weight: 600;
			color: #6c757d;
			margin-bottom: 16rpx;
		}

		.empty-desc {
			font-size: 26rpx;
			color: #adb5bd;
			text-align: center;
			line-height: 1.5;
		}
	}

	// 加载更多
	.load-more {
		padding: 24rpx 0;
	}

	// 响应式适配
	@media (max-width: 750rpx) {
		.message-item {
			margin: 0 16rpx 16rpx;
			padding: 24rpx;
			border-radius: 20rpx;
		}

		.header-section {
			padding: 24rpx 24rpx 32rpx;

			.header-title {
				font-size: 32rpx;
			}

			.read-all-btn {
				font-size: 24rpx;
				padding: 12rpx 20rpx;
			}
		}
	}

	// 深色模式支持（可选）
	@media (prefers-color-scheme: dark) {
		.message-container {
			background-color: #1a1a1a;
		}

		.message-item {
			background-color: #2d2d2d;
			border-color: #404040;

			.username {
				color: #ffffff;
			}

			.message-text text {
				color: #cccccc;
			}
		}

		.post-preview {
			background: linear-gradient(135deg, #404040 0%, #2d2d2d 100%);
			border-color: #404040;

			.post-text {
				color: #aaaaaa;
			}
		}
	}
</style>