<template>
	<view>
		<view style="position: absolute;">
			<u-navbar :custom-back="onBack" back-icon-color="#fff" :background="background" :border-bottom="false">
			</u-navbar>
		</view>
		<video class="video_" :enable-danmu="true" :danmu-btn="true" :autoplay="true" :enable-play-gesture="true"
			:controls="false" :show-fullscreen-btn="false" :loop="true" :src="postDetail.media[0]"></video>
		<view class="tool-box">
			<view hover-class="none" @click="navigateToUserHome(postDetail.uid)" class="item">
				<u-avatar :src="postDetail.userInfo.avatar"></u-avatar>
			</view>
			<view class="item">
				<image v-show="postDetail.isCollection" @click="cancelCollection" src="/static/fav-1.png"></image>
				<image v-show="!postDetail.isCollection" @click="addCollection" src="/static/fav.png"></image>
				<text>{{ postDetail.collectionCount }}</text>
			</view>
			<view class="item" @click="goVideo()">
				<image src="/static/comment.png"></image>
				<text>{{ postDetail.commentCount }}</text>
			</view>
			<!-- #ifdef MP -->
			<button open-type="share" class="u-reset-button item">
				<image src="/static/share.png"></image>
			</button>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<button class="u-reset-button item" @click="copyPageUrl">
				<image src="/static/share.png"></image>
			</button>
			<!-- #endif -->
		</view>
		<!-- 底部内容 -->
		<view class="footer">
			<view class="go-video" @click="goVideo">前往视频专区<u-icon name="arrow-right-double"></u-icon></view>
			<view @click="navigateToUserHome(postDetail.uid)" hover-class="none" class="username">
				@{{ postDetail.userInfo.username }}</view>
			<view @click="goVideo">
				<text class="c-txt" @longpress="onCopy">{{ replace(postDetail.content) }}</text>
			</view>
		</view>
		<!-- 评论弹窗 -->
		<lf-popup v-model="commentPopup" height="50vh">
			<scroll-view scroll-y style="height: 45vh;" @scrolltolower="reachBottom">
				<view class="comment-box">
					<view class="title">评论（{{ postDetail.commentCount }}）</view>
					<!-- 帖子内容 -->
					<view class="comment-item" @longpress="onCopy">
						<image @click="jumpUser(postDetail.userInfo.uid)" class="avatar"
							:src="postDetail.userInfo.avatar"></image>
						<view class="c-content">
							<view class="user">
								<text>{{ postDetail.userInfo.username }}</text>
							</view>
							<text class="c-txt">{{ postDetail.content }}</text>
							<text class="time">{{ timeFormat(postDetail.createTime) }}</text>
						</view>
					</view>
					<view style="margin-bottom: 40rpx;" v-for="(item, index) in commentList" :key="index">
						<!-- 评论列表 -->
						<view class="comment-item" @longpress="delComment(item, index)">
							<image @click="jumpUser(item.userInfo.uid)" class="avatar" :src="item.userInfo.avatar">
							</image>
							<view class="c-content" @click="onReply(item)">
								<view class="user">
									<text style="color: #999;">{{ item.userInfo.username }}</text>
									<view v-if="item.isThumbs">
										<view @click.stop="cancelThumbs(item.id, index)" class="thumbs">
											<text class="num">{{ item.thumbs }}</text>
											<u-icon class="icon" size="40" name="heart-fill" color="#e62e00"></u-icon>
										</view>
									</view>
									<view v-else>
										<view @click.stop="onThumbs(item.id, index)" class="thumbs">
											<text class="num">{{ item.thumbs }}</text>
											<u-icon class="icon" size="40" name="heart-fill" color="#bfbfbf"></u-icon>
										</view>
									</view>
								</view>
								<text class="c-txt">{{ item.content }}</text>
								<text class="time">{{ timeFormat(item.createTime) }}</text>
							</view>
						</view>
						<view v-if="item.children.length > 0">
							<view @click="onReply(item2, index, index2)"
								v-for="(item2, index2) in item.children" :key="item2.id"
								@longpress="delComment(item2, index, index2)" class="sub-comment-item">
								<view class="user">
									<u-avatar class="avatar" :size="40" :src="item2.userInfo.avatar"></u-avatar>
									<view class="u-head">
										<text style="color: #999;">{{ item2.userInfo.username }}</text>
										<view v-if="item2.isThumbs">
											<view class="thumbs" @click.stop="cancelThumbs(item2.id, index, index2)">
												<text class="num">{{ item2.thumbs }}</text>
												<u-icon class="icon" size="40" name="heart-fill" color="#e62e00"></u-icon>
											</view>
										</view>
										<view v-else>
											<view class="thumbs" @click.stop="onThumbs(item2.id, index, index2)">
												<text class="num">{{ item2.thumbs }}</text>
												<u-icon class="icon" size="40" name="heart-fill" color="#bfbfbf"></u-icon>
											</view>
										</view>
									</view>
								</view>
								<view class="reply">
									<text>@</text>
									<view @click="navigateToUserHome(item2.toUser.uid)" hover-class="none"
										class="name">{{ item2.toUser.username }}</view>
									<text>：{{ item2.content }}</text>
									<view class="time">{{ timeFormat(item2.createTime) }}</view>
								</view>
							</view>
						</view>
						<view class="more-comment-btn" v-if="item.childrenCount>2 && item.showBtn"
							@click.stop="remainComment(item,index)">展开剩余回复</view>
						<u-line margin="40rpx 0 0 110rpx" length="85%"></u-line>
					</view>
					<!-- 加载状态 -->
					<view v-if="commentList.length > 0">
						<view style="margin: 30rpx;">
							<u-loadmore :status="loadStatus" />
						</view>
					</view>
					<view v-else>
						<u-empty text="暂无评论" mode="message"></u-empty>
					</view>
					<view style="height: 100rpx;"></view>
				</view>
			</scroll-view>
			<!-- 评论输入框 -->
			<view class="comment-tool">
				<textarea :placeholder="placeholder" @blur="onBlur" :focus="focus" confirm-type="send" fixed="true"
					cursor-spacing="10" v-model="form.content" auto-height="true"
					placeholder-class="txt-placeholder"></textarea>
				<u-button @click="addComment" :disabled="isSubmitD">发布</u-button>
			</view>
		</lf-popup>
	</view>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { onLoad, onReachBottom, onPullDownRefresh, onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app'
import { timeFormat } from '@/utils/filters.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $c = proxy.$c
const $f = proxy.$f
const $u = proxy.$u

// 响应式数据
const postDetail = ref({
	content: '',
	userInfo: {
		username: '',
		avatar: ''
	},
	createTime: '',
	commentCount: '',
	uid: '',
	media: []
})

const isSubmitD = ref(false)
const commentPopup = ref(false)
const commentList = ref([])
const focus = ref(false)
const placeholder = ref('说点什么...')
const page = ref(1)
const postId = ref(0)

const form = reactive({
	pid: 0,
	type: 1,
	toUid: '',
	postId: '',
	content: ''
})

const loadStatus = ref('loadmore')
const background = reactive({
	backgroundColor: '#070707',
})

// 过滤器函数
const replace = (str) => {
	if (!str) return ''
	str = str.replace(/\n/g, '')
	if (str.length > 20) {
		str = str.substring(0, 20) + '...'
	}
	return str
}

// 生命周期
onLoad((options) => {
	checkVideoOpen()
	postId.value = options.id

	if (options.scene) {
		postId.value = options.scene
	}

	form.postId = postId.value

	getPostDetail()
	getCommentList()
})

onReachBottom(() => {
	console.log('onReachBottom')
})

onPullDownRefresh(() => {
	console.log('onPullDownRefresh')
})

// 分享配置
onShareAppMessage((res) => {
	if (res.from === 'button') {
		console.log(res.target)
	}
	let imgURL
	if (postDetail.value.media.length > 0) {
		imgURL = postDetail.value.media[0]
	}
	return {
		title: postDetail.value.content,
		path: '/pages/post/video-detail?id=' + postId.value
	}
})

onShareTimeline(() => {
	let imgURL = postDetail.value.media[0]
	return {
		title: postDetail.value.content,
		query: 'id=' + postId.value
	}
})

// 方法
const checkVideoOpen = () => {
	$H.get('user/isOpen').then(res => {
		if (res.result == 0) {
			$f.toast("视频模块未开启")
			setTimeout(function() {
				uni.switchTab({
					url: '/pages/index/index'
				})
			}, 1000)
		}
	})
}

const openCommentPop = () => {
	commentPopup.value = true
}

const onBack = () => {
	let pages = getCurrentPages()
	if (pages.length > 1) {
		uni.navigateBack()
	} else {
		uni.switchTab({
			url: '/pages/index/index'
		})
	}
}

const goVideo = () => {
	uni.navigateTo({
		url: "/pages/post/video?id=" + postId.value
	})
}

const navigateToUserHome = (uid) => {
	uni.navigateTo({
		url: '/pages/user/home?uid=' + uid
	})
}

// 删除评论
const delComment = (e, index, index2) => {
	let user = uni.getStorageSync("userInfo")

	if (e.uid != user.uid) {
		return
	}
	commentPopup.value = false
	uni.showModal({
		title: '提示',
		content: '确定删除该评论？',
		success: function(res) {
			if (res.confirm) {
				$H.post('comment/del', {
					id: e.id
				}).then(res2 => {
					if (res2.code == 0) {
						if (index2 || index2 === 0) {
							commentList.value[index].children.splice(index2, 1)
						} else {
							commentList.value.splice(index, 1)
						}
					}
				})
			} else if (res.cancel) {
				// console.log('用户点击取消')
			}
		}
	})
}

const onCopy = () => {
	uni.setClipboardData({
		data: postDetail.value.content,
		success: function() {
			uni.hideToast()
			$f.toast('复制成功', 'success')
		}
	})
}

const copyPageUrl = () => {
	uni.setClipboardData({
		data: $c.shareH5Url + 'pages/post/video-detail?id=' + postId.value,
		success: function() {
			uni.hideToast()
			$f.toast('复制成功快分享给好友叭~', 'success')
		}
	})
}

// 评论触底
const reachBottom = () => {
	page.value++
	getCommentList()
}

const jumpUser = (uid) => {
	uni.navigateTo({
		url: '/pages/user/home?uid=' + uid
	})
}

// 点赞
const onThumbs = (id, index, index2) => {
	$H.post('comment/thumbs', {
		id: id
	}).then(res => {
		if (res.code == 0) {
			if (index2 || index2 == 0) {
				commentList.value[index].children[index2].isThumbs = true
				commentList.value[index].children[index2].thumbs++
			} else {
				commentList.value[index].isThumbs = true
				commentList.value[index].thumbs++
			}
		}
	})
}

// 取消点赞
const cancelThumbs = (id, index, index2) => {
	console.log(index + ',' + index2)
	$H.post('comment/cancelThumbs', {
		id: id
	}).then(res => {
		if (res.code == 0) {
			if (index2 || index2 == 0) {
				commentList.value[index].children[index2].isThumbs = false
				commentList.value[index].children[index2].thumbs--
			} else {
				commentList.value[index].isThumbs = false
				commentList.value[index].thumbs--
			}
		}
	})
}

// 回复评论
const onReply = (e) => {
	placeholder.value = '回复：' + e.userInfo.username
	focus.value = true

	let pid = e.pid
	if (pid === 0) {
		form.pid = e.id
	} else {
		form.pid = e.pid
	}

	form.toUid = e.userInfo.uid
	form.postId = postId.value
}

// 输入框失去焦点时
const onBlur = () => {
	placeholder.value = '说点什么...'
	focus.value = false
	form.pid = 0
}

// 获取评论列表
const getCommentList = () => {
	loadStatus.value = 'loading'
	$H.get('comment/list', {
		postId: postId.value,
		page: page.value
	}).then(res => {
		if (res.code == 0) {
			commentList.value = commentList.value.concat(res.result.data)

			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = 'nomore'
			} else {
				loadStatus.value = 'loadmore'
			}
		}
	})
}

const cancelCollection = () => {
	$H.post('post/cancelCollection', {
		id: postId.value
	}).then(res => {
		if (res.code === 0) {
			postDetail.value.collectionCount--
			postDetail.value.isCollection = false
		}
	})
}

const addCollection = () => {
	$H.post('post/addCollection', {
		id: postId.value,
		uid: postDetail.value.uid
	}).then(res => {
		if (res.code === 0) {
			postDetail.value.collectionCount++
			postDetail.value.isCollection = true
		}
	})
}

const addComment = () => {
	isSubmitD.value = true
	if (form.content == '') {
		$u.toast('评论不能为空')
		isSubmitD.value = false
		return
	}

	uni.showLoading({
		mask: true,
		title: '发布中'
	})

	$H.post('post/addComment', form).then(res => {
		if (res.code == 0) {
			form.content = ''
			$u.toast('评论成功')
			page.value = 1
			commentList.value = []
			getCommentList()
		}
		isSubmitD.value = false
		uni.hideLoading()
	})
}

const getPostDetail = () => {
	$H.get('post/detail', {
		id: postId.value
	}).then(res => {
		postDetail.value = res.result
		var decryptedUrl = $f.decryptUrl(res.result.media[0])
		var list = JSON.parse(decryptedUrl)
		postDetail.value.media[0] = list[0]
	})
}

const remainComment = (item, index) => {
	$H.get('comment/remainComment', {
		id: item.id
	}).then(res => {
		if (res.code == 0) {
			commentList.value[index].children = commentList.value[index].children.concat(res.result)
			commentList.value[index].showBtn = false
		}
	})
}
</script>

<style lang="scss" scoped>
	.video_ {
		width: 100%;
		height: 100vh;
		display: block;
	}

	.tool-box {
		position: absolute;
		bottom: 100rpx;
		right: 30rpx;
		color: #fff;
		font-size: 24rpx;
		z-index: 999;

		.item {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			margin-bottom: 30rpx;

			.iconfont {
				font-size: 60rpx;
			}

			image {
				width: 50rpx;
				height: 50rpx;
			}
		}
	}

	/* 评论tool */
	.comment-tool {
		position: fixed;
		bottom: 0;
		width: 100%;
		background-color: #fff;
		padding: 20rpx;
		display: flex;
		z-index: 999;
	}

	.comment-tool textarea {
		padding: 20rpx;
		border-radius: 10rpx;
		min-height: 40rpx;
	}

	.comment-tool .u-btn {
		margin-left: 10rpx;
	}


	/*评论列表*/
	.comment-box {
		background-color: #ffffff;
		margin-top: 20rpx;
	}

	.comment-box>.title {
		margin: 20rpx;
	}

	.comment-item {
		display: flex;
		padding: 20rpx;

		&:active {
			background-color: #F5F5F5;
		}

		.c-content {
			display: flex;
			flex-direction: column;
			flex: 1;

			.time {
				color: #999;
				font-size: 10px;
			}

			.user {
				display: flex;

				.thumbs {
					margin-left: auto;
					display: flex;
					align-items: center;
					color: #bfbfbf;

					.num {
						margin-right: 10rpx;
					}
				}
			}
		}

		.avatar {
			width: 100rpx;
			height: 100rpx;
			border-radius: 50%;
			margin-right: 20rpx;
		}
	}

	.sub-comment-item {
		margin-left: 100rpx;
		padding: 20rpx;

		&:active {
			background-color: #F5F5F5;
		}

		.user {
			display: flex;
			align-items: center;

			.name {
				color: #616161;
			}

			.avatar {
				margin-right: 10rpx;
			}

			.u-head {
				flex: 1;
				display: flex;

				.thumbs {
					margin-left: auto;
					display: flex;
					align-items: center;
					color: #bfbfbf;

					.num {
						margin-right: 10rpx;
					}
				}
			}
		}

		.reply {
			.time {
				margin-left: auto;
				font-size: 20rpx;
				color: #999;
			}

			.name {
				display: inline-block;
				color: #2b85e4;
				font-weight: 600;
			}
		}
	}

	// 底部内容框
	.footer {
		position: absolute;
		bottom: 0;
		width: 100%;
		color: #fff;
		padding: 20rpx;

		.username {
			margin-bottom: 20rpx;
		}
	}

	.go-video {
		background-color: rgba(239, 236, 236, 0.3);
		color: #fff;
		font-size: 30rpx;
		opacity: 1;
		width: 244rpx;
		height: 52rpx;
		margin-bottom: 20rpx;
		border-radius: 5%;
		padding: 0rpx 14rpx;
	}
</style>