<template>
	<view class="setting-container">
		<!-- 通知设置 -->
		<view class="setting-section">
			<view class="section-title">通知设置</view>
			<view class="setting-card">
				<view class="setting-item">
					<view class="item-left">
						<view class="item-icon">
							<u-icon name="bell" size="32" color="#6b7280"></u-icon>
						</view>
						<view class="item-content">
							<view class="item-title">私信铃声</view>
							<view class="item-desc">收到私信时播放提示音</view>
						</view>
					</view>
					<view class="item-right">
						<u-switch v-model="systemConfig.bell" @change="switchChange" 
							active-color="#6366f1" inactive-color="#e5e7eb"></u-switch>
					</view>
				</view>
				
				<!-- #ifdef APP-PLUS -->
				<view class="setting-item">
					<view class="item-left">
						<view class="item-icon">
							<u-icon name="mic-off" size="32" color="#6b7280"></u-icon>
						</view>
						<view class="item-content">
							<view class="item-title">私信震动</view>
							<view class="item-desc">收到私信时设备震动提醒</view>
						</view>
					</view>
					<view class="item-right">
						<u-switch v-model="systemConfig.vibrate" @change="switchChange" 
							active-color="#6366f1" inactive-color="#e5e7eb"></u-switch>
					</view>
				</view>
				<!-- #endif -->
			</view>
		</view>

		<!-- 隐私设置 -->
		<view class="setting-section">
			<view class="section-title">隐私设置</view>
			<view class="setting-card">
				<view class="setting-item">
					<view class="item-left">
						<view class="item-icon">
							<u-icon name="heart" size="32" color="#6b7280"></u-icon>
						</view>
						<view class="item-content">
							<view class="item-title">隐藏关注列表</view>
							<view class="item-desc">仅自己可见关注的用户</view>
						</view>
					</view>
					<view class="item-right">
						<u-switch v-model="form.isWatch" @change="change" 
							active-color="#6366f1" inactive-color="#e5e7eb"></u-switch>
					</view>
				</view>

				<view class="setting-item">
					<view class="item-left">
						<view class="item-icon">
							<u-icon name="account" size="32" color="#6b7280"></u-icon>
						</view>
						<view class="item-content">
							<view class="item-title">隐藏粉丝列表</view>
							<view class="item-desc">仅自己可见粉丝用户</view>
						</view>
					</view>
					<view class="item-right">
						<u-switch v-model="form.isFollow" @change="change" 
							active-color="#6366f1" inactive-color="#e5e7eb"></u-switch>
					</view>
				</view>

				<view class="setting-item last-item">
					<view class="item-left">
						<view class="item-icon">
							<u-icon name="file-text" size="32" color="#6b7280"></u-icon>
						</view>
						<view class="item-content">
							<view class="item-title">隐藏我的作品</view>
							<view class="item-desc">仅自己可见发布的内容</view>
						</view>
					</view>
					<view class="item-right">
						<u-switch v-model="form.isPost" @change="change" 
							active-color="#6366f1" inactive-color="#e5e7eb"></u-switch>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { getCurrentInstance } from 'vue'

// 获取当前实例
const { proxy } = getCurrentInstance()

// 使用store
const store = useStore()

// 响应式数据
const form = ref({
	isFollow: false,
	isWatch: false,
	isPost: false
})

// 计算属性
const systemConfig = computed(() => store.getters.systemConfig)

// 方法
const getUserSetting = () => {
	proxy.$H.get('user/userSetting').then(res => {
		if (res.code === 0) {
			form.value.isFollow = res.result.isFan
			form.value.isWatch = res.result.isWatch
			form.value.isPost = res.result.isPost
		}
	})
}

const switchChange = () => {
	uni.setStorageSync('system_config', systemConfig.value)
}

const change = () => {
	proxy.$H.post('user/updateUserSetting', {
		isFan: form.value.isFollow,
		isWatch: form.value.isWatch,
		isPost: form.value.isPost
	}).then(res => {
		if (res.code === 0) {
			proxy.$f.toast(res.msg)
		}
	})
}

// 生命周期
onMounted(() => {
	getUserSetting()
})
</script>
<style lang="scss" scoped>
	.setting-container {
		background-color: #f9fafb;
		min-height: 100vh;
		padding: 24rpx 24rpx 40rpx;
	}

	.setting-section {
		margin-bottom: 32rpx;

		.section-title {
			font-size: 28rpx;
			font-weight: 600;
			color: #374151;
			margin-bottom: 16rpx;
			padding: 0 8rpx;
		}

		.setting-card {
			background: #ffffff;
			border-radius: 16rpx;
			box-shadow: 0 1rpx 3rpx rgba(0, 0, 0, 0.04);
			border: 1rpx solid #f3f4f6;
			overflow: hidden;
		}

		.setting-item {
			display: flex;
			align-items: center;
			justify-content: space-between;
			padding: 32rpx 24rpx;
			border-bottom: 1rpx solid #f3f4f6;
			transition: background-color 0.2s ease;

			&:active {
				background-color: #f9fafb;
			}

			&.last-item {
				border-bottom: none;
			}

			.item-left {
				display: flex;
				align-items: center;
				flex: 1;

				.item-icon {
					width: 72rpx;
					height: 72rpx;
					border-radius: 16rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					margin-right: 24rpx;
					background: #f3f4f6;
				}

				.item-content {
					flex: 1;

					.item-title {
						font-size: 32rpx;
						font-weight: 500;
						color: #111827;
						line-height: 1.3;
						margin-bottom: 4rpx;
					}

					.item-desc {
						font-size: 26rpx;
						color: #6b7280;
						line-height: 1.4;
					}
				}
			}

			.item-right {
				margin-left: 24rpx;
			}
		}
	}
</style>