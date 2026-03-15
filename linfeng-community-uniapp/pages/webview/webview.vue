<template>
	<view>
		<web-view :src="src"></web-view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const src = ref("")

onLoad((options) => {
	if (options.src) {
		src.value = options.src
	}
})
</script>

<style lang="scss">

</style>
