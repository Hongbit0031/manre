<template>
	<view class="container">
		<template v-if="disList && disList.length > 0">
			<view class="topic-item" v-for="(item, index) in formatDisList" :key="index"
				@click="chooseTopic(item.id, item.title)">
				<view class="topic-header">
					<text class="topic-tag"># {{ item.title }}</text>
				</view>
				<view class="topic-content">
					<text class="topic-desc">{{ item.introduce }}</text>
				</view>
				<view class="topic-footer">
					<view class="footer-item">
						<u-icon name="eye" size="28rpx" color="#000000"></u-icon>
						<text>{{ numberFormat(item.readCount)}}次浏览</text>
					</view>
					<view class="footer-item">
						<u-icon name="file-text" size="28rpx" color="#000000"></u-icon>
						<text>{{ item.postNumber }} 篇内容</text>
					</view>
					<view class="footer-item">
						<u-icon name="account" size="28rpx" color="#000000"></u-icon>
						<text>发起人:{{ item.userInfo.username }}</text>
					</view>
				</view>
			</view>
		</template>

		<!-- 加载状态 -->
		<block v-if="disList.length === 0 && loadStatus == 'nomore'">
			<u-empty margin-top="100" text="暂无话题" mode="favor"></u-empty>
		</block>
		<block v-else>
			<view style="margin: 30rpx 0;">
				<u-loadmore :status="loadStatus" />
			</view>
		</block>
	</view>
</template>

<script setup>
	import {
		ref,
		computed,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad,
		onShow,
		onReachBottom
	} from '@dcloudio/uni-app'
	import {
		numberFormat
	} from "@/utils/filters.js"

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H
	const $f = proxy.$f

	const disList = ref([])
	const loadStatus = ref('loadmore')
	const page = ref(1)
	const topicId = ref("")
	const fromPage = ref('') // 记录来源页面
	const btnStyle = ref({
		color: "#fff",
		backgroundColor: '#343434'
	})

	const formatDisList = computed(() => {
		return disList.value.map(item => ({
			id: item?.id || 0,
			title: item?.title || '暂无标题',
			introduce: item?.introduce || '暂无介绍',
			readCount: item?.readCount || 0,
			postNumber: item?.postNumber || 0,
			userInfo: {
				username: item?.userInfo?.username || '未知用户'
			}
		}))
	})

	onLoad((options) => {
		topicId.value = options.topicId
		fromPage.value = options.from || '' // 获取来源页面标识
	})

	onShow(() => {
		page.value = 1
		disList.value = []
		getDisList()
	})

	onReachBottom(() => {
		page.value++
		getDisList()
	})

	function replace(str) {
		if (!str) return ''
		str = str.replace(/\n/g, '')
		return str.substring(0, 40)
	}

	function getDisList() {
		loadStatus.value = 'loading'
		$H.post('discuss/list', {
			page: page.value,
			topicId: topicId.value
		}).then(res => {
			if (res.code === 0 && res.result?.data) {
				const newData = Array.isArray(res.result.data) ? res.result.data : []
				disList.value = disList.value.concat(newData)

				if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
					loadStatus.value = 'nomore'
				} else {
					loadStatus.value = 'loadmore'
				}
			} else {
				loadStatus.value = 'nomore'
			}
		}).catch(() => {
			loadStatus.value = 'nomore'
		})
	}

	function chooseTopic(id, name) {
		if (!id || !name) {
			uni.showToast({
				title: '话题信息不完整',
				icon: 'none'
			})
			return
		}

		// 根据来源页面发送不同的事件
		if (fromPage.value === 'article') {
			// 如果来自长文页面
			uni.$emit('chooseDiscussForArticle', {
				id,
				name
			})
		} else if (fromPage.value === 'post') {
			// 如果来自帖子发布页面
			uni.$emit('chooseDiscussForPost', {
				id,
				name
			})
		} else {
			// 默认情况，使用旧方法（兼容其他页面）
			let pages = getCurrentPages()
			let prevPage = pages[pages.length - 2]
			if (prevPage?.$vm) {
				prevPage.$vm.form.discussId = id
				prevPage.$vm.disName = name
			}
		}
		uni.navigateBack()
	}

	function createDiscuss() {
		$f.jump("/pages/discuss/add?topicId=" + topicId.value)
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

			.topic-tag {
				font-size: 32rpx;
				font-weight: 700;
				color: #000000;
			}
		}

		.topic-content {
			margin-bottom: 20rpx;

			.topic-desc {
				font-size: 28rpx;
				color: #666;
				line-height: 1.5;
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