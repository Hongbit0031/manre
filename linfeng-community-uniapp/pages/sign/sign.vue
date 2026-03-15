<template>
	<view class="sign-page">
		<view class="sign-header bg-color-blue">
			<view class="sign-header__container acea-row row-between-wrapper">
				<view class="sign-header__left acea-row row-between-wrapper">
					<view class="sign-header__avatar">
						<image :src="userInfo.avatar" />
					</view>
					<view class="sign-header__text">
						<view class="sign-header__username line1">{{ userInfo.username || '' }}</view>
						<view class="sign-header__points acea-row">
							<text>积分: {{ userInfo.integral || 0 }}</text>
						</view>
					</view>
				</view>
				<view @click="goSignRecord()" class="sign-header__right acea-row row-middle">
					<view class="iconfont icon-caidan"></view>
					<view>明细</view>
				</view>
			</view>
		</view>
		<view class="sign-card">
			<view class="sign-progress acea-row row-between-wrapper">
				<view class="sign-progress__item" v-for="(item, signSystemListIndex) in signSystemList"
					:key="signSystemListIndex">
					<view
						:class="signSystemListIndex + 1 === signSystemList.length ? 'sign-progress__reward-text' : ''">
						{{ item.day }}</view>
					<view class="sign-progress__icon"
						:class=" (signSystemListIndex + 1 === signSystemList.length ? 'sign-progress__icon--reward' : '') +' ' + (sign_index >= signSystemListIndex + 1 ? 'sign-progress__icon--active' : '')">
					</view>
					<view class="sign-progress__points"
						:class="sign_index >= signSystemListIndex + 1 ? 'sign-progress__points--active' : ''">
						+{{ item.signNum }}</view>
				</view>
			</view>
			<!--加在 sign-btn 上 sign-btn--signed 为已签到-->
			<view class="sign-btn bg-color-blue" :class="userInfo.isDaySign ? 'sign-btn--signed' : ''" @click="goSign">
				{{ userInfo.isDaySign ? "已签到" : "立即签到" }}</view>
			<view class="sign-card__decoration"></view>
		</view>
		<view class="sign-card sign-stats">
			<view class="sign-stats__title">已累计签到</view>
			<view class="sign-stats__counter acea-row row-center row-bottom">
				<view class="sign-stats__digit" v-for="(item, signCountIndex) in signCount" :key="signCountIndex">
					{{ item || 0 }}</view>
				<view class="sign-stats__unit">天</view>
			</view>
			<view class="sign-stats__tip">据说连续签到第{{ day }}天可获得超额积分哦</view>
			<view class="sign-history">
				<view class="sign-history__item acea-row row-between-wrapper" v-for="(item, signListIndex) in signList"
					:key="signListIndex">
					<view>
						<view class="sign-history__title line1">{{ item.title }}</view>
						<view class="sign-history__date">{{ item.addTime }}</view>
					</view>
					<view class="sign-history__points font-colors">+{{ item.number }}</view>
				</view>
				<view @click="goSignRecord()" class="sign-history__more acea-row row-center-wrapper"
					v-if="signList.length > 0">
					点击更多
					<view class="iconfont icon-xiangyou acea-row row-center-wrapper"></view>
				</view>
			</view>
		</view>
		<view class="sign-popup acea-row row-center-wrapper" :class="active === true ? 'sign-popup--active' : ''">
			<view class="sign-popup__light loadingpic">
			</view>
			<view class="sign-popup__content">
				<view class="sign-popup__status">签到成功</view>
				<view class="sign-popup__points">获得{{ integral }}积分</view>
				<view class="sign-popup__btn" @click="close">好的</view>
			</view>
		</view>
		<view class="sign-mask" @touchmove.prevent v-if="active"></view>
	</view>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance,
		onMounted
	} from 'vue'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H

	const userInfo = ref({})
	const integral = ref(0)
	const signCount = ref([])
	const sign_index = ref(0)
	const signSystemList = ref([])
	const signList = ref([])
	const page = ref(1)
	const limit = ref(3)
	const active = ref(false)
	const day = ref("")

	onMounted(() => {
		uni.showLoading({
			title: "加载中",
			mask: true
		})
		signUser()
		signConfig()
		getSignList()
	})

	function goSignRecord() {
		uni.navigateTo({
			url: "/pages/sign/integral"
		})
	}

	// js给数字补0；num：需要补0的数字，length：长度（补到多少位）；
	function PrefixInteger(num, length) {
		return (Array(length).join("0") + num).slice(-length).split("")
	}

	//数字转中文
	function changeNumToChinese(n) {
		var cnum = ["零", "一", "二", "三", "四", "五", "六", "七", "八", "九"]
		var s = ""
		n = "" + n // 数字转为字符串
		for (var i = 0; i < n.length; i++) {
			s += cnum[parseInt(n.charAt(i))]
		}
		return s
	}

	// 获取用户信息
	function signUser() {
		$H.post('sign/userInfo', {}).then(res => {
			uni.hideLoading()
			res.result.integral = parseInt(res.result.integral)
			var sumSginDay = res.result.sumSignDay
			userInfo.value = res.result
			signCount.value = PrefixInteger(sumSginDay, 4)
			sign_index.value = parseInt(res.result.signNum)
		})
	}

	// 签到配置
	function signConfig() {
		$H.get('sign/config', {}).then(res => {
			signSystemList.value = res.result
			day.value = changeNumToChinese(signSystemList.value.length)
		})
	}

	//  用户签到
	function goSign() {
		let sumSginDay = userInfo.value.sumSignDay
		if (userInfo.value.isDaySign) {
			uni.showToast({
				title: "您今日已签到",
				icon: "none",
				duration: 2000
			})
			return
		}
		$H.post('sign/sign', {}).then(res => {
			active.value = true
			integral.value = res.result
			let signIndex = parseInt(sign_index.value + 1)
			sign_index.value = signIndex > signSystemList.value.length ? 1 : signIndex
			signCount.value = PrefixInteger(sumSginDay + 1, 4)
			userInfo.value.isDaySign = true
			signUser()
			getSignList()
		})
	}

	//获取签到列表
	function getSignList() {
		$H.get('sign/signList', {
			page: page.value,
			limit: limit.value
		}).then(res => {
			signList.value = res.result
		})
	}

	function close() {
		active.value = false
	}
</script>
<style scoped lang="scss">
	.font-colors {
		color: #64B5F6 !important;
	}

	.bg-color-blue {
		background-color: #738be2 !important;
	}

	.icon-color {
		color: #64B5F6;
	}

	.sign-page {
		.sign-header {
			width: 100%;
			height: 310rpx;

			&__container {
				padding: 0 0 0 30rpx;
				height: 234rpx;
			}

			&__left {
				width: 530rpx;
				font-size: 32rpx;
				color: #fff;
				font-weight: bold;
			}

			&__points {
				text {
					font-size: 24rpx;
					margin-top: 19rpx;
					background-color: #a17cff;
					text-align: center;
					border-radius: 6rpx;
					font-weight: normal;
					padding: 6rpx 15rpx;
					display: inline-block;
					white-space: nowrap;
				}
			}

			&__text {
				width: 410rpx;
			}

			&__avatar {
				width: 86rpx;
				height: 86rpx;
				border-radius: 50%;
				border: 4rpx solid #ecddbc;

				image {
					width: 100%;
					height: 100%;
					border-radius: 50%;
				}
			}

			&__right {
				width: 142rpx;
				height: 66rpx;
				background-color: #fff;
				border-radius: 50rpx 0 0 50rpx;
				font-size: 24rpx;
				color: #aaaaff;

				.iconfont {
					font-size: 33rpx;
					padding: 0 10rpx 0 30rpx;
					height: 35rpx;
					line-height: 35rpx;
				}
			}
		}

		.sign-card {
			background-color: #fff;
			margin: -80rpx 20rpx 0 20rpx;
			border-radius: 15rpx;
			padding-bottom: 80rpx;
			position: relative;
		}

		.sign-progress {
			padding: 0 30rpx;
			height: 240rpx;

			&__item {
				font-size: 22rpx;
				color: #8a8886;
				text-align: center;
			}

			&__reward-text {
				width: 74rpx;
				height: 32rpx;
				background-color: #f4b409;
				border-radius: 16rpx;
				font-size: 20rpx;
				color: #a57d3f;
				line-height: 32rpx;
			}

			&__points {
				font-size: 30rpx;
				color: #999;

				&--active {
					color: #ff9000;
				}
			}

			&__icon {
				background-image: url('https://demo.linfeng.tech/resource/img/str2-sign.png');
				background-repeat: no-repeat;
				background-size: 100% 100%;
				width: 56rpx;
				height: 56rpx;
				margin: 10rpx 0;

				&--active {
					background-image: url('https://demo.linfeng.tech/resource/img/yel-sign.png');
				}

				&--reward {
					background-image: url('https://demo.linfeng.tech/resource/img/str-sign.png');
					width: 75rpx;
					height: 56rpx;
				}
			}
		}

		// Sign button
		.sign-btn {
			width: 400rpx;
			height: 76rpx;
			font-size: 30rpx;
			line-height: 76rpx;
			color: #fff;
			border-radius: 50rpx;
			text-align: center;
			margin: 0 auto;

			&--signed {
				background-color: #999 !important;
			}
		}

		.sign-card__decoration {
			background-image: url('https://demo.linfeng.tech/resource/img/lan-sign.png');
			background-repeat: no-repeat;
			background-size: 100% 100%;
			width: 558rpx;
			height: 68rpx;
			position: absolute;
			left: 50%;
			transform: translateX(-50%);
			bottom: -41rpx;
			z-index: 9;
		}

		.sign-stats {
			margin-top: 15rpx;
			padding: 73rpx 0 0 0;

			&__title {
				font-size: 30rpx;
				color: #666;
				text-align: center;
			}

			&__counter {
				margin: 45rpx 0 49rpx 0;
			}

			&__digit {
				width: 80rpx;
				height: 116rpx;
				background-repeat: no-repeat;
				background-size: 100% 100%;
				color: #fff;
				font-size: 72rpx;
				text-align: center;
				line-height: 116rpx;
				margin-right: 19rpx;
				background-image: url('https://demo.linfeng.tech/resource/img/card-sign.png');
			}

			&__unit {
				font-size: 30rpx;
				color: #232323;
			}

			&__tip {
				font-size: 30rpx;
				color: #999999;
				padding: 0 55rpx;
				text-align: center;
				line-height: 1.5;
			}
		}

		.sign-history {
			margin: 45rpx 37rpx 0 37rpx;
			border-top: 1px dashed #eee;

			&__item {
				border-bottom: 1px solid #eee;
				height: 130rpx;
			}

			&__title {
				color: #232323;
				font-size: 30rpx;
				width: 400rpx;
			}

			&__date {
				font-size: 24rpx;
				color: #bbbbbb;
				margin-top: 9rpx;
			}

			&__points {
				font-size: 36rpx;
				font-family: 'GuildfordProBook 5';
			}

			&__more {
				.iconfont {
					font-size: 25rpx;
					margin: 2rpx 0 0 10rpx;
				}
			}
		}

		.sign-popup {
			width: 644rpx;
			height: 645rpx;
			position: fixed;
			top: 50%;
			left: 50%;
			margin-left: -322rpx;
			margin-top: -322.5rpx;
			z-index: 99;
			text-align: center;
			transition: all 0.3s ease-in-out 0s;
			opacity: 0;
			transform: scale(0);

			&--active {
				opacity: 1;
				transform: scale(1);
			}

			&__light {
				background-repeat: no-repeat;
				background-size: 100% 100%;
				width: 100%;
				height: 100%;
			}

			&__content {
				background-image: url('https://demo.linfeng.tech/resource/img/pop-sign.png');
				background-repeat: no-repeat;
				background-size: 100% 100%;
				width: 420rpx;
				height: 420rpx;
				margin-top: -700rpx;
				position: relative;
			}

			&__status {
				font-size: 34rpx;
				color: #fff;
				margin-top: 150rpx;
			}

			&__points {
				font-size: 30rpx;
				color: rgba(255, 255, 255, 0.6);
				margin-top: 9rpx;
			}

			&__btn {
				font-size: 30rpx;
				color: #ffffff;
				width: 260rpx;
				height: 76rpx;
				background-color: #303133;
				border-radius: 38rpx;
				line-height: 76rpx;
				margin: 48rpx auto 0 auto;
			}
		}

		.sign-mask {
			position: fixed;
			top: 0;
			left: 0;
			right: 0;
			bottom: 0;
			background-color: rgba(0, 0, 0, 0.5);
			z-index: 98;
		}
	}
</style>