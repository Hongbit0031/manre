<template>
	<view>
		<!-- navbar -->
		<u-navbar title="圈子介绍" :custom-back="onBack">
		</u-navbar>
		<view>
			<!-- 基本信息 -->
			<view class="header">
				<image class="avatar" :src="topicInfo.coverImage"></image>
				<view class="right">
					<view class="top">
						<text class="name">{{ topicInfo.topicName }}</text>
						<view class="num">
							<text>{{ numberFormat(topicInfo.postCount) }}动态</text>
							<text>{{ numberFormat(topicInfo.userCount) }}成员</text>
						</view>
					</view>
					<view class="bottom ">
						<div class="tag">
							简介
						</div>
						<text class="desc">{{ topicInfo.description || '暂无简介' }}</text>
					</view>
				</view>
			</view>
					<!-- 圈主 -->
		<view class="user-block" v-if="topicInfo.userInfo && topicInfo.userInfo.uid">
			<view class="title">圈主</view>
			<UserListComp :list="[topicInfo.userInfo]" loadStatus="none"></UserListComp>
		</view>
			<!-- 圈内管理员 -->
			<section class="circle-manage box" v-if="adminList.length > 0">
				<view class="title">圈内管理</view>
				<view class="wrap">
					<block v-for="(item, index) in topicInfo.adminList" :key="index">
						<view class="item" @click="toUserHome(item.uid)">
							<image :src="item.avatar" mode=""></image>
							<view class="name">{{ item.username }}</view>
						</view>
					</block>
					<view @click="jump('/pages/topic/admin?id=' + topicInfo.id)" class="add-btns"
						v-if="topicInfo.uid == sessionUser.uid">
						<image class="add-btn-img" src="/static/images/add-icon.png" mode=""></image>
					</view>
				</view>
			</section>
			<!-- 私密圈子不可见 -->
			<view v-if="!topicInfo.isJoin && topicInfo.isPrivacy==1">
				<u-empty margin-top="100" text="私密圈子加入后才能查看成员~" mode="permission"></u-empty>
			</view>
			<view v-if="topicInfo.isJoin || topicInfo.isPrivacy==0">
				
				<u-line margin="20rpx 0rpx"></u-line>
				
				<!-- 圈友 -->
				<section class="circle-member ">
					<view class="top">
						<view class="title">圈子成员</view>
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
								<image style="height: 22rpx;margin-left:10rpx ;" mode="heightFix" v-if="item.level" :src="$IMG+'/vip/level_'+item.level+'.png'" class="level-img-2"/>
								<view class="btn-wrap">
									<view class="delete btn" v-if="topicInfo.uid == sessionUser.uid || topicInfo.isAdmin"
										@click.stop="removeTopic(item.uid, index)">
										删除
									</view>
									<view v-if="!item.hasFollow" class="describe btn" @click.stop="follow(item.uid, index)">
										关注</view>
									<view v-else class="describe btn" style="background-color: #999;"
										@click.stop="cancelFollow(item.uid, index)">取关</view>
								</view>
							</view>
						</block>
					</view>
				</section>
			</view>
		</view>
		<!-- 自定义加载 -->
		<toast  color="#fff" type="round"></toast>
	</view>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import UserListComp from '@/components/user-list/user-list.vue'
import { numberFormat } from '@/utils/filters.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $u = proxy.$u
const $IMG = proxy.$IMG
const $loading = proxy.$loading

const userList = ref([])
const page = ref(1)
const loadStatus = ref('loadmore')
const topicId = ref('')
const topicInfo = ref({
	userInfo: {},
	adminList: []
})
const adminList = ref([])
const current = ref(0)
const sessionUser = uni.getStorageSync('userInfo')
const searchContent = ref('')

onLoad((options) => {
	topicId.value = options.id
	getTopicInfo()
})

onReachBottom(() => {
	page.value++
	getUserList()
})

function onBack() {
	let pages = getCurrentPages()
	if (pages.length > 1) {
		uni.navigateBack()
	} else {
		uni.switchTab({
			url: '/pages/index/index'
		})
	}
}

function getTopicInfo() {
	$loading(true)
	$H.get('topic/detail', {
		id: topicId.value
	}).then(res => {
		topicInfo.value = res.result
		if (topicInfo.value.adminList != null) {
			adminList.value = topicInfo.value.adminList
		}
		$loading(false)
		getUserList()
	})
}

function searchList() {
	userList.value = []
	getUserList()
}

function getUserList() {
	// 私密判断拦截
	if (!topicInfo.value.isJoin && topicInfo.value.isPrivacy == 1) {
		return
	}
	loadStatus.value = 'loading'
	$H.post('topic/user', {
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

function toUserHome(uid) {
	uni.navigateTo({
		url: '/pages/user/home?uid=' + uid
	})
}

function jump(url) {
	uni.navigateTo({
		url
	})
}

function follow(uid, index) {
	$H.post('user/addFollow', {
		id: uid
	}).then(res => {
		if (res.code === 0) {
			userList.value[index].hasFollow = true
			$u.toast(res.msg)
		}
	})
}

function cancelFollow(uid, index) {
	$H.post('user/cancelFollow', {
		id: uid
	}).then(res => {
		if (res.code === 0) {
			userList.value[index].hasFollow = false
			$u.toast(res.msg)
		}
	})
}

function removeTopic(uid, index) {
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
</script>
<style>
	page {
		background-color: #f5f5f5;
	}
</style>
<style lang="scss" scoped>
	.tab-box {
		margin: auto;
	}

	.header {
		display: flex;
		margin-bottom: 20rpx;
		background-color: #fff;
		padding: 30rpx;
		padding-right: 0;

		.avatar {
			width: 120rpx;
			height: 120rpx;
			border-radius: 10rpx;
			margin-right: 20rpx;
		}

		.right {
			width: 80%;
			display: flex;
			flex-direction: column;
			margin-top: 10rpx;

			.top {
				display: flex;
				justify-content: space-between;
				align-items: flex-top;

				.name {
					width: 330rpx;
					font-weight: 600;
				}

				.num {
					font-size: 24rpx;
					color: #999;

					text {
						margin-right: 20rpx;
					}
				}
			}

			.bottom {
				display: flex;
				margin-top: 10rpx;

				.tag {
					width: 70rpx;
					height: 42rpx;
					font-size: 20rpx;
					text-align: center;
					border-radius: 10rpx;
					border: .5px solid #dddddd;
					margin-right: 15rpx;
				}

				.desc {
					font-size: 24rpx;
					color: #999;
					width: 500rpx;
				}
			}


		}
	}

	.desc-box {
		background-color: #fff;
		padding: 30rpx;
		min-height: 990rpx;

		.title {
			margin-bottom: 20rpx;
		}

		.desc {
			color: #616161;
			line-height: 1.8;
		}
	}

	.user-block {
		margin-bottom: 20rpx;
		background-color: #fff;
		padding: 20rpx;

		&:last-child {
			margin-bottom: 0;
		}

		.title {
			font-weight: 800;
			font-size: 36rpx;
			line-height: 46rpx;
			padding-left: 26rpx;
		}
	}

	.box {
		padding: 26rpx 36rpx;
		font-weight: 800;

		.title {
			font-size: 36rpx;
			line-height: 46rpx;
		}
	}

	.circle-manage {
		border-bottom: 2rpx solid #e2e2e2;
		background-color: #fff;

		.wrap {
			margin-top: 20rpx;
			display: flex;

			.item {
				margin-right: 20rpx;
				display: flex;
				flex-direction: column;
				justify-content: center;
				align-items: center;

				image {
					width: 112rpx;
					height: 112rpx;
					border-radius: 200rpx;
				}

				.name {
					margin-top: 10rpx;
					max-width: 130rpx;
					font-size: 30rpx;
					line-height: 40rpx;
				}
			}

			.add-btns {
				width: 112rpx;
				height: 112rpx;
				background: #e8e8e8;
				border: 1px dashed #000000;
				border-radius: 100rpx;

				.add-btn-img {
					width: 60rpx;
					height: 60rpx;
					align-items: center;
					margin: 25rpx;
				}
			}
		}
	}

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