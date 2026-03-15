<template>
	<view>
		<view class="page-body">
			<block v-for="(item,index) in msgList" :key="index">
				<view class="notification-content">
					<text class="date">{{item.createTime}}</text>
					<view class="notification-card">
						<view class="notification-header">
							<u-icon name="volume-up-fill" color="#f56c6c" size="28"></u-icon>
							<text class="notification-title">{{item.title}}</text>
						</view>
						<u-line></u-line>
						<view class="notification-body">
							<text class="notification-text">
								{{item.content}}
							</text>
						</view>
					</view>
				</view>
			</block>
			<!-- 加载状态 -->
			<block v-if="msgList.length === 0 && page==1">
				<u-empty margin-top="100" text="暂无系统消息" mode="favor"></u-empty>
			</block>
			<view style="height: 130rpx;"></view>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance, onUnmounted } from 'vue'
import { onLoad, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $u = proxy.$u

const mTxt = ref("")
const msgList = ref([])
const user = ref({})
const page = ref(1)
const isTop = ref(true) //上拉到顶后加锁处理
const timer = ref(null) //定时器

onLoad((options) => {
	getMessage(1)
	timer.value = setInterval(() => {
		getMessage(1)
	}, 30000)
})

onPullDownRefresh(() => {
	page.value++
	if (isTop.value) {
		getMessage(0)
	}
	uni.stopPullDownRefresh()
	if (timer.value) {
		clearInterval(timer.value)
		timer.value = null
	}
})

onReachBottom(() => {
	if (timer.value) {
		clearInterval(timer.value)
		timer.value = null
	}
	timer.value = setInterval(() => {
		getMessage(1)
	}, 30000)
})

onUnmounted(() => {
	if (timer.value) {
		clearInterval(timer.value)
		timer.value = null
	}
})

//如果为1说明是初始加载或发送消息后的更新，如果参数为0说明是上拉加载
function getMessage(e) {
	if (e == 1) {
		page.value = 1
	}
	$H.post("user/systemInfoList", {
		page: page.value,
		uid: user.value.uid
	}).then(res => {
		if (e == 1) {
			msgList.value = res.result
			page.value = 1
		} else {
			if (res.result.length == 0) {
				$u.toast('到顶啦！')
				isTop.value = false
			}
			msgList.value = res.result.concat(msgList.value)
		}
		updateChatStatus()
	})
}

// 更新阅读状态
function updateChatStatus() {
	$H.post("message/updateSystemStatus", {
		uid: user.value.uid
	}).then(res => {
		
	})
}
</script>

<style lang="scss" scoped>
	.page-body {
		padding: 0 20rpx;
		font-size: 28rpx;
	}

	.container {
		background-color: #f8f8f8;
		min-height: 100rpx;
	}

	.notification-content {
		padding: 30rpx;
	}

	.date {
		font-size: 28rpx;
		color: #999;
		text-align: center;
		margin-bottom: 6rpx;
		display: block;
	}

	.notification-card {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 16rpx;
		box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.05);
	}

	.notification-header {
		display: flex;
		align-items: center;
		margin-bottom: 24rpx;
	}

	.notification-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-left: 16rpx;
	}

	.notification-body {
		// padding-left: 50rpx;
	}

	.notification-text {
		font-size: 28rpx;
		color: #666;
		line-height: 1.6;
	}
</style>