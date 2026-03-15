<template>
	<view>
		<view class="get-wx-info" :class="showPop === true ? 'on' : ''">
			<view class="title">
				<u-icon name="weixin-circle-fill" color="#00aa00" class="titles-top"></u-icon>
				<view class="title-txt">
					小程序 申请
				</view>
			</view>
			<view class="desc-container">
				<view class="desc-content">
					请完善您的头像、昵称信息
				</view>
				<view class="desc-wx">
					如不设置可点击取消，系统分配默认头像昵称
				</view>
			</view>
			<view class="main">
				<button class="wx-info-container" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
					<view class="main-item">
						<image class="ava-img" :src="avatarUrl?avatarUrl:default_avatar" mode=""></image>
						<view class="ava-img-desc">
							点击选择头像
						</view>
					</view>
				</button>
				<view class="main-item">
					<view class="nick">
						昵称
					</view>
					<input class="nick-infomation" type="nickname" v-model="nickName" @blur="getNickname"
						placeholder="请填写昵称">
				</view>
			</view>
			<view class="show-cancel">
				<view class="lf-bts" @click="closePop">
					取消
				</view>
				<view class="lf-bts right" @click="authorize">
					授权
				</view>
			</view>
		</view>
		<view class="cancel-pop" @touchmove.prevent v-show="showPop" @click="closePop"></view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'

// Props
const props = defineProps({
	showPop: {
		type: Boolean,
		default: false
	}
})

// Emit
const emit = defineEmits(['close', 'info'])

// 获取实例
const { proxy } = getCurrentInstance()
const $c = proxy.$c

// 响应式数据
const avatarUrl = ref('')
const nickName = ref('')
const openid = ref('')
const default_avatar = ref('https://demo.linfeng.tech/resource/images/default_avatar.png')

// 方法
const onChooseAvatar = (e) => {
	uploadImg(e.detail.avatarUrl)
}

const getNickname = (e) => {
	nickName.value = e.detail.value
}

const uploadImg = async (url) => {
	avatarUrl.value = url
}

const closePop = () => {
	emit('close', false)
}

const authorize = () => {
	if (!avatarUrl.value) {
		uni.showToast({
			title: '请选择头像',
			icon: "none",
			duration: 1500
		})
		return
	}
	nickName.value = nickName.value.trim()
	if (!nickName.value) {
		uni.showToast({
			title: '请填写昵称',
			icon: "none",
			duration: 1500
		})
		return
	}
	uni.showLoading({
		title: "更新中"
	})
	uni.uploadFile({
		url: $c.domain + 'common/upload',
		filePath: avatarUrl.value,
		name: 'Image',
		header: {
			token: uni.getStorageSync("token")
		},
		success: (uploadFileRes) => {
			let data = JSON.parse(uploadFileRes.data)
			if (data.code == 0) {
				let obj = {}
				obj.avatarUrl = data.result
				obj.nickName = nickName.value
				emit("info", obj)
			}
			uni.hideLoading()
		}
	})
}
</script>

<style lang="scss" scoped>

	button {
		padding: 0upx;
		margin: 0upx;
		border: none;
		border-radius: 0upx;
		box-sizing: border-box;
		background-color: #f3f3f3;
	}

	button.button-hover {
		transform: translate(3rpx, 3rpx);
		background-color: #f3f3f3;
	}

	button::after {
		border: none
	}

	.cancel-pop {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: #000;
		opacity: 0.7;
		z-index: 10;
	}

	.get-wx-info {
		background-color: #fff;
		position: fixed;
		bottom: 0;
		left: 0;
		width: 100%;
		z-index: 6666;
		padding: 32rpx 40rpx 200rpx;
		border-radius: 24rpx 24rpx 0 0;
		box-shadow: 0px 0px 30rpx 30rpx rgba(107, 127, 153, 0.30);
		box-sizing: border-box;
		
		transform: translate3d(0, 100%, 0);
		-webkit-transform: translate3d(0, 100%, 0);
		-ms-transform: translate3d(0, 100%, 0);
		-moz-transform: translate3d(0, 100%, 0);
		-o-transform: translate3d(0, 100%, 0);
		transition: all .3s cubic-bezier(.25, .5, .5, .9);
		-webkit-transition: all .3s cubic-bezier(.25, .5, .5, .9);
		-moz-transition: all .3s cubic-bezier(.25, .5, .5, .9);
		-o-transition: all .3s cubic-bezier(.25, .5, .5, .9);
		&.on {
			transform: translate3d(0, 0, 0);
			-webkit-transform: translate3d(0, 0, 0);
			-ms-transform: translate3d(0, 0, 0);
			-moz-transform: translate3d(0, 0, 0);
			-o-transform: translate3d(0, 0, 0);
		}

		.title {
			width: 100%;
			padding: 36rpx 10rpx 40rpx;
			box-sizing: border-box;
			overflow: auto;

			.titles-top {
				float: left;
				width: 40rpx;
				height: 40rpx;
				font-size: 40rpx;
				border-radius: 50%;
				margin-right: 20rpx;
			}

			.title-txt {
				float: left;
				font-size: 28rpx;
				font-family: PingFang SC, PingFang SC-Semibold;
				font-weight: 600;
				text-align: left;
				color: #000;
			}
		}

		.desc-container {
			width: 100%;

			.desc-content {
				width: 100%;
				font-size: 32rpx;
				font-family: PingFang SC, PingFang SC-Semibold;
				font-weight: 600;
				text-align: left;
				color: #000;
			}

			.desc-wx {
				width: 100%;
				font-size: 26rpx;
				font-family: PingFang SC, PingFang SC-Semibold;
				font-weight: 400;
				text-align: left;
				color: #666;
				margin-top: 10rpx;
			}
		}

		.main {
			width: 100%;
			border-top: 2rpx solid #ededed;
			margin-top: 30rpx;

			.wx-info-container {
				border-color: #fff;
				padding: 0rpx !important;
				margin: 0rpx !important;
				border: none !important;
				border-radius: 0rpx !important;
				box-sizing: border-box !important;
				background-color: #f3f3f3 !important;

			}

			.main-item {
				width: 100%;
				height: 112rpx;
				border-bottom: 2rpx solid #ededed;
				overflow: auto;
				padding: 0 20rpx;

				.ava-img {
					float: left;
					width: 70rpx;
					height: 70rpx;
					border-radius: 8rpx;
					margin-top: 20rpx;
					margin-right: 20rpx;
				}

				.ava-img-desc {
					float: left;
					font-size: 26rpx;
					font-family: PingFang SC, PingFang SC-Semibold;
					font-weight: 400;
					line-height: 112rpx;
					text-align: left;
					color: #666;
				}

				.nick {
					float: left;
					font-size: 26rpx;
					font-family: PingFang SC, PingFang SC-Semibold;
					font-weight: 400;
					line-height: 112rpx;
					text-align: left;
					color: #000;
				}

				.nick-infomation {
					float: left;
					font-size: 26rpx;
					font-family: PingFang SC, PingFang SC-Semibold;
					font-weight: 400;
					line-height: 112rpx;
					text-align: left;
					color: #000;
					margin-top: 34rpx;
					margin-left: 36rpx;
				}
			}
		}

		.show-cancel {
			width: 80%;
			padding: 0 30rpx;
			box-sizing: border-box;
			margin: 44rpx auto 0;

			.lf-bts {
				float: left;
				width: 210rpx;
				height: 72rpx;
				line-height: 72rpx;
				text-align: center;
				background: #F2F2F2;
				border-radius: 8rpx;
				font-size: 28rpx;
				font-family: PingFang SC, PingFang SC-Regular;
				font-weight: 400;
				text-align: center;
				color: #5DBC6F;

				&.right {
					float: right;
					background: #5DBC6F;
					color: #ffffff;
				}
			}
		}

	}
</style>