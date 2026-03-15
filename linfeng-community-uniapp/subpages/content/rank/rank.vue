<template>
	<view>
		<view class="rank-container">
			<!-- 渐变背景头部 -->
			<view class="header-gradient">
				<view class="tab">
					<view :class="{'tab-active' : currentRank === 0}" @click="tabChangeRank()">
						<text>发帖排行</text>
					</view>
					<view :class="{'tab-active' : currentRank === 1}" @click="tabChangeRank()">
						<text>涨粉排行</text>
					</view>
				</view>
			</view>

			<!-- 前三名领奖台 -->
			<view class="podium" v-if="userList.length > 0">
				<view class="podium-item second" v-if="userList.length >= 2">
					<view class="rank-badge">2</view>
					<image class="podium-avatar" :src="userList[1].avatar" @click="goUser(userList[1].uid)"></image>
					<text class="podium-username">{{userList[1].username}}</text>
					<view class="podium-stats">
						<text v-if="currentRank==0">📄 {{userList[1].postNumber}}</text>
						<text v-if="currentRank==1">👥 {{userList[1].fans}}</text>
					</view>
				</view>

				<view class="podium-item first">
					<view class="crown">👑</view>
					<view class="rank-badge champion">1</view>
					<image class="podium-avatar" :src="userList[0].avatar" @click="goUser(userList[0].uid)"></image>
					<text class="podium-username">{{userList[0].username}}</text>
					<view class="podium-stats champion-stats">
						<text v-if="currentRank==0">📄 {{userList[0].postNumber}}</text>
						<text v-if="currentRank==1">👥 {{userList[0].fans}}</text>
					</view>
				</view>

				<view class="podium-item third" v-if="userList.length >= 3">
					<view class="rank-badge">3</view>
					<image class="podium-avatar" :src="userList[2].avatar" @click="goUser(userList[2].uid)"></image>
					<text class="podium-username">{{userList[2].username}}</text>
					<view class="podium-stats">
						<text v-if="currentRank==0">📄 {{userList[2].postNumber}}</text>
						<text v-if="currentRank==1">👥 {{userList[2].fans}}</text>
					</view>
				</view>
			</view>

			<!-- 完整排行榜 -->
			<view class="ranking-list">
				<view class="ranking-header">
					<text class="ranking-title">完整排行榜</text>
				</view>
				<template v-if="userList.length > 0">
					<view class="rank-item" v-for="(item,index) in userList" :key="index"
						:class="{'top-three': index < 3}">
						<view class="rank-left">
							<view class="rank-number"
								:class="{'gold': index === 0, 'silver': index === 1, 'bronze': index === 2}">
								{{index+1}}
							</view>
							<image class="user-avatar" :src="item.avatar" @click="goUser(item.uid)"></image>
							<view class="user-info" @click="goUser(item.uid)">
								<text class="username">{{item.username}}</text>
								<text class="user-rank">第{{index+1}}名</text>
							</view>
						</view>
						<view class="rank-score">
							<text class="score-number" v-if="currentRank==0">{{item.postNumber}}</text>
							<text class="score-number" v-if="currentRank==1">{{item.fans}}</text>
							<text class="score-label" v-if="currentRank==0">发帖</text>
							<text class="score-label" v-if="currentRank==1">粉丝</text>
						</view>
					</view>
				</template>
			</view>

			<!-- 说明信息 -->
			<view class="info-card">
				<view class="info-icon">💡</view>
				<text class="info-text" v-if="currentRank==0">发帖排行：按照近一周发帖数量进行排名</text>
				<text class="info-text" v-if="currentRank==1">涨粉排行：按照最近粉丝数增长幅度进行排名</text>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="userList.length == 0">
				<view class="empty-icon">📊</view>
				<text class="empty-title">暂无排行数据</text>
				<text class="empty-desc">还没有用户上榜，快来成为第一名吧！</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

// Reactive data
const currentRank = ref(0)
const userList = ref([])

// Methods
const tabChangeRank = () => {
	currentRank.value = currentRank.value ? 0 : 1
	userList.value = []
	if (currentRank.value == 0) {
		getUserRanking()
	} else {
		getHotUserList()
	}
}

const getUserRanking = () => {
	$H.post('user/userRank').then(res => {
		userList.value = res.result
	})
}

const getHotUserList = () => {
	$H.get('user/getHotUserList').then(res => {
		userList.value = res.result
	})
}

const goUser = (uid) => {
	uni.navigateTo({
		url: "/pages/user/home?uid=" + uid
	})
}

// Lifecycle
onLoad(() => {
	if (currentRank.value == 0) {
		getUserRanking()
	} else {
		getHotUserList()
	}
})
</script>

<style lang="scss" scoped>
	.rank-container {
		min-height: 100vh;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	}

	.header-gradient {
		padding: 40rpx 0 60rpx 0;
	}

	.tab {
		display: flex;
		justify-content: center;
		align-items: center;
		margin: 0 40rpx;
		background: rgba(255, 255, 255, 0.1);
		border-radius: 60rpx;
		padding: 8rpx;
		backdrop-filter: blur(10px);

		view {
			flex: 1;
			height: 80rpx;
			line-height: 80rpx;
			text-align: center;
			border-radius: 52rpx;
			font-size: 28rpx;
			font-weight: 600;
			color: rgba(255, 255, 255, 0.8);
			transition: all 0.3s ease;

			text {
				display: flex;
				align-items: center;
				justify-content: center;
			}
		}

		.tab-active {
			background: #fff;
			color: #667eea;
			box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);
		}
	}

	.podium {
		display: flex;
		justify-content: center;
		align-items: flex-end;
		padding: 0 40rpx 40rpx;
		margin-bottom: 40rpx;

		.podium-item {
			display: flex;
			flex-direction: column;
			align-items: center;
			position: relative;
			margin: 0 20rpx;

			.crown {
				position: absolute;
				top: -40rpx;
				font-size: 60rpx;
				animation: bounce 2s infinite;
			}

			.rank-badge {
				position: absolute;
				top: -15rpx;
				right: -15rpx;
				width: 50rpx;
				height: 50rpx;
				border-radius: 50%;
				background: #ccc;
				color: #fff;
				font-size: 24rpx;
				font-weight: bold;
				display: flex;
				align-items: center;
				justify-content: center;
				border: 4rpx solid #fff;
				z-index: 2;

				&.champion {
					background: linear-gradient(45deg, #ffd700, #ffed4e);
					color: #333;
				}
			}

			.podium-avatar {
				width: 120rpx;
				height: 120rpx;
				border-radius: 50%;
				border: 6rpx solid #fff;
				box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.2);
			}

			.podium-username {
				color: #fff;
				font-size: 26rpx;
				font-weight: 600;
				margin: 20rpx 0 10rpx;
				text-align: center;
				max-width: 140rpx;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}

			.podium-stats {
				background: rgba(255, 255, 255, 0.2);
				padding: 10rpx 20rpx;
				border-radius: 30rpx;
				color: #fff;
				font-size: 22rpx;
				font-weight: 600;
				backdrop-filter: blur(10px);

				&.champion-stats {
					background: linear-gradient(45deg, #ffd700, #ffed4e);
					color: #333;
				}
			}

			&.first {
				.podium-avatar {
					width: 140rpx;
					height: 140rpx;
					border-color: #ffd700;
				}
			}

			&.second .rank-badge {
				background: linear-gradient(45deg, #c0c0c0, #e8e8e8);
				color: #333;
			}

			&.third .rank-badge {
				background: linear-gradient(45deg, #cd7f32, #daa520);
				color: #fff;
			}
		}
	}

	.ranking-list {
		background: #fff;
		margin: 0 30rpx;
		border-radius: 30rpx 30rpx 0 0;
		min-height: 500rpx;
		padding-bottom: 40rpx;
	}

	.ranking-header {
		padding: 40rpx;
		text-align: center;
		border-bottom: 2rpx solid #f5f5f5;

		.ranking-title {
			font-size: 32rpx;
			font-weight: bold;
			color: #333;
		}
	}

	.rank-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 30rpx 40rpx;
		border-bottom: 2rpx solid #f8f9fa;
		transition: all 0.3s ease;

		&:last-child {
			border-bottom: none;
		}

		&.top-three {
			background: linear-gradient(90deg, #fff7e6 0%, #fff 100%);
		}

		.rank-left {
			display: flex;
			align-items: center;
			flex: 1;

			.rank-number {
				width: 60rpx;
				height: 60rpx;
				border-radius: 50%;
				background: #f5f5f5;
				color: #999;
				font-size: 24rpx;
				font-weight: bold;
				display: flex;
				align-items: center;
				justify-content: center;
				margin-right: 30rpx;

				&.gold {
					background: linear-gradient(45deg, #ffd700, #ffed4e);
					color: #333;
				}

				&.silver {
					background: linear-gradient(45deg, #c0c0c0, #e8e8e8);
					color: #333;
				}

				&.bronze {
					background: linear-gradient(45deg, #cd7f32, #daa520);
					color: #fff;
				}
			}

			.user-avatar {
				width: 80rpx;
				height: 80rpx;
				border-radius: 50%;
				margin-right: 24rpx;
				border: 2rpx solid #f0f0f0;
			}

			.user-info {
				.username {
					display: block;
					font-size: 28rpx;
					font-weight: 600;
					color: #333;
					margin-bottom: 6rpx;
				}

				.user-rank {
					font-size: 22rpx;
					color: #999;
				}
			}
		}

		.rank-score {
			text-align: right;

			.score-number {
				display: block;
				font-size: 32rpx;
				font-weight: bold;
				color: #667eea;
				margin-bottom: 6rpx;
			}

			.score-label {
				font-size: 22rpx;
				color: #999;
			}
		}
	}

	.info-card {
		background: rgba(255, 255, 255, 0.95);
		margin: 30rpx;
		padding: 30rpx;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		backdrop-filter: blur(10px);
		box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);

		.info-icon {
			font-size: 40rpx;
			margin-right: 20rpx;
		}

		.info-text {
			flex: 1;
			font-size: 24rpx;
			color: #666;
			line-height: 1.5;
		}
	}

	.empty-state {
		text-align: center;
		padding: 100rpx 40rpx;
		color: #fff;

		.empty-icon {
			font-size: 120rpx;
			margin-bottom: 30rpx;
		}

		.empty-title {
			display: block;
			font-size: 32rpx;
			font-weight: bold;
			margin-bottom: 20rpx;
		}

		.empty-desc {
			font-size: 24rpx;
			opacity: 0.8;
		}
	}

	@keyframes bounce {

		0%,
		20%,
		50%,
		80%,
		100% {
			transform: translateY(0);
		}

		40% {
			transform: translateY(-10rpx);
		}

		60% {
			transform: translateY(-5rpx);
		}
	}
</style>