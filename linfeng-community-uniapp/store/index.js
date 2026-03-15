import { createStore } from 'vuex'
import stringUtil from '@/utils/stringUtil.js';
import request from '@/utils/request.js'

const store = createStore({
	state: {
		hasLogin: uni.getStorageSync("hasLogin"),
		sessionKey: uni.getStorageSync("sessionKey"),
		messegeNum: [],

		systemConfig: uni.getStorageSync('system_config') || {
			vibrate: true, //是否开启手机震动
			bell: true, ///是否开启铃声通知
		},
		onlineArray: [],
		isSocketOpen: false, //是否连接websocket
		token: uni.getStorageSync('token') || '',
		loginUserInfo: uni.getStorageSync('userInfo') || {},
		personMessage: [], //好友聊天记录
		lastUpdateSession: {
			id: '',
			count: 0
		},
		editingGroupInfo: null,
		friendList: [],
		friendListShow: [],
		sessionList: [], //会话列表
		groupList: [],
		totalUnread: {
			message: 0,
			notice: 0
		},
		chattingUserInfo: null,
		chattingGroupInfo: null,
		noticeList: [],
		loading: false
	},
	mutations: {
		login(state, userInfo) {
			state.hasLogin = uni.getStorageSync("hasLogin");
			state.sessionKey = uni.getStorageSync("sessionKey");
		},
		logout(state) {
			state.hasLogin = false;
			state.sessionKey = null;
		},
		_setFriendList(state, list) {
			state.friendList = list
		},
		switch_loading(state, tf) {
			if (tf) {
				state.loading = tf;
			} else {
				state.loading = !state.loading
			}
		}
	},
	getters: {
		messegeNum: state => {
			return state.messegeNum
		},
		onlineArray: state => {
			return state.onlineArray
		},
		lastUpdateSession: state => {
			return state.lastUpdateSession
		},
		personMessage: state => {
			return state.personMessage
		},
		noticeList: state => {
			return state.noticeList
		},
		totalUnread: state => {
			return state.totalUnread
		},
		groupList: state => {
			return state.groupList
		},
		sessionList: state => {
			return state.sessionList
		},
		friendListShow: state => {
			return state.friendListShow
		},
		friendList: state => {
			return state.friendList;
		},
		systemConfig: state => {
			return state.systemConfig;
		},
		isSocketOpen: state => {
			return state.isSocketOpen;
		},
		title: state => {
			return state.title
		},
		messageList: state => {
			return state.messageList
		},
		loginUserInfo: state => {
			return state.loginUserInfo
		},
		chattingUserInfo: state => {
			return state.chattingUserInfo
		},
		chattingGroupInfo: state => {
			return state.chattingGroupInfo
		},
	},
	actions: {
		initSessionList({ state, dispatch }) {
			state.totalUnread.message = 0
			state.sessionList = [];
			for (let i = 0; i < state.friendList.length; i++) {
				let session = {
					type: 'person',
					avatar: state.friendList[i].avatar,
					sessionId: state.friendList[i].session_id,
					name: state.friendList[i].notation,
					lastMessage: state.friendList[i].last_message,
					isHidden: state.friendList[i].is_hidden,
					unread: state.friendList[i].unread,
					updateTime: state.friendList[i].update_time,
					chattingUserId: state.friendList[i].friend_id
				}
				state.totalUnread.message += session.unread
				state.sessionList.push(session)
			}
			// console.log('init',state.sessionList)
			dispatch('sortSessionList')
		},
		//统计所有的未读消息
		countUnreadMessage({ state }) {
			state.totalUnread.message = 0
			for (let i = 0; i < state.sessionList.length; i++) {
				state.totalUnread.message += state.sessionList[i].unread;
			}
			// 同时更新tabbar消息数量
			const all = state.totalUnread.message + state.totalUnread.notice;
			state.messegeNum = [0, 0, 0, all, 0];
		},
		///按照时间顺序排序session
		sortSessionList({ state }) {
			for (let i = 0; i < state.sessionList.length; i++) {
				for (let j = i + 1; j < state.sessionList.length; j++) {
					if (state.sessionList[j].updateTime > state.sessionList[i].updateTime) {
						let t = state.sessionList[i]
						state.sessionList[i] = state.sessionList[j]
						state.sessionList[j] = t
					}
				}
			}
			// console.log('按照时间顺序排序session:',state.sessionList)
		},
		//获取通知消息
		getNoticeList({ state }) {
			request.get('notice/list').then(res => {
				if (res.code == 0) {
					state.totalUnread.notice = res.result.count
					state.noticeList = res.result.noticeList
				}
			})
		},
		//设置好友会话消息的session_id
		initFriendSessionId({ state }) {
			for (let i = 0; i < state.friendList.length; i++) {
				let session = {
					sessionId: state.friendList[i].session_id,
					pageNum: 1,
					pageSize: 20,
					hasNextPage: false,
					lastMessageId: 0, ///用来防止加载历史消息时出问题
					list: []
				}
				state.personMessage.push(session)
			}
		},

		getFriendList({ state, dispatch }) {
			request.get('friend/list').then(res => {
				if (res.code == 0) {
					state.friendList = res.result
					dispatch('initSessionList')
					dispatch('initFriendListShow');
					dispatch('initFriendSessionId');
				}
			})
		},
		initFriendListShow({ state, getters }) {
			state.friendListShow = []
			for (let i = 0; i <= 28; i++) {
				let t = []
				state.friendListShow.push(t)
			}
			for (let i = 0; i < state.friendList.length; i++) {
				//console.log(getters.friendList[i])
				let t = {
					id: state.friendList[i].friend_id,
					name: state.friendList[i].notation,
					url: state.friendList[i].avatar
				}
				let num = stringUtil.getFirstLetter(state.friendList[i].notation).charCodeAt(0)
				if (num >= 65 && num <= 90) {
					getters.friendListShow[num - 63].push(t)
				} else {
					state.friendListShow[1].push(t)
				}
			}
		},
		clearUnread(context, param) {
			request.post('chat/clearUnread', param).then(res => {
				if (res.code == 0) {
					// console.log('已清除未读标记')
				}
			})
		},
	}
})

export default store