<template>
	<view class="page-container">
		<view @click="protocol('app_standard_context')">
			<u-notice-bar type="none" mode="vertical" :list="list"></u-notice-bar>
		</view>
		<view class="content-section">
			<view class="form-panel">
				<view class="panel-header">
					<u-icon name="edit-pen" size="36" color="#5c6bc0" class="panel-icon"></u-icon>
					<text class="panel-title">基本信息</text>
				</view>
				
				<view class="form-item">
					<text class="form-label required">圈子类目</text>
					<view class="input-container" @click="jumpClass">
						<text class="input-text">{{ cateName }}</text>
						<text class="input-arrow">▶</text>
					</view>
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

			<view class="form-panel">
				<view class="panel-header">
					<u-icon name="setting" size="36" color="#5c6bc0" class="panel-icon"></u-icon>
					<text class="panel-title">圈子设置</text>
				</view>
				
				<view class="toggle-item" v-if="privacyCircle">
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
						<switch color="#5c6bc0" :checked="privacySwitch" @change="changeBtn2" />
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

			<!-- Upload Card -->
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
					<view class="upload-container" v-if="!form.coverImage">
						<view class="upload-button" @click="chooseImage('coverImage')">
							<text class="upload-icon">+</text>
							<text class="upload-text">点击上传</text>
						</view>
					</view>
					<view class="image-preview" v-else>
						<image class="preview-image" :src="form.coverImage" mode="aspectFill"></image>
						<view class="preview-delete" @click="removeImage('coverImage')">×</view>
					</view>
				</view>
				
				<view class="divider"></view>
				
				<view class="form-item">
					<text class="form-label required">圈子背景</text>
					<view class="upload-guide">
						<u-icon name="info-circle" size="24" color="#909399"></u-icon>
						<text class="upload-hint">上传一个展示圈子风格的背景图片</text>
					</view>
					<view class="upload-container" v-if="!form.bgImage">
						<view class="upload-button" @click="chooseImage('bgImage')">
							<text class="upload-icon">+</text>
							<text class="upload-text">点击上传</text>
						</view>
					</view>
					<view class="image-preview" v-else>
						<image class="preview-image" :src="form.bgImage" mode="aspectFill"></image>
						<view class="preview-delete" @click="removeImage('bgImage')">×</view>
					</view>
				</view>
			</view>
		</view>

		<!-- Submit Footer -->
		<view class="submit-footer">
			<view class="agreement-section">
				<checkbox-group @change="isAgree">
					<label class="agreement-label">
						<checkbox color="#5c6bc0" value="agree" :checked="agree" />
						<text class="agreement-text" @click.stop="protocol('app_standard_context')">我已清楚平台规范并自愿承担责任</text>
					</label>
				</checkbox-group>
			</view>
			<view class="button-section">
				<button 
					v-if="canAdd" 
					:disabled="!canAdd" 
					@click="submit"
					class="submit-button" 
					hover-class="button-hover">
					<view class="button-content">
						<u-icon name="checkmark" size="32" color="#FFFFFF" class="button-icon"></u-icon>
						<text>创建圈子</text>
					</view>
				</button>
				<button 
					v-else
					disabled
					class="submit-button-disabled">
					<view class="button-content">
						<u-icon name="close" size="32" color="#FFFFFF" class="button-icon"></u-icon>
						<text>权限不足</text>
					</view>
				</button>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, reactive, getCurrentInstance, onUnmounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import config from '@/utils/config.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const agree = ref(true)
const cateList = ref([])
const cateName = ref('请选择')
const uploadImgUrl = config.domain + 'common/upload'
const header = ref({
	token: uni.getStorageSync('token')
})
const privacySwitch = ref(false)
const form = reactive({
	cateId: '',
	topicName: '',
	description: '',
	coverImage: '',
	bgImage: '',
	rest: false,
	question: '',
	isPrivacy: 0
})
const canAdd = ref(false)
const list = ref(['请遵守平台的《社区规范协议》,文明建圈'])
const privacyCircle = ref(false)

onLoad(() => {
	getCate()
	detection()
	privateCirclesOpen()
	
	// 监听类目选择事件
	uni.$on('selectCategory', handleCategorySelect)
})

onUnmounted(() => {
	// 移除事件监听
	uni.$off('selectCategory', handleCategorySelect)
})

function handleCategorySelect(data) {
	form.cateId = data.cateId
	cateName.value = data.cateName
}

function changeBtn(e) {
	form.rest = e.detail.value
}

function changeBtn2(e) {
	privacySwitch.value = e.detail.value
	if(e.detail.value){
		form.isPrivacy = 1
		form.rest = true
	}else{
		form.isPrivacy = 0
		form.rest = false
	}
}

function detection() {
	$H.get('topic/detection').then(res => {
		if (res.code == 0) {
			canAdd.value = res.result
		}
	})
}

function privateCirclesOpen() {
	$H.get('topic/privateCirclesOpen').then(res => {
		if (res.code == 0) {
			privacyCircle.value = res.result
		}
	})
}

function isAgree() {
	agree.value = !agree.value
}

function jumpClass() {
	uni.navigateTo({
		url: 'category'
	})
}

function chooseImage(field) {
	uni.chooseImage({
		count: 1,
		success: (res) => {
			const tempFilePaths = res.tempFilePaths
			uni.showLoading({
				mask: true,
				title: '上传中'
			})
			
			uni.uploadFile({
				url: uploadImgUrl,
				filePath: tempFilePaths[0],
				name: 'Image',
				header: header.value,
				success: (uploadRes) => {
					const data = JSON.parse(uploadRes.data)
					if (data.code === 0) {
						form[field] = data.result
					} else {
						uni.showToast({
							title: '上传失败',
							icon: 'none'
						})
					}
				},
				complete: () => {
					uni.hideLoading()
				}
			})
		}
	})
}

function removeImage(field) {
	form[field] = ''
}

function submit() {
	if (!agree.value) {
		uni.showToast({
			title: '请先勾选同意框',
			icon: 'none'
		})
		return
	}
	if (!form.cateId) {
		uni.showToast({
			title: '请选择圈子类目',
			icon: 'none'
		})
		return
	}

	if (!form.topicName) {
		uni.showToast({
			title: '请填写圈子名称',
			icon: 'none'
		})
		return
	}

	if (!form.description) {
		uni.showToast({
			title: '请填写圈子介绍',
			icon: 'none'
		})
		return
	}

	if (!form.coverImage) {
		uni.showToast({
			title: '请上传圈子头像',
			icon: 'none'
		})
		return
	}

	if (!form.bgImage) {
		uni.showToast({
			title: '请上传圈子主页背景图',
			icon: 'none'
		})
		return
	}
	if (form.rest && !form.question) {
		uni.showToast({
			title: '问题内容不能为空',
			icon: 'none'
		})
		return
	}
	if (form.rest && form.question.length > 100) {
		uni.showToast({
			title: '问题内容不能大于100字',
			icon: 'none'
		})
		return
	}
	$H.post('topic/topicAdd', form).then(res => {
		if (res.code == 0) {
			uni.showToast({
				title: '成功创建圈子',
				icon: 'success'
			})
			uni.redirectTo({
				url: '/pages/topic/detail?id=' + res.result
			})
		}
	})
}

function getCate() {
	$H.post('topic/classList').then(res => {
		cateList.value = res.result
	})
}

function protocol(type) {
	uni.navigateTo({
		url: '/pages/user/protocol?type=' + type
	})
}
</script>

<style scoped>
	page {
		background-color: #f9f9fb;
		min-height: 100%;
	}

	.page-container {
		padding-bottom: 200rpx;
		display: flex;
		flex-direction: column;
	}

	.header-section {
		padding: 20rpx 30rpx;
		background-color: #ffffff;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.03);
	}

	.notice-bar {
		display: flex;
		align-items: center;
		background-color: #f0f4ff;
		padding: 16rpx 20rpx;
		border-radius: 8rpx;
	}

	.notice-icon {
		margin-right: 16rpx;
		font-size: 36rpx;
	}

	.notice-swiper {
		height: 60rpx;
		flex: 1;
	}

	.notice-text {
		font-size: 28rpx;
		color: #5c6bc0;
		line-height: 60rpx;
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

	.input-container {
		display: flex;
		justify-content: space-between;
		align-items: center;
		height: 88rpx;
		border-bottom: 1px solid #f0f0f0;
		padding: 0 20rpx;
	}

	.input-text {
		font-size: 28rpx;
		color: #666;
	}

	.input-arrow {
		font-size: 24rpx;
		color: #999;
		transform: scale(0.8);
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

	.upload-container {
		width: 180rpx;
		height: 180rpx;
		border: 1px dashed #ddd;
		border-radius: 8rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		position: relative;
	}

	.upload-button {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		width: 100%;
		height: 100%;
	}

	.upload-icon {
		font-size: 48rpx;
		color: #ccc;
		margin-bottom: 10rpx;
	}

	.upload-text {
		font-size: 24rpx;
		color: #999;
	}

	.image-preview {
		width: 180rpx;
		height: 180rpx;
		position: relative;
		margin-right: 20rpx;
	}

	.preview-image {
		width: 100%;
		height: 100%;
		border-radius: 8rpx;
	}

	.preview-delete {
		position: absolute;
		right: -16rpx;
		top: -16rpx;
		background-color: rgba(0, 0, 0, 0.5);
		color: #fff;
		width: 36rpx;
		height: 36rpx;
		border-radius: 50%;
		text-align: center;
		line-height: 32rpx;
		font-size: 24rpx;
	}

	.submit-footer {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background-color: #ffffff;
		padding: 30rpx;
		box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.08);
		z-index: 99;
		border-top-left-radius: 24rpx;
		border-top-right-radius: 24rpx;
	}

	.agreement-section {
		margin-bottom: 24rpx;
	}

	.agreement-label {
		display: flex;
		align-items: center;
	}

	.agreement-text {
		font-size: 26rpx;
		color: #666;
		margin-left: 10rpx;
	}

	.button-section {
		display: flex;
		justify-content: center;
	}

	.submit-button {
		width: 90%;
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

	.submit-button-disabled {
		width: 90%;
		height: 88rpx;
		background-color: #cccccc;
		color: #FFFFFF;
		border-radius: 44rpx;
		font-size: 32rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border: none;
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