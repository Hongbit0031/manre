<template>
	<view>
		<view style="margin: 6rpx;width: 100%;">
			<u-search
				placeholder="输入用户名搜索"
				:show-action="false"
				v-model="searchContent"
				@custom="searchList"
				@search="searchList"
				@change="searchList"
			></u-search>
		</view>
		<block v-for="(item,index) in userList" :key="index">
			<view @click="toUserHome(item.uid)" class="member-item" v-if="type===0">
				<u-avatar class="avatar" :src="item.avatar"></u-avatar>
				<view class="user">
					<text class="name">{{item.username}}</text>
					<text v-if="item.gender == '男'" class="iconfont icon-nan"></text>
					<text v-if="item.gender == '女'" class="iconfont icon-nv"></text>
				</view>
				<block v-if="item.isAdmin">
					<u-button @click.stop="adminDel(index,item.uid)" class="btn-gz" type="primary"
						size="mini">解除管理员</u-button>
				</block>
				<block v-else>
					<u-button @click.stop="adminAdd(index,item.uid)" class="btn-gz" type="default"
						size="mini">设为管理员</u-button>
				</block>
			</view>
		
			<view @click="toUserHome(item.uid)" class="member-item" v-else-if="type===1">
				<u-avatar class="avatar" :src="item.avatar"></u-avatar>
				<view class="user">
					<text class="name">{{item.username}}</text>
					<text v-if="item.gender == '男'" class="iconfont icon-nan"></text>
					<text v-if="item.gender == '女'" class="iconfont icon-nv"></text>
				</view>
				<u-button @click.stop="giveTopic(index,item)" class="btn-gz" type="default"
					size="mini">转让圈主</u-button>
			</view>
		</block>
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

const userList = ref([])
const page = ref(1)
const loadStatus = ref('loadmore')
const topicId = ref('')
const searchContent = ref('')
const type = ref(0) // 0设置管理员  1转让圈主

onLoad((options) => {
	topicId.value = options.id
	if (options.type) {
		type.value = Number(options.type)
	}
	getUserList()
})

onReachBottom(() => {
	page.value++
	getUserList()
})

function getUserList() {
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

function adminAdd(index, uid) {
	$H.post('post/setAdmin', {
		topicId: topicId.value,
		uid: uid
	}).then(res => {
		if (res.code == 0) {
			$u.toast('已设置为管理员')
			userList.value[index].isAdmin = true
		}
	})
}

function adminDel(index, uid) {
	$H.post('post/cancelAdmin', {
		topicId: topicId.value,
		uid: uid
	}).then(res => {
		if (res.code == 0) {
			$u.toast('已解除管理员')
			userList.value[index].isAdmin = false
		}
	})
}

function searchList() {
	userList.value = []
	getUserList()
}

function giveTopic(index, item) {
	uni.showModal({
		title: '警告',
		content: '该操作不可逆，你确定转让圈主权限给用户【' + item.username + '】吗？',
		success: (res) => {
			if (res.confirm) {
				$H.post('topic/giveTopic', {
					topicId: topicId.value,
					uid: item.uid
				}).then(res => {
					if (res.code == 0) {
						$u.toast('转让成功')
						setTimeout(() => {
							uni.redirectTo({
								url: '/pages/my/topic'
							})
						}, 1500)
					}
				})
			}
		}
	})
}
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
</style>
