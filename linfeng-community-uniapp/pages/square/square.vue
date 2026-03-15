<template>
	<view>
		<u-navbar :is-back="false" :border-bottom="false" :background="background">
			<u-icon name="search" :size="40" class="search-wrap" @click="toSearch"></u-icon>
			<view class="tabs-wrapper">
				<u-tabs :list="pageTab" active-color="#6366f1" inactive-color="#94a3b8" v-model="pageCurrent" @change="pageTabChange"
					bg-color=""></u-tabs>
			</view>
		</u-navbar>
		<!-- 轮播图 -->
		<view class="swiper-box">
			<u-swiper @click="onSwiper" :list="swiperList" name="img" border-radius="20" mode="rect"
				indicator-pos="bottomRight"></u-swiper>
		</view>
		<!-- 热榜 -->
		<view v-if="pageCurrent == 0">
			<!-- 公告 -->
			<view v-if="showNotice">
				<u-notice-bar :list="noticeContent" type="none"></u-notice-bar>
			</view>
			<!-- 圈子推荐 -->
			<topic-recommend v-if="topicHotList.length" :items="topicHotList"></topic-recommend>
			<!-- 热门话题 -->
			<discuss-recommend v-if="discussList.length>0" :list="discussList"></discuss-recommend>
			<!-- 热门用户推荐 -->
			<user-recommend v-if="hotUserList.length>0" type="scroll" :users="hotUserList" @follow="follow"
				@cancelFollow="cancelFollow"></user-recommend>

			<!-- tabs -->
			<u-tabs :list="classList" name="cateName" active-color="#000000" v-model="current" @change="tabChange"></u-tabs>
			<!-- 圈子列表 -->
			<navigator class="topic-wrap" hover-class="none" :url="'/pages/topic/detail?id=' + item.id"
				v-for="(item, index) in topicList" :key="index">
				<view class="info-wrap">
					<image class="cover-img" mode="aspectFill" :src="item.coverImage"></image>
					<view class="right">
						<view class="name">{{ item.topicName }}</view>
						<view class="count-wrap">
							<text>{{ numberFormat(item.userNum)}}人加入</text>
							<text>{{ numberFormat(item.postNum)}}条动态</text>
						</view>
					</view>
				</view>
				<view class="post-img-wrap" v-if="item.imgList.length > 0">
					<image mode="aspectFill" v-for="(item2, index2) in item.imgList" :key="index2" :src="item2"></image>
				</view>
			</navigator>
			<!-- 加载状态 -->
			<block v-if="topicList.length === 0 && loadStatus == 'nomore'">
				<u-empty margin-top="100" text="暂无内容" mode="favor"></u-empty>
			</block>
			<block v-else>
				<view style="margin: 30rpx 0;">
					<u-loadmore :status="loadStatus" />
				</view>
			</block>
		</view>
		<view v-if="pageCurrent == 1">
			<!-- 导航区 -->
			<nav-pane :content="navContent" v-if="navContent.length>0"></nav-pane>
			<!-- 帖子热榜 -->
			<post-recommend title="热门贴" desc="热帖推荐" :hotPost="hotPost"></post-recommend>

		</view>
		<!-- tabbar -->
		<lf-tabbar :active="1" :count="messegeNum"></lf-tabbar>
		<!-- 自定义加载 -->
		<toast iconBg="#aaaaff" color="#fff"></toast>
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
		onReachBottom,
		onPullDownRefresh
	} from '@dcloudio/uni-app'
	import {
		useStore
	} from 'vuex'
	import {
		numberFormat
	} from "@/utils/filters.js"
	import topicRecommend from "@/components/topic-recommend/topic-recommend.vue"
	import userRecommend from "@/components/user-recommend/user-recommend.vue"
	import discussRecommend from "@/components/discuss-recommend/discuss-recommend.vue"
	import postRecommend from "@/components/post-recommend/post-recommend.vue"
	import navPane from "@/components/nav-pane/nav-pane.vue"

	const {
		proxy
	} = getCurrentInstance();
	const $H = proxy.$H;
	const $f = proxy.$f;
	const $loading = proxy.$loading;
	const store = useStore();

	const page = ref(1);
	const classList = ref([{
		cateId: '',
		cateName: '最热'
	}]);
	const classId = ref('');
	const topicList = ref([]);
	const loadStatus = ref('loadmore');
	const current = ref(0);
	const topDisList = ref([]);
	const swiperList = ref([]);
	const topicHotList = ref([]);
	const noticeContent = ref([]);
	const showNotice = ref(false);
	const hotUserList = ref([]);
	const discussList = ref([]);
	const pageTab = ref([{
		name: '推荐'
	}, {
		name: '热榜'
	}]);
	const pageCurrent = ref(0);
	const hotList = ref([]);
	const hotPost = ref([]);
	const navContent = ref([]);
	const background = ref({
		backgroundImage: 'linear-gradient(180deg, #eef2ff 0%, #ffffff 100%)'
	});
	const iosClose = ref('');
	const isIphone = ref(getApp().globalData.iphone);

	const messegeNum = computed(() => store.getters.messegeNum);

	onLoad(() => {
		$loading(true);
		getNotice();
		getClassList();
		getLinkList();
		getDiscussList();
		getHotUserList();
		getHotTopic();
		getTopicList();
		getHotPost();
		getSysInfo();
	});

	onShow(() => {
		getMsgNum();
	});

	onReachBottom(() => {
		page.value++;
		getTopicList();
	});

	onPullDownRefresh(() => {
		getLinkList();
		getHotTopic();
		getClassList();
		// getNotice();
		getDiscussList();
		page.value = 1;
		topicList.value = [];
		getTopicList();
		getHotUserList();
		uni.stopPullDownRefresh();
	});

	function follow(id, index) {
		$H.post('user/addFollow', {
			id: id
		}).then(res => {
			if (res.code == 0) {
				hotUserList.value[index].isFollow = true;
				$f.toast('关注成功')
			}
		});
	}

	function cancelFollow(id, index) {
		$H.post('user/cancelFollow', {
			id: id
		}).then(res => {
			if (res.code == 0) {
				hotUserList.value[index].isFollow = false;
				$f.toast('取关成功')
			}
		});
	}

	function getHotPost() {
		$H.get('post/hotPost').then(res => {
			if (res.code == 0) {
				hotPost.value = res.result;
			}
		});
	}

	function pageTabChange(index) {
		pageCurrent.value = index
	}

	function getDiscussList() {
		$H.get('discuss/hotDiscussList').then(res => {
			discussList.value = res.result;
		});
	}

	function getNotice() {
		$H.get('topic/notice').then(res => {
			if (res.code == 0) {
				noticeContent.value = res.result;
				if (res.result[0] == '') {
					showNotice.value = false
				} else {
					showNotice.value = true
				}
			}
			$loading(false);
		});
	}

	function getHotTopic() {
		$H.get('topic/hot').then(res => {
			topicHotList.value = res.result;
		});
	}

	function jumpTopic(id) {
		uni.navigateTo({
			url: '/pages/topic/detail?id=' + id
		});
	}

	function previewImage(url, urls) {
		uni.previewImage({
			current: url, // 当前显示图片的http链接
			urls: urls // 需要预览的图片http链接列表
		});
	}

	// 获取圈子列表
	function getTopicList() {
		loadStatus.value = 'loading';
		$H.get('topic/classTopicAreImg', {
			classId: classId.value,
			page: page.value
		}).then(res => {
			topicList.value = topicList.value.concat(res.result.data);
			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = 'nomore';
			} else {
				loadStatus.value = 'loadmore';
			}
		});
	}

	// 处理点击轮播图跳转
	function onSwiper(index) {
		let link = swiperList.value[index];

		//跳转页面
		if (link.type == 1) {
			// #ifdef MP-WEIXIN
			uni.navigateTo({
				url: '/pages/webview/webview?src=' + link.url
			});
			// #endif

			// #ifdef H5
			window.open(link.url)
			// #endif
			// #ifdef APP-PLUS
			plus.runtime.openURL(link.url)
			// #endif
		}

		//跳转其他小程序
		if (link.type == 2) {
			uni.navigateToMiniProgram({
				appId: link.appid,
				path: link.url
			})
		}

		//跳转小程序页面
		if (link.type == 3) {
			if (link.url == '/pages/index/index' || link.url == '/pages/square/square' || link.url ==
				'/pages/message/message' || link.url == '/pages/my/my') {
				uni.switchTab({
					url: link.url
				})
			} else {
				uni.navigateTo({
					url: link.url
				})
			}
		}
	}

	// 获取轮播图
	function getLinkList() {
		$H.post('link/list', {
			cateId: 0
		}).then(res => {
			swiperList.value = res.result;
		});
	}

	function getSysInfo() {
		$H.get('system/basic').then(res => {
			iosClose.value = res.iosClose;
			getNavList();
		});
	}

	// 获取导航区
	function getNavList() {
		$H.post('navigation/getNav').then(res => {
			if (res.code == 0) {
				if (isIphone.value && iosClose.value == '0') {
					let list = res.result
					const excludedValues = ['/pages/user/vip/vip', '/pages/account/account', '/pages/pay/pay'];

					navContent.value = list.filter(item => !excludedValues.includes(item.url));
				} else {
					navContent.value = res.result;
				}
			}
		});
	}

	// 获取热门博主
	function getHotUserList() {
		$H.get('user/getHotUserList').then(res => {
			hotUserList.value = res.result;
		});
	}

	function getMsgNum() {
		$H.post('message/num').then(res => {
			const msgNum = res.result;
			let all = store.state.totalUnread.message + store.state.totalUnread.notice + res.result.allCount
			store.state.messegeNum = [0, 0, 0, all, 0];
		});
	}

	function getClassList() {
		$H.get('topic/classList').then(res => {
			let classListData = [{
				cateId: '',
				cateName: '最热'
			}];

			classList.value = classListData.concat(res.result);
		});
	}

	function tabChange(index) {
		page.value = 1;
		current.value = index;
		classId.value = classList.value[index].cateId;
		topicList.value = [];
		getTopicList();
	}

	function toSearch() {
		uni.navigateTo({
			url: '/pages/search/search'
		});
	}
</script>
<style>
	page {
		background-color: #f5f5f5;
	}
</style>
<style lang="scss" scoped>
	.search-wrap {
		margin-left: 20rpx;
		padding: 12rpx 20rpx;
		border-radius: 50rpx;
		backdrop-filter: blur(20rpx);
		box-shadow: 0 2rpx 12rpx rgba(99, 102, 241, 0.08);
		transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
		
		&:active {
			background: rgba(255, 255, 255, 0.95);
			transform: scale(0.96);
			box-shadow: 0 4rpx 16rpx rgba(99, 102, 241, 0.12);
		}
	}

	/* #ifdef MP-WEIXIN */
	::v-deep .u-slot-content {
		justify-content: center !important;
		position: relative;
	}
	/* #endif */

	.tabs-wrapper {
		position: absolute;
		left: 50%;
		transform: translateX(-50%);
		display: flex;
		justify-content: center;
		align-items: center;
		width: 300rpx;
		/* #ifdef MP-WEIXIN */
		position: relative;
		left: auto;
		transform: none;
		margin: 0 auto;
		/* #endif */
	}


	.wrap {
		padding: 0 40rpx;
	}

	.swiper-box {
		padding: 10rpx;
	}

	// 热门圈子
	.hot-topic {
		padding: 20rpx;
		display: flex;

		.topic-item {
			margin: 0 20rpx;
			width: 100rpx;

			.cover-box {
				position: relative;

				.cover-img {
					width: 100%;
					height: 100rpx;
					border-radius: 50%;
				}
			}

			.footer {
				font-size: 24rpx;

				.name {
					display: inline-block;
					width: 120rpx;
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}

				.user-num {
					color: #999;
				}
			}
		}
	}

	// 块标题
	.block-title {
		font-weight: bold;
		padding: 20rpx;
		color: #616161;
		display: flex;
		font-size: 28rpx;

		.right {
			margin-left: auto;
			color: #999;
			font-size: 24rpx;
			display: flex;
			align-items: center;
		}
	}

	// 帖子
	.topic-wrap {
		background-color: #fff;
		margin: 30rpx;
		padding: 20rpx;
		border-radius: 20rpx;

		.info-wrap {
			display: flex;

			.cover-img {
				width: 100rpx;
				height: 100rpx;
				border-radius: 50%;
				margin-right: 20rpx;
			}

			.right {
				.name {
					font-weight: bold;
				}

				.count-wrap {
					font-size: 24rpx;
					color: #616161;

					text {
						margin-right: 10rpx;
					}
				}
			}
		}

		.post-img-wrap {
			display: grid;
			grid-template-columns: repeat(3, 1fr);
			grid-gap: 10rpx;
			margin-top: 20rpx;

			image {
				width: 100%;
				height: 200rpx;
				border-radius: 10rpx;
			}
		}
	}
</style>