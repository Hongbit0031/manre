<template>
	<view>
		<!-- 投票弹窗 -->
		<lf-popup v-model="showVotePopup" height="100%">
			<scroll-view scroll-y style="height: 100%;">
				<view class="vote-wrap">
					<lf-field v-model="title" placeholder="添加标题"></lf-field>
					<lf-field v-model="item.value" v-for="(item, index) in voteOption" :key="index"
						:placeholder="'选项' + (index + 1)" right-icon="close" @onRightIcon="onRightIcon(index)">
					</lf-field>
					<view class="option-add" @click="optionAdd">
						<u-icon name="plus" color="#333"></u-icon>
						<text class="txt">添加选项</text>
					</view>
					<!-- 投票类型 -->
					<view class="block-title">投票类型</view>
					<view class="choose-wrap">
						<view class="item" @click="type = 1" :class="{ active: type === 1 }">单选</view>
						<view class="item" @click="type = 2" :class="{ active: type === 2 }">多选</view>
					</view>
					<!-- 投票有效期 -->
					<view class="block-title">投票有效期</view>
					<view class="choose-wrap">
						<view class="item" @click="expireTime = 1" :class="{ active: expireTime === 1 }">1天</view>
						<view class="item" @click="expireTime = 2" :class="{ active: expireTime === 2 }">7天</view>
						<view class="item" @click="expireTime = 3" :class="{ active: expireTime === 3 }">30天</view>
						<view class="item" @click="expireTime = 4" :class="{ active: expireTime === 4 }">90天</view>
					</view>
					<view v-if="title && voteOption[0].value && voteOption[1].value" @click="showVotePopup = false"
						style="margin-top: 50rpx;">
						<lf-button color="#333333">下一步</lf-button>
					</view>
					<view v-else style="margin: 50rpx;"><button style="border-radius: 20rpx;">下一步</button></view>
				</view>
			</scroll-view>
		</lf-popup>
		<!-- 帖子 -->
		<view class="container">
			<textarea placeholder="说些什么叭..." :auto-height="true" maxlength="-1" v-model="content"
				class="post-txt"></textarea>
			<view class="vote-preview" @click="showVotePopup = true">
				<view class="title">{{ title }}</view>
				<view v-if="type === 1" class="type">单选</view>
				<view v-if="type === 2" class="type">多选</view>
				<view class="option-item" v-for="(item, index) in voteOption" :key="index">{{ item.value }}</view>
			</view>
			<!-- 选择圈子 -->
			<view v-if="isTopic" class="choose-item" @click="navigateToChooseTopic">
				<image class="icon" src="/static/p_1.png"></image>
				<text class="txt">{{ topicName }}</text>
				<u-icon class="u-icon" name="arrow-right"></u-icon>
			</view>
			<lf-button color="#333333" @click="submit">发布</lf-button>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad,
		onShow,
		onUnload
	} from '@dcloudio/uni-app'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H

	const title = ref('')
	const showVotePopup = ref(true)
	const voteOption = ref([{
			value: ''
		},
		{
			value: ''
		}
	])
	const type = ref(1)
	const expireTime = ref(1)
	const content = ref('')
	const form = ref({
		topicId: '',
		discussId: '',
		address: ''
	})
	const topicName = ref('请选择圈子')
	const disName = ref('选择话题')
	const isTopic = ref(true)

	onLoad((options) => {
		if (options.topicId) {
			form.value.topicId = options.topicId
			isTopic.value = false
		}
	})

	onShow(() => {
		// #ifdef MP-WEIXIN
		if (!uni.getStorageSync('hasLogin')) {
			$f.toast('登录后再发帖哦')
		}
		// #endif
		// #ifndef MP-WEIXIN
		if (!uni.getStorageSync('hasLogin')) {
			uni.navigateTo({
				url: "/pages/user/go-login"
			})
		}
		// #endif

		// 监听选择圈子事件
		uni.$on('chooseTopicForVote', (data) => {
			form.value.topicId = data.id
			topicName.value = data.name
			form.value.discussId = ''
			disName.value = '选择话题'
		})
	})

	onUnload(() => {
		// 移除事件监听
		uni.$off('chooseTopicForVote')
	})

	function navigateToChooseTopic() {
		uni.navigateTo({
			url: '/pages/topic/choose-topic/choose-topic?from=vote'
		})
	}

	function optionAdd() {
		voteOption.value.push({
			value: ''
		})
	}

	function onRightIcon(index) {
		voteOption.value.splice(index, 1)
	}

	function hasDuplicateValue(voteOpt) {
		const values = voteOpt.map(option => option.value)
		return values.some((value, index) => values.indexOf(value) !== index)
	}

	function submit() {
		if (!form.value.topicId) {
			uni.showToast({
				title: '请选择圈子',
				icon: 'none'
			})
			return
		}
		//过滤空的选项
		var r = voteOption.value.filter((s) => {
			if (s.value) {
				return s
			}
		})
		//筛选重复选项 拒绝发布
		const hasDuplicate = hasDuplicateValue(voteOption.value)
		if (hasDuplicate) {
			uni.showToast({
				title: '选项内容不能重复',
				icon: 'none'
			})
			return
		}

		voteOption.value = r
		$H.post('post/voteAdd', {
			topicId: form.value.topicId,
			address: form.value.address,
			content: content.value,
			voteTitle: title.value,
			voteType: type.value,
			expireTime: expireTime.value,
			voteOptions: voteOption.value
		}).then(res => {
			if (res.code == 0) {
				uni.redirectTo({
					url: '/pages/post/detail?id=' + res.result
				})
			}
		})
	}
</script>

<style lang="scss" scoped>
	.vote-wrap {
		.block-title {
			margin-bottom: 20rpx;
			margin-left: 20rpx;
		}

		.option-add {
			display: flex;
			align-items: center;
			justify-content: center;
			border: 1px solid #333;
			background-color: #f5f5f5;
			margin: 30rpx;
			padding: 20rpx;
			border-radius: 20rpx;

			.txt {
				margin-left: 10rpx;
			}
		}

		.choose-wrap {
			display: flex;
			margin: 20rpx;

			.item {
				padding: 20rpx 40rpx;
				font-size: 24rpx;
				margin-right: 20rpx;
				background-color: #f5f5f5;
				border-radius: 10rpx;
			}

			.active {
				background-color: #333;
				color: #fff;
			}
		}
	}

	.post-txt {
		width: 100%;
		padding: 20rpx 0;
		min-height: 300rpx;
	}

	.vote-preview {
		background-color: #f5f5f5;
		padding: 30rpx;
		border-radius: 20rpx;

		.type {
			font-size: 24rpx;
			color: #999;
			margin: 20rpx 0;
		}

		.option-item {
			background-color: #fff;
			border: 1px solid #999;
			border-radius: 10rpx;
			text-align: center;
			margin-bottom: 20rpx;
		}
	}

	.choose-item {
		display: flex;
		align-items: center;
		padding: 20rpx;
		border-bottom: 1px solid #f5f5f5;

		&:last-child {
			border: 0;
		}

		.txt {
			margin-left: 20rpx;
		}

		.icon {
			width: 40rpx;
			height: 40rpx;
		}

		.u-icon {
			margin-left: auto;
			color: #999;
		}

		.add-icon {
			margin-left: 0;
		}
	}
</style>