<template>
	<view class="container">
		<block v-for="(item,index) in list" :key="index">
			<view @click="jumpTopic(item.id)">
				<view class="topic-item">
					<view class="topic-header">
						<u-image class="cover-img" width="80rpx" height="80rpx" border-radius="50%" :src="item.coverImage"></u-image>
						<view class="topic-info">
							<view class="topic-name">{{item.topicName}}</view>
							<view class="topic-desc">{{item.description}}</view>
						</view>
					</view>
					
					<view class="topic-footer">
						<view class="footer-item">
							<u-icon name="account" size="28rpx" color="#000000"></u-icon>
							<text>{{numberFormat(item.userCount)}}位成员</text>
						</view>
						<view class="footer-item">
							<u-icon name="file-text" size="28rpx" color="#000000"></u-icon>
							<text>{{item.postCount}} 篇内容</text>
						</view>
						<view class="footer-item">
							<u-icon name="account-fill" size="28rpx" color="#000000"></u-icon>
							<text>圈主:{{item.userInfo.username || '未知'}}</text>
						</view>
					</view>
				</view>
			</view>
		</block>
		<!-- 加载状态 -->
		<block v-if="loadStatus != 'none'">
			<block v-if="list.length > 0">
				<view style="margin: 30rpx;">
					<u-loadmore :status="loadStatus" bgColor="#f5f5f5" />
				</view>
			</block>
			<block v-else>
				<u-empty margin-top="100" text="暂无相关圈子" mode="favor"></u-empty>
			</block>
		</block>
	</view>
</template>

<script setup>
import { numberFormat } from '@/utils/filters.js'

defineProps({
	list: {
		type: Array,
		default: () => []
	},
	loadStatus: {
		type: String,
		default: 'loadmore'
	}
})

function jumpTopic(id) {
	uni.navigateTo({
		url: '/pages/topic/detail?id=' + id
	})
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
