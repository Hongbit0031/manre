<template>
	<view class="discuss-container">
		<view class="discussInfo">
			<view class="title">
				<view class="dis-title"># {{discussInfo.title}}</view>
				<view class="count">
					<text>{{discussInfo.postCount}} 篇内容</text>
					<text>{{numberFormat(discussInfo.readCount)}} 次浏览</text>
				</view>
			</view>
			<view class="desc">
				<view class="discuss-desc">{{discussInfo.introduce}}</view>
			</view>
			<view class="user" @click="jumpUser(discussInfo.userInfo.uid)">
				<text class="tag">
					发起人
				</text>
				<view class="user-c">
					<text>{{discussInfo.userInfo.username}}</text>
				</view>
				<u-avatar size="40" class="avatar" :src="discussInfo.userInfo.avatar"></u-avatar>
			</view>
		</view>
		<view class="tabs-box">
			<view class="tab-left">
				<u-tabs :list="tabList" :is-scroll="false" active-color="#000000" v-model="current"
					@change="tabsChange"></u-tabs>
			</view>
		</view>
		<view v-show="current == 0">
			<post-list :list="postList_data" :loadStatus="loadStatus"></post-list>
		</view>
		<view v-show="current == 1">
			<post-list :list="postList_data" :loadStatus="loadStatus"></post-list>
		</view>
		<!-- 返回顶部 -->
		<lf-back-top :show-tag="showTag"></lf-back-top>
		<!-- 发布按钮 -->
		<view @click="goAdd" url="/pages/post/add" class="plus-box">
			<u-icon color="#fff" name="plus"></u-icon>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad,
		onReachBottom,
		onPageScroll,
		onShareAppMessage,
		onShareTimeline
	} from '@dcloudio/uni-app'
	import {
		numberFormat
	} from "@/utils/filters.js"
	import postList from '@/components/post-list/post-list.vue'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H
	const $f = proxy.$f

	const disId = ref(0)
	const loadStatus = ref("loading")
	const postList_data = ref([])
	const discussInfo = ref({
		userInfo: {}
	})
	const page = ref(1)
	const page2 = ref(1)
	const current = ref(0)
	const tabList = ref([{
			name: '最新'
		},
		{
			name: '最热'
		}
	])
	const showTag = ref(false)

	onLoad((options) => {
		disId.value = options.id
		getDiscussInfo()
		getPostList()

		//#ifdef MP-WEIXIN
		wx.showShareMenu({
			withShareTicket: true,
			menus: ['shareAppMessage', 'shareTimeline']
		})
		//#endif
	})

	onShareAppMessage((res) => {
		if (res.from === 'button') {
			console.log(res.target)
		}
		return {
			title: discussInfo.value.introduce,
			path: '/pages/discuss/detail?id=' + disId.value,
		}
	})

	onShareTimeline(() => {
		let imgURL = ""
		return {
			title: discussInfo.value.introduce,
			imageUrl: imgURL,
			query: 'id=' + disId.value
		}
	})

	onReachBottom(() => {
		if (current.value == 0) {
			page.value++
		} else if (current.value == 1) {
			page2.value++
		}
		getPostList()
	})

	onPageScroll((e) => {
		if (e.scrollTop > 750) {
			showTag.value = true
		} else {
			showTag.value = false
		}
	})

	function goAdd() {
		if (uni.getStorageSync('hasLogin')) {
			$H.get('discuss/checkJoinTopic', {
				id: disId.value
			}).then(res => {
				if (res.code == 0) {
					uni.navigateTo({
						url: "/pages/post/add?topicId=" + discussInfo.value.topicId +
							"&discussId=" + discussInfo.value.id + "&type=1&disName=" + discussInfo.value
							.title
					})
				} else {
					setTimeout(function() {
						uni.navigateTo({
							url: "/pages/topic/detail?id=" + discussInfo.value.topicId
						})
					}, 1500)
				}
			})
		} else {
			$f.toast('请先登录哦')
		}
	}

	function getDiscussInfo() {
		$H.get('discuss/detail', {
			id: disId.value
		}).then(res => {
			discussInfo.value = res.result
		})
	}

	function getPostList() {
		loadStatus.value = "loading"
		let sort = "last"
		let pages = page.value
		if (current.value == 1) {
			sort = "hot"
			pages = page2.value
		}
		$H.post('post/getPostListByDiscussId', {
			disId: disId.value,
			page: pages,
			sort: sort
		}).then(res => {
			postList_data.value = postList_data.value.concat(res.result.data)

			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = "nomore"
			} else {
				loadStatus.value = "loadmore"
			}
		})
	}

	function jumpUser(uid) {
		uni.navigateTo({
			url: '/pages/user/home?uid=' + uid
		})
	}

	function tabsChange(index) {
		postList_data.value = []
		page.value = 1
		page2.value = 1
		current.value = index
		if (index == 0) {
			getPostList()
		} else if (index == 1) {
			getPostList()
		}
	}
</script>

<style lang="scss" scoped>
	.discuss-container {
		/* #ifdef APP-PLUS */
		background-color: #ebebeb;
		/* #endif */
		/* #ifndef APP-PLUS */
		background-color: #f9f9f9;
		/* #endif */
	}

	.discussInfo {

		padding: 20rpx;
		margin-bottom: 20rpx;
		background-color: #fff;
		align-items: center;

		.title {
			display: flex;
			justify-content: space-between;
			align-items: flex-end;
			margin: 10rpx 4rpx;
		}

		.desc {
			display: flex;
			align-items: center;

			.tag {
				width: 70rpx;
				height: 42rpx;
				font-size: 24rpx;
				text-align: center;
				border-radius: 10rpx;
				border: .5px solid #dddddd;
				margin-right: 15rpx;

			}
		}
	}

	.discussInfo>.user {
		display: flex;
		margin: 10rpx 4rpx;
		align-items: center;

		.tag {
			width: 100rpx;
			height: 42rpx;
			font-size: 24rpx;
			text-align: center;
			border-radius: 10rpx;
			border: .5px solid #dddddd;
			margin-right: 15rpx;
		}
	}

	.discussInfo>.user>.user-c {
		display: flex;
		flex-direction: column;
		margin-right: 15rpx;
	}

	.discussInfo>.user>.user-c text:nth-child(2) {
		font-size: 10px;
		color: #999;
	}

	.avatar {
		margin-right: 10rpx;
		font-size: 0;
	}

	.count {
		font-size: 12px;
		color: #999;
		margin-bottom: 10rpx;
	}

	.count text {
		margin: 0 10rpx;
	}

	.dis-title {
		font-size: 36rpx;
		font-weight: 600;
		color: #616161;
		width: 400rpx;
	}

	.discuss-desc {
		color: #616161;
	}

	.tabs-box {
		background-color: #FFFFFF;

		.tab-left {
			width: 25%;
		}
	}
</style>