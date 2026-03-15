<template>
	<view class="report-submit">
		<view class="report-submit__content">
			<view class="card info-card">
				<view class="card__text">
					本平台严厉打击引流广告、虚假不实信息、色情低俗、辱骂引战、违法违规、涉政言论等违规内容。
				</view>
			</view>

			<view class="card">
				<view class="card__header">
					<text class="card__title">选择具体类型</text>
					<text class="card__required">*</text>
				</view>
				<view class="type-selector">
					<view v-for="(item, index) in classList" :key="index" 
						class="type-selector__item" 
						:class="{ 'type-selector__item--active': selectedItem === item.id }" 
						@click="selectCate(item.id)">
						{{ item.cateName }}
					</view>
				</view>
			</view>

			<view class="card">
				<view class="card__header">
					<text class="card__title">举报描述</text>
					<text class="card__required">*</text>
				</view>
				<textarea 
					v-model="form.content" 
					class="card__textarea" 
					placeholder="请描述存在的问题，提供更多信息有助于举报被快速处理。"
					placeholder-style="color: #909399;">
				</textarea>
			</view>

			<view class="card">
				<view class="card__header">
					<text class="card__title">上传截图</text>
					<text class="card__subtitle">(最多3张)</text>
				</view>
				<view class="upload-area">
					<u-upload 
						ref="uUploadRef" 
						:size-type="['original']" 
						name="Image" 
						:max-count="3" 
						:header="header"
						:action="uploadImgUrl" 
						@on-uploaded="submit" 
						:auto-upload="false">
					</u-upload>
				</view>
			</view>
		</view>
		
		<view class="report-submit__footer">
			<button class="btn btn--primary" @click="uploadImg">提交举报</button>
		</view>
	</view>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

// 获取当前实例
const { proxy } = getCurrentInstance()

// 响应式数据
const uUploadRef = ref(null)
const uploadImgUrl = ref(proxy.$c.domain + 'common/upload')
const form = reactive({
	linkId: 0,
	cateId: '',
	content: '',
	media: [],
})
const header = ref({
	token: uni.getStorageSync('token')
})
const selectedItem = ref('')
const fileList = ref([])
const classList = ref([
	{
		id: 1,
		cateName: '帖子'
	},
	{
		id: 2,
		cateName: '评论'
	},
	{
		id: 3,
		cateName: '用户'
	},
	{
		id: 4,
		cateName: '圈子'
	},
])

// 方法
const selectCate = (id) => {
	selectedItem.value = id
	form.cateId = id
}

const uploadImg = () => {
	if (!form.cateId) {
		proxy.$u.toast('请选择举报类型')
		return
	}
	if (!form.content) {
		proxy.$u.toast('描述不能为空')
		return
	}
	uni.showLoading({
		mask: true,
		title: '提交中'
	})
	uUploadRef.value.upload()
}

const chooseClass = (id, name) => {
	form.cateId = id
	// cateName 变量未在模板中使用，移除
	// show 变量未定义，移除
}

const submit = (e) => {
	uni.showLoading({
		mask: true,
		title: '提交中'
	})

	let mediaList = []
	e.forEach(function(item, index) {
		mediaList.push(item.response.result)
	})

	form.media = mediaList
	if (form.media.length == 0) {
		uni.hideLoading()
		proxy.$u.toast('截图材料不能为空')
		return
	}

	proxy.$H.post('report/addReport', form).then(res => {
		if (res.code == 0) {
			proxy.$u.toast('举报成功')
			setTimeout(function() {
				uni.reLaunch({
					url: '/pages/report/list'
				})
			}, 1000)
		}
		uni.hideLoading()
	})
}

// 生命周期
onLoad((options) => {
	if (options.postId) {
		form.linkId = options.postId
		form.cateId = 1
		selectedItem.value = 1
		// cateName 变量未使用，移除
	}
	if (options.uid) {
		let userInfo = uni.getStorageSync("userInfo")
		if (userInfo) {
			if (userInfo.uid == options.uid) {
				proxy.$u.toast('不能举报自己')
				setTimeout(function() {
					uni.navigateBack()
				}, 1000)
			}
		} else {
			// #ifdef MP-WEIXIN
			uni.navigateTo({
				url: "/pages/user/login"
			})
			// #endif

			// #ifdef H5||APP-PLUS
			uni.navigateTo({
				url: "/pages/user/go-login"
			})
			// #endif
		}
	}
})
</script>

<style>
page {
	background-color: #f7f8fb;
}
</style>

<style lang="scss" scoped>
// Variables
$primary-color: #5c6bc0;
$secondary-color: #3f51b5;
$danger-color: #ff4757;
$text-primary: #303133;
$text-secondary: #606266;
$text-light: #909399;
$bg-color: #f7f8fb;
$card-bg: #ffffff;
$border-color: #f5f7fa;

// Mixins
@mixin flex-center {
	display: flex;
	align-items: center;
	justify-content: center;
}

@mixin card-style {
	background-color: $card-bg;
	border-radius: 16rpx;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
	margin-bottom: 24rpx;
}

.report-submit {
	min-height: 100vh;
	background-color: $bg-color;
	display: flex;
	flex-direction: column;
	padding-bottom: 140rpx;
	
	&__content {
		flex: 1;
		padding: 24rpx;
	}
	
	&__footer {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: $card-bg;
		padding: 24rpx;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
}

.card {
	@include card-style;
	padding: 30rpx;
	
	&__header {
		margin-bottom: 24rpx;
		display: flex;
		align-items: center;
	}
	
	&__title {
		font-size: 32rpx;
		color: $text-primary;
		font-weight: 600;
	}
	
	&__subtitle {
		font-size: 24rpx;
		color: $text-light;
		margin-left: 12rpx;
	}
	
	&__required {
		color: $danger-color;
		margin-left: 8rpx;
	}
	
	&__text {
		font-size: 28rpx;
		color: $text-secondary;
		line-height: 1.6;
	}
	
	&__textarea {
		width: 100%;
		height: 180rpx;
		background-color: $bg-color;
		border-radius: 12rpx;
		padding: 20rpx;
		font-size: 28rpx;
		color: $text-primary;
		box-sizing: border-box;
	}
}

.info-card {
	border-left: 8rpx solid $primary-color;
}

.type-selector {
	display: flex;
	flex-wrap: wrap;
	margin: 0 -10rpx;
	
	&__item {
		width: calc(50% - 20rpx);
		margin: 10rpx;
		height: 80rpx;
		@include flex-center;
		background-color: $bg-color;
		border-radius: 12rpx;
		color: $text-secondary;
		font-size: 28rpx;
		transition: all 0.3s;
		
		&--active {
			background-color: rgba($primary-color, 0.1);
			color: $primary-color;
		}
		
		&:active {
			transform: scale(0.98);
		}
	}
}

.upload-area {
	margin-top: 10rpx;
}

.btn {
	height: 88rpx;
	line-height: 88rpx;
	text-align: center;
	font-size: 30rpx;
	color: $card-bg;
	border-radius: 44rpx;
	width: 100%;
	transition: all 0.3s;
	
	&--primary {
		background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
		box-shadow: 0 6rpx 20rpx rgba($primary-color, 0.3);
		
		&:active {
			transform: scale(0.98);
			box-shadow: 0 4rpx 10rpx rgba($primary-color, 0.3);
		}
	}
}
</style>