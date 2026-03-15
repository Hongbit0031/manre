<template>
	<view class="cashout-container" v-if="open">
		<view class="cashout-header">
			<view class="header-content">
				<text class="header-title">提现申请</text>
				<view class="header-balance">
					<text class="balance-label">当前可提现余额</text>
					<view class="balance-amount">
						<text class="currency">¥</text>
						<text class="amount">{{canCashOut}}</text>
					</view>
				</view>
			</view>
			<view class="header-decoration"></view>
		</view>

		<view class="cashout-content">
			<view class="section-title">提现金额</view>

			<view class="amount-input-wrapper">
				<view class="amount-input-container">
					<text class="currency-symbol">¥</text>
					<input type="number" v-model="moneyNumber" placeholder="输入提现金额"
						placeholder-class="input-placeholder" class="amount-input" />
				</view>
				<view class="input-hint" :class="{'input-error': canCashOut <= 0}">
					{{ canCashOut <= 0 ? '暂无可提现余额' : '当前可提现余额为 ¥' + canCashOut }}
				</view>
				<view class="input-error-message" v-if="!canSubmit">
					存在待审核提现申请，请耐心等待
				</view>
			</view>

			<view class="section-title payment-title">选择提现至</view>

			<view class="payment-methods">
				<radio-group @change="radioChange">
					<view class="payment-option" :class="{'payment-option--active': index === current}"
						v-for="(item, index) in items" :key="index">
						<view class="payment-info">
							<image :src="item.icon" class="payment-icon"></image>
							<text class="payment-name">{{item.title}}</text>
						</view>
						<radio :value="item.title" color="#5662f6" :checked="index === current" />
					</view>
				</radio-group>
			</view>

			<view class="divider"></view>

			<view class="section-title upload-title">收款码截图</view>
			<view class="upload-container">
				<u-upload ref="uUploadRef" :size-type="['original']" name="Image" :max-count="1" :header="header"
					:action="uploadImgUrl" @on-uploaded="submit" :auto-upload="false" :width="200" :height="200"
					upload-text="上传收款码">
				</u-upload>
			</view>

			<view class="divider"></view>

			<view class="submit-section">
				<button class="submit-button"
					:class="{'submit-button--active': canSubmit, 'submit-button--disabled': !canSubmit}"
					@click="uploadImg">
					{{ canSubmit ? '申请提现' : '无法申请提现' }}
				</button>

				<view class="submit-notice" v-if="canSubmit">
					<view class="notice-icon">!</view>
					<text class="notice-text">提现申请将在1-3个工作日内审核</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad
	} from '@dcloudio/uni-app'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H
	const $c = proxy.$c
	const $f = proxy.$f
	const $u = proxy.$u

	// Reactive data
	const moneyNumber = ref('')
	const canCashOut = ref(0)
	const canSubmit = ref(true)
	const items = ref([{
			id: 0,
			icon: 'https://demo.linfeng.tech/resource/img/alipay.png',
			title: '支付宝',
		},
		{
			id: 1,
			icon: 'https://demo.linfeng.tech/resource/img/weixin.png',
			title: '微信',
		}
	])
	const current = ref(0)
	const uploadImgUrl = $c.domain + 'common/upload'
	const header = {
		token: uni.getStorageSync('token')
	}
	const open = ref(true)
	const uUploadRef = ref(null)

	// Methods
	const checkOpen = () => {
		$H.get('system/cashOutIsOpen').then(res => {
			if (res.code == 0) {
				open.value = res.result
				if (!res.result) {
					$f.toast("模块未开启")
					setTimeout(() => {
						uni.switchTab({
							url: '/pages/index/index'
						})
					}, 1500)
				}
			}
		})
	}

	const uploadImg = () => {
		if (!moneyNumber.value) {
			$u.toast('请输入提现金额')
			return
		}
		if (moneyNumber.value <= 0) {
			$u.toast('输入金额非法')
			return
		}
		if (canCashOut.value <= 0) {
			$u.toast('暂无可提现余额')
			return
		}
		if (moneyNumber.value > canCashOut.value) {
			$u.toast('可提现余额不足')
			return
		}

		uni.showLoading({
			mask: true,
			title: '提交中'
		})
		uUploadRef.value.upload()
	}

	const radioChange = (evt) => {
		for (let i = 0; i < items.value.length; i++) {
			if (items.value[i].title === evt.detail.value) {
				current.value = i
				break
			}
		}
	}

	const getBasicInfo = () => {
		$H.get('cashOut/getAccountBasicInfo').then(res => {
			if (res.code == 0) {
				canCashOut.value = res.result.nowMoney
				canSubmit.value = res.result.canSubmit
				if (!res.result.cashOpen) {
					$u.toast('提现已关闭')
					setTimeout(() => {
						uni.switchTab({
							url: '/pages/my/my'
						})
					}, 1000)
				}
			}
		})
	}

	const submit = (e) => {
		if (e.length == 0) {
			uni.hideLoading()
			$u.toast('付款码截图不能为空')
			return
		}
		uni.showLoading({
			mask: true,
			title: '提交中'
		})
		$H.post('cashOut/submit', {
			url: e[0].response.result,
			moneyNumber: moneyNumber.value,
			type: current.value
		}).then(res => {
			if (res.code == 0) {
				$u.toast('提交成功,请耐心等待审核')
				setTimeout(() => {
					uni.switchTab({
						url: '/pages/my/my'
					})
				}, 2500)
			}
			uni.hideLoading()
		})
	}

	// Lifecycle hooks
	onLoad(() => {
		checkOpen()
		getBasicInfo()
	})
</script>

<style lang="scss" scoped>
	$primary-color: #5662f6;
	$secondary-color: #7580ff;
	$accent-color: #ffd166;
	$error-color: #e04646;
	$dark-text: #2d3748;
	$light-text: #ffffff;
	$border-radius: 16rpx;
	$card-bg: #ffffff;
	$blue-gradient: linear-gradient(135deg, #a1c4fd, #aaaaff);

	page {
		background-color: #f8fafc;
	}

	.cashout-container {
		min-height: 100vh;
		background-color: #f8fafc;
	}


	.cashout-header {
		position: relative;
		width: 100%;
		height: 320rpx;
		background: $blue-gradient;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		overflow: hidden;

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
	}

	.header-content {
		position: relative;
		z-index: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.header-title {
		font-size: 36rpx;
		font-weight: 600;
		color: $light-text;
		margin-bottom: 30rpx;
	}

	.header-balance {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.balance-label {
		font-size: 26rpx;
		color: rgba(255, 255, 255, 0.9);
		margin-bottom: 10rpx;
	}

	.balance-amount {
		display: flex;
		align-items: baseline;

		.currency {
			font-size: 32rpx;
			font-weight: 500;
			color: $light-text;
			margin-right: 8rpx;
		}

		.amount {
			font-size: 64rpx;
			font-weight: 700;
			color: $light-text;
			line-height: 1;
			text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
		}
	}

	.header-decoration {
		position: absolute;
		bottom: 0;
		left: 0;
		right: 0;
		height: 20rpx;
		background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
		opacity: 0.2;
	}


	.cashout-content {
		width: calc(100% - 60rpx);
		margin: -50rpx auto 0;
		padding: 40rpx 30rpx 50rpx;
		background-color: $card-bg;
		border-radius: $border-radius;
		box-shadow: 0 4rpx 30rpx rgba(0, 0, 0, 0.05);
		position: relative;
		z-index: 10;
	}

	.section-title {
		font-size: 30rpx;
		color: $dark-text;
		font-weight: 600;
		margin-bottom: 20rpx;
		position: relative;
		padding-left: 20rpx;

		&::before {
			content: '';
			position: absolute;
			left: 0;
			top: 50%;
			transform: translateY(-50%);
			width: 6rpx;
			height: 28rpx;
			background: $primary-color;
			border-radius: 3rpx;
		}
	}

	.payment-title {
		margin-top: 40rpx;
	}

	.amount-input-wrapper {
		margin-bottom: 30rpx;
	}

	.amount-input-container {
		display: flex;
		align-items: center;
		height: 100rpx;
		padding: 0 20rpx;
		border-radius: $border-radius;
		background-color: #f9fafc;
		border: 1px solid rgba(237, 242, 247, 0.8);
		margin-bottom: 15rpx;

		.currency-symbol {
			font-size: 40rpx;
			font-weight: 500;
			color: $dark-text;
			margin-right: 10rpx;
		}
	}

	.amount-input {
		flex: 1;
		height: 90rpx;
		font-size: 40rpx;
		color: $dark-text;
		font-weight: 600;
	}

	.input-placeholder {
		color: #a0aec0;
		font-weight: normal;
	}

	.input-hint {
		font-size: 24rpx;
		color: #a0aec0;
		padding-left: 20rpx;
	}

	.input-error {
		color: $error-color;
	}

	.input-error-message {
		font-size: 24rpx;
		color: $error-color;
		margin-top: 10rpx;
		padding-left: 20rpx;
	}

	.payment-methods {
		margin-bottom: 20rpx;
	}

	.payment-option {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 25rpx 20rpx;
		border-radius: $border-radius;
		background-color: #f9fafc;
		margin-bottom: 15rpx;
		border: 1px solid rgba(237, 242, 247, 0.8);
		transition: all 0.3s ease;

		&--active {
			background-color: rgba($primary-color, 0.05);
			border-color: $primary-color;
			box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.05);
		}
	}

	.payment-info {
		display: flex;
		align-items: center;
	}

	.payment-icon {
		width: 60rpx;
		height: 60rpx;
		margin-right: 20rpx;
	}

	.payment-name {
		font-size: 30rpx;
		color: $dark-text;
		font-weight: 500;
	}

	.divider {
		height: 1px;
		background-color: #edf2f7;
		margin: 30rpx 0;
	}


	.upload-title {
		margin-bottom: 30rpx;
	}

	.upload-container {
		padding: 10rpx;
	}

	.upload-button {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		width: 220rpx;
		height: 220rpx;
		background-color: #f9fafc;
		border: 1px dashed rgba($primary-color, 0.5);
		border-radius: 12rpx;
	}

	.upload-text {
		margin-top: 15rpx;
		font-size: 24rpx;
		color: $primary-color;
	}


	.submit-section {
		margin-top: 60rpx;
	}

	.submit-button {
		width: 100%;
		height: 88rpx;
		line-height: 88rpx;
		text-align: center;
		font-size: 30rpx;
		font-weight: 600;
		border-radius: 44rpx;
		margin-bottom: 20rpx;

		&--active {
			background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
			color: $light-text;
			box-shadow: 0 6rpx 20rpx rgba($primary-color, 0.2);

			&:active {
				transform: scale(0.98);
				box-shadow: 0 4rpx 10rpx rgba($primary-color, 0.1);
			}
		}

		&--disabled {
			background: #a0aec0;
			color: $light-text;
		}
	}

	.submit-notice {
		display: flex;
		align-items: center;
		justify-content: center;
		margin-top: 20rpx;

		.notice-icon {
			width: 30rpx;
			height: 30rpx;
			line-height: 30rpx;
			text-align: center;
			border-radius: 50%;
			background-color: rgba($primary-color, 0.1);
			color: $primary-color;
			font-size: 20rpx;
			font-weight: bold;
			margin-right: 10rpx;
		}

		.notice-text {
			font-size: 24rpx;
			color: #718096;
		}
	}
</style>