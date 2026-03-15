<template>
	<view class="report-container">
		<u-tabs :list="tabsList" :is-scroll="false" active-color="#5c6bc0" inactive-color="#909399" 
			:current="current" @change="tabChange" bar-width="50" bar-height="6"></u-tabs>
		
		<!-- 报告列表 -->
		<view class="report-list">
			<view v-for="(item, index) in reportList" :key="index">
				<view class="report-card">
					<view class="report-card__header">
						<view class="report-card__date">{{ item.createTime }}</view>
						<view class="report-card__status" 
							:class="{
								'status--pending': item.status==0,
								'status--processed': item.status==1,
								'status--rejected': item.status==2
							}">
							<text>{{ item.status==0 ? '待审核' : item.status==1 ? '已处理' : '已拒绝' }}</text>
						</view>
					</view>
					<view class="report-card__content" @click="goDetail(item.id)" hover-class="none">
						<image class="report-card__image" mode="aspectFill" :src="item.media[0]"></image>
						<view class="report-card__info">
							<view class="report-card__text">{{ item.content.substring(0, 25) }}...</view>
							<view class="report-card__type">
								<text v-if="item.type==1">帖子</text>
								<text v-else-if="item.type==2">评论</text>
								<text v-else-if="item.type==3">用户</text>
								<text v-else-if="item.type==4">圈子</text>
							</view>
						</view>
					</view>
					<view class="report-card__footer">
						<text class="report-card__id">单号: {{ item.id }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 加载状态 -->
		<view v-if="loadStatus == 'nomore' && reportList.length === 0" class="empty-state">
			<u-empty text="暂无举报数据" mode="favor"></u-empty>
		</view>
		<view v-else class="loadmore">
			<u-loadmore bg-color="#f7f8fb" color="#5c6bc0" :status="loadStatus" />
		</view>
		
		<!-- 悬浮添加按钮 -->
		<view @click="goReportAdd" class="float-btn">
			<u-icon name="plus" color="#ffffff" size="24"></u-icon>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'

// 获取当前实例
const { proxy } = getCurrentInstance()

// 响应式数据
const loadStatus = ref('loadmore')
const tabsList = ref([
	{
		name: '全部'
	},
	{
		name: '待审核'
	},
	{
		name: '已处理'
	},
	{
		name: '已拒绝'
	}
])
const current = ref(0)
const reportList = ref([])
const page = ref(1)

// 方法
const goReportAdd = () => {
	uni.navigateTo({
		url: "/pages/report/report"
	})
}

const goDetail = (id) => {
	uni.navigateTo({
		url: '/pages/report/detail?id=' + id
	})
}

const tabChange = (index) => {
	current.value = index
	reportList.value = []
	page.value = 1
	getReportList()
}

const getReportList = () => {
	loadStatus.value = 'loading'

	let status = current.value - 1

	proxy.$H
		.get('report/list', {
			page: page.value,
			status: status
		})
		.then(res => {
			reportList.value = reportList.value.concat(res.result.data)
			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = 'nomore'
			}
		})
}

// 生命周期
onLoad((options) => {
	getReportList()
})

onReachBottom(() => {
	page.value++
	getReportList()
})
</script>

<style>
page {
	background-color: #f7f8fb;
}
</style>

<style lang="scss" scoped>
.report-container {
	padding: 0 0 120rpx;
	min-height: 100vh;
	box-sizing: border-box;
}

.report-list {
	padding: 20rpx;
}

.report-card {
	background-color: #ffffff;
	border-radius: 16rpx;
	margin-bottom: 24rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	transition: all 0.3s;
	
	&:active {
		transform: scale(0.98);
	}
	
	&__header {
		padding: 24rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		border-bottom: 1px solid #f5f7fa;
	}
	
	&__date {
		font-size: 24rpx;
		color: #909399;
	}
	
	&__status {
		padding: 6rpx 16rpx;
		border-radius: 30rpx;
		font-size: 24rpx;
		
		&.status--pending {
			background-color: rgba(255, 170, 0, 0.1);
			color: #ffaa00;
		}
		
		&.status--processed {
			background-color: rgba(0, 170, 0, 0.1);
			color: #00aa00;
		}
		
		&.status--rejected {
			background-color: rgba(255, 0, 0, 0.1);
			color: #ff0000;
		}
	}
	
	&__content {
		padding: 24rpx;
		display: flex;
		align-items: center;
		border-bottom: 1px solid #f5f7fa;
	}
	
	&__image {
		width: 140rpx;
		height: 140rpx;
		border-radius: 12rpx;
		margin-right: 24rpx;
		background-color: #f5f7fa;
	}
	
	&__info {
		flex: 1;
	}
	
	&__text {
		font-size: 28rpx;
		color: #303133;
		margin-bottom: 16rpx;
		line-height: 1.4;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
	}
	
	&__type {
		display: inline-block;
		background-color: rgba(92, 107, 192, 0.1);
		padding: 4rpx 16rpx;
		border-radius: 20rpx;
		
		text {
			font-size: 24rpx;
			color: #5c6bc0;
		}
	}
	
	&__footer {
		padding: 20rpx 24rpx;
		display: flex;
		justify-content: flex-end;
	}
	
	&__id {
		font-size: 24rpx;
		color: #909399;
	}
}

.empty-state {
	padding-top: 30%;
}

.loadmore {
	padding: 20rpx 0 40rpx;
}

.float-btn {
	position: fixed;
	right: 30rpx;
	bottom: 100rpx;
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	background: linear-gradient(135deg, #5c6bc0 0%, #3f51b5 100%);
	box-shadow: 0 6rpx 20rpx rgba(92, 107, 192, 0.4);
	display: flex;
	justify-content: center;
	align-items: center;
	transition: all 0.3s;
	z-index: 99;
	
	&:active {
		transform: scale(0.9);
	}
}
</style>
