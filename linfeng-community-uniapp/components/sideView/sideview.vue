<template>
	<view>
		<view style="height: 350rpx;">
			<image v-if="!loginUserInfo.bgImg" class="background" mode="aspectFill" style="" :src="$IMG+'/images/user-bg.png'"></image>
			<image v-if="loginUserInfo.bgImg" class="background" mode="aspectFill" style="" :src="loginUserInfo.bgImg"></image>
			<view style="position: relative;">
				<view class="top-container">
					<view class="avatar-container" >
						<image class="avatar" :src="loginUserInfo.avatar"></image>
					</view>
					<view style="margin-left: 10rpx;padding: 5rpx;display: block;">
						<view class="name">{{loginUserInfo.username}}</view>
						<view v-show="isSocketOpen" style="font-size: 25rpx;color:#00C957;">在线</view>
						<view v-show="!isSocketOpen" @click="refresh()" style="font-size: 25rpx;color: red;">离线</view>
					</view>
				</view>
			</view>
		</view>
		<view>
			<view class="item" @click="$f.jump('/pages/index/index',4)">
				<image class="icon" :src="$IMG+'/im/index.png'"></image>
				<view class="text">首页</view>
			</view>
			<view class="item" @click="$f.jump('/pages/square/square',4)">
				<image class="icon" :src="$IMG+'/im/square.png'"></image>
				<view class="text">广场中心</view>
			</view>
			<view class="item" @click="$f.jump('/pages/user/home')">
				<image class="icon" :src="$IMG+'/im/mine.png'"></image>
				<view class="text">我的主页</view>
			</view>
			<view class="item" @click="$f.jump('/pages/message/message',4)">
				<image class="icon" :src="$IMG+'/im/friend.png'"></image>
				<view class="text">消息列表</view>
			</view>
			<!-- <view class="item" @click="$f.jump('/pages/im/chat-list/chat-list')">
				<image class="icon" :src="$IMG+'/im/chat.png'"></image>
				<view class="text">会话列表</view>
			</view> -->
			<view class="item" @click="$f.jump('/pages/im/notice-list/notice-list')">
				<image class="icon" :src="$IMG+'/im/sq.png'"></image>
				<view class="text">申请通知</view>
			</view>
			<view class="item" @click="$f.jump('/pages/user/edit-info/setting')">
				<image class="icon" :src="$IMG+'/im/setting.png'"></image>
				<view class="text">系统设置</view>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, computed, getCurrentInstance } from 'vue'
	import { useStore } from 'vuex'
	
	const { proxy } = getCurrentInstance();
	const $IMG = ref(proxy.$IMG);
	const store = useStore();
	
	const loginUserInfo = computed(() => store.getters.loginUserInfo);
	const isSocketOpen = computed(() => store.getters.isSocketOpen);
	
	function refresh() {
		//连接websocket
		// websocket.initConnect();
		///获取通知消息
		store.dispatch('getNoticeList');
		//获取好友列表
		store.dispatch('getFriendList');
	}
</script>

<style lang="scss" scoped>
	.item{
		display: flex;
		margin-top: 50rpx;
		margin-left: 20rpx;
		.icon{
			width: 40rpx;
			height: 40rpx;
			margin-right: 20rpx;
		}
		.text{
			text-align: center;
			line-height: 40rpx;
		}
	}
	
	.background{
		position: fixed;
		z-index: -1;
		height: 350rpx;
		width: 100%;
		top: -1rpx;
	}
	.top-container{
		position: absolute;
		top: 220rpx;
		left: 20rpx;
		z-index: 10;
		display: flex;
		.avatar-container{
			width: 102rpx;
			height: 102rpx;
			padding: 1rpx;
			background-color: #fff;
			border-radius: 50%;
			text-align: center;
			.avatar{
				width: 100rpx;
				height: 100rpx;
				border-radius: 50%;
			}
		}
		.name{
			font-size: 40rpx;
			color: #fff;
			font-family: "Cambria, Cochin, Georgia, Times, 'Times New Roman', serif";
			word-break: break-all;
		}
	}


</style>