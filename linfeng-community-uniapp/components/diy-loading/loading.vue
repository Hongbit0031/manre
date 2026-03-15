<template>
	<view v-if="showToast" class="lee-bg" :class="shadowBg === 'white' ? 'shadow-bg-white' : '' ">
		<view class="lees-box" @click="switch_loading">
			<view class="spinner" v-if="type == 'round'">
				<view :style="{background:iconBg}" class="bounce1"></view>
				<view :style="{background:iconBg}" class="bounce2"></view>
				<view :style="{background:iconBg}" class="bounce3"></view>
			</view>
			<view class="spinner2" v-if="type == 'rect'">
				<view :style="{background:iconBg}" class="rect1"></view>
				<view :style="{background:iconBg}" class="rect2"></view>
				<view :style="{background:iconBg}" class="rect3"></view>
				<view :style="{background:iconBg}" class="rect4"></view>
				<view :style="{background:iconBg}" class="rect5"></view>
			</view>
			<view :class="[type.indexOf('rotate') >-1 ? type:'rotate1']" class="loading"
				v-if="type.indexOf('rotate') >-1"></view>
			<view :style="{color:color}" :class="[animateTxt?'animateTxt':'','load-txt']" v-if="loadTxt">{{loadTxt}}
			</view>
		</view>
	</view>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'

const props = defineProps({
	// 显示的图标类型 rect,round,rotate1,rotate2,rotate3,rotate4
	type: {
		type: String,
		default: 'round'
	},
	// 加载动画的文字
	loadTxt: {
		type: String,
		default: '加载中'
	},
	// 加载遮罩的背景,可选值为white
	shadowBg: {
		type: String,
		default: 'black'
	},
	// 显示图标颜色，type值为rect或round才可改背景颜色
	iconBg: {
		type: String,
		default: '#167BEF'
	},
	// 加载文字颜色
	color: {
		type: String,
		default: '#434343'
	},
	// 是否开启加载文字闪烁
	animateTxt: {
		type: Boolean,
		default: true
	}
})

const store = useStore()

const showToast = computed(() => store.state.loading)

const switch_loading = () => {
	store.commit("switch_loading")
}
</script>

<style lang="scss" scoped>
	.lee-bg {
		position: fixed;
		top: 0;
		left: 0;
		width: 100vw;
		height: 100vh;
		z-index: 999;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(0, 0, 0, 0.3);

		.lees-box {
			margin-top: -15vh;
			text-align: center;

			.load-txt {
				font-size: 24rpx;
				text-align: center;
				// color: #a8a8a8;
			}

			.animateTxt {
				animation: loadTxt 2s infinite;
			}

			.spinner {
				view {
					width: 30rpx;
					height: 30rpx;
					border-radius: 50%;
					display: inline-block;
					-webkit-animation: bouncedelay 1.4s infinite ease-in-out;
					animation: bouncedelay 1.4s infinite ease-in-out;
					-webkit-animation-fill-mode: both;
					animation-fill-mode: both;
				}

				.bounce1 {
					-webkit-animation-delay: -0.32s;
					animation-delay: -0.32s;
				}

				.bounce2 {
					-webkit-animation-delay: -0.16s;
					animation-delay: -0.16s;
				}
			}

			.spinner2 {
				width: 50px;
				height: 60px;
				text-align: center;
				font-size: 10px;

				view {
					// background-color: #67CF22;
					height: 100%;
					width: 6px;
					margin-right: 3px;
					display: inline-block;

					-webkit-animation: stretchdelay 1.2s infinite ease-in-out;
					animation: stretchdelay 1.2s infinite ease-in-out;
				}

				.rect2 {
					-webkit-animation-delay: -1.1s;
					animation-delay: -1.1s;
				}

				.rect3 {
					-webkit-animation-delay: -1.0s;
					animation-delay: -1.0s;
				}

				.rect4 {
					-webkit-animation-delay: -0.9s;
					animation-delay: -0.9s;
				}

				.rect5 {
					-webkit-animation-delay: -0.8s;
					animation-delay: -0.8s;
				}
			}

			@-webkit-keyframes stretchdelay {

				0%,
				40%,
				100% {
					-webkit-transform: scaleY(0.4)
				}

				20% {
					-webkit-transform: scaleY(1.0)
				}
			}

			@keyframes stretchdelay {

				0%,
				40%,
				100% {
					transform: scaleY(0.4);
					-webkit-transform: scaleY(0.4);
				}

				20% {
					transform: scaleY(1.0);
					-webkit-transform: scaleY(1.0);
				}
			}

			.loading {
				width: 50rpx;
				height: 50rpx;
				margin-bottom: 10rpx;
				display: inline-block;
				vertical-align: middle;
				-webkit-animation: a 1s steps(30) infinite;
				animation: loading 1s steps(30) infinite;
				background: transparent url(./img/icon1.png) no-repeat;
				background-size: 100%;
			}

			.rotate2 {
				background-image: url(./img/icon2.png);
			}

			.rotate3 {
				background-image: url(./img/icon3.png);
			}

			.rotate4 {
				background-image: url(./img/icon4.png);
			}

			@keyframes loading {
				0% {
					-webkit-transform: rotate(0deg);
					transform: rotate(0deg);
				}

				to {
					-webkit-transform: rotate(1turn);
					transform: rotate(1turn);
				}
			}
		}
	}

	.shadow-bg-white {
		background: rgba(255, 255, 255, 0.7);
	}

	@keyframes loadTxt {
		0% {
			opacity: 1;
		}

		50% {
			opacity: 0;
		}

		100% {
			opacity: 1;
		}
	}

	@-webkit-keyframes bouncedelay {

		0%,
		80%,
		100% {
			-webkit-transform: scale(0.0)
		}

		40% {
			-webkit-transform: scale(1.0)
		}
	}

	@keyframes bouncedelay {

		0%,
		80%,
		100% {
			transform: scale(0.0);
			-webkit-transform: scale(0.0);
		}

		40% {
			transform: scale(1.0);
			-webkit-transform: scale(1.0);
		}
	}
</style>