<template>
	<view class="article-container">
		<view class="content">
			<view class="detail">
				<view class="title">{{ articleDetail.title }}</view>
				<view class="info">
					<view class="author-info" @click="jumpUser(articleDetail.userInfo.uid)">
						<text class="username">{{ articleDetail.userInfo.username }}</text>
						<text class="date hidden">{{ timeFormat(articleDetail.createTime) }}</text>
					</view>
					<view class="read">浏览 {{ articleDetail.readCount }}</view>
				</view>
				<!-- 圈子信息 -->
				<view class="topic-info" @click="toTopicDetail">
					<image mode="aspectFill" class="cover" :src="articleDetail.topicInfo.coverImage"></image>
					<view class="center">
						<view class="q-desc">{{ articleDetail.topicInfo.topicName.substring(0, 12) }}</view>
						<view class="count-txt">{{ numberFormat(articleDetail.topicInfo.userCount) }}位成员 /
							{{ numberFormat(articleDetail.topicInfo.postCount) }}篇作品
						</view>
					</view>
					<view class="right">
						<text>去看看</text>
						<u-icon name="arrow-right-double"></u-icon>
					</view>
				</view>
				<view class="desc">
					<mp-html :content="articleDetail.content" />
				</view>
				<!-- 打赏模块 -->
				<view class="reward-box" v-if="rewardBtn=='1' && !articleDetail.isAuthor">
					<view @click="openRewardBtn">
						<image mode="aspectFill" :src="$IMG+'/images/reward.png'"></image>
						<text>积分打赏</text>
					</view>
				</view>
			</view>

			<view class="float-empty"></view>
			<view class="comment-bar">
				<view class="input" @tap="openCommentPop"><text>发表评论</text></view>
				<view class="operate">
					<view class="info" hover-class="none" @click="openCommentPop">
						<image src="/static/images/comment.png"></image>
						<text class="count"
							v-if="articleDetail.commentCount > 0">{{ articleDetail.commentCount }}</text>
					</view>

					<view class="like">
						<image @tap="addCollection()" v-show="!articleDetail.isCollection"
							src="/static/images/unlike.png">
						</image>
						<image @tap="cancelCollection()" v-show="articleDetail.isCollection"
							src="/static/images/like.png">
						</image>
						<text class="count"
							v-if="articleDetail.collectionCount > 0">{{ articleDetail.collectionCount }}</text>
					</view>
					<button class="share" @click="share">
						<image src="/static/images/share-article.png"></image>
					</button>

				</view>
			</view>
			<!-- 评论弹窗 -->
			<view>
				<view @click="onMask" v-show="commentPopup" class="mask"></view>
				<view v-show="commentPopup" class="popup-wrap">
					<scroll-view scroll-y style="height: 45vh;" @scrolltolower="reachBottom">
						<view class="comment-box">
							<view class="title">评论（{{ articleDetail.commentCount }}）</view>
							<view style="margin-bottom: 40rpx;" v-for="(item, index) in commentList" :key="index">
								<!-- 评论列表 -->
								<view class="comment-item" @longpress="delComment(item, index)">
									<image @click="jumpUser(item.userInfo.uid)" class="avatar"
										:src="item.userInfo.avatar">
									</image>
									<view class="c-content" @click="onReply(item)">
										<view class="user">
											<text style="color: #999;">{{ item.userInfo.username }}</text>
											<block v-if="item.isThumbs">
												<view @click.stop="cancelThumbs(item.id, index)" class="thumbs">
													<text class="num">{{ item.thumbs }}</text>
													<u-icon class="icon" size="40" name="heart-fill" color="#e62e00">
													</u-icon>
												</view>
											</block>
											<block v-else>
												<view @click.stop="onThumbs(item.id, index)" class="thumbs">
													<text class="num">{{ item.thumbs }}</text>
													<u-icon class="icon" size="40" name="heart-fill" color="#bfbfbf">
													</u-icon>
												</view>
											</block>
										</view>

										<mp-html class="count-txt" :content="item.content" selectable="true" />
										<image style="width: 200rpx; margin-top: 20rpx; border-radius: 16rpx"
											mode="widthFix" v-if="item.img&&item.img !== ''" :src="item.img"
											@click.stop="commentPreviewImage(item.img)" />
										<text class="time">{{ timeFormat(item.createTime) }}</text>
									</view>
								</view>
								<template v-if="item.children!=null&&item.children.length > 0">
									<view @click="onReply(item2, index, index2)"
										v-for="(item2, index2) in item.children" :key="item2.id"
										@longpress="delComment(item2, index, index2)" class="sub-comment-item">
										<view class="user">
											<u-avatar class="avatar" :size="40" :src="item2.userInfo.avatar"></u-avatar>
											<view class="u-head">
												<text style="color: #999;">{{ item2.userInfo.username }}</text>
												<block v-if="item2.isThumbs">
													<view class="thumbs"
														@click.stop="cancelThumbs(item2.id, index, index2)">
														<text class="num">{{ item2.thumbs }}</text>
														<u-icon class="icon" size="40" name="heart-fill"
															color="#e62e00">
														</u-icon>
													</view>
												</block>
												<block v-else>
													<view class="thumbs"
														@click.stop="onThumbs(item2.id, index, index2)">
														<text class="num">{{ item2.thumbs }}</text>
														<u-icon class="icon" size="40" name="heart-fill"
															color="#bfbfbf">
														</u-icon>
													</view>
												</block>
											</view>
										</view>
										<view class="reply">
											<view>
												<text>@</text>
												<navigator :url="'/pages/user/home?uid=' + item2.toUser.uid"
													hover-class="none" class="name">
													{{ item2.toUser.username }}
												</navigator>

												：<mp-html class="count-txt" style="display: inline;"
													:content="item2.content" selectable="true" />
											</view>
											<image style="width: 200rpx; margin-top: 20rpx; border-radius: 16rpx"
												mode="widthFix" v-if="item2.img&&item2.img !== ''" :src="item2.img"
												@click.stop="commentPreviewImage(item2.img)" />
											<view class="time">{{ timeFormat(item2.createTime) }}</view>
										</view>
									</view>
								</template>
								<view class="more-comment-btn" v-if="item.childrenCount>2 && item.showBtn"
									@click.stop="remainComment(item,index)">展开剩余回复</view>
								<u-line margin="40rpx 0 0 110rpx" length="85%"></u-line>
							</view>
							<!-- 加载状态 -->
							<block v-if="commentList.length > 0">
								<view style="margin: 30rpx;">
									<u-loadmore :status="loadStatus" />
								</view>
							</block>
							<block v-else>
								<u-empty text="暂无评论" mode="message"></u-empty>
							</block>
							<view style="height: 100rpx;"></view>
						</view>
					</scroll-view>
					<!-- 评论输入框 -->
					<view class="comment-tool">
						<view class="wrap">
							<view style="height: 60rpx; display: flex; align-items: center">
								<input ref="lfinput" type="text" :placeholder="placeholder" class="comment-input"
									@blur="onBlur" @focus="focusEvent" v-model="form.content" confirm-type="send"
									@confirm="addComment" />
								<image src="/static/images/img-icon.svg" style="width: 50rpx; height: 50rpx"
									@click="commentImgUpload"></image>
							</view>
							<u-button @click="addComment" :disabled="isSubmitD">
								发送
							</u-button>
						</view>
						<view v-if="commentTempImgUrl" class="img-wrap">
							<image :src="commentTempImgUrl" class="comment-img" />
							<view class="mask" @click="commentPreviewImage(commentTempImgUrl)">
								预览
							</view>
							<view class="del" @click="deleteTempImage">×</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 分享选择弹窗 -->
			<lf-popup v-model="showShare" height="240rpx" :indexs='998'>
				<view class="share-wrap" @click="closeShare">
					<!-- #ifdef MP-WEIXIN -->
					<button open-type="share" class="share-item u-reset-button">
						<image src="/static/wechat.png"></image>
						<text>微信好友</text>
					</button>
					<!-- #endif -->
					<!-- #ifdef H5 || APP-PLUS -->
					<view class="share-item2" @click="copyPageUrl">
						<image src="/static/images/share.png"></image>
						<text>分享链接</text>
					</view>
					<!-- #endif -->
					<!-- #ifdef H5 || MP-WEIXIN -->
					<view class="share-item" @click="shareCanvas" v-if="userIsLogin">
						<image src="/static/images/share2.png"></image>
						<text>分享海报</text>
					</view>
					<!-- #endif -->
				</view>
			</lf-popup>
			<!-- 分享海报弹窗-->
			<u-popup v-model="showCanvas" mode="center" width="80%">
				<view class="share-box">
					<image :src="posterUrl" class="images"></image>
				</view>
				<view class="footer">
					<u-button :custom-style="shareBtnStyle" @click="saveImg" type="success"
						shape="circle">保存分享</u-button>
				</view>
			</u-popup>
			<!-- 打赏弹框 -->
			<u-popup v-model="showReward" mode="center">
				<view class="show-reward">
					<image class="show-reward-image" @click="jumpUser(articleDetail.uid)"
						:src="articleDetail.userInfo.avatar" />
					<text
						style="font-weight: 600;font-size:30rpx;margin-bottom: 20rpx;">{{articleDetail.userInfo.username}}</text>

					<view class="show-reward-view">
						<view @tap.stop.prevent="changeRewardSelect" :class="rewardCount == 5 ? 'check-it' : ''"
							data-price="5">$5</view>
						<view @tap.stop.prevent="changeRewardSelect" :class="rewardCount == 20 ? 'check-it' : ''"
							data-price="20">$20</view>
						<view @tap.stop.prevent="changeRewardSelect" :class="rewardCount == 50 ? 'check-it' : ''"
							data-price="50">$50</view>
						<view @tap.stop.prevent="changeRewardSelect" :class="rewardCount == 100 ? 'check-it' : ''"
							data-price="100">$100</view>
					</view>
					<view class="show-reward-pop">
						<text>$</text>
						<input class="show-input-pop" @input="rewardChange" :value="rewardCount"
							placeholder-class="count-input" type="number" placeholder="请输入打赏积分" />
					</view>
					<view class="show-reward-btn" @tap.stop.prevent="submitReward">确认打赏</view>
				</view>
			</u-popup>
		</view>
		<!-- 自定义加载 -->
		<toast color="#fff" type="rotate3"></toast>
	</view>
</template>

<script setup>
	import {
		ref,
		reactive,
		getCurrentInstance
	} from 'vue'
	import {
		onLoad,
		onShareAppMessage,
		onShareTimeline
	} from '@dcloudio/uni-app'
	import {
		timeFormat,
		numberFormat
	} from "@/utils/filters.js";

	const {
		proxy
	} = getCurrentInstance();
	const $H = proxy.$H;
	const $c = proxy.$c;
	const $u = proxy.$u;
	const $f = proxy.$f;
	const $loading = proxy.$loading;
	const $IMG = ref(proxy.$IMG);

	const id = ref(0);
	const articleDetail = reactive({
		comment: [],
		media: [],
		commentList: {
			data: []
		},
		topicInfo: {
			topicName: ''
		},
		userInfo: {},
		voteInfo: {},
		content: ''
	});
	const commentPopup = ref(false);
	const commentList = ref([]);
	const focus = ref(false);
	const placeholder = ref('说点什么...');
	const page = ref(1);
	const form = reactive({
		pid: 0,
		type: 1,
		toUid: '',
		postId: '',
		content: '',
		commentImg: ''
	});
	const loadStatus = ref('loadmore');
	const isSubmitD = ref(false);
	const showShare = ref(false);
	const showCanvas = ref(false);
	const shareBtnStyle = reactive({
		backgroundColor: '#333'
	});
	const posterUrl = ref("");
	const showReward = ref(false);
	const rewardCount = ref('');
	const rewardBtn = ref('0');
	const commentTempImgUrl = ref('');
	const userIsLogin = ref(false);

	function getArticleDetail() {
		$loading(true);
		$H.get('post/detail', {
			id: id.value
		}).then(res => {
			if (res.code == 500) {
				$loading(false);
				uni.hideLoading();
				$u.toast(res.msg);
				setTimeout(function() {
					uni.switchTab({
						url: '/pages/index/index'
					});
				}, 1500);
			} else {
				if (res.result.type != 3) {
					uni.redirectTo({
						url: '/pages/post/detail?id=' + id.value
					})
				}
				Object.assign(articleDetail, res.result);
				rewardBtn.value = res.result.rewardBtn;

				// #ifdef H5
				articleDetail.content = $f.formatText(res.result.content)
				// #endif

				// #ifdef MP-WEIXIN || APP-PLUS
				articleDetail.content = res.result.content || ''
				// #endif

				uni.setNavigationBarTitle({
					title: articleDetail.title
				});
				uni.setNavigationBarColor({
					frontColor: '#ffffff',
					backgroundColor: '#ACA3E0',
					animation: {
						duration: 400,
						timingFunc: 'easeIn'
					}
				})
				$loading(false);
			}
		});
	}

	function cancelCollection() {
		$H.post('post/cancelCollection', {
			id: id.value
		}).then(res => {
			if (res.code === 0) {
				articleDetail.isCollection = false;
				articleDetail.collectionCount--;
			}
		});
	}

	function addCollection() {
		$H.post('post/addCollection', {
			id: id.value,
			uid: articleDetail.uid
		}).then(res => {
			if (res.code === 0) {
				articleDetail.isCollection = true;
				articleDetail.collectionCount++;
			}
		});
	}

	function focusEvent() {}

	function openCommentPop() {
		commentPopup.value = true
	}

	function toTopicDetail() {
		uni.navigateTo({
			url: '/pages/topic/detail?id=' + articleDetail.topicId
		});
	}

	function delComment(e, index, index2) {
		let user = uni.getStorageSync("userInfo");
		if (e.uid != user.uid) {
			return;
		}
		uni.showModal({
			title: '提示',
			content: '确定删除该评论？',
			success: function(res) {
				if (res.confirm) {
					$H.post('comment/del', {
						id: e.id
					}).then(res2 => {
						if (res2.code == 0) {
							if (index2 || index2 === 0) {
								commentList.value[index].children.splice(index2, 1);
							} else {
								commentList.value.splice(index, 1);
							}
							articleDetail.commentCount--;
						}
					});
				}
			}
		});
	}

	function reachBottom() {
		page.value++;
		getCommentList();
	}

	function jumpUser(uid) {
		uni.navigateTo({
			url: '/pages/user/home?uid=' + uid
		});
	}

	function getCommentList() {
		loadStatus.value = 'loading';
		$H.get('comment/list', {
			postId: id.value,
			page: page.value
		}).then(res => {
			if (res.code == 0) {
				// #ifdef H5
				res.result.data.forEach(item => {
					item.content = $f.formatText(item.content)
					item.children.forEach(item2 => {
						item2.content = $f.formatText(item2.content)
					})
				})
				// #endif

				// #ifdef MP-WEIXIN || APP-PLUS
				res.result.data.forEach(item => {
					item.content = item.content || ''
					item.children.forEach(item2 => {
						item2.content = item2.content || ''
					})
				})
				// #endif
				commentList.value = commentList.value.concat(res.result.data);

				if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
					loadStatus.value = 'nomore';
				} else {
					loadStatus.value = 'loadmore';
				}
			}
		});
	}

	function onThumbs(id, index, index2) {
		$H.post('comment/thumbs', {
			id: id
		}).then(res => {
			if (res.code == 0) {
				if (index2 || index2 == 0) {
					commentList.value[index].children[index2].isThumbs = true;
					commentList.value[index].children[index2].thumbs++;
				} else {
					commentList.value[index].isThumbs = true;
					commentList.value[index].thumbs++;
				}
			}
		});
	}

	function cancelThumbs(id, index, index2) {
		$H.post('comment/cancelThumbs', {
			id: id
		}).then(res => {
			if (res.code == 0) {
				if (index2 || index2 == 0) {
					commentList.value[index].children[index2].isThumbs = false;
					commentList.value[index].children[index2].thumbs--;
				} else {
					commentList.value[index].isThumbs = false;
					commentList.value[index].thumbs--;
				}
			}
		});
	}

	function onReply(e) {
		placeholder.value = '回复：' + e.userInfo.username;
		focus.value = true;
		let pid = e.pid;
		if (pid === 0) {
			form.pid = e.id;
		} else {
			form.pid = e.pid;
		}
		form.toUid = e.userInfo.uid;
		form.postId = id.value;
	}

	function onBlur() {
		placeholder.value = '说点什么...';
		focus.value = false;
	}

	function commentImgUpload() {
		uni.chooseImage({
			count: 1,
			sizeType: ['original', 'compressed'],
			sourceType: ['album'],
			success: function(res) {
				uni.showLoading({
					mask: true,
					title: "上传中"
				})
				uni.uploadFile({
					url: $c.domain + 'common/upload',
					filePath: res.tempFilePaths[0],
					name: 'Image',
					header: {
						token: uni.getStorageSync("token")
					},
					success: (uploadFileRes) => {
						let data = JSON.parse(uploadFileRes.data)
						if (data.code == 0) {
							commentTempImgUrl.value = data.result
							form.commentImg = data.result
							uni.hideLoading();
						}
					}
				});
			}
		});
	}

	function deleteTempImage() {
		commentTempImgUrl.value = ''
		form.commentImg = ''
	}

	function commentPreviewImage(url) {
		uni.previewImage({
			urls: [url]
		})
	}

	function addComment() {
		isSubmitD.value = true;
		if (form.content == '') {
			$u.toast('评论不能为空');
			isSubmitD.value = false;
			return;
		}
		form.postId = id.value;
		uni.showLoading({
			mask: true,
			title: '发布中'
		});
		$H.post('post/addComment', form).then(res => {
			uni.hideLoading();
			if (res.code == 0) {
				if (res.result) {
					uni.showModal({
						title: '提示',
						content: '评论审核通过后发布哦，请耐心等待',
						showCancel: false,
						success: function(res) {
							if (res.confirm) {}
						}
					});
				} else {
					$u.toast('评论成功');
				}
				form.content = '';
				page.value = 1;
				commentList.value = [];
				form.commentImg = '';
				form.pid = 0;
				commentTempImgUrl.value = '';
				getCommentList();
				articleDetail.commentCount++;
			} else if (res.code == 500) {
				$u.toast(res.msg);
			}
			isSubmitD.value = false;
		});
	}

	function closeShare() {
		showShare.value = false
	}

	function copyPageUrl() {
		uni.setClipboardData({
			data: $c.shareH5Url + 'subpages/content/article/article?id=' + id.value,
			success: function() {
				uni.hideToast();
				$f.toast('链接复制成功', 'none');
				showShare.value = false;
			}
		});
	}

	function saveImg() {
		// #ifdef MP-WEIXIN
		uni.getImageInfo({
			src: posterUrl.value,
			success: function(image) {
				uni.saveImageToPhotosAlbum({
					filePath: image.path,
					success: function() {
						uni.showToast({
							title: '图片保存成功'
						});
					},
					fail: function() {
						uni.showModal({
							title: '图片保存失败',
							content: '请确认是否已开启授权',
							confirmText: '开启授权',
							success(res) {
								if (res.confirm) {
									uni.openSetting({
										success(settingdata) {
											if (settingdata.authSetting[
													"scope.writePhotosAlbum"]) {
												uni.showToast({
													title: '授权成功，请重试哦~',
													icon: "none"
												});
											} else {
												uni.showToast({
													title: '请确定已打开保存权限',
													icon: "none"
												});
											}
										}
									})
								}
							}
						})
					},
				});
			},
			fail() {}
		});
		// #endif
		// #ifdef H5
		var oA = document.createElement('a');
		oA.download = '';
		oA.href = posterUrl.value;
		document.body.appendChild(oA);
		oA.click();
		oA.remove();
		// #endif
	}

	function shareCanvas() {
		showCanvas.value = true;
		uni.showLoading({
			mask: true,
			title: '正在生成海报'
		});
		let shareOrigin;
		let shareUrl;
		// #ifdef H5
		shareOrigin = "h5";
		shareUrl = $c.shareH5Url + "subpages/content/article/article?id=" + id.value;
		// #endif
		// #ifdef MP-WEIXIN
		shareOrigin = "weixin";
		shareUrl = "subpages/content/article/article?id=" + id.value;
		// #endif
		$H.get('post/qrCode', {
			postId: id.value,
			origin: shareOrigin,
			url: shareUrl
		}).then(res => {
			if (res.code == 0) {
				posterUrl.value = res.result;
			} else {
				showCanvas.value = false;
			}
			uni.hideLoading();
		});
	}

	function share() {
		showShare.value = true
	}

	function isInteger(obj) {
		return obj % 1 === 0;
	}

	function changeRewardSelect(value) {
		rewardCount.value = value.currentTarget.dataset.price
	}

	function rewardChange(value) {
		rewardCount.value = value.detail.value
	}

	function openRewardBtn() {
		showReward.value = true
		rewardCount.value = ''
	}

	function submitReward() {
		if (!uni.getStorageSync('hasLogin')) {
			$u.toast("请先登录");
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
		if (rewardCount.value < 0) {
			$u.toast("输入数值违法");
			return;
		}
		if (!isInteger(rewardCount.value)) {
			$u.toast('必须为整数');
			return;
		}
		showReward.value = false
		uni.showModal({
			title: '提醒',
			content: '打赏后不可撤回，你确定给该帖子作者打赏' + rewardCount.value + '积分吗',
			success: function(res) {
				if (res.confirm) {
					$H.post('bill/reward', {
						rewardCount: rewardCount.value,
						postId: id.value
					}).then(res => {
						if (res.code == 0) {
							showReward.value = false
							$u.toast('打赏成功');
						}
					});
				}
			}
		});
	}

	function remainComment(item, index) {
		$H.get('comment/remainComment', {
			id: item.id
		}).then(res => {
			if (res.code == 0) {
				commentList.value[index].children = commentList.value[index].children.concat(res.result);
				commentList.value[index].showBtn = false
			}
		});
	}

	function onMask() {
		commentPopup.value = false;
	}

	onLoad((options) => {
		if (options.id) {
			id.value = options.id
		}
		getArticleDetail();
		getCommentList();
		let userInfos = uni.getStorageSync('userInfo');
		if (userInfos) {
			userIsLogin.value = true
		}
		//#ifdef MP-WEIXIN
		wx.showShareMenu({
			withShareTicket: true,
			menus: ['shareAppMessage', 'shareTimeline']
		});
		//#endif
	});

	onShareAppMessage(() => {
		let imgURL;
		if (articleDetail.media.length > 0) {
			imgURL = articleDetail.media[0];
		}
		return {
			title: articleDetail.title,
			path: '/subpages/content/article/article?id=' + id.value
		};
	});

	onShareTimeline(() => {
		let imgURL = articleDetail.media[0];
		return {
			title: articleDetail.title,
			query: 'id=' + id.value
		};
	});
</script>

<style lang="scss" scoped>
	.article-container {
		height: 100%;
		background: #ffffff;
	}

	.content {
		position: relative;
	}

	.detail {
		padding: 40rpx 32rpx;
		background: #fff;

		.title {
			font-size: 44rpx;
			color: #262626;
			line-height: 70rpx;
			font-weight: bold;
		}

		.info {
			margin-top: 32rpx;
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			align-items: center;

			.author-info {
				.username {
					color: #666;
				}

				.date {
					color: #999;
					margin-left: 24rpx;
				}
			}

			.read {
				color: #999;
			}
		}

		.desc {
			margin-top: 56rpx;
			overflow: hidden;
			color: #262626;

			.wxParse {
				color: #262626;
			}
		}
	}

	.comment-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		width: 100%;
		height: 120rpx;
		background-color: #ffffff;
		box-shadow: 0 1px 1px 1px rgba(0, 0, 0, 0.3);
		display: flex;
		flex-direction: row;

		.input {
			flex-grow: 0;
			flex-shrink: 0;
			margin: 20rpx 10rpx 20rpx 20rpx;
			display: flex;
			align-items: center;
			background: #f2f2f2;
			width: 420rpx;
			border-radius: 40rpx 40rpx 0 40rpx;

			text {
				font-size: 32rpx;
				color: #aaa;
				margin-left: 38rpx;
			}
		}

		.operate {
			width: 100%;
			flex-grow: 1;
			flex-shrink: 1;
			display: flex;
			justify-content: space-around;
			align-items: center;
			margin-right: 10rpx;

			view,
			navigator {
				display: flex;
				justify-content: space-around;
				align-items: center;
				width: 33%;

				image {
					width: 45rpx;
					height: 45rpx;
				}
			}

			.info,
			.like {
				position: relative;

				.count {
					position: absolute;
					top: -15rpx;
					right: -4rpx;
					background-image: linear-gradient(140deg, #aaaaff 9%, #aa55ff 75%);
					display: flex;
					align-items: center;
					border-radius: 50rpx;
					padding: 0 10rpx;
					font-size: 22rpx;
					color: #fff;
					height: 30rpx;
					line-height: 30rpx;
				}
			}

			.share {
				display: flex;
				justify-content: space-around;
				padding: 0;
				background: transparent;

				image {
					width: 45rpx;
					height: 45rpx;
					border: 0;
				}

				&:after {
					display: none;
				}
			}

		}
	}

	.float-empty {
		height: 120rpx;
		width: 100%;
		display: block;
		background: #fff;
	}

	/*评论列表*/
	.comment-box {
		background-color: #ffffff;
		margin-top: 20rpx;
	}

	.comment-box>.title {
		margin: 20rpx;
	}

	.comment-item {
		display: flex;
		padding: 20rpx;

		&:active {
			background-color: #F5F5F5;
		}

		.c-content {
			display: flex;
			flex-direction: column;
			flex: 1;

			.time {
				color: #999;
				font-size: 10px;
			}

			.user {
				display: flex;

				.thumbs {
					margin-left: auto;
					display: flex;
					align-items: center;
					color: #bfbfbf;

					.num {
						margin-right: 10rpx;
					}
				}
			}
		}

		.avatar {
			width: 100rpx;
			height: 100rpx;
			border-radius: 50%;
			margin-right: 20rpx;
		}
	}

	.sub-comment-item {
		margin-left: 100rpx;
		padding: 20rpx;

		&:active {
			background-color: #F5F5F5;
		}

		.user {
			display: flex;
			align-items: center;

			.name {
				color: #616161;
			}

			.avatar {
				margin-right: 10rpx;
			}

			.u-head {
				flex: 1;
				display: flex;

				.thumbs {
					margin-left: auto;
					display: flex;
					align-items: center;
					color: #bfbfbf;

					.num {
						margin-right: 10rpx;
					}
				}
			}
		}

		.reply {
			.time {
				margin-left: auto;
				font-size: 20rpx;
				color: #999;
			}

			.name {
				display: inline-block;
				color: #2b85e4;
				font-weight: 600;
			}
		}
	}

	/* 评论tool */
	.comment-tool {
		position: fixed;
		bottom: 0;
		width: 100%;
		background-color: #fff;
		padding: 20rpx;
		display: flex;
		z-index: 99;

		.wrap {
			padding: 3px;
			padding-left: 10px;
			display: flex;
			background-color: #f5f5f5;
			border-radius: 20px;

			input {
				width: 480rpx !important;
			}
		}

		.img-wrap {
			// position: relative;
			position: absolute;
			bottom: 120rpx;
			width: 80rpx;
			height: 80rpx;
			margin: 20rpx;
			margin-bottom: 0;
			color: #fff;

			.comment-img {
				border-radius: 8rpx;
				width: 90rpx;
				height: 90rpx;
			}

			.mask {
				border-radius: 8rpx;
				z-index: 10;
				position: absolute;
				left: 0;
				top: 0;
				width: 100%;
				height: 100%;
				background-color: rgba(0, 0, 0, 0.2);
				font-size: 24rpx;
				display: flex;
				align-items: flex-end;
				justify-content: center;
			}

			.del {
				z-index: 10;
				position: absolute;
				right: -10rpx;
				top: -10rpx;
				width: 30rpx;
				height: 30rpx;
				border-radius: 30rpx;
				background-color: rgba(0, 0, 0, 0.5);
				display: flex;
				align-items: center;
				justify-content: center;
				font-size: 24rpx;
			}
		}
	}

	.comment-tool textarea {
		padding: 20rpx;
		border-radius: 10rpx;
		min-height: 40rpx;
	}

	.comment-tool .u-btn {
		margin-left: 10rpx;
	}

	// 分享弹窗
	.share-wrap {
		display: flex;
		padding: 30rpx;
		width: 45%;
		margin: 0 auto;

		.share-item {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;

			&:nth-child(1) {
				margin-right: auto;
			}

			image {
				width: 100rpx;
				height: 100rpx;
			}

			text {
				font-size: 24rpx;
				margin-top: 20rpx;
			}
		}

		.share-item2 {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;

			&:nth-child(1) {
				margin: auto;
			}

			image {
				width: 100rpx;
				height: 100rpx;
			}

			text {
				font-size: 24rpx;
				margin-top: 20rpx;
			}
		}
	}

	//海报弹窗
	.share-box {
		height: 530px;
		position: relative;

		.images {
			width: 100%;
			height: 100%;
		}

	}

	// 圈子信息
	.topic-info {
		display: flex;
		flex-direction: row;
		align-items: center;
		font-size: 24rpx;
		background-color: #F1F3F5;
		margin: 20rpx 0;
		padding: 24rpx;
		border-radius: 12rpx;
		cursor: pointer;

		.cover {
			width: 100rpx;
			height: 100rpx;
			margin-right: 20rpx;
			flex-shrink: 0;
			border-radius: 12rpx;
		}

		.center {
			flex: 1;
			min-width: 0;
			overflow: hidden;

			.q-desc {
				font-size: 28rpx;
				font-weight: 600;
				margin-bottom: 8rpx;
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
			}

			.count-txt {
				font-size: 24rpx;
				color: #999;
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
			}
		}

		.right {
			margin-left: 20rpx;
			color: #55aaff;
			display: flex;
			align-items: center;
			flex-shrink: 0;
			white-space: nowrap;

			text {
				margin-right: 4rpx;
			}
		}
	}

	.mask {
		position: fixed;
		top: 0;
		bottom: 0;
		left: 0;
		right: 0;
		z-index: 99;
		background-color: rgba(0, 0, 0, 0.3);
	}

	.popup-wrap {
		width: 100%;
		position: fixed;
		bottom: 0;
		background-color: #ffffff;
		animation-name: mymove;
		animation-duration: 0.3s;
		border-radius: 20rpx 20rpx 0 0;
		z-index: 998;
		height: 50vh;
	}

	@keyframes mymove {
		from {
			bottom: -50vh;
		}

		to {
			bottom: 0;
		}
	}
</style>