<template>
	<view class="user" :style="[background]">
		<view class="header">
			<!-- #ifndef  H5 -->
			<u-sticky offset-top="0" h5-nav-height="0" bg-color="transparent">
				<u-navbar :is-back="false" title="个人中心" :title-bold="true" :is-fixed="false" :border-bottom="false"
					:background="{ background: 'rgba(256,256, 256,' + navBg + ')' }"
					:title-color="navBg > 0.5 ? '#000' : '#fff'"></u-navbar>
			</u-sticky>
			<!-- #endif -->
			<view class="user-info row-between">
				<view class="info row">
					<image v-if="hasLogin" class="avatar mr20 flexnone" :src="userInfo.avatar" @click="showDrawer()">
					</image>
					<image v-else class="avatar mr20 flexnone" @tap="toLogin" src="/static/images/unlogin_avatar.png">
					</image>
					<view class="white" v-if="hasLogin" @tap="toNav('/pages/user/home')">

						<view class="name xxl line1">{{userInfo.username}}
							<image style="height: 22rpx;margin-left:10rpx ;" mode="heightFix" v-if="userInfo.level"
								:src="$IMG+'/vip/level_'+userInfo.level+'.png'" class="level-img-2" />
						</view>

						<view class="user-intro row-between" v-if="userInfo.intro">
							<view class="xs white ml20 mr20">{{userInfo.intro}}</view>
						</view>
					</view>
					<view class="white" v-else @tap="toLogin">
						<view style="font-size: 42rpx">点击登录</view>
						<view class="sm">登录体验更多功能</view>
					</view>
				</view>
				<view class="row" style="align-self: flex-start;">
					<view class="user-opt" style="margin-right: 30rpx;" @tap="goMessage()">
						<view class="dot row-center" v-if="msg>0"></view>
						<image style="width:58rpx;height: 58rpx;" src="/static/images/icon_my_news.png"></image>
					</view>
					<view class="user-opt" @tap="toNav('/subpages/content/setting/setting')">
						<image style="width:58rpx;height: 58rpx;" src="/static/images/icon_my_setting.png"></image>
					</view>
				</view>
			</view>
			<view class="member column-end" @click="toVip" >
				<view class="member-entery row-between">
					<view class="row">
						<image class="icon-md" src="/static/images/icon_member.png"></image>
						<view class="ml10" v-if="hasLogin&&userInfo.vip==1">尊贵的会员您好</view>
						<view class="ml10" v-else-if="hasLogin&&userInfo.vip==0">普通用户</view>
						<view class="ml10" v-else>登录体验更多服务</view>
					</view>
					<view class="row">
						<view class="sm" v-if="isOpen=='0'">{{hasLogin ? '会员中心' : '请先登录'}}</view>
						<view class="sm" v-else>{{hasLogin ? '' : '请先登录'}}</view>
						<u-icon name="arrow-right"></u-icon>
					</view>
				</view>
			</view>
		</view>
		<view class="my-assets bg-white">
			<view class="title row lg">我的账号</view>
			<view class="nav row">
				<view class="icon-box" @click="toNav('/pages/my/fans')">
					<view class="icon-num">{{userInfo.fans || 0}}</view>
					<view class="icon-txt">粉丝</view>
				</view>
				<view class="icon-box" @click="toNav('/pages/my/follow')">
					<view class="icon-num">{{userInfo.follow || 0}}</view>
					<view class="icon-txt">关注</view>
				</view>
				<view class="icon-box" @click="toNav('/pages/my/post')">
					<view class="icon-num">{{userInfo.postNum || 0}}</view>
					<view class="icon-txt">帖子</view>
				</view>
				<view class="icon-box" @click="toNav('/pages/sign/integral')">
					<view class="icon-num">{{userInfo.integral || 0}}</view>
					<view class="icon-txt">积分</view>
				</view>
			</view>
		</view>
		<!-- mini幻灯片 -->
		<view v-if="bannerList && bannerList.length>0">
			<mine-banner :items="bannerList"></mine-banner>
		</view>
		<!-- 我的服务 -->
		<view class="block-wrap">
			<view class="block-title">我的服务</view>
			<u-grid :col="4" :border="false" style="margin: 20rpx 0;" @click="toNav">
				<u-grid-item v-for="(menu,menuListIndex) in menuList" :key="menuListIndex" :index="menu.url">
					<image class="gn-icon" :src="menu.img"></image>
					<view class="grid-text">{{menu.name}}</view>
				</u-grid-item>
			</u-grid>
		</view>
		<view class="xs muted" style="margin: 50rpx 0;" @click="goBeian">
			<view class="row-center">
				慢热
			</view>
			<view class="row-center">
				© m.scs.baby
			</view>
		</view>
		<!-- tabbar -->
		<lf-tabbar :active="4" :count="msgCount"></lf-tabbar>
		<!-- 侧边栏 -->
		<unidrawer ref="draw" :mask="true" :width="280" :mode="'left'">
			<sideview></sideview>
		</unidrawer>
		<!-- 自定义加载 -->
		<toast color="#fff" type="rotate3"></toast>
	</view>
</template>

<script setup>
	import { ref, computed, getCurrentInstance } from 'vue'
	import { onLoad, onShow, onPageScroll, onShareAppMessage } from '@dcloudio/uni-app'
	import { useStore } from 'vuex'
	import mineBanner from "@/components/mine-banner/mine-banner.vue"
	import unidrawer from '@/uni_modules/uni-drawer/components/uni-drawer/uni-drawer.vue'
	import sideview from '@/components/sideView/sideview.vue'
	import wxMpUtil from "@/utils/wxMpUtil.js"
	import config from '@/utils/config.js'
	
	const { proxy } = getCurrentInstance();
	const $H = proxy.$H;
	const $c = proxy.$c;
	const $loading = proxy.$loading;
	const store = useStore();
	
	const $IMG = ref(proxy.$IMG);
	const userInfo = ref('');
	const showNav = ref(false);
	const navH = ref(0);
	const navBg = ref(0);
	const menuList = ref([]);
	const statusBarH = ref("");
	const hasLogin = ref(false);
	const background = ref('');
	const msg = ref(0);
	const bgImg = ref('');
	const bannerList = ref([]);
	const isOpen = ref('');
	const shareCover = ref('');
	const iosClose = ref('');
	const isIphone = ref(getApp().globalData.iphone);
	const draw = ref(null);
	
	const msgCount = computed(() => {
		msg.value = store.state.messegeNum[3]
		return store.state.messegeNum
	});
	
	// uni-app 页面生命周期
	onLoad((options) => {
		getSysInfo();
		getBgImg();
		getLinkList();
			//#ifdef MP-WEIXIN
			wx.showShareMenu({
				withShareTicket: true,
				menus: ['shareAppMessage', 'shareTimeline']
			});
			//#endif
			// #ifdef H5
		if (uni.getStorageSync('hasLogin') && config.wxh5Login) {
				//登录后的用户绑定公众号openid 下次登录判断openid即可 不用手机验证码登录
				wxMpUtil.checkAndSaveWxMpOpenid(options.code, "/pages/my/my")
				let ua = navigator.userAgent.toLowerCase();
				if (ua.match(/MicroMessenger/i) == "micromessenger") {
					if (typeof WeixinJSBridge == "undefined"){
					   if( document.addEventListener ){
						   document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
					   }else if (document.attachEvent){
						   document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
						   document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
					   }
					}
				}
			}
			// #endif
	});
	
	onShow(() => {
			if (uni.getStorageSync('hasLogin')) {
			getUserInfo();
			hasLogin.value = true;
			} else {
			hasLogin.value = false;
			userInfo.value = '';
			}
		getMsgNum();
	});
	
	onPageScroll((e) => {
			const top = uni.upx2px(100)
		const { scrollTop } = e
			let percent = scrollTop / top > 1 ? 1 : scrollTop / top
		navBg.value = percent
	});
	
	onShareAppMessage((res) => {
		let imgURL = shareCover.value
			return {
			title: $c.miniappName,
				path: '/pages/index/index',
				imageUrl: imgURL
			};
	});
	
	function getUserMenu() {
		$loading(true);
		$H.get('userMenu/list').then(res => {
					if (res.code == 0) {
				if (isIphone.value && iosClose.value == '0') {
							let list = res.result
					const excludedValues = ['/pages/user/vip/vip', '/pages/account/account', '/pages/pay/pay'];
					menuList.value = list.filter(item => !excludedValues.includes(item.url));
				} else {
					menuList.value = res.result;
						}
			}
			$loading(false);
				});
	}
	
	function getMsgNum() {
		$H.post('message/num').then(res => {
			const msgNum = res.result;
			let all = store.state.totalUnread.message + store.state.totalUnread.notice + res.result.allCount
			store.state.messegeNum = [0, 0, 0, all, 0];
				});
	}
	
			//显示侧边栏
	function showDrawer() {
		draw.value.open();
	}
	
			// 获取轮播图
	function getLinkList() {
		$H.post('link/list', {
						cateId: 1
		}).then(res => {
			bannerList.value = res.result;
					});
	}
	
	function toLogin() {
				// #ifdef APP-PLUS || H5
				uni.navigateTo({
					url: '/pages/user/go-login'
				});
				// #endif
				// #ifdef MP-WEIXIN
				uni.navigateTo({
					url: '/pages/user/login'
				});
				// #endif
	}
	
	function getSysInfo() {
		$H.get('system/basic').then(res => {
			shareCover.value = res.logo;
			isOpen.value = res.vipShow;
			iosClose.value = res.iosClose;
			getUserMenu();
				});
	}
	
	function getUserInfo() {
		$H.get('user/userInfo').then(res => {
			userInfo.value = res.result;
			if (res.result.status == 1) {
						uni.removeStorageSync("hasLogin");
						uni.removeStorageSync("token");
						uni.removeStorageSync("userInfo");
						uni.closeSocket();
						uni.switchTab({
							url: "/pages/index/index"
						})
					}
				});
	}
	
	function toUcenter() {
				uni.navigateTo({
					url: '/pages/user/home'
				});
	}
	
	function toNav(url) {
				uni.navigateTo({
					url: url
				});
	}
	
	function getIsOpen() {
		$H.get('system/vipShow').then(res => {
			isOpen.value = res.vipShow;
				});
	}
	
	function toVip() {
				//会员入口总开关开着才能进入，且要考虑IOS机型是否开启虚拟支付相关功能的开关
		if (isOpen.value == '0') {
			if (!isIphone.value) {
						uni.navigateTo({
							url: '/pages/user/vip/vip'
						});
					} else {
				if (iosClose.value == '1') {
							uni.navigateTo({
								url: '/pages/user/vip/vip'
							});
						}
					}
				}
	}
	
	function goMessage() {
				uni.switchTab({
					url: '/pages/message/message'
				})
	}
	
	function getBgImg() {
		$H.get('system/bgImgConfig').then(res => {
			bgImg.value = res.bgImg;
			background.value = {
				'background-image': `url(${bgImg.value})`
					}
				});
	}
	
	function goBeian() {
				// #ifdef H5
				window.open("https://beian.miit.gov.cn/")
				// #endif
	}
</script>
<style lang="scss">
	.user {
		background-image: url(../../static/images/my_topbg.png);
		background-size: 100% 420rpx;
		background-repeat: no-repeat;

		.header {
			display: flex;
			flex-direction: column;
			height: 420rpx;

			.user-info {
				padding: 10rpx 30rpx;
				//#ifdef  H5 
				padding-top: 90rpx;

				//#endif
				.avatar {
					height: 110rpx;
					width: 110rpx;
					border-radius: 50%;
					overflow: hidden;
				}

				.name {
					text-align: left;
					margin-bottom: 5rpx;
					max-width: 400rpx;
				}

				.user-intro {
					border: 1px solid white;
					border-radius: 100rpx;

					.copy-btn {
						background-color: #FFDFDA;
						height: 40rpx;
						width: 90rpx;
						border-radius: 100rpx;
					}
				}

				.user-opt {
					position: relative;

					.dot {
						position: absolute;
						background-color: #ee0a24;
						border: 2rpx solid #FFFFFF;
						color: #F4F4F5;
						border-radius: 100%;
						top: 6rpx;
						right: 0rpx;
						font-size: 22rpx;
						min-width: 16rpx;
						height: 16rpx;
					}
				}

				.buyer-type {
					background-color: #FFA200;
					height: 38rpx;
					padding: 0 10rpx;
				}
			}

			.member {
				flex: 1;
				padding: 0 20rpx;

				.member-entery {
					color: #FFE0A1;
					padding: 0 16rpx;
					width: 100%;
					height: 80rpx;
					background: url(../../static/images/bg_member_grade.png);
					background-size: 100%;
				}
			}

		}

		.order-nav {
			.icon-contain {
				position: relative;
			}
		}

		.order-nav,
		.my-assets {
			margin: 20rpx 20rpx 0;
			border-radius: 8rpx;
		}

		.title {
			height: 88rpx;
			padding: 0 30rpx;
			border-bottom: 1px dashed #ffffff;
		}

		.nav {
			padding: 26rpx 0 0;

			.assets-item {
				flex: 1;
			}

			.item {
				width: 25%;
			}

			.badge {
				padding: 0 6rpx;
				min-width: 28rpx;
				height: 28rpx;
				border-radius: 28rpx;
				box-sizing: border-box;
				border: 1rpx solid #FF2C3C;
				color: #FF2C3C;
				position: absolute;
				left: 33rpx;
				top: -10rpx;
				z-index: 2;
			}

			.nav-icon {
				width: 52rpx;
				height: 52rpx;
			}
		}
	}

	.block-wrap {
		background-color: #fff;
		border-radius: 20rpx;
		margin: 20rpx;
		overflow: hidden;

		.block-title {
			background-color: #fff;
			padding: 20rpx 32rpx;
			// font-weight: bold;
		}
	}
	.icon-box{
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		margin-bottom: 20rpx;
		flex: 1;
		border-right: 1px solid #eee;
		&:last-child {
			border-right: none;
		}
	}
	.icon-txt{
		font-size: 26rpx;
		color: #999999;
	}
	.gn-icon {
		width: 60rpx;
		height: 60rpx;
		margin-bottom: 20rpx;
	}

	.grid-text {
		color: #999;
		font-size: 12px;
		margin-bottom: 8rpx;
	}

	.icon {
		min-height: 34rpx;
		min-width: 34rpx;
		height: 34rpx;
		width: 34rpx;
		vertical-align: middle;
	}

	.icon-md {
		min-height: 44rpx;
		min-width: 44rpx;
		height: 44rpx;
		width: 44rpx;
		vertical-align: middle;
	}

	.row {
		display: flex;
		align-items: center;
	}

	.row-between {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.column-end {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: flex-end;
	}

	.mr20 {
		margin-right: 20rpx;
	}

	.flexnone {
		flex: none;
	}

	.line1 {
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.column-between {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: space-between;
	}

	.wrap {
		flex-wrap: wrap;
	}

	.bg-white {
		background-color: #ffffff;
	}

	.white {
		color: #ffffff;
	}

	.icon-num {
		font-size: 34rpx;
		color: #2b2b2b;
		font-weight: 500;
	}

	.ml20 {
		margin-left: 20rpx;
	}

	.lg {
		font-size: 32rpx;
	}

	.sm {
		font-size: 26rpx;
	}

	.xs {
		font-size: 24rpx;
	}

	.mr5 {
		margin-right: 5rpx;
	}

	.xxl {
		font-size: 36rpx;
	}

	.xxs {
		font-size: 24rpx;
		color: #ccc;
	}

	.mb20 {
		margin-bottom: 20rpx;
	}

	.mt10 {
		margin-top: 10rpx;
	}

	.row-center {
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.br60 {
		border-radius: 60rpx;
	}

	.ml10 {
		margin-left: 10rpx;
	}

	.ml5 {
		margin-left: 5rpx;
	}

	.muted {
		color: #999999;
	}
</style>