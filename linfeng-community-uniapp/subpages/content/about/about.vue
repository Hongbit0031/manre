<template>
	<view class="about-page">
		<!-- 顶部应用信息卡片 -->
		<view class="card app-card">
			<view class="app-logo-box">
				<image class="app-logo" :src="logoUrl" mode="aspectFit"></image>
			</view>
			<view class="app-info">
				<text class="app-name">{{ appName }}</text>
				<text class="app-version">v{{ version }}</text>
			</view>
		</view>

		<!-- 功能列表 -->
		<view class="card list-card">
			<view class="list-item" @click="checkUpdate">
				<text class="item-text">检测更新</text>
				<u-icon class="item-arrow" name="arrow-right" size="32" color="#cccccc"></u-icon>
			</view>
		</view>

		<view class="card list-card">
			<view class="list-item" @click="goProtocol('app_standard_context')">
				<text class="item-text">社区规范协议</text>
				<u-icon class="item-arrow" name="arrow-right" size="32" color="#cccccc"></u-icon>
			</view>
			<view class="list-item" @click="goProtocol('app_privacy_agreement')">
				<text class="item-text">用户隐私协议</text>
				<u-icon class="item-arrow" name="arrow-right" size="32" color="#cccccc"></u-icon>
			</view>
			<view class="list-item" @click="goProtocol('app_protocol_context')">
				<text class="item-text">用户服务协议</text>
				<u-icon class="item-arrow" name="arrow-right" size="32" color="#cccccc"></u-icon>
			</view>
		</view>

		<!-- 底部版权信息 -->
		<view class="copyright">
			<text>CopyRight ©2022-2026 慢热 All Rights Reserved</text>
		</view>
	</view>
</template>

<script setup>
import { ref, getCurrentInstance, onMounted } from 'vue'

const { proxy } = getCurrentInstance()
const $H = proxy?.$H
const $c = proxy.$c;
// 应用信息
const logoUrl = ref('')
const version = ref('')
const appName = $c.miniappName

const getSysInfo = () => {
	if (!$H) return
	$H.get('system/aboutUs').then(res => {
		logoUrl.value = res.logo
		version.value = res.version
	})
}

// 获取当前版本号（仅在 APP 端有意义）
const getAppVersion = () => {
	// #ifdef APP-PLUS
	plus.runtime.getProperty(plus.runtime.appid, (widgetInfo) => {
		version.value = widgetInfo.version || ''
	})
	// #endif

	// #ifdef H5 || MP
	// H5 和 小程序没有 plus.runtime，这里用占位符或者后台返回的版本号（如有）
	if (!version.value) {
		version.value = '1.0.0'
	}
	// #endif
}

// 检测更新
const checkUpdate = () => {
	// #ifdef APP-PLUS
	if (!$H) {
		return uni.showToast({
			title: '网络异常，请稍后重试',
			icon: 'none'
		})
	}

	uni.showLoading({
		title: '正在检查更新...',
		mask: true
	});

	plus.runtime.getProperty(plus.runtime.appid, (widgetInfo) => {
		const currentVersion = widgetInfo.version;
		console.log('当前APP版本号:', currentVersion);

		$H.get('appversion/version', { version: currentVersion }).then(res => {
			uni.hideLoading();
			console.log('版本检测返回数据:', res);

			if (res.code === 0 && res.result && res.result.update) {
				// 有新版本，显示更新弹窗
				showUpdateDialog(res.result);
			} else {
				uni.showToast({
					title: '当前已是最新版本',
					icon: 'none'
				});
			}
		}).catch(err => {
			uni.hideLoading();
			console.error('版本检测请求失败:', err);
			uni.showToast({
				title: '检查更新失败，请稍后重试',
				icon: 'none'
			});
		});
	});
	// #endif

	// #ifdef H5 || MP
	uni.showToast({
		title: '当前已是最新版本',
		icon: 'none'
	})
	// #endif
}

// 显示更新弹窗
const showUpdateDialog = (versionInfo) => {
	// #ifdef APP-PLUS
	const isForce = versionInfo.isForce == 1;

	uni.showModal({
		title: '发现新版本 v' + versionInfo.version,
		content: versionInfo.content || '有新版本可用，是否立即更新？',
		confirmText: isForce ? '立即更新' : '立即更新',
		cancelText: isForce ? '' : '稍后再说',
		showCancel: !isForce,
		success: (res) => {
			if (res.confirm) {
				// 下载或打开应用市场
				if (versionInfo.url) {
					// WGT更新包地址，直接下载安装
					if (versionInfo.url.endsWith('.wgt') || versionInfo.url.includes('wgt')) {
						downloadWgt(versionInfo.url, versionInfo.isForce);
					} else {
						// APK/IPA地址，打开浏览器下载或应用市场
						plus.runtime.openURL(versionInfo.url);
					}
				} else {
					// 根据平台打开应用商店
					openAppStore(versionInfo);
				}

				// 如果是强制更新，退出应用
				if (isForce) {
					exitApp();
				}
			} else if (isForce) {
				// 强制更新但用户点击取消，退出应用
				exitApp();
			}
		}
	});
	// #endif
}

// 下载WGT更新包
const downloadWgt = (wgtUrl, isForce) => {
	// #ifdef APP-PLUS
	uni.showLoading({
		title: '正在下载更新包...',
		mask: true
	});

	const downloadTask = uni.downloadFile({
		url: wgtUrl,
		success: (res) => {
			if (res.statusCode === 200) {
				// 下载成功，安装更新
				plus.runtime.install(res.tempFilePath, {
					force: isForce
				}, (success) => {
					uni.hideLoading();
					uni.showToast({
						title: '已完成即将重启',
						icon: 'success',
						duration: 1500
					});

					// 延迟重启应用
					setTimeout(() => {
						plus.runtime.restart();
					}, 1500);
				}, (err) => {
					uni.hideLoading();
					console.error('安装更新失败:', err);
					uni.showToast({
						title: '更新安装失败，请手动更新',
						icon: 'none'
					});
				});
			} else {
				uni.hideLoading();
				uni.showToast({
					title: '下载更新包失败',
					icon: 'none'
				});
			}
		},
		fail: (err) => {
			uni.hideLoading();
			console.error('下载更新包失败:', err);
			uni.showToast({
				title: '下载失败，请检查网络',
				icon: 'none'
			});
		}
	});
	// #endif
}

// 打开应用商店
const openAppStore = (versionInfo) => {
	// #ifdef APP-PLUS
	let appUrl = '';

	// iOS
	if (uni.getSystemInfoSync().platform === 'ios') {
		appUrl = versionInfo.iosUrl || 'itms-apps://itunes.apple.com/cn/app/id你的APPID';
	} else {
		// Android
		if (versionInfo.androidUrl) {
			appUrl = versionInfo.androidUrl;
		} else {
			// 默认打开应用宝或其他市场
			appUrl = 'market://details?id=' + plus.runtime.appid;
		}
	}

	if (appUrl) {
		plus.runtime.openURL(appUrl);
	}
	// #endif
}

// 退出应用
const exitApp = () => {
	// #ifdef APP-PLUS
	setTimeout(() => {
		plus.runtime.quit();
	}, 1000);
	// #endif
}

// 跳转协议页面
const goProtocol = (type) => {
	uni.navigateTo({
		url: `/pages/user/protocol?type=${type}`
	})
}

onMounted(() => {
	getSysInfo()
	getAppVersion()
})
</script>

<style lang="scss" scoped>
.about-page {
	min-height: 100vh;
	background-color: #f7f7f7;
	padding: 20rpx 24rpx 80rpx;
	box-sizing: border-box;
}

.card {
	background-color: #ffffff;
	border-radius: 24rpx;
	padding: 30rpx;
	box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.02);
}

.app-card {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding-top: 60rpx;
	padding-bottom: 60rpx;
	margin-bottom: 30rpx;

	.app-logo-box {
		width: 180rpx;
		height: 180rpx;
		border-radius: 40rpx;
		overflow: hidden;
		background-color: #ffffff;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 24rpx;
	}

	.app-logo {
		width: 160rpx;
		height: 160rpx;
	}

	.app-info {
		display: flex;
		flex-direction: column;
		align-items: center;

		.app-name {
			font-size: 36rpx;
			font-weight: 600;
			color: #333333;
			margin-bottom: 8rpx;
		}

		.app-version {
			font-size: 26rpx;
			color: #999999;
		}
	}
}

.list-card {
	margin-bottom: 20rpx;

	.list-item {
		min-height: 108rpx;
		padding: 0 4rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		border-bottom-width: 1rpx;
		border-bottom-style: solid;
		border-bottom-color: #f3f3f3;

		&:last-child {
			border-bottom-width: 0;
		}

		.item-text {
			font-size: 30rpx;
			color: #333333;
		}

		.item-arrow {
			font-size: 32rpx;
			color: #cccccc;
		}
	}
}

.copyright {
	margin-top: 50rpx;
	text-align: center;
	font-size: 28rpx;
	color: #b3b3b3;
}
</style>
