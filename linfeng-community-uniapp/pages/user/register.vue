<template>
	<view class="container flex-items-plus flex-column">
		<view class="login-logoBox">
			<image class="login-logo" :src="logoUrl"></image>
		</view>
		<view class="iphoneNum-box flex-row-plus flex-items">
			<view style="margin-right: 30rpx">
				<image class="loginIcon" src="../../static/images/phone.png"></image>
			</view>
			<view>
				<input v-model="form.mobile" placeholder-class="iphoneNum-input" type="number" maxlength='11'
					placeholder="请输入您的手机号" />
			</view>
		</view>
		<view class="flex-row-plus mar-top-20">
			<view class="code-box">
				<view style="margin-right: 30rpx">
					<image class="loginIcon" src="../../static/images/code.png"></image>
				</view>
				<view>
					<input v-model="form.code" placeholder-class="codeNum-input" placeholder="请输入验证码" />
				</view>
			</view>
			<view :class="disabled === true ? 'on' : ''" :disabled="disabled" class="getcode" @click="getCode">
				{{ text }}
			</view>
		</view>
		<view v-if="emailLogin=='1'" class="email-box flex-row-plus flex-items">
			<view style="margin-right: 30rpx">
				<image class="loginIcon-email" src="../../static/images/email.png"></image>
			</view>
			<view>
				<input v-model="form.email" placeholder-class="iphoneNum-input" maxlength='30' placeholder="邮箱[可选填]" />
			</view>
		</view>
		<view v-if="emailLogin=='1'" class="mar-top-60">
			<view class="registerBut mar-top-100" @click="phoneRegister">注册并登录
			</view>
		</view>
		<view v-else class="mar-top-30">
			<view class="registerBut mar-top-60" @click="phoneRegister">注册并登录
			</view>
		</view>
		<view class="flex-row-plus mar-top-30">
			<text class="font-colors">已有账号，</text>
			<view class="font-colors" @click="gologin">去登录</view>
		</view>
		<view class="agreement">
			<image v-if="!agreement" src="../../static/images/none.png" mode="" @click="agreement=true"></image>
			<image v-else src="../../static/images/solid.png" mode="" @click="agreement=false"></image>
			<text class="font-color-999">注册即代表同意</text>
			<text class="font-colors" @click="protocol('app_protocol_context')">《APP用户服务协议》</text>和
			<text class="font-colors" @click="protocol('app_privacy_agreement')">《APP个人隐私协议》</text>
		</view>
	</view>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import websocket from '@/utils/websocket.js'
import $store from '@/store/index.js'
import { useSendVerifyCode } from '@/hooks/useSendVerifyCode.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $u = proxy.$u

// 响应式数据
const form = reactive({
	mobile: "",
	code: "",
	email: ""
})

const logoUrl = ref("")
const agreement = ref(false)
const emailLogin = ref("")

// 使用发送验证码组合式函数
const { disabled, text, sendCode } = useSendVerifyCode()

// 生命周期
onLoad((options) => {
	if (options.mobile) {
		form.mobile = options.mobile
	}
	getSysInfo()
})

// 方法
const gologin = () => {
	// #ifdef H5 || APP
	uni.navigateTo({
		url: './sms-login'
	})
	// #endif
	// #ifdef MP-WEIXIN
	uni.navigateTo({
		url: './login'
	})
	// #endif
}

const getSysInfo = () => {
	$H.get('system/config').then(res => {
		logoUrl.value = res.logoUrl
		emailLogin.value = res.emailLogin
	})
}

const phoneRegister = () => {
	if (form.mobile == '') {
		$u.toast('请输入手机号')
		return
	} else if (form.code == '') {
		$u.toast('请输入验证码')
		return
	} else if (!agreement.value) {
		uni.showToast({
			title: '请先阅读并同意《用户服务协议和个人隐私协议》',
			duration: 2000,
			icon: 'none'
		})
		return
	}
	var emailRegExp = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/
	if (form.email != '') {
		if (!emailRegExp.test(form.email)) {
			$u.toast('请输入规范的邮箱')
			return
		}
	}
	uni.showLoading({
		mask: true,
		title: '登录中'
	})
	$H.post("user/register", form).then(res => {
		if (res.code == 0) {
			uni.setStorageSync("hasLogin", true)
			uni.setStorageSync("token", res.token)
			$store.state.token = res.token
			uni.switchTab({
				url: '/pages/index/index'
			})
			getUserInfo()
		}
		uni.hideLoading()
	})
}

const getUserInfo = () => {
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
		visitor()//访问统计
	})
}

const getCode = () => {
	if (disabled.value) return
	let phoneCodeVerification = /^[1][3-9][0-9]{9}$/
	if (form.mobile == '') {
		$u.toast('请输入手机号')
	} else if (!phoneCodeVerification.test(form.mobile)) {
		$u.toast('请输入规范的手机号')
	} else {
		uni.showLoading({
			title: '正在获取验证码'
		})

		$H.post("user/sendSmsCode", {
			mobile: form.mobile
		}).then(res => {
			if (res.code == 0) {
				uni.hideLoading()
				sendCode()
				uni.showToast({
					title: res.msg,
					icon: 'none',
					duration: 3500,
				})
			}
		})
	}
}

const protocol = (type) => {
	uni.navigateTo({
		url: './protocol?type=' + type
	})
}

const visitor = () => {
	//访问统计
	var terminal='other'
	// #ifdef APP
	terminal='App'
	// #endif
	// #ifdef H5
	terminal='H5'
	// #endif
	// #ifdef MP
	terminal='miniapp'
	// #endif
	$H.post('user/visitor',{
		'terminal':terminal
	}).then(res => {})
}
</script>

<style lang="scss" scoped>
	.container {
		background-color: #FFFFFF;
		height: 100vh;

		.login-logoBox {
			margin-top: -300rpx;

			.login-logo {
				width: 200rpx;
				height: 200rpx;
			}
		}

		.iphoneNum-box {
			margin-top: 100rpx;
			border-bottom: 1rpx solid #DDDDDD;
			height: 100rpx;
			width: 600rpx;

			.loginIcon {
				width: 40rpx;
				height: 53rpx;
			}


			.iphoneNum-input {
				color: #999999;
				font-size: 28rpx;
				font-weight: 400;
			}
		}

		.email-box {
			margin-top: 10rpx;
			border-bottom: 1rpx solid #DDDDDD;
			height: 100rpx;
			width: 600rpx;

			.loginIcon-email {
				width: 50rpx;
				height: 50rpx;
			}

			.iphoneNum-input {
				color: #999999;
				font-size: 28rpx;
				font-weight: 400;
			}
		}

		.passwordNum-box {
			border-bottom: 1rpx solid #DDDDDD;
			height: 100rpx;
			width: 600rpx;

			.passwordNum-input {
				color: #999999;
				font-size: 28rpx;
				font-weight: 400;
				width: 346rpx;
			}
		}

		.code-box {
			border-bottom: 1rpx solid #DDDDDD;
			height: 100rpx;
			width: 360rpx;
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			align-items: center;

			.loginIcon {
				width: 44rpx;
				height: 50rpx;
			}

			.code-lab {
				width: 200rpx;
			}

			.codeNum-input {
				color: #999999;
				font-size: 28rpx;
				font-weight: 400;
			}
		}

		.getcode {
			background-color: #a4a6a8;
			height: 100rpx;
			width: 230rpx;
			display: flex;
			flex-direction: row;
			justify-content: center;
			align-items: center;
			margin-left: 20rpx;
			color: #FFFFFF;
		}

		.registerBut {
			background: #333333;
			color: #efece8;
			height: 100rpx;
			width: 600rpx;
			text-align: center;
			line-height: 100rpx;
			margin-top: 30rpx;
		}

		.registerBut {
			background: #333333;
			color: #FFFFFF;
			height: 88rpx;
			width: 600rpx;
			text-align: center;
			line-height: 88rpx;
			margin-top: 140rpx;
		}

		.agreement {
			margin: 100rpx 50rpx;
			line-height: 50rpx;

			image {
				width: 34rpx;
				height: 34rpx;
				margin-right: 15upx;
			}
		}
	}

	.font-colors {
		color: #abbacc;
	}

	.font-color-999 {
		color: #999999;
	}
</style>
