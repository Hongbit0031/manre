<template>
	<view class="account-page">
		<view class="account-page__wrapper">
			<view class="account-page__header">
				<view class="account-page__account-main">
					<view class="account-page__account-info acea-row row-top row-between">
						<view class="account-page__assets">
							<view>总资产(元)</view>
							<view class="money">{{ now_money }}</view>
						</view>
						<view @click="navigateToCashOut" class="account-page__recharge-btn" v-if="canCash == '1'">提现
						</view>
						<view @click="navigateToPay" class="account-page__recharge-btn" v-if="is_hide == '0'">充值</view>
					</view>
					<view class="account-page__cumulative acea-row row-top">
						<view class="account-page__cumulative-item">
							<view>累计消费(元)</view>
							<view class="money">{{ orderStatusSum }}</view>
						</view>
					</view>
				</view>
			</view>
			<view class="account-page__nav acea-row row-middle">
				<view class="account-page__nav-item" @click="goUserBill(0)">
					<view class="icon">
						<image src="/static/record1.png" />
					</view>
					<view>账单记录</view>
				</view>
				<view class="account-page__nav-item" @click="goUserBill(1)">
					<view class="icon">
						<image src="/static/record2.png" />
					</view>
					<view>消费记录</view>
				</view>
				<view class="account-page__nav-item" @click="goUserBill(2)" v-if="is_hide == '0'">
					<view class="icon">
						<image src="/static/record3.png" />
					</view>
					<view>充值记录</view>
				</view>
			</view>

			<view class="account-page__advert acea-row row-between-wrapper"></view>
			<!-- 积分区 -->
			<view class="account-page__header">
				<view class="account-page__points-section">
					<view class="account-page__account-info acea-row row-top row-between">
						<view class="account-page__assets">
							<view>总积分</view>
							<view class="money">{{ allIntegral }}</view>
						</view>
						<view @click="show2 = true" class="account-page__recharge-btn" v-if="yuetoint == '0'">兑入
						</view>
						<view @click="show = true" class="account-page__recharge-btn" v-if="exchange == '0'">兑出
						</view>
					</view>
					<view class="account-page__cumulative acea-row row-top">
						<view class="account-page__cumulative-item">
							<view>累计使用</view>
							<view class="money">{{ consumer }}</view>
						</view>
					</view>
				</view>
			</view>
			<view class="account-page__nav acea-row row-middle">
				<view class="account-page__nav-item" @click="goUserIntegral(0)">
					<view class="icon">
						<image src="/static/record06.png" />
					</view>
					<view>积分记录</view>
				</view>
				<view class="account-page__nav-item" @click="goUserIntegral(1)">
					<view class="icon">
						<image src="/static/record07.png" />
					</view>
					<view>支出记录</view>
				</view>
				<view class="account-page__nav-item" @click="goUserIntegral(2)">
					<view class="icon">
						<image src="/static/record08.png" />
					</view>
					<view>收入记录</view>
				</view>
			</view>

			<!-- 积分兑换余额 兑换弹窗 -->
			<u-popup v-model="show" mode="center" border-radius="14">
				<view class="exchange-popup">
					<view class="exchange-popup__container flex-items flex-column">
						<view class="mar-top-60">
							<label class="exchange-popup__title">积分兑换余额</label>
						</view>
						<view class="flex-items flex-row mar-top-60">
							<view class="fs26">我的积分</view>
							<input class="exchange-popup__input mar-left-20 fs26" v-model="allIntegral"
								disabled="disabled" />
						</view>
						<view class="flex-items flex-row mar-top-30">
							<label class="fs26">可兑余额</label>
							<input class="exchange-popup__input mar-left-20 fs26" v-model="canRecharge"
								disabled="disabled" />
						</view>
						<view class="flex-items flex-row mar-top-30">
							<label class="fs26">兑换余额</label>
							<input class="exchange-popup__input mar-left-20 fs26" v-model="rechargeValue"
								type="number" />
						</view>
						<view class="flex-items flex-row mar-top-30">
							<label class="exchange-popup__tip">{{integral}}积分可兑换1元</label>
						</view>

						<view class="exchange-popup__btn-group">
							<view class="exchange-popup__btn exchange-popup__btn--cancel" @click="cancelSubmit">取消
							</view>
							<view class="exchange-popup__btn exchange-popup__btn--confirm" @click="submit">确定</view>
						</view>
					</view>
				</view>
			</u-popup>
			<!-- 余额兑换积分 兑换弹窗 -->
			<u-popup v-model="show2" mode="center" border-radius="14">
				<view class="exchange-popup">
					<view class="exchange-popup__container flex-items flex-column">
						<view class="mar-top-60">
							<label class="exchange-popup__title">余额兑换积分</label>
						</view>
						<view class="flex-items flex-row mar-top-60">
							<view class="fs26">我的余额</view>
							<input class="exchange-popup__input mar-left-20 fs26" v-model="now_money"
								disabled="disabled" />
						</view>
						<view class="flex-items flex-row mar-top-30">
							<label class="fs26">可兑积分</label>
							<input class="exchange-popup__input mar-left-20 fs26" v-model="canRechargeIntegral"
								disabled="disabled" />
						</view>
						<view class="flex-items flex-row mar-top-30">
							<label class="fs26">兑换积分</label>
							<input class="exchange-popup__input mar-left-20 fs26" v-model="rechargeInt" type="number" />
						</view>
						<view class="flex-items flex-row mar-top-30">
							<label class="exchange-popup__tip">1元可兑换{{ratio}}积分</label>
						</view>

						<view class="exchange-popup__btn-group">
							<view class="exchange-popup__btn exchange-popup__btn--cancel" @click="cancelSubmit">取消
							</view>
							<view class="exchange-popup__btn exchange-popup__btn--confirm" @click="submit2">确定</view>
						</view>
					</view>
				</view>
			</u-popup>
		</view>

	</view>


</template>
<script setup>
	import {
		ref,
		getCurrentInstance
	} from 'vue'
	import {
		onShow
	} from '@dcloudio/uni-app'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H
	const $u = proxy.$u
	const accountBgImage = `url('${proxy.$c.imgResource}/images/white-bg.png')`

	// Reactive data
	const is_hide = ref('0')
	const exchange = ref('0')
	const now_money = ref(0)
	const orderStatusSum = ref(0)
	const recharge = ref(0)
	const show = ref(false)
	const show2 = ref(false)
	const integral = ref(0)
	const allIntegral = ref(0)
	const consumer = ref(0)
	const canRecharge = ref(0)
	const rechargeValue = ref('')
	const canCash = ref('0')
	const yuetoint = ref(0)
	const ratio = ref(0)
	const canRechargeIntegral = ref(0)
	const rechargeInt = ref('')

	// Methods
	const goUserBill = (types) => {
		uni.navigateTo({
			url: '/pages/bill/bill?types=' + types
		})
	}

	const goUserIntegral = (type) => {
		uni.navigateTo({
			url: '/pages/sign/integral?type=' + type
		})
	}

	const getIndex = () => {
		$H.post('user/bill').then(
			res => {
				now_money.value = res.result.nowMoney
				orderStatusSum.value = res.result.orderStatusSum
				is_hide.value = res.result.isHide
				exchange.value = res.result.exchange
				consumer.value = res.result.consumer
				integral.value = res.result.integral
				allIntegral.value = res.result.allIntegral
				canRecharge.value = res.result.allIntegral / res.result.integral
				canCash.value = res.result.canCash
				ratio.value = res.result.ratio
				yuetoint.value = res.result.yuetoint
				canRechargeIntegral.value = res.result.ratio * res.result.nowMoney
			},
			err => {
				uni.showToast({
					title: err.msg,
					icon: 'none',
					duration: 2000,
				})
			}
		)
	}

	const cancelSubmit = () => {
		show.value = false
		show2.value = false
	}

	const isInteger = (obj) => {
		return obj % 1 === 0
	}

	const navigateToCashOut = () => {
		uni.navigateTo({
			url: '/pages/account/cash-out'
		})
	}

	const navigateToPay = () => {
		uni.navigateTo({
			url: '/pages/pay/pay'
		})
	}

	// 积分兑换余额
	const submit = () => {
		if (canRecharge.value <= 0) {
			$u.toast('可兑换金额不足')
		}
		if (!rechargeValue.value) {
			$u.toast('请输入兑换金额')
			return
		}
		const ret = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/
		if (!ret.test(rechargeValue.value)) {
			$u.toast('输入金额不合法')
			return
		}
		if (rechargeValue.value > canRecharge.value) {
			$u.toast('不能高于可兑换金额')
			return
		}
		uni.showLoading({
			mask: true,
			title: '兑换中'
		})
		$H.post('bill/exchange', {
			rechargeValue: rechargeValue.value
		}).then(res => {
			if (res.code == 0) {
				uni.redirectTo({
					url: '/pages/bill/bill?types=0'
				})
			}
			uni.hideLoading()
		})
	}

	// 余额兑换积分
	const submit2 = () => {
		if (canRechargeIntegral.value <= 0) {
			$u.toast('可兑换积分不足')
		}
		if (!rechargeInt.value) {
			$u.toast('请输入兑换金额')
			return
		}
		const ret = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/
		if (!ret.test(rechargeInt.value)) {
			$u.toast('输入额度不合法')
			return
		}
		if (!isInteger(rechargeInt.value)) {
			$u.toast('积分必须为整数')
			return
		}
		if (rechargeInt.value > canRechargeIntegral.value) {
			$u.toast('不能高于可兑换额度')
			return
		}

		uni.showLoading({
			mask: true,
			title: '兑换中'
		})
		$H.post('bill/exchangeToIntegral', {
			rechargeValue: rechargeInt.value
		}).then(res => {
			if (res.code == 0) {
				uni.redirectTo({
					url: '/pages/bill/bill?types=0'
				})
			}
			uni.hideLoading()
		})
	}

	// Lifecycle hooks
	onShow(() => {
		getIndex()
	})
</script>
<style lang="scss" scoped>
	.account-page {
		&__wrapper {
			background-color: #fff;
			padding: 32rpx 0 34rpx 0;
			margin-bottom: 14rpx;
		}

		&__header {
			width: 690rpx;
			height: 330rpx;
			background-image: linear-gradient(to right, #7e97f2 0%, #aaaaff 100%);
			border-radius: 16rpx;
			margin: 0 auto;
			color: rgba(255, 255, 255, 0.6);
			font-size: 24rpx;
		}

		&__account-main {
			background-image: v-bind(accountBgImage);
			background-repeat: no-repeat;
			background-size: 100%;
			height: 100%;
			width: 100%;
			padding: 36rpx 0 29rpx 0;
		}

		&__account-info {
			padding: 0 35rpx;
		}

		&__assets {
			.money {
				font-size: 72rpx;
				color: #fff;
				font-family: 'GuildfordProBook 5';
				margin-top: 10rpx;
				height: 75rpx;
				line-height: 75rpx;
			}
		}

		&__recharge-btn {
			font-size: 28rpx;
			width: 150rpx;
			height: 54rpx;
			border-radius: 27rpx;
			background-color: #fff9f8;
			text-align: center;
			line-height: 54rpx;
			color: #aaaaff !important;
		}

		&__cumulative {
			margin-top: 46rpx;
		}

		&__cumulative-item {
			flex: 1;
			padding-left: 35rpx;

			.money {
				font-size: 48rpx;
				font-family: 'GuildfordProBook 5';
				color: #fff;
				margin-top: 6rpx;
			}
		}

		&__points-section {
			width: 690rpx;
			height: 330rpx;
			background-image: linear-gradient(to right, #b77cff 0%, #aaaaff 100%);
			border-radius: 16rpx;
			margin: 0 auto;
			color: rgba(255, 255, 255, 0.6);
			font-size: 24rpx;
			background-repeat: no-repeat;
			background-size: 100%;
			height: 100%;
			width: 100%;
			padding: 36rpx 0 29rpx 0;
		}

		&__nav {
			height: 155rpx;
			border-bottom: 1px solid #f5f5f5;
		}

		&__nav-item {
			flex: 1;
			text-align: center;
			font-size: 26rpx;
			color: #999;

			.icon {
				width: 44rpx;
				height: 44rpx;
				margin: 0 auto;
				margin-bottom: 20rpx;

				image {
					width: 100%;
					height: 100%;
				}
			}
		}

		&__advert {
			padding: 0 30rpx;
			margin-top: 30rpx;
		}
	}

	// 弹窗样式
	.exchange-popup {
		&__container {
			width: 520rpx;
			height: 550rpx;
		}

		&__input {
			border: 1rpx solid #DDDDDD;
			width: 300rpx;
			height: 48rpx;
			padding-left: 16rpx;
		}

		&__disabled-input {
			background: #EEEEEE;
			color: #999999;
		}

		&__title {
			font-size: 36rpx;
			font-weight: 200;
		}

		&__tip {
			color: #888283;
			font-size: 22rpx;
		}

		&__btn-group {
			position: absolute;
			bottom: 0;
			display: flex;
		}

		&__btn {
			width: 260rpx;
			height: 90rpx;
			text-align: center;
			line-height: 90rpx;

			&--cancel {
				border: 1rpx solid #E5E5E5;
			}

			&--confirm {
				background-color: #323232;
				color: #FFFFFF;
			}
		}
	}
</style>
