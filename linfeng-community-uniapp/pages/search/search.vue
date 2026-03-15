<template>
	<view class="container">
		<u-search placeholder="搜索帖子动态/用户/圈子" :show-action="true" shape="square" border-color="#575757"
			margin="10rpx 30rpx 20rpx" search-icon="/static/images/more-search.png" v-model="keyword" @custom="onChange"
			@search="onChange"></u-search>
		<!-- tabs -->
		<view v-if="onSearchFlag">
			<view class="tabs-box">
				<u-tabs :list="tabList" :is-scroll="false" active-color="#000000" v-model="current"
					@change="tabChange"></u-tabs>
			</view>

			<view v-show="current === 0">
				<PostListComp :list="postList" :loadStatus="loadStatus"></PostListComp>
			</view>
			<view v-show="current === 1">
				<UserListComp :list="userList" :loadStatus="loadStatus"></UserListComp>
			</view>
			<view v-show="current === 2">
				<TopicListComp :list="topicList" :loadStatus="loadStatus" loadmoreBgColor="#fff"></TopicListComp>
			</view>
		</view>
		<view v-if="!onSearchFlag">
			<view class="empty-box">
				<view v-if="userSearch.length > 0" style="margin-right: auto;">
					<label>搜索记录</label>
					<image @click="deleteSearchByUId" style="width: 35rpx;height: 35rpx;margin-left: 450rpx;"
						src="/static/images/search-del.png" mode=""></image>
				</view>
				<view class="item-wrap">
					<view v-for="item in userSearch" :key="item.searchId">
						<view class="item" @click="historyKeyword(item.content)">
							{{ item.content }}
						</view>
					</view>
				</view>
				<view v-if="hotSearch.length > 0" style="margin-right: auto;margin-top: 20rpx;">
					<label>热门搜索</label>
				</view>
				<view class="item-wrap">
					<view class="item" v-for="(item, index) in hotSearch" :key="index">
						<view @click="historyKeyword(item)">{{ item }}</view>
					</view>
				</view>
			</view>
			<!-- 帖榜 -->
			<PostRecommendComp title="搜索发现" desc="热帖排行" :hotPost="hotPost"></PostRecommendComp>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance,
		onMounted
	} from 'vue'
	import PostListComp from '@/components/post-list/post-list.vue'
	import TopicListComp from '@/components/topic-list/topic-list.vue'
	import UserListComp from '@/components/user-list/user-list.vue'
	import PostRecommendComp from "@/components/post-recommend/post-recommend.vue"
	import {
		timeFormat
	} from '@/utils/filters.js'
	import {
		onReachBottom,
		onLoad
	} from '@dcloudio/uni-app'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H

	const keyword = ref('')
	const tabList = ref([{
			name: '动态'
		},
		{
			name: '用户'
		},
		{
			name: '圈子'
		}
	])
	const current = ref(0)
	const postList = ref([])
	const userList = ref([])
	const topicList = ref([])
	const pagePost = ref(1)
	const pageUser = ref(1)
	const topicPage = ref(1)
	const loadStatus = ref('loadmore')
	const hotSearch = ref([])
	const userSearch = ref([])
	const hotPost = ref([])
	const onSearchFlag = ref(false)

	// 页面加载
	onLoad(() => {
		getUserSearchHistory()
		getHotSearchHistory()
		getHotPost()
	})

	// 触底加载
	onReachBottom(() => {
		let type = current.value
		if (type === 0) {
			pagePost.value++
			getPostList()
		}

		if (type === 1) {
			pageUser.value++
			getUserList()
		}

		if (type === 2) {
			topicPage.value++
			getTopicList()
		}
	})

	function getHotPost() {
		$H.get('post/hotPost').then(res => {
			if (res.code == 0) {
				hotPost.value = res.result;
			}
		})
	}

	function onChange() {
		if (!uni.getStorageSync('hasLogin')) {
			proxy.$f.toast('请先登录哦')
			return;
		}
		onSearchFlag.value = true
		if (keyword.value) {
			let type = current.value

			pagePost.value = 1
			pageUser.value = 1
			topicPage.value = 1

			if (type === 0) {
				postList.value = []
				getPostList()
			}

			if (type === 1) {
				userList.value = []
				getUserList()
			}

			if (type === 2) {
				topicList.value = []
				getTopicList()
			}
		}
	}

	function getUserList() {
		loadStatus.value = 'loading'
		$H.get('user/search', {
			keyword: keyword.value,
			page: pageUser.value
		}).then(res => {
			if (res.code == 0) {
				userList.value = userList.value.concat(res.result.data)
				if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
					loadStatus.value = 'nomore'
				} else {
					loadStatus.value = 'loadmore'
				}
			}
		})
	}

	function getTopicList() {
		loadStatus.value = 'loading'
		$H.get('topic/search', {
			keyword: keyword.value,
			page: topicPage.value
		}).then(res => {
			if (res.code == 0) {
				topicList.value = topicList.value.concat(res.result.data)
				if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
					loadStatus.value = 'nomore'
				} else {
					loadStatus.value = 'loadmore'
				}
			}
		})
	}

	function getPostList() {
		loadStatus.value = 'loading'
		$H.get('post/search', {
			keyword: keyword.value,
			page: pagePost.value
		}).then(res => {
			if (res.code == 0) {
				postList.value = postList.value.concat(res.result.data)
				if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
					loadStatus.value = 'nomore'
				} else {
					loadStatus.value = 'loadmore'
				}
			}
		})
	}

	function tabChange(index) {
		current.value = index

		if (index === 0) {
			postList.value = []
			getPostList()
		}

		if (index === 1) {
			userList.value = []
			getUserList()
		}

		if (index === 2) {
			topicList.value = []
			getTopicList()
		}
	}

	// 删除所有历史搜索消息
	function deleteSearchByUId() {
		$H.post('search/deleteSearchByUId').then(res => {
			if (res.code == 0) {
				userSearch.value = []
			}
		})
	}

	// 获取热门搜索列表
	function getHotSearchHistory() {
		$H.get('search/getHotSearchHistory').then(res => {
			if (res.code == 0) {
				hotSearch.value = res.result
			}
		})
	}

	// 获取用户搜索历史列表
	function getUserSearchHistory() {
		$H.get('search/getUserSearchHistory').then(res => {
			if (res.code == 0) {
				userSearch.value = res.result
			}
		})
	}

	// 点击内容跳转搜索
	function historyKeyword(keyWord) {
		keyword.value = keyWord
		onChange()
	}
</script>

<style>
	page {
		background-color: #fff !important;
	}
</style>
<style lang="scss" scoped>
	@import 'search.scss';
</style>