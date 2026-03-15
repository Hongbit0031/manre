<template>
	<view>
		<view class="container" v-if="open">
			<!-- header -->
			<view class="header">
				<view class="txt">
					付费贴需要支付
				</view>
				<view class="num">
					<text class="tag">¥</text>{{ post.pay || 0 }}
				</view>
			</view>
			<!-- body -->
			<view class="body">
				<view class="title">
					标题
				</view>
				<view class="content title-style">
					{{post.title}}
				</view>
			</view>
			<view class="body">
				<view class="title">
					简介
				</view>
				<view class="content content-style">
					<view style="white-space: pre-wrap;">
						<mp-html :content="post.brief" selectable="true" />
					</view>
				</view>
			</view>
			<view>
				<view>
					<view class="pay-content" style="background-color:#fff;">
						<u-icon name="lock"></u-icon>
						付费贴详情内容需支付后查看
						<view class="dynamic">
							<view class="dy dynamic_left"></view>
							<view class="dy dynamic_right"></view>
						</view>
					</view>
				</view>
			</view>
			<!-- footer -->
			<view class="footer">
				<view v-if="enough" class="btn"  @click="payVipPost">
					立即支付
				</view>
				<view v-else class="btn"  @click="goRecharge">
					去充值
				</view>
				<view class="tips">
					提示：支付后即可查看付费帖详情，支付后不可退款
				</view>
				<view v-if="noMoney" class="noMoney">
					余额不足，请先充值
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'

// 获取当前实例
const { proxy } = getCurrentInstance()

// 响应式数据
const enough = ref(false)
const active = ref(0)
const money = ref("")
const now_money = ref("")
const picList = ref([])
const activePic = ref(0)
const numberPic = ref("")
const paid_price = ref("")
const rechar_id = ref(0)
const post = ref({})
const userInfo = ref({})
const postId = ref(0)
const type = ref(0)
const isBuy = ref(false)
const open = ref(true)
const noMoney = ref(false)

// 方法
const checkOpen = () => {
	proxy.$H.get('system/payPostIsOpen').then(res => {
		if(res.code==0){
			open.value = res.result
			if(!res.result){
				proxy.$f.toast("模块未开启")
				setTimeout(function() {
					uni.switchTab({
						url: '/pages/index/index'
					})
				}, 1500)
			}
		}
	})
}

const payVipPost = () => {
	uni.showModal({
		title: '提示',
		content: '确定支付吗？',
		success: function(res) {
			if (res.confirm) {
				proxy.$H.post('user/payVipPost', {
					postId: postId.value
				}).then(res => {
					let url
					if (type.value == 1 || type.value == 4) {
						url = '/pages/post/detail?id=' + postId.value
					} else if (type.value == 2) {
						url = '/pages/post/video-detail?id=' + postId.value
					}
					if (res.code == 0) {
						uni.redirectTo({
							url: url
						})
					}
				})
			} else if (res.cancel) {
			}
		}
	})
}

const goRecharge = () => {
	uni.navigateTo({
		url: '/pages/pay/pay?from=paidpost'
	})
}

const getUserInfo = () => {
	proxy.$H.get('user/userInfo').then(res => {
		userInfo.value = res.result
		now_money.value = userInfo.value.money
		getVipPostInfo()
	})
}

const getVipPostInfo = () => {
	proxy.$H.post('post/getVipPostInfo', {
		postId: postId.value,
		uid: userInfo.value.uid
	}).then(res => {
		post.value = res.result
		isBuy.value = res.result.isBuy
		if (res.result.isBuy) {
			let url
			if (type.value == 1 || type.value == 4) {
				url = '/pages/post/detail?id=' + postId.value
			} else if (type.value == 2) {
				url = '/pages/post/video-detail?id=' + postId.value
			}
			uni.navigateTo({
				url: url
			})
		} else {
			if (userInfo.value.money >= res.result.pay) {
				enough.value = true
			} else {
				noMoney.value = true
			}
		}
	})
}

// 生命周期
onLoad((options) => {
	postId.value = options.id
	type.value = options.type
	checkOpen()
})

onShow(() => {
	getUserInfo()
})
</script>

<style scoped lang="less">
	.container {
		background-color: #f7f7f6;
		width: 100vw;
		height: 100vh;
		padding: 0;
		padding-top: 20rpx;
		overflow: scroll;
	}

	.header {
		display: flex;
		flex-direction: column;
		align-items: center;

		.txt {
			font-size: 28rpx;
			font-weight: 450;
		}

		.num {
			font-size: 80rpx;
			font-weight: 450;

			.tag {
				font-size: 60rpx;
				margin-right: 10rpx;
			}
		}
	}

	.body {
		background-color: #fff;
		padding: 20rpx;
		display: flex;
		justify-content: space-between;

		.title {
			color: #a8a4a4;
			font-weight: 600;
		}

		.content {
			width: 550rpx;
			display: flex;
			justify-content: flex-end;
			overflow: auto;
			
		}
		.title-style{
			font-weight: 700;
			font-size: 36rpx;
		}
		.content-style{
			font-weight: 500;
		}

	}

	.footer {
		margin-top: 50rpx;
		
		.btn {
			width: 710rpx;
			height: 80rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			background-color: #28a840;
			color: #fff;
			border-radius: 10rpx;
			margin-left: 20rpx;
		}
		.tips {
			display: flex;
			justify-content: center;
			margin-top: 20rpx;
			font-size: 24rpx;
			color: #999;
			
		}
		.noMoney {
			display: flex;
			justify-content: center;
			margin-top: 20rpx;
			font-size: 32rpx;
			color: #c74a46;
			font-weight: 600;
		}
	}


</style>
