<template>
	<view v-if="disLists.length>0">
		<discuss-list :list="disLists" :loadStatus="loadStatus" @on-delete="handleDelete"></discuss-list>
	</view>
	<view v-else>
		<u-empty margin-top="100" text="该圈子暂无话题" mode="favor"></u-empty>
	</view>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad,
		onReachBottom
	} from '@dcloudio/uni-app'
	import discussList from '@/components/discuss-list/discuss-list'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H

	const disLists = ref([])
	const loadStatus = ref("loadmore")
	const page = ref(1)
	const topicId = ref("")

	onLoad((options) => {
		if (options.topicId) {
			topicId.value = options.topicId
		}

		getDisList()
	})

	onReachBottom(() => {
		page.value++
		getDisList()
	})

	function getDisList() {
		loadStatus.value = "loading"
		$H.post("discuss/list", {
			page: page.value,
			topicId: topicId.value
		}).then(res => {
			disLists.value = disLists.value.concat(res.result.data)
			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = "nomore"
			} else {
				loadStatus.value = "loadmore"
			}
		})
	}

	function handleDelete(id) {
		// 从列表中移除被删除的项
		const index = disLists.value.findIndex(item => item.id === id)
		if (index > -1) {
			disLists.value.splice(index, 1)
		}

		// 如果列表为空，显示空状态
		if (disLists.value.length === 0) {
			loadStatus.value = "nomore"
		}
	}
</script>

<style lang="scss">

</style>