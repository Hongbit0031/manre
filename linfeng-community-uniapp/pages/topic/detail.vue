<template>
	<view class="wrap">
		<view style="position: absolute;">
			<u-navbar :custom-back="onBack" back-icon-color="#fff" :background="background" :border-bottom="false">
			</u-navbar>
		</view>
		<view class="head">
			<image mode="aspectFill" class="bg" :src="topicInfo.bgImage"></image>
		</view>
		<view class="body">
			<view class="head-c">
				<text class="name">{{ topicInfo.topicName }}</text>
				<view class="count">
					<text>{{ formattedUserCount }}人已加入</text>
					<text>{{ formattedPostCount }}篇内容</text>
				</view>
				<view>
					<block v-if="topicInfo.isJoin">
						<u-button class="mg-left-auth" @click="showShare = true" :custom-style="btnStyle">
							<text style="font-size: 25rpx;">分享</text>
						</u-button>
					</block>
					<block v-else>
						<u-button class="mg-left-auth" :custom-style="btnStyle" @click="joinTopic">
							<text style="font-size: 25rpx;">加入</text>
						</u-button>
					</block>
				</view>
			</view>
			<!-- 简介 -->
			<view class="member-wrap" @click="navigateToTopicUser">
				<view class="member-wrap-head">
					<text class="notice-txt u-line-1">{{ formattedDescription }}</text>
					<u-icon class="icon" name="arrow-right" style="font-size: 20rpx;margin-top: 20rpx;margin-bottom: 20rpx;"></u-icon>
				</view>
			</view>
			<!-- 私密圈子不可见 -->
			<view v-if="!topicInfo.isJoin && topicInfo.isPrivacy==1">
				<u-empty margin-top="100" text="私密圈子加入后才能浏览帖子~" mode="permission"></u-empty>
			</view>
			<!-- 进圈用户或公开圈子可见 -->
			<view v-else>
				<!-- 私密圈子提醒 -->
				<u-notice-bar v-if="!closePrivacy && topicInfo.isPrivacy==1"  type="none" mode="vertical" :close-icon="true" :list="privacyTip" @close="closePrivacyNotice"></u-notice-bar>
							<!-- 置顶 -->
			<view class="post-top-box">
				<view @longpress="onTopDel(item, index)" @click="navigateToPostDetail(item.id)" class="post-item"
					v-for="(item, index) in topicInfo.topPost" :key="index">
					<view class="tag">置顶</view>
					<view class="title">{{ item.title || item.content.substring(0, 15) }}</view>
				</view>
			</view>
							<!-- 圈话题 -->
			<view class="member-wrap" v-if="topicInfo.discussList.length > 0">
				<view class="member-wrap-head">
					<text style="font-size: 30rpx;font-weight: 600;">圈内话题</text>
					<view @click="navigateToDiscussList" class="user-num" hover-class="none">
						<text style="font-size: 26rpx;font-weight: 400;color: #999999;">查看更多</text>
					</view>
				</view>
				<scroll-view :scroll-x="true" style="height: 130rpx;">
					<view class="dis-wrap">
						<view @click="navigateToDiscussDetail(item.id)" class="d-item"
							v-for="item in topicInfo.discussList" :key="item.id">
							<view class="u-line-2">#{{ item.title.substring(0, 10) }}</view>
						</view>
					</view>
				</scroll-view>
			</view>
				<!-- 用户列表滚动 -->
				<topic-user-scroll :users="topicInfo.userList" :topicId="topicInfo.id"></topic-user-scroll>
				<!-- 分类tab -->
				<view class="tabs-box">
					<view class="tab-left">
						<u-tabs :list="tabList" :is-scroll="false" active-color="#000000" v-model="current" @change="tabsChange"></u-tabs>
					</view>
				</view>
				<view v-show="current == 0">
					<post-list :showTopic="false" :handle="true" :uid="topicInfo.uid" :list="postNews"
						:loadStatus="loadStatus" :admin="topicInfo.isAdmin"></post-list>
				</view>
				<view v-show="current == 1">
					<post-list :showTopic="false" :handle="true" :uid="topicInfo.uid" :list="postHot"
						:loadStatus="loadStatus" :admin="topicInfo.isAdmin"></post-list>
				</view>
				<view v-show="current == 2">
					<post-list :showTopic="false" :handle="true" :uid="topicInfo.uid" :list="postType12"
						:loadStatus="loadStatus" :admin="topicInfo.isAdmin"></post-list>
				</view>
				<view v-show="current == 3">
					<post-list :showTopic="false" :handle="true" :uid="topicInfo.uid" :list="postType4"
						:loadStatus="loadStatus" :admin="topicInfo.isAdmin"></post-list>
				</view>
				<view v-show="current == 4">
					<post-list :showTopic="false" :handle="true" :uid="topicInfo.uid" :list="postType3"
						:loadStatus="loadStatus" :admin="topicInfo.isAdmin"></post-list>
				</view>
						
			</view>
			
		</view>
		<!-- 底部菜单 -->
		<view class="tabbar">
			<view @click="showPopupChange" class="tab-item mid-button">
				<u-icon name="plus" size="50"></u-icon>
			</view>
		</view>
		<!-- 菜单弹框 -->
		<lf-popup v-model="showMenu">
			<view class="popup-head">
				<text>菜单</text>
				<u-icon @click="showMenu = false" size="40" class="close" color="#999" name="close"></u-icon>
			</view>
			<u-grid :col="4" :border="false">
				<!-- 如果是圈主或管理员 -->
				<block v-if="topicInfo.uid == sessionUser.uid || topicInfo.isAdmin">
					<u-grid-item @click="jumpMenu('/pages/discuss/add?topicId=' + topicId)">
						<image class="menu-icon" src="../../static/add-dis.png"></image>
						<view class="grid-text">新建话题</view>
					</u-grid-item>
					<u-grid-item @click="jumpMenu('/pages/topic/info-edit?topicId=' + topicId)">
						<image class="menu-icon" src="../../static/topic.png"></image>
						<view class="grid-text">圈子编辑</view>
					</u-grid-item>
					<u-grid-item @click="jumpMenu('/subpages/content/member-management/member-management?type=0&id=' + topicId)">
						<image class="menu-icon" src="../../static/topic-user.png"></image>
						<view class="grid-text">成员管理</view>
					</u-grid-item>
					<u-grid-item @click="jumpMenu('/subpages/content/member-management/member-management?type=1&id=' + topicId)">
						<image class="menu-icon" src="../../static/block.png"></image>
						<view class="grid-text">黑名单管理</view>
					</u-grid-item>
					<u-grid-item @click="jumpMenu('/subpages/content/apply-list/apply-list?types=0&id=' + topicId)">
						<image class="menu-icon" src="../../static/check.png"></image>
						<view class="grid-text">进圈审核</view>
					</u-grid-item>
					<!-- 如果是圈主 -->
					<block v-if="topicInfo.uid == sessionUser.uid">
						<u-grid-item @click="jumpMenu('/pages/topic/admin?id=' + topicId)">
							<image class="menu-icon" src="../../static/admin.png"></image>
							<view class="grid-text">管理员</view>
						</u-grid-item>
						<u-grid-item @click="giveTopic()">
							<image class="menu-icon" src="../../static/give-topic.png"></image>
							<view class="grid-text">转让圈子</view>
						</u-grid-item>
						<u-grid-item @click="delTopicModel = true">
							<image class="menu-icon" src="../../static/jiesan.png"></image>
							<view class="grid-text">解散圈子</view>
						</u-grid-item>
					</block>
				</block>
				<!-- 普通成员 -->
				<block v-else>
					<u-grid-item @click="outTopic">
						<image class="menu-icon" src="../../static/jiesan.png"></image>
						<view class="grid-text">退出圈子</view>
					</u-grid-item>
				</block>
			</u-grid>
		</lf-popup>
		<!-- 加入圈子弹窗 -->
		<u-modal v-model="joinTopicModel" :show-cancel-button="true" confirm-text="加入圈子"
			:content="'是否加入【' + topicInfo.topicName + '】?'" @confirm="joinTopic"></u-modal>
		<!-- 解散圈子弹窗 -->
		<u-modal v-model="delTopicModel" :show-cancel-button="true" confirm-color="red" confirm-text="确认"
			:content="'解散【' + topicInfo.topicName + '】后，将不可恢复，是否确认解散？'" @confirm="topicDel"></u-modal>
		<!-- 选择分享弹窗 -->
		<u-popup v-model="showShare" mode="center" border-radius="20" width="80%">

			<view class="share-type">
				<!-- #ifdef MP -->
				<button open-type="share" class="type-item u-reset-button">
					<u-icon class="icon" name="weixin-fill" color="#00b33c"></u-icon>
					<text>发给微信好友</text>
				</button>
				<!-- #endif -->
				<!-- #ifdef MP-WEIXIN || H5 -->
				<view class="type-item" @click="shareCanvas">
					<u-icon class="icon" name="photo" color="#aaaaff"></u-icon>
					<text>生成分享海报</text>
				</view>
				<!-- #endif -->
				<!-- #ifdef H5 || APP-PLUS -->
				<view class="type-item" @click="copyPageUrl">
					<u-icon class="icon" name="cut" color="#0099ff"></u-icon>
					<text>复制链接</text>
				</view>
				<!-- #endif -->

			</view>
		</u-popup>

		<!-- 分享海报弹窗-->
		<u-popup v-model="showCanvas" mode="center" width="80%">
			<view class="share-box">
				<image :src="posterUrl" class="images"></image>
			</view>
			<view class="footer">
				<u-button :custom-style="shareBtnStyle" @click="saveImg" type="success" shape="circle">保存分享</u-button>
			</view>
		</u-popup>
		<!-- 操作按钮弹窗 -->
		<lf-popup v-model="showPopup">
			<view class="handle-wrap">
				<view @click="handleJump(item)" class="item" v-for="(item,index) in popup" :key="index">
					<image mode="widthFix" class="icon" :src="item.icon"></image>
					<text class="txt">{{item.text}}</text>
				</view>
			</view>
		</lf-popup>
		<!-- 发布弹窗 -->
		<u-popup v-model="showPlusPost" mode="center" border-radius="20" width="80%">
			<view class="share-type">
				<view @click="onTrigger(1)" class="type-item">
					<u-icon class="icon" size="40" name="photo" color="#aaaaff"></u-icon>
					<text>发布帖子</text>
				</view>
				<view @click="onTrigger(2)" class="type-item" v-if="isOpen==1">
					<u-icon class="icon" size="40" name="play-circle-fill" color="#aa55ff"></u-icon>
					<text>发布视频</text>
				</view>
				<view @click="onTrigger(4)" class="type-item">
					<u-icon class="icon" size="36" name="order" color="#e34b00"></u-icon>
					<text>发布长文</text>
				</view>
				<view @click="onTrigger(3)" class="type-item">
					<image class="icon" src="/static/h_1.png"></image>
					<text>发布投票</text>
				</view>
				
			</view>
		</u-popup>
		<view style="height: 200rpx;"></view>
		<!-- 返回顶部 -->
		<lf-back-top :show-tag="showTag"></lf-back-top>
		<!-- 自定义加载 -->
		<toast  color="#fff" type="round"></toast>
	</view>
</template>

<script setup>
import { ref, computed, getCurrentInstance } from 'vue'
import { onLoad, onReachBottom, onPageScroll, onPullDownRefresh, onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app'
import postList from '@/components/post-list/post-list.vue'
import disList from '@/components/discuss-list/discuss-list.vue'
import userList from '@/components/user-list/user-list.vue'
import topicUserScroll from '@/components/topic-user-scroll/topic-user-scroll.vue'
import { numberFormat } from '@/utils/filters.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $c = proxy.$c
const $f = proxy.$f
const $u = proxy.$u

// Helper functions for filters
const substr = (e) => {
	return e.substr(0, 5)
}

const replace = (str) => {
	str = str.replace(/\n/g, '')
	return str
}

// Reactive data
const customStyle = ref({
	height: '40rpx',
	width: '120rpx'
})

const isOpen = ref(getApp().globalData.isOpen)
const showPlusPost = ref(false)
const showCanvas = ref(false)
const showShare = ref(false)
const joinTopicModel = ref(false)
const delTopicModel = ref(false)
const showMenu = ref(false)
const showPopup = ref(false)
const showTag = ref(false)
const current = ref(0)

const tabList = ref([
	{ name: '最新' },
	{ name: '最热' },
	{ name: '图文' },
	{ name: '投票' },
	{ name: '长文' }
])

const btnStyle = ref({
	marginRight: 0,
	width: '150rpx',
	fontSize: '20rpx',
	height: '60rpx',
	lineHeight: '60rpx',
	backgroundColor: '#333',
	color: '#fff'
})

const shareBtnStyle = ref({
	backgroundColor: '#333'
})

const topicId = ref(0)
const topicInfo = ref({
	memberList: [],
	discussList: [],
	description: '',
	userInfo: {
		username: ''
	},
	topPost: [],
	userList: []
})

const postHot = ref([])
const postNews = ref([])
const postType12 = ref([])
const postType3 = ref([])
const postType4 = ref([])
const loadStatus = ref('loading')
const page1 = ref(1)
const page2 = ref(1)
const page3 = ref(1)
const page4 = ref(1)
const page5 = ref(1)

const background = ref({
	backgroundColor: 'unset'
})

const sessionUser = ref(uni.getStorageSync('userInfo'))

const popup = ref([
	{
		icon: '/static/images/topic-menu.png',
		text: '菜单',
		url: 'menu'
	},
	{
		icon: '/static/images/topic-post.png',
		text: '发布',
		url: 'release'
	}
])

const posterUrl = ref("")
const privacyTip = ref(['当前圈子为私密圈子,请遵守平台规定哦'])
const closePrivacy = ref(false)
const isTopicAdmin = ref(false)
const applyInfoNum = ref(0)

// Computed properties for formatted data
const formattedUserCount = computed(() => numberFormat(topicInfo.value.userCount))
const formattedPostCount = computed(() => numberFormat(topicInfo.value.postCount))
const formattedDescription = computed(() => replace(topicInfo.value.description || ''))

// Methods
const showPopupChange = () => {
	showPopup.value = !showPopup.value
}

const onTopDel = (e, index) => {
	if (topicInfo.value.uid != sessionUser.value.uid) {
		return
	}
	uni.showModal({
		title: '提示',
		content: '是否解除置顶?',
		success: (res) => {
			if (res.confirm) {
				$H.post('post/topPostDel', {
					postId: e.id,
					topicId: e.topicId
				}).then(res => {
					if (res.code == 0) {
						topicInfo.value.topPost.splice(index, 1)
					}
				})
			}
		}
	})
}

const copyPageUrl = () => {
	uni.setClipboardData({
		data: $c.shareH5Url + 'pages/topic/detail?id=' + topicId.value,
		success: () => {
			uni.hideToast()
			$f.toast('复制成功', 'success')
			showShare.value = false
		}
	})
}

const onMenu = () => {
	if (topicInfo.value.isJoin) {
		showMenu.value = true
	} else {
		joinTopicModel.value = true
	}
}

const onPlus = () => {
	if (topicInfo.value.isJoin) {
		showPlusPost.value = true
	} else {
		joinTopicModel.value = true
	}
}

const handleJump = (e) => {
	showPopup.value = false
	if (e.url == 'menu') {
		if (topicInfo.value.isJoin) {
			showMenu.value = true
		} else {
			joinTopicModel.value = true
		}
	} else if (e.url == 'release') {
		if (topicInfo.value.isJoin) {
			showPlusPost.value = true
		} else {
			joinTopicModel.value = true
		}
	}
}

const closePrivacyNotice = () => {
	closePrivacy.value = true
}

const shareCanvas = () => {
	showShare.value = false
	showCanvas.value = true
	uni.showLoading({
		mask: true,
		title: '正在生成海报'
	})
	
	// Use separate variables for different platforms
	let origin = ''
	let url = ''
	
	// #ifdef H5
	origin = "h5"
	url = $c.shareH5Url + "pages/topic/detail?id=" + topicId.value
	// #endif
	
	// #ifdef MP-WEIXIN
	origin = "weixin"
	url = "pages/topic/detail?id=" + topicId.value
	// #endif

	$H.get('topic/qrCode', {
		topicId: topicId.value,
		origin: origin,
		url: url
	}).then(res => {
		if (res.code == 0) {
			posterUrl.value = res.result
		} else {
			showCanvas.value = false
		}
		uni.hideLoading()
	})
}

const saveImg = () => {
	// #ifdef MP-WEIXIN
	uni.getImageInfo({
		src: posterUrl.value,
		success: (image) => {
			uni.saveImageToPhotosAlbum({
				filePath: image.path,
				success: () => {
					uni.showToast({
						title: '图片保存成功'
					})
				},
				fail: () => {
					uni.showModal({
						title: '图片保存失败',
						content: '请确认是否已开启授权',
						confirmText: '开启授权',
						success(res) {
							if (res.confirm) {
								uni.openSetting({
									success(settingdata) {
										if (settingdata.authSetting["scope.writePhotosAlbum"]) {
											uni.showToast({
												title: '授权成功，请重试哦~',
												icon: "none"
											})
										} else {
											uni.showToast({
												title: '请确定已打开保存权限',
												icon: "none"
											})
										}
									}
								})
							}
						}
					})
				}
			})
		},
		fail() {}
	})
	// #endif
	
	// #ifdef H5
	const oA = document.createElement('a')
	oA.download = ''
	oA.href = posterUrl.value
	document.body.appendChild(oA)
	oA.click()
	oA.remove()
	// #endif
}

const topicDel = () => {
	uni.showLoading({
		mask: true,
		title: '操作中'
	})
	$H.get('topic/topicDel', {
		id: topicId.value
	}).then(res => {
		if (res.code == 200 || res.code == 0) {
			$u.toast('该圈子已成功解散')
			setTimeout(() => {
				uni.switchTab({
					url: '/pages/index/index'
				})
			}, 1500)
		}
		uni.hideLoading()
	})
}

const jumpUser = () => {
	uni.navigateTo({
		url: '/pages/topic/topic-user?id=' + topicId.value
	})
}

const onBack = () => {
	const pages = getCurrentPages()
	if (pages.length > 1) {
		uni.navigateBack()
	} else {
		uni.switchTab({
			url: '/pages/index/index'
		})
	}
}

const tabsChange = (index) => {
	current.value = index
	if (index == 1 && postHot.value.length == 0) {
		getPostList()
	} else if (index == 2 && postType12.value.length == 0) {
		getPostList()
	} else if (index == 3 && postType4.value.length == 0) {
		getPostList()
	} else if (index == 4 && postType3.value.length == 0) {
		getPostList()
	}
}

const onTrigger = (type) => {
	if (!topicInfo.value.isJoin) {
		joinTopicModel.value = true
		return
	}

	showPlusPost.value = false
	if (type == 3) {
		uni.navigateTo({
			url: '/pages/vote/vote?topicId=' + topicId.value
		})
	} else if (type == 4) {
		uni.navigateTo({
			url: '/subpages/content/article/add?topicId=' + topicId.value
		})
	} else {
		uni.navigateTo({
			url: '/pages/post/add?topicId=' + topicId.value + '&topicName=' + topicInfo.value.topicName + '&type=' + type
		})
	}
}

const jump = (uid) => {
	uni.navigateTo({
		url: '/pages/user/home?uid=' + uid
	})
}

const joinTopic = () => {
	$H.get('topic/joinTopic', {
		id: topicId.value
	}).then(res => {
		if (res.code === 0) {
			if (res.result == 1) {
				uni.showModal({
					title: '提示',
					content: '圈主开启了进圈验证，请先回答圈主设置的问题',
					showCancel: false,
					success: (res) => {
						if (res.confirm) {
							uni.navigateTo({
								url: "/subpages/content/topic-apply/topic-apply?id=" + topicId.value
							})
						}
					}
				})
			} else if (res.result == 2) {
				uni.showModal({
					title: '提示',
					content: '你已提交审核，请耐心等待管理员审核',
					showCancel: false
				})
			} else {
				topicInfo.value.isJoin = true
				joinTopicModel.value = false
			}
		}
	})
}

const outTopic = () => {
	$H.get('topic/userTopicDel', {
		id: topicId.value
	}).then(res => {
		if (res.code === 0) {
			topicInfo.value.isJoin = false
			showMenu.value = false
		}
	})
}

const getTopicInfo = () => {
	proxy.$loading(true)
	$H.get('topic/detail', {
		id: topicId.value
	}).then(res => {
		if (res.code == 500) {
			$u.toast(res.msg)
			setTimeout(() => {
				uni.switchTab({
					url: '/pages/square/square'
				})
			}, 1500)
		}
		topicInfo.value = res.result
		proxy.$loading(false)
		getPostList()
		isTopicAdmin.value = res.result.isAdmin
		checkTopicApplyByAdmin()
	})
}

const checkTopicApplyByAdmin = () => {
	if (isTopicAdmin.value) {
		$H.get('topic/checkTopicApplyByAdmin', {
			topicId: topicId.value
		}).then(res => {
			if (res.code === 0) {
				applyInfoNum.value = res.result
				if (applyInfoNum.value > 0) {
					uni.showModal({
						title: '平台提醒',
						content: '您有' + applyInfoNum.value + '条进圈申请待审核',
						confirmText: '去处理',
						cancelText: '稍后处理',
						success: (res) => {
							if (res.confirm) {
								uni.navigateTo({
									url: '/subpages/content/apply-list/apply-list?types=0&id=' + topicId.value
								})
							}
						}
					})
				}
			}
		})
	}
}

const jumpMenu = (url) => {
	showMenu.value = false
	uni.navigateTo({
		url: url
	})
}

const getPostList = () => {
	if (!topicInfo.value.isJoin && topicInfo.value.isPrivacy == 1) {
		return
	}
	loadStatus.value = 'loading'
	let page = page1.value
	let order = 'id desc'

	if (current.value == 1) {
		page = page2.value
		order = 'read_count desc'
	} else if (current.value == 2) {
		page = page3.value
		order = '12'
	} else if (current.value == 3) {
		page = page4.value
		order = '4'
	} else if (current.value == 4) {
		page = page5.value
		order = '3'
	}

	$H.post('post/getListByTopicId', {
		topicId: topicId.value,
		page: page,
		order: order
	}).then(res => {
		if (current.value == 0) {
			postNews.value = postNews.value.concat(res.result.data)
		} else if (current.value == 1) {
			postHot.value = postHot.value.concat(res.result.data)
		} else if (current.value == 2) {
			postType12.value = postType12.value.concat(res.result.data)
		} else if (current.value == 3) {
			postType4.value = postType4.value.concat(res.result.data)
		} else if (current.value == 4) {
			postType3.value = postType3.value.concat(res.result.data)
		}

		if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
			loadStatus.value = 'nomore'
		} else {
			loadStatus.value = 'loadmore'
		}
	})
}

const giveTopic = () => {
	uni.navigateTo({
		url: '/pages/topic/admin?type=1&id=' + topicId.value
	})
}

const navigateToTopicUser = () => {
	uni.navigateTo({
		url: '/pages/topic/topic-user?id=' + topicId.value
	})
}

const navigateToDiscussDetail = (id) => {
	uni.navigateTo({
		url: '/pages/discuss/detail?id=' + id
	})
}

const navigateToDiscussList = () => {
	uni.navigateTo({
		url: '/pages/discuss/list?topicId=' + topicId.value
	})
}

const navigateToPostDetail = (id) => {
	uni.navigateTo({
		url: '/pages/post/detail?id=' + id
	})
}

// Lifecycle hooks
onLoad((options) => {
	topicId.value = options.id

	if (options.scene) {
		topicId.value = options.scene
	}

	getTopicInfo()
})

onReachBottom(() => {
	if (current.value == 0) {
		page1.value++
	} else if (current.value == 1) {
		page2.value++
	} else if (current.value == 2) {
		page3.value++
	} else if (current.value == 3) {
		page4.value++
	} else if (current.value == 4) {
		page5.value++
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

onPullDownRefresh(() => {
	if (current.value == 0) {
		page1.value = 1
		postNews.value = []
	} else if (current.value == 1) {
		page2.value = 1
		postHot.value = []
	} else if (current.value == 2) {
		page3.value = 1
		postType12.value = []
	} else if (current.value == 3) {
		page4.value = 1
		postType4.value = []
	} else if (current.value == 4) {
		page5.value = 1
		postType3.value = []
	}

	getTopicInfo()
	uni.stopPullDownRefresh()
})

onShareAppMessage((res) => {
	if (res.from === 'button') {
		console.log(res.target)
	}
	return {
		title: topicInfo.value.topicName + '-' + topicInfo.value.description,
		path: '/pages/topic/detail?id=' + topicId.value,
		imageUrl: topicInfo.value.bgImage
	}
})

onShareTimeline(() => {
	return {
		title: topicInfo.value.topicName + '-' + topicInfo.value.description,
		imageUrl: topicInfo.value.bgImage,
		query: 'id=' + topicId.value
	}
})
</script>

<style>
	page {
		background-color: #f5f5f5;
	}
</style>
<style lang="scss" scoped>
	.wrap {
		height: calc(100vh);
	}

	.mg-left-auth {
		// margin-left: 10rpx;
	}

	.mg-left-20 {
		margin-left: 20rpx;
	}

	.tabs-box {
		background-color: #FFFFFF;
		margin-top: 20rpx;

		.tab-left {
			width: 90%;
		}
	}

	.notice-txt {
		color: #999;
		font-size: 12px;
		margin-top: 20rpx;
		margin-bottom: 20rpx;
	}

	.grid-text {
		font-size: 12px;
		color: #616161;
		margin-bottom: 30rpx;
	}

	.head {
		position: relative;
		height: 260rpx;
	}

	.body {
		position: absolute;
		top: 250rpx;
		left: 0;
		width: 750rpx;
		border-top-left-radius: 30rpx;
		border-top-right-radius: 30rpx;
		overflow: hidden;
	}

	.head-c {
		background-color: #FFFFFF;
		width: 100%;
		display: flex;
		padding: 30rpx;
		padding-right: 0;
	}

	.head-c .count {
		font-size: 10px;
		display: flex;
		align-items: center;
		margin-right: 20rpx	;
	}

	.head-c .count text {
		margin: 0 5rpx;
		color: #8a8989;
	}

	.head-c .name {
		font-size: 20px;
		font-weight: bold;
		width: 250rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.margin-left {
		margin-left: auto;
		margin-right: 20rpx;
	}

	.head .bg {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
	}

	/* 管理员 */
	.member-wrap {
		margin-top: -25rpx;
		padding: 0 30rpx 15rpx;
		background-color: #fff;
		border-bottom: 1px solid #eee;
	}

	.member-wrap .avatar {
		margin-bottom: 10rpx;
	}

	.member-wrap .member-wrap-head {
		display: flex;
		
		.tag {
			width: 70rpx;
			height: 42rpx;
			font-size: 20rpx;
			text-align: center;
			border-radius: 10rpx;
			border: .5px solid #dddddd;
			margin-right: 15rpx;
			margin-top: 20rpx;
			margin-bottom: 20rpx;
		}
		.user-num {
			margin-left: auto;
			color: #999;
		}

		.icon {
			margin-left: auto;
			color: #999;
		}
	}

	// 置顶
	.post-top-box {
		background-color: #fff;
		padding: 10rpx 20rpx 40rpx;
		margin-bottom: 20rpx;
		.post-item {
			display: flex;
			align-items: center;
			padding: 6rpx;
			font-size: 28rpx;

			&:last-child {
				margin-bottom: 0;
			}

			.tag {
				background-color: #e50000;
				color: #fff;
				padding: 0 10rpx;
				border-radius: 10rpx;
				font-size: 20rpx;
				height: 40rpx;
				line-height: 40rpx;
				margin-right: 20rpx;
			}
		}
	}

	// 圈话题
	.dis-wrap {
		display: flex;
		margin-top: 20rpx;

		.d-item {
			flex-grow: 0;
			flex-shrink: 0;
			// width: 330rpx;
			background-color: #F5F5F5;
			border-radius: 10rpx;
			padding: 20rpx;
			font-size: 28rpx;
			font-weight: 800;
			margin-right: 20rpx;
		}
	}

	.handle-wrap {
		display: flex;
		padding: 50rpx 0;

		.item {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			flex: 1;
			padding: 20rpx 0;
			border-radius: 20rpx;

			.icon {
				width: 100rpx;
				margin-bottom: 20rpx;
			}

			.txt {
				font-size: 28rpx;
			}
		}
	}

	.handle-close {
		display: flex;
		justify-content: center;
		margin-bottom: 50rpx;
	}

	// 菜单
	.tabbar {
		position: fixed;
		bottom: 50rpx;
		// width: 30%;
		margin-left: 42%;
		margin-right: 15%;
		background-color: #dde1e5;
		display: flex;
		padding: 10rpx;
		box-shadow: 0 0 45rpx #413f3d;
		font-size: 24rpx;
		border-radius: 100rpx;
		z-index: 9999;

		.tab-item {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			margin: 0 auto;

			.icon {
				margin-top: 10rpx;
				width: 40rpx;
				height: 40rpx;
			}
		}

		.mid-button {
			background-color: #333;
			width: 90rpx;
			height: 90rpx;
			border-radius: 50%;
			color: #fff;
		}
	}

	// 菜单弹窗
	.popup-head {
		text-align: center;
		font-size: 24rpx;
		position: relative;
		padding: 30rpx;
		border-bottom: 1px solid #F5F5F5;
		margin-bottom: 30rpx;

		.close {
			position: absolute;
			right: 30rpx;
			top: 30rpx;
		}
	}

	.menu-icon {
		width: 60rpx;
		height: 60rpx;
		margin-bottom: 10rpx;
	}

	.share-type {
		padding: 50rpx 30rpx;

		.type-item {
			background-color: #F5F5F5;
			padding: 20rpx;
			display: flex;
			justify-content: center;
			align-items: center;

			.icon {
				width: 40rpx;
				height: 40rpx;
				margin-right: 20rpx;
			}

			&:nth-child(2) {
				margin: 20rpx 0;
			}
			&:nth-child(3) {
				margin: 20rpx 0;
			}
		}
	}

	//海报弹窗
	.share-box {
		height: 530px;
		position: relative;

		.images {
			width: 100%;
			height: 100%;
		}

	}

	.footer {
		// position: absolute;
		bottom: 20rpx;
		left: 20rpx;
		right: 20rpx;
	}
</style>
