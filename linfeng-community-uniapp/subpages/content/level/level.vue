<template>
	<view class="grade">
		<view class="top">
			<image class="header-bg" :src="$IMG+'/level/arc.png'"></image>
			<view class="level-indicators">
				<view v-if="userInfo.level>1" class="level-indicator prev">
					<image :src="$IMG+'/level/grade.png'"></image>
					<text>{{userInfo.level-1}}</text>
				</view>
				<view v-else class="level-indicator empty">
				</view>
				<view class="level-indicator current">
					<image :src="$IMG+'/level/grade.png'"></image>
					<text>{{userInfo.level}}</text>
				</view>
				<view v-if="userInfo.level<lastLevelId" class="level-indicator next">
					<image :src="$IMG+'/level/grade.png'"></image>
					<text>{{userInfo.level+1}}</text>
				</view>
				<view v-else class="level-indicator empty">
				</view>
			</view>
			<view class="exp-summary">
				<view class="exp-value">
					<text class="exp-label">当前经验值</text>
					<image :src="$IMG+'/level/ring.png'"></image>
					<text class="exp-number">{{userInfo.integral}}</text>
				</view>
				<view class="exp-status">
					<text v-if="needCount>0">距离下一等级还差 <text class="highlight">{{needCount}}</text> 经验</text>
					<text v-else>恭喜！您已到达最高等级</text>
				</view>
			</view>
		</view>
		
		<view class="content">
			<view class="user-profile">
				<view class="avatar-container">
					<image :src="userInfo.avatar" class="avatar"></image>
					<view class="avatar-ring"></view>
				</view>
				<view class="user-info">
					<text class="username">{{userInfo.username}}</text>
					<view class="level-badge">Lv.{{userInfo.level}}</view>
				</view>
				<view class="points-info">
					<text class="points-value">{{userInfo.integral}}</text>
					<text class="points-label">积分数量</text>
				</view>
			</view>
			
			<view class="experience-card">
				<text class="card-title">经验值</text>
				<u-line-progress 
					:percent="percentage" 
					:round="true" 
					active-color="#7580ff"
					:striped-active="true"
					height="16"
					class="progress-bar">
				</u-line-progress>
				<view class="level-progress">
					<text class="current-level">Lv.{{userInfo.level}}</text>
					<text class="exp-progress">{{userInfo.integral}} / {{needCount+userInfo.integral}}</text>
					<text v-if="needCount>0" class="next-level">Lv.{{userInfo.level+1}}</text>
				</view>
			</view>
			
			<view class="tasks-card">
				<view class="card-header">
					<text class="card-title">成长任务</text>
					<text class="card-subtitle">快速提升经验等级的方法</text>
				</view>
				
				<view class="tasks-list">
					<view class="task-item" @click="jump('/pages/post/add?type=1')">
						<view class="task-icon">
							<image :src="$IMG+'/level/grade1.png'"></image>
						</view>
						<view class="task-content">
							<text class="task-title">发布贴子</text>
							<text class="task-desc">发布帖子领取积分奖励</text>
						</view>
						<view class="task-action">
							<text>去发帖</text>
						</view>
					</view>
					
					<view class="task-item" @click="jump('/pages/sign/sign')">
						<view class="task-icon">
							<image :src="$IMG+'/img/yel-sign.png'"></image>
						</view>
						<view class="task-content">
							<text class="task-title">积分签到</text>
							<text class="task-desc">连续签到奖励更多</text>
						</view>
						<view class="task-action">
							<text>去签到</text>
						</view>
					</view>
					
					<view class="task-item" v-if="isOpen" @click="jump('/pages/user/vip/vip')">
						<view class="task-icon">
							<image :src="$IMG+'/level/list1.png'"></image>
						</view>
						<view class="task-content">
							<text class="task-title">成为会员</text>
							<text class="task-desc">积分奖励翻倍</text>
						</view>
						<view class="task-action">
							<text>去充值</text>
						</view>
					</view>
					
					<view class="task-item" @click="jump('/pages/luck-draw/luck-draw')">
						<view class="task-icon">
							<image :src="$IMG+'/level/choujiang.png'"></image>
						</view>
						<view class="task-content">
							<text class="task-title">积分抽奖</text>
							<text class="task-desc">有机会获得积分奖励</text>
						</view>
						<view class="task-action">
							<text>去抽奖</text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const { proxy } = getCurrentInstance()
const $IMG = proxy.$IMG
const $H = proxy.$H
const $f = proxy.$f

// Reactive data
const percentage = ref(0)
const userInfo = ref({})
const needCount = ref(0)
const lastLevelId = ref(5)
const isOpen = ref(false)
const isIphone = getApp().globalData.iphone

// Methods
const getUserInfo = () => {
	$H.get('user/userInfo').then(res => {
		userInfo.value = res.result
	})
}

const getLevelInfo = () => {
	uni.showLoading({
		title: "加载中"
	})
	$H.get('user/getUserLevelInfo').then(res => {
		if (res.code == 0) {
			needCount.value = res.result.needCount
			lastLevelId.value = res.result.lastLevelId
			percentage.value = res.result.rate
		}
		uni.hideLoading()
	})
}

const jump = (url) => {
	$f.jump(url)
}

const getSysInfo = () => {
	$H.get('system/basic').then(res => {
		if(res.vipShow=='1'){
			isOpen.value = false
		}else{
			if(isIphone && res.iosClose=='0'){
				isOpen.value = false
			}else{
				isOpen.value = true
			}
		}
	})
}

// Lifecycle
onLoad(() => {
	getUserInfo()
	getLevelInfo()
	getSysInfo()
})
</script>

<style lang="scss" scoped>
	.grade {
		min-height: 100vh;
		background: #f8fafc;
		
		/* Global text styling */
		text {
			font-family: 'PingFang SC', sans-serif;
		}

		/* Header section with gradient */
		.top {
			width: 750rpx;
			height: 450rpx;
			padding: 0 40rpx;
			border-radius: 0 0 60rpx 60rpx;
			background: linear-gradient(135deg, #a1c4fd 0%, #aaaaff 100%);
			position: relative;
			box-shadow: 0 8rpx 20rpx rgba(161, 196, 253, 0.3);
			
			&::before {
				content: '';
				position: absolute;
				top: 0;
				right: 0;
				width: 300rpx;
				height: 300rpx;
				background: rgba(255, 255, 255, 0.1);
				border-radius: 50%;
				transform: translate(50%, -50%);
			}
			
			&::after {
				content: '';
				position: absolute;
				bottom: -50rpx;
				left: -50rpx;
				width: 200rpx;
				height: 200rpx;
				background: rgba(255, 255, 255, 0.1);
				border-radius: 50%;
			}
			
			.header-bg {
				width: 100%;
				height: 160rpx;
				margin-top: 88rpx;
				opacity: 0.8;
			}

			/* Level indicators section */
			.level-indicators {
				display: flex;
				justify-content: center;
				margin-top: -100rpx;
				position: relative;
				z-index: 2;

				.level-indicator {
					width: 144rpx;
					height: 136rpx;
					justify-content: center;
					align-items: center;
					display: flex;
					position: relative;

					&.empty {
						width: 84rpx;
						height: 80rpx;
						opacity: 0;
					}

					&.current {
						transform: scale(1.2);
						z-index: 2;
						margin: 0 52rpx;
					}

					&.prev, &.next {
						margin-top: -40rpx;
						opacity: 0.8;
						
						>image {
							width: 84rpx;
							height: 80rpx;
						}
						
						>text {
							font-size: 40rpx;
						}
					}

					image {
						width: 144rpx;
						height: 136rpx;
					}

					text {
						font-size: 64rpx;
						font-weight: 600;
						color: #999999;
						line-height: 90rpx;
						text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
						background: linear-gradient(180deg, #FFFFFF 0%, #5662f6 100%);
						-webkit-background-clip: text;
						-webkit-text-fill-color: transparent;
						position: absolute;
					}
				}
			}

			/* Experience summary section */
			.exp-summary {
				display: flex;
				flex-direction: column;
				align-items: center;
				margin-top: 30rpx;
				position: relative;
				z-index: 2;

				.exp-value {
					display: flex;
					align-items: center;
					background: rgba(255, 255, 255, 0.2);
					border-radius: 30rpx;
					padding: 6rpx 20rpx;

					.exp-label {
						font-size: 28rpx;
						color: #FFFFFF;
						line-height: 40rpx;
						font-weight: 500;
					}

					>image {
						width: 36rpx;
						height: 30rpx;
						margin: 0 8rpx;
					}

					.exp-number {
						font-size: 38rpx;
						font-weight: 600;
						color: #FFFFFF;
					}
				}

				.exp-status {
					margin-top: 16rpx;
					background: rgba(255, 255, 255, 0.15);
					border-radius: 20rpx;
					padding: 4rpx 16rpx;
					
					text {
						font-size: 22rpx;
						color: rgba(255, 255, 255, 0.9);
						line-height: 40rpx;
						
						.highlight {
							color: #FFFFFF;
							font-weight: 600;
							font-size: 24rpx;
						}
					}
				}
			}
		}

		/* Main content section */
		.content {
			padding: 0 40rpx;
			margin-top: -60rpx;
			position: relative;
			z-index: 5;

			/* User profile card */
			.user-profile {
				width: 100%;
				height: 116rpx;
				background: linear-gradient(90deg, #5662f6 0%, #7580ff 100%);
				border-radius: 58rpx;
				padding: 0 36rpx 0 18rpx;
				display: flex;
				align-items: center;
				box-shadow: 0 8rpx 16rpx rgba(86, 98, 246, 0.2);
				
				.avatar-container {
					width: 84rpx;
					height: 84rpx;
					display: flex;
					justify-content: center;
					align-items: center;
					margin-right: 20rpx;
					position: relative;

					.avatar {
						width: 84rpx;
						height: 84rpx;
						border-radius: 50%;
						border: 2rpx solid #FFFFFF;
					}

					.avatar-ring {
						width: 92rpx;
						height: 92rpx;
						border-radius: 50%;
						border: 2rpx solid rgba(255, 255, 255, 0.8);
						position: absolute;
						z-index: -1;
					}
				}

				.user-info {
					flex: 1;
					display: flex;
					align-items: center;

					.username {
						font-size: 36rpx;
						font-weight: 600;
						color: #FFFFFF;
						line-height: 50rpx;
						margin-right: 14rpx;
						text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.1);
					}

					.level-badge {
						height: 34rpx;
						background: rgba(255, 255, 255, 0.3);
						border-radius: 17rpx;
						padding: 0 14rpx;
						font-size: 24rpx;
						color: #FFFFFF;
						line-height: 34rpx;
						font-weight: 500;
					}
				}

				.points-info {
					display: flex;
					flex-direction: column;
					align-items: flex-end;

					.points-value {
						font-size: 36rpx;
						font-weight: 600;
						color: #FFFFFF;
						line-height: 50rpx;
					}

					.points-label {
						font-size: 24rpx;
						color: rgba(255, 255, 255, 0.8);
						line-height: 34rpx;
					}
				}
			}

			/* Experience progress card */
			.experience-card {
				width: 100%;
				background: #FFFFFF;
				border-radius: 20rpx;
				padding: 40rpx 30rpx;
				margin-top: 24rpx;
				box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.04);

				.card-title {
					display: block;
					font-size: 32rpx;
					font-weight: 600;
					color: #333333;
					line-height: 44rpx;
					margin-bottom: 30rpx;
					position: relative;
					
					&::after {
						content: '';
						position: absolute;
						bottom: -10rpx;
						left: 0;
						width: 60rpx;
						height: 4rpx;
						background: #5662f6;
						border-radius: 2rpx;
					}
				}
				
				.progress-bar {
					margin: 10rpx 0;
				}

				.level-progress {
					display: flex;
					margin: 22rpx 0 20rpx 0;
					justify-content: space-between;
					align-items: center;

					.current-level {
						font-size: 28rpx;
						font-weight: 600;
						color: #333333;
					}
					
					.exp-progress {
						font-size: 28rpx;
						color: #666666;
					}
					
					.next-level {
						font-size: 28rpx;
						font-weight: 600;
						color: #5662f6;
					}
				}
			}

			/* Tasks card */
			.tasks-card {
				width: 100%;
				background: #FFFFFF;
				border-radius: 20rpx;
				margin-top: 24rpx;
				margin-bottom: 30rpx;
				padding: 40rpx 30rpx;
				box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.04);
				
				.card-header {
					margin-bottom: 30rpx;
					
					.card-title {
						font-size: 32rpx;
						font-weight: 600;
						color: #333333;
						line-height: 44rpx;
						position: relative;
						
						&::after {
							content: '';
							position: absolute;
							bottom: -10rpx;
							left: 0;
							width: 60rpx;
							height: 4rpx;
							background: #5662f6;
							border-radius: 2rpx;
						}
					}

					.card-subtitle {
						font-size: 24rpx;
						color: #999999;
						line-height: 34rpx;
						margin-top: 14rpx;
						display: block;
					}
				}

				.tasks-list {
					.task-item {
						display: flex;
						align-items: center;
						padding: 20rpx 0;
						position: relative;
						
						&:not(:last-child)::after {
							content: '';
							position: absolute;
							left: 0;
							right: 0;
							bottom: 0;
							height: 1rpx;
							background: #f5f5f5;
						}

						.task-icon {
							width: 84rpx;
							height: 84rpx;
							border-radius: 50%;
							background: #f0f3ff;
							display: flex;
							justify-content: center;
							align-items: center;
							margin-right: 20rpx;
							box-shadow: 0 4rpx 8rpx rgba(86, 98, 246, 0.15);

							>image {
								width: 50rpx;
								height: 50rpx;
							}
						}

						.task-content {
							flex: 1;
							display: flex;
							flex-direction: column;

							.task-title {
								font-size: 28rpx;
								color: #333333;
								line-height: 40rpx;
								font-weight: 500;
							}

							.task-desc {
								font-size: 24rpx;
								color: #999999;
								line-height: 34rpx;
								margin-top: 4rpx;
							}
						}

						.task-action {
							width: 130rpx;
							height: 56rpx;
							background: linear-gradient(90deg, #b1b5ef 0%, #7580ff 100%);
							border-radius: 28rpx;
							text-align: center;
							box-shadow: 0 4rpx 8rpx rgba(86, 98, 246, 0.2);
							transition: all 0.2s ease;
							
							&:active {
								transform: scale(0.95);
							}

							>text {
								font-size: 24rpx;
								font-weight: 600;
								color: #FFFFFF;
								line-height: 56rpx;
							}
						}
					}
				}
			}
		}
	}
</style>