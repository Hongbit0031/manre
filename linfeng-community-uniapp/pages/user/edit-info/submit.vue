<template>
	<view class="container">
		<view v-if="type == 'tagStr'">
			<view class="f-wrap">
				<view class="title">已选择标签</view>
				<view @click="onTagDel(index)" v-for="(item,index) in value" :key="index" class="tag">{{item}}</view>
			</view>
			<view class="f-wrap">
				<view class="title">自定义标签</view>
				<view class="tag-add" @click="showAddTag = true">
					<u-icon color="#999" size="50" name="plus"></u-icon>
				</view>
				<!-- 添加弹窗 -->
				<u-popup v-model="showAddTag" mode="center" border-radius="20" width="80%">
					<view class="popup-wrap">
						<view class="title">自定义标签</view>
						<input v-model="addValue" class="tag-input" type="text" placeholder="最长4个字" />
					</view>
					<view class="btn-wrap">
						<view @click="onCancel" class="btn-cancel">取消</view>
						<view @click="onConfirm" class="btn-confirm">保存</view>
					</view>
				</u-popup>
			</view>
			<view class="f-wrap">
				<view class="title">标签</view>
				<view @click="onTagAdd(item)" v-for="(item,index) in tagList" :key="index" class="tag">{{item}}</view>
			</view>
		</view>
		<view v-else>
			<input v-model="value" class="input" type="text" />
		</view>
		<view style="margin-top: 50rpx;">
			<lf-button @click="submit">保存</lf-button>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

// 获取当前实例
const { proxy } = getCurrentInstance()

// 响应式数据
const showAddTag = ref(false)
const type = ref("")
const value = ref("")
const tagList = ref(["发帖达人", "旅行", "生活", "干饭人", "摄影", "读书", "运动", "码农", "新人"])
const addValue = ref("")

// 方法
const onCancel = () => {
	showAddTag.value = false
	addValue.value = ""
}

const onConfirm = () => {
	if (!addValue.value) {
		proxy.$u.toast('自定义标签不能为空')
		return
				}

	if (addValue.value.length > 4) {
		proxy.$u.toast('不能超过4个字')
		return
				}

	let str = value.value.toString()
	if (str.includes(addValue.value)) {
		proxy.$u.toast('已存在相同标签')
		return
	}

	value.value.push(addValue.value)
	showAddTag.value = false
	addValue.value = ""
}

const submit = () => {
				// uni.showLoading({
				// 	mask: true,
				// 	title: "正在保存"
				// })
				let obj = {}
	let typeValue = type.value
	obj[typeValue] = value.value
	if(typeValue=='email'){
					var emailRegExp=/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
		if(!emailRegExp.test(value.value)){
						uni.hideLoading();
			proxy.$u.toast('请输入规范的邮箱');
						return;
					}
					
				}
	proxy.$H.post("user/userInfoEdit", obj).then(res => {
					if (res.code == 0) {
						uni.navigateBack();
					}
					// uni.hideLoading();
				})
}

const onTagDel = (index) => {
	value.value.splice(index, 1)
}

const onTagAdd = (name) => {
	let str = value.value.toString()
				if (str.includes(name)) {
		proxy.$u.toast('已存在相同标签')
		return
				}
				
	value.value.push(name)
			}

// 页面加载时的处理
onLoad((options) => {
	type.value = options.type
	if (options.value) {
		value.value = JSON.parse(options.value)
		}
})
</script>

<style>
	page {
		background-color: #F5F5F5;
	}

	.popup-wrap {
		padding: 50rpx 30rpx;
	}

	.popup-wrap .tag-input {
		background-color: #F5F5F5;
		margin-bottom: 30rpx;
		padding: 20rpx;
	}

	.popup-wrap>.title {
		text-align: center;
		font-weight: bold;
		margin-bottom: 20rpx;
	}

	.btn-wrap {
		display: flex;
	}

	.btn-wrap .btn-cancel {
		width: 50%;
		padding: 20rpx 0;
		text-align: center;
		background-color: #F5F5F5;
	}

	.btn-wrap .btn-confirm {
		background-color: #8687fd;
		width: 50%;
		padding: 20rpx 0;
		text-align: center;
		color: #FFFFFF;
	}

	.input {
		border-bottom: 1px solid #e2e2e2;
		padding: 10rpx;
	}

	.tag-add {
		padding: 20rpx;
		background-color: #F5F5F5;
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	/* 标签 */

	.tag {
		padding: 10rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		display: inline-block;
		margin-right: 20rpx;
		margin-bottom: 20rpx;
		background-color: #ffebe5;
	}

	.tag:nth-child(2n) {
		background-color: #ecf9f2;
	}

	.tag:nth-child(3n) {
		background-color: #fff7e5;
	}

	.tag:nth-child(5n) {
		background-color: #b3e0ff;
	}
</style>
