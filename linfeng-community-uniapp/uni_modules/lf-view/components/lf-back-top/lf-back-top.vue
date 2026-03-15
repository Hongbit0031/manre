<template>
	<view v-if="showTag">
		<view @click="backToTop" class="back-top">
			<u-icon name="arrow-upward" color="#fff"></u-icon>
		</view>
	</view>
</template>

<script setup>
// Props定义
const props = defineProps({
	showTag: {
		type: Boolean,
		default: false
	},
	duration: {
		type: Number,
		default: 300
	}
})

// 方法
const backToTop = () => {
	uni.pageScrollTo({
		scrollTop: 0,
		duration: props.duration
	})
}
</script>

<style lang="scss" scoped>
.back-top{
	position: fixed;
	right: 20rpx;
	bottom: 180rpx;
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	background-color: #adadad;
	box-shadow: 0 0 20rpx #999;
	display: flex;
	justify-content: center;
	align-items: center;
	opacity: 0.7;
}
</style>