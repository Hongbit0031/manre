<template>
	<view v-if="open" class="payment-container">
		<view class="payment-header">
			<view class="balance-container">
				<text class="balance-title">我的余额</text>
				<view class="balance-amount">
					<text class="currency">￥</text>
					<text class="amount">{{ now_money || 0 }}</text>
				</view>
			</view>
			<view class="header-decoration"></view>
		</view>
		<view class="payment-content">
			<view class="section-title">选择充值金额</view>
			
			<view class="payment-options">
				<view 
					class="option-card" 
					:class="{'option-card--active': activePic === index}" 
					v-for="(item, index) in picList" 
					:key="index" 
					@click="picCharge(index, item)">
					
					<view class="option-price">
						<text class="currency-symbol">￥</text>
						<text class="price-value">{{ item.price }}</text>
					</view>
					
					<view class="option-bonus" v-if="item.givePrice > 0">
						<view class="bonus-tag">赠送</view>
						<text class="bonus-amount">￥{{ item.givePrice }}</text>
					</view>
				</view>
			</view>

			<button class="payment-button" @click="recharge">
				<text>立即充值</text>
			</button>

			<view class="payment-notice">
				<view class="notice-icon">!</view>
				<text class="notice-text">充值后不支持退款</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import wxMpUtil from "@/utils/wxMpUtil.js"
import config from '@/utils/config.js'

// 获取当前实例
const { proxy } = getCurrentInstance()

// 响应式数据
const active = ref(0)
const from = ref('')
const money = ref("")
const now_money = ref("")
const picList = ref([])
const activePic = ref(0)
const numberPic = ref("")
const paid_price = ref("")
const rechar_id = ref(0)
const fromPaidPost = ref(false)
const open = ref(true)
const userInfo = ref(null)

// 方法
const checkOpen = () => {
	proxy.$H.get('system/rechargeIsOpen').then(res => {
		if(res.code==0){
			open.value = res.result
			if(!res.result){
				proxy.$f.toast("模块未开启")
				setTimeout(function() {
					uni.switchTab({
						url: '/pages/index/index'
					})
				}, 1500)
			}
		}
	})
}

const getUserInfo = () => {
	proxy.$H.get('user/userInfo').then(res => {
		userInfo.value = res.result
		now_money.value = userInfo.value.money
	})
}

/**
 * 充值额度选择
 */
const getRecharge = () => {
	proxy.$H.get('user/recharge/list').then(res => {
		picList.value = res.result
		if (picList.value[0]) {
			rechar_id.value = picList.value[0].id
			paid_price.value = picList.value[0].price
			numberPic.value = picList.value[0].givePrice
		}
	})
}

/**
 * 选择金额
 */
const picCharge = (idx, item) => {
	activePic.value = idx
	if (idx == picList.value.length) {
		rechar_id.value = 0
		paid_price.value = ""
		numberPic.value = ""
	} else {
		money.value = ""
		rechar_id.value = item.id
		paid_price.value = item.price
		numberPic.value = item.givePrice
	}
}

const recharge = () => {
	let price = Number(money.value)
	if (picList.value.length == activePic.value && price === 0) {
		uni.showToast({
			title: "请输入您要充值的金额",
			icon: "none",
			duration: 2000,
		})
		return
	} else if (picList.value.length == activePic.value && price < 0.01) {
		uni.showToast({
			title: "充值金额不能低于0.01",
			icon: "none",
			duration: 2000,
		})
		return
	} else if (picList.value.length == activePic.value && price > 99999) {
		uni.showToast({
			title: "充值金额不能大于99999",
			icon: "none",
			duration: 2000,
		})
		return
	}
	
	let prices = ""
	let paidPrice = ""
	if (price) {
		prices = price
		paidPrice = 0
	} else {
		prices = numberPic.value
		paidPrice = paid_price.value
	}
	
	uni.showLoading({
		mask: true,
		title: '支付中...',
	})
	
	// #ifdef MP-WEIXIN
	proxy.$H.post('user/recharge', {
			price: paidPrice,
			from: 'weixin',
			paidPrice: prices,
			recharId: rechar_id.value,
		})
		.then((res) => {
			uni.hideLoading()

			uni.requestPayment({
				provider: 'wxpay',
				timeStamp: res.data.timeStamp,
				nonceStr: res.data.nonceStr,
				package: res.data.package,
				signType: res.data.signType,
				paySign: res.data.paySign,
				success: function(payRes) {
					uni.showToast({
						icon: 'none',
						title: '支付成功'
					})
					if (fromPaidPost.value) {
						uni.navigateBack()
					} else {
						uni.navigateTo({
							url: '/pages/account/account'
						})
					}
				},
				fail: function(err) {
					uni.showToast({
						icon: 'none',
						title: '支付取消'
					})
					uni.navigateTo({
						url: '/pages/account/account'
					})
				}
			})
		})
		.catch((err) => {
			console.log(err)
			uni.showToast({
				title: err.msg,
				icon: "none",
				duration: 2000,
			})
		})
	// #endif
	
	// #ifdef APP-PLUS
	proxy.$H.post('user/rechargeByApp', {
			price: paidPrice,
			from: 'app',
			paidPrice: prices,
			recharId: rechar_id.value,
		})
		.then((res) => {
			uni.hideLoading()
			let str = res.data.package
			var index = str.lastIndexOf("\=")
			str = str.substring(index + 1, str.length)
			var obj = {
				appid: res.data.appId,
				noncestr: res.data.nonceStr,
				package: 'Sign=WXPay',
				prepayid: str,
				timestamp: res.data.timeStamp,
				sign: res.data.paySign,
				partnerid: res.data.partnerId
			}
			uni.requestPayment({
				provider: 'wxpay',
				orderInfo: obj,
				success: function(payRes) {
					uni.showToast({
						icon: 'none',
						title: '支付成功'
					})
					if (fromPaidPost.value) {
						uni.navigateBack()
					} else {
						uni.navigateTo({
							url: '/pages/account/account'
						})
					}
				},
				fail: function(err) {
					console.log(err)
					uni.showToast({
						icon: 'none',
						title: '支付取消'
					})
				}
			})

		})
		.catch((err) => {
			console.log(err)
			uni.showToast({
				title: err.msg,
				icon: "none",
				duration: 2000,
			})
		})
	// #endif
	
	// #ifdef H5
	let ua = navigator.userAgent.toLowerCase()
	if (ua.match(/MicroMessenger/i) == "micromessenger") {
		proxy.$H.post('user/rechargeByWXH5', {
				price: paidPrice,
				from: 'weixin',
				paidPrice: prices,
				recharId: rechar_id.value,
			})
			.then((res) => {
				uni.hideLoading()
				WeixinJSBridge.invoke(
					'getBrandWCPayRequest', {
						appId: res.data.appId,
						timeStamp: res.data.timeStamp,
						nonceStr: res.data.nonceStr,
						package: res.data.package,
						signType: res.data.signType,
						paySign: res.data.paySign
					},
					function(res) {
						if (res.err_msg == "get_brand_wcpay_request:ok") {
							uni.showToast({
								icon: 'none',
								title: '支付成功'
							})
							if (fromPaidPost.value) {
								uni.navigateBack()
							} else {
								uni.navigateTo({
									url: '/pages/account/account'
								})
							}
						} else if (res.err_msg == "get_brand_wcpay_request:cancel") {
							uni.showToast({
								icon: 'none',
								title: '支付取消'
							})
						} else {
							uni.showToast({
								icon: 'none',
								title: '支付失败'
							})
						}
					})

			}).catch(err => {
				uni.hideLoading()
				uni.showToast({
					title: '支付失败',
					icon: 'none'
				})
				uni.navigateTo({
					url: '/pages/account/account'
				})
			})
	} else {
		proxy.$H.post('user/rechargeByH5', {
				price: paidPrice,
				from: 'weixin',
				paidPrice: prices,
				recharId: rechar_id.value,
			})
			.then((res) => {
				uni.hideLoading()
				console.log("h5:" + res.data)
				location.replace(res.data.mwebUrl)
			}).catch(err => {
				uni.hideLoading()
				uni.showToast({
					title: '支付失败',
					icon: 'none'
				})
				uni.navigateTo({
					url: '/pages/account/account'
				})
			})
	}
	// #endif
}

// 生命周期
onMounted(() => {
	checkOpen()
	getRecharge()
	getUserInfo()
})

onLoad((options) => {
	// #ifdef H5
	if(config.wxh5Login){
		wxMpUtil.checkAndSaveWxMpOpenid(options.code, "/pages/pay/pay")
		let ua = navigator.userAgent.toLowerCase()
		if (ua.match(/MicroMessenger/i) == "micromessenger") {
			if (typeof WeixinJSBridge == "undefined") {
				if (document.addEventListener) {
					document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false)
				} else if (document.attachEvent) {
					document.attachEvent('WeixinJSBridgeReady', onBridgeReady)
					document.attachEvent('onWeixinJSBridgeReady', onBridgeReady)
				}
			}
		}
	}
	// #endif	
	if (options.from) {
		if (options.from == 'paidpost') {
			fromPaidPost.value = true
		}
	}
})
</script>

<style scoped lang="scss">
	// Variables
	$primary-color: #5662f6;
	$secondary-color: #7580ff;
	$accent-color: #ffd166;
	$blue-gradient: linear-gradient(135deg, #a1c4fd, #aaaaff);
	$dark-text: #2d3748;
	$light-text: #ffffff;
	$border-radius: 16rpx;
	$card-bg: #ffffff;
	
	page {
		background-color: #f8fafc;
	}
	
	.payment-container {
		min-height: 100vh;
		background-color: #f8fafc;
	}
	
	// Header Section
	.payment-header {
		position: relative;
		width: 100%;
		height: 380rpx;
		background: linear-gradient(135deg, #a1c4fd, #aaaaff);
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
	
	.balance-container {
		position: relative;
		z-index: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
	}
	
	.balance-title {
		font-size: 30rpx;
		color: rgba(255, 255, 255, 0.9);
		margin-bottom: 20rpx;
		position: relative;
		
		&::after {
			content: '';
			position: absolute;
			bottom: -10rpx;
			left: 50%;
			transform: translateX(-50%);
			width: 60rpx;
			height: 2rpx;
			background: rgba(255, 255, 255, 0.5);
		}
	}
	
	.balance-amount {
		display: flex;
		align-items: flex-start;
		justify-content: center;
		margin-top: 10rpx;
		
		.currency {
			font-size: 40rpx;
			color: #fff;
			margin-top: 15rpx;
			margin-right: 8rpx;
			font-weight: 600;
		}
		
		.amount {
			font-size: 90rpx;
			color: $light-text;
			font-weight: 700;
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
		background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
		opacity: 0.2;
	}
	
	// Content Section
	.payment-content {
		width: calc(100% - 60rpx);
		margin: -50rpx auto 0;
		padding: 40rpx 30rpx 50rpx;
		background-color: $card-bg;
		border-radius: $border-radius;
		box-shadow: 0 4rpx 30rpx rgba(0, 0, 0, 0.05);
		position: relative;
		z-index: 10;
		border: 1px solid rgba(237, 242, 247, 0.8);
	}
	
	.section-title {
		font-size: 32rpx;
		color: $dark-text;
		margin-bottom: 40rpx;
		position: relative;
		padding-left: 20rpx;
		font-weight: bold;
		
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
	
	// Payment Options
	.payment-options {
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;
		margin-bottom: 40rpx;
	}
	
	.option-card {
		width: 31%;
		padding: 24rpx 0;
		background-color: #f9fafc;
		border-radius: $border-radius;
		margin-bottom: 20rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		position: relative;
		transition: all 0.3s ease;
		border: 1px solid rgba(237, 242, 247, 0.8);
		
		&--active {
			background-color: rgba($primary-color, 0.05);
			border-color: $primary-color;
			transform: translateY(-4rpx);
			box-shadow: 0 8rpx 16rpx rgba(0, 0, 0, 0.05), 0 0 0 1px rgba($primary-color, 0.2);
			
			.currency-symbol, .price-value {
				color: $primary-color;
			}
		}
	}
	
	.option-price {
		display: flex;
		align-items: baseline;
		margin-bottom: 10rpx;
		
		.currency-symbol {
			font-size: 26rpx;
			color: #64748b;
			margin-right: 2rpx;
		}
		
		.price-value {
			font-size: 40rpx;
			color: #64748b;
			font-weight: 600;
		}
	}
	
	.option-bonus {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-top: 4rpx;
		
		.bonus-tag {
			font-size: 20rpx;
			color: $accent-color;
			background-color: rgba($accent-color, 0.1);
			padding: 2rpx 10rpx;
			border-radius: 10rpx;
			margin-bottom: 4rpx;
		}
		
		.bonus-amount {
			font-size: 22rpx;
			color: #64748b;
		}
	}
	
	// Button
	.payment-button {
		width: 100%;
		height: 88rpx;
		line-height: 88rpx;
		text-align: center;
		background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
		color: $light-text;
		font-size: 32rpx;
		font-weight: 600;
		border-radius: 44rpx;
		margin-top: 20rpx;
		box-shadow: 0 8rpx 20rpx rgba($primary-color, 0.15);
		transition: all 0.3s;
		position: relative;
		overflow: hidden;
		
		&::before {
			content: '';
			position: absolute;
			top: -10rpx;
			left: -10rpx;
			right: -10rpx;
			bottom: -10rpx;
			background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
			opacity: 0.5;
			filter: blur(10px);
			z-index: -1;
		}
		
		&:active {
			transform: scale(0.98);
			box-shadow: 0 4rpx 10rpx rgba($primary-color, 0.1);
		}
	}
	
	// Notice
	.payment-notice {
		display: flex;
		align-items: center;
		justify-content: center;
		margin-top: 30rpx;
		
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
			color: #64748b;
		}
	}
</style>