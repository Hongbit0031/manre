<template>
	<view class="article-list" v-if="list.length > 0">
		<view 
			@click="jumpArticle(item.id)"
			class="article-item" 
			v-for="(item, index) in list" 
			:key="index" 
			hover-class="article-item-hover"
		>
			<view class="article-content">
				<view class="article-text">
					<view class="article-title">{{ item.title }}</view>
					<view class="article-meta">
						<view class="author-info" @click.stop="jumpUser(item.userInfo.uid)">
							<view class="author-name" v-if="item.userInfo.username">{{ item.userInfo.username }}</view>
							<view class="publish-time">{{ timeFormat(item.createTime) }}</view>
						</view>
						
						<view class="view-count">
							<image src="/static/images/view.png"></image>
							<text>{{ item.readCount }}</text>
						</view>
					</view>
				</view>
				<view class="article-image">
					<image :src="item.media[0]" mode="aspectFill"></image>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { timeFormat } from "@/utils/filters.js"

defineProps({
	list: {
		type: Array,
		default: () => []
	}
})

function jumpArticle(id) {
	uni.navigateTo({
		url: '/subpages/content/article/article?id=' + id
	})
}

function jumpUser(uid) {
	uni.navigateTo({
		url: '/pages/user/home?uid=' + uid
	})
}
</script>

<style lang="scss">
.article-list {
	background-color: #ffffff;
	border-radius: 16rpx;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	margin: 20rpx;
	
	.article-item {
		padding: 30rpx;
		position: relative;
		transition: all 0.2s ease;
		
		&:not(:last-child)::after {
			content: '';
			position: absolute;
			left: 30rpx;
			right: 30rpx;
			bottom: 0;
			height: 1px;
			background: #f0f2f5;
		}
		
		&-hover {
			background-color: #f9fafc;
		}
		
		.article-content {
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			align-items: center;
			
			.article-text {
				flex: 1;
				margin-right: 30rpx;
				
				.article-title {
					font-size: 32rpx;
					font-weight: 500;
					color: #333333;
					line-height: 1.5;
					display: -webkit-box;
					text-overflow: ellipsis;
					word-break: break-all;
					-webkit-line-clamp: 2;
					-webkit-box-orient: vertical;
					overflow: hidden;
					margin-bottom: 16rpx;
				}
				
				.article-meta {
					display: flex;
					align-items: center;
					justify-content: space-between;
					
					.author-info {
						display: flex;
						align-items: center;
						
						.author-name {
							font-size: 26rpx;
							color: #5c6bc0;
							margin-right: 16rpx;
							max-width: 200rpx;
							overflow: hidden;
							text-overflow: ellipsis;
							white-space: nowrap;
						}
						
						.publish-time {
							font-size: 24rpx;
							color: #999999;
						}
					}
					
					.view-count {
						display: flex;
						align-items: center;
						
						image {
							width: 32rpx;
							height: 32rpx;
							margin-right: 8rpx;
						}
						
						text {
							font-size: 24rpx;
							color: #999999;
						}
					}
				}
			}
			
			.article-image {
				width: 220rpx;
				height: 160rpx;
				border-radius: 12rpx;
				overflow: hidden;
				box-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.1);
				
				image {
					width: 100%;
					height: 100%;
					transition: transform 0.3s ease;
				}
			}
		}
	}
}
</style>
