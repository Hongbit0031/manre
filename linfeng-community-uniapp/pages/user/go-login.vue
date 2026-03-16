<template>
	<view class="container flex-items-plus flex-column">
		<view class="login-logoBox">
			<image class="login-logo" :src="logoUrl"></image>
		</view>
		<view class="loginTitle-text flex-column-plus flex-items font-color-666 fs26">
			<text>为了给您提供更好的服务</text>
			<text>请先登录哦</text>
		</view>
		<view>
			<!-- #ifdef H5 || APP-PLUS -->
			<view class="loginBut" @click="goMobileLogin">手机号登录</view>
			<view v-if="emailLogin=='1'&&openEmailRegister=='0'" class="loginButton" @click="goEmailLogin">邮箱登录</view>
			<view v-if="emailLogin=='1'&&openEmailRegister=='1'" class="loginButton" @click="goEmailLogin">邮箱登录/注册</view>
			<!-- #endif -->
			<!-- #ifdef MP-WEIXIN -->
			<view class="loginBut" @click="goWxLogin">微信登录</view>
			<!-- #endif -->
		</view>
		<!-- #ifdef H5 || APP-PLUS -->
		<view class="flex-row-plus mar-top-30">
			<text class="font-colors">还没有账号，</text>
			<view class="register-text" @click="goRegister">去注册</view>
		</view>
		<!-- #endif -->
		<view class="flex-row-plus mar-top-60">
			<text class="font-colors" @click="goBack">——暂不登录，再看看——</text>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import websocket from '@/utils/websocket.js'
import $store from '@/store/index.js'
import wxMpUtil from "@/utils/wxMpUtil.js"
import config from '@/utils/config.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

// 响应式数据
const logoUrl = ref("")
const emailLogin = ref("")
const openEmailRegister = ref("")

// 生命周期
onLoad((options) => {
	getSysInfo()
	// #ifdef H5
	//实现第三方自动登录
	if (options && options.accessToken) {
		doAutoLoginEvent(options.accessToken)
	}else{
		if(config.wxh5Login){
			wxMpUtil.checkRegisterAndLogin(options.code, "/pages/user/go-login")
			let ua = navigator.userAgent.toLowerCase()
			if (ua.match(/MicroMessenger/i) == "micromessenger") {
				if (typeof WeixinJSBridge == "undefined"){
				   if( document.addEventListener ){
					   document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false)
				   }else if (document.attachEvent){
					   document.attachEvent('WeixinJSBridgeReady', onBridgeReady)
					   document.attachEvent('onWeixinJSBridgeReady', onBridgeReady)
				   }
				}
			}
		}
	}
	// #endif
})

onShow(() => {
	uni.hideLoading()
})

// 方法
const getSysInfo = () => {
	$H.get('system/config').then(res => {
		logoUrl.value = res.logoUrl
		emailLogin.value = res.emailLogin
		openEmailRegister.value = res.openEmailRegister
	})
}

const goMobileLogin = () => {
	uni.navigateTo({
		url: './sms-login'
	})
}

const goRegister = () => {
	uni.navigateTo({
		url: 'register'
	})
}

const goBack = () => {
	uni.switchTab({
		url: '/pages/index/index'
	})
}

const goEmailLogin = () => {
	uni.navigateTo({
		url: './email-login'
	})
}

const goWxLogin = () => {
	uni.navigateTo({
		url: './login'
	})
}

const doAutoLoginEvent = (token) => {
	uni.setStorageSync("hasLogin", true)
	uni.setStorageSync("token", token)
	$store.state.token = token
	//初始化登录信息
	getLoginUserInfo()
}

const getLoginUserInfo = () => {
	$H.get("user/userInfo").then(res => {
		$store.state.loginUserInfo = res.result
		uni.setStorageSync("userInfo", res.result)
		//连接websocket
		websocket.initConnect().catch(err => {
				console.warn('WebSocket连接失败', err)
			})
		//获取好友列表
		$store.dispatch('getFriendList')
		///获取通知消息
		$store.dispatch('getNoticeList')
		uni.switchTab({
			url: '/pages/index/index'
		})
	})
}
</script>

<style lang="scss">
	.container {
		background-color: #FFFFFF;
		height: 100vh;

		.login-logoBox {
			margin-top: -200rpx;

			.login-logo {
				width: 200rpx;
				height: 200rpx;
			}
		}

		.loginTitle-text {
			margin-top: 100rpx;
		}

		.goToHome {
			background: none;
			border: none;
			width: auto;
			margin-top: 50rpx;
			color: #999999;
			text-decoration: underline;

			&:after {
				display: none;
			}
		}

		.loginWxBut {
			background: #333333;
			color: #efece8;
			height: 88rpx;
			width: 600rpx;
			text-align: center;
			line-height: 88rpx;
			margin-top: 49rpx;
		}

		.loginBut {
			background-color: #333333;
			color: #efece8;
			height: 88rpx;
			width: 600rpx;
			text-align: center;
			line-height: 88rpx;
			margin-top: 30rpx;
			border-radius: 10rpx;
		}

		.loginButton {
			background-color: #959292;
			color: #efece8;
			height: 88rpx;
			width: 600rpx;
			text-align: center;
			line-height: 88rpx;
			margin-top: 30rpx;
			border-radius: 10rpx;
		}

		.register-text {
			color: #8b857d;
		}
	}
</style>
<style scoped>
	.loginWxBut::after {
		border-radius: 0 !important;
	}

	.flex-column-plus {
		display: flex;
		flex-direction: column
	}


	.fs28 {
		font-size: 28upx;
	}


	.font-colors {
		color: #adaca9;
	}

	.font-color-666 {
		color: #6887a9;
	}
</style>