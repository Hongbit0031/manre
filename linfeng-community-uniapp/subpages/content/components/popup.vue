<template>
	<view>
		<view class="mask"></view>		
		<view>
			<view class="messBox">
				<view class="wrapBox">
					<view class="title">{{ title }}</view>
					<view class="desc">
						<rich-text :nodes="content"></rich-text>
					</view>
					<view class="btn" @click="closePop">我知道了</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
const props = defineProps({
	title: {
		type: String,
		default: ''
	},
	content: {
		type: String,
		default: ''
	}
})

const emit = defineEmits(['closePop'])

const closePop = () => {
	emit('closePop')
}
</script>

<style lang="scss">
	.mask {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		z-index: 888;
		background-color: rgba(0, 0, 0, 0.6);
	}

	.btn {
		width: 70%;
		margin: 20rpx auto;
		margin-top: 40rpx;
		padding: 20rpx 0px;
		text-align: center;
		background: #4884FF;
		border-radius: 34px;
		font-size: 30rpx;
		color: #fff;
	}


	.messBox {
		position: fixed;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		z-index: 999;
		width: 80%;
		padding: 10rpx 0rpx;

		.wrapBox {
			background-color: #fff;
			border-radius: 20rpx;
			padding: 30rpx;

			.title {
				text-align: center;
				font-size: 32rpx;
				font-weight: bold;
			}

			.desc {
				max-height: 400rpx;
				overflow-y: auto;
				margin-top: 20rpx;
				font-size: 30rpx;
			}
		}
	}


	.desc {
		height: 230rpx;
		overflow: scroll;
		margin-top: 10rpx;
		font-size: 30rpx;
		color: #666;
		line-height: 50rpx;
	}

	.btnbox {
		display: flex;
		justify-content: space-around;
		margin: 40rpx 0rpx 20rpx 0rpx;
		margin-top: 50rpx;

		.btnno {
			padding: 10rpx 30rpx;
			background-color: #f8f8f8;
			border-radius: 10rpx;
			font-size: 30rpx;
			color: #666;
		}

		.btnUp {
			padding: 10rpx 30rpx;
			background-color: #4884FF;
			border-radius: 10rpx;
			font-size: 30rpx;
			color: #fff;
		}
	}
</style>