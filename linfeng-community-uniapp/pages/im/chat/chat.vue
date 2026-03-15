<template>
	<view>
		<view>
			<MessageBoxComp ref="messageBoxRef"></MessageBoxComp>
		</view>

		<view>
			<InputBoxComp @sendMessage="handleMessage" @bottomHeight="handleBottomHeight"
				@keyboradHeight="handleKeyboradHeight" ref="inputBoxRef"></InputBoxComp>
		</view>

	</view>
</template>

<script setup>
import { ref, computed, watch, getCurrentInstance } from 'vue'
import { onLoad, onShow, onUnload, onPageScroll, onNavigationBarButtonTap } from '@dcloudio/uni-app'
import { useStore } from 'vuex'
import InputBoxComp from '@/components/inputBox/inputBox.vue'
import MessageBoxComp from '@/components/messageBox/messageBox.vue'
import websocket from '@/utils/websocket.js'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const store = useStore()

// 响应式数据
const emoji = ref('')
const index = ref(0)
const messageBoxRef = ref(null)
const inputBoxRef = ref(null)

// 计算属性
const loginUserInfo = computed(() => store.getters.loginUserInfo)
const chattingUserInfo = computed(() => store.getters.chattingUserInfo)
const personMessage = computed(() => store.getters.personMessage)
const onlineArray = computed(() => store.getters.onlineArray)

// 监听
watch(onlineArray, () => {
	showOnline()
})

// 生命周期
onUnload(() => {
	store.state.chattingUserInfo = null
})

onNavigationBarButtonTap((e) => {
	if (e.index === 0) {
		uni.navigateTo({
			url: '/pages/user/home?uid=' + store.state.chattingUserInfo.chattingUserId
		})
	}
})

onPageScroll((h) => {
	if (h.scrollTop == 0) {
		messageBoxRef.value.getHistoryMsg()
	}
})

onLoad(() => {
	if (chattingUserInfo.value == null) {
		uni.switchTab({
			url: '/pages/index/index'
		})
		return
	}
	
	//清空消息未读
	clearUnread()
	showOnline()
})

onShow(() => {
	//处理偶尔取不到值得问题
	if(!loginUserInfo.value.uid){
		getUserInfo()
	}
})

// 方法
const showOnline = () => {
	const targetId = chattingUserInfo.value?.chattingUserId
	// 后端在线列表既可能是数字也可能是字符串/对象，统一转成字符串比对
	const isOnline = onlineArray.value.some(item => {
		const uid = typeof item === 'object' ? (item?.uid || item?.id || item?.userId) : item
		return String(uid) === String(targetId)
	})
	const tip = isOnline ? '在线' : '离线'
	uni.setNavigationBarTitle({
		title: chattingUserInfo.value.name + '(' + tip + ')'
	})
}

const clearUnread = () => {
	$H.post('chat/clearUnread', {
		myId: loginUserInfo.value.uid,
		friendId: chattingUserInfo.value.chattingUserId
	}).then(res => {
		if (res.code == 0) {
			for (let i = 0; i < store.state.sessionList.length; i++) {
				if (store.state.sessionList[i].sessionId == chattingUserInfo.value.sessionId) {
					store.state.sessionList[i].unread = 0
					break
				}
			}
			store.dispatch('countUnreadMessage')
		}
	})
}

const handleMessage = (message) => {
	let m = {
		senderId: loginUserInfo.value.uid,
		receiverId: chattingUserInfo.value.chattingUserId,
		sendTime: message.time,
		content: message.content,
		messageType: message.type,
		sessionId: chattingUserInfo.value.sessionId
	}
	let msg = {
		type: 'person-message',
		data: m
	}
	uni.sendSocketMessage({
		data: JSON.stringify(msg),
		fail() {
			uni.closeSocket()
			websocket.initConnect()
			setTimeout(function() {
				retrySend(message)
			}, 1500)
		},
		success() {
			
		}
	})
}

//尝试重新发送
const retrySend = (message) => {
	let m = {
		senderId: loginUserInfo.value.uid,
		receiverId: chattingUserInfo.value.chattingUserId,
		sendTime: message.time,
		content: message.content,
		messageType: message.type,
		sessionId: chattingUserInfo.value.sessionId
	}
	let msg = {
		type: 'person-message',
		data: m
	}
	uni.sendSocketMessage({
		data: JSON.stringify(msg),
		fail() {
			uni.showToast({
				title: '发送失败,请重新发送',
				icon: 'none'
			})
		},
		success() {}
	})
}

const handleBottomHeight = (height) => {
	messageBoxRef.value.setBottomHeigth(height)
}

const handleKeyboradHeight = (height) => {
	messageBoxRef.value.setKeyboardHeight(height)
}

const getUserInfo = () => {
	$H.get("user/userInfo").then(res => {
		store.state.loginUserInfo = res.result
		uni.setStorageSync("userInfo", res.result)
	})
}
</script>

<style lang="scss">
	
</style>
