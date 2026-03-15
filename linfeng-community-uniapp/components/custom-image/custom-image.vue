<template>
	<view :style="[viewStyle]" :class="{'custom-image': true, 'image-round': round}" @click="onClick">
		<image v-if="!error" :src="src" :mode="mode" :lazy-load="lazyLoad" class="image" :show-menu-by-longpress="showMenuByLongpress"
		 @load="onLoaded" @error="onErrored"></image>
		<view v-if="loading && showLoading" class="loading-wrap image">
			<slot v-if="useLoadingSlot" name="loading"></slot>
			<u-icon color="#aaa" v-else name="photo-fill" size="45"></u-icon>
		</view>
		<view v-if="error && showError" class="error-wrap image">
			<slot v-if="useErrorSlot" name="error"></slot>
			<u-icon color="#aaa" v-else name="error-circle-fill" size="45"></u-icon>
			<text class="sm">加载失败</text>
		</view>
	</view>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
	src: {
		type: String,
		default: ''
	},
	round: {
		type: Boolean,
		default: false
	},
	width: {
		type: null
	},
	height: {
		type: null
	},
	radius: {
		type: null
	},
	lazyLoad: {
		type: Boolean,
		default: true
	},
	useErrorSlot: {
		type: Boolean,
		default: false
	},
	useLoadingSlot: {
		type: Boolean,
		default: false
	},
	showMenuByLongpress: {
		type: Boolean,
		default: false
	},
	mode: {
		type: String,
		default: 'scaleToFill'
	},
	showError: {
		type: Boolean,
		default: true
	},
	showLoading: {
		type: Boolean,
		default: true
	},
	customStyle: {
		type: Object,
		default: () => ({})
	}
})

const emit = defineEmits(['load', 'error', 'click'])

const error = ref(false)
const loading = ref(true)
const viewStyle = ref({})

onMounted(() => {
	setStyle()
})

function setStyle() {
	const { width, height, radius } = props
	let style = {}

	if (width) {
		style.width = width
	}

	if (height) {
		style.height = height
	}

	if (radius) {
		style['overflow'] = 'hidden'
		style['border-radius'] = radius
	}
	
	viewStyle.value = style
	
	if (props.customStyle) {
		viewStyle.value = Object.assign(viewStyle.value, props.customStyle)
	}
}

function onLoaded(event) {
	loading.value = false
	emit('load', event.detail)
}

function onErrored(event) {
	error.value = false
	loading.value = true
	emit('error', event.detail)
}

function onClick(event) {
	emit('click', event.detail)
}

watch(() => props.src, () => {
	error.value = false
	loading.value = true
})

watch(() => props.width, () => {
	setStyle()
})

watch(() => props.height, () => {
	setStyle()
})
</script>

<style lang="scss">
	.custom-image {
		position: relative;
		display: block;
		width: 100%;
		height: 100%;
		&.image-round {
			overflow: hidden;
			border-radius: 50%;
		}
		.image {
			display: block;
			width: 100%;
			height: 100%;
		}
		
		.loading-wrap,
		.error-wrap {
			position: absolute;
			top: 0;
			left: 0;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			color: #969799;
			font-size: 28rpx;
			background-color: #f7f8fa;
		}
	}
	.sm {
	    font-size: 26rpx;
	}
</style>
