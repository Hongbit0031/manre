<template>
	<view>
		<view class="info-box">
			<view v-if="showType=='1'&&postDetail.media&&postDetail.media.length>0&&postDetail.type == 1">
				<u-swiper :list="postDetail.media" :height="imgHeight" img-mode="aspectFit" indicator-pos="bottomRight"
					:effect3d="true" @click="showImage" @change="changeSwiper"></u-swiper>
			</view>
			<u-line v-if="showType=='1'&&postDetail.media&&postDetail.media.length>0&&postDetail.type == 1"
				margin="28rpx 0rpx"></u-line>
			<view :class="'user-item-'+showType">
				<image class="avatar-z" @click="jumpUser(postDetail.uid)" :src="postDetail.userInfo.avatar"></image>
				<view class="user-item-user">
					<view class="diyTag">
						<text class="user-name">{{ postDetail.userInfo.username }}</text>
						<image src="@/static/vip/vipIcon.png" class="image3" v-if="postDetail.userInfo.vip==1" />
						<image style="height: 22rpx;margin-left: 10rpx ;" mode="heightFix"
							v-if="postDetail.userInfo.level" :src="$IMG+'/vip/level_'+postDetail.userInfo.level+'.png'"
							class="level-img-2" />
					</view>
					<view class="postIntro">{{ postDetail.userInfo.intro }}</view>
				</view>
				<view v-if="!postDetail.isAuthor && postDetail.isFollow" class="followStyle1" @click="cancelFollow">
					已关注
				</view>
				<view v-if="!postDetail.isAuthor && !postDetail.isFollow" class="followStyle" @click="follow">
					关注
				</view>
				<view v-if="postDetail.isAuthor" class="postStatus">
					<!-- <text v-if="postDetail.status==0">正常</text> -->
					<text v-if="postDetail.status==1">待审核</text>
					<text v-if="postDetail.status==2">已下架</text>
				</view>
			</view>
			<view class="icon">
				<text>{{ timeFormat(postDetail.createTime) }}</text>
			</view>
			<view class="hr"></view>
			<view class="post-title">{{ postDetail.title }}</view>
			<!-- 付费贴简介 -->
			<view class="post-text" v-if="postDetail.cut==1">
				<mp-html :content="postDetail.brief" selectable="true" />
			</view>
			<!-- 帖子内容 -->
			<view class="post-text">
				<mp-html :content="postDetail.content" selectable="true" />
			</view>
			<!-- 图片 -->
			<block v-if="postDetail.type == 1 && showType=='0'">
				<!--一张图片-->
				<block v-if="postDetail.media.length == 1">
					<image class="img-style-1" @tap.stop="previewImage" mode="aspectFill"
						:data-current="postDetail.media[0]" :data-image="postDetail.media" :src="postDetail.media[0]">
					</image>
				</block>
				<!--二张图片-->
				<block v-else-if="postDetail.media.length == 2">
					<view class="img-style-2">
						<image v-for="(mediaItem, index2) in postDetail.media" :key="index2" @tap.stop="previewImage"
							mode="aspectFill" :data-current="mediaItem" :data-image="postDetail.media" :src="mediaItem">
						</image>
					</view>
				</block>
				<!--四张图-->
				<block v-else-if="postDetail.media.length == 4">
					<view class="img-style-4">
						<image v-for="(mediaItem, index2) in postDetail.media" :key="index2" @tap.stop="previewImage"
							mode="aspectFill" :data-current="mediaItem" :data-image="postDetail.media" :src="mediaItem">
						</image>
					</view>
				</block>
				<!--其他张数图片-->
				<block v-else>
					<view class="other-img-flex" :class="imageClass[postDetail.media.length]">
						<view :class="'wrap-style ' + 'wrap-style-' + (imageIndex + 1)"
							v-for="(image, imageIndex) in postDetail.media" :key="imageIndex">
							<image class="show-flex" mode="aspectFill" @tap.stop="previewImage" :data-current="image"
								:data-image="postDetail.media" :src="image"></image>
						</view>
					</view>
				</block>
			</block>
			<block v-if="postDetail.type == 2 && postDetail.media.length > 0">
				<video :controls="true" :autoplay="true" :loop="true" :src="postDetail.media[0]"></video>
			</block>
			<!-- 投票 -->
			<view v-if="postDetail.type === 4" class="vote-box">
				<view class="title">{{ postDetail.voteInfo.title }}</view>
				<view class="expire-time" v-if="postDetail.voteInfo.type === 1">单选</view>
				<view class="expire-time" v-if="postDetail.voteInfo.type === 2">多选</view>
				<!-- 是否投票失效 -->
				<view class="expire-box" v-if="isVoteExpire">投票已过期</view>
				<view v-else class="expire-time">截止：{{ postDetail.voteInfo.time }}</view>
				<view class="vote-item" @click="castVote(index2, postDetail.voteInfo.type)" :class="{ 
						active: item2.checked,
						'my-vote': postDetail.isVoteResult && isMyVoteOption(item2.id)
					}" v-for="(item2, index2) in postDetail.voteInfo.options" :key="index2">
					<text v-if="postDetail.isVoteResult || isVoteExpire">{{ item2.content }}<text
							style="color: #999;margin-left: 20rpx;">({{ item2.ticketNum }}票)</text></text>
					<text v-else>{{ item2.content }}</text>
				</view>
				<lf-button v-if="!postDetail.isVoteResult && isVoteExpire === false" @click="voteSubmit">投票</lf-button>
			</view>

			<!-- 话题 -->
			<view class="detail-tag" v-if="postDetail.discussId > 0 && postDetail.type == 1">
				<view @click="toDiscuss(postDetail.discussId)">
					<image mode="aspectFill" src="/static/images/topic.png"></image>
					<view>{{postDetail.discussName}}</view>
				</view>
			</view>
			<!-- 地址信息 -->
			<view v-if="postDetail.address" @click="openLocation" class="post-address">
				<view>
					<u-icon class="icon" name="map-fill"></u-icon>
					<view>{{postDetail.address}}</view>
				</view>
			</view>
			<!-- 圈子信息 -->
			<view class="topic-info" @click="toTopicDetail">
				<image mode="aspectFill" class="cover" :src="postDetail.topicInfo.coverImage"></image>
				<view class="center">
					<view class="desc">{{ postDetail.topicInfo.topicName }}</view>
					<view class="count-txt">{{ numberFormat(postDetail.topicInfo.userCount) }}位成员 /
						{{ numberFormat(postDetail.topicInfo.postCount) }}篇作品
					</view>
				</view>
				<view class="right">
					<text>去看看</text>
					<u-icon name="arrow-right-double"></u-icon>
				</view>
			</view>
			<!--点赞、分享、评论-->
			<view class="p-footer">
				<block v-if="postDetail.isCollection">
					<view class="p-item" @click="cancelCollection">
						<u-icon name="thumb-up-fill" size="40" color="#cc0000"></u-icon>
						<!-- <text class="iconfont icon-lujing" style="color: #d81e06;"></text> -->
						<text style="margin-left: 10rpx;">{{ postDetail.collectionCount }}</text>
					</view>
				</block>
				<block v-else>
					<view class="p-item" @click="addCollection">
						<!-- #ifdef APP-PLUS -->
						<text class="iconfont icon-shoucang"></text>
						<!-- #endif -->
						<!-- #ifdef H5 || MP-WEIXIN -->
						<u-icon name="thumb-up" size="40"></u-icon>
						<!-- #endif -->
						<text style="margin-left: 10rpx;">{{ postDetail.collectionCount }}</text>
					</view>
				</block>
				<view class="p-item" @click="focus = true">
					<text class="iconfont icon-pinglun"></text>
					<text>{{ postDetail.commentCount }}</text>
				</view>
				<!-- #ifdef MP -->
				<view class="p-item" @click="openShare()">
					<text class="iconfont icon-forward"></text>
					<text>分享</text>
				</view>
				<!-- #endif -->
				<!-- #ifdef H5 -->
				<view class="p-item" @click="showShare = true">
					<text class="iconfont icon-forward"></text>
					<text>分享</text>
				</view>
				<!-- #endif -->
				<!-- #ifdef APP-PLUS -->
				<view class="p-item">
					<text class="iconfont icon-yuedu"></text>
					<text>{{ postDetail.readCount }}</text>
				</view>
				<!-- #endif -->

			</view>
		</view>
		<!-- 中间的打赏模块 -->
		<view class="reward-box" v-if="rewardBtn=='1' && postDetail.type!=2 && !postDetail.isAuthor">
			<view @click="openRewardBtn">
				<image mode="aspectFill" :src="$IMG+'/images/reward.png'"></image>
				<text>积分打赏</text>
			</view>
		</view>
		<!-- 广告 -->
		<view>
			<linfeng-ad :isVip="isVip" :open="adIsOpen" :h5Adpid="h5Adpid" :wxAdpid="wxAdpid"></linfeng-ad>
		</view>

		<view class="comment-box">
			<view class="title">评论（{{ postDetail.commentCount }}）</view>
			<view style="margin-bottom: 40rpx;" v-for="(item, index) in commentList" :key="index">
				<view class="comment-item" @longpress="delComment(item, index)">
					<image @click="jumpUser(item.userInfo.uid)" class="avatar" :src="item.userInfo.avatar"></image>
					<view class="c-content" @click="onReply(item)">
						<view class="user">
							<text style="color: #999;">{{ item.userInfo.username }}</text>
							<text v-if="item.userInfo.uid == postDetail.userInfo.uid" class="official">作者</text>
							<image style="height: 22rpx;margin-left:10rpx ;margin-top: 20rpx;" mode="heightFix"
								v-if="item.userInfo.level" :src="$IMG+'/vip/level_'+item.userInfo.level+'.png'"
								class="level-img-2" />
							<image v-if="item.userInfo.vip==1" src="@/static/vip/vipIcon.png"
								style="width: 75rpx;height: 30rpx;margin-left: 7rpx;margin-top: 20rpx;"
								mode="heightFix" />

							<block v-if="item.isThumbs">
								<view @click.stop="cancelThumbs(item.id, index)" class="thumbs">
									<text class="num">{{ item.thumbs }}</text>
									<u-icon class="icon" size="40" name="thumb-up-fill" color="#e62e00"></u-icon>
								</view>
							</block>
							<block v-else>
								<view @click.stop="onThumbs(item.id, index)" class="thumbs">
									<text class="num">{{ item.thumbs }}</text>
									<u-icon class="icon" size="40" name="thumb-up-fill" color="#bfbfbf"></u-icon>
								</view>
							</block>
						</view>

						<mp-html class="count-txt" :content="item.content" selectable="true" />
						<image style="width: 200rpx; margin-top: 20rpx; border-radius: 16rpx" mode="widthFix"
							v-if="item.img&&item.img !== ''" :src="item.img"
							@click.stop="commentPreviewImage(item.img)" />
						<text class="time">{{ timeFormat(item.createTime) }}</text>
					</view>
				</view>
				<template v-if="item.children!=null&&item.children.length > 0">
					<view @longpress="delComment(item, index, index2)" @click="onReply(item2, index, index2)"
						v-for="(item2, index2) in item.children" :key="item2.id" class="sub-comment-item">
						<view class="user">
							<u-avatar class="avatar" :size="40" :src="item2.userInfo.avatar"></u-avatar>
							<view class="u-head">
								<text style="color: #999;">{{ item2.userInfo.username }}</text>
								<text v-if="item2.userInfo.uid == postDetail.userInfo.uid" class="official">作者</text>
								<image style="height: 22rpx;margin-left:10rpx ;margin-top: 20rpx;" mode="heightFix"
									v-if="item2.userInfo.level" :src="$IMG+'/vip/level_'+item2.userInfo.level+'.png'"
									class="level-img-2" />
								<image v-if="item2.userInfo.vip==1" src="../../static/vip/vipIcon.png"
									style="width: 75rpx;height: 30rpx;margin-left: 7rpx;margin-top: 20rpx;"
									mode="heightFix" />

								<block v-if="item2.isThumbs">
									<view class="thumbs" @click.stop="cancelThumbs(item2.id, index, index2)">
										<text class="num">{{ item2.thumbs }}</text>
										<u-icon class="icon" size="40" name="thumb-up-fill" color="#e62e00"></u-icon>
									</view>
								</block>
								<block v-else>
									<view class="thumbs" @click.stop="onThumbs(item2.id, index, index2)">
										<text class="num">{{ item2.thumbs }}</text>
										<u-icon class="icon" size="40" name="thumb-up-fill" color="#bfbfbf"></u-icon>
									</view>
								</block>
							</view>
						</view>
						<view class="reply">
							<view>
								<text>@</text>
								<navigator :url="'/pages/user/home?uid=' + item2.toUser.uid" hover-class="none"
									class="name">
									{{ item2.toUser.username }}
								</navigator>
								：<mp-html class="count-txt" style="display: inline;" :content="item2.content"
									selectable="true" />
							</view>
							<image style="width: 200rpx; margin-top: 20rpx; border-radius: 16rpx" mode="widthFix"
								v-if="item2.img&&item2.img !== ''" :src="item2.img"
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
			<block v-if="commentList!=null&&commentList.length > 0">
				<view style="margin: 30rpx;">
					<u-loadmore :status="loadStatus" />
				</view>
			</block>
			<block v-else>
				<u-empty text="暂无评论" mode="message"></u-empty>
			</block>
		</view>
		<view style="height: 100rpx;"></view>
		<!-- 评论输入框 -->
		<view class="comment-tool">
			<view class="wrap">
				<view style="height: 60rpx; display: flex; align-items: center">
					<input ref="lfinput" type="text" :placeholder="placeholder" class="comment-input" @blur="onBlur"
						@focus="focusEvent" v-model="form.content" confirm-type="send" @confirm="addComment" />
					<image src="/static/images/img-icon.svg" style="width: 40rpx; height: 40rpx"
						@click="commentImgUpload"></image>
				</view>
				<u-button @click="addComment" :disabled="isSubmitD" :custom-style="btnStyle" type="success">
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
		<!-- 分享选择弹窗 -->
		<lf-popup v-model="showShare" height="240rpx">
			<view class="share-wrap" @click="closeShare">
				<!-- #ifdef MP-WEIXIN -->
				<button open-type="share" class="share-item-wx u-reset-button">
					<image src="/static/wechat.png"></image>
					<text>微信好友</text>
				</button>
				<!-- #endif -->
				<!-- #ifdef H5 -->
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
				<u-button :custom-style="shareBtnStyle" @click="saveImg" type="success" shape="circle">保存分享</u-button>
			</view>
		</u-popup>
		<!-- 打赏弹框 -->
		<u-popup v-model="showReward" mode="center">
			<view class="show-reward">
				<image class="show-reward-image" @click="jumpUser(postDetail.uid)" :src="postDetail.userInfo.avatar" />
				<text
					style="font-weight: 600;font-size:30rpx;margin-bottom: 20rpx;">{{postDetail.userInfo.username}}</text>

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
		onReachBottom,
		onShareAppMessage,
		onShareTimeline
	} from '@dcloudio/uni-app'
	import linfengAd from "@/components/linfeng-ad/linfeng-ad.vue";
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

	const btnStyle = ref({
		borderRadius: '100rpx',
		color: "#fff",
		backgroundColor: '#000000',
		width: '166rpx',
		height: '60rpx',
		fontSize: '30rpx'
	});

	const postId = ref(0);
	const postDetail = reactive({
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
	const focus = ref(false);
	const isSubmitD = ref(false);
	const commentList = ref([]);
	const placeholder = ref('说点什么...');
	const form = reactive({
		pid: 0,
		type: 1,
		toUid: '',
		postId: '',
		content: '',
		commentImg: ''
	});
	const showShare = ref(false);
	const showCanvas = ref(false);
	const shareBtnStyle = reactive({
		backgroundColor: '#333'
	});
	const page = ref(1);
	const loadStatus = ref('loadmore');
	const isVoteExpire = ref(false);
	const isVip = ref(0);
	const posterUrl = ref("");
	const showTel = ref(false);
	const imageClass = ref(['', 'one-img', 'two-img', 'three-img', 'four-img', 'five-img', 'six-img', 'seven-img',
		'eight-img', 'nine-img'
	]);
	const h5Adpid = ref('');
	const wxAdpid = ref('');
	const adIsOpen = ref('');
	const showType = ref('0');
	const rewardBtn = ref('0');
	const showReward = ref(false);
	const rewardCount = ref('');
	const imgHeight = ref(980);
	const tmpHeight = ref(0);
	const commentTempImgUrl = ref('');
	const myVoteIds = ref([]);
	const userIsLogin = ref(false);

	function getAdConfig() {
		$H.get('system/getAd').then(res => {
			if (res.code == 0) {
				adIsOpen.value = res.adIsOpen;
				h5Adpid.value = res.h5Adpid;
				wxAdpid.value = res.wxAdpid;
			}
		});
	}

	function close(e) {
		showTel.value = e
	}

	function openShare() {
		showShare.value = true
	}

	function messageRead(mid) {
		$H.post('message/readMessage', {
			postId: postId.value,
			mid
		}).then(() => {});
	}

	function voteSubmit() {
		let voteDate = [];
		(postDetail.voteInfo.options || []).forEach(item => {
			if (item.checked) voteDate.push(item.id);
		});
		$H.post('post/vote/userVote', {
			id: postDetail.voteInfo.id,
			vote: voteDate
		}).then(res => {
			if (res.code == 0) {
				getPostDetail();
				uni.showToast({
					title: "投票成功",
					icon: "none",
					duration: 2000
				});
			}
		});
	}

	function castVote(index, type) {
		if (!postDetail.isVoteResult && !isVoteExpire.value) {
			if (type === 1) {
				(postDetail.voteInfo.options || []).forEach(item => {
					item.checked = false;
				});
			}
			const checked = postDetail.voteInfo.options[index].checked;
			postDetail.voteInfo.options[index].checked = !checked;
		}
	}

	function closeShare() {
		showShare.value = false
	}

	function copyPageUrl() {
		uni.setClipboardData({
			data: $c.shareH5Url + 'pages/post/detail?id=' + postId.value,
			success: function() {
				uni.hideToast();
				$f.toast('链接复制成功', 'none');
				showShare.value = false;
			}
		});
	}

	function delComment(e, index, index2) {
		let user = uni.getStorageSync('userInfo');
		var flag = false;
		var i = 0;
		(e.children || []).map((item) => {
			if (item.uid == user.uid) {
				flag = true;
				e.id = item.id;
				if (index2) {
					index2 = i;
				}
			}
			i++;
		});
		if (e.uid != user.uid) {
			if (!flag) {
				return;
			}
		}
		uni.showModal({
			title: '提示',
			content: '确定删除自己的评论嘛？',
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
						}
					});
				}
			}
		});
	}

	function onCopy() {
		$f.copy(postDetail.content);
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
		shareUrl = $c.shareH5Url + "pages/post/detail?id=" + postId.value;
		// #endif
		// #ifdef MP-WEIXIN
		shareOrigin = "weixin";
		shareUrl = "pages/post/detail?id=" + postId.value;
		// #endif
		$H.get('post/qrCode', {
			postId: postId.value,
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

	function toDiscuss(id) {
		uni.navigateTo({
			url: '/pages/discuss/detail?id=' + id
		});
	}

	function toTopicDetail() {
		uni.navigateTo({
			url: '/pages/topic/detail?id=' + postDetail.topicId
		});
	}

	function openLocation() {
		uni.openLocation({
			address: postDetail.address,
			latitude: parseFloat(postDetail.latitude),
			longitude: parseFloat(postDetail.longitude),
			success: () => {
				console.log('success');
			}
		});
	}

	function onThumbs(id, index, index2) {
		$H.post('comment/thumbs', {
			id
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
			id
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

	function focusEvent(event) {}

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
		form.postId = postId.value;
	}

	function onBlur() {
		placeholder.value = '留下你的精彩评论...';
		focus.value = false;
	}

	function getCommentList() {
		loadStatus.value = 'loading';
		$H.get('comment/list', {
			postId: postId.value,
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
					item.content = $f.formatTextForMiniProgram(item.content || '')
					item.children.forEach(item2 => {
						item2.content = $f.formatTextForMiniProgram(item2.content || '')
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

	function jumpUser(uid) {
		uni.navigateTo({
			url: '/pages/user/home?uid=' + uid
		});
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
					postDetail.commentCount += 1
				}
				form.content = '';
				form.commentImg = ''
				commentTempImgUrl.value = ''
				page.value = 1;
				commentList.value = [];
				form.pid = 0;
				getCommentList();
			} else if (res.code == 500) {
				$u.toast(res.msg);
			}
			isSubmitD.value = false;
		});
	}

	function getPostDetail() {
		$loading(true);
		$H.get('post/detail', {
			id: postId.value
		}).then(res => {
			if (res.code == 500) {
				uni.hideLoading();
				$u.toast(res.msg);
				$loading(false);
				setTimeout(function() {
					uni.switchTab({
						url: '/pages/index/index'
					});
				}, 1500);
			} else if (res.code == 999) {
				uni.hideLoading();
				$u.toast(res.msg);
				$loading(false);
				setTimeout(function() {
					uni.redirectTo({
						url: '/pages/post/confirm?id=' + postId.value + '&type=1'
					});
				}, 1500);
			} else {
				$loading(false);
				Object.assign(postDetail, res.result);
				// #ifdef H5 
				postDetail.content = $f.formatText(res.result.content)
				// #endif
				// #ifdef MP-WEIXIN || APP-PLUS
				postDetail.content = $f.formatTextForMiniProgram(res.result.content || '')
				// #endif
				// 统一处理换行，在所有平台都生效
				postDetail.content = postDetail.content.replace(/\n/g, '<br>');
				if (res.result.showType) {
					showType.value = res.result.showType;
				}
				rewardBtn.value = res.result.rewardBtn;
				if (postDetail.type == 3) {
					uni.redirectTo({
						url: '/subpages/content/article/article?id=' + postId.value
					})
				}
				if (postDetail.type == 2) {
					var r = $f.decryptUrl(postDetail.media[0]);
					var list = JSON.parse(r)
					postDetail.media[0] = list[0]
				}
				if (postDetail.type == 1 && postDetail.media && postDetail.media[0]) {
					getImageHeight(postDetail.media[0])
				}
				if (postDetail.isVoteResult && postDetail.myVoteResult) {
					try {
						myVoteIds.value = JSON.parse(postDetail.myVoteResult);
					} catch (e) {
						console.error('解析投票结果失败:', e);
						myVoteIds.value = [];
					}
				}
				if (postDetail.type == 4 && postDetail.voteId && postDetail.voteId > 0) {
					(postDetail.voteInfo.options || []).forEach(item => {
						item.checked = false;
					});
					let timestamp = Date.parse(new Date()) / 1000;
					if (postDetail.voteInfo.expireTime < timestamp) {
						isVoteExpire.value = true;
					}
				}
			}
		});
	}

	function cancelCollection() {
		$H.post('post/cancelCollection', {
			id: postId.value
		}).then(res => {
			if (res.code === 0) {
				postDetail.isCollection = false;
				postDetail.collectionCount--;
			}
		});
	}

	function addCollection() {
		$H.post('post/addCollection', {
			id: postId.value,
			uid: postDetail.uid
		}).then(res => {
			if (res.code === 0) {
				postDetail.isCollection = true;
				postDetail.collectionCount++;
			}
		});
	}

	function addThumb() {
		$H.post('post/addThumb', {
			id: postId.value
		}).then(res => {
			if (res.code === 0) {
				postDetail.isThumb = true;
			}
		});
	}

	function cancelThumb() {
		$H.post('post/cancelThumb', {
			id: postId.value,
			uid: postDetail.uid
		}).then(res => {
			if (res.code === 0) {
				postDetail.isThumb = false;
			}
		});
	}

	function follow() {
		$H.post('user/addFollow', {
			id: postDetail.uid
		}).then(res => {
			if (res.code === 0) {
				postDetail.isFollow = true;
			} else {
				proxy?.$refs?.uToast?.show?.({
					title: res.msg,
					type: 'error'
				});
			}
		});
	}

	function cancelFollow() {
		$H.post('user/cancelFollow', {
			id: postDetail.uid
		}).then(res => {
			if (res.code === 0) {
				postDetail.isFollow = false;
			}
		});
	}

	function previewImage(e) {
		uni.previewImage({
			current: e.currentTarget.dataset.current,
			urls: e.currentTarget.dataset.image
		});
	}

	function showImage(index) {
		uni.previewImage({
			current: postDetail.media[index],
			urls: postDetail.media
		});
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
		showReward.value = true;
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
						postId: postId.value,
					}).then(res => {
						if (res.code == 0) {
							showReward.value = false;
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

	function changeSwiper(index) {
		getImageHeight(postDetail.media[index])
	}

	function getImageHeight(url) {
		uni.getImageInfo({
			src: url,
			success: function(image) {
				if (image.height > tmpHeight.value) {
					tmpHeight.value = image.height
				}
				if (tmpHeight.value < 980 && tmpHeight.value > 450) {
					imgHeight.value = tmpHeight.value;
				} else if (tmpHeight.value > 980) {
					imgHeight.value = 980
				} else if (tmpHeight.value < 450) {
					imgHeight.value = 450
				}
			}
		});
	}

	function isMyVoteOption(optionId) {
		return myVoteIds.value.includes(optionId);
	}

	onLoad((options) => {
		postId.value = options.id;
		if (options.scene) {
			postId.value = options.scene;
		}
		if (options.mid) {
			messageRead(options.mid);
		}
		form.postId = postId.value;
		getPostDetail();
		getCommentList();
		getAdConfig();
		let userInfo = uni.getStorageSync('userInfo');
		if (userInfo) {
			isVip.value = uni.getStorageSync('userInfo').vip
			userIsLogin.value = true
		}
		//#ifdef MP-WEIXIN
		wx.showShareMenu({
			withShareTicket: true,
			menus: ['shareAppMessage', 'shareTimeline']
		});
		//#endif
	});
	onReachBottom(() => {
		page.value++;
		getCommentList();
	});
	onShareAppMessage(() => {
		let imgURL;
		if (postDetail.media != null) {
			if (postDetail.media.length > 0) {
				imgURL = postDetail.media[0];
			}
		}
		let content = postDetail.content;
		if (postDetail.cut == 1) {
			content = postDetail.brief;
			imgURL = "https://demo.linfeng.tech/resource/images/user-bg.png"
		}
		return {
			title: content,
			path: '/pages/post/detail?id=' + postId.value,
			imageUrl: imgURL
		};
	});
	onShareTimeline(() => {
		let imgURL = postDetail.media[0];
		let content = postDetail.content;
		if (postDetail.cut == 1) {
			content = postDetail.brief;
			imgURL = "https://demo.linfeng.tech/resource/images/user-bg.png"
		}
		return {
			title: content,
			imageUrl: imgURL,
			query: 'id=' + postId.value
		};
	});
</script>

<style lang="scss" scoped>
	.post-title {
		margin: 20rpx 0;
		font-size: 36rpx;
		font-weight: 600;
	}

	.info-box {
		padding: 20rpx;
		background-color: #ffffff;
	}

	.icon text {
		font-size: 12px;
		color: #999;
		margin-right: 20rpx;
	}

	.detail-tag,
	.post-address {
		display: flex;
		align-items: center;
		padding: 12rpx 0;
	}

	.post-address>view,
	.detail-tag>view {
		display: flex;
		align-items: center;
		height: 56rpx;
		line-height: 56rpx;
		border-radius: 56rpx;
		background: #eff2f3;
		padding: 0 24rpx 0 12rpx;
		margin-right: 16rpx;
	}

	.post-address>view view,
	.detail-tag>view view {
		margin-left: 8rpx;
		font-size: 26rpx;
		font-weight: 400;
	}

	.post-address image,
	.detail-tag image {
		height: 44rpx;
		width: 44rpx;
	}

	/*关注*/

	.user-item-0 {
		display: flex;

		.user-item-user {

			.diyTag {
				display: flex;
				align-items: center;

				.user-name {
					font-size: 36rpx;
				}

				.image3 {
					width: 75rpx;
					height: 30rpx;
					margin-left: 5rpx;
				}
			}

			flex: 1;

			.postIntro {
				font-size: 12px;
				color: #999;
				display: -webkit-box;
				-webkit-box-orient: vertical;
				-webkit-line-clamp: 1;
				overflow: hidden;
			}
		}

		.avatar-z {
			width: 80rpx;
			height: 80rpx;
			float: left;
			margin-right: 10rpx;
			border-radius: 50%;
			margin-top: 10rpx;
		}
	}

	.user-item-1 {
		display: flex;
		border-radius: 30rpx;
		background-color: #fafbfc;
		padding: 20rpx;

		.user-item-user {

			.diyTag {
				display: flex;
				align-items: center;

				.user-name {
					font-size: 36rpx;
				}

				.image3 {
					width: 75rpx;
					height: 30rpx;
					margin-left: 5rpx;
				}
			}

			flex: 1;

			.postIntro {
				font-size: 12px;
				color: #999;
				display: -webkit-box;
				-webkit-box-orient: vertical;
				-webkit-line-clamp: 1;
				overflow: hidden;
			}
		}

		.avatar-z {
			width: 80rpx;
			height: 80rpx;
			float: left;
			margin-right: 10rpx;
			border-radius: 50%;
			margin-top: 10rpx;
		}
	}

	.followStyle {
		width: 120rpx;
		height: 50rpx;
		border: 1px solid #F3F2F2;
		border-radius: 50rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 26rpx;
		background-color: #333;
		color: #F3F2F2;
	}

	.followStyle1 {
		width: 100rpx;
		height: 50rpx;
		border: 1px solid #F3F2F2;
		border-radius: 50rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 26rpx;
		color: #9F9E9F;
	}

	.postStatus {
		width: 100rpx;
		height: 50rpx;
		border: 1px solid #F3F2F2;
		border-radius: 50rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 26rpx;
		color: #f47878;
	}

	/*底部操作*/
	.p-footer {
		margin: 30rpx;
		display: flex;
		font-size: 24rpx;
		color: #616161;

		.p-item {
			display: flex;
			align-items: center;

			.iconfont {
				font-size: 40rpx;
			}

			&:nth-child(2) {
				margin: 0 auto;
			}

			.iconfont {
				margin-right: 10rpx;
			}
		}
	}

	/*评论列表*/
	.comment-box {
		background-color: #ffffff;
		margin-top: 20rpx;
		padding: 20rpx;
	}

	.comment-box>.title {
		margin-bottom: 20rpx;
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

				.official {
					color: #fff;
					font-size: 21rpx;
					text-align: center;
					width: 50rpx;
					min-width: 50rpx;
					height: 30rpx;
					min-height: 30rpx;
					line-height: 30rpx;
					border-radius: 4rpx;
					background-image: linear-gradient(to right, #e67577, #e5cad1);
					margin-left: 6rpx;
					margin-top: 12rpx;
				}


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
		background-color: #F3F2F2;

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

				.official {
					color: #fff;
					font-size: 21rpx;
					text-align: center;
					width: 50rpx;
					min-width: 50rpx;
					height: 30rpx;
					min-height: 30rpx;
					line-height: 30rpx;
					border-radius: 4rpx;
					background-image: linear-gradient(to right, #e67577, #e5cad1);
					margin-left: 6rpx;
					margin-top: 12rpx;
				}

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
				color: #55aaff;
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
		z-index: 999;

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
			position: relative;
			width: 80rpx;
			height: 80rpx;
			margin: 20rpx;
			margin-bottom: 0;
			color: #fff;

			.comment-img {
				border-radius: 8rpx;
				width: 80rpx;
				height: 80rpx;
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
		background-color: #f5f5f5;
		padding: 20rpx;
		border-radius: 10rpx;
		min-height: 40rpx;
	}

	.comment-tool .u-btn {
		margin-left: 10rpx;
	}


	.comment-input {
		height: 60rpx;
		font-size: 28rpx;
		font-weight: 500;
		line-height: 60rpx;
		width: 500rpx;
		margin-right: 20rpx;
	}

	/*文章图片*/
	.img-style-1 {
		display: block;
		width: 100%;
		max-height: 600rpx;
		border-radius: 5px;
	}

	.img-style-2 {
		display: flex;
	}

	.img-style-2 image {
		margin: 5rpx;
		border-radius: 5px;
		width: 100%;
		height: 294rpx;
	}

	.img-style-3 {
		display: flex;
		flex-wrap: wrap;
	}

	.img-style-3 image {
		width: 31.3%;
		height: 200rpx;
		margin: 1%;
		border-radius: 5px;
	}

	.img-style-4 {
		display: flex;
		flex-wrap: wrap;
	}

	.img-style-4 image {
		width: 48%;
		height: 320rpx;
		margin: 0.5%;
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

			.desc {
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

	// 分享弹窗
	.share-wrap {
		display: flex;
		padding: 30rpx;
		width: 45%;
		margin: 0 auto;

		.share-item-wx {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;

			&:nth-child(1) {
				margin-right: auto;
			}

			image {
				width: 88rpx;
				height: 88rpx;
			}

			text {
				font-size: 24rpx;
				margin-top: 26rpx;
			}
		}

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
				width: 94rpx;
				height: 94rpx;
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

	.footer {
		// position: absolute;
		bottom: 20rpx;
		left: 20rpx;
		right: 20rpx;
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

		.active {
			background-color: #333;
			color: #fff;
		}

		.my-vote {
			background-color: rgba(75, 181, 67, 0.1);
			border-color: #4BB543;
			color: #4BB543;
		}
	}

	.expire-box {
		background-color: #999;
		color: #fff;
		font-size: 24rpx;
		display: inline-block;
		padding: 0 20rpx;
		border-radius: 10rpx;
		margin-bottom: 20rpx;
	}

	video {
		width: 100%;
	}

	.post-text {
		white-space: pre-wrap;
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