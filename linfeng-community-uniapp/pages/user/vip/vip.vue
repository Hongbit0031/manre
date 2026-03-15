<template>
	<view class="vip-container">
		<!-- 头部会员信息 -->
		<view class="vip-header">
			<view class="user-profile">
				<image mode="aspectFill" class="avatar" :src="userInfo.avatar"></image>
				<view class="user-info" v-if="userInfo.vip==1">
					<view class="username">{{userInfo.username}} <text class="vip-badge">VIP</text></view>
					<view class="expiry-date">会员有效期至：{{userInfo.vipExpireTime}}</view>
				</view>
				<view class="user-info" v-else>
					<view class="username">{{userInfo.username}}</view>
					<view class="non-vip">您还不是VIP会员</view>
				</view>
			</view>
		</view>

		<!-- 会员充值选项 -->
		<view class="pricing-section">
			<view class="section-title">会员套餐</view>
			<view class="pricing-options">
				<view class="pricing-card" 
					v-for="(item,index) in vipList" 
					:key="index"
					:class="{'pricing-card--active': activeIndex === index}"
					@click="activeIndex = index">
					<view class="discount-tag" v-if="item.remark.includes('优惠')">限时优惠</view>
					<view class="pricing-name">{{item.name}}</view>
					<view class="pricing-price">
						<text class="currency">¥</text>
						<text class="amount">{{item.price}}</text>
					</view>
					<view class="pricing-remark">{{item.remark}}</view>
				</view>
			</view>
			
			<view class="agreement">
				<text>查看开通会员协议</text>
				<view @click="jumpToProtocol" class="agreement-link">《会员服务协议》</view>
			</view>

			<button class="action-button" @click="selectPay">
				<text>{{ userInfo.vip==0 ? '立即开通' : '立即续费' }}</text>
			</button>
		</view>

		<!-- 会员权益 -->
		<view class="benefits-section">
			<view class="section-title">会员专属权益</view>
			<view class="benefits-grid">
				<view class="benefit-card" v-for="item in benefitList" :key="item.id">
					<image mode="aspectFill" class="benefit-icon" :src="item.icon"></image>
					<view class="benefit-content">
						<text class="benefit-title">{{item.title}}</text>
						<text class="benefit-desc">{{item.describes}}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 支付选择弹框 -->
		<PayDialogComponent 
			:money="money" 
			:show="payVisible" 
			@cancel="cancelPayHandle" 
			@success="successPayHandle">
		</PayDialogComponent>

	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import PayDialogComponent from "@/components/payDialog/payDialog.vue"
import wxMpUtil from "@/utils/wxMpUtil.js"
import config from '@/utils/config.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $u = proxy.$u
const $IMG = ref(proxy.$IMG)

const payVisible = ref(false)
const userInfo = ref({})
const activeIndex = ref(0)
const vipList = ref([])
const orderId = ref(0)
const renameCount = ref(0)
const integralNum = ref(0)
const topicNum = ref(0)
const type = ref("")
const money = ref('')
const benefitList = ref([])

onLoad((options) => {
	getIsOpen()
	getUserInfo()
	getVipList()
	getVipConfig()
	getVipBenefitList()
	
	// #ifdef H5
	if (config.wxh5Login) {
		wxMpUtil.checkAndSaveWxMpOpenid(options.code, "/pages/user/vip/vip")
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
})

function jumpToProtocol() {
	uni.navigateTo({
		url: "/pages/user/protocol?type=app_vip_recharge_context"
	})
}

function getVipBenefitList() {
	$H.get("vipbenefit/getList").then(res => {
		benefitList.value = res.result
	})
}

function getIsOpen() {
	$H.get('system/vipShow').then(res => {
		if (res.vipShow == '1') {
			$u.toast("会员模块未开启")
			setTimeout(function() {
				uni.switchTab({
					url: '/pages/index/index'
				})
			}, 1000)
		}
	})
}

function getVipConfig() {
	$H.get("vip/vipConfig").then(res => {
		renameCount.value = res.renameCount
		integralNum.value = res.integralNum
		topicNum.value = res.topicNum
	})
}

function selectPay() {
	payVisible.value = true
	money.value = vipList.value[activeIndex.value].price
}

//余额支付
function yuePay() {
	let scene = 1
	if (userInfo.value.vip == 0) {
		scene = 0
	}
	type.value = "yue"
	uni.showLoading({
		mask: true,
		title: "支付中"
	})
	$H.post("vip/rechargeVipByYue", {
		vipId: vipList.value[activeIndex.value].id,
		payType: type.value
	}).then(res => {
		if (res.code == 0) {
			getUserInfo()
			if (scene == 0) {
				uni.showModal({
					title: '提示',
					content: '余额支付成功，欢迎您成为会员',
					showCancel: false,
					success: function(res) {
						if (res.confirm) {}
					}
				})
			} else {
				uni.showModal({
					title: '提示',
					content: '余额支付成功，欢迎您续费会员',
					showCancel: false,
					success: function(res) {
						if (res.confirm) {}
					}
				})
			}
		}
		uni.hideLoading()
	})
}

//微信支付
function onPay() {
	uni.showLoading({
		mask: true,
		title: "处理中"
	})
	// #ifdef APP-PLUS
	type.value = "app"
	// #endif
	// #ifdef H5
	let ua = navigator.userAgent.toLowerCase()
	if (ua.match(/MicroMessenger/i) == "micromessenger") {
		type.value = "wxh5"
	} else {
		type.value = "h5"
	}
	// #endif
	// #ifdef MP-WEIXIN
	type.value = "weixin"
	// #endif
	$H.post("vip/rechargeVip", {
		vipId: vipList.value[activeIndex.value].id,
		payType: type.value
	}).then(res => {
		orderId.value = res.result
		if (res.code == 0) {
			$H.post("vip/pay", {
				orderId: orderId.value,
				payType: type.value
			}).then(res => {
				uni.hideLoading()
				// #ifdef MP-WEIXIN
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
						getUserInfo()
					},
					fail: function(err) {
						uni.showToast({
							icon: 'none',
							title: '支付取消'
						})
						uni.navigateTo({
							url: '/pages/user/vip/vip'
						})
					}
				})
				// #endif

				// #ifdef APP-PLUS
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
						getUserInfo()
					},
					fail: function(err) {
						console.log('支付失败:' + JSON.stringify(err))
						uni.showToast({
							icon: 'none',
							title: '支付取消'
						})
					}
				})
				// #endif

				// #ifdef H5
				let ua = navigator.userAgent.toLowerCase()
				if (ua.match(/MicroMessenger/i) == "micromessenger") {
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
								getUserInfo()
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
				} else {
					location.replace(res.data.mwebUrl)
				}
				// #endif

			}).catch((err) => {
				console.log(err)
				uni.showToast({
					title: err.msg,
					icon: "none",
					duration: 2000,
				})
				// #ifdef H5
				uni.navigateTo({
					url: '/pages/user/vip/vip'
				})
				// #endif
			})
		}
		uni.hideLoading()
	})
}

function getVipList() {
	$H.get("vip/vipList").then(res => {
		vipList.value = res.result
	})
}

function getUserInfo() {
	$H.get('user/userInfo').then(res => {
		userInfo.value = res.result
		uni.setStorageSync("userInfo", res.result)
	})
}

function successPayHandle(value) {
	if (value == 1) {
		onPay()
		payVisible.value = false
	} else if (value == 2) {
		yuePay()
		payVisible.value = false
	} else {
		$u.toast("请选择支付方式")
	}
}

function cancelPayHandle() {
	payVisible.value = false
}
</script>

<style>
page {
	background-color: #f8fafc;
}
</style>

<style lang="scss" scoped>
// 变量定义
$primary-color: #5662f6;
$secondary-color: #7580ff;
$accent-color: #ffd166;
$dark-color: #2d3748;
$light-color: #f8fafc;
$border-color: #e2e8f0;
$border-radius: 16rpx;

// 全局容器样式
.vip-container {
	min-height: 100vh;
	padding-bottom: 50rpx;
}

// 会员头部区域
.vip-header {
	position: relative;
	padding: 60rpx 40rpx 80rpx;
	background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
	border-radius: 0 0 30rpx 30rpx;
	box-shadow: 0 10rpx 20rpx rgba($primary-color, 0.1);
	color: #fff;
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
		background: rgba(255, 255, 255, 0.05);
		border-radius: 50%;
	}
}

.user-profile {
	display: flex;
	align-items: center;
	position: relative;
	z-index: 1;
	
	.avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		border: 4rpx solid rgba(255, 255, 255, 0.6);
		margin-right: 30rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	}
	
	.user-info {
		flex: 1;
	}
	
	.username {
		font-size: 38rpx;
		font-weight: 600;
		margin-bottom: 8rpx;
		display: flex;
		align-items: center;
	}
	
	.vip-badge {
		font-size: 20rpx;
		background-color: $accent-color;
		color: $dark-color;
		padding: 4rpx 12rpx;
		border-radius: 100rpx;
		margin-left: 12rpx;
		font-weight: bold;
	}
	
	.expiry-date {
		font-size: 24rpx;
		opacity: 0.85;
	}
	
	.non-vip {
		font-size: 26rpx;
		background: rgba(255, 255, 255, 0.2);
		padding: 6rpx 16rpx;
		border-radius: 100rpx;
		display: inline-block;
	}
}

// 定价区域
.pricing-section {
	margin: -50rpx 30rpx 40rpx;
	padding: 40rpx;
	background-color: #fff;
	border-radius: $border-radius;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
	position: relative;
	z-index: 10;
}

.section-title {
	font-size: 34rpx;
	font-weight: bold;
	color: $dark-color;
	margin-bottom: 30rpx;
	position: relative;
	padding-left: 24rpx;
	
	&::before {
		content: '';
		position: absolute;
		left: 0;
		top: 50%;
		transform: translateY(-50%);
		width: 8rpx;
		height: 30rpx;
		background: $primary-color;
		border-radius: 4rpx;
	}
}

.pricing-options {
	display: flex;
	justify-content: space-between;
	flex-wrap: wrap;
	margin: 0 -10rpx;
}

.pricing-card {
	width: calc(33.33% - 20rpx);
	margin: 0 10rpx 20rpx;
	padding: 30rpx 15rpx;
	background-color: #f9fafc;
	border-radius: $border-radius;
	display: flex;
	flex-direction: column;
	align-items: center;
	position: relative;
	transition: all 0.3s ease;
	border: 2rpx solid transparent;
	
	&--active {
		background-color: rgba($primary-color, 0.05);
		border-color: $primary-color;
		transform: translateY(-4rpx);
		box-shadow: 0 6rpx 20rpx rgba($primary-color, 0.1);
	}
	
	.discount-tag {
		position: absolute;
		top: 0;
		right: 0;
		background: $accent-color;
		color: $dark-color;
		font-size: 20rpx;
		padding: 6rpx 12rpx;
		border-radius: 0 $border-radius 0 $border-radius;
		font-weight: bold;
	}
}

.pricing-name {
	font-size: 26rpx;
	color: $dark-color;
	margin-bottom: 12rpx;
	font-weight: 500;
}

.pricing-price {
	margin-bottom: 10rpx;
	color: $primary-color;
	
	.currency {
		font-size: 26rpx;
		font-weight: 500;
		vertical-align: top;
	}
	
	.amount {
		font-size: 46rpx;
		font-weight: 700;
		line-height: 1;
	}
}

.pricing-remark {
	font-size: 22rpx;
	color: #64748b;
	text-align: center;
}

.agreement {
	font-size: 24rpx;
	color: #64748b;
	margin: 30rpx 0;
	text-align: center;
	
	.agreement-link {
		display: inline-block;
		color: $primary-color;
		margin-left: 8rpx;
	}
}

.action-button {
	width: 100%;
	height: 88rpx;
	line-height: 88rpx;
	background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
	color: #fff;
	font-size: 32rpx;
	font-weight: 600;
	border-radius: 44rpx;
	box-shadow: 0 6rpx 20rpx rgba($primary-color, 0.3);
	text-align: center;
	transition: all 0.3s;
	
	&:active {
		transform: scale(0.98);
		box-shadow: 0 4rpx 10rpx rgba($primary-color, 0.2);
	}
}

// 会员权益区域
.benefits-section {
	margin: 0 30rpx;
	padding: 40rpx;
	background-color: #fff;
	border-radius: $border-radius;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.benefits-grid {
	display: flex;
	flex-wrap: wrap;
	margin: 0 -15rpx;
}

.benefit-card {
	width: calc(50% - 30rpx);
	margin: 15rpx;
	padding: 24rpx;
	background-color: #f9fafc;
	border-radius: $border-radius;
	display: flex;
	align-items: center;
	transition: all 0.3s;
	
	&:hover {
		transform: translateY(-4rpx);
		box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.04);
	}
	
	.benefit-icon {
		width: 80rpx;
		height: 80rpx;
		border-radius: $border-radius;
		margin-right: 20rpx;
	}
}

.benefit-content {
	flex: 1;
	
	.benefit-title {
		font-size: 28rpx;
		color: $dark-color;
		font-weight: 600;
		margin-bottom: 6rpx;
		display: block;
	}
	
	.benefit-desc {
		font-size: 22rpx;
		color: #64748b;
		line-height: 1.4;
		display: block;
	}
}
</style>