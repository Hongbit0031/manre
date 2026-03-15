<template>
	<view>
		<!-- <u-form-item label="问题" label-position="top"><u-input type="textarea" v-model="form.question"
				placeholder="" /></u-form-item> -->
		<lf-field label="问题" type="textarea" v-model="form.question" :disabled2="dis"></lf-field>
		<lf-field label="回答" type="textarea" v-model="form.answer" placeholder="请填写你的回答(100字以内)"></lf-field>

		<!-- 提交按钮 -->
		<lf-button shape="circle" @click="submit">提交</lf-button>
	</view>
</template>

<script setup>
	import {
		ref,
		reactive,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad
	} from '@dcloudio/uni-app'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H
	const $u = proxy.$u

	// Reactive data
	const topicId = ref('')
	const dis = ref(true)
	const form = reactive({
		answer: '',
		question: '',
		topicId: ''
	})

	// Methods
	const getTopicInfo = () => {
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
			form.question = res.result.question
		})
	}

	const submit = () => {
		if (!form.answer) {
			$u.toast('回答内容不能为空')
			return
		}
		$H.post('topic/joinTopicApply', form).then(res => {
			if (res.code == 0) {
				$u.toast('请等待圈子管理员审核')
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			}
		})
	}

	// Lifecycle hooks
	onLoad((options) => {
		if (options.id) {
			topicId.value = options.id
			form.topicId = options.id
			getTopicInfo()
		}
	})
</script>

<style>

</style>