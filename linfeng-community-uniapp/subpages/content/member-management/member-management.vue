<template>
	<view>
		<section class="circle-member" v-if="type==0">
			<view class="top">
				<view class="title">圈子成员管理</view>
			</view>
			<view style="margin: 6rpx;width: 100%;">
				<u-search placeholder="输入用户名搜索成员" :show-action="false" v-model="searchContent" @custom="searchList"
					@search="searchList" @change="searchList"></u-search>
			</view>
			<view class="wrap">
				<block v-for="(item, index) in userList" :key="index">
					<view class="item" @click="toUserHome(item.uid)">
						<image :src="item.avatar"></image>
						<view class="name">{{ item.username }}</view>
						<view class="btn-wrap">
							<view class="delete btn" v-if="topicInfo.uid == sessionUser.uid || topicInfo.isAdmin"
								@click.stop="removeTopic(item.uid, index)">
								删除
							</view>
							<view v-if="!item.isBlock" class="describe btn" @click.stop="doBlock(item, index)">
								拉黑</view>
						</view>
					</view>
				</block>
			</view>
		</section>
		<section class="circle-member" v-if="type==1">
			<view class="top">
				<view class="title">圈子黑名单用户管理</view>
			</view>
			<view style="margin: 6rpx;width: 100%;">
				<u-search placeholder="输入用户名搜索成员" :show-action="false" v-model="searchContent" @custom="searchList"
					@search="searchList" @change="searchList"></u-search>
			</view>
			<view class="wrap">
				<block v-for="(item, index) in userList" :key="index">
					<view class="item" @click="toUserHome(item.uid)">
						<image :src="item.avatar"></image>
						<view class="name">{{ item.username }}</view>
						<view class="btn-wrap">
							<view class="delete btn" v-if="topicInfo.uid == sessionUser.uid || topicInfo.isAdmin"
								@click.stop="removeBlock(item, index)">
								解除
							</view>
						</view>
					</view>
				</block>
			</view>
		</section>

		<!-- 加载状态 -->
		<block v-if="userList.length === 0 && loadStatus == 'nomore'">
			<u-empty margin-top="100" text="暂无用户" mode="favor"></u-empty>
		</block>
		<block v-else>
			<u-loadmore margin-bottom="50" margin-top="50" v-if="userList.length > 10" :status="loadStatus" />
		</block>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $u = proxy.$u
const $f = proxy.$f

// Reactive data
const userList = ref([])
const page = ref(1)
const loadStatus = ref('loadmore')
const topicId = ref('')
const searchContent = ref('')
const sessionUser = uni.getStorageSync('userInfo')
const topicInfo = ref({})
const type = ref(0) // 0成员管理  1黑名单列表

// Methods
const getTopicInfo = () => {
	$H.get('topic/detail', {
		id: topicId.value
	}).then(res => {
		topicInfo.value = res.result
		if (topicInfo.value.uid != sessionUser.uid && !topicInfo.value.isAdmin) {
			$f.toast("你不是该圈子管理员")
			setTimeout(() => {
				uni.switchTab({
					url: '/pages/index/index'
				})
			}, 1000)
		}
	})
}

const getUserList = () => {
	let requestUrl = 'topic/userList'
	if (type.value == 1) {
		requestUrl = 'topic/blockUserList'
	}
	loadStatus.value = 'loading'
	$H.post(requestUrl, {
		page: page.value,
		id: topicId.value,
		searchContent: searchContent.value
	}).then(res => {
		userList.value = userList.value.concat(res.result.data)
		if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
			loadStatus.value = 'nomore'
		} else {
			loadStatus.value = 'loadmore'
		}
	})
}

const searchList = () => {
	userList.value = []
	getUserList()
}

const removeTopic = (uid, index) => {
	uni.showModal({
		title: '警告',
		content: '删除用户后用户退出圈子，后续用户可以重新加入圈子，你确定将该用户移出圈子吗？',
		success: (res) => {
			if (res.confirm) {
				$H.post('user/removeTopic', {
					id: uid,
					topicId: topicId.value
				}).then(res => {
					if (res.code === 0) {
						userList.value = []
						getUserList()
						$u.toast(res.msg)
					}
				})
			}
		}
	})
}

const doBlock = (item, index) => {
	uni.showModal({
		title: '警告',
		content: '拉黑后用户自动退出圈子并且无法进圈，你确定将用户【' + item.username + '】拉入圈子黑名单吗？',
		success: (res) => {
			if (res.confirm) {
				$H.post('topic/doBlock', {
					topicId: topicId.value,
					uid: item.uid
				}).then(res => {
					if (res.code == 0) {
						$u.toast('拉黑成功')
						setTimeout(() => {
							uni.redirectTo({
								url: '/subpages/content/member-management/member-management?type=1&id=' + topicId.value
							})
						}, 1500)
					}
				})
			}
		}
	})
}

const removeBlock = (item, index) => {
	uni.showModal({
		title: '警告',
		content: '解除用户黑名单后用户可以重新申请加入圈子，你确定将用户【' + item.username + '】移出圈子黑名单吗？',
		success: (res) => {
			if (res.confirm) {
				$H.post('topic/removeBlock', {
					topicId: topicId.value,
					uid: item.uid
				}).then(res => {
					if (res.code == 0) {
						$u.toast('解除成功')
						setTimeout(() => {
							uni.redirectTo({
								url: '/pages/topic/detail?id=' + topicId.value
							})
						}, 1500)
					}
				})
			}
		}
	})
}

const toUserHome = (uid) => {
	uni.navigateTo({
		url: '/pages/user/home?uid=' + uid
	})
}

// Lifecycle hooks
onLoad((options) => {
	if (options.type) {
		type.value = options.type
	}
	topicId.value = options.id
	getTopicInfo()
	getUserList()
})

onReachBottom(() => {
	page.value++
	getUserList()
})
</script>

<style lang="scss" scoped>
	.member-item {
		display: flex;
		align-items: center;
		padding: 20rpx;
		border-bottom: 1px solid #F5F5F5;
		background-color: #FFFFFF;
	}

	.member-item .icon-nv {
		color: #ff4d94;
	}

	.member-item .icon-nan {
		color: #0091ff;
	}

	.member-item .avatar {
		margin-right: 20rpx;
	}

	.member-item .user .name {
		margin-right: 20rpx;
	}

	.member-item .user .iconfont {
		font-size: 12px;
	}

	/* #ifndef MP */
	.member-item .btn-gz {
		// margin-left: auto;
		margin-right: 20rpx;
	}

	/* #endif */
	/* #ifdef MP */
	.member-item .btn-gz {
		margin-left: auto;
	}

	/* #endif */
	.circle-member {
		background-color: #fff;

		.top {
			padding-left: 36rpx;
			font-size: 36rpx;
			font-weight: 800;
		}

		.wrap {
			.item {
				padding: 22rpx 36rpx;
				display: flex;
				align-items: center;
				border-bottom: 2rpx solid #e2e2e2;

				image {
					width: 108rpx;
					height: 108rpx;
					border-radius: 100rpx;
				}

				.name {
					font-size: 38rpx;
					max-width: 300rpx;
					margin-left: 8rpx;
				}

				.btn-wrap {
					margin-left: auto;
					display: flex;

					.btn {
						width: 94rpx;
						height: 42rpx;
						font-size: 26rpx;
						line-height: 42rpx;
						border-radius: 8rpx;
						text-align: center;
						color: #fff;
					}

					.delete {
						background-color: #ec4b3d;
						margin-right: 18rpx;
					}

					.describe {
						background-color: #000000;
					}
				}
			}
		}
	}
</style>