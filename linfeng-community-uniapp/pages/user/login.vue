<template>
	<view>
		<view class="login">
			<image class="logo" :src="shareCover"></image>
			<text class="txt1">申请微信授权登录</text>
			<text class="txt2">为了给您提供更好的服务,请先登录哦</text>
			<button class="login-btn" v-if="loginType==0" @click="login">一键登录</button>
			<button open-type="getPhoneNumber" @getphonenumber="getPhoneNumber" v-if="loginType==1" class="login-btn">手机号登录</button>
			<text class="txt3" @click="goBack">暂不登录</text>
		</view>
		<WeixinLoginComp :showPop="showPop" @info="updateWxInfo" @close="closePop"></WeixinLoginComp>
		<!-- 用户隐私协议 -->
		<u-popup mode="center" mask-close-able="false" v-model="showPrivacy">
			<view class="popup-box">
				<view class="lf_privacy_title">
					{{title}}
				</view>
				<view class="lf_privacy_content">
					<text class="lf_privacy_txt">{{desc1}}</text>
					<text class="lf_privacy_txt lf_privacy_color" @click="openPrivacyContract">
						{{urlTitle}}
					</text>
					<text class="lf_privacy_txt">{{desc2}}</text>
				</view>
				<view class="lf_privacy_btn">
					<button class="lf_privacy_btn_refuse" @click="handleDisagree">拒绝</button>
					<button id="agree-btn" type="default" open-type="agreePrivacyAuthorization"
						class="lf_privacy_btn_refuse agree"
						@agreeprivacyauthorization="handleAgreePrivacyAuthorization">同意</button>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad,
		onShow
	} from '@dcloudio/uni-app'
	import websocket from '@/utils/websocket.js'
	import $store from '@/store/index.js'
	import WeixinLoginComp from "@/components/weixin-login/weixin-login.vue"

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H

	// 响应式数据
	const shareCover = ref("")
	const showPop = ref(false)
	const showPrivacy = ref(false)
	const title = ref("用户隐私保护提示")
	const desc1 = ref("感谢您使用本产品，您使用本产品前应当仔细阅读并同意")
	const urlTitle = ref("《小程序隐私保护指引》")
	const desc2 = ref("当您点击同意并开始使用产品服务时，即表示你已理解并同意该条款内容，该条款将对您产生法律约束力。如您拒绝，将无法更好的体验产品。")
	const loginType = ref(0) //登录方式 0通过openid登录  1手机号登录 
	const loginInfoPop = ref(0) //登录是否要求填写头像昵称0否1是
	// 生命周期
	onLoad(() => {
		getSysInfo()
		if (wx.getPrivacySetting) {
			wx.getPrivacySetting({
				success: res => {
					console.log("是否需要授权：", res.needAuthorization, "隐私协议的名称为：", res.privacyContractName)
					if (res.needAuthorization) {
						showPrivacy.value = true
					}
				},
				fail: () => {},
				complete: () => {},
			})
		}
	})

	onShow(() => {

	})

	// 方法
	const visitor = () => {
		//访问统计
		var terminal = 'other'
		// #ifdef APP
		terminal = 'App'
		// #endif
		// #ifdef H5
		terminal = 'H5'
		// #endif
		// #ifdef MP
		terminal = 'miniapp'
		// #endif
		$H.post('user/visitor', {
			'terminal': terminal
		}).then(res => {})
	}

	const openPrivacyContract = () => {
		uni.openPrivacyContract({})
	}

	const handleAgreePrivacyAuthorization = () => {
		showPrivacy.value = false
		login()
	}

	const handleDisagree = () => {
		showPrivacy.value = false
		uni.switchTab({
			url: '/pages/index/index'
		})
	}

	const goBack = () => {
		uni.reLaunch({
			url: '/pages/index/index'
		})
	}

	const getSysInfo = () => {
		$H.get("system/wxLoginBasicInfo").then(res => {
			shareCover.value = res.logo
			loginType.value = res.loginType
			loginInfoPop.value = res.loginInfoPop
		})
	}

	const login = async () => {
		let loginCode = await getLoginCode()
		$H.post('user/miniWxLogin', {
			code: loginCode,
		}).then(res2 => {
			if (res2.code === 0) {
				uni.setStorageSync("hasLogin", true)
				uni.setStorageSync("token", res2.token)
				$store.state.token = res2.token
				getUserInfo()
			} else {
				console.log(res2.msg)
				uni.hideLoading()
			}
		})
	}

	const getUserInfo = () => {
		uni.showLoading({
			title: "自动登录中"
		})
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
			uni.hideLoading()
			visitor() //访问统计
			let name = res.result.username
			//如果后台开启了弹框设置功能且微信名称是默认的，那么需要弹框提醒用户设置昵称和用户名
			if (name.includes("LF_") && loginInfoPop.value==1) {
				showPop.value = true
			} else {
				uni.navigateBack()
			}
		})
	}

	const getLoginCode = () => {
		return new Promise((resolve, reject) => {
			uni.login({
				provider: 'weixin',
				success: function(loginRes) {
					resolve(loginRes.code)
				}
			})
		})
	}

	//手机号一键获取接口 需在微信公众平台提前付费开通该接口使用权限
	const getPhoneNumber = (e) => {
		if (e.detail.errMsg === "getPhoneNumber:ok") {
			console.log(e)
			uni.login({
				provider: 'weixin',
				success: (res) => {
					$H.post('user/getSessionKey', {
						code: res.code
					}).then(res1 => {
						if (res1.code == 0) {
							$H.post('user/bindWxPhone', {
								wechatOpenId: res1.openid,
								sessionKey: res1.session_key,
								encryptedData: e.detail.encryptedData,
								iv: e.detail.iv,
							}).then(res2 => {
								uni.setStorageSync("hasLogin", true)
								uni.setStorageSync("token", res2.result.token)
								$store.state.token = res2.result.token
								if (loginInfoPop.value==1) {
									showPop.value = true
								}
								visitor() //访问统计
							})
						}
					})
				},
				fail: () => {
					uni.showToast({
						title: "微信登录授权失败",
						icon: "none"
					})
				}
			})
		} else {
			uni.showToast({
				title: "获取手机授权失败",
				icon: "none"
			})
		}
	}

	const closePop = () => {
		showPop.value = false
		uni.navigateBack()
	}

	const updateWxInfo = (obj) => {
		$H.post("user/userInfoEdit", {
			avatar: obj.avatarUrl,
			username: obj.nickName
		}).then(res => {
			if (res.code == 0) {
				userInfo()
			}
		})
	}

	const userInfo = () => {
		$H.get("user/userInfo").then(res => {
			$store.state.loginUserInfo = res.result
			uni.setStorageSync("userInfo", res.result)
			showPop.value = false
			uni.navigateBack()
		})
	}
</script>

<style lang="scss" scoped>
	.login {
		display: flex;
		flex-direction: column;
		padding: 100rpx;
	}

	.login .logo {
		width: 230rpx;
		height: 230rpx;
		margin: 50rpx auto;
	}

	.login .txt1 {
		margin-bottom: 10rpx;
	}

	.login .txt2 {
		margin-bottom: 50rpx;
	}

	.login .txt3 {
		color: #8c8c8c;
		margin-bottom: 90rpx;
		text-align: center;
	}

	.user-avatar {
		width: 160rpx;
		height: 160rpx;
		border-radius: 50%;
		overflow: hidden;
		margin: 50rpx auto;
	}

	.user-name {
		font-size: 35rpx;
		font-family: PingFang SC;
		font-weight: bold;
		color: #000;
		margin: 50rpx auto;
	}

	.login-btn {
		background: #323232;
		color: #fff;
		margin-left: 20rpx;
		margin-right: 20rpx;
		border-radius: 40rpx;
		margin-bottom: 16rpx;
	}

	.popup-box {
		width: 80vw;
		overflow: hidden;
		background: #ffffff;
		padding: 30rpx;
		border-radius: 24rpx;

		.lf_privacy_title {
			font-size: 48rpx;
			font-family: Source Han Sans CN-Bold, Source Han Sans CN;
			font-weight: bold;
			color: #000000;
			line-height: 56rpx;
			text-align: center;
		}

		.lf_privacy_content {
			margin-top: 48rpx;
			text-indent: 2em;

			.lf_privacy_txt {
				font-size: 28rpx;
				font-family: Source Han Sans CN-Normal, Source Han Sans CN;
				font-weight: 400;
				color: #000000;
				line-height: 33rpx;
			}
		}

		.lf_privacy_btn {
			display: flex;
			justify-content: space-evenly;
			align-items: center;
			margin-top: 48rpx;

			.lf_privacy_btn_refuse {
				padding: 0 60rpx;
				margin: 0;
				background: none;
				font-size: 32rpx;
				font-family: Source Han Sans CN-Normal, Source Han Sans CN;
				font-weight: 400;
				color: #000000;
				line-height: 80rpx;
			}

			.agree {
				color: #ffffff;
				background: #00aa00;
			}
		}

		.lf_privacy_color {
			color: #aaaaff !important;
		}
	}
</style>