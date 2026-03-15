<template>
	<view>
		<block v-for="(item, index) in list" :key="index">
			<view @click="jump(item)">
				<view class="post-item">
					<view class="post-item-top-user">
						<view @click.stop="toUcenter(item.uid)">
							<u-avatar class="avatar" :src="item.userInfo.avatar" :show-level='item.userInfo.type == 1'
								level-bg-color="#8072f3"></u-avatar>
						</view>
						<view class="center">
							<view style="display: flex;align-items: center;">
								<text v-if="item.userInfo.type == 1" class="official">官方</text>
								<text class="username">{{ item.userInfo.username.substring(0, 12) }}</text>
								<image src="@/static/vip/vipIcon.png" class="image" v-if="item.userInfo.vip==1" />
								<image style="height: 22rpx;margin-left:10rpx ;" mode="heightFix" v-if="item.userInfo.level" :src="$IMG+'/vip/level_'+item.userInfo.level+'.png'" class="level-img-2"/>
								<view v-if="showTag&&item.postTop>0&&item.status==0" class="officials"><u-icon
										name="arrow-upward"></u-icon>置顶</view>
								<text v-if="!showTag&&item.status==1" class="officials">审核中</text>
								<text v-if="!showTag&&item.status==2" class="officials">已下架</text>
							</view>
							<view>
							<text class="time">{{ timeFormat(item.createTime) }}</text>
							</view>
						</view>
						<view class="right">
							<!-- 圈主 -->
							<view v-if="handle && sessionUid == uid" @tap.stop="onActive(item, index)">
								<u-icon class="arrow-down" size="40" name="more-dot-fill"></u-icon>
							</view>
							<!-- 圈子管理员 -->
							<view v-else-if="admin" @tap.stop="onActive(item, index)">
								<u-icon class="arrow-down" size="40" name="more-dot-fill"></u-icon>
							</view>
							<!-- 帖子发布者 -->
							<view v-else-if="sessionUid == item.uid" @tap.stop="onActive(item, index)">
								<u-icon class="arrow-down" size="40" name="more-dot-fill"></u-icon>
							</view>
							<!-- 普通用户 -->
							<view v-else @tap.stop="onActive2(item, index)">
								<u-icon class="arrow-down" size="40" name="more-dot-fill"></u-icon>
							</view>
						</view>
					</view>
					<text class="discuss-title" :data-id="item.discussId" v-if="item.discussId > 0"
						@tap.stop="toDiscuss">#{{ item.discussTitle }}</text>
					<view class="post-content" v-if="item.type != 3">
						<rich-text class="post-text" :nodes="item.content"></rich-text>
						<!-- 帖子类型 -->
						<block v-if="item.type == 1&&item.cut != 1">
							<!--一张图片-->
							<block v-if="item.media.length == 1">
								<image :lazy-load="true" mode="aspectFill" class="img-style-1" :src="item.media[0]"
									@tap.stop="previewImage(item.media[0], item.media)"></image>
							</block>
							<!--二张图片-->
							<block v-else-if="item.media.length == 2">
								<view class="img-style-2">
									<image :lazy-load="true" v-for="(mediaItem, index2) in item.media" :key="index2"
										@tap.stop="previewImage(mediaItem, item.media)" mode="aspectFill"
										:src="mediaItem"></image>
								</view>
							</block>
							<!--四张图片-->
							<block v-else-if="item.media.length == 4">
								<view class="img-style-4">
									<image :lazy-load="true" v-for="(mediaItem, index2) in item.media" :key="index2"
										@tap.stop="previewImage(mediaItem, item.media)" mode="aspectFill"
										:src="mediaItem"></image>
								</view>
							</block>
							<block v-else>
								<view class="other-img-flex" :class="imageClass[item.media.length]">
									<view :class="'wrap-style ' + 'wrap-style-' + (imageIndex + 1)"
										v-for="(image, imageIndex) in item.media" :key="imageIndex">
										<image class="show-flex" mode="aspectFill" :src="item.media[imageIndex]"
											@tap.stop="previewImage(image, item.media)"></image>
									</view>
								</view>
							</block>
						</block>
						<block v-if="item.type == 1&&item.cut == 1">
							<view>
								<view class="pay-content">
									<u-icon name="lock"></u-icon>
									解锁查看全部信息
									<view class="dynamic">
										<view class="dy dynamic_left"></view>
										<view class="dy dynamic_right"></view>
									</view>
								</view>
							</view>
						</block>
						<!-- 视频 -->
						<view class="video-wrap" v-if="item.type == 2 && item.media.length > 0 && item.cut != 1">
							<image class="icon" src="/static/play.png"></image>
							<image mode="aspectFill" :src="getVideoThumb(item.media[0])"></image>
						</view>
						<!-- 付费视频 -->
						<block v-if="item.type == 2&&item.cut == 1">
							<view>
								<view class="pay-content">
									<u-icon name="lock"></u-icon>
									付费视频需解锁查看
									<view class="dynamic">
										<view class="dy dynamic_left"></view>
										<view class="dy dynamic_right"></view>
									</view>
								</view>
							</view>
						</block>
					</view>
					<view class="post-content" v-if="item.type === 3">
						<rich-text class="post-text" :nodes="item.title"></rich-text>
					<block v-if="item.media && item.media.length > 0 && item.media[0]">
								<image :lazy-load="true" mode="aspectFill" class="img-style-1" :src="item.media[0]"
									@tap.stop="previewImage(item.media[0], item.media)"></image>
						</block>
					</view>
					<!-- 投票 -->
					<view v-if="item.type === 4" class="vote-box">
						<view class="title">{{item.voteInfo.title}}</view>
						<view class="expire-time">截止：{{item.voteInfo.time }}</view>
						<view class="vote-item" v-for="(item2,index2) in item.voteInfo.options" :key="index2">
							{{item2.content}}
						</view>
					</view>
					<!-- 圈子 -->
					<!-- <view class="topic" v-if="showTopic">
						<u-icon class="topic-icon" name="moments-circel-fill"></u-icon>
						<text>官方圈子</text>
					</view> -->
					<!-- 位置 -->
					<view class="address" v-if="item.address">
						<u-icon class="icon" name="map-fill"></u-icon>
						<text>{{ item.address }}</text>
					</view>
					<!-- 底部 -->
					<view class="p-footer" :class="{ 'has-media': hasMedia(item) }">
						<view class="p-item margin50">
							<text class="iconfont icon-pinglun"></text>
							<text class="count">{{ item.commentCount }}</text>
						</view>
						<view class="p-item margin50">
							<text class="iconfont icon-yuedu"></text>
							<text class="count">{{ numberFormat(item.readCount) }}</text>
						</view>
						<view v-if="item.isCollection" class="p-item" @click.stop="cancelCollection(item.id,index)">
							<u-icon name="thumb-up-fill" color="#cc0000"></u-icon>
							<text class="count">{{ item.collectionCount }}</text>
						</view>
						<view v-if="!item.isCollection" class="p-item" @click.stop="addCollection(item.id,index)">
							<u-icon name="thumb-up"></u-icon>
							<text class="count">{{ item.collectionCount }}</text>
						</view>
					</view>
				</view>
			</view>
			<!-- #ifdef H5||MP-WEIXIN -->
			<view v-if="index%3==0&&openAd">
				<!-- 广告 -->
				<linfeng-ad :isVip="isVip" :open="open" :h5Adpid="h5Adpid" :wxAdpid="wxAdpid"></linfeng-ad>
			</view>
			<!-- #endif -->

		</block>
		<!-- 操作弹窗 -->
		<u-action-sheet :list="actionList" v-model="showAction" @click="actionCon"></u-action-sheet>
		<!-- 加载状态 -->
		<block v-if="list.length === 0 && loadStatus == 'nomore'">
			<u-empty margin-top="100" text="暂无内容" mode="favor"></u-empty>
		</block>
		<block v-else>
			<view style="margin: 30rpx 0;">
				<u-loadmore :status="loadStatus" />
			</view>
		</block>
	</view>
</template>

<script setup>
	import { ref, watch, getCurrentInstance } from 'vue'
	import storageConfig from '@/utils/storage-config.js'
	import linfengAd from "../../components/linfeng-ad/linfeng-ad.vue"
	import { timeFormat, numberFormat } from "@/utils/filters.js";
	
	const { proxy } = getCurrentInstance();
	const $H = proxy.$H;
	const $c = proxy.$c;
	const $u = proxy.$u;
	const $f = proxy.$f;
	
	const props = defineProps({
			list: Array,
			loadStatus: String,
			handle: {
				default: false,
				type: Boolean
			},
			showTopic: {
				default: true,
				type: Boolean
			},
			uid: Number,
			admin: {
				default: false,
				type: Boolean
			},
			//是否显示红色置顶状态
		showTag: {
			default: false,
			type: Boolean
		},
			//流量主总开关0关闭1开启
			open: {
				type: String,
				default: '0'
			},
			h5Adpid: {
				type: String,
				default: '1818425366'
			},
			wxAdpid: {
				type: String,
				default: '1872486102'
			},
			//帖子列表页是否展示流量主广告
			openAd: {
				type: Boolean,
				default: false
			},
	});
	
	const $IMG = ref(proxy.$IMG);
	const showAction = ref(false);
	const actionList = ref([{
					text: '关注',
					key: 'follow'
				}, {
					text: '举报',
					key: 'report'
	}]);
	const choosePost = ref('');
	const chooseIndex = ref('');
	const sessionUid = ref('');
	const isVip = ref(0);
	const imageClass = ref(['', 'one-img', 'two-img', 'three-img', 'four-img', 'five-img', 'six-img', 'seven-img', 'eight-img', 'nine-img']);
	
	// 初始化用户信息
	let userInfo = uni.getStorageSync('userInfo');
	if (userInfo) {
		sessionUid.value = userInfo.uid;
		isVip.value = uni.getStorageSync('userInfo').vip
	}
	
	watch(() => props.uid, (n) => {
		if (sessionUid.value == n && props.handle) {
					//圈主
			actionList.value.unshift({
						text: '置顶',
						key: 'top'
					});
			actionList.value.unshift({
						text: '删除',
						color: 'red',
						key: 'delete'
					});
		} else if (props.admin) {
					//圈子管理员
			actionList.value.unshift({
						text: '删除',
						color: 'red',
						key: 'delete'
					});
			}
	});
	
	function getVideoThumb(url) {
				return storageConfig.getVideoThumb(url)
	}
	
	function copyPageUrl(id) {
				uni.setClipboardData({
			data: $c.shareH5Url + 'pages/post/detail?id=' + id,
					success: function() {
						uni.hideToast();
				$f.toast('复制成功', 'success');
				showAction.value = false;
					}
				});
	}
	
	function onActive(postInfo, index) {
		showAction.value = true;
		choosePost.value = postInfo;
		chooseIndex.value = index;
		if (sessionUid.value == choosePost.value.uid && !props.admin) {
					//帖子发布者
			actionList.value = actionList.value.filter(item => item.key != 'delete' && item.key != 'cancelFollow' && item.key != 'follow')
			actionList.value.unshift({
						text: '删除',
						color: 'red',
						key: 'delete'
					});
				} else {
					//圈子管理员或圈主
					if (postInfo.isFollow) {
				actionList.value = actionList.value.filter(item => item.key != 'cancelFollow' && item.key != 'follow')
				actionList.value.unshift({
							text: '取消关注',
							key: 'cancelFollow'
						});
					} else {
				actionList.value = actionList.value.filter(item => item.key != 'cancelFollow' && item.key != 'follow')
				actionList.value.unshift({
							text: '关注',
							key: 'follow'
						});
					}
				}
	}
	
			//普通用户触发
	function onActive2(postInfo, index) {
				if (postInfo.isFollow) {
			actionList.value = actionList.value.filter(item => item.key != 'cancelFollow' && item.key != 'follow')
			actionList.value.unshift({
						text: '取消关注',
						key: 'cancelFollow'
					});
				} else {
			actionList.value = actionList.value.filter(item => item.key != 'cancelFollow' && item.key != 'follow')
			actionList.value.unshift({
						text: '关注',
						key: 'follow'
					});
				}
		showAction.value = true;
				try {
					uni.hideTabBar();
				} catch (e) {
					// 忽略非 TabBar 页面的错误
				}
		choosePost.value = postInfo;
		chooseIndex.value = index;
		actionList.value = actionList.value.filter(item => item.key != 'delete')
	}
	
	function actionCon(index) {
		let key = actionList.value[index].key;
				if (key == 'follow') {
			follow();
				} else if (key == 'cancelFollow') {
			cancelFollow();
				} else if (key == 'delete') {
			postDel();
				} else if (key == 'top') {
			postTop();
				} else if (key == 'report') {
			report();
				}
	}
	
	function report() {
				uni.navigateTo({
			url: '/pages/report/report?postId=' + choosePost.value.id + '&uid=' + choosePost.value.uid
				})
	}
	
	function cancelCollection(id, index) {
		$H.post('post/cancelCollection', { id: id }).then(res => {
						if (res.code === 0) {
				props.list[index].isCollection = false;
				props.list[index].collectionCount--;
						}
					});
	}
	
	function addCollection(id, index) {
		$H.post('post/addCollection', { id: id, uid: props.list[index].uid }).then(res => {
						if (res.code === 0) {
				props.list[index].isCollection = true;
				props.list[index].collectionCount++;
						}
					});
	}
	
			// 置顶帖子
	function postTop() {
		$H.post('post/setPostTop', {
			postId: choosePost.value.id,
			topicId: choosePost.value.topicId
		}).then(res => {
						if (res.code == 0) {
				$f.toast('成功设为置顶', 'success');
						}
					});
	}
	
	function postDel() {
		$H.post('post/del', { id: choosePost.value.id }).then(res => {
						if (res.code == 0) {
				props.list.splice(chooseIndex.value, 1);
						}
					});
	}
	
	function follow() {
		$H.post('user/addFollow', { id: choosePost.value.uid }).then(res => {
						if (res.code === 0) {
				props.list[chooseIndex.value].isFollow = true;
				$u.toast(res.msg);
						}
					});
	}
	
	function cancelFollow() {
		$H.post('user/cancelFollow', { id: choosePost.value.uid }).then(res => {
						if (res.code === 0) {
				props.list[chooseIndex.value].isFollow = false;
				$u.toast(res.msg);
						}
					});
	}
	
	function previewImage(url, urls) {
				uni.previewImage({
					current: url, // 当前显示图片的http链接
					urls: urls // 需要预览的图片http链接列表
				});
	}
	
	function jumpTopic(id) {
				uni.navigateTo({
					url: '/pages/topic/detail?id=' + id
				});
	}

	function jump(e) {
				let url;

				// 图文
				if (e.type == 1 || e.type == 4) {
					if (e.cut == 0) {
						url = '/pages/post/detail?id=' + e.id;
					} else {
				$H.post('post/getVipPostInfo', {
							postId: e.id
						}).then(res => {
							if (res.result.isBuy) {
								url = '/pages/post/detail?id=' + e.id;
							} else {
								url = '/pages/post/confirm?id=' + e.id + '&type=' + e.type;
							}
							uni.navigateTo({
								url: url
							})
						});
					}
				}
		
				//长文
				if (e.type == 3) {
					url = '/subpages/content/article/article?id=' + e.id;
				}

				//视频
				if (e.type == 2) {
					// #ifdef APP-PLUS
					if (e.cut == 0) {
						url = '/pages/post/detail?id=' + e.id;
					} else {
				$H.post('post/getVipPostInfo', {
							postId: e.id
						}).then(res => {
							if (res.result.isBuy) {
								url = '/pages/post/detail?id=' + e.id;
							} else {
								url = '/pages/post/confirm?id=' + e.id + '&type=' + e.type;
							}
							uni.navigateTo({
								url: url
							})
						});
					}
					// #endif
					// #ifndef APP-PLUS
					if (e.cut == 0) {
						url = '/pages/post/video-detail?id=' + e.id;
					} else {
				$H.post('post/getVipPostInfo', {
							postId: e.id
						}).then(res => {
							if (res.result.isBuy) {
								url = '/pages/post/video-detail?id=' + e.id;
							} else {
								url = '/pages/post/confirm?id=' + e.id + '&type=' + e.type;
							}
							uni.navigateTo({
								url: url
							})
						});
					}
					// #endif
				}

				uni.navigateTo({
					url: url
				})
	}
	
	function toDiscuss(e) {
				uni.navigateTo({
					url: '/pages/discuss/detail?id=' + e.currentTarget.dataset.id
				});
	}
	
	function toUcenter(uid) {
				uni.navigateTo({
					url: '/pages/user/home?uid=' + uid
				});
			}
	
	// 判断是否有图片或视频显示
	function hasMedia(item) {
		if (item.type == 1 && item.cut != 1) {
			// 图文类型，有媒体文件
			return item.media && item.media.length > 0
		}
		if (item.type == 2 && item.cut != 1) {
			// 视频类型，有媒体文件
			return item.media && item.media.length > 0
		}
		if (item.type == 3) {
			// 长文类型，有封面图
			return item.media && item.media.length > 0 && item.media[0]
		}
		return false
	}
</script>

<style lang="scss" scoped>
	.post-item {
		background: #fff;
		padding: 32rpx;
		margin: 0 24rpx 8rpx 24rpx;
		border-radius: 20rpx;
		box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.04);
		transition: all 0.3s ease;
		border: 1rpx solid #f5f5f5;

		&:active {
			transform: scale(0.98);
			box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
		}

		.post-content {
			margin-top: 24rpx;

			.img-style-1 {
				display: block;
				width: 100%;
				max-height: 600rpx;
				border-radius: 16rpx;
				overflow: hidden;
				box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
			}

			.img-style-2 {
				display: flex;
				gap: 8rpx;

				image {
					flex: 1;
					border-radius: 12rpx;
					height: 294rpx;
					box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
				}
			}

			.img-style-3 {
				display: flex;
				flex-wrap: wrap;
				gap: 8rpx;

				image {
					width: calc(33.33% - 6rpx);
					height: 200rpx;
					border-radius: 12rpx;
					box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
				}
			}

			.img-style-4 {
				display: flex;
				flex-wrap: wrap;
				gap: 8rpx;

				image {
					width: calc(50% - 4rpx);
					height: 320rpx;
					border-radius: 12rpx;
					box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
				}
			}
		}

		.address {
			display: inline-flex;
			align-items: center;
			font-size: 24rpx;
			background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf0 100%);
			border-radius: 24rpx;
			padding: 8rpx 20rpx;
			margin: 16rpx 0;
			color: #64748b;
			border: 1rpx solid #e2e8f0;

			.icon {
				margin-right: 8rpx;
				font-size: 22rpx;
			}
		}

		.topic {
			display: inline-flex;
			align-items: center;
			font-size: 24rpx;
			background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
			border-radius: 24rpx;
			padding: 8rpx 20rpx;
			margin: 16rpx 0;
			color: #ffffff;
			box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);

			.topic-icon {
				margin-right: 8rpx;
				font-size: 22rpx;
			}
		}
	}

	.post-item-top-user {
		display: flex;
		align-items: center;
		margin-bottom: 8rpx;

		.avatar {
			margin-right: 20rpx;
			
		}

		.center {
			flex: 1;
			display: flex;
			flex-direction: column;
			gap: 8rpx;

			.username {
				font-size: 30rpx;
				font-weight: 600;
				color: #1e293b;
				line-height: 1.4;
			}

			.time {
				font-size: 22rpx;
				color: #94a3b8;
			}

			.official {
				display: inline-block;
				font-size: 20rpx;
				color: #ffffff;
				background-color: $themes-color;
				padding: 5rpx 10rpx;
				border-radius: 10rpx;
				margin-right: 10rpx;
			}

			.officials {
				display: inline-flex;
				align-items: center;
				font-size: 22rpx;
				color: #ffffff;
				background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
				padding: 4rpx 12rpx;
				border-radius: 8rpx;
				margin-left: auto;
				font-weight: 500;
				box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.3);
			}

			.image {
				width: 60rpx;
				height: 28rpx;
				margin-left: 8rpx;
			}

			.level-img-2 {
				margin-left: 8rpx;
			}
		}

		.right {
			height: 88rpx;
			display: flex;
			align-items: center;

			.arrow-down {
				color: #94a3b8;
				margin-left: 20rpx;
				padding: 8rpx;
				border-radius: 50%;
				transition: all 0.3s ease;

				&:active {
					background: #f1f5f9;
					transform: scale(0.9);
				}
			}
		}
	}

	.post-text {
		display: block;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 10;
		white-space: pre-wrap;
		overflow: hidden;
		font-size: 28rpx;
		line-height: 1.8;
		color: #334155;
		word-break: break-word;
	}

	.discuss-title {
		display: inline-block;
		padding: 0 20rpx;
		line-height: 40rpx;
		height: 40rpx;
		border-radius: 50rpx;
		font-size: 22rpx;
		border: 1px solid #F5F6F5;
		color: #6e84ef;
		text-align: center;
		margin: 12rpx 0;
	}

	.p-footer {
		display: flex;
		align-items: center;
		justify-content: space-around;
		margin-top: 24rpx;
		padding-top: 20rpx;
		
		&.has-media {
			border-top: 1rpx solid #f1f5f9;
		}

		.p-item {
			display: flex;
			align-items: center;
			color: #64748b;
			padding: 8rpx 16rpx;
			border-radius: 16rpx;
			transition: all 0.3s ease;
			min-width: 80rpx;
			justify-content: center;

			&:active {
				background: #f1f5f9;
				transform: scale(0.95);
			}

			.iconfont {
				font-size: 36rpx;
				color: #64748b;
			}

			.count {
				margin-left: 8rpx;
				font-size: 24rpx;
				color: #64748b;
				font-weight: 500;
			}
		}

		.p-item[hidden] {
			display: none !important;
		}

		.margin50 {
			margin: 0;
		}
	}

	.comment-wrap {
		font-size: 28rpx;
		padding-top: 24rpx;
		border-top: 1rpx solid #f1f5f9;
		margin-top: 20rpx;

		.item {
			.name {
				color: #1e293b;
				font-weight: 600;
			}
		}
	}

	.video-wrap {
		display: flex;
		justify-content: center;
		align-items: center;
		position: relative;
		width: 100%;
		height: 500rpx;
		border-radius: 16rpx;
		overflow: hidden;
		margin-top: 16rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);

		.icon {
			width: 120rpx;
			height: 120rpx;
			z-index: 99;
			opacity: 0.9;
			transition: all 0.3s ease;
		}

		image {
			position: absolute;
			width: 100%;
			height: 100%;
			object-fit: cover;
		}

		&:active .icon {
			transform: scale(0.9);
			opacity: 0.7;
		}
	}



	// 投票
	.vote-box {
		background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
		border-radius: 20rpx;
		padding: 32rpx;
		margin: 24rpx 0;
		border: 1rpx solid #e2e8f0;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

		.title {
			font-weight: 600;
			font-size: 32rpx;
			color: #1e293b;
			margin-bottom: 12rpx;
		}

		.expire-time {
			font-size: 24rpx;
			color: #64748b;
			margin-bottom: 24rpx;
			padding-bottom: 16rpx;
			border-bottom: 1rpx solid #e2e8f0;
		}

		.vote-item {
			font-size: 26rpx;
			font-weight: 500;
			padding: 24rpx;
			border-radius: 16rpx;
			border: 2rpx solid #cbd5e1;
			text-align: center;
			margin-bottom: 16rpx;
			background: #ffffff;
			color: #334155;
			transition: all 0.3s ease;

			&:last-child {
				margin-bottom: 0;
			}

			&:active {
				background: #f1f5f9;
				border-color: #94a3b8;
				transform: scale(0.98);
			}
		}
	}

	.other-img-flex {
		display: flex;
		align-items: center;
		position: relative;
		margin: 16rpx 0;
		width: 100%;
		gap: 8rpx;

		.wrap-style {
			overflow: hidden;
			border-radius: 12rpx;
			box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
			transition: all 0.3s ease;

			&:active {
				transform: scale(0.98);
				box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
			}

			.show-flex {
				width: 100%;
				height: 100%;
				display: block;
			}
		}
	}

	.two-img {
		justify-content: space-between;
		align-items: center;
	}

	.two-img .wrap-style-1,
	.two-img .wrap-style-2 {
		height: 332rpx;
		width: 332rpx;
	}

	.two-img .wrap-style-1 {
		border-radius: 12rpx 0 0 12rpx;
	}

	.two-img .wrap-style-2 {
		border-radius: 0 12rpx 12rpx 0;
	}

	.two-img .wrap-style-1 .show-flex,
	.two-img .wrap-style-2 .show-flex {
		border-radius: 0;
	}

	.three-img {
		justify-content: space-between;
		flex-wrap: wrap;
		width: 674rpx;
		gap: 0;
	}

	.three-img .wrap-style-1,
	.three-img .wrap-style-2,
	.three-img .wrap-style-3 {
		height: 332rpx;
		width: 332rpx;
	}

	.three-img .wrap-style-1 {
		height: 446rpx;
		width: 674rpx;
		margin-bottom: 10rpx;
	}

	.three-img .wrap-style-1 {
		border-radius: 12rpx 12rpx 0 0;
	}

	.three-img .wrap-style-2 {
		border-radius: 0 0 0 12rpx;
	}

	.three-img .wrap-style-3 {
		border-radius: 0 0 12rpx 0;
	}

	.three-img .wrap-style-1 .show-flex,
	.three-img .wrap-style-2 .show-flex,
	.three-img .wrap-style-3 .show-flex {
		border-radius: 0;
	}

	.four-img {
		justify-content: space-between;
		flex-wrap: wrap;
	}

	.four-img .wrap-style-1,
	.four-img .wrap-style-2,
	.four-img .wrap-style-3,
	.four-img .wrap-style-4 {
		height: 218rpx;
		width: 218rpx;
	}

	.four-img .wrap-style-1 {
		height: 446rpx;
		width: 674rpx;
		margin-bottom: 10rpx;
	}

	.four-img .wrap-style-1 .show-flex {
		border-radius: 12rpx 12rpx 0 0;
	}

	.four-img .wrap-style-2 .show-flex {
		border-radius: 0 0 0 12rpx;
	}

	.four-img .wrap-style-3 .show-flex {
		border-radius: 0 0 12rpx 0;
	}

	.five-img {
		justify-content: space-between;
		flex-wrap: wrap;
		width: 674rpx;
		gap: 0;
	}

	.five-img .wrap-style-1,
	.five-img .wrap-style-2,
	.five-img .wrap-style-3,
	.five-img .wrap-style-4,
	.five-img .wrap-style-5 {
		height: 332rpx;
		width: 332rpx;
	}

	.five-img .wrap-style-1 {
		height: 446rpx;
		width: 446rpx;
		margin-bottom: 10rpx;
	}

	.five-img .wrap-style-2 {
		height: 218rpx;
		width: 218rpx;
		margin-top: -238rpx;
	}

	.five-img .wrap-style-3 {
		height: 218rpx;
		width: 218rpx;
		position: absolute;
		right: 0;
		top: 228rpx;
	}

	.five-img .wrap-style-1 .show-flex {
		border-radius: 12rpx 0 0 0;
	}

	.five-img .wrap-style-2 .show-flex {
		border-radius: 0 12rpx 0 0;
	}

	.five-img .wrap-style-4 .show-flex {
		border-radius: 0 0 0 12rpx;
	}

	.five-img .wrap-style-5 .show-flex {
		border-radius: 0 0 12rpx 0;
	}

	.six-img {
		justify-content: space-between;
		flex-wrap: wrap;
		width: 674rpx;
		gap: 0;
	}

	.six-img .wrap-style-1,
	.six-img .wrap-style-2,
	.six-img .wrap-style-3,
	.six-img .wrap-style-4,
	.six-img .wrap-style-5,
	.six-img .wrap-style-6 {
		height: 218rpx;
		width: 218rpx;
	}

	.six-img .wrap-style-1 {
		height: 446rpx;
		width: 446rpx;
		margin-bottom: 10rpx;
	}

	.six-img .wrap-style-2 {
		margin-top: -238rpx;
	}

	.six-img .wrap-style-3 {
		position: absolute;
		right: 0;
		top: 228rpx;
	}

	.six-img .wrap-style-1 .show-flex {
		border-radius: 12rpx 0 0 0;
	}

	.six-img .wrap-style-2 .show-flex {
		border-radius: 0 12rpx 0 0;
	}

	.six-img .wrap-style-4 .show-flex {
		border-radius: 0 0 0 12rpx;
	}

	.six-img .wrap-style-6 .show-flex {
		border-radius: 0 0 12rpx 0;
	}

	.seven-img {
		justify-content: space-between;
		flex-wrap: wrap;
		width: 674rpx;
		gap: 0;
	}

	.seven-img .wrap-style-1,
	.seven-img .wrap-style-2,
	.seven-img .wrap-style-3,
	.seven-img .wrap-style-4,
	.seven-img .wrap-style-5,
	.seven-img .wrap-style-6,
	.seven-img .wrap-style-7 {
		height: 218rpx;
		width: 218rpx;
		margin-bottom: 10rpx;
	}

	.seven-img .wrap-style-1 {
		height: 446rpx;
		width: 674rpx;
	}

	.seven-img .wrap-style-5,
	.seven-img .wrap-style-6,
	.seven-img .wrap-style-7 {
		margin: 0;
	}

	.seven-img .wrap-style-1 .show-flex {
		border-radius: 12rpx 12rpx 0 0;
	}

	.seven-img .wrap-style-5 .show-flex {
		border-radius: 0 0 0 12rpx;
	}

	.seven-img .wrap-style-7 .show-flex {
		border-radius: 0 0 12rpx 0;
	}

	.eight-img {
		justify-content: space-between;
		flex-wrap: wrap;
		width: 674rpx;
		gap: 0;
	}

	.eight-img .wrap-style-1,
	.eight-img .wrap-style-2,
	.eight-img .wrap-style-3,
	.eight-img .wrap-style-4,
	.eight-img .wrap-style-5,
	.eight-img .wrap-style-6,
	.eight-img .wrap-style-7,
	.eight-img .wrap-style-8 {
		height: 218rpx;
		width: 218rpx;
		margin-bottom: 10rpx;
	}

	.eight-img .wrap-style-6,
	.eight-img .wrap-style-7,
	.eight-img .wrap-style-8 {
		margin: 0;
	}

	.eight-img .wrap-style-1,
	.eight-img .wrap-style-2 {
		height: 332rpx;
		width: 332rpx;
	}

	.eight-img .wrap-style-1 .show-flex {
		border-radius: 12rpx 0 0 0;
	}

	.eight-img .wrap-style-2 .show-flex {
		border-radius: 0 12rpx 0 0;
	}

	.eight-img .wrap-style-6 .show-flex {
		border-radius: 0 0 0 12rpx;
	}

	.eight-img .wrap-style-8 .show-flex {
		border-radius: 0 0 12rpx 0;
	}

	.nine-img {
		justify-content: space-between;
		flex-wrap: wrap;
		width: 674rpx;
		gap: 0;
	}

	.nine-img .wrap-style-1,
	.nine-img .wrap-style-2,
	.nine-img .wrap-style-3,
	.nine-img .wrap-style-4,
	.nine-img .wrap-style-5,
	.nine-img .wrap-style-6,
	.nine-img .wrap-style-7,
	.nine-img .wrap-style-8,
	.nine-img .wrap-style-9 {
		height: 218rpx;
		width: 218rpx;
		margin-bottom: 10rpx;
	}

	.nine-img .wrap-style-7,
	.nine-img .wrap-style-8,
	.nine-img .wrap-style-9 {
		margin: 0;
	}

	.nine-img .wrap-style-1 .show-flex {
		border-radius: 12rpx 0 0 0;
	}

	.nine-img .wrap-style-3 .show-flex {
		border-radius: 0 12rpx 0 0;
	}

	.nine-img .wrap-style-7 .show-flex {
		border-radius: 0 0 0 12rpx;
	}

	.nine-img .wrap-style-9 .show-flex {
		border-radius: 0 0 12rpx 0;
	}
</style>