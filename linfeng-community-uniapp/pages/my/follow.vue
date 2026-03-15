<template>
	<view>
		<UserListComponent :list="userList" :loadStatus="loadStatus"></UserListComponent>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { getCurrentInstance } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import UserListComponent from '@/components/user-list/user-list.vue'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const userList = ref([])
const loadStatus = ref("loadmore")
const page = ref(1)
const uid = ref(0)

onLoad((options) => {
	if (options.uid) {
		uid.value = options.uid
	}
	getUserList(uid.value)
})

// 监听页面触底事件
onReachBottom(() => {
	page.value++
	getUserList(uid.value)
})

function getUserList(userId) {
	loadStatus.value = "loading"
	$H.get('user/follow', {
		page: page.value,
		uid: uid.value
	}).then(res => {
		if (res.result.data) {
			userList.value = userList.value.concat(res.result.data)
			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = "nomore"
			} else {
				loadStatus.value = "loadmore"
			}
		} else {
			loadStatus.value = "loadmore"
		}
	})
}
</script>

<style lang="scss">
	
</style>