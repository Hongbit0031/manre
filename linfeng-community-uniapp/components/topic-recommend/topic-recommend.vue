<template>
	<view class="recommend-box" :class="boxClass"
		:style="background ? 'background: url(' + background + ') no-repeat center; background-size: cover;' : ''">
		<view class="rec-head">
			<view class="title">{{title}}</view>
			<view class="more" @click="more()">更多</view>
		</view>
		<view class="recommend-scroll topic-wrap">
			<scroll-view class="wrap-scroller" scroll-x="true">
				<view v-for="(item, index) in items" :key="index" class="recommend-scroll-block" @click="goDetail(item.id)">
					<view class="recommend-scroll-cover">
						<image class="cover-image" mode="aspectFill" :src="item.coverImage"></image>
					</view>
					<view class="recommend-scroll-info">
						<view class="topic-name">{{item.topicName}}</view>
						<view class="user-count">{{item.userNum}}成员</view>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup>
	const props = defineProps({
		boxClass: {
			type: String,
			default: ""
		},
		title: {
			type: String,
			default: "热门圈子"
		},
		items: {
			type: Array,
			default: []
		},
		background: {
			type: String,
			default: ''
		}
	});
	
	function more() {
		uni.navigateTo({
			url: "/pages/topic/class-list"
		})
	}
	
	function goDetail(id) {
		uni.navigateTo({
			url: "/pages/topic/detail?id=" + id
		})
	}
</script>

<style>

	.recommend-box {
		padding: 20rpx 0;
		border-radius: 12rpx;
		background: #FFFFFF;
	}

	.recommend-box .rec-head {
		display: flex;
		flex-wrap: nowrap;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
		padding: 0 20rpx;
	}
	.title{
		font-size: 30rpx;
		font-weight: 600;
	}
	.more{
		font-size: 25rpx;
		font-weight: 300;
		color: #6a6a6a;
	}
	.recommend-scroll {
		white-space: nowrap;
	}

	.recommend-scroll-block {
		display: inline-block;
		position: relative;
		width: 42%;
		margin-right: 12rpx;
	}

	.recommend-scroll-block:first-child {
		margin-left: 20rpx;
	}

	.recommend-scroll-cover {
		position: relative;
		height: 240rpx;
		width: 100%;
	}

	.cover-image {
		height: 100%;
		width: 100%;
		border-radius: 8rpx;
	}

	.cover-text {
		position: absolute;
		top: 0;
		left: 30rpx;
		height: 48rp;
		line-height: 48rpx;
		padding: 0 24rpx;
		border-radius: 0 0 8rpx 8rpx;
		background: #010101;
		color: #FFFFFF;
		font-size: 22rpx;
		font-weight: 400;
	}

	.recommend-scroll-info {
		position: absolute;
		z-index: 3;
		bottom: 0;
		left: 0;
		width: 100%;
		background: linear-gradient(to bottom, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.7));
		padding: 40rpx 0 20rpx;
		border-radius: 0 0 12rpx 12rpx;
	}

	.topic-name, .user-count, .price-info  {
		color: #FFFFFF;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		padding: 0 30rpx;
	}

	.topic-name {
		font-size: 26rpx;
		font-weight: 500;
		height: 1.4em;
		line-height: 1.4em;
	}

	.user-count {
		font-size: 24rpx;
		font-weight: 300;
		height: 1.4em;
		line-height: 1.4em;
	}
	.topic-wrap {
		padding-bottom: 20rpx;
	}

	.topic-wrap .recommend-scroll-block {
		width: 31%;
	}

	.topic-wrap .wrap-scroller,
	.topic-wrap .recommend-scroll-cover {
		height: 220rpx;
	}
</style>
