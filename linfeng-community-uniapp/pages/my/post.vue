<template>
	<view>
		<view v-if="type&&type==3">
			<ArticleListComponent :list="postList"></ArticleListComponent>
		</view>
		<view v-else>
			<PostListComponent :list="postList" :loadStatus="loadStatus"></PostListComponent>
		</view>

		<!-- 返回顶部 -->
		<lf-back-top :show-tag="showTag"></lf-back-top>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { getCurrentInstance } from 'vue'
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app'
import PostListComponent from '@/components/post-list-common/post-list-common.vue'
import ArticleListComponent from '@/components/article-list/article-list.vue'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const postList = ref([])
const loadStatus = ref("loading")
const page = ref(1)
const type = ref('')
const showTag = ref(false)

onLoad((options) => {
	if (options.type) {
		type.value = options.type
		getPostListByType()
	} else {
		getPostList()
	}
})

// 监听页面滚动
onPageScroll((e) => {
	if (e.scrollTop > 750) {
		showTag.value = true
	} else {
		showTag.value = false
	}
})

// 监听页面触底事件
onReachBottom(() => {
	// 防止重复加载
	if (loadStatus.value === 'loading' || loadStatus.value === 'nomore') {
		return
	}
	
	if (type.value == '') {
		page.value++
		getPostList()
	} else {
		page.value++
		getPostListByType()
	}
})

function getPostListByType() {
	loadStatus.value = "loading"
	$H.get('post/getPostListByType', {
		page: page.value,
		type: type.value
	}).then(res => {
		const data = Array.isArray(res.result.data) ? res.result.data : []
		postList.value = postList.value.concat(data)
		if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
			loadStatus.value = "nomore"
		} else {
			loadStatus.value = "loadmore"
		}
	})
}

function getPostList() {
	loadStatus.value = "loading"
	$H.get('post/myPost', {
		page: page.value
	}).then(res => {
		const data = Array.isArray(res.result.data) ? res.result.data : []
		postList.value = postList.value.concat(data)
		if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
			loadStatus.value = "nomore"
		} else {
			loadStatus.value = "loadmore"
		}
	})
}
</script>

<style>
	page {
		background-color: #F5F5F5;
	}
</style>