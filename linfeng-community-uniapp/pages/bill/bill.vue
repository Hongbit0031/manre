<template>
	<view class="bill-details" ref="container">
		<view class="nav acea-row">
			<view class="item" :class="types == 0 ? 'on' : ''" @click="changeTypes(0)">全部</view>
			<view class="item" :class="types == 1 ? 'on' : ''" @click="changeTypes(1)">支出</view>
			<view class="item" :class="types == 2 ? 'on' : ''" @click="changeTypes(2)">收入</view>
		</view>
		<view class="sign-record">
			<view class="list">
				<view class="item" v-for="(item, listIndex) in list" :key="listIndex">
					<view class="data"></view>
					<view class="listn">
						<view class="itemn acea-row row-between-wrapper">
							<view>
								<view class="name">{{ item.title }}</view>
								<view class="mark">{{ item.mark }}</view>
								<view>{{ item.addTime }}</view>
							</view>
							<view class="num" :class="item.pm == 0 ? 'font-colors' : ''">
								{{ item.pm == 0 ? "-" : "+" }}{{ item.number }}
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		<block v-if="list.length === 0">
			<u-empty margin-top="100" text="暂无记录" mode="favor"></u-empty>
		</block>

	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue'
	import {
		getCurrentInstance
	} from 'vue'
	import {
		onLoad,
		onReachBottom
	} from '@dcloudio/uni-app'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H

	const loadStatus = ref("loading")
	const types = ref(0)
	const where = ref({
		page: 1,
		limit: 10
	})
	const list = ref([])

	onLoad((options) => {
		if (options.types) {
			types.value = options.types
		}
		getBillList()
	})

	// 监听页面触底事件
	onReachBottom(() => {
		where.value.page++
		getBillList()
	})

	function changeTypes(val) {
		if (val != types.value) {
			types.value = val
			list.value = []
			where.value.page = 1
		}
		getBillList()
	}

	function getBillList() {
		loadStatus.value = "loading"
		$H.post("bill/billList", {
			page: where.value.page,
			limit: where.value.limit,
			type: types.value
		}).then(res => {
			const data = Array.isArray(res.result.data) ? res.result.data : []
			list.value = list.value.concat(data)
			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = "nomore"
			} else {
				loadStatus.value = "loadmore"
			}
		})
	}
</script>

<style lang="scss" scoped>
	.bill-details {
		.nav {
			background-color: #fff;
			height: 90rpx;
			width: 100%;
			line-height: 90rpx;

			.item {
				flex: 1;
				-o-flex: 1;
				-ms-flex: 1;
				text-align: center;
				font-size: 30rpx;
				color: #282828;
				height: 100%;

				&.on {
					color: #55aaff;
					border-bottom: 3rpx solid #55aaff;
				}
			}
		}
	}

	.sign-record {
		.list {
			.item {
				.data {
					height: 50rpx;
					line-height: 80rpx;
					padding: 0 30rpx;
					font-size: 24rpx;
					color: #666;
				}

				.listn {
					background-color: #fff;
					font-size: 24rpx;
					color: #999;

					.itemn {
						height: 160rpx;
						border-bottom: 1px solid #eee;
						padding-right: 30rpx;
						margin-left: 30rpx;

						.name {
							width: 390rpx;
							font-size: 34rpx;
							color: #161616;
							margin-bottom: 6rpx;
						}

						.mark {
							width: 390rpx;
							font-size: 26rpx;
							color: #5d5d5d;
							margin-bottom: 6rpx;
						}

						.num {
							font-size: 36rpx;
							font-family: 'GuildfordProBook 5';
							color: #ff0000;
						}
					}
				}
			}
		}
	}

	.font-colors {
		color: #64B5F6 !important;
	}
</style>