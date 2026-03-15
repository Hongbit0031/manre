<template>
	<view>
		<discussListComponent
			:list="discussList" 
			:loadStatus="loadStatus"
			@on-delete="handleDelete"
		></discussListComponent>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { getCurrentInstance, onMounted } from 'vue'
import { onReachBottom } from '@dcloudio/uni-app'
import discussListComponent from '@/components/discuss-list/discuss-list.vue'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const discussList = ref([])
const loadStatus = ref("loadmore")
const page = ref(1)

onMounted(() => {
	getDiscussList()
})

// 监听页面触底事件
onReachBottom(() => {
	page.value++
	getDiscussList()
})

function getDiscussList() {
	loadStatus.value = "loading"
	$H.post("discuss/myDis", {
		page: page.value
	}).then(res => {
		const data = Array.isArray(res.result.data) ? res.result.data : []
		discussList.value = discussList.value.concat(data)
		if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
			loadStatus.value = "nomore"
		} else {
			loadStatus.value = "loadmore"
		}
	})
}

function handleDelete(id) {
	// 从列表中移除被删除的项
	const index = discussList.value.findIndex(item => item.id === id)
	if (index > -1) {
		discussList.value.splice(index, 1)
	}
	
	// 如果当前页面数据为空且不是第一页，重新加载上一页数据
	if (discussList.value.length === 0 && page.value > 1) {
		page.value--
		getDiscussList()
	}
	// 如果是第一页且没有数据，设置为无更多数据状态
	else if (discussList.value.length === 0) {
		loadStatus.value = "nomore"
	}
}
</script>

<style lang="scss">

</style>
