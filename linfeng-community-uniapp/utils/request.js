import config from './config.js'

/**
 * 通用请求封装
 */
const request = (options = {}) => {
	return new Promise((resolve, reject) => {
		let url = options.url
		if (url.indexOf("http://") === -1 && url.indexOf("https://") === -1) {
			options.url = config.domain + url
		}
		options.header.token = uni.getStorageSync("token")
		options.complete = (response) => {
			if (response.statusCode === 200 || response.statusCode === 0) {
				const { code, msg } = response.data
				
				if (code === 401 || code === 420) {
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
				} else if (code === 500) {
					uni.showToast({
						title: msg,
						icon: "none",
						duration: 2000
					})
				} else if (code === 701) {
					// 封号
					uni.showToast({
						title: msg,
						icon: "none",
						duration: 2000
					})
					uni.removeStorageSync("hasLogin")
					uni.removeStorageSync("token")
					uni.removeStorageSync("userInfo")
					uni.closeSocket()
					uni.switchTab({
						url: "/pages/index/index"
					})
				}
				resolve(response.data)
			} else {
				uni.showToast({
					title: '请求异常',
					icon: "none"
				})
				reject(response)
			}
		}

		uni.request(options)
	})
}

/**
 * POST 请求
 */
export const post = (url, data = {}, header = {}) => {
	const options = {
		url,
		data,
		header,
		method: "POST"
	}
	return request(options)
}

/**
 * GET 请求
 */
export const get = (url, data = {}, header = {}) => {
	const options = {
		url,
		data,
		header
	}
	return request(options)
}

// 默认导出（保持向后兼容）
export default {
	request,
	post,
	get
}