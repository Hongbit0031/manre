<template>
	<view class="account-delete-page">
		<view class="content-container">
			<!-- 警告图标 -->
			<view class="warning-icon">
				<view class="icon-circle">
					<text class="icon-text">!</text>
				</view>
			</view>

			<!-- 标题 -->
			<view class="title">重要提醒</view>

			<!-- 介绍文字 -->
			<view class="intro-text">
				请您在注销账号前，仔细阅读以下提醒内容，注销后您的账号将无法使用。
			</view>

			<!-- 后果列表 -->
			<view class="consequences-list">
				<view class="list-item">
					<text class="bullet">•</text>
					<text class="item-text">无法登录使用账号，移除所有登录方式</text>
				</view>
				<view class="list-item">
					<text class="bullet">•</text>
					<text class="item-text">账号头像重置为默认，昵称重置为"用户已注销"</text>
				</view>
				<view class="list-item">
					<text class="bullet">•</text>
					<text class="item-text">个人数据和历史信息将无法恢复</text>
				</view>
				<view class="list-item">
					<text class="bullet">•</text>
					<text class="item-text">删除所有帖子，且无法恢复</text>
				</view>
				<view class="list-item">
					<text class="bullet">•</text>
					<text class="item-text">取消会员身份，且无法恢复</text>
				</view>
				<view class="list-item">
					<text class="bullet">•</text>
					<text class="item-text">清空账号内所有资产和权益</text>
				</view>
			</view>
		</view>

		<!-- 底部固定区域 -->
		<view class="footer">
			<!-- 复选框 -->
			<view class="checkbox-container" @click="toggleAgree">
				<view class="checkbox" :class="{ checked: isAgreed }">
					<text class="checkmark" v-if="isAgreed">✓</text>
				</view>
				<text class="checkbox-text">我已阅读并知晓上述注销后果</text>
			</view>

			<!-- 按钮 -->
			<view class="next-button" :class="{ disabled: !isAgreed || countdown > 0 }" @click="handleNext">
				<text class="button-text">
					{{ countdown > 0 ? `请先阅读注销说明 ${countdown}s` : '确定注销' }}
				</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance, onMounted, onUnmounted } from 'vue'

const { proxy } = getCurrentInstance()
const $H = proxy?.$H

// 是否同意
const isAgreed = ref(false)

// 倒计时（秒）
const countdown = ref(60)
let timer = null

// 切换同意状态
const toggleAgree = () => {
	isAgreed.value = !isAgreed.value
}

// 开始倒计时
const startCountdown = () => {
	timer = setInterval(() => {
		if (countdown.value > 0) {
			countdown.value--
		} else {
			clearInterval(timer)
			timer = null
		}
	}, 1000)
}

// 下一步
const handleNext = () => {
	if (countdown.value > 0) {
		uni.showToast({
			title: `请先阅读注销说明 ${countdown.value} 秒后再操作`,
			icon: 'none'
		})
		return
	}

	if (!isAgreed.value) {
		uni.showToast({
			title: '请先阅读并同意注销须知',
			icon: 'none'
		})
		return
	}

	// 跳转到下一步（可以跳转到验证页面或确认页面）
	uni.showModal({
		title: '确认注销',
		content: '注销账号后，所有数据将无法恢复，确定要继续吗？',
		confirmText: '确定注销',
		cancelText: '取消',
		confirmColor: '#ff4757',
		success: (res) => {
			if (res.confirm) {
				// 调用注销接口
				deleteAccount()
			}
		}
	})
}

// 注销账号
const deleteAccount = () => {
	uni.showLoading({
		title: '注销中...',
		mask: true
	})

	$H.post('user/deleteAccount', {}).then(res => {
		uni.hideLoading()
		if (res.code === 0) {
			uni.showToast({
				title: '账号注销成功',
				icon: 'success',
				duration: 2000
			})
			// 清除本地数据
			uni.removeStorageSync("hasLogin")
			uni.removeStorageSync("token")
			uni.removeStorageSync("userInfo")
			uni.removeStorageSync("activeStyle")
			// 关闭WebSocket连接
			uni.closeSocket()
			// 跳转到登录页
			setTimeout(() => {
				// 跳转到首页
				uni.switchTab({
					url: "/pages/index/index"
				})
			}, 2000)
		} else {
			uni.showToast({
				title: res.msg || '注销失败',
				icon: 'none'
			})
		}
	}).catch(err => {
		uni.hideLoading()
		uni.showToast({
			title: '注销失败，请稍后重试',
			icon: 'none'
		})
	})
}

// 生命周期
onMounted(() => {
	// 页面加载时开始倒计时
	startCountdown()
})

onUnmounted(() => {
	// 组件卸载时清除定时器
	if (timer) {
		clearInterval(timer)
		timer = null
	}
})
</script>

<style lang="scss" scoped>
.account-delete-page {
	min-height: 100vh;
	background-color: #ffffff;
	padding-bottom: 200rpx;
}

.content-container {
	padding: 60rpx 40rpx 40rpx;
}

.warning-icon {
	display: flex;
	justify-content: center;
	margin-bottom: 50rpx;
	margin-top: 20rpx;

	.icon-circle {
		width: 140rpx;
		height: 140rpx;
		border-radius: 50%;
		background-color: #ff4757;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 4rpx 12rpx rgba(255, 71, 87, 0.3);

		.icon-text {
			font-size: 100rpx;
			font-weight: bold;
			color: #ffffff;
			line-height: 1;
		}
	}
}

.title {
	font-size: 40rpx;
	font-weight: 600;
	color: #333333;
	text-align: center;
	margin-bottom: 40rpx;
}

.intro-text {
	font-size: 28rpx;
	color: #333333;
	line-height: 1.6;
	margin-bottom: 40rpx;
}

.consequences-list {
	.list-item {
		display: flex;
		margin-bottom: 28rpx;
		align-items: flex-start;
		padding-left: 8rpx;

		.bullet {
			font-size: 32rpx;
			color: #333333;
			margin-right: 20rpx;
			line-height: 1.5;
			flex-shrink: 0;
		}

		.item-text {
			flex: 1;
			font-size: 28rpx;
			color: #333333;
			line-height: 1.6;
		}
	}
}

.footer {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background-color: #ffffff;
	padding: 30rpx 40rpx;
	padding-bottom: calc(30rpx + env(safe-area-inset-bottom));
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.checkbox-container {
	display: flex;
	align-items: center;
	margin-bottom: 30rpx;

	.checkbox {
		width: 40rpx;
		height: 40rpx;
		border: 2rpx solid #dddddd;
		border-radius: 8rpx;
		margin-right: 16rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background-color: #ffffff;
		transition: all 0.3s;
		flex-shrink: 0;

		&.checked {
			background-color: #ff4757;
			border-color: #ff4757;

			.checkmark {
				color: #ffffff;
				font-size: 26rpx;
				font-weight: bold;
				line-height: 1;
			}
		}
	}

	.checkbox-text {
		font-size: 28rpx;
		color: #333333;
		line-height: 1.5;
	}
}

.next-button {
	width: 100%;
	height: 88rpx;
	background-color: #ff4757;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s;
	box-shadow: 0 4rpx 12rpx rgba(255, 71, 87, 0.3);

	&.disabled {
		background-color: #e5e5e5;
		box-shadow: none;
		opacity: 0.6;
		cursor: not-allowed;

		.button-text {
			color: #999999;
		}
	}

	&:active:not(.disabled) {
		opacity: 0.9;
		transform: scale(0.98);
	}

	.button-text {
		font-size: 32rpx;
		font-weight: 500;
		color: #ffffff;
	}
}
</style>
