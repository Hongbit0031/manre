<template>
	<view>
		<button class="reset-button button" @click="onClick" hover-class="button-hover" :class="[shape == 'circle' ? 'round-circle' : '']" :style="{ 'background-color': color}">
			<slot></slot>
		</button>
	</view>
</template>

<script setup>
// Props定义
const props = defineProps({
	color: {
		type: String,
		default: '#95acff'
	},
	shape: {
		type: String,
		default: 'circle'
	}
})

// Emits定义
const emit = defineEmits(['click'])

// 方法
const onClick = () => {
	emit("click", '')
}
</script>

<style lang="scss" scoped>
// 去除button的所有默认样式
.reset-button {
	padding: 0;
	font-size: inherit;
	line-height: inherit;
	background-color: transparent;
	color: inherit;
}

.reset-button::after {
	border: none;
}

// button样式
.button {
	color: white;
	display: block;
	padding: 20rpx;
	margin: 20rpx;
}

.button-hover {
	background-color: #ffffff !important;
	color: white;
}

.round-circle {
	border-radius: 100rpx;
}
</style>
