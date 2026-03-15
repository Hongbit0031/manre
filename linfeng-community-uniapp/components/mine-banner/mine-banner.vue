<template>

	<view class="mini-banner mini-swiper">
		<swiper indicator-dots="true" autoplay="autoplay" circular="ture" class="mini-banner-group dots-left"
			indicator-color="rgba(255,255,255, 0.3)" indicator-active-color="rgba(255,255,255, 0.8)" interval="5000"
			duration="150" easing-function="linear">
			<swiper-item class="mini-banner-item" v-for="(item, index) in items" :key="index">
				<view class="mini-banner-block" @click="jump(item)">
					<view class="mini-banner-title" v-if="item.title">{{item.title}}</view>
					<image class="banner-img" mode="aspectFill" :src="item.img"></image>
				</view>
			</swiper-item>
		</swiper>
	</view>
</template>

<script setup>
	const props = defineProps({
		type: {
			type: String,
			default: ""
		},
		items: {
			type: Array,
			default: []
		}
	});
	
	function jump(link) {
		//跳转内部页面
		if (link.type == 3) {
			if (link.url == '/pages/index/index' || link.url == '/pages/square/square' || link.url ==
				'/pages/message/message' || link.url == '/pages/my/my') {
				uni.switchTab({
					url: link.url
				})
			} else {
				uni.navigateTo({
					url: link.url
				})
			}
		}
		//跳转外部页面
		if (link.type == 1) {
			// #ifdef MP-WEIXIN
			uni.navigateTo({
				url: '/pages/webview/webview?src=' + link.url
			});
			// #endif
		
			// #ifdef H5
			window.open(link.url)
			// #endif
			// #ifdef APP-PLUS
			plus.runtime.openURL(link.url)
			// #endif
		}
	}
</script>

<style>
	.mini-banner-group .wx-swiper-dot,
	.uni-swiper-dot {
		height: 4px;
		width: 4px;
		border-radius: 4px;
	}

	.mini-banner-group .wx-swiper-dot.wx-swiper-dot-active,
	.uni-swiper-dot-active {
		width: 12px;
		border-radius: 4px;
	}

	.dots-left .wx-swiper-dots.wx-swiper-dots-horizontal {
		width: 90%;
		text-align: left;
		padding-left: 0rpx;
		bottom: 30rpx;
	}

	.mini-banner {
		height: 360rpx;
		width: 100%;
	}

	.mini-banner .mini-banner-group,
	.mini-banner .mini-banner-item,
	.mini-banner-block,
	.banner-img {
		height: 100%;
		width: 100%;
	}

	.mini-banner .mini-banner-block {
		position: relative;
	}

	.mini-banner .mini-banner-block .mini-banner-title {
		position: absolute;
		height: 1em;
		line-height: 1em;
		left: 30rpx;
		bottom: 90rpx;
		font-size: 36rpx;
		font-weight: 600;
		color: #FFFFFF;
	}

	.banner-img {
		border-radius: 12rpx;
	}

	.mini-swiper {
		height: 160rpx;
		width: 100%;
	}

	.mini-swiper .mini-banner-block .mini-banner-title {
		display: none;
	}
</style>
