<template>
	<view >
		<view class="hot-post" v-if="hotPost.length>0">
			<view class="hot-post-head">
				<view class="contain">
					<view class="title">{{title}}</view>
					<view class="time">{{desc}}</view>
				</view>
			</view>
			<view>
				<view class="item" v-for="(post ,index) in hotPost" :key="index" @tap.stop.prevent="jumpHot(post)">
					<view :class="'order order_' + (index + 1)">{{ index + 1 }}</view>
					<view class="post" v-if="post.type==1 || post.type==3">{{ post.title }}</view>
					<view class="post" v-if="post.type==2 || post.type==4">{{ post.content }}</view>
					<block>
						<view class="tag hot" v-if="index<3">热</view>
						<view class="tag new" v-else>新</view>
					</block>
				</view>
			</view>
		</view>
	</view>

</template>

<script setup>
	const props = defineProps({
		hotPost: {
			type: Array,
			default: []
		},
		title: {
			type: String,
			default: ""
		},
		desc: {
			type: String,
			default: ""
		}
	});
	
	function jumpHot(item) {
		uni.navigateTo({
			url: '/pages/post/detail?id=' + item.id
		})
	}
</script>

<style lang="scss">
	page {
		background-color: #F5F5F5;
	}

	.hot-post{
		margin:  40rpx 40rpx 0;
		background-image: linear-gradient(rgba(255, 241, 241, 0.6) 0, #fffcff 100%);
		padding: 20rpx;
		border-radius: 20rpx;
		.hot-post-head{
			display: flex;
			justify-content: space-between;
			height: 40rpx;
			line-height: 40rpx;
			.contain{
				display: flex;
				.title{
					font-weight: 600;
				}
				.time{
					font-weight: 100;
					color: #c8c8c8;
					margin-left: 50rpx;
					font-size: 22rpx;
					border: 0.5rpx solid #d3d3d3;
					padding: 0 10rpx;
					border-radius: 6rpx;
				}
			}
		}
		.item{
			display: flex;
			height: 80rpx;
			line-height: 80rpx;
			.order{
				font-size: 30rpx;
				margin-right: 20rpx;
				color: #d1d1d1;
				font-weight: 600;
				width: 30rpx;
				min-width: 30rpx;
				text-align: right;
			}
			.order.order_1{
				color: #fb5835;
			}
			.order.order_2{
				color: #fe6434;
			}
			.order.order_3{
				color: #ffaa02;
			}
			.post{
				font-size: 30rpx;
				margin-right: 10rpx;
				max-width: 540rpx;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}
			.tag{
				color: #fff;
				font-size: 21rpx;
				margin-top: 22.5rpx;
				text-align: center;
				width: 30rpx;
				height: 30rpx;
				line-height: 30rpx;
				border-radius: 4rpx;
			}
			.tag.new{
				background-color: #ff9e3d;
				margin-left: auto;
			}
			.tag.hot{
				background-color: #fd6e46;
				margin-left: auto;
			}
		}
	}

</style>