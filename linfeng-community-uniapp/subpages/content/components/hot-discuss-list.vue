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
						<view @tap.stop.prevent="jump(item.id)" class="hot-discuss-name">#{{ item.title }}
						</view>
						<block>
							<view class="hot-discuss-tag hot" v-if="index < 3">热</view>
						</block>
						<view style="margin-left: 40rpx;font-size: 22rpx;color: #939393;">{{item.readCount}}</view>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
	list: {
		type: Array,
		default: () => []
	}
})

const jump = (id) => {
	uni.navigateTo({
		url: "/pages/discuss/detail?id=" + id
	})
}
</script>

<style lang="scss">
	.hot-discuss {
		background-image: linear-gradient(rgba(255, 225, 222, 0.6) 0, #fffcff 100%);
		.hot-discuss-title {
			display: flex;
			justify-content: space-between;
			margin: auto;
			color: #000000;
			width: 720rpx;
			font-size: 30rpx;
			padding: 0rpx 12rpx;
			font-weight: 600;
			.tip {
				font-size: 26rpx;
				font-weight: 400;
				color: #999999;
			}
		}

		.hot-discuss-contain {
			flex-direction: column;
			flex-wrap: wrap;
			height: 360rpx;
			display: flex;
			padding-left: 20rpx;

			.hot-discuss-item {
				display: flex;
				width: 550rpx;
				white-space: nowrap;
				text-overflow: ellipsis;
				padding: 10rpx 0;
				overflow: hidden;
				height: 70rpx;
				line-height: 60rpx;

				.hot-discuss-name {
					overflow: hidden;
					max-width: 430rpx;
					margin-left: 15rpx;
					text-overflow: ellipsis;
					font-size: 30rpx;
				}

				.hot-discuss-tag {
					display: inline-block;
					width: 33rpx;
					height: 33rpx;
					border-radius: 10rpx;
					margin-left: 6rpx;
					line-height: 33rpx;
					text-align: center;
					font-size: 22rpx;
					color: #fff;
					border-radius: 7rpx;
					margin-top: 13rpx;
				}

				.hot-discuss-tag.hot {
					background-color: #f86400;
				}
			}
		}
	}
</style>