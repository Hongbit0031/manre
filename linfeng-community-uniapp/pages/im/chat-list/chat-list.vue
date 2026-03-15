<template>
	<view >
		<!-- 顶部导航栏 -->
		<view class="navigate-bar">
			<view class="navigate-bar-app"></view>
			<view class="navigate-bar-main">
				<view class="navigate-bar-left">
					<view class="navigate-bar-left-avatar" @tap="showDrawer()">
						<image class="navigate-bar-left-avatar-image" :src="loginUserInfo.avatar"></image>
						<view v-if="!isSocketOpen" class="navigate-bar-left-avatar-red-dot"></view>
						<view v-if="isSocketOpen" class="navigate-bar-left-avatar-green-dot"></view>
					</view>
				</view>
				<view class="navigate-bar-middle">
					会话列表
				</view>
				<!-- #ifndef MP-WEIXIN -->
				<view class="navigate-bar-right">
					<image @tap="gotoSearch()" class="navigate-bar-right-icon" :src="$IMG+'/im/search.png'"></image>
				</view>
				<!-- #endif -->
				
			</view>
		</view>
		<view class="navigate-bar-space" style="margin-bottom:30rpx"></view>

	
		<view class="list">
			<view class="flex_col" @longpress="onLongPress" :class="{'active':pickerUserIndex==index}" @tap="listTap(item)" v-for="(item,index) in sessionList"
			 :key="index" :data-index="index">
				<view class="avatar-container">
					<image :src="item.avatar" mode="aspectFill" style="border-radius: 10%;"></image>
					<!-- #ifdef H5 -->
					<u-badge style="position: absolute;right: -10upx;top: -10upx;"  :count="item.unread"></u-badge>
					<!-- #endif -->
					<!-- #ifdef MP-WEIXIN -->
					<u-badge class="msg-tag" :count="item.unread"></u-badge>
					<!-- #endif -->
					<!-- #ifdef APP-PLUS -->
					<u-badge :offset="[-10, -10]" :count="item.unread"></u-badge>
					<!-- #endif -->
				</view>
				<view class="flex_grow">
					<view class="flex_col">
						<view class="flex_grow">{{item.name}}</view>
						<view class="time">{{timeShowFormat(item.updateTime)}}</view>
					</view>
					<view class="info">{{item.lastMessage}}</view>
				</view>
			</view>
		</view>
		<view v-if="sessionList.length == 0">
			<view class="msg-empty">
				<image class="img" mode="widthFix" src="/static/empty.png"></image>
				<text class="txt">暂无会话</text>
			</view>
		</view>
		<view class="shade" v-show="showShade" @tap="hidePop">
			<view class="pop" :style="popStyle" :class="{'show':showPop}">
				<view v-for="(item,index) in popButton" :key="index" @tap="pickerMenu" :data-index="index">{{item}}</view>
			</view>
		</view>
		
		<!-- 侧边栏 -->
		 <UniDrawer ref="drawerRef" :mask="true" :width="280" :mode="'left'">
			<SideView></SideView>
		</UniDrawer>
		
		<!-- 悬浮返回按钮 -->
		<view class="floating-back-btn" @tap="goBack">
			<u-icon name="arrow-right-double" size="30" color="#ffffff"></u-icon>
		</view>

	</view>
</template>

<script setup>
import { ref, computed, getCurrentInstance, nextTick } from 'vue'
import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { useStore } from 'vuex'
import timeUtil from '@/utils/timeUtil.js'
import stringUtil from '@/utils/stringUtil.js'
import UniDrawer from '@/uni_modules/uni-drawer/components/uni-drawer/uni-drawer.vue'
import SideView from '@/components/sideView/sideview.vue'

const { proxy } = getCurrentInstance()
const $IMG = ref(proxy.$IMG)
const $H = proxy.$H
const store = useStore()

// 响应式数据
const uploadShow = ref(false)
const selectedImage = ref('')
const title = ref('慢热IM')
const show = ref(false)
const selectedAvartar = ref(null)
const maxImageMB = ref(3)
const tip = ref('')
const value = ref(0)
const winSize = ref({})
const showShade = ref(false)
const showPop = ref(false)
const popButton = ref(["标记未读"])
const popStyle = ref("")
const pickerUserIndex = ref(-1)
const drawerRef = ref(null)

// 计算属性
const loginUserInfo = computed(() => store.getters.loginUserInfo)
const isSocketOpen = computed(() => store.getters.isSocketOpen)
const sessionList = computed(() => store.getters.sessionList)
const totalUnread = computed(() => store.getters.totalUnread)

// 生命周期 - 刷新
onPullDownRefresh(() => {
	//从新获取消息列表
	store.dispatch('getFriendList')
	setTimeout(() => {
		if(store.state.isSocketOpen){
			uni.showToast({
				icon:'none',
				title:'刷新成功'
			})
		}
		else{
			uni.showToast({
				icon:'error',
				title:'刷新失败'
			})
		}
		uni.stopPullDownRefresh()
	}, 800)
})

onLoad(() => {
	if (!uni.getStorageSync('hasLogin')) {
		// #ifdef MP-WEIXIN
		uni.navigateTo({
			url: "/pages/user/login"
		})
		// #endif
		// #ifndef MP-WEIXIN
		uni.navigateTo({
			url: "/pages/user/go-login"
		})
		// #endif
	}
	getWindowSize()
	
	// #ifdef H5
	document.onLong = function(e) {
		var e = e || window.event;
		e.preventDefault();
	};
	// #endif
})

onShow(() => {	
})

// 方法
const timeShowFormat = (time) => {
	return timeUtil.timeShowFormat(timeUtil.getFormatTime(time))
}

const close = () => {}
const open = () => {}

//去搜索页面
const gotoSearch = () => {
	uni.navigateTo({
		url:'/pages/search/search'
	})
}

//显示侧边栏
const showDrawer = () => {
	drawerRef.value.open()
}

const hideDrawer = () => {
	drawerRef.value.close()
}

/* 列表触摸事件 */
const listTap = (item) => {
	/* 因弹出遮罩问题，所以需要在遮罩弹出的情况下阻止列表事件的触发 */
	if (showShade.value) {
		return
	}
	if(item.type=='person'){
		store.state.chattingUserInfo = item
		$H.post('chat/list', {
			sessionId: item.sessionId,
			pageNum: 1,
			pageSize: 20
		}).then(res => {
			if (res.code == 0) {
				let data = res.result
				for (let i = 0; i < store.state.personMessage.length; i++) {
					if (store.state.personMessage[i].sessionId == data.sessionId) {
						let current = Number(data.pageInfo.current)
						let total = Number(data.pageInfo.total)
						let size = Number(data.pageInfo.size)
						store.state.personMessage[i].lastMessageId = data.pageInfo.records
							.length != 0 ? data.pageInfo.records[0].id : 0
						store.state.personMessage[i].list = data.pageInfo.records.reverse()
						store.state.personMessage[i].pageNum = current
						store.state.personMessage[i].hasNextPage = current * size < total
						break;
					}
				}
				uni.navigateTo({
					url:'/pages/im/chat/chat'
				})
			}else{
				uni.showToast({
					icon:'none',
					mask:'消息加载失败'
				})
			}
		})

	}
}

/* 获取窗口尺寸 */
const getWindowSize = () => {
	uni.getSystemInfo({
		success: (res) => {
			winSize.value = {
				"witdh": res.windowWidth,
				"height": res.windowHeight
			}
		}
	})
}

/* 长按监听 */
const onLongPress = (e) => {
	let [touches, style, index] = [e.touches[0], "", e.currentTarget.dataset.index]

	/* 因 非H5端不兼容 style 属性绑定 Object ，所以拼接字符 */
	if (touches.clientY > (winSize.value.height / 2)) {
		style = `bottom:${winSize.value.height-touches.clientY}px;`
	} else {
		style = `top:${touches.clientY}px;`
	}
	if (touches.clientX > (winSize.value.witdh / 2)) {
		style += `right:${winSize.value.witdh-touches.clientX}px`
	} else {
		style += `left:${touches.clientX}px`
	}

	popStyle.value = style
	pickerUserIndex.value = Number(index)
	showShade.value = true
	nextTick(() => {
		setTimeout(() => {
			showPop.value = true
		}, 10)
	})
}

/* 隐藏弹窗 */
const hidePop = () => {
	showPop.value = false
	pickerUserIndex.value = -1
	setTimeout(() => {
		showShade.value = false
	}, 250)
}

/* 选择菜单 */
const pickerMenu = (e) => {
	let index = Number(e.currentTarget.dataset.index)
	// console.log(`第${pickerUserIndex.value+1}个用户,第${index+1}个按钮`);
	store.state.sessionList[pickerUserIndex.value].unread = 1
	/* 
	 因为隐藏弹窗方法中会将当前选择的用户下标还原为-1,
	 如果行的菜单方法存在异步情况，请在隐藏之前将该值保存，或通过参数传入异步函数中
	 */
	hidePop()
}

/* 返回上一页 */
const goBack = () => {
	uni.navigateBack({
		delta: 1
	})
}
</script>

<style lang="scss" scoped>
	@import url('@/static/css/navigate-bar.css');
	.msg-empty{
		  display: flex;
		  flex-direction: column;
		  align-items:center ;
		  min-height: 800rpx;
		  .img{
			 width: 200rpx;
			 margin-top: 100rpx;
		  }
		  .txt{
			  color: #999;
			  font-size: 20rpx;
			  margin-top: 20rpx;
		  }
	}
	/* 列式弹性盒子 */
	.flex_col {
		display: flex;
		flex-direction: row;
		flex-wrap: nowrap;
		justify-content: flex-start;
		align-items: center;
		align-content: center;
	}
	
	/* 弹性盒子弹性容器 */
	.flex_col .flex_grow {
		width: 0;
		-webkit-box-flex: 1;
		-ms-flex-positive: 1;
		flex-grow: 1;
	}
	
	.flex_row .flex_grow {
		-webkit-box-flex: 1;
		-ms-flex-positive: 1;
		flex-grow: 1;
	}
	
	/* 弹性盒子允许换行 */
	.flex_col.flex_wrap {
		-ms-flex-wrap: wrap;
		flex-wrap: wrap;
	}
	.msg-tag{
		position: absolute;
		right: -20upx;
		top: -20upx;
	}
	/* 列表 */
	.list {
		background-color: #fff;
		font-size: 28upx;
		color: #333;
		user-select: none;
		touch-callout: none;
	
		&>view {
			padding: 24upx 30upx;
			position: relative;
	
			&:active,
			&.active {
				background-color: #f3f3f3;
			}
			
			.avatar-container{
				height: 90upx;
				width: 90upx;
				border-radius: 4px;
				margin-right: 20upx;
				position: relative;
			}
			image {
				height: 90upx;
				width: 90upx;
				// border-radius: 4px;
				// margin-right: 20upx;
			}
	
			&>view {
				line-height: 40upx;
	
				.time,
				.info {
					color: #999;
					font-size: 24upx;
				}
	
				.time {
					width: 150upx;
					text-align: right;
				}
	
				.info {
					overflow: hidden;
					text-overflow: ellipsis;
					white-space: nowrap;
				}
			}
		}
	
		&>view:not(:first-child) {
			margin-top: 1px;
	
			&::after {
				content: '';
				display: block;
				height: 0;
				border-top: #CCC solid 1px;
				width: 620upx;
				position: absolute;
				top: -1px;
				right: 0;
				transform:scaleY(0.5);	/* 1px像素 */
			}
		}
	}
	
	/* 遮罩 */
	.shade {
		position: fixed;
		z-index: 100;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		-webkit-touch-callout: none;
	
		.pop {
			position: fixed;
			z-index: 101;
			width: 200upx;
			box-sizing: border-box;
			font-size: 28upx;
			text-align: left;
			color: #333;
			background-color: #fff;
			box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
			line-height: 80upx;
			transition: transform 0.15s ease-in-out 0s;
			user-select: none;
			-webkit-touch-callout: none;
			transform: scale(0, 0);
	
			&.show {
				transform: scale(1, 1);
			}
	
			&>view {
				padding: 0 20upx;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
				user-select: none;
				-webkit-touch-callout: none;
	
				&:active {
					background-color: #f3f3f3;
				}
			}
		}
	}
	
	/* 悬浮返回按钮 */
	.floating-back-btn {
		position: fixed;
		bottom: 100rpx;
		right: 30rpx;
		width: 80rpx;
		height: 80rpx;
		background-color: rgba(0, 0, 0, 0.6);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 999;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.3);
		transition: all 0.3s ease;
		
		&:active {
			transform: scale(0.95);
			background-color: rgba(0, 0, 0, 0.8);
		}
		
		.back-icon {
			width: 40rpx;
			height: 40rpx;
			filter: brightness(0) invert(1); /* 将图标变为白色 */
		}
	}
	
</style>
