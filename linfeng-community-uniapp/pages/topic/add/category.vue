<template>
	<view class="container">
		<view class="title">选择圈子类目</view>
		<view class="class-wrap">
			<view class="class-item u-line-1" @click="chooseClass(item.cateId,item.cateName)" v-for="(item, index) in classList" :key="index">{{ item.cateName }}</view>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const classList = ref([])

onLoad(() => {
	getCatList()
})

function getCatList() {
	$H.get('topic/classList').then(res => {
		classList.value = res.result
	})
}

function chooseClass(id, name) {
	// 使用事件总线传递数据
	uni.$emit('selectCategory', {
		cateId: id,
		cateName: name
	})
	uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.title {
	margin-bottom: 30rpx;
}
.class-wrap {
	.class-item {
		width: 30%;
		display: inline-block;
		border: 1px solid #999;
		padding: 20rpx;
		font-size: 24rpx;
		color: #999;
		text-align: center;
		margin-bottom: 20rpx;
		border-radius: 10rpx;
		&:nth-child(3n + 2) {
			margin-left: 5%;
			margin-right: 5%;
		}
		
		&:active{
			background-color: #333;
			color: #fff;
		}
	}
}
</style>
