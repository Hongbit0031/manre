<template>
	<view class="page-container">
		<view class="content-section">
			<!-- 基本信息面板 -->
			<view class="form-panel">
				<view class="panel-header">
					<u-icon name="edit-pen" size="36" color="#5c6bc0" class="panel-icon"></u-icon>
					<text class="panel-title">基本信息</text>
				</view>
				
				<view class="form-item">
					<text class="form-label required">圈子名称</text>
					<input 
						class="uni-input" 
						v-model="form.topicName" 
						placeholder="请填写圈子名称" 
						placeholder-class="input-placeholder"
					/>
				</view>
				
				<view class="form-item">
					<text class="form-label required">圈子介绍</text>
					<textarea 
						class="uni-textarea" 
						v-model="form.description" 
						placeholder="请填写圈子介绍" 
						maxlength="500"
						placeholder-class="input-placeholder"
					/>
					<text class="textarea-count">{{ form.description.length }}/500</text>
				</view>
			</view>

			<!-- 圈子设置面板 -->
			<view class="form-panel">
				<view class="panel-header">
					<u-icon name="setting" size="36" color="#5c6bc0" class="panel-icon"></u-icon>
					<text class="panel-title">圈子设置</text>
				</view>
				
				<view class="toggle-item">
					<view class="toggle-content">
						<view class="toggle-title">圈子隐私</view>
						<view class="toggle-desc">公开的圈子任何人可见，私密圈子仅成员可见</view>
					</view>
					<view class="toggle-control">
						<view v-if="form.isPrivacy==0" class="status-tag status-public">
							<u-icon name="eye" size="24" color="#34A853"></u-icon>
							<text>已公开</text>
						</view>
						<view v-if="form.isPrivacy==1" class="status-tag status-private">
							<u-icon name="lock" size="24" color="#3f51b5"></u-icon>
							<text>已私密</text>
						</view>
						<switch color="#5c6bc0" :checked="form.isPrivacy==1" @change="changeBtn2" />
					</view>
				</view>
				
				<view class="divider"></view>
				
				<view class="toggle-item">
					<view class="toggle-content">
						<view class="toggle-title">进圈验证</view>
						<view class="toggle-desc">开启后，用户需回答问题并通过审核才能加入</view>
					</view>
					<view class="toggle-control">
						<view v-if="!form.rest" class="status-tag status-off">
							<u-icon name="close-circle" size="24" color="#969696"></u-icon>
							<text>已关闭</text>
						</view>
						<view v-if="form.rest" class="status-tag status-on">
							<u-icon name="checkmark-circle" size="24" color="#5c6bc0"></u-icon>
							<text>已开启</text>
						</view>
						<switch color="#5c6bc0" :checked="form.rest" @change="changeBtn" />
					</view>
				</view>

				<view class="form-item" v-if="form.rest">
					<text class="form-label">进圈问题设置</text>
					<textarea 
						class="uni-textarea" 
						v-model="form.question" 
						placeholder="请填写用户入圈需要回答的问题,由圈子管理员或圈主审核(100字以内)" 
						maxlength="100"
						placeholder-class="input-placeholder"
					/>
					<text class="textarea-count">{{ form.question ? form.question.length : 0 }}/100</text>
				</view>
			</view>

			<!-- 圈子形象面板 -->
			<view class="form-panel">
				<view class="panel-header">
					<u-icon name="photo" size="36" color="#5c6bc0" class="panel-icon"></u-icon>
					<text class="panel-title">圈子形象</text>
				</view>
				
				<view class="form-item">
					<text class="form-label required">圈子头像</text>
					<view class="upload-guide">
						<u-icon name="info-circle" size="24" color="#909399"></u-icon>
						<text class="upload-hint">上传一个代表圈子特色的头像</text>
					</view>
					<u-upload 
						ref="uUpload" 
						name="Image" 
						:file-list="fileList2" 
						:header="header" 
						:action="uploadImgUrl"
						max-count="1" 
						@on-uploaded="onUploaded2" 
						:before-upload="beforeUpload"
						@on-remove="onRemove2">
					</u-upload>
				</view>
				
				<view class="divider"></view>
				
				<view class="form-item">
					<text class="form-label required">圈子背景</text>
					<view class="upload-guide">
						<u-icon name="info-circle" size="24" color="#909399"></u-icon>
						<text class="upload-hint">上传一个展示圈子风格的背景图片</text>
					</view>
					<u-upload 
						ref="uUpload" 
						name="Image" 
						:file-list="fileList1" 
						:header="header" 
						:action="uploadImgUrl"
						max-count="1" 
						@on-uploaded="onUploaded1" 
						:before-upload="beforeUpload"
						@on-remove="onRemove1">
					</u-upload>
				</view>
			</view>
		</view>

		<!-- Submit Footer -->
		<view class="submit-footer">
			<button 
				class="submit-button" 
				hover-class="button-hover"
				@click="submit">
				<view class="button-content">
					<u-icon name="checkmark" size="32" color="#FFFFFF" class="button-icon"></u-icon>
					<text>保存修改</text>
				</view>
			</button>
		</view>
	</view>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import config from '@/utils/config.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $u = proxy.$u

const uploadImgUrl = config.domain + 'common/upload'
const header = ref({
	token: uni.getStorageSync('token')
})
const form = reactive({
	topicName: '',
	description: '',
	bgImage: '',
	coverImage: '',
	id: '',
	rest: false,
	question: '',
	isPrivacy: 0
})
const fileList1 = ref([])
const fileList2 = ref([])

onLoad((options) => {
	getTopicInfo(options.topicId)
})

function changeBtn(e) {
	form.rest = e.detail.value
}

function changeBtn2(e) {
	if (e.detail.value) {
		form.isPrivacy = 1
		form.rest = true
	} else {
		form.isPrivacy = 0
		form.rest = false
	}
}

function beforeUpload() {
	uni.showLoading({
		mask: true,
		title: '上传中'
	})
}

function onUploaded1(e) {
	form.bgImage = e[0].response.result
	uni.hideLoading()
}

function onUploaded2(e) {
	form.coverImage = e[0].response.result
	uni.hideLoading()
}

function onRemove1(index) {
	form.bgImage = ''
}

function onRemove2(index) {
	form.coverImage = ''
}

function submit() {
	if (form.rest && !form.question) {
		$u.toast('问题内容不能为空')
		return
	}
	if (form.rest && form.question.length > 100) {
		$u.toast('问题内容不能大于100字')
		return
	}
	$H.post('topic/topicEdit', form).then(res => {
		if (res.code == 0) {
			$u.toast('保存成功')
			uni.navigateBack()
		}
	})
}

function getTopicInfo(topicId) {
	$H.get('topic/detail', {
		id: topicId
	}).then(res => {
		fileList1.value = [{
			url: res.result.bgImage
		}]

		fileList2.value = [{
			url: res.result.coverImage
		}]

		// 使用 Object.assign 更新 reactive 对象
		Object.assign(form, res.result)
		
		if (res.result.rest == 1) {
			form.rest = true
		} else {
			form.rest = false
		}
	})
}
</script>

<style scoped>
	page {
		background-color: #f9f9fb;
		min-height: 100%;
	}

	.page-container {
		padding-bottom: 140rpx;
		display: flex;
		flex-direction: column;
	}

	.content-section {
		flex: 1;
		padding: 0 20rpx;
	}

	.form-panel {
		background-color: #ffffff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.06);
	}

	.panel-header {
		display: flex;
		align-items: center;
		margin-bottom: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1px solid #f5f5f7;
	}

	.panel-icon {
		margin-right: 12rpx;
	}

	.panel-title {
		font-size: 34rpx;
		font-weight: 600;
		color: #333;
	}

	.form-item {
		margin-bottom: 30rpx;
	}

	.form-label {
		display: block;
		font-size: 28rpx;
		color: #333;
		margin-bottom: 16rpx;
		font-weight: 500;
	}

	.required::before {
		content: "*";
		color: #ff5252;
		margin-right: 4rpx;
	}

	.uni-input {
		height: 88rpx;
		border-bottom: 1px solid #f0f0f0;
		font-size: 28rpx;
		padding: 0 20rpx;
	}

	.uni-textarea {
		width: 100%;
		height: 200rpx;
		background-color: #f9f9fb;
		border-radius: 8rpx;
		padding: 20rpx;
		font-size: 28rpx;
		box-sizing: border-box;
	}

	.textarea-count {
		text-align: right;
		font-size: 24rpx;
		color: #999;
		margin-top: 8rpx;
		display: block;
	}

	.input-placeholder {
		color: #c0c0c0;
	}

	.toggle-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 24rpx 0;
	}

	.toggle-content {
		flex: 1;
	}

	.toggle-title {
		font-size: 30rpx;
		font-weight: 500;
		color: #333;
		margin-bottom: 8rpx;
	}

	.toggle-desc {
		font-size: 24rpx;
		color: #909399;
	}

	.toggle-control {
		display: flex;
		align-items: center;
	}

	.status-tag {
		display: flex;
		align-items: center;
		font-size: 24rpx;
		padding: 6rpx 20rpx;
		border-radius: 100rpx;
		margin-right: 24rpx;
		gap: 6rpx;
	}

	.status-public {
		background-color: rgba(52, 168, 83, 0.12);
		color: #34A853;
	}

	.status-private {
		background-color: rgba(66, 133, 244, 0.12);
		color: #4285F4;
	}

	.status-on {
		background-color: rgba(124, 77, 255, 0.12);
		color: #5c6bc0;
	}

	.status-off {
		background-color: rgba(150, 150, 150, 0.12);
		color: #969696;
	}

	.divider {
		height: 1rpx;
		background-color: #f0f0f0;
		margin: 24rpx 0;
	}

	.upload-guide {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
		gap: 8rpx;
	}

	.upload-hint {
		font-size: 24rpx;
		color: #909399;
	}

	.submit-footer {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background-color: #ffffff;
		padding: 20rpx 30rpx;
		box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.08);
		z-index: 99;
		border-top-left-radius: 24rpx;
		border-top-right-radius: 24rpx;
	}

	.submit-button {
		width: 100%;
		height: 88rpx;
		background: linear-gradient(135deg, #5c6bc0 0%, #3f51b5 100%);
		color: #FFFFFF;
		border-radius: 44rpx;
		font-size: 32rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border: none;
	}

	.button-hover {
		opacity: 0.9;
	}

	.button-content {
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.button-icon {
		margin-right: 10rpx;
		display: flex;
		align-items: center;
	}
</style>