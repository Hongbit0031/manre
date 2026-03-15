<template>
	<view class="container">
		<!-- 顶部提示信息 -->
		<view class="tip-box" @click="protocol('app_standard_context')">
			<u-icon name="volume-up" size="28rpx" color="#f9f9f9"></u-icon>
			<text class="tip-text">提醒：请遵守平台的《社区规范协议》,文明发话题</text>
		</view>

		<!-- 表单内容 -->
		<view class="form-box">
			<view class="form-item">
				<text class="required">*</text>
				<text class="label">话题名称</text>
				<view class="input-box">
					<input class="input" type="text" v-model="form.title" placeholder="请填写话题名称"
						placeholder-class="placeholder" maxlength="20" />
					<text class="count">{{ form.title.length }}/20</text>
				</view>
			</view>

			<view class="form-item">
				<text class="required">*</text>
				<text class="label">话题介绍</text>
				<view class="textarea-box">
					<textarea class="textarea" v-model="form.introduce" placeholder="请填写话题介绍"
						placeholder-class="placeholder" maxlength="400" />
					<text class="count">{{ form.introduce.length }}/400</text>
				</view>
			</view>
		</view>

		<!-- 底部按钮 -->
		<view class="footer">
			<button class="submit-btn" :disabled="btnDisabled" :loading="btnLoading" @tap="disAdd">创建话题</button>
		</view>
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

	const btnDisabled = ref(false)
	const btnLoading = ref(false)
	const form = reactive({
		topicId: "",
		introduce: "",
		title: "",
	})

	onLoad((options) => {
		form.topicId = options.topicId
	})

	function protocol(type) {
		uni.navigateTo({
			url: '/pages/user/protocol?type=' + type
		})
	}

	function disAdd() {
		if (!form.title.trim()) {
			uni.showToast({
				title: '请填写话题名称',
				icon: 'none'
			})
			return
		}
		if (!form.introduce.trim()) {
			uni.showToast({
				title: '请填写话题介绍',
				icon: 'none'
			})
			return
		}

		btnDisabled.value = true
		btnLoading.value = true

		$H.post("discuss/addDis", form).then(res => {
			if (res.code == 0) {
				uni.showToast({
					title: res.msg,
					icon: 'success'
				})
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			} else {
				uni.showToast({
					title: res.msg,
					icon: 'none'
				})
			}
			btnDisabled.value = false
			btnLoading.value = false
		})
	}
</script>

<style lang="scss" scoped>
	.container {
		min-height: 100vh;
		background-color: #fff;
		display: flex;
		flex-direction: column;
	}

	.tip-box {
		background-color: #444;
		padding: 14rpx 30rpx;

		.tip-text {
			color: #fff;
			font-size: 24rpx;
			line-height: 1.4;
			margin-left: 10rpx;
		}
	}

	.form-box {
		padding: 30rpx;
		flex: 1;

		.form-item {
			margin-bottom: 40rpx;

			.required {
				color: #FF0000;
				margin-right: 4rpx;
			}

			.label {
				font-size: 28rpx;
				color: #333;
				margin-bottom: 20rpx;
				display: inline-block;
			}

			.input-box,
			.textarea-box {
				position: relative;
				margin-top: 16rpx;

				.count {
					position: absolute;
					right: 20rpx;
					bottom: 20rpx;
					font-size: 24rpx;
					color: #999;
				}
			}

			.input {
				width: 100%;
				height: 80rpx;
				background-color: #F8F8F8;
				border-radius: 8rpx;
				padding: 0 30rpx;
				font-size: 28rpx;
			}

			.textarea {
				width: 100%;
				height: 240rpx;
				background-color: #F8F8F8;
				border-radius: 8rpx;
				padding: 20rpx 30rpx;
				font-size: 28rpx;
			}

			.placeholder {
				color: #999;
				font-size: 28rpx;
			}
		}
	}

	.footer {
		padding: 30rpx;

		.submit-btn {
			width: 100%;
			height: 88rpx;
			background-color: #444;
			color: #fff;
			font-size: 32rpx;
			border-radius: 44rpx;
			display: flex;
			align-items: center;
			justify-content: center;

			&[disabled] {
				opacity: 0.6;
			}
		}
	}
</style>