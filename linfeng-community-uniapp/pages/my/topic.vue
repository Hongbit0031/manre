<template>
	<view class="container">
		<!-- 顶部切换栏 -->
		<view class="tabs">
			<view 
				class="tab-item" 
				v-for="(item, index) in tabList" 
				:key="index"
				:class="{'active': activeTab === index}"
				@click="onTabChange(index)"
			>
				{{item.name}}
				<view class="tab-line" v-if="activeTab === index"></view>
			</view>
		</view>

		<TopicListComponent
			v-if="activeTab === 0"
			:list="topicList"
			:loadStatus="loadStatus"
		></TopicListComponent>
		<TopicListComponent
			v-else
			:list="joinTopicList"
			:loadStatus="loadStatus"
		></TopicListComponent>

		<!-- 创建圈子按钮 -->
		<view style="height: 120rpx;"></view>
		<view class="f-fixed">
			<u-button @click="jump" class="fixed-bottom" :custom-style="btnStyle" shape="circle">
				<u-icon name="plus"></u-icon>
				<text>创建圈子</text>
			</u-button>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { getCurrentInstance } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import TopicListComponent from '../../components/topic-list/topic-list.vue'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const tabList = ref([
	{ name: '创建的圈子' },
	{ name: '加入的圈子' }
])
const activeTab = ref(0)
const btnStyle = ref({
	color: "#fff",
	backgroundColor: '#343434'
})
const topicList = ref([]) // 我创建的圈子
const joinTopicList = ref([]) // 我加入的圈子
const loadStatus = ref("loading")
const page = ref(1)

onLoad(() => {
	getMyTopic()
})

// 监听页面触底事件
onReachBottom(() => {
	if (activeTab.value === 0) {
		page.value++
		getMyTopic()
	} else {
		page.value++
		getJoinTopic()
	}
})

function onTabChange(index) {
	activeTab.value = index
	page.value = 1
	if (index === 0) {
		topicList.value = []
		getMyTopic()
	} else {
		joinTopicList.value = []
		getJoinTopic()
	}
}

function getMyTopic() {
	loadStatus.value = "loading"
	$H.get("topic/myCreateTopic", {
		page: page.value
	}).then(res => {
		const data = Array.isArray(res.result.data) ? res.result.data : []
		topicList.value = topicList.value.concat(data)
		if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
			loadStatus.value = "nomore"
		} else {
			loadStatus.value = "loadmore"
		}
	})
}

function getJoinTopic() {
	loadStatus.value = "loading"
	$H.post("topic/userJoinTopic", {
		page: page.value
	}).then(res => {
		const data = Array.isArray(res.result.data) ? res.result.data : []
		joinTopicList.value = joinTopicList.value.concat(data)
		if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
			loadStatus.value = "nomore"
		} else {
			loadStatus.value = "loadmore"
		}
	})
}

function jump() {
	uni.navigateTo({
		url: '/pages/topic/add/add'
	})
}
</script>

<style lang="scss">
	page {
		background-color: #f5f5f5;
	}
	
	.tabs {
		background-color: #fff;
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 20rpx 0;
		position: relative;
		
		.tab-item {
			position: relative;
			padding: 0 30rpx;
			font-size: 28rpx;
			color: #666;
			
			&.active {
				color: #343434;
				font-weight: bold;
				
				.tab-line {
					position: absolute;
					bottom: -18rpx;
					left: 50%;
					transform: translateX(-50%);
					width: 40rpx;
					height: 4rpx;
					background-color: #343434;
					border-radius: 2rpx;
				}
			}
		}
	}
</style>
