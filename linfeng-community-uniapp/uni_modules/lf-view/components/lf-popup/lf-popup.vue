<template>
	<view>
		<view @click="onMask" v-show="modelValue" class="mask"></view>
		<view v-show="modelValue" :style="{ height: height }" class="popup-wrap"><slot></slot></view>
	</view>
</template>

<script setup>
import { computed } from 'vue'

// Props定义
const props = defineProps({
	modelValue: {
		type: Boolean,
		default: false
	},
	height: {
		type: String,
		default: 'auto'
	}
})

// Emits定义
const emit = defineEmits(['update:modelValue'])

// 计算属性
const value = computed({
	get: () => props.modelValue,
	set: (val) => emit('update:modelValue', val)
})

// 方法
const onMask = () => {
	let show = value.value
	if (show) {
		show = false
	} else {
		show = true
	}
	
	emit('update:modelValue', show)
}
</script>

<style lang="scss" scoped>
.mask {
	position: fixed;
	top: 0;
	bottom: 0;
	left: 0;
	right: 0;
	z-index: 99;
	background-color: rgba(0, 0, 0, 0.3);
}
.popup-wrap {
	width: 100%;
	position: fixed;
	bottom: 0;
	background-color: #ffffff;
	animation-name: mymove;
	animation-duration: 0.3s;
	border-radius: 20rpx 20rpx 0 0;
	z-index: 999999;
}

@keyframes mymove {
	from {
		bottom: -50vh;
	}
	to {
		bottom: 0;
	}
}
</style>
