<template>
	<view class="apply-list-con" ref="container" :style="{ overflow: showNotice ? 'hidden' : 'auto', height: showNotice ? '100vh' : 'auto' }">
		<view class="nav acea-row">
			<view class="item" :class="types == 0 ? 'on' : ''" @click="changeTypes(0)">待审核</view>
			<view class="item" :class="types == 1 ? 'on' : ''" @click="changeTypes(1)">已通过</view>
			<view class="item" :class="types == 2 ? 'on' : ''" @click="changeTypes(2)">已拒绝</view>
		</view>
		<view class="apply-record">
			<view class="list">
				<view class="item" v-for="(item, listIndex) in list" :key="listIndex">
					<view style="display: flex;margin-left: 30rpx;">
						<u-avatar shape="square" size="80" fontSize="26" bgColor="#FEA356" :src="item.userInfo.avatar"
							@click="toUserHome(item.uid)"></u-avatar>
						<text style="margin-left: 20rpx;">{{ item.userInfo.username }}</text>
					</view>
					<view class="listn">
						<view class="itemn acea-row row-between-wrapper">
							<view @click="showAnswer(item.userInfo.username, item.answer)">
								<view class="name">{{ item.answer }}</view>
								<view>{{ item.createTime }}</view>
							</view>
							<button class="notice-btn agree" @click="agree(item.id)" v-if="item.status==0">
								同意
							</button>
							<button class="notice-btn refuse" @click="reject(item.id)" v-if="item.status==0">
								拒绝
							</button>
							<view class="btn-style" v-else-if="item.status==1">已通过</view>
							<view class="btn-style" v-else-if="item.status==2">已拒绝</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		<PopupComp v-if="showNotice" :title="popTitle" :content="popContent" @closePop="closePop"></PopupComp>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import PopupComp from "@/subpages/content/components/popup.vue"

const { proxy } = getCurrentInstance()
const $H = proxy.$H

// Reactive data
const loadStatus = ref('loading')
const types = ref(0)
const where = ref({
	page: 1,
	limit: 10
})
const list = ref([])
const topicId = ref('')
const popTitle = ref('')
const popContent = ref('')
const showNotice = ref(false)

// Methods
const closePop = () => {
	showNotice.value = false
}

const showAnswer = (name, value) => {
	showNotice.value = true
	popContent.value = value
	popTitle.value = name + "的回答"
}

const changeTypes = (val) => {
	if (val != types.value) {
		types.value = val
		list.value = []
		where.value.page = 1
	}
	getApplyLists()
}

const getApplyLists = () => {
	loadStatus.value = 'loading'
	$H.post('topic/applyInfoList', {
		page: where.value.page,
		limit: where.value.limit,
		type: types.value,
		topicId: topicId.value
	}).then(res => {
		list.value = list.value.concat(res.result.data)
		if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
			loadStatus.value = 'nomore'
		} else {
			loadStatus.value = 'loadmore'
		}
	})
}

const agree = (id) => {
	$H.post('topic/agreeApply', {
		id: id
	}).then(res => {
		if (res.code == 0) {
			where.value.page = 1
			list.value = []
			getApplyLists()
			uni.showToast({
				title: '操作成功',
				icon: 'none'
			})
		}
	})
}

const reject = (id) => {
	$H.post('topic/rejectApply', {
		id: id,
	}).then(res => {
		if (res.code == 0) {
			where.value.page = 1
			list.value = []
			getApplyLists()
			uni.showToast({
				title: '操作成功',
				icon: 'none'
			})
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
	types.value = options.types
	topicId.value = options.id
	getApplyLists()
})

onReachBottom(() => {
	where.value.page++
	getApplyLists()
})
</script>

<style lang="less" scoped>
	.notice-btn {
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 26rpx;
		color: #ffffff;
		// margin-bottom: 20rpx;

	}

	.agree {
		background-color: #2979ff;
	}

	.refuse {
		background-color: #f75555;
	}

	.apply-list-con .nav {
		background-color: #fff;
		height: 0.9 * 100rpx;
		width: 100%;
		line-height: 0.9 * 100rpx;
	}

	.apply-list-con .nav .item {
		flex: 1;
		-o-flex: 1;
		-ms-flex: 1;
		text-align: center;
		font-size: 0.3 * 100rpx;
		color: #282828;
		height: 100%;
	}

	.apply-list-con .nav .item.on {
		color: #55aaff;
		border-bottom: 0.03 * 100rpx solid #55aaff;
	}

	.apply-record {
		margin-top: 20rpx;
		background-color: #fff;
	}

	.apply-record .list .item {
		background-color: #fff;
		padding: 20rpx 0;
		border-bottom: 1px solid #eee;
	}

	.apply-record .list .item:last-child {
		border-bottom: none;
	}

	.apply-record .list .item .data {
		height: 80rpx;
		line-height: 80rpx;
		padding: 0 0.3 * 100rpx;
		font-size: 0.24 * 100rpx;
		color: #666;
	}

	.apply-record .list .item .listn {
		background-color: #fff;
		font-size: 0.24 * 100rpx;
		color: #999;
	}

	.apply-record .list .item .listn .itemn {
		height: 1.2 * 100rpx;
		padding-right: 0.3 * 100rpx;
		margin-left: 0.3 * 100rpx;
	}

	.apply-record .list .item .listn .itemn .name {
		width: 3.9 * 100rpx;
		font-size: 0.28 * 100rpx;
		color: #282828;
		margin-bottom: 0.06 * 100rpx;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.apply-record .list .item .listn .itemn .btn-style {
		font-size: 0.36 * 100rpx;
		font-family: 'GuildfordProBook 5';
		color: #ff0000;
		margin-bottom: 60rpx;
	}
</style>