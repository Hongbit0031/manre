<template>
	<view class="notice-container">
		<view v-if="noticeList.length>0" class="title-head">
			<text class="title-text">全部通知消息</text>
		</view>

		<view class="list" v-if="noticeList.length>0">
			<view v-for="(item,index) in noticeList" :key="index" class="notice-item" 
				:class="{'unread': !item.isRead}">
				<view class="item-content">
					<view class="avatar-section" @click="toUserHome(item)">
						<u-avatar shape="circle" size="100" fontSize="28"
							:src="getUserAvatar(item)"
							bgColor="#FEA356"></u-avatar>
						<view v-if="!item.isRead" class="unread-dot"></view>
					</view>
					
					<view class="content-section">
						<view class="message-content">
							<text class="message-text">{{showDetail(item)}}</text>
						</view>
						<view class="time-section">
							<text class="time-text">{{item.createTime}}</text>
						</view>
					</view>
					
					<view class="action-section">
						<button class="action-btn read-btn" @click="check(item.id)"
							v-if="!item.isRead&&item.type==='reject'">
							已读
						</button>
						
						<button class="action-btn delete-btn" @tap="deleteNotice(item.id)" v-if="item.isRead">
							删除
						</button>
						
						<view class="btn-group" v-if="!item.isRead&&item.type!=='reject'">
							<button class="action-btn agree-btn" @click="agree(item)">
								同意
							</button>
							<button class="action-btn refuse-btn" @click="reject(item.id)">
								拒绝
							</button>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<view v-if="noticeList.length == 0" class="empty-state">
			<view class="empty-icon">📭</view>
			<text class="empty-title">暂无通知消息</text>
			<text class="empty-subtitle">所有通知消息都会在这里显示</text>
		</view>
	</view>
</template>

<script setup>
import { computed, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useStore } from 'vuex'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const store = useStore()

// 计算属性
const loginUserInfo = computed(() => store.getters.loginUserInfo)
const noticeList = computed(() => store.getters.noticeList)

// 生命周期
onLoad(() => {
	store.dispatch('getNoticeList')
})

// 方法
const check = (id) => {
	$H.post("notice/readById", {
		id: id
	}).then(res => {
		store.dispatch('getNoticeList')
	})
}

const reject = (id) => {
	$H.post("notice/reject", {
		id: id,
		senderName: loginUserInfo.value.username,
		senderAvatar: loginUserInfo.value.avatar
	}).then(res => {
		if (res.code == 0) {
			uni.showToast({
				title: '操作成功',
				icon: 'none'
			})
			store.dispatch('getNoticeList')
		}
	})
}

const deleteNotice = (id) => {
	$H.post("notice/delete", {
		id: id
	}).then(res => {
		uni.showToast({
			title: '删除成功',
			icon: 'none'
		})
		store.dispatch('getNoticeList')
	})
}

const agree = (item) => {
	if (item.type == 'person-apply') {
		agreePersonApply(item.id)
		return
	}
}

const agreePersonApply = (id) => {
	let msg = {
		type: 'person-apply-agree',
		data: {
			id: id
		}
	}
	uni.sendSocketMessage({
		data: JSON.stringify(msg),
		fail(res) {
			setTimeout(function() {
				retry(id);
			}, 1200);
		}
	})
}

const retry = (id) => {
	$H.post('friend/agreePersonApply', {
		id: id
	}).then(res => {
		if (res.code == 0) {
			store.dispatch('getNoticeList')
			uni.showToast({
				icon: 'success',
				title: '操作成功'
			})
		}
	})
}

const getUserAvatar = (item) => {
	let info = JSON.parse(item.information)
	if(info.senderAvatar){
		return info.senderAvatar;
	}
	return "";
}

const toUserHome = (item) => {
	let info = JSON.parse(item.information)
	uni.navigateTo({
		url: '/pages/user/home?uid=' + item.senderId
	})
}

const showDetail = (item) => {
	let info = JSON.parse(item.information)
	switch (item.type) {
		case 'person-apply':
			return '验证信息: ' + info.applyMessage
			break;
		case 'reject':
			return '拒绝消息(来自: ' + info.senderName + ')'
			break;
	}
}
</script>

<style lang="scss" scoped>
	.notice-container {
		background-color: #f8f9fa;
		min-height: 100vh;
		padding: 0 24rpx;
	}

	.title-head {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 40rpx 0 20rpx 0;
		
		.title-text {
			color: #1a1a1a;
			font-size: 36rpx;
			font-weight: 600;
		}
		
		.count-badge {
			background: #007aff;
			color: white;
			padding: 8rpx 16rpx;
			border-radius: 20rpx;
			font-size: 24rpx;
			min-width: 40rpx;
			text-align: center;
		}
	}

	.list {
		.notice-item {
			background: white;
			border-radius: 16rpx;
			margin-bottom: 16rpx;
			box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);
			transition: all 0.3s ease;
			
			&.unread {
				border-left: 6rpx solid #007aff;
				background: linear-gradient(90deg, rgba(0, 122, 255, 0.02) 0%, rgba(255,255,255,1) 10%);
			}
			
			&:active {
				transform: scale(0.98);
			}
		}

		.item-content {
			display: flex;
			align-items: flex-start;
			padding: 32rpx 24rpx;
			position: relative;
		}

		.avatar-section {
			position: relative;
			margin-right: 24rpx;
			
			.unread-dot {
				position: absolute;
				top: -4rpx;
				right: -4rpx;
				width: 20rpx;
				height: 20rpx;
				background: #ff3b30;
				border-radius: 50%;
				border: 3rpx solid white;
			}
		}

		.content-section {
			flex: 1;
			min-width: 0;
			
			.message-content {
				margin-bottom: 12rpx;
				
				.message-text {
					font-size: 28rpx;
					color: #333;
					line-height: 1.5;
					word-break: break-word;
				}
			}
			
			.time-section {
				.time-text {
					font-size: 24rpx;
					color: #999;
				}
			}
		}

		.action-section {
			display: flex;
			flex-direction: column;
			gap: 8rpx;
			margin-left: 20rpx;
			align-items: flex-end;
			
			.btn-group {
				display: flex;
				flex-direction: column;
				gap: 12rpx;
			}
		}

		.action-btn {
			padding: 12rpx 32rpx;
			border-radius: 30rpx;
			border: none;
			font-size: 24rpx;
			font-weight: 500;
			transition: all 0.2s ease;
			min-width: 120rpx;
			height: 56rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.1);
			
			&:active {
				transform: scale(0.95);
				opacity: 0.8;
			}
		}

		.read-btn {
			background-color: #5c87ff;
			color: white;
			
			&:active {
				background-color: #4a6ecc;
			}
		}

		.delete-btn {
			background-color: #f5f6fa;
			color: #666;
			border: 1rpx solid #ebedf5;
			box-shadow: none;
			
			&:active {
				background-color: #ebedf5;
			}
		}

		.agree-btn {
			background-color: #2979ff;
			color: white;
			margin-bottom: 8rpx;
			
			&:active {
				background-color: #aaaaff;
			}
		}

		.refuse-btn {
			background-color: #ff6b6b;
			color: white;
			
			&:active {
				background-color: #e66262;
			}
		}
	}

	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 120rpx 40rpx;
		min-height: 600rpx;
		
		.empty-icon {
			font-size: 120rpx;
			margin-bottom: 40rpx;
			opacity: 0.6;
		}
		
		.empty-title {
			font-size: 32rpx;
			color: #666;
			font-weight: 500;
			margin-bottom: 16rpx;
		}
		
		.empty-subtitle {
			font-size: 26rpx;
			color: #999;
			text-align: center;
			line-height: 1.5;
		}
	}
</style>