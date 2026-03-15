//微信公众号工具类封装 2024/02/18
import config from '@/utils/config.js';
import request from '@/utils/request.js';
import websocket from '@/utils/websocket.js';
import $store from '@/store/index.js';

export default {
	checkAndSaveWxMpOpenid(code, url) {
		let ua = navigator.userAgent.toLowerCase();
		if (ua.match(/MicroMessenger/i) != "micromessenger") {
			return
		}

		if (code != null && code != undefined) {
			request.post("user/bindMpOpenid", {
				code: code
			})
			return
		}
		let currentuUrl = window.location.href
		let wxMpCode = currentuUrl.match(/=(\S*)&state=1#/)
		if (wxMpCode !== null && wxMpCode != undefined) {
			const replaceUrl = 'https://' + window.location.host + '/#' + url + '?code=' + wxMpCode[1] + '&state=1'
			return location.replace(replaceUrl)
		} else {
			request.get("user/hasMpOpenid").then(res => {
				if (res.result) {
					return
				}
				let local = encodeURIComponent("https://" + window.location.host + "/#" + url);
				//访问设置的回调地址 redirect_uri，会重定向显示带code参数的地址
				window.location.href = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + config
					.wxMpAppId +
					"&redirect_uri=" + local +
					"&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
			})
		}
	},
	// 检查code是否注册 注册过自动登录
	checkRegisterAndLogin(code, url) {
		let ua = navigator.userAgent.toLowerCase();
		if (ua.match(/MicroMessenger/i) != "micromessenger") {
			return
		}
		if (code != null && code != undefined) {
			request.post("user/loginByWxMp", {
				code: code
			}).then(res => {
				if (res.code == 0) {
					uni.showToast({
						title: "公众号自动登录",
						icon:'none'
					})
					//code已注册用户 走登录操作
					uni.setStorageSync("hasLogin", true);
					uni.setStorageSync("token", res.token);
					$store.state.token = res.token;
					//初始化用户信息
					request.get("user/userInfo").then(res2 => {
						$store.state.loginUserInfo = res2.result;
						uni.setStorageSync("userInfo", res2.result)
						//连接websocket
						websocket.initConnect();
						//获取好友列表
						$store.dispatch('getFriendList');
						///获取通知消息
						$store.dispatch('getNoticeList');
						uni.switchTab({
							url: '/pages/my/my'
						});
						return
					});
				} else if (res.code == 501) {
					//没有绑定过openid的用户
					uni.showToast({
						title: "请使用手机号登录",
						icon:'none',
						duration: 2000
					})
					return
				} else {
					//接口异常
					return
				}
			})
			return
		}
		let currentuUrl = window.location.href
		let wxMpCode = currentuUrl.match(/=(\S*)&state=1#/)
		if (wxMpCode !== null && wxMpCode != undefined) {
			const replaceUrl = 'https://' + window.location.host + '/#' + url + '?code=' + wxMpCode[1] + '&state=1'
			return location.replace(replaceUrl)
		} else {
			let local = encodeURIComponent("https://" + window.location.host + "/#" + url);
			//访问设置的回调地址 redirect_uri，会重定向显示带code参数的地址
			window.location.href = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + config.wxMpAppId +
				"&redirect_uri=" + local +
				"&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
		}
	}

}