<script>
	import websocket from '@/utils/websocket.js';
	import $store from '@/store/index.js';
	
	// 保存应用实例引用
	let appInstance = null;
	
	export default {
		onLaunch: function() {
			// 保存应用实例
			appInstance = this;
			
			// #ifndef H5
			try {
				uni.hideTabBar();
			} catch (e) {
				// 忽略非 TabBar 页面的错误
			}
			// #endif
			
			// #ifdef MP-WEIXIN
			const updateManager = uni.getUpdateManager();
			updateManager.onCheckForUpdate(function(res) {
				// 请求完新版本信息的回调
			});

			updateManager.onUpdateReady(function(res) {
				uni.showModal({
					title: '更新提示',
					content: '新版本已经准备好，是否重启应用？',
					success(res) {
						if (res.confirm) {
							// 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
							updateManager.applyUpdate();
						}
					}
				});
			});

			updateManager.onUpdateFailed(function(res) {
				// 新的版本下载失败
			});
			// #endif
			
			// #ifdef APP-PLUS
			// APP版本更新检测
			this.checkAppUpdate();
			// #endif
			
			// 状态栏高度
			this.globalData.statusBarHeight = uni.getSystemInfoSync().statusBarHeight

			// #ifdef MP-WEIXIN
			// 获取微信胶囊的位置信息 width,height,top,right,left,bottom
			const custom = wx.getMenuButtonBoundingClientRect()
			this.globalData.navigationBarHeight = custom.height + (custom.top - this.globalData.statusBarHeight) * 2
			//只在微信小程序端判断机型
			let platform = uni.getSystemInfoSync().platform;
			if (platform == 'ios') {
				this.globalData.iphone=true
			}
			// #endif
			// #ifdef APP-PLUS || H5
			this.globalData.navigationBarHeight = 55
			// #endif
			// 总体高度 = 状态栏高度 + 导航栏高度
			this.globalData.navHeight = this.globalData.navigationBarHeight + this.globalData.statusBarHeight
			
			
			this.$H.get('user/isOpen').then(res => {
				this.globalData.isOpen = res.result;
			});
			//访问统计
			var terminal='other'
			// #ifdef APP
			terminal='App'
			// #endif
			// #ifdef H5
			terminal='H5'
			// #endif
			// #ifdef MP
			terminal='miniapp'
			// #endif
			this.$H.post('user/visitor',{
				'terminal':terminal
			}).then(res => {});
		},
		
		onShow: function() {
			// #ifdef MP-WEIXIN
			if(typeof wx!=='undefined' && wx.showShareMenu){
				wx.showShareMenu({
					withShareTicket: true,
					menus: ['shareAppMessage', 'shareTimeline']
				})
			}
			// #endif
			// #ifdef H5
			uni.getSystemInfo({
				success(e) {
					/*兼容pc端打开H5*/
					if (e.windowWidth > 420 && !window.top.isPC && !/iOS|Android/i.test(e.system)) {
						// 保存当前URL到sessionStorage
						sessionStorage.setItem('originalUrl', window.location.href);
						window.location.pathname = '/static/pc/pc.html';
					}
				}
			})
			// #endif
			if (uni.getStorageSync('hasLogin')) {
				//连接websocket
				 if (!$store.state.isSocketOpen) {
            		websocket.initConnect();
        		}
				//获取好友列表
				$store.dispatch('getFriendList');
				///获取通知消息
				$store.dispatch('getNoticeList');
			}
		},
		onPageNotFound() {
			// 跳转到 404 页面：
			uni.redirectTo({
				url: "pages/error/error"
			});
		},
		globalData: {
			isOpen: 1, //是否关闭视频功能 默认1开启 0关闭
			statusBarHeight: 0, // 状态导航栏高度
			navHeight: 0, // 总体高度
			navigationBarHeight: 0, // 导航栏高度(标题栏高度)
			iphone: false,//是否为IOS机型
		},
		
		methods: {
			// APP版本更新检测
			checkAppUpdate() {
				// #ifdef APP-PLUS
				// 获取当前APP版本号
				plus.runtime.getProperty(plus.runtime.appid, (widgetInfo) => {
					const currentVersion = widgetInfo.version;
					console.log('当前APP版本号:', currentVersion);
					// 请求服务器版本信息
					this.$H.get('appversion/version', {
						version: currentVersion
					}).then(res => {
						if (res.code === 0 && res.result && res.result.update) {
							// 需要更新，显示更新弹窗
							this.showUpdateDialog(res.result);
						}
					}).catch(err => {
						uni.hideLoading();
						console.error('版本检测请求失败:', err);
					});
				});
				// #endif
			},
			
			// 显示更新弹窗
			showUpdateDialog(versionInfo) {
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
									this.downloadWgt(versionInfo.url, versionInfo.isForce);
								} else {
									// APK/IPA地址，打开浏览器下载或应用市场
									plus.runtime.openURL(versionInfo.url);
								}
							} else {
								// 根据平台打开应用商店
								this.openAppStore(versionInfo);
							}
							
							// 如果是强制更新，退出应用
							if (isForce) {
								this.exitApp();
							}
						} else if (isForce) {
							// 强制更新但用户点击取消，退出应用
							this.exitApp();
						}
					}
				});
				// #endif
			},
			
			// 下载WGT更新包
			downloadWgt(wgtUrl, isForce) {
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
			},
			
			// 打开应用商店
			openAppStore(versionInfo) {
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
			},
			
			// 退出应用
			exitApp() {
				// #ifdef APP-PLUS
				setTimeout(() => {
					plus.runtime.quit();
				}, 1000);
				// #endif
			}
		}
	};
</script>

<style lang="scss">
	@import "./uni_modules/vk-uview-ui/index.scss";
	@import "static/css/iconfont.css";
	@import "static/css/app-common.scss";
</style>