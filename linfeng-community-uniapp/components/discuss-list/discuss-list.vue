<template>
	<view class="container">
		<block v-for="(item,index) in list" :key="item.id">
			<view class="topic-item" @tap="toDetail(item.id)">
				<view class="topic-header">
					<text class="topic-tag"># {{item.title}}</text>
					<view v-if="sessionUid == item.uid" @tap.stop="onActive(item,index)" class="action-btn">
						<u-icon size="32" name="more-dot-fill" color="#000000"></u-icon>
					</view>
				</view>
				<view class="topic-content">
					<text class="topic-desc">{{item.introduce}}</text>
				</view>
				<view class="topic-footer">
					<view class="footer-item">
						<u-icon name="eye" size="28rpx" color="#000000"></u-icon>
						<text>{{numberFormat(item.readCount)}}次浏览</text>
					</view>
					<view class="footer-item">
						<u-icon name="file-text" size="28rpx" color="#000000"></u-icon>
						<text>{{item.postNumber || 0}} 篇内容</text>
					</view>
					<view class="footer-item">
						<u-icon name="account" size="28rpx" color="#000000"></u-icon>
						<text>发起人:{{item.userInfo.username}}</text>
					</view>
				</view>
			</view>
		</block>
		<!-- 加载状态 -->
		<block v-if="list.length === 0 && loadStatus == 'nomore'">
			<u-empty margin-top="100" text="暂无内容" mode="favor"></u-empty>
		</block>
		<block v-else>
			<u-loadmore margin-bottom="50" margin-top="50" v-if="list.length > 10" bg-color="#f5f5f5"
				:status="loadStatus" />
		</block>
		<!-- 操作弹窗 -->
		<u-action-sheet :list="actionList" v-model="showAction" @click="confirm"></u-action-sheet>
	</view>
</template>

<script setup>
	import {
		ref,
		watch,
		getCurrentInstance,
		onMounted
	} from 'vue'
	import {
		numberFormat
	} from "@/utils/filters.js"

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H

	const props = defineProps({
		list: {
			type: Array,
			required: true
		},
		loadStatus: {
			type: String,
			default: 'loading'
		}
	})

	const emit = defineEmits(['on-delete'])

	const showAction = ref(false)
	const sessionUid = ref("")
	const actionList = ref([{
		text: '删除话题',
		color: 'red'
	}])
	const chooseDis = ref({})
	const chooseIndex = ref("")
	const disList = ref([])

	watch(() => props.list, (n) => {
		disList.value = n
	})

	onMounted(() => {
		let userInfo = uni.getStorageSync("userInfo")
		if (userInfo) {
			sessionUid.value = userInfo.uid
			console.log(sessionUid.value)
		}
	})

	function toDetail(id) {
		uni.navigateTo({
			url: '/pages/discuss/detail?id=' + id
		})
	}

	function onActive(e, index) {
		showAction.value = true
		chooseDis.value = e
		chooseIndex.value = index
	}

	function confirm(index) {
		if (index === 0) {
			disDel()
		}
	}

	function disDel() {
		$H.post('discuss/del', {
			id: chooseDis.value.id
		}).then(res => {
			if (res.code == 0) {
				emit('on-delete', chooseDis.value.id)
				uni.showToast({
					title: '删除成功',
					icon: 'success'
				})
			}
		})
	}
</script>

<style lang="scss" scoped>
	.container {
		padding: 20rpx;
		background-color: #f5f5f5;
	}

	.topic-item {
		background-color: #ffffff;
		padding: 30rpx;
		margin-bottom: 20rpx;
		border-radius: 12rpx;

		.topic-header {
			margin-bottom: 16rpx;
			display: flex;
			align-items: center;
			justify-content: space-between;

			.topic-tag {
				font-size: 32rpx;
				font-weight: 700;
				color: #000000;
				flex: 1;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}

			.action-btn {
				flex-shrink: 0;
				padding: 10rpx;
				margin: -10rpx;

				::v-deep .u-icon {
					display: flex;
				}
			}
		}

		.topic-content {
			margin-bottom: 20rpx;

			.topic-desc {
				font-size: 28rpx;
				color: #666;
				line-height: 1.5;
				display: block;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}
		}

		.topic-footer {
			display: flex;
			align-items: center;
			justify-content: space-between;
			font-size: 24rpx;
			color: #999;

			.footer-item {
				display: flex;
				align-items: center;
				flex: 1;
				justify-content: center;
				overflow: hidden;
				white-space: nowrap;

				::v-deep .u-icon {
					margin-right: 8rpx;
					flex-shrink: 0;
				}

				text {
					font-size: 24rpx;
					color: #000000;
					overflow: hidden;
					text-overflow: ellipsis;
					white-space: nowrap;
				}

				&:last-child {
					margin-right: 0;
				}
			}
		}
	}

	// 加载更多样式
	.u-loadmore {
		margin: 30rpx 0;
		display: flex;
		justify-content: center;
		align-items: center;
	}
</style>