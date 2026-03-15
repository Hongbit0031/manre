<template>
	<view>
		<PostListComponent :list="postList" :loadStatus="loadStatus"></PostListComponent>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { getCurrentInstance } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import PostListComponent from '../../components/post-list/post-list.vue'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const postList = ref([])
const loadStatus = ref("loading")
const page = ref(1)

onLoad(() => {
	getPostList()
})

// 监听页面触底事件
onReachBottom(() => {
	page.value++
	getPostList()
})

function getPostList() {
	loadStatus.value = "loading"
	$H.get('post/myCollectPost', {
		page: page.value
	}).then(res => {
		if (res.result.data) {
			postList.value = postList.value.concat(res.result.data)
			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = "nomore"
			} else {
				loadStatus.value = "loadmore"
			}
		} else {
			loadStatus.value = "nomore"
		}
	})
}
</script>

<style lang="scss">
	page {
		background-color: #f5f5f5;
	}
</style>
