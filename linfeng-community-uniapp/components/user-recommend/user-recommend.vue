<template>
	<view>
		<!-- 横向滚动用户列表-->
		<view v-if="type=='scroll'">
			<view class="recommend-title">
				<view>{{title}}</view>
				<view>滑动查看</view>
			</view>
			<view class="recommend-box">
				<scroll-view scroll-x scroll-with-animation scroll-left="scrollLeft" show-scrollbar="false">
					<!-- 用户基础信息块 -->
					<view v-for="(user, index) in users" :key="index"
						@click="goUserHome('/pages/user/home?uid=' + user.uid)" class="user-recommend-list">
						<view class="user-avatar">
							<image mode="aspectFill" :src="user.avatar"></image>
							<image v-if="user.vip==1" mode="aspectFill"
								src="../../static/vip/vipIcon.png"></image>
						</view>
						<view class="user-name">{{user.username}}</view>
						<view class="user-info">
						<text>作品 {{numberFormat(user.postNum)}}</text>/
						<text>粉丝 {{numberFormat(user.fans)}}</text>
						</view>
						<view v-if="needButton" class="watch-box">
							<view v-if="user.isFollow" class="active" @click.stop="cancelFollow(user.uid,index)">已关注</view>
							<view v-if="!user.isFollow" @click.stop="follow(user.uid,index)">+ 关注</view>
						</view>
					</view>
				</scroll-view>
			</view>

		</view>

		<!-- 两组一行 关注列表-->
		<view v-else-if="type=='wrap'">
			<view class="box-row">
				<view v-for="(user, index) in users" :key="index" class="user-recommend-list">
					<!-- 用户基础信息块 -->
					<view class="user-avatar">
						<image mode="aspectFill" :src="user.avatar"></image>
						<image v-if="user.vip==1" mode="aspectFill" src="../../static/vip/vipIcon.png"></image>
					</view>
					<view class="user-name">{{user.username}}</view>
					<view class="user-info">
					<text>作品 {{numberFormat(user.postNum)}}</text>
					<text>粉丝 {{numberFormat(user.fans)}}</text>
					</view>
					<view v-if="needButton" class="watch-box">
						<view v-if="user.isFollow" class="active" @click.stop="cancelFollow(user.uid,index)">已关注</view>
						<view v-if="!user.isFollow" @click.stop="follow(user.uid,index)">+ 关注</view>
					</view>
				</view>
			</view>
		</view>


	</view>
</template>

<script setup>
	import { numberFormat } from "@/utils/filters.js"

	const props = defineProps({
			type: {
				type: String,
				default: "scroll"
			},
			extClass: {
				type: String,
				default: ""
			},
			title: {
				type: String,
				default: "热门博主"
			},
			users: {
				type: Array,
			default: []
			},
			needButton: {
				type: Boolean,
			default: true
			}
	});
	
	const emit = defineEmits(['follow', 'cancelFollow']);
	
	function follow(id, index) {
		emit('follow', id, index)
	}
	
	function cancelFollow(id, index) {
		emit('cancelFollow', id, index)
	}
	
	function goUserHome(url) {
				uni.navigateTo({
			url: url
				})
	}
</script>

<style scoped="less">
.recommend-title {
	display: flex;
	flex-wrap: nowrap;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 10rpx;
	padding: 0 20rpx;
}

.recommend-title view:nth-child(1) {
	font-size: 30rpx;
	font-weight: 600;
}

.recommend-title view:nth-child(2) {
	font-size: 26rpx;
	font-weight: 400;
	color: #999999;
}
.recommend-box scroll-view {
	padding: 10rpx 0 20rpx;
	white-space: nowrap;
}

.recommend-box scroll-view .user-recommend-list {
	width: 230rpx;
}

.box-row {
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
}

.box-row .user-recommend-list {
	width: 240rpx;
	width: 48.4%;
	margin-right: 0;
	margin-bottom: 20rpx;
}
.user-avatar {
	position: relative;
}
.user-recommend-list {
	text-align: center;
	line-height: 1.8rem;
	padding: 30rpx 0;
	position: relative;
	margin-right: 14rpx;
	margin-bottom: 4rpx;
	display: inline-block;
	border-radius: 12rpx;
	background-color: #FFFFFF;
}
.user-avatar image:nth-child(1) {
	height: 72rpx;
	width: 72rpx;
	border-radius: 50%;
	margin-bottom: -8rpx;
}

.user-avatar image:nth-child(2) {
	/* height: 32rpx;
	width: 32rpx; */
	width: 66rpx;
	height: 25rpx;
	position: absolute;
	bottom: 8rpx;
	margin-left: -20rpx;
}

.user-name {
	font-size: 28rpx;
	font-weight: 500;
	height: 1.6rem;
	line-height: 1.6rem;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.user-info {
	font-size: 22rpx;
	height: 2.2rem;
	line-height: 2.2rem;
}

.user-info text {
	margin: 0 10rpx;
	font-weight: 300;
}

.watch-box {
	display: flex;
	justify-content: center;
}

.watch-box view {
	font-size: 24rpx;
	font-weight: 300;
	color: #FFFFFF;
	text-align: center;
	border-radius: 38rpx;
	height: 50rpx;
	line-height: 50rpx;
	padding: 0 24rpx;
	background: #010101;
	word-break: keep-all;
}

.watch-box view.active {
	background-color: #f3f3f3;
	color: #555555;
}

.box-row {
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
}

.box-row .user-recommend-list {
	width: 240rpx;
	width: 48.4%;
	margin-right: 0;
	margin-bottom: 20rpx;
}
</style>

