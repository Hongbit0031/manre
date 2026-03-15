<template>
	<view :class="'pop-up ' + (show ? 'show' : 'hide')">
		<view @tap="close" class="pop-up-mask" v-if="show"></view>
		<view :class="'pop-box ' + (isPartDetails ? 'pb' : '')">
			<view class="pop-box-title">选择支付方式</view>
			<view @tap="close" class="iconfont iconclose pop-box-close">
				关闭
			</view>
			<view class="pop-box-select" @tap.stop.prevent="changeSelect" data-choose="1" :class="choose == 1 ? 'choose-it' : ''">
				<u-icon name="weixin-circle-fill" color="#00aa00" size="48"></u-icon>
				<view class="decs-select">微信支付</view>
			</view>
			<view class="pop-box-select" @tap.stop.prevent="changeSelect" data-choose="2" :class="choose == 2 ? 'choose-it' : ''">
				<u-icon name="rmb-circle-fill" color="#ffaa00"  size="48"></u-icon>
				<view class="decs-select">余额支付</view>
			</view>
			<view class="pop-box-tips">
				<text>合计需要支付：</text>
				<text class="pop-box-tips-red">{{ money }}元</text>
			</view>
			<view class="pop-box-agreement">
				<image @tap="selectHandle" class="pop-box-agreement-icon"
				:src="$IMG + '/img/full-gou.png'" v-if="select"></image>
				<image @tap="selectHandle" class="pop-box-agreement-icon"
					:src="$IMG + '/img/empty-gou.png'" v-else></image>
				<view>本人同意此协议</view>
				<view @tap="jumpToAgreement" class="pop-box-agreement-text">《会员开通协议》
				</view>
			</view>
			<view @tap="payHandle" class="pop-box-button">确认支付</view>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()
const $u = proxy.$u
const $f = proxy.$f
const $IMG = ref(proxy.$IMG)

const props = defineProps({
	show: {
		type: Boolean,
		default: false
	},
	money: {
		type: [String, Number],
		default: 0.01
	},
	//  解决iOS底部适配问题
	isPartDetails: {
		type: Boolean,
		default: true
	}
})

const emit = defineEmits(['cancel', 'success'])

const select = ref(true)
const choose = ref(1)

function changeSelect(value) {
	choose.value = value.currentTarget.dataset.choose
}

function close() {
	emit("cancel")
}

function payHandle() {
	if (!select.value) {
		$u.toast('请先勾选协议框')
		return
	}
	emit("success", choose.value)
}

function jumpToAgreement() {
	$f.jump("/pages/user/protocol?type=app_vip_recharge_context")
}

function selectHandle() {
	select.value = !select.value
}
</script>
<style lang="scss">
	.decs-select{
		margin-left: 30rpx;
		font-size: 34rpx;
	}
	.pop-up.show .pop-box {
		transform: translate(0, 0);
	}

	.pop-up.hide .pop-box {
		transform: translate(0, 100%);
	}

	.pop-up .pop-up-mask {
		top: 0;
		height: 100%;
		background: rgba(0, 0, 0, 0.6);
		z-index: 998;
	}

	.pop-box,
	.pop-up .pop-up-mask {
		position: fixed;
		left: 0;
		width: 100%;
	}

	.pop-box {
		background: white;
		border-radius: 24rpx 24rpx 0 0;
		padding: 10px 20px;
		bottom: 0;
		transition: all 0.2s ease;
		overflow: hidden;
		z-index: 1001;
	}

	.pb {
		// 解决ios底部小黑线
		padding-bottom: calc(env(safe-area-inset-bottom) + 32rpx);
	}

	.pop-box .pop-box-title {
		font-size: 40rpx;
		font-weight: 700;
		color: #000;
	}

	.pop-box .pop-box-close {
		position: absolute;
		right: 32rpx;
		top: 24rpx;
		color: #9c9c9c;
		font-size: 32rpx;
	}

	.pop-box-select {
		margin-top: 40rpx;
		height: 90rpx;
		border-radius: 20rpx;
		display: flex;
		align-items: center;
		padding-left: 26rpx;
		color: #000;
		background-color: #f9f9f9;
		font-size: 32rpx;
		font-weight: 500;
		padding-right: 34rpx;
	}

	.choose-it{
		border: 1px solid #fa3426;
	}
	

	.pop-box-tips {
		color: #9c9c9c;
		font-size: 28rpx;
		margin-top: 48rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.pop-box-tips-red {
		color: #fa5555;
		font-weight: 700;
	}

	.pop-box-button {
		height: 96rpx;
		background: #00aa00;
		font-size: 24rpx;
		justify-content: center;
		color: #fff;
		font-size: 32rpx;
		font-weight: 500;
		border-radius: 24rpx;
		margin-top: 64rpx;
		margin-bottom: 32px;
	}

	.pop-box-agreement,
	.pop-box-button {
		display: flex;
		align-items: center;
	}

	.pop-box-agreement {
		justify-content: flex-start;
		margin-top: 48rpx;
		color: #aeb2bb;
		font-size: 24rpx;
	}

	.pop-box-agreement-icon {
		width: 28rpx;
		height: 28rpx;
		display: block;
		margin-right: 16rpx;
	}

	.pop-box-agreement-text {
		color: #111e38;
		font-size: 24rpx;
	}
</style>