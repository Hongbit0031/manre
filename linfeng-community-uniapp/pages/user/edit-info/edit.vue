<template>
	<view>
		<view class="c-wrap">
			<u-form ref="uFormRef" label-width="auto">
				<u-form-item label="头像" input-align="right">
					<u-avatar @click="onAvatar" mode="square" :src="userInfo.avatar" size="100"></u-avatar>
				</u-form-item>
				<u-form-item label="昵称" right-icon="arrow-right">
					<u-input :placeholder="userInfo.username" input-align="right"
						@click="jump(userInfo.username,'username')" />
				</u-form-item>
				<u-form-item label="手机号" right-icon="arrow-right">
					<u-input :placeholder="userInfo.mobile" v-if="userInfo.mobile" input-align="right" disabled />
					<u-input placeholder="未绑定" v-else input-align="right" disabled />
				</u-form-item>
				<u-form-item label="邮箱" right-icon="arrow-right">
					<u-input @click="goEmailCheck" :placeholder="userInfo.email" v-if="userInfo.email"
						input-align="right" />
					<u-input @click="goEmailCheck" placeholder="未绑定" v-else input-align="right" />
				</u-form-item>
				<u-form-item label="性别" right-icon="arrow-right">
					<view @click="showGender = true" class="gender-display">{{ userInfo.gender || '请选择' }}</view>
				</u-form-item>
				<u-form-item label="个性签名" right-icon="arrow-right">
					<u-input @click="jump(userInfo.intro,'intro')" :placeholder="userInfo.intro" input-align="right" />
				</u-form-item>
				<u-form-item label="标签" label-position="top" :border-bottom="false">
					<view @click="jump(userInfo.tagStr,'tagStr')" class="tag-box">
						<view v-for="(item,index) in userInfo.tagStr" :key="index" class="tag">{{item}}</view>
					</view>
				</u-form-item>
				<u-form-item label="背景图" input-align="right">
					<view class="bg-img-container">
						<u-image @click="onBgImg" mode="aspectFill" :src="userInfo.bgImg" width="200" height="100"></u-image>
					</view>
				</u-form-item>
			</u-form>
		</view>
		<view class="f-fixed">
			<lf-button @click="outlogin">退出登录</lf-button>
		</view>
		<!-- 性别选择 -->
		<lf-select v-model="showGender" :list="gender" @confirm="saveGender"></lf-select>
	</view>
</template>

<script setup>
	import {
		ref,
		reactive,
		getCurrentInstance
	} from 'vue'
	import {
		onShow
	} from '@dcloudio/uni-app'
	import $store from '@/store/index.js'
	import websocket from '@/utils/websocket.js'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy.$H
	const $c = proxy.$c

	// 响应式数据
	const uFormRef = ref(null)
	const userInfo = reactive({
		avatar: '',
		username: '',
		mobile: '',
		email: '',
		gender: '',
		intro: '',
		tagStr: [],
		bgImg: ''
	})

	const showGender = ref(false)
	const gender = ref([{
			lable: "男",
			value: 1
		},
		{
			lable: "女",
			value: 2
		},
		{
			lable: "保密",
			value: 0
		}
	])

	// 生命周期
	onShow(() => {
		getUserInfo()
	})

	// 方法
	const saveGender = (index) => {
		let genderValue = gender.value[index[0]].value

		$H.post("user/userInfoEdit", {
			gender: genderValue
		}).then(res => {
			if (res.code == 0) {
				userInfo.gender = gender.value[index[0]].lable
			}
		})
	}

	const getUserInfo = () => {
		$H.get("user/userInfo").then(res => {
			Object.assign(userInfo, res.result)

			if (res.result.gender === 1) {
				userInfo.gender = '男'
			} else if (res.result.gender === 2) {
				userInfo.gender = '女'
			} else {
				userInfo.gender = '保密'
			}

			$store.state.loginUserInfo = userInfo
			uni.setStorageSync("userInfo", userInfo)
		})
	}

	const jump = (value, type) => {
		uni.navigateTo({
			url: "/pages/user/edit-info/submit?value=" + JSON.stringify(value) + "&type=" + type
		})
	}

	const goEmailCheck = () => {
		uni.navigateTo({
			url: "/pages/user/email-login?type=auth"
		})
	}

	const outlogin = () => {
		$store.state.totalUnread.message = 0
		$store.state.totalUnread.notice = 0
		$store.state.friendList = []
		$store.state.friendListShow = []
		$store.state.sessionList = []
		uni.removeStorageSync("hasLogin")
		uni.removeStorageSync("token")
		uni.removeStorageSync("userInfo")
		uni.removeStorageSync("activeStyle")
		uni.closeSocket()
		websocket.cleanup()
		uni.switchTab({
			url: "/pages/index/index"
		})
	}

	const onAvatar = () => {
		uni.chooseImage({
			count: 1,
			sizeType: ['original', 'compressed'],
			sourceType: ['album'],
			success: (res) => {
				uni.showLoading({
					mask: true,
					title: "上传头像中"
				})
				uni.uploadFile({
					url: $c.domain + 'common/upload',
					filePath: res.tempFilePaths[0],
					name: 'Image',
					header: {
						token: uni.getStorageSync("token")
					},
					success: (uploadFileRes) => {
						let data = JSON.parse(uploadFileRes.data)
						if (data.code == 0) {
							updateAvatar(data.result)
							uni.hideLoading()
						}
					}
				})
			}
		})
	}

	const updateAvatar = (avatar) => {
		$H.post("user/userInfoEdit", {
			avatar: avatar
		}).then(res => {
			if (res.code == 0) {
				userInfo.avatar = avatar
				$store.state.loginUserInfo = userInfo
				uni.setStorageSync("userInfo", userInfo)
			}
		})
	}

	const onBgImg = () => {
		uni.chooseImage({
			count: 1,
			sizeType: ['original', 'compressed'],
			sourceType: ['album'],
			success: (res) => {
				uni.showLoading({
					mask: true,
					title: "上传背景图中"
				})
				uni.uploadFile({
					url: $c.domain + 'common/upload',
					filePath: res.tempFilePaths[0],
					name: 'Image',
					header: {
						token: uni.getStorageSync("token")
					},
					success: (uploadFileRes) => {
						let data = JSON.parse(uploadFileRes.data)
						if (data.code == 0) {
							updateBgImg(data.result)
							uni.hideLoading()
						}
					}
				})
			}
		})
	}

	const updateBgImg = (bgImg) => {
		$H.post("user/userInfoEdit", {
			bgImg: bgImg
		}).then(res => {
			if (res.code == 0) {
				userInfo.bgImg = bgImg
				$store.state.loginUserInfo = userInfo
				uni.setStorageSync("userInfo", userInfo)
			}
		})
	}
</script>

<style scoped>
	.c-wrap {
		padding: 20rpx;
		background-color: #FFFFFF;
	}

	.bind-mobile {
		display: flex;
	}

	/* 标签 */
	.tag-box {}

	.tag-box .tag {
		padding: 10rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		display: inline-block;
		margin-right: 20rpx;
		margin-bottom: 20rpx;
		background-color: #ffebe5;
	}

	.tag-box .tag:nth-child(2n) {
		background-color: #ecf9f2;
	}

	.tag-box .tag:nth-child(3n) {
		background-color: #fff7e5;
	}

	.tag-box .tag:nth-child(5n) {
		background-color: #b3e0ff;
	}

	.bg-img-container {
		display: flex;
		justify-content: flex-end;
	}

	.gender-display {
		padding: 20rpx 0;
		color: #C0C4CC;
		font-size: 28rpx;
		min-height: 70rpx;
		line-height: 70rpx;
		width: 100%;
		text-align: right;
		box-sizing: border-box;
	}
</style>