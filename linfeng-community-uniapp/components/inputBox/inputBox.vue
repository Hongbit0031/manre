<template>
	<view :class="['container', isIos ? 'ios-safe' : '']">
		<view class="in">
			<textarea maxlength="600" @keyup.enter="sendText()" auto-height="true" v-model="text" confirm-type="send"
				@focus="showKeyBoard()" class="input-area"></textarea>
			<view @tap="sendText()" :class="[isDisable?'send-btn-disable':'send-btn-able','send-btn']">发送</view>
		</view>

		<view class="icon-container">
			<!-- #ifndef MP-WEIXIN -->
			<image class="input-icon" @tap="openDrawer(1)" :src="$IMG+'/im/emoji.png'"></image>
			<!-- #endif -->
			<image class="input-icon" @tap="selectImage()" :src="$IMG+'/im/image.png'"></image>
			<!-- #ifdef MP-WEIXIN || H5 -->
			<image class="input-icon" @tap="selectVideo()" :src="$IMG+'/im/video.png'"></image>
			<!-- #endif -->
			<!-- #ifdef MP-WEIXIN -->
			<lsj-upload style="width: 55rpx;height: 55rpx;hebackground-color: red;margin-left: 0rpx;" class="input-icon"
				ref="lsjUploadRef" childId="upload1" :count="1" :size="maxFileMB" :option="fileOption"
				@change="fileOnChange">
				<image class="input-icon" :src="$IMG+'/im/file.png'"></image>
			</lsj-upload>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<image class="input-icon" @tap="selectFile()" :src="$IMG+'/im/file.png'"></image>
			<!-- #endif -->
		</view>
		<view class="emoji-container" v-if="showDrawer===1">
			<!-- <image class="emoji" v-for="(e,index) in emojiList" :key="index" :src="'/static/emoji/big/'+e.url"
				@click="text += e.alt"></image> -->
				<image class="emoji" v-for="(e,index) in emojiList" :key="index" :src="emojiUrl+e.url"
				@click="text += e.alt"></image>
			<view style="height: 100rpx;"></view>
			<view style="position: fixed;z-index: 100;right: 20rpx;bottom: 30rpx;display: flex">
				<view class="btn">
					<view @tap="deleteAEmoji()">X</view>
				</view>
				<view class="btn">
					<view @tap="showDrawer = 0">关闭</view>
				</view>
			</view>
		</view>

		<view class="image-container" v-if="selectedImage!==null">
			<image @tap="selectImage()" mode="aspectFill"
				style="width: 80%;height: 70%;overflow: hidden;margin-top: 1%;border-radius: 2%;" :src="selectedImage">
			</image>
			<view style="display: flex;position: fixed;width: 90%;margin-left: 4%;bottom: 50rpx;">
				<u-button @click="imageCancel()">取消</u-button>
				<u-button style="margin-left: 2%;" @click="sendImage()" class="send-btn-able"
					type="primary">发送</u-button>
			</view>
		</view>
		<view class="video-container" v-if="selectedVideo!==null">
			<video style="width: 80%;height: 70%;overflow: hidden;margin-top: 1%;border-radius: 2%;"
				:src="selectedVideo"></video>
			<view style="display: flex;position: fixed;width: 90%;margin-left: 4%;bottom: 50rpx;">
				<u-button @click="videoCancel()">取消</u-button>
				<u-button style="margin-left: 2%;" @click="sendVideo()" class="send-btn-able"
					type="primary">发送</u-button>
			</view>
		</view>
		<view class="file-container" v-if="selectedFile!==null">
			<view>
				<image style="width: 100rpx;height: 100rpx;margin-top: 60rpx;" :src="$IMG+'/im/clip.png'"></image>
				<view style="display: block;">
					<view>{{selectedFile===null?'':selectedFile.name}}</view>
					<view>{{selectedFile===null?'':(selectedFile.size/1048576).toFixed(2)+'MB'}}</view>
				</view>
			</view>
			<view style="display: flex;position: fixed;width: 90%;margin-left: 4%;bottom: 50rpx;">
				<u-button @click="fileCancel()">取消</u-button>
				<u-button style="margin-left: 2%;" @click="sendFile()" class="send-btn-able"
					type="primary">发送</u-button>
			</view>

		</view>
		<view :style="{'height':keyboradHeight+'px'}"></view>
	</view>
</template>

<script setup>
	import {
		ref,
		watch,
		getCurrentInstance,
		onMounted,
		nextTick
	} from 'vue'
	import emojiList from '@/static/emoji/emojiList.js'
	import timeUtil from '@/utils/timeUtil.js'
	const {
		proxy
	} = getCurrentInstance()
	const $IMG = proxy.$IMG
	const $c = proxy.$c

// 平台检测
const isIos = ref(false)


	const props = defineProps({
		maxImageMB: {
			type: Number,
			default: 5
		},
		maxVideoMB: {
			type: Number,
			default: 10
		},
		maxFileMB: {
			type: Number,
			default: 10
		},
		fileExtensions: {
			type: Array,
			default: () => ['.zip', '.doc', '.txt', '.pdf', '.doc', '.docx', '.ppt', '.exe', '.html', '.js',
				'.css', '.rar', '.xls', '.png', '.jpg', '.mp3', '.mp4', '.wav', '.gif'
			]
		},
	})

	const emit = defineEmits(['bottomHeight', 'keyboradHeight', 'sendMessage'])

	// 响应式数据
	const isDisable = ref(true)
	const text = ref('')
	const showDrawer = ref(0)
	const emojiListData = ref(emojiList)
	const status = ref(false)
	const recorder = ref(null)
	const selectedImage = ref(null)
	const selectedVideo = ref(null)
	const selectedFile = ref(null)
	const audioTime = ref(0)
	const fileOption = ref({
		url: 'null'
	})
	const keyboradHeight = ref(0)
	const height = ref(0)
	const lsjUploadRef = ref(null)
	const emojiUrl = ref('')
	// 监听
	watch(text, (newData, oldData) => {
		if (newData !== '') {
			isDisable.value = false
			if (newData[newData.length - 1] === '\n') {
				text.value = text.value.replace('\n', ' ')
			}
		} else isDisable.value = true
	})

	watch(showDrawer, (newData, oldData) => {
		if (oldData === 5) {
			if (status.value) {
				// recorder.value.stop()
				status.value = !status.value
				recorder.value = null
			}
		}
		if (newData !== 2) imageCancel()
		if (newData !== 3) videoCancel()
		if (newData !== 4) fileCancel()
		switch (newData) {
			case 0:
				height.value = 0;
				break;
			case 1:
				height.value = 600;
				break;
			case 2:
			case 3:
			case 4:
				height.value = 500;
				break;
			case 5:
				height.value = 450;
				break;
		}
		emit('bottomHeight', height.value + 150)
	})

	onMounted(() => {
		// 检测是否iOS设备（用于小程序iOS样式微调）
		try {
			const sys = uni.getSystemInfoSync()
			const platform = (sys.platform || '').toLowerCase()
			const system = sys.system || ''
			isIos.value = platform === 'ios' || /iphone|ipad|ipod|ios/i.test(system)
		} catch (e) {
			isIos.value = false
		}
		emojiUrl.value = $c.emojiUrl
		// #ifdef APP-PLUS
		//  监听键盘高度变化	
		uni.onKeyboardHeightChange(res => {
			if (res.height === 0) {
				keyboradHeight.value = 0
				emit('keyboradHeight', 20)
			} else {
				emit('keyboradHeight', 90)
				if (showDrawer.value === 0) {
					keyboradHeight.value = 35
				} else {
					showDrawer.value = 0
					keyboradHeight.value = res.height
				}
			}
		})
		// #endif
	})

	// 方法
	const showKeyBoard = () => {
		showDrawer.value = 0
	}

	//发送文件消息
	const sendFile = () => {
		// #ifdef APP-PLUS
		uni.showToast({
			icon: 'none',
			title: '手机端暂时不行'
		})
		return
		// #endif

		uni.showLoading({
			title: '正在上传文件'
		})
		uni.uploadFile({
			url: $c.domain + 'im/file/upload', //文件上传接口
			filePath: selectedFile.value.url,
			name: 'file',
			fail(res) {
				console.log(res)
				uni.hideLoading()
				uni.showToast({
					icon: 'error',
					title: '文件上传失败'
				})
			},
			success: (res) => {
				uni.hideLoading()
				let response = JSON.parse(res.data)
				if (response.code != 0) {
					uni.showToast({
						icon: 'error',
						title: '文件上传失败'
					})
				} else {
					let fileString = {
						name: response.data.name,
						size: response.data.size,
						url: response.data.download
					}

					let message = {
						type: 'file',
						time: timeUtil.getFormatTime(new Date()),
						content: JSON.stringify(fileString)
					}
					emit('sendMessage', message)
					fileCancel()
				}
			}
		})
	}

	//发送视频消息
	const sendVideo = () => {
		uni.showLoading({
			title: '正在上传视频'
		})
		uni.uploadFile({
			url: $c.domain + 'im/video/upload', //图片上传接口
			filePath: selectedVideo.value,
			name: 'video',
			fail(res) {
				console.log(res)
				uni.hideLoading()
				uni.showToast({
					icon: 'error',
					title: '视频上传失败'
				})
			},
			success: (res) => {
				uni.hideLoading()
				let response = JSON.parse(res.data)
				if (response.code != 0) {
					uni.showToast({
						icon: 'error',
						title: '视频上传失败'
					})
				} else {
					let message = {
						type: 'video',
						time: timeUtil.getFormatTime(new Date()),
						content: $c.imfile + response.data.url
					}
					emit('sendMessage', message)
					videoCancel()
				}
			}
		})
	}

	//发送图片消息
	const sendImage = () => {
		uni.showLoading({
			title: '正在上传图片'
		})
		uni.uploadFile({
			url: $c.domain + 'im/image/upload', //图片上传接口
			filePath: selectedImage.value,
			name: 'image',
			fail(res) {
				console.log(res)
				uni.hideLoading()
				uni.showToast({
					icon: 'error',
					title: '图片上传失败'
				})
			},
			success: (res) => {
				uni.hideLoading()
				let response = JSON.parse(res.data)
				if (response.code != 0) {
					uni.showToast({
						icon: 'error',
						title: '图片上传失败'
					})
				} else {
					let imageString = {
						originUrl: $c.imfile + response.data.url,
						compressUrl: $c.imfile + response.data.compressUrl
					}
					let message = {
						type: 'image',
						time: timeUtil.getFormatTime(new Date()),
						content: JSON.stringify(imageString)
					}
					emit('sendMessage', message)
					imageCancel()
				}
			}
		})
	}

	//发送文字
	const sendText = () => {
		if (isDisable.value) return
		if (text.value.replace(/ /g, '') === '') {
			uni.showToast({
				icon: 'none',
				title: '请输入有效内容'
			})
			text.value = ''
			return
		}
		//不要超过255就行，数据库varchar（255）
		if (text.value.length > 200) {
			uni.showToast({
				icon: 'none',
				title: '字数不要大于200'
			})
			return
		}
		let message = {
			type: 'text',
			time: timeUtil.getFormatTime(new Date()),
			content: text.value
		}
		emit('sendMessage', message)
		text.value = ''
	}

	const openDrawer = (index) => {
		if (showDrawer.value === index) showDrawer.value = 0
		else showDrawer.value = index
	}

	const deleteAEmoji = () => {
		if (text.value === '' || text.value.lastIndexOf('[') === '-1') return
		text.value = text.value.slice(0, text.value.lastIndexOf('['))
	}

	const handlerSave = () => {
		let tag = document.createElement('a')
		tag.href = recorder.value.localUrl
		tag.download = '录音'
		tag.click()
	}

	const handlerOnCahnger = () => {
		if (status.value) {
			// recorder停止逻辑
		} else {
			// recorder开始逻辑
			setTimeout(() => {
				handleAudioTime()
			}, 1000)
		}
		status.value = !status.value
	}

	const handleAudioTime = () => {
		if (status.value) {
			audioTime.value++
			setTimeout(() => {
				handleAudioTime()
			}, 1000)
		}
	}

	const imageCancel = () => {
		selectedImage.value = null
		if (showDrawer.value === 2) showDrawer.value = 0
	}

	const videoCancel = () => {
		selectedVideo.value = null
		if (showDrawer.value === 3) showDrawer.value = 0
	}

	const fileCancel = () => {
		selectedFile.value = null
		if (showDrawer.value === 4) showDrawer.value = 0
	}

	const selectImage = () => {
		uni.chooseImage({
			count: 1, //默认9
			sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
			sourceType: ['album', 'camera'], //从相册选择
			success: function(res) {
				if (props.maxImageMB * 1048576 < res.tempFiles[0].size) {
					uni.showToast({
						icon: 'none',
						title: '图片请限制在' + props.maxImageMB + 'MB以内'
					})
				} else {
					showDrawer.value = 2
					nextTick(() => {
						selectedImage.value = res.tempFilePaths[0]
						console.log(selectedImage.value)
					})
				}
			}
		})
	}

	const selectVideo = () => {
		uni.chooseVideo({
			sourceType: ['camera', 'album'],
			sizeType: ['compressed'],
			success: function(res) {
				// #ifdef APP-PLUS || MP
				if (props.maxVideoMB * 1048576 < res.size) {
					uni.showToast({
						icon: 'none',
						title: '视频请限制在' + props.maxVideoMB + 'MB以内'
					})
				}
				// #endif
				// #ifdef H5
				if (props.maxVideoMB * 1048576 < res.tempFile.size) {
					uni.showToast({
						icon: 'none',
						title: '视频请限制在' + props.maxVideoMB + 'MB以内'
					})
				}
				// #endif
				else {
					showDrawer.value = 3
					nextTick(() => {
						selectedVideo.value = res.tempFilePath
					})
				}
			}
		})
	}

	const selectFile = () => {
		// #ifdef H5
		uni.chooseFile({
			count: 1, //默认100
			extension: props.fileExtensions,
			success: function(res) {
				if (props.maxFileMB * 1048576 < res.tempFiles[0].size) {
					uni.showToast({
						icon: 'none',
						title: '文件请限制在' + props.maxFileMB + 'MB以内'
					})
				} else {
					showDrawer.value = 4
					nextTick(() => {
						selectedFile.value = {
							name: res.tempFiles[0].name,
							size: res.tempFiles[0].size,
							url: res.tempFilePaths[0]
						}
					})
				}
			}
		})
		// #endif
	}

	const fileOnChange = (files) => {
		showDrawer.value = 4
		files.forEach((key, val) => {
			selectedFile.value = {
				name: key.name,
				size: key.size,
				url: key.path
			}
		})
	}

	// 暴露给父组件的属性（如果需要）
	defineExpose({
		emojiList: emojiListData
	})
</script>

<style lang="scss" scoped>
	.container {
		position: fixed;
		bottom: 0;
		width: 100%;
		background-color: #efeeee;
	}

	.in {
		position: relative;
		/* #ifdef H5 */
		margin-top: 10rpx;
		/* #endif */
		margin-left: 1%;
	}

	/* #ifdef H5 */
	.input-area {
		margin-left: 1%;
		padding: 10rpx;
		background-color: #FFFFFF;
		border-radius: 20rpx;
		width: 79%;
		max-height: 180rpx;
		overflow-y: scroll;
		overflow-x: hidden;
	}

	/* #endif */
	/* #ifdef APP-PLUS || MP */
	.input-area {
		background-color: #FFFFFF;
		padding: 10rpx;
		max-height: 180rpx;
	}

	/* #endif */

	.send-btn {
		width: 15%;
		height: 60rpx;
		font-weight: 540;
		font-size: 30rpx;
		font-family: Verdana, Geneva, Tahoma, sans-serif;
		color: rgb(255, 254, 255);
		border-radius: 20rpx;
		text-align: center;
		position: absolute;
		right: 1%;
		bottom: 1%;
		line-height: 60rpx;

	}

	/* 微信小程序适配：提高发送按钮底部位置并为容器增加安全区内边距，避免被系统键盘或安全区域遮挡 */
	/* #ifdef MP-WEIXIN */
	.container {
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);
	}
	.send-btn {
		/* 使用垂直居中，保证与 textarea 中心对齐并避免被键盘遮挡 */
		bottom: auto;
		top: 50%;
		transform: translateY(-50%);
	}
	/* iOS 小程序单独微调，避免键盘遮挡导致按钮半隐 */
	.ios-safe {
		padding-bottom: calc(env(safe-area-inset-bottom) + 18rpx);
	}
	.ios-safe .send-btn {
		/* 优先使用 bottom 偏移以保证可见性（兼容 iOS 小程序键盘行为） */
		top: auto;
		transform: none;
		bottom: 20rpx;
	}
	/* #endif */

	.send-btn-disable {
		background-color: #7c7c7c;
	}

	.send-btn-able {
		background-color: #000000;
	}

	.send-btn-able:active {
		background-color: #0071bc;
	}

	.icon-container {
		margin-top: 10rpx;
		margin-bottom: 0rpx;
		padding-left: 20rpx;

		.input-icon {
			width: 55rpx;
			height: 55rpx;
			margin-left: 20rpx;
			margin-right: 30rpx;
		}
	}

	.emoji-container {
		padding-left: 10rpx;
		width: 100%;
		height: 600rpx;
		background-color: #eaeaea;
		overflow: scroll;
		overflow-x: hidden;
		position: relative;

		.btn {
			text-align: center;
			line-height: 80rpx;
			width: 100rpx;
			height: 80rpx;
			border-radius: 20rpx;
			background-color: #ffffff;
			margin-left: 20rpx;
		}

		.btn:active {
			background-color: #cfcfcf;
		}

		.emoji {
			width: 50rpx;
			height: 50rpx;
			margin: 20rpx;
		}
	}

	.recorder-container {
		padding-left: 10rpx;
		width: 100%;
		height: 450rpx;
		background-color: #eaeaea;
		text-align: center;

		.record-btn {
			width: 200rpx;
			height: 200rpx;
			margin-top: 100rpx;
		}

		.btn {
			text-align: center;
			line-height: 80rpx;
			width: 100rpx;
			height: 80rpx;
			border-radius: 20rpx;
			background-color: #ffffff;
			margin-left: 20rpx;
		}

		.btn:active {
			background-color: #cfcfcf;
		}
	}

	.image-container {
		padding-left: 10rpx;
		width: 100%;
		height: 500rpx;
		background-color: #eaeaea;
		text-align: center;
	}

	.video-container {
		padding-left: 10rpx;
		width: 100%;
		height: 500rpx;
		background-color: #eaeaea;
		text-align: center;
	}

	.file-container {
		padding-left: 10rpx;
		width: 100%;
		height: 500rpx;
		background-color: #eaeaea;
		text-align: center;
	}
</style>