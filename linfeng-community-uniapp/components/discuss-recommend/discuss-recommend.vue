<template>
	<view>
		<view class="hot-discuss">
			<view class="hot-discuss-title">
				<view>热门话题</view>
				<view class="tip">滑动查看</view>
			</view>
			<scroll-view :show-scrollbar="false" scrollX enableFlex="enable-flex" enhanced="enhanced"
				scrollWithAnimation="scroll-with-animation">
				<view class="hot-discuss-contain">
					<view class="hot-discuss-item"
						v-for="(item, index) in list" :key="index">
						<view class="hot-discuss-name-wrap" @tap.stop.prevent="jump(item.id)">
							<view class="hot-discuss-name">#{{ item.title }}</view>
							<view class="hot-discuss-tag hot" v-if="index < 3">热</view>
						</view>
						<view class="hot-discuss-count">{{numberFormat(item.readCount)}}</view>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup>
	import { numberFormat } from "@/utils/filters.js"
	
	const props = defineProps({
			list: {
				type: Array,
				default: function(e) {
					return [];
				}
			}
	});
	
	function jump(id) {
				uni.navigateTo({
			url: "/pages/discuss/detail?id=" + id
				})
	}
</script>

<style lang="scss">
	.hot-discuss {
		background: linear-gradient(180deg, rgba(238, 242, 255, 0.6) 0%, rgba(255, 255, 255, 0.9) 100%);
		background-image: linear-gradient(180deg, #eef2ff 0%, #ffffff 100%);
		border-radius: 24rpx;
		margin: 20rpx;
		padding: 24rpx 0;
		box-shadow: 0 4rpx 20rpx rgba(99, 102, 241, 0.06);
		position: relative;
		overflow: hidden;
		
		&::before {
			content: '';
			position: absolute;
			top: 0;
			left: 0;
			right: 0;
			height: 2rpx;
			background: linear-gradient(90deg, rgba(99, 102, 241, 0.2) 0%, rgba(168, 85, 247, 0.2) 50%, rgba(99, 102, 241, 0.2) 100%);
		}
		
		.hot-discuss-title {
			display: flex;
			justify-content: space-between;
			margin: auto;
			color: #1e293b;
			width: 720rpx;
			font-size: 30rpx;
			padding: 0rpx 12rpx;
			font-weight: 600;
			position: relative;
			z-index: 1;
			
			.tip {
				font-size: 26rpx;
				font-weight: 400;
				color: #64748b;
				// background: rgba(255, 255, 255, 0.6);
				padding: 4rpx 12rpx;
				border-radius: 12rpx;
				backdrop-filter: blur(10rpx);
			}
		}

		.hot-discuss-contain {
			flex-direction: column;
			flex-wrap: wrap;
			height: 360rpx;
			display: flex;
			padding-left: 20rpx;
			position: relative;
			z-index: 1;

			.hot-discuss-item {
				display: flex;
				width: 550rpx;
				white-space: nowrap;
				text-overflow: ellipsis;
				padding: 10rpx 0;
				overflow: hidden;
				height: 70rpx;
				line-height: 60rpx;
				align-items: center;
				transition: all 0.3s ease;
				
				.hot-discuss-name-wrap {
					display: flex;
					align-items: center;
					max-width: 430rpx;
					gap: 8rpx;
				}
				
				.hot-discuss-name {
					overflow: hidden;
					text-overflow: ellipsis;
					margin-left: 15rpx;
					font-size: 30rpx;
					color: #334155;
					font-weight: 500;
					transition: color 0.3s ease;
				}
				
				&:active .hot-discuss-name {
					color: #6366f1;
				}

				.hot-discuss-tag {
					display: flex;
					align-items: center;
					justify-content: center;
					width: 36rpx;
					height: 36rpx;
					border-radius: 10rpx;
					font-size: 22rpx;
					color: #fff;
					box-shadow: 0 2rpx 8rpx rgba(248, 100, 0, 0.3);
				}

				.hot-discuss-tag.hot {
					background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
				}

				.hot-discuss-count {
					margin-left: 40rpx;
					font-size: 22rpx;
					color: #939393;
				}
			}
		}
	}
</style>