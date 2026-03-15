<template>
	<view class="container">
		<view class="menu-wrap">
			<!-- 左侧分类列表 -->
			<scroll-view scroll-y class="left-menu" :scroll-top="scrollTop">
				<view
					v-for="(item, index) in classList"
					:key="index"
					class="menu-item"
					:class="[current == index ? 'menu-active' : '']"
					:data-current="index"
					@tap.stop="swichMenu(index)"
				>
					<text class="menu-text">{{ item.cateName }}</text>
				</view>
			</scroll-view>
			
			<!-- 右侧圈子列表 -->
			<scroll-view scroll-y class="right-content" @scrolltolower="reachBottom">
				<view class="topic-list">
					<block v-for="(item, index) in topicList" :key="index">
						<view @click="toTopicDetail(item.id)">
							<view class="topic-item">
								<view class="topic-header">
									<u-image class="topic-avatar" width="80rpx" height="80rpx" shape="circle" :src="item.coverImage"></u-image>
									<view class="topic-info">
										<view class="topic-name">{{ item.topicName }}</view>
										<view class="topic-desc">{{ item.description }}</view>
									</view>
								</view>
								<view class="topic-footer">
									<view class="footer-item">
										<u-icon name="account" size="28rpx" color="#000000"></u-icon>
										<text>{{ numberFormat(item.userCount) }}位成员</text>
									</view>
									<view class="footer-item">
										<u-icon name="file-text" size="28rpx" color="#000000"></u-icon>
										<text>{{ item.postCount }} 篇内容</text>
									</view>
								</view>
							</view>
						</view>
					</block>
					
					<!-- 加载状态 -->
					<block v-if="loadStatus != 'none'">
						<block v-if="topicList.length > 0">
							<view style="margin: 30rpx;">
								<u-loadmore :status="loadStatus" />
							</view>
						</block>
						<block v-else>
							<u-empty margin-top="100" text="暂无相关圈子" mode="favor"></u-empty>
						</block>
					</block>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { numberFormat } from '@/utils/filters.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const scrollTop = ref(0)
const current = ref(0)
const menuHeight = ref(0)
const menuItemHeight = ref(0)
const classList = ref([])
const classId = ref('')
const topicList = ref([])
const loadStatus = ref('loadmore')
const keyword = ref('')
const page = ref(1)

onLoad((options) => {
	if (options.classId) {
		classId.value = options.classId
	}
	getClassList()
})

function toTopicDetail(id) {
	uni.navigateTo({
		url: '/pages/topic/detail?id=' + id
	})
}

function toSearch() {
	uni.navigateTo({
		url: '/pages/search/search'
	})
}

function reachBottom() {
	page.value++
	getTopicList()
}

function search() {
	uni.navigateTo({
		url: '/pages/topic/list?keyword=' + keyword.value
	})
}

function getClassList() {
	$H.get('topic/classList').then(res => {
		classList.value = res.result

		if (classId.value) {
			classList.value.forEach((item, index) => {
				if (item.cateId == classId.value) {
					classId.value = res.result[index].cateId
					swichMenu(index)
				}
			})
		} else {
			classId.value = res.result[0].cateId
			getTopicList()
		}
	})
}

function getTopicList() {
	loadStatus.value = 'loading'
	$H.get('topic/list', {
		classId: classId.value,
		page: page.value
	}).then(res => {
		topicList.value = topicList.value.concat(res.result.data)
		if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
			loadStatus.value = 'nomore'
		} else {
			loadStatus.value = 'loadmore'
		}
	})
}

// 点击左边的栏目切换
async function swichMenu(index) {
	if (index == current.value) return
	current.value = index
	// 如果为0，意味着尚未初始化
	if (menuHeight.value == 0 || menuItemHeight.value == 0) {
		await getElRect('left-menu', 'menuHeight')
		await getElRect('menu-item', 'menuItemHeight')
	}
	// 将菜单菜单活动item垂直居中
	scrollTop.value = index * menuItemHeight.value + menuItemHeight.value / 2 - menuHeight.value / 2

	classId.value = classList.value[index].cateId
	topicList.value = []
	page.value = 1
	getTopicList()
}

// 获取一个目标元素的高度
function getElRect(elClass, dataVal) {
	return new Promise((resolve, reject) => {
		const query = uni.createSelectorQuery().in(proxy)
		query
			.select('.' + elClass)
			.fields(
				{
					size: true
				},
				res => {
					// 如果节点尚未生成，res值为null，循环调用执行
					if (!res) {
						setTimeout(() => {
							getElRect(elClass, dataVal)
						}, 10)
						return
					}
					if (dataVal === 'menuHeight') {
						menuHeight.value = res.height
					} else if (dataVal === 'menuItemHeight') {
						menuItemHeight.value = res.height
					}
					resolve()
				}
			)
			.exec()
	})
}
</script>

<style lang="scss" scoped>
.container {
	height: 100vh;
	background-color: #f5f5f5;
}

.menu-wrap {
	display: flex;
	height: 100%;
}

.left-menu {
	width: 180rpx;
	height: 100%;
	background-color: #f5f5f5;
	flex-shrink: 0;
	
	.menu-item {
		height: 100rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		position: relative;
		
		.menu-text {
			font-size: 28rpx;
			color: #666;
		}
		
		&.menu-active {
			background-color: #fff;
			
			.menu-text {
				color: #000;
				font-weight: bold;
			}
			
			&::before {
				content: '';
				position: absolute;
				left: 0;
				top: 50%;
				transform: translateY(-50%);
				width: 8rpx;
				height: 32rpx;
				background-color: #000;
				border-radius: 0 4rpx 4rpx 0;
			}
		}
	}
}

.right-content {
	flex: 1;
	width: 0;
	height: 100%;
	background-color: #fff;
	overflow-x: hidden;
	
	.topic-list {
		padding: 20rpx;
	}
	
	.topic-item {
		background-color: #fff;
		padding: 20rpx;
		margin-bottom: 20rpx;
		
		.topic-header {
			display: flex;
			align-items: flex-start;
			margin-bottom: 16rpx;
			
			.topic-avatar {
				margin-right: 20rpx;
				flex-shrink: 0;
			}
			
			.topic-info {
				flex: 1;
				overflow: hidden;
				padding-right: 20rpx;
				
				.topic-name {
					font-size: 32rpx;
					font-weight: 700;
					color: #000;
					margin-bottom: 8rpx;
				}
				
				.topic-desc {
					font-size: 26rpx;
					color: #666;
					overflow: hidden;
					text-overflow: ellipsis;
					white-space: nowrap;
				}
			}
		}
		
		.topic-footer {
			display: flex;
			align-items: center;
			justify-content: space-between;
			font-size: 24rpx;
			color: #999;
			margin-left: 100rpx;  // 80rpx(头像宽度) + 20rpx(头像右边距)
			
			.footer-item {
				display: flex;
				align-items: center;
				max-width: 45%;
				overflow: hidden;
				white-space: nowrap;
				
				::v-deep .u-icon {
					margin-right: 8rpx;
					flex-shrink: 0;
				}
				
				text {
					color: #000000;
					overflow: hidden;
					text-overflow: ellipsis;
					white-space: nowrap;
				}
			}
		}
	}
}
</style>
