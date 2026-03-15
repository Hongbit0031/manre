<template>
	<view class="container">
		<view
			class="topic-item"
			@click="chooseTopic(item.id, item.topicName)"
			v-for="(item, index) in topicList"
			:key="index"
		>
			<view class="topic-header">
				<u-image class="cover-img" width="80rpx" height="80rpx" border-radius="50%" :src="item.coverImage"></u-image>
				<view class="topic-info">
					<view class="topic-name">{{ item.topicName }}</view>
					<view class="topic-desc">{{ item.description }}</view>
				</view>
			</view>
			
			<view class="topic-footer">
				<view class="footer-item">
					<u-icon name="account" size="28rpx" color="#000000"></u-icon>
					<text>{{ numberFormat(item.userCount) }}位成员</text>
				</view>
				<view class="footer-item">
					<u-icon name="file-text" size="28rpx" color="#000000"></u-icon>
					<text>{{ item.postCount }} 篇内容</text>
				</view>
				<view class="footer-item">
					<u-icon name="account-fill" size="28rpx" color="#000000"></u-icon>
					<text>圈主:{{ item.userInfo.username || '未知' }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import { numberFormat } from '@/utils/filters.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const topicList = ref([])
const loadStatus = ref('loadmore')
const page = ref(1)
const fromPage = ref('') // 记录来源页面

onLoad((options) => {
	fromPage.value = options.from || '' // 获取来源页面标识
	getTopicList()
})

onReachBottom(() => {
	page.value++
	getTopicList()
})

function getTopicList() {
	$H.post('topic/userJoinTopic', {
		page: page.value
	}).then(res => {
		topicList.value = topicList.value.concat(res.result.data)
		if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
			loadStatus.value = 'nomore'
		} else {
			loadStatus.value = 'loadmore'
		}
	})
}

function chooseTopic(id, name) {
	// 根据来源页面发送不同的事件
	if (fromPage.value === 'vote') {
		// 如果来自投票页面
		uni.$emit('chooseTopicForVote', { id, name })
	} else if (fromPage.value === 'article') {
		// 如果来自长文页面
		uni.$emit('chooseTopicForArticle', { id, name })
	} else if (fromPage.value === 'post') {
		// 如果来自帖子发布页面
		uni.$emit('chooseTopicForPost', { id, name })
	} else {
		// 默认情况，尝试使用旧方法（兼容其他页面）
		let pages = getCurrentPages()
		let prevPage = pages[pages.length - 2]
		if (prevPage?.$vm) {
			// Vue2 方式
			if (prevPage.$vm.form) {
				prevPage.$vm.form.topicId = id
			}
			if (prevPage.$vm.topicName !== undefined) {
				prevPage.$vm.topicName = name
			}
			if (prevPage.$vm.form && prevPage.$vm.form.discussId !== undefined) {
				prevPage.$vm.form.discussId = ''
			}
			if (prevPage.$vm.disName !== undefined) {
				prevPage.$vm.disName = '选择话题'
			}
		}
	}
	uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.container {
	padding: 20rpx;
	background-color: #f5f5f5;
}

.topic-item {
	background-color: #ffffff;
	padding: 30rpx;
	margin-bottom: 20rpx;
	border-radius: 12rpx;
	
	.topic-header {
		display: flex;
		align-items: flex-start;
		margin-bottom: 20rpx;
		
		.cover-img {
			margin-right: 20rpx;
			flex-shrink: 0;
		}
		
		.topic-info {
			flex: 1;
			overflow: hidden;
			
			.topic-name {
				font-size: 32rpx;
				font-weight: 700;
				color: #000000;
				margin-bottom: 8rpx;
			}
			
			.topic-desc {
				font-size: 26rpx;
				color: #666;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}
		}
	}
	
	.topic-footer {
		display: flex;
		align-items: center;
		justify-content: space-between;
		font-size: 24rpx;
		color: #000000;
		
		.footer-item {
			display: flex;
			align-items: center;
			flex: 1;
			justify-content: center;
			overflow: hidden;
			white-space: nowrap;
			
			::v-deep .u-icon {
				margin-right: 8rpx;
				flex-shrink: 0;
			}
			
			text {
				font-size: 24rpx;
				color: #000000;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}
		}
	}
}
</style>
