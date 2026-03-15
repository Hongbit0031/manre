<template>
	<view>
		<view class="u-search-box">
			<u-search placeholder="输入名称搜索圈子" v-model="keyword" @change="getTopicList" :show-action="false"></u-search>
		</view>
		<u-grid :col="3" :border="false" @click="toTopic">
			<u-grid-item :index="item2.id" v-for="(item2, index2) in topicList" :key="index2">
				<u-image border-radius="10" width="200rpx" height="260rpx" :src="item2.cover_image"></u-image>
				<view class="grid-text">{{item2.topic_name}}</view>
			</u-grid-item>
		</u-grid>
		<!-- 加载状态 -->
		<block v-if="topicList.length === 0 && loadStatus == 'nomore'">
			<u-empty text="暂无相关圈子" mode="favor"></u-empty>
		</block>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const current = ref(0)
const topicClssList = ref([])
const topicList = ref([])
const loadStatus = ref('nomore')
const keyword = ref('')

onLoad((options) => {
	keyword.value = options.keyword
})

function getTopicList() {
	loadStatus.value = 'loading'
	topicList.value = []
	$H.get('topic/list', {
		keyword: keyword.value
	}).then(res => {
		topicList.value = topicList.value.concat(res.result.data)
		if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
			loadStatus.value = 'nomore'
		} else {
			loadStatus.value = 'loadmore'
		}
	})
}

function toTopic(id) {
	uni.navigateTo({
		url: '/pages/topic/detail?id=' + id
	})
}
</script>

<style lang="scss" scoped>
	.u-search-box{
		padding: 30rpx 20rpx;
	}
	.swiper{
		height: calc(100vh - 80rpx);
	}
	
	.grid-text{
		margin-top: 20rpx;
		font-size: 12px;
	}
</style>
