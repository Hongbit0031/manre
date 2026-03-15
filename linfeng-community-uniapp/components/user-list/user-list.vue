<template>
	<view>
		<block v-for="(item, index) in userList" :key="index">
			<view @click="toUserHome(item.uid)" class="member-item">
				<u-avatar class="avatar" :src="item.avatar"></u-avatar>
				<view class="user">
					<text class="name">{{ item.username }}</text>
					<image style="height: 22rpx;margin-right: 10rpx ;" mode="heightFix" v-if="item.level" :src="$IMG+'/vip/level_'+item.level+'.png'" class="level-img-2"/>
					<text v-if="item.gender == 1" class="iconfont icon-nan"></text>
					<text v-if="item.gender == 2" class="iconfont icon-nv"></text>
				</view>
				
				<u-button :custom-style="customStyle" @click.stop="follow(index, item.uid)" v-if="item.hasFollow === 0" class="btn-gz" type="default" size="mini">关注</u-button>
				<u-button @click.stop="cancelFollow(index, item.uid)" v-if="item.hasFollow === 1" class="btn-gz" type="default" size="mini" plain>互相关注</u-button>
				<u-button @click.stop="cancelFollow(index, item.uid)" v-if="item.hasFollow === 2" class="btn-gz" type="default" size="mini" plain>已关注</u-button>
			</view>
		</block>
		<!-- 加载状态 -->
		<block v-if="loadStatus != 'none'">
			<block v-if="list.length === 0 && loadStatus == 'nomore'"><u-empty margin-top="100" text="暂无用户" mode="favor"></u-empty></block>
			<block v-else><u-loadmore margin-bottom="50" margin-top="50" :status="loadStatus" /></block>
		</block>
	</view>
</template>

<script setup>
import { ref, watch, getCurrentInstance } from 'vue'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $IMG = proxy.$IMG

const props = defineProps({
	list: {
		type: Array,
		default: () => []
	},
	loadStatus: {
		type: String,
		default: 'loadmore'
	}
})

const userList = ref([])
const customStyle = ref({
	backgroundColor: 'black',
	color: 'white'
})

watch(() => props.list, (newVal) => {
	userList.value = newVal
}, { immediate: true })

function toUserHome(uid) {
	uni.navigateTo({
		url: '/pages/user/home?uid=' + uid
	})
}

function follow(index, uid) {
	$H.post('user/addFollow', {
		id: uid
	}).then(res => {
		if (res.code == 0) {
			userList.value[index].hasFollow = 2
		}
	})
}

function cancelFollow(index, uid) {
	$H.post('user/cancelFollow', {
		id: uid
	}).then(res => {
		if (res.code === 0) {
			userList.value[index].hasFollow = 0
		}
	})
}
</script>

<style lang="scss" scoped>
.member-item {
	display: flex;
	align-items: center;
	padding: 20rpx;
	border-bottom: 1px solid #f5f5f5;
	background-color: #ffffff;
	&:last-child{
		border-bottom: 0;
	}
	
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
	margin-right: 20rpx;
}
/* #endif */
/* #ifdef MP */
.member-item .btn-gz {
	margin-left: auto;
}
/* #endif */
</style>
