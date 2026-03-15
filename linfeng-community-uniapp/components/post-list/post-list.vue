<template>
	<view>
		<block v-for="(item, index) in list" :key="index">
			<view @click="jump(item)" style="background-color: #ffffff;">
				<view class="post-item">
					<view class="post-item-top-user">
						<view @click.stop="toUcenter(item.uid)">
							<u-avatar class="avatar" :src="item.userInfo.avatar" :show-level='item.userInfo.type == 1'
								level-bg-color="#8072f3"></u-avatar>
						</view>
						<view class="center">
							<view style="display: flex;align-items: center;">
								<text class="username">{{ item.userInfo.username.substring(0, 12) }}</text>
								
								<image style="height: 22rpx;" mode="heightFix" v-if="item.userInfo.level" :src="$IMG+'/vip/level_'+item.userInfo.level+'.png'" class="level-img"/>
								<image src="@/static/vip/vipIcon.png" class="image" v-if="item.userInfo.vip==1" />
								<view v-if="showTag&&item.postTop>0&&item.status==0" class="officials"><u-icon
										name="arrow-upward"></u-icon>置顶</view>
								<text v-if="!showTag&&item.status==1" class="officials">审核中</text>
								<text v-if="!showTag&&item.status==2" class="officials">已下架</text>
							</view>
							<view>
								<text class="desc-user" v-if="item.userInfo.type == 1">官方管理员</text>
								<text class="desc-user" v-else-if="item.userInfo.vip==1">会员用户</text>
								<text class="desc-user" v-else>普通用户</text>
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
							<rich-text class="post-text" :nodes="item.brief"></rich-text>
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
						<!-- 视频 aliyun qiniu-->
						<view class="video-wrap" v-if="item.type == 2 && item.media.length > 0 && item.cut != 1">
							<image class="icon" src="/static/play.png"></image>
							<image 
								mode="aspectFill" 
								:src="getVideoThumb(item.media[0])"
							></image>
						</view>
						<!-- 付费视频 -->
						<block v-if="item.type == 2&&item.cut == 1">
							<rich-text class="post-text" :nodes="item.brief"></rich-text>
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
					<!-- 时间&话题&位置&圈子 -->
					<view class="middle-box">
						<view style="display: flex;align-items: center;">
							<text class="time-index">{{ timeFormat(item.createTime) }}</text>
						</view>
						<view class="quanzi" v-if="item.discussId > 0">
							<text class="txt" style="max-width: 120rpx;color: #000000;" @click.stop="toDiscuss(item.discussId)">
								<text class="tag-discuss"></text>#{{ item.discussTitle }}</text>
						</view>
						<view class="quanzi" v-if="item.address">
							<u-icon name="map-fill" size="24" color="#000"></u-icon>
							<text class="txt" style="margin-left: 6rpx;">{{ item.address }}</text>
						</view>
						<view class="quanzi" @click.stop="jumpTopic(item.topicId)">
							<u-icon name="moments-circel-fill" size="28" color="#000"></u-icon>
							<view class="txt quan-txt">
								{{item.topicName}}
							</view>
						</view>
					</view>

					<!-- 底部 -->
					<view class="p-footer">
						<view class="p-item" style="margin-right: 54rpx;">
							<u-icon name="chat" color="#000000" size="44"></u-icon>
							<text class="count">{{ item.commentCount }}</text>
						</view>
						<view v-show="item.isCollection" class="p-item" @click.stop="cancelCollection(item.id,index)">
							<u-icon name="thumb-up-fill" color="#cc0000" size="42"></u-icon>
							<text class="count" style="color: #EE0000;">{{ item.collectionCount }}</text>
						</view>
						<view v-show="!item.isCollection" class="p-item" @click.stop="addCollection(item.id,index)">
							<u-icon name="thumb-up" color="#000000" size="42"></u-icon>
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
	import linfengAd from "../../components/linfeng-ad/linfeng-ad.vue"
	import storageConfig from '@/utils/storage-config.js'
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
					return; // 付费帖子在异步回调中跳转，直接返回
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
					return; // 付费帖子在异步回调中跳转，直接返回
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
					return; // 付费帖子在异步回调中跳转，直接返回
					}
					// #endif
				}

			// 只有非付费帖子才会执行到这里
			if (url) {
				uni.navigateTo({
					url: url
				})
			}
	}
	
	function toDiscuss(e) {
				uni.navigateTo({
					url: '/pages/discuss/detail?id=' + e
				});
	}
	
	function toUcenter(uid) {
				uni.navigateTo({
					url: '/pages/user/home?uid=' + uid
				});
			}
</script>

<style lang="scss" scoped>
	.quanzi {
		display: flex;
		justify-content: center;
		align-items: center;
		margin-left: 15rpx;
		font-size: 22rpx;
		padding: 0 20rpx;
		height: 40rpx;
		border: 1px solid #F5F6F5;
		border-radius: 50rpx;
		color: #9A9B9A;

		.txt {
			max-width: 140rpx;
			white-space: nowrap;
			/*设置不换行*/
			overflow: hidden;
			/*设置隐藏*/
			text-overflow: ellipsis;
			/*设置隐藏部分为省略号*/
		}

	}

	.time-index {
		font-size: 24rpx;
		color: #999999;
	}

	.desc-user {
		font-size: 18rpx;
		color: #999999;
	}

	.tag-discuss {
		color: #000000;
		margin-right: 8rpx;
		font-size: 600;
	}

	.middle-box {
		display: flex;
		align-items: center;
		margin-top: 28rpx;
	}

	.quan-txt {
		max-width: 120rpx;
		margin-left: 8rpx;
	}

	.post-item {
		background: #fff;
		padding: 40rpx 40rpx 30rpx 40rpx;
		margin-bottom: 8rpx;
		border-bottom: 1rpx solid #f0f0f0;

		.post-content {
			margin-top: 20rpx;

			.img-style-1 {
				display: block;
				width: 100%;
				max-height: 600rpx;
				border-radius: 5px;
				overflow: hidden;

			}

			.img-style-2 {
				display: flex;

				image {
					margin: 5rpx;
					border-radius: 5px;
					width: 100%;
					height: 294rpx;
				}
			}

			.img-style-3 {
				display: flex;
				flex-wrap: wrap;

				image {
					width: 31.3%;
					height: 200rpx;
					margin: 1%;
					border-radius: 5px;
				}
			}

			.img-style-4 {
				display: flex;
				flex-wrap: wrap;

				image {
					width: 48%;
					height: 320rpx;
					margin: 0.5%;

				}
			}
		}

		.address {
			display: flex;
			align-items: center;
			font-size: 22rpx;
			border-radius: 50rpx;
			padding: 5rpx 20rpx;
			border: 1rpx solid #F5F6F5;
			color: #9A9B9A;

			.icon {
				margin-right: 5rpx;
			}

			.txt {
				width: 160rpx;
				white-space: nowrap;
				/*设置不换行*/
				overflow: hidden;
				/*设置隐藏*/
				text-overflow: ellipsis;
				/*设置隐藏部分为省略号*/
			}
		}
	}


	.post-item-top-user {
		display: flex;
		align-items: center;

		.avatar {
			margin-right: 20rpx;
		}

		.center {
			flex: 1;
			display: flex;
			flex-direction: column;
			font-size: 24rpx;
			color: #999;

			.username {
				font-size: 30rpx;
				font-weight: 600;
				color: #2E2D2D;
				margin-top: -14rpx;
				margin-bottom: -4rpx;
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
				display: inline-block;
				font-size: 25rpx;
				color: #ffffff;
				background-color: #ff5d4e;
				padding: 5rpx 10rpx;
				border-radius: 10rpx;
				margin-left: auto;
			}

			.image {
				width: 0.60 * 100rpx;
				height: 0.28 * 100rpx;
				margin-left: 0.05 * 100rpx;
			}

			// .image3 {
			// 	width: 38rpx;
			// 	height: 38rpx;
			// 	margin-left: 7rpx;
			// 	margin-bottom: 8rpx;
			// }
		}

		.right {
			height: 85rpx;
			margin-left: 20rpx;

			.arrow-down {
				color: #999;
			}
		}
	}

	.post-text {
		font-size: 28rpx;
		display: block;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 10;
		white-space: pre-wrap;
		overflow: hidden;
		color: #000000;
	}

	.p-footer {
		display: flex;
		margin-top: 20rpx;
		justify-content: flex-end;

		.p-item {
			// margin: 0 auto;
			color: #616161;
			display: flex;
			align-items: center;


			.count {
				margin-left: 19rpx;
				font-size: 24rpx;
				color: #000;
			}
		}

		.p-item[hidden] {
			display: none !important;
		}
	}

	.comment-wrap {
		font-size: 28rpx;
		padding-top: 20rpx;
		border-top: 1px solid #F5F5F5;

		.item {
			.name {
				color: #000;
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

		.icon {
			width: 100rpx;
			height: 100rpx;
			z-index: 99;
		}

		image {
			position: absolute;
		}
	}

	

	// 投票
	.vote-box {
		background-color: #F5F5F5;
		border-radius: 20rpx;
		padding: 20rpx;
		margin: 20rpx;

		.title {
			font-weight: bold;
		}

		.expire-time {
			font-size: 24rpx;
			margin: 20rpx 0;
		}

		.vote-item {
			font-size: 24rpx;
			font-weight: bold;
			padding: 20rpx;
			border-radius: 20rpx;
			border: 1px solid #999;
			text-align: center;
			margin-bottom: 20rpx;

			&:last-child {
				margin-bottom: 0;
			}
		}
	}

	.other-img-flex {
		display: flex;
		align-items: center;
		position: relative;
		margin: 10rpx 0;
		width: 674rpx;

		.wrap-style .show-flex {
			width: 100%;
			height: 100%;
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

	.two-img .wrap-style-1 .show-flex {
		border-radius: 12rpx 0 0 12rpx;
	}

	.two-img .wrap-style-2 .show-flex {
		border-radius: 0 12rpx 12rpx 0;
	}

	.three-img {
		justify-content: space-between;
		flex-wrap: wrap;
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

	.three-img .wrap-style-1 .show-flex {
		border-radius: 12rpx 12rpx 0 0;
	}

	.three-img .wrap-style-2 .show-flex {
		border-radius: 0 0 0 12rpx;
	}

	.three-img .wrap-style-3 .show-flex {
		border-radius: 0 0 12rpx 0;
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