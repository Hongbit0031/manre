<template>
	<view class="setting-page">
		<!-- 第一组：账号与安全、隐私设置、存储空间 -->
		<view class="card list-card">
			<view class="list-item" @click="goAccountSecurity">
				<view class="item-left">
					<u-icon name="account" size="36" color="#333333"></u-icon>
					<text class="item-text">账号维护</text>
				</view>
				<u-icon class="item-arrow" name="arrow-right" size="28" color="#cccccc"></u-icon>
			</view>
			<view class="list-item" @click="goPrivacySetting">
				<view class="item-left">
					<u-icon name="lock" size="36" color="#333333"></u-icon>
					<text class="item-text">隐私设置</text>
				</view>
				<u-icon class="item-arrow" name="arrow-right" size="28" color="#cccccc"></u-icon>
			</view>
			<view class="list-item" @click="goStorage">
				<view class="item-left">
					<u-icon name="trash" size="36" color="#333333"></u-icon>
					<text class="item-text">存储空间</text>
				</view>
				<view class="item-right">
					<text class="item-extra">{{ storageSize }}</text>
					<u-icon class="item-arrow" name="arrow-right" size="28" color="#cccccc"></u-icon>
				</view>
			</view>
		</view>

		<!-- 第二组：帮助与客服、关于我们 -->
		<view class="card list-card">
			<view class="list-item" @click="goHelp">
				<view class="item-left">
					<u-icon name="chat" size="36" color="#070707"></u-icon>
					<text class="item-text">帮助与客服</text>
				</view>
				<u-icon class="item-arrow" name="arrow-right" size="28" color="#cccccc"></u-icon>
			</view>
			<view class="list-item" @click="goAbout">
				<view class="item-left">
					<u-icon name="info-circle" size="36" color="#333333"></u-icon>
					<text class="item-text">关于{{ appName }}</text>
				</view>
				<u-icon class="item-arrow" name="arrow-right" size="28" color="#cccccc"></u-icon>
			</view>
		</view>

		<!-- 第三组：账号注销 -->
		<view class="card list-card">
			<view class="list-item delete-item" @click="goAccountDelete">
				<view class="item-left">
					<u-icon name="man-delete" size="36" color="#333333"></u-icon>
					<text class="item-text delete-text">账号注销</text>
				</view>
				<u-icon class="item-arrow" name="arrow-right" size="28" color="#cccccc"></u-icon>
			</view>
		</view>

		<!-- 第四组：退出登录 -->
		<view class="card list-card">
			<view class="list-item logout-item" @click="handleLogout">
				<view class="item-left">
					<u-icon name="close-circle" size="36" color="#333333"></u-icon>
					<text class="item-text logout-text">退出登录</text>
				</view>
				<u-icon class="item-arrow" name="arrow-right" size="28" color="#cccccc"></u-icon>
			</view>
		</view>
	</view>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance,
		onMounted
	} from 'vue'
	import {
		onLoad
	} from '@dcloudio/uni-app'
	import $store from '@/store/index.js'
	import websocket from '@/utils/websocket.js'

	const {
		proxy
	} = getCurrentInstance()
	const $H = proxy?.$H
	const $c = proxy.$c

	// 应用名称
	const appName = $c?.miniappName || '应用'

	// 存储空间大小
	const storageSize = ref('计算中...')

	// 获取存储空间（适配三端）
	const getStorageSize = () => {
		// #ifdef APP-PLUS
		try {
			plus.cache.calculate((size) => {
				// 转换为MB或GB
				if (size < 1024 * 1024) {
					storageSize.value = (size / 1024).toFixed(2) + ' KB'
				} else if (size < 1024 * 1024 * 1024) {
					storageSize.value = (size / (1024 * 1024)).toFixed(2) + ' MB'
				} else {
					storageSize.value = (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
				}
			})
		} catch (e) {
			storageSize.value = '0 MB'
		}
		// #endif

		// #ifdef H5
		try {
			// H5端：使用localStorage估算
			let totalSize = 0
			for (let key in localStorage) {
				if (localStorage.hasOwnProperty(key)) {
					totalSize += localStorage[key].length + key.length
				}
			}
			// 转换为KB或MB
			if (totalSize < 1024) {
				storageSize.value = totalSize + ' B'
			} else if (totalSize < 1024 * 1024) {
				storageSize.value = (totalSize / 1024).toFixed(2) + ' KB'
			} else {
				storageSize.value = (totalSize / (1024 * 1024)).toFixed(2) + ' MB'
			}
		} catch (e) {
			storageSize.value = '0 MB'
		}
		// #endif

		// #ifdef MP
		// 微信小程序端：使用uni.getStorageInfoSync获取
		try {
			const info = uni.getStorageInfoSync()
			// 小程序存储限制是10MB，这里根据keys数量粗略估算
			const estimatedSize = info.keys.length * 2 // 每个key估算2KB
			if (estimatedSize < 1024) {
				storageSize.value = estimatedSize + ' KB'
			} else {
				storageSize.value = (estimatedSize / 1024).toFixed(2) + ' MB'
			}
			// 显示已用/总容量
			storageSize.value = (estimatedSize / 1024).toFixed(2) + ' MB / 10 MB'
		} catch (e) {
			storageSize.value = '0 MB'
		}
		// #endif
	}

	// 跳转方法
	const goAccountSecurity = () => {
		uni.navigateTo({
			url: '/pages/user/edit-info/edit'
		})
	}

	const goPrivacySetting = () => {
		uni.navigateTo({
			url: '/pages/user/edit-info/setting'
		})
	}

	const goStorage = () => {
		// 可以在这里实现清理缓存功能
		uni.showModal({
			title: '存储空间',
			content: `当前缓存：${storageSize.value}\n清理缓存会导致退出登录，是否继续清理？`,
			success: (res) => {
				if (res.confirm) {
					clearStorage()
				}
			}
		})
	}

	// 清理缓存
	const clearStorage = () => {
		uni.showLoading({
			title: '清理中...'
		})

		// #ifdef APP-PLUS
		try {
			plus.cache.clear(() => {
				uni.hideLoading()
				uni.showToast({
					title: '清理成功',
					icon: 'success'
				})
				storageSize.value = '0 MB'
			})
		} catch (e) {
			uni.hideLoading()
			uni.showToast({
				title: '清理失败',
				icon: 'none'
			})
		}
		// #endif

		// #ifdef H5
		try {
			localStorage.clear()
			uni.hideLoading()
			uni.showToast({
				title: '清理成功',
				icon: 'success'
			})
			storageSize.value = '0 MB'
		} catch (e) {
			uni.hideLoading()
			uni.showToast({
				title: '清理失败',
				icon: 'none'
			})
		}
		// #endif

		// #ifdef MP
		// 微信小程序端：清理所有本地存储
		try {
			const info = uni.getStorageInfoSync()
			info.keys.forEach(key => {
				uni.removeStorageSync(key)
			})
			uni.hideLoading()
			uni.showToast({
				title: '清理成功',
				icon: 'success'
			})
			// 重新获取存储大小
			setTimeout(() => {
				getStorageSize()
			}, 500)
		} catch (e) {
			uni.hideLoading()
			uni.showToast({
				title: '清理失败',
				icon: 'none'
			})
		}
		// #endif
	}

	const goHelp = () => {
		uni.navigateTo({
			url: '/pages/user/contact'
		})
	}

	const goAbout = () => {
		uni.navigateTo({
			url: '/subpages/content/about/about'
		})
	}

	// 账号注销
	const goAccountDelete = () => {
		uni.navigateTo({
			url: '/subpages/content/account-delete/account-delete'
		})
	}

	// 退出登录
	const handleLogout = () => {
		uni.showModal({
			title: '提示',
			content: '确定要退出登录吗？',
			success: (res) => {
				if (res.confirm) {
					logout()
				}
			}
		})
	}

	const logout = () => {
		// 清空store状态
		$store.state.totalUnread.message = 0
		$store.state.totalUnread.notice = 0
		$store.state.friendList = []
		$store.state.friendListShow = []
		$store.state.sessionList = []

		// 清除本地存储
		uni.removeStorageSync("hasLogin")
		uni.removeStorageSync("token")
		uni.removeStorageSync("userInfo")
		uni.removeStorageSync("activeStyle")

		// 关闭WebSocket连接
		uni.closeSocket()
		websocket.cleanup()

		// 跳转到首页
		uni.switchTab({
			url: "/pages/index/index"
		})
	}

	onMounted(() => {
		getStorageSize()
	})

	onLoad((options) => {
		if (!uni.getStorageSync('hasLogin')) {
			proxy.$u.toast("请先登录");
			// #ifdef MP-WEIXIN
			uni.navigateTo({
				url: "/pages/user/login"
			})
			// #endif
			// #ifndef MP-WEIXIN
			uni.navigateTo({
				url: "/pages/user/go-login"
			})
			// #endif
			return;
		}
	});
</script>

<style lang="scss" scoped>
	.setting-page {
		min-height: 100vh;
		background-color: #f7f7f7;
		padding: 20rpx 24rpx 80rpx;
		box-sizing: border-box;
	}

	.card {
		background-color: #ffffff;
		border-radius: 24rpx;
		padding: 0;
		box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.02);
		overflow: hidden;
	}

	.list-card {
		margin-bottom: 20rpx;

		.list-item {
			min-height: 120rpx;
			padding: 0 30rpx;
			display: flex;
			align-items: center;
			justify-content: space-between;
			border-bottom-width: 1rpx;
			border-bottom-style: solid;
			border-bottom-color: #f0f0f0;

			&:last-child {
				border-bottom-width: 0;
			}

			.item-left {
				display: flex;
				align-items: center;
				flex: 1;

				.item-text {
					margin-left: 20rpx;
					font-size: 30rpx;
					color: #333333;
				}
			}

			.item-right {
				display: flex;
				align-items: center;
				margin-left: 20rpx;

				.item-extra {
					font-size: 26rpx;
					color: #999999;
					margin-right: 12rpx;
				}
			}

			.item-arrow {
				font-size: 28rpx;
				color: #cccccc;
			}
		}

		.logout-item {
			.logout-text {
				color: #333333;
			}
		}

		.delete-item {
			.delete-text {
				color: #333333;
			}
		}
	}
</style>