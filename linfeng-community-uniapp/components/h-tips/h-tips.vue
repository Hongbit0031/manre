<template>
	<view class="wrap">
		<view class="tip-tag">
			提醒
		</view>
		{{content}}
	</view>
</template>

<script setup>
defineProps({
	content: {
		type: String,
		default: ''
	}
})
</script>

<style lang="scss" scoped>
	.wrap {
		display: flex;
		align-items: center;
		width: auto;
		height: 70rpx;
		background-color: #444444;
		border-radius: 10rpx;
		font-size: 24rpx;
		color: #ffffff;
		
		.tip-tag {
			margin-left: 19rpx;
			margin-right: 15rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			width: 66rpx;
			height: 34rpx;
			border-radius: 6rpx;
			background-color: #ffffff;
			font-size: 20rpx;
			color: #000000;
		}
	}
</style>