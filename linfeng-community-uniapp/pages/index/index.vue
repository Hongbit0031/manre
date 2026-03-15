<template>
	<view class="index-con" :style="{paddingTop:navbarHeight +'px'}">
		<!-- navbar -->
		<view id="navbar" class="lf-nav" :style="{paddingTop:statusBarHeight +'px'}">
			<view class="lf-all">
				<view class="lf-all-nav" :style="{height: navigationBarHeight+'px'}">
					<view class="navbar-img" @click="refresh()">
						<image :src="shareCover" mode="" class="n-img"></image>
					</view>
					<view class="search-wrap" @click="toSearch">
						<u-search shape="round" height="60" placeholder="搜索帖子/用户/圈子" :show-action="false"></u-search>
					</view>
				</view>
				<view class="lf-tab">
					<view class="lf-tab-con">
						<u-tabs :list="tabList" font-size="28" name="cateName" v-model="current" @change="tabChange"
							active-color="#000000" inactive-color="#949494" bar-height="6" bar-width="20" height="40">
						</u-tabs>
					</view>
					<!-- 筛选按钮 -->
					<view class="lf-filter" v-if="current === 2">
						<index-filter @filterChange="handleFilterChange" />
					</view>
					<view class="lf-vip" v-if="current != 2 && vipShow=='0'&& showVipIcon && iosCheck">
						<image @click="toVipPage()" src="/static/join-vip.png" class="lf-vip-icon"></image>
					</view>
				</view>
			</view>
		</view>
		<view class="lf-top"></view>
		<!-- 最新 -->
		<view v-if="current === 2">
			<post-list v-if="indexStyle1=='0'" :list="lastPost" :loadStatus="loadStatus4" :showTag="true"
				:open="adIsOpen" :openAd="true" :wxAdpid="wxAdpid" :h5Adpid="h5Adpid"></post-list>
			<post-list-twice v-if="indexStyle1=='1'" :dataList="lastPost" :loadStatus="loadStatus4"></post-list-twice>
			<post-list-common v-if="indexStyle1=='2'" :list="lastPost" :loadStatus="loadStatus4" :showTag="true"
				:open="adIsOpen" :openAd="true" :wxAdpid="wxAdpid" :h5Adpid="h5Adpid"></post-list-common>
		</view>
		<!-- 关注 -->
		<view v-if="current === 0">
			<post-list v-if="indexStyle3=='0'" :list="followUserPost" :loadStatus="loadStatus1" :showTag="true"
				:open="adIsOpen" :openAd="true" :wxAdpid="wxAdpid" :h5Adpid="h5Adpid"></post-list>
			<post-list-twice v-if="indexStyle3=='1'" :dataList="followUserPost"
				:loadStatus="loadStatus1"></post-list-twice>
			<post-list-common v-if="indexStyle3=='2'" :list="followUserPost" :loadStatus="loadStatus1" :showTag="true"
				:open="adIsOpen" :openAd="true" :wxAdpid="wxAdpid" :h5Adpid="h5Adpid"></post-list-common>
		</view>
		<view v-if="current === 1">
			<!-- 我的圈子 -->
			<view class="topic-wrap">
				<view class="block-title">
					<view>我加入的圈子</view>
				</view>
				<u-grid @click="jump" :col="5" :border="false">
					<!-- 我的圈子 -->
					<u-grid-item :index="'/pages/topic/detail?id='+item.id" v-for="(item, index) in joinTopicList"
						:key="index">
						<view class="grid-topic">
							<u-image width="120rpx" :border-radius="10" height="120rpx" :src="item.coverImage">
							</u-image>
							<view class="name">{{ item.topicName.substring(0, 5) }}</view>
							<text v-if="sessionUid == item.uid" class="user">圈主</text>
						</view>
					</u-grid-item>
					<!-- 创建圈子 -->
					<u-grid-item index="/pages/topic/add/add">
						<navigator class="grid-topic">
							<u-image width="120rpx" :border-radius="10" height="120rpx" src="/static/add-1.png">
							</u-image>
							<view class="name">创建圈子</view>
						</navigator>
					</u-grid-item>
				</u-grid>
			</view>
			<post-list v-if="indexStyle2=='0'" :list="joinTopicPost" :loadStatus="loadStatus3" :showTag="true"
				:open="adIsOpen" :openAd="true" :wxAdpid="wxAdpid" :h5Adpid="h5Adpid"></post-list>

			<post-list-twice v-if="indexStyle2=='1'" :dataList="joinTopicPost"
				:loadStatus="loadStatus3"></post-list-twice>

			<post-list-common v-if="indexStyle2=='2'" :list="joinTopicPost" :loadStatus="loadStatus3" :showTag="true"
				:open="adIsOpen" :openAd="true" :wxAdpid="wxAdpid" :h5Adpid="h5Adpid"></post-list-common>

		</view>
		<!-- 首页自定义弹框 -->
		<u-mask :show="showNotice">
			<view class="system-pop">
				<view class="pop-title">{{popTitle}}</view>
				<view class="popup-txt">{{popContent}}</view>
				<view class="popup-bot" @click="closePop">
					<button>我知道了</button>
				</view>
			</view>
		</u-mask>
		<!-- tabbar -->
		<lf-tabbar :active="0" :count="messegeNum"></lf-tabbar>
		<!-- 返回顶部 -->
		<lf-back-top :show-tag="showTag"></lf-back-top>
		<!-- 自定义加载 -->
		<toast color="#fff" type="rotate3"></toast>
		<!-- H5端备案信息 ICP备案号自行填写 -->
		<!-- #ifdef H5 -->
		<view class="beian-info" @click="goBeian">
			<text>© m.scs.baby</text>
		</view>
		<!-- #endif -->
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
		onPullDownRefresh,
		onPageScroll,
		onShareAppMessage
	} from '@dcloudio/uni-app'
	import {
		useStore
	} from 'vuex'
	import postList from '@/components/post-list/post-list.vue'
	import postListTwice from '@/components/post-list-twice/post-list-twice.vue'
	import postListCommon from '@/components/post-list-common/post-list-common.vue'
	import indexFilter from '@/components/index-filter/index-filter.vue'

	const {
		proxy
	} = getCurrentInstance();
	const $H = proxy.$H;
	const $c = proxy.$c;
	const $loading = proxy.$loading;
	const $f = proxy.$f;
	const store = useStore();

	const $IMG = ref(proxy.$IMG);
	const sessionUid = ref(uni.getStorageSync('userInfo').uid);
	const loadStatus1 = ref('loadmore');
	const loadStatus2 = ref('loadmore');
	const loadStatus3 = ref('loadmore');
	const loadStatus4 = ref('loadmore');
	const page = ref(1);
	const page2 = ref(1);
	const page3 = ref(1);
	const page4 = ref(1);
	const shareCover = ref('');
	const topDisList = ref([]);
	const swiperList = ref([]);
	const followUserPost = ref([]);
	const joinTopicPost = ref([]);
	const lastPost = ref([]);
	const tabList = ref([{
		name: '关注'
	}, {
		name: '圈子'
	}, {
		name: '最新'
	}]);
	const current = ref(2);
	const joinTopicList = ref([]);
	const wxText = ref("点击添加到我的小程序");
	const background = ref({
		backgroundImage: 'linear-gradient(45deg, rgb(210,227,235), rgb(173, 173, 220))'
	});
	const h5Adpid = ref('1818425366');
	const wxAdpid = ref('1872486102');
	const adIsOpen = ref('0');
	const navbarHeight = ref(0);
	const navigationBarHeight = ref(0);
	const statusBarHeight = ref(0);
	const indexStyle1 = ref('0');
	const indexStyle2 = ref('0');
	const indexStyle3 = ref('0');
	const vipShow = ref('0');
	const showTag = ref(false);
	const popTitle = ref("");
	const popContent = ref("");
	const showNotice = ref(false);
	const showVipIcon = ref(true);
	const iosCheck = ref(false);
	const isIphone = ref(getApp().globalData.iphone);
	const filterPost = ref(0);
	const typePost = ref(0);

	const messegeNum = computed(() => store.getters.messegeNum);

	onLoad(() => {
		$loading(true);
		navigationBarHeight.value = getApp().globalData.navigationBarHeight;
		statusBarHeight.value = getApp().globalData.statusBarHeight;
		getSysInfo();
		getLastPost();
		getAdConfig();
		getPop();
		if (uni.getStorageSync('hasLogin')) {
			if (uni.getStorageSync('userInfo').vip == 1) {
				showVipIcon.value = false
			}
		}
	});

	onShow(() => {
		getMsgNum();
		checkCurrentPage();
	});

	onReachBottom(() => {
		if (current.value === 0) {
			page2.value++;
			getFollowUserPost();
		} else if (current.value === 1) {
			page3.value++;
			getJoinTopicPost();
		} else if (current.value === 2) {
			page4.value++;
			getLastPost();
		}
	});

	onPullDownRefresh(() => {
		if (current.value === 0) {
			page2.value = 1;
			followUserPost.value = [];
			getFollowUserPost();
			getMsgNum();
		} else if (current.value === 1) {
			page3.value = 1;
			joinTopicPost.value = [];
			getJoinTopicPost();
			getUserJoinTopic();
			getMsgNum();
		} else if (current.value === 2) {
			page4.value = 1;
			lastPost.value = [];
			getLastPost();
		}
		uni.stopPullDownRefresh();
	});

	onPageScroll((e) => {
		if (e.scrollTop > 750) {
			showTag.value = true
		} else {
			showTag.value = false
		}
	});

	onShareAppMessage((res) => {
		return {
			title: $c.miniappName,
			path: '/pages/index/index',
			imageUrl: shareCover.value
		};
	});

	function handleFilterChange(result) {
		// console.log('筛选结果：', result);
		// 根据筛选结果处理数据
		if (result.style) {
			indexStyle1.value = result.style === 'twice' ? '1' :
				result.style === 'old' ? '2' : '0';
			indexStyle2.value = result.style === 'twice' ? '1' :
				result.style === 'old' ? '2' : '0';
			indexStyle3.value = result.style === 'twice' ? '1' :
				result.style === 'old' ? '2' : '0';
		}
		typePost.value = result.type
		filterPost.value = result.filter
		// 刷新数据
		refresh();
	}

	function refresh() {
		if (current.value === 0) {
			page2.value = 1;
			followUserPost.value = [];
			getFollowUserPost();
			getMsgNum();
		} else if (current.value === 1) {
			page3.value = 1;
			joinTopicPost.value = [];
			getJoinTopicPost();
			getUserJoinTopic();
			getMsgNum();
		} else if (current.value === 2) {
			page4.value = 1;
			lastPost.value = [];
			getLastPost();
		}
	}

	function getMsgNum() {
		$H.post('message/num').then(res => {
			const msgNum = res.result;
			let all = store.state.totalUnread.message + store.state.totalUnread.notice + res.result.allCount
			store.state.messegeNum = [0, 0, 0, all, 0];
		});
	}

	function checkCurrentPage() {
		// 跳转到登录需要刷新页面
		if ($f.myCache('lf_login_index')) {
			if (current.value === 0) {
				page2.value = 1;
				followUserPost.value = [];
				getFollowUserPost();
				getMsgNum();
			} else if (current.value === 1) {
				page3.value = 1;
				joinTopicPost.value = [];
				getJoinTopicPost();
				getUserJoinTopic();
				getMsgNum();
			}
			uni.removeStorageSync('lf_login_index')
		}
	}

	function tabChange(index) {
		// console.log('index:',index)
		current.value = index;
		followUserPost.value = [];
		joinTopicList.value = [];
		lastPost.value = [];
		joinTopicPost.value = [];
		if (index === 1) {
			if (uni.getStorageSync('hasLogin')) {
				page3.value = 1;
				getJoinTopicPost();
				getUserJoinTopic();
				getMsgNum();
			} else {
				$f.myCache('lf_login_index', '1', 300)
				// #ifdef MP-WEIXIN
				$f.toast('登录后即可查看')
				// #endif
				// #ifndef MP-WEIXIN
				uni.navigateTo({
					url: "/pages/user/go-login"
				})
				// #endif
			}
		}
		if (index === 0) {
			if (uni.getStorageSync('hasLogin')) {
				page2.value = 1;
				getFollowUserPost();
				getMsgNum();
			} else {
				$f.myCache('lf_login_index', '1', 300)
				// #ifdef MP-WEIXIN
				$f.toast('登录后即可查看')
				// #endif
				// #ifndef MP-WEIXIN
				uni.navigateTo({
					url: "/pages/user/go-login"
				})
				// #endif
			}
		}
		if (index === 2) {
			page4.value = 1;
			getLastPost();
		}
	}

	// 用户加入的圈子
	function getUserJoinTopic() {
		loadStatus2.value = 'loading';
		$H.post('topic/userJoinTopic', {
			classId: ''
		}).then(res => {
			joinTopicList.value = res.result.data;
			uni.stopPullDownRefresh();
		});
	}

	function jump(url) {
		uni.navigateTo({
			url: url
		})
	}

	function getSysInfo() {
		$H.get('system/basic').then(res => {
			shareCover.value = res.logo;
			indexStyle1.value = res.indexStyle1;
			indexStyle2.value = res.indexStyle2;
			indexStyle3.value = res.indexStyle3;
			//如果用户之前自定义设置过样式，那就按自定义样式显示
			if (uni.getStorageSync('activeStyle')) {
				let style = uni.getStorageSync('activeStyle')
				indexStyle1.value = style === 'twice' ? '1' :
					style === 'old' ? '2' : '0';
				indexStyle2.value = style === 'twice' ? '1' :
					style === 'old' ? '2' : '0';
				indexStyle3.value = style === 'twice' ? '1' :
					style === 'old' ? '2' : '0';
			}
			vipShow.value = res.vipShow;
			if (isIphone.value && res.iosClose == '1') {
				iosCheck.value = false
			} else {
				iosCheck.value = true
			}
		});
	}

	// 获取加入的圈子动态
	function getJoinTopicPost() {
		loadStatus3.value = 'loading';
		$H.get('post/joinTopicPost', {
			page: page3.value
		}).then(res => {
			joinTopicPost.value = joinTopicPost.value.concat(res.result.data);
			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus3.value = 'nomore';
			} else {
				loadStatus3.value = 'loadmore';
			}
		});
	}

	// 获取关注用户帖子
	function getFollowUserPost() {
		loadStatus1.value = 'loading';
		$H.get('post/followUserPost', {
			page: page2.value
		}).then(res => {
			if (res.code == 0 && res.result) {
				followUserPost.value = followUserPost.value.concat(res.result.data);
				if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
					loadStatus1.value = 'nomore';
				} else {
					loadStatus1.value = 'loadmore';
				}
			} else {
				loadStatus1.value = 'nomore';
			}
		});
	}

	//获取最新帖子
	function getLastPost() {
		loadStatus4.value = 'loading';
		$H.get('post/lastPostByFilter', {
			page: page4.value,
			limit: 20,
			filter: filterPost.value,
			type: typePost.value
		}).then(res => {
			lastPost.value = lastPost.value.concat(res.result.data);
			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus4.value = 'nomore';
			} else {
				loadStatus4.value = 'loadmore';
			}
		});
	}

	//获取广告配置
	function getAdConfig() {
		$H.get('system/getAd').then(res => {
			if (res.code == 0) {
				adIsOpen.value = res.adIsOpen;
				h5Adpid.value = res.h5Adpid;
				wxAdpid.value = res.wxAdpid;
			}
			$loading(false);
		});
	}

	//弹窗公告
	function getPop() {
		$H.get('system/getPop').then(res => {
			if (res.code == 0) {
				if (res.popupOpen == "0") {
					if (!$f.myCache('lf_pop_info')) {
						showNotice.value = true
						popTitle.value = res.popTitle
						popContent.value = res.popContent
						let day = parseInt(res.popTime)
						$f.myCache('lf_pop_info', 'lf', day * 3600 * 24)
					}
				}
			}
		});
	}

	function toVipPage() {
		if (uni.getStorageSync('hasLogin')) {
			$f.jump('/pages/user/vip/vip')
		} else {
			$f.toast('请先登录哦')
		}
	}

	function toSearch() {
		// #ifdef MP-WEIXIN
		$f.jump('/pages/search/search')
		// #endif
		// #ifndef MP-WEIXIN
		if (uni.getStorageSync('hasLogin')) {
			$f.jump('/pages/search/search')
		} else {
			$f.toast('请先登录哦')
		}
		// #endif
	}

	function closePop() {
		showNotice.value = false
	}

	function goBeian() {
		// #ifdef H5
		window.open('https://beian.miit.gov.cn/', '_blank')
		// #endif
	}
</script>

<style lang="scss" scoped>
	.index-con {
		padding-bottom: 80rpx;

		.lf-tab {
			display: flex;
			margin-top: 15rpx;
			padding-right: 40rpx;
			justify-content: space-between;
			align-items: center; // 添加垂直居中对齐
			width: 750rpx;
		}

		.lf-tab-con {
			width: 450rpx; // 调整宽度，为筛选按钮留出空间
		}

		.lf-filter {
			margin-left: auto;
			// margin-right: auto;
			padding-top: 5rpx;
		}
	}

	.lf-nav {
		background-color: #ffffff;
		position: fixed;
		top: 0;
		z-index: 999;
	}

	.lf-all {
		display: flex;
		flex-direction: column;
	}

	.lf-all-nav {
		display: flex;
		align-items: center;
	}

	// .lf-tab {
	// 	display: flex;
	// 	margin-top: 15rpx;
	// 	padding-right: 40rpx;
	// 	justify-content: space-between;
	// 	width: 750rpx;
	// }

	// .lf-tab-con {
	// 	width: 500rpx;
	// }

	.lf-vip {
		padding-top: 5rpx;
		height: 40rpx;
	}

	.lf-vip-icon {
		width: 146rpx;
		height: 40rpx;
	}

	.lf-top {
		/* #ifdef H5*/
		margin-top: 160rpx;
		/* #endif */
		/* #ifdef MP */
		margin-top: 242rpx;
		/* #endif */
		/* #ifdef APP-PLUS */
		margin-top: 242rpx;
		/* #endif */

	}

	.navbar-img {
		.n-img {
			width: 80rpx;
			height: 80rpx;
			margin: 0 20rpx 0 40rpx
		}

	}

	.search-wrap {
		/* #ifdef MP */
		width: 410rpx;
		/* #endif */
		/* #ifndef MP */
		width: 100%;
		/* #endif */

	}

	.swiper-body {
		height: calc(100vh - var(--status-bar-height) - 43px);
	}

	.body-scroll-view {
		width: 100%;
		height: 100%;
	}

	.tab-box {
		width: 80%;
	}

	.block-title {
		font-weight: bold;
		padding: 20rpx;
		color: #616161;
		display: flex;
		font-size: 28rpx;

		.right {
			margin-left: auto;
			color: #333;
			font-size: 20rpx;
			display: flex;
			align-items: center;
		}
	}

	// 顶部圈子
	.topic-wrap {
		padding: 0 20rpx;
		background-color: #fff;
		margin-bottom: 1rpx;
	}

	.grid-topic {
		position: relative;
		margin-bottom: 20rpx;

		.name {
			font-size: 20rpx;
			text-align: center;
		}

		.user {
			position: absolute;
			left: 0;
			top: 4rpx;
			font-size: 24rpx;
			display: block;
			background-color: #f18686;
			padding: 2rpx;
			border-radius: 0 0 10rpx 0;
			height: auto;
			color: white;
		}
	}

	// 弹框样式
	.system-pop {
		width: 540rpx;
		box-sizing: border-box;
		padding: 42rpx;
		background: white;
		border-radius: 30rpx;
		margin: 500rpx auto;

		.pop-title {
			text-align: center;
			font-size: 31upx;
			color: #000;
			font-weight: bold;
			margin-bottom: 20upx;
		}

		.popup-txt {
			line-height: 48upx;
			font-size: 28upx;
		}

		.popup-bot {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-top: 30upx;

			>button {
				color: #FFF;
				font-size: 26rpx;
				font-weight: 500;
				line-height: 80rpx;
				width: 46%;
				text-align: center;
				height: 80rpx;
				border-radius: 16rpx;
				border: none;
				background: #00aaff;
			}

			>button:nth-of-type(1) {
				color: #00aaff;
				background: #f2f2f2;
			}
		}
	}

	// H5端备案信息样式
	.beian-info {
		text-align: center;
		padding: 20rpx 0;
		margin-top: 40rpx;
		margin-bottom: 20rpx;
		font-size: 24rpx;
		color: #999999;
		cursor: pointer;
		transition: color 0.3s;

		&:hover {
			color: #666666;
		}

		text {
			text-decoration: underline;
		}
	}
</style>