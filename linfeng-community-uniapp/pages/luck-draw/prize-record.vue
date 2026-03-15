<template>
	<view class="prize-record-container">
		<block v-if="lists.length!=0">
			<view v-for="(item, index) in lists" :key="index" class="prize-record-item row">
				<CustomImageComponent :src="item.prizeImage" style="width: 90rpx;height: 90rpx" radius="6rpx" />
				<view class="prize-record-info">
					<view class="lg" v-if="item.prizeType==1">{{item.prizeName}}＋{{item.number}}</view>
					<view class="lg" v-else-if="item.prizeType==2">{{item.prizeName}}</view>
					<view class="lg" v-else-if="item.prizeType==3">{{item.prizeName}}￥{{item.number}}</view>
					<view class="lg" v-else-if="item.prizeType==4">{{item.prizeName}}{{item.number}}件(联系客服领取)</view>
					<view class="xs lighter" v-if="item.prizeType==1 ||item.prizeType==3 ">领取时间：{{item.createTime}}
					</view>
					<view class="xs lighter" v-if="item.prizeType==2">抽奖时间：{{item.createTime}}</view>
					<view class="xs lighter" v-if="item.prizeType==4">中奖时间：{{item.createTime}}</view>
				</view>
			</view>
		</block>
		<block v-else>
			<view class="data-null column-center">
				<image :src="$IMG+'/images/null_gift.png'" class="img-null"></image>
				<text class="xs muted">暂无抽奖记录～</text>
			</view>
		</block>
	</view>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad,
		onReachBottom
	} from '@dcloudio/uni-app'
	import CustomImageComponent from "@/components/custom-image/custom-image.vue"

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H
	const $IMG = ref(proxy.$IMG)

	const loadStatus = ref("loading")
	const page = ref(1)
	const lists = ref([])

	onLoad(() => {
		getUserRecordFun()
	})

	onReachBottom(() => {
		// 防止重复加载
		if (loadStatus.value === 'loading' || loadStatus.value === 'nomore') {
			return
		}
		page.value++
		getUserRecordFun()
	})

	function getUserRecordFun() {
		loadStatus.value = "loading"
		$H.get('luckDraw/record/' + page.value).then(res => {
			const data = Array.isArray(res.result.data) ? res.result.data : []
			lists.value = lists.value.concat(data)
			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = "nomore"
			} else {
				loadStatus.value = "loadmore"
			}
		})
	}
</script>
<style lang="scss">
	.prize-record-container {
		padding: 0 20rpx;

		.prize-record-item {
			margin-top: 20rpx;
			background-color: white;
			padding: 26rpx 24rpx;
			border-radius: 10rpx;

			.prize-record-info {
				margin-left: 24rpx;
				flex: 1
			}
		}
	}

	.data-null {
		padding-top: 200rpx;
	}

	.row {
		display: flex;
		align-items: center;
	}

	.lg {
		font-size: 32rpx;
	}

	.xs {
		font-size: 24rpx;
	}

	.muted {
		color: #606266;
	}

	.lighter {
		color: #7a7c81;
	}

	.column-center {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}
</style>