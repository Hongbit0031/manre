<template>
	<view class="main-info">
		<view class="desc">
			<mp-html :content="content" />
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const type = ref('app_protocol_context')
const content = ref('')

onLoad((options) => {
	if (options.type) {
		type.value = options.type
	}
	let title = '协议'
	if (type.value == 'app_privacy_agreement') {
		getPrivacyAgreement()
		title = '用户隐私协议'
	} else if (type.value == 'app_protocol_context') {
		getProtocolContext()
		title = '用户服务协议'
	} else if (type.value == 'app_vip_recharge_context') {
		getVipRechargeContext()
		title = '会员充值协议'
	} else if (type.value == 'app_standard_context') {
		getStandardContext()
		title = '社区规范协议'
	}
	uni.setNavigationBarTitle({
		title: title
	})
})

function getProtocolContext() {
	$H.get('system/protocol').then(res => {
		content.value = res.result
	})
}

function getPrivacyAgreement() {
	$H.get('system/privacy').then(res => {
		content.value = res.result
	})
}

function getStandardContext() {
	$H.get('system/standard').then(res => {
		content.value = res.result
	})
}

function getVipRechargeContext() {
	$H.get('system/vipRecharge').then(res => {
		content.value = res.result
	})
}
</script>

<style lang="scss" scoped>
	.main-info {
		padding: 20rpx;
	}

	.desc {
		overflow: hidden;
		color: #262626;

		.wxParse {
			color: #262626;
		}
	}
</style>