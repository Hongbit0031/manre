<template>

	<view class="user-list-box">
		<view class="block">
			<view class="title-txt">
				<view>圈内成员</view>
				<view @click="jump('/pages/topic/topic-user?id='+ topicId)">查看更多</view>
			</view>
			<view class="scroll-user">
				<scroll-view scroll-x="true">
					<view v-for="(user, index) in users" :key="index" @click="jump('/pages/user/home?uid=' + user.uid)" class="wrap-user">
						<view class="user-avatar">
							<image mode="aspectFill" :src="user.avatar"></image>
						</view>
						<view class="user-name">{{user.username}}</view>
					</view>
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script setup>
defineProps({
	prop: {
		type: String,
		default: "prop"
	},
	users: {
		type: Array,
		default: () => []
	},
	topicId: {
		type: Number,
		default: 0
	}
})

const jump = (url) => {
	uni.navigateTo({
		url: url
	})
}
</script>

<style>
	.user-list-box {
		padding: 0 20rpx;
		background-color: #fff;
	}

	.scroll-user {
		white-space: nowrap;
		height: 140rpx;
	}

	.title-txt {
		display: flex;
		flex-wrap: nowrap;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.title-txt view:nth-child(1) {
		font-size: 30rpx;
		font-weight: 600;
	}

	.title-txt view:nth-child(2) {
		font-size: 26rpx;
		font-weight: 400;
		color: #999999;
	}

	.wrap-user {
		display: inline-block;
		text-align: center;
		width: 23%;
	}

	.user-avatar {
		position: relative;
	}

	.user-avatar image:nth-child(1) {
		height: 72rpx;
		width: 72rpx;
		border-radius: 50%;
		margin-bottom: -8rpx;
	}

	.user-avatar image:nth-child(2) {
		height: 32rpx;
		width: 32rpx;
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
</style>
