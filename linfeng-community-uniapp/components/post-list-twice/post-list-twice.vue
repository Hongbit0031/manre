<template>
	<view>
		<view class="waterfall">
			<!-- 瀑布流左侧 -->
			<view class="post-list">
				<block v-for="(item, index) in leftList" :key="item.id || index">
					<view class="post-item-wrapper" @tap="handlePostClick(item)">
						<view class="post-item">
							<!-- 纯文字贴 -->
							<view v-if="item.type == 1 && (!item.media || item.media.length == 0)" 
								class="text-only-box"
								:class="{ 'text-center-box': item.content && item.content.length <= 50 }">
								<view class="text-only-content" 
									:class="{ 'text-center': item.content && item.content.length <= 50 }">
									{{ item.content && item.content.length > 100 ? item.content.substring(0, 100) + '...' : item.content }}
								</view>
							</view>
							
							<!-- 有媒体的帖子 -->
							<view v-else class="media-box">
								<!-- 图片封面 -->
								<image v-if="item.type == 1 && item.media && item.media.length > 0" 
									:src="item.media[0]" 
									mode="aspectFill"
									class="media-img" />
								<!-- 图片类型图标 -->
								<view v-if="item.type == 1 && item.media && item.media.length > 0" class="type-icon top-right">
									<u-icon name="photo" color="#ffffff" size="28"></u-icon>
								</view>
								
								<!-- 长文封面 -->
								<image v-else-if="item.type == 3" 
									:src="item.media[0]" 
									mode="aspectFill"
									class="media-img" />
								<!-- 长文类型图标 -->
								<view v-if="item.type == 3" class="type-icon top-right">
									<u-icon name="file-text" color="#ffffff" size="28"></u-icon>
								</view>
								
								<!-- 投票封面 -->
								<image v-else-if="item.type == 4" 
									src="/static/images/vote-cover.jpeg" 
									mode="aspectFill"
									class="media-img" />
								
								<!-- 视频封面 -->
								<image v-else-if="item.type == 2" 
									:src="getVideoThumb(item.media[0]) || item.media[0]"
									mode="aspectFill" 
									class="media-img" />
								<!-- 视频播放图标 -->
								<view v-if="item.type == 2" class="type-icon center">
									<u-icon name="play-right-fill" color="#ffffff" size="60"></u-icon>
								</view>
							</view>

							<view class="post-content">
								<!-- 纯文字贴不显示标题，因为内容已在上方显示 -->
								<view class="title" v-if="item.type == 1 && item.media && item.media.length > 0">
									<text>{{ item.title }}</text>
								</view>
								<!-- 长文和视频显示标题 -->
								<view class="title" v-else-if="item.type == 2 || item.type == 3">
									<text>{{ item.title }}</text>
								</view>
								<!-- 投票显示内容 -->
								<view class="title" v-else-if="item.type == 4">
									<text>{{ item.content }}</text>
								</view>

								<view class="post-footer">
									<view class="userBox" @tap.stop="handleUserClick(item.userInfo.uid)">
										<image class="user-avatar" :src="item.userInfo.avatar" />
										<text class="username">
											{{ item.userInfo.username.substring(0, 9) }}
										</text>
										<image v-if="item.userInfo.level"
											class="level-img"
											mode="heightFix"
											:src="$IMG+'/vip/level_'+item.userInfo.level+'.png'" />
									</view>

									<view class="collection-btn" 
										v-if="item.isCollection"
										@tap.stop="handleCancelCollection(item.id, index, 'left')">
										<u-icon name="heart-fill" color="#cc0000"></u-icon>
										<text class="collection-count">{{ item.collectionCount }}</text>
									</view>
									<view class="collection-btn" 
										v-else
										@tap.stop="handleAddCollection(item.id, index, 'left', item.uid)">
										<u-icon name="heart"></u-icon>
										<text class="collection-count">{{ item.collectionCount }}</text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</block>
			</view>

			<!-- 瀑布流右侧 -->
			<view class="post-list">
				<block v-for="(item, index) in rightList" :key="item.id || index">
					<view class="post-item-wrapper" @tap="handlePostClick(item)">
						<view class="post-item">
							<!-- 纯文字贴 -->
							<view v-if="item.type == 1 && (!item.media || item.media.length == 0)" 
								class="text-only-box"
								:class="{ 'text-center-box': item.content && item.content.length <= 50 }">
								<view class="text-only-content" 
									:class="{ 'text-center': item.content && item.content.length <= 50 }">
									{{ item.content && item.content.length > 100 ? item.content.substring(0, 100) + '...' : item.content }}
								</view>
							</view>
							
							<!-- 有媒体的帖子 -->
							<view v-else class="media-box">
								<!-- 图片封面 -->
								<image v-if="item.type == 1 && item.media && item.media.length > 0" 
									:src="item.media[0]" 
									mode="aspectFill"
									class="media-img" />
								<!-- 图片类型图标 -->
								<view v-if="item.type == 1 && item.media && item.media.length > 0" class="type-icon top-right">
									<u-icon name="photo" color="#ffffff" size="28"></u-icon>
								</view>
								
								<!-- 长文封面 -->
								<image v-else-if="item.type == 3" 
									:src="item.media[0]" 
									mode="aspectFill"
									class="media-img" />
								<!-- 长文类型图标 -->
								<view v-if="item.type == 3" class="type-icon top-right">
									<u-icon name="file-text" color="#ffffff" size="28"></u-icon>
								</view>
								
								<!-- 投票封面 -->
								<image v-else-if="item.type == 4" 
									src="/static/images/vote-cover.jpeg" 
									mode="aspectFill"
									class="media-img" />
								
								<!-- 视频封面 -->
								<image v-else-if="item.type == 2" 
									:src="getVideoThumb(item.media[0]) || item.media[0]"
									mode="aspectFill" 
									class="media-img" />
								<!-- 视频播放图标 -->
								<view v-if="item.type == 2" class="type-icon center">
									<u-icon name="play-right-fill" color="#ffffff" size="60"></u-icon>
								</view>
							</view>

							<view class="post-content">
								<!-- 纯文字贴不显示标题，因为内容已在上方显示 -->
								<view class="title" v-if="item.type == 1 && item.media && item.media.length > 0">
									<text>{{ item.title }}</text>
								</view>
								<!-- 长文和视频显示标题 -->
								<view class="title" v-else-if="item.type == 2 || item.type == 3">
									<text>{{ item.title }}</text>
								</view>
								<!-- 投票显示内容 -->
								<view class="title" v-else-if="item.type == 4">
									<text>{{ item.content }}</text>
								</view>

								<view class="post-footer">
									<view class="userBox" @tap.stop="handleUserClick(item.userInfo.uid)">
										<image class="user-avatar" :src="item.userInfo.avatar" />
										<text class="username">
											{{ item.userInfo.username.substring(0, 9) }}
										</text>
										<image v-if="item.userInfo.level"
											class="level-img"
											mode="heightFix"
											:src="$IMG+'/vip/level_'+item.userInfo.level+'.png'" />
									</view>

									<view class="collection-btn" 
										v-if="item.isCollection"
										@tap.stop="handleCancelCollection(item.id, index, 'right')">
										<u-icon name="heart-fill" color="#cc0000"></u-icon>
										<text class="collection-count">{{ item.collectionCount }}</text>
									</view>
									<view class="collection-btn" 
										v-else
										@tap.stop="handleAddCollection(item.id, index, 'right', item.uid)">
										<u-icon name="heart"></u-icon>
										<text class="collection-count">{{ item.collectionCount }}</text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</block>
			</view>
		</view>

		<!-- 加载状态 -->
		<view>
			<block v-if="dataList.length === 0 && loadStatus == 'nomore'">
				<u-empty margin-top="100" text="暂无内容" mode="favor"></u-empty>
			</block>
			<block v-else>
				<u-loadmore margin-bottom="50" margin-top="50" bg-color="#f5f5f5" :status="loadStatus" />
			</block>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, getCurrentInstance } from 'vue'
import storageConfig from '@/utils/storage-config.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const props = defineProps({
	dataList: {
		type: Array,
		default: () => []
	},
	loadStatus: {
		type: String,
		default: 'loading'
	}
})

const $IMG = ref(proxy.$IMG)

// 过滤需要展示的帖子
const filteredList = computed(() => {
	return props.dataList.filter(
		v => (v.media && v.media.length > 0 && v.cut != 1) || v.type == 4 || (v.type == 1 && (!v.media || v.media.length == 0))
	)
})

// 简单的左右交替分配
const leftList = computed(() => {
	const list = []
	filteredList.value.forEach((item, index) => {
		if (index % 2 === 0) {
			list.push(item)
		}
	})
	return list
})

const rightList = computed(() => {
	const list = []
	filteredList.value.forEach((item, index) => {
		if (index % 2 === 1) {
			list.push(item)
		}
	})
	return list
})

// 处理帖子点击
function handlePostClick(item) {
	if (!item || !item.id) return
	
	let url = ''
	
	// 图文 / 投票
	if (item.type == 1 || item.type == 4) {
		if (item.cut == 0) {
			url = '/pages/post/detail?id=' + item.id
			uni.navigateTo({ url })
		} else {
			$H.post('post/getVipPostInfo', {
				postId: item.id
			}).then(res => {
				if (res.result.isBuy) {
					url = '/pages/post/detail?id=' + item.id
				} else {
					url = '/pages/post/confirm?id=' + item.id + '&type=' + item.type
				}
				uni.navigateTo({ url })
			})
		}
		return
	}
	
	// 长文
	if (item.type == 3) {
		url = '/subpages/content/article/article?id=' + item.id
		uni.navigateTo({ url })
		return
	}
	
	// 视频
	if (item.type == 2) {
		// #ifdef APP-PLUS
		if (item.cut == 0) {
			url = '/pages/post/detail?id=' + item.id
			uni.navigateTo({ url })
		} else {
			$H.post('post/getVipPostInfo', {
				postId: item.id
			}).then(res => {
				if (res.result.isBuy) {
					url = '/pages/post/detail?id=' + item.id
				} else {
					url = '/pages/post/confirm?id=' + item.id + '&type=' + item.type
				}
				uni.navigateTo({ url })
			})
		}
		// #endif
		
		// #ifndef APP-PLUS
		if (item.cut == 0) {
			url = '/pages/post/video-detail?id=' + item.id
			uni.navigateTo({ url })
		} else {
			$H.post('post/getVipPostInfo', {
				postId: item.id
			}).then(res => {
				if (res.result.isBuy) {
					url = '/pages/post/video-detail?id=' + item.id
				} else {
					url = '/pages/post/confirm?id=' + item.id + '&type=' + item.type
				}
				uni.navigateTo({ url })
			})
		}
		// #endif
	}
}

// 处理用户点击
function handleUserClick(uid) {
	if (!uid) return
	uni.navigateTo({
		url: '/pages/user/home?uid=' + uid
	})
}

// 处理取消收藏
function handleCancelCollection(id, index, side) {
	if (!id) return
	
	const list = side === 'left' ? leftList.value : rightList.value
	const item = list[index]
	if (!item) return
	
	$H.post('post/cancelCollection', { id }).then(res => {
		if (res.code === 0) {
			item.isCollection = false
			item.collectionCount--
		}
	})
}

// 处理添加收藏
function handleAddCollection(id, index, side, uid) {
	if (!id) return
	
	const list = side === 'left' ? leftList.value : rightList.value
	const item = list[index]
	if (!item) return
	
	$H.post('post/addCollection', {
		id,
		uid: uid || item.uid
	}).then(res => {
		if (res.code === 0) {
			item.isCollection = true
			item.collectionCount++
		}
	})
}

// 获取视频缩略图
function getVideoThumb(url) {
	return storageConfig.getVideoThumb(url)
}
</script>

<style>
page {
	background-color: #fafafa;
}
</style>

<style lang="scss" scoped>
.waterfall {
	display: flex;
}

.post-list {
	width: 50%;
	display: flex;
	flex-direction: column;
	padding: 0 8rpx;
}

.post-item-wrapper {
	margin: 4rpx 0;
}

.post-item {
	background: #fff;
	box-shadow: 0px 1px 10px rgba(189, 189, 189, 0.2);
	border-radius: 13rpx;
	width: 100%;
	overflow: hidden;
}

.text-only-box {
	width: 100%;
	height: 320rpx;
	padding: 30rpx 20rpx;
	padding-bottom: 20rpx;
	box-sizing: border-box;
	background-color: #fff;
	display: flex;
	align-items: flex-start;
	justify-content: center;
	overflow: hidden;
	
	&.text-center-box {
		align-items: center;
	}
	
	.text-only-content {
		font-size: 30rpx;
		line-height: 1.6;
		font-weight: 500;
		color: #333333;
		word-break: break-all;
		text-decoration: underline;
		text-decoration-color: rgba(0, 0, 0, 0.15);
		text-decoration-thickness: 1rpx;
		text-underline-offset: 4rpx;
		
		overflow: hidden;
		display: -webkit-box;
		-webkit-line-clamp: 7;
		-webkit-box-orient: vertical;
		text-overflow: ellipsis;
		
		&.text-center {
			text-align: center;
		}
	}
}

.media-box {
	position: relative;
	width: 100%;
	height: 450rpx;
	background-color: #f5f5f5;
	overflow: hidden;

	.media-img {
		width: 100%;
		height: 100%;
		object-fit: cover;
		display: block;
	}
	
	.type-icon {
		position: absolute;
		z-index: 10;
		display: flex;
		align-items: center;
		justify-content: center;
		pointer-events: none;
		
		&.top-right {
			top: 16rpx;
			right: 16rpx;
			background: rgba(0, 0, 0, 0.4);
			border-radius: 50%;
			width: 48rpx;
			height: 48rpx;
			backdrop-filter: blur(4rpx);
		}
		
		&.center {
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);
			background: rgba(0, 0, 0, 0.5);
			border-radius: 50%;
			width: 100rpx;
			height: 100rpx;
			backdrop-filter: blur(8rpx);
		}
	}
}

.post-content {
	padding: 10rpx;
}

.title {
	font-size: 32rpx;
	height: 70rpx;
	line-height: 70rpx;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.post-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 10rpx;
}

.userBox {
	display: flex;
	align-items: center;
	flex: 1;
	
	.user-avatar {
		width: 42rpx;
		height: 42rpx;
		border-radius: 21rpx;
	}
	
	.username {
		font-size: 20rpx;
		font-weight: 600;
		margin-left: 10rpx;
		max-width: 140rpx;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		height: 27rpx;
		line-height: 27rpx;
	}
	
	.level-img {
		height: 22rpx;
		margin-left: 10rpx;
	}
}

.collection-btn {
	display: flex;
	align-items: center;
	padding: 8rpx;
	
	.collection-count {
		font-size: 20rpx;
		margin-left: 10rpx;
	}
}
</style>
