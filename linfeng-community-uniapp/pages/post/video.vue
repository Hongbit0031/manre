<template>
	<view class="shortvideo">
		<view style="position: absolute;">
		<u-navbar :is-back="false" :border-bottom="false" :background="background">
			<u-icon name="search" :size="40" class="search-wrap" color="#fff" @click="toSearch"></u-icon>
			<u-tabs :list="pageTab" v-model="pageCurrent" @change="pageTabChange" bg-color="" active-color="#fff" inactive-color="#afafaf"></u-tabs>
		</u-navbar>
		</view>
		<!-- #ifdef MP -->
		<swiper :style="'width: '+ windowWidth +'px; height: '+ windowHeight +'px; background-color: #000000;'"
			:vertical="true" @animationfinish="animationfinish" @change="change" :current="current"
			:indicator-dots="false" @touchstart="mpTouchstart" @touchend="mpTouchend">
			<swiper-item v-for="(list,index) in dataList" :key="index">
				<view v-if="Math.abs(k-index)<=1">
					<view>
						<video v-if="isShow" :id="list._id+''+index" :loop="true" :muted="false" :controls="false"
							:http-cache="true" :page-gesture="false" :show-fullscreen-btn="false" :show-loading="false"
							:show-center-play-btn="false" :enable-progress-gesture="false" :src="list.src"
							@ended="ended" @click="tapVideoHover(list.state,$event)"
							@timeupdate="timeupdate($event,index)"
							:style="'width: '+ windowWidth +'px; height: '+ windowHeight +'px; background-color: #000000; z-index: -1;'"
							:poster="getVideoThumb(list.src)"></video>
						
					</view>
					<!-- 播放状态：pause 的时候就会暂停 -->
					<view class="videoHover" @click="tapVideoHover(list.state,$event)"
						:style="'width: '+ windowWidth +'px; height: '+ windowHeight +'px;'">
						<image v-if="list.state=='pause'" class="playState" src="@/static/stop.png"></image>
					</view>

					<!-- 右侧栏目 -->
					<view class="tool-box">
						<view hover-class="none" @click="navigateToUserHome(list.uid)" class="item">
							<u-avatar :src="list.userInfo.avatar"></u-avatar>
						</view>
						<view class="item">
							<image v-show="list.isCollection" @click="cancelCollection" src="/static/fav-1.png">
							</image>
							<image v-show="!list.isCollection" @click="addCollection" src="/static/fav.png">
							</image>
							<text>{{ list.collectionCount }}</text>
						</view>
						<view class="item" @click="openCommentPop">
							<image src="/static/comment.png"></image>
							<text>{{ list.commentCount }}</text>
						</view>
						<!-- #ifdef MP -->
						<button open-type="share" class="u-reset-button item">
							<image src="/static/share.png"></image>
						</button>
						<!-- #endif -->
						<!-- #ifdef H5 -->
						<button class="u-reset-button item" @click="copyPageUrl">
							<image src="/static/share.png"></image>
						</button>
						<!-- #endif -->
					</view>

					<!-- 底部内容 -->
					<view class="footer">
						<view @click="navigateToUserHome(list.uid)" hover-class="none" class="username">
							@{{ list.userInfo.username }}</view>
						<view @click="openCommentPop">
							<text class="discuss-title" :data-id="list.discussId" v-if="list.discussId > 0"
								@tap.stop="toDiscuss">#{{ list.discussTitle }}</text>
							<text class="c-txt" @longpress="onCopy">{{ replace(list.content) }}</text>
						</view>
					</view>
					<!-- 评论弹窗 -->
					<view>
						<view @click="onMask" v-show="commentPopup" class="mask"></view>
						<view v-show="commentPopup"  class="popup-wrap">
							<scroll-view scroll-y style="height: 45vh;" @scrolltolower="reachBottom">
								<view class="comment-box">
									<view class="title">评论（{{ list.commentCount }}）<text class="com">来自：</text><text class="topicName" @tap.stop="toTopic(list.topicId)">{{list.topicName}}</text></view>
									<u-line color="#eaeaea"></u-line>
									<!-- 帖子内容 -->
									<view class="comment-item" @longpress="onCopy">
										<image @click="jumpUser(list.userInfo.uid)" class="avatar"
											:src="list.userInfo.avatar"></image>
										<view class="c-content">
											<view class="user">
												<text>{{ list.userInfo.username }}</text>
												<text class="official">作者</text>
											</view>
											<view class="c-txt">
												<text class="discuss-title-comment" :data-id="list.discussId" v-if="list.discussId > 0"
													@tap.stop="toDiscuss">#{{ list.discussTitle }}</text>
												<text>{{ list.content }}</text>
											</view>
											<text class="time">{{ timeFormat(list.createTime) }}</text>
										</view>
									</view>
									<u-line color="#f3f3f3"></u-line>
									<view style="margin-bottom: 40rpx;" v-for="(item, indexs) in commentList" :key="indexs">
										<!-- 评论列表 -->
										<view class="comment-item" @longpress="delComment(item, indexs)">
											<image @click="jumpUser(item.userInfo.uid)" class="avatar"
												:src="item.userInfo.avatar">
											</image>
											<view class="c-content" @click="onReply(item)">
											<view class="user">
												<text style="color: #999;">{{ item.userInfo.username }}</text>
												<block v-if="item.isThumbs">
													<view @click.stop="cancelThumbs(item.id, indexs)" class="thumbs">
														<text class="num">{{ item.thumbs }}</text>
														<u-icon class="icon" size="40" name="heart-fill"
															color="#e62e00"></u-icon>
													</view>
												</block>
												<block v-else>
													<view @click.stop="onThumbs(item.id, indexs)" class="thumbs">
														<text class="num">{{ item.thumbs }}</text>
														<u-icon class="icon" size="40" name="heart-fill"
															color="#bfbfbf"></u-icon>
													</view>
												</block>
											</view>
												<text class="c-txt">{{ item.content }}</text>
												<image style="width: 200rpx; margin-top: 20rpx; border-radius: 16rpx" mode="widthFix"
													v-if="item.img&&item.img !== ''" :src="item.img" @click.stop="commentPreviewImage(item.img)" />
												<text class="time">{{ timeFormat(item.createTime) }}</text>
											</view>
										</view>
										<view v-if="item.children.length > 0">
											<view @click="onReply(item2, indexs, index2)"
												v-for="(item2, index2) in item.children" :key="item2.id"
												@longpress="delComment(item2, indexs, index2)" class="sub-comment-item">
												<view class="user">
													<u-avatar class="avatar" :size="40" :src="item2.userInfo.avatar"></u-avatar>
													<view class="u-head">
														<text style="color: #999;">{{ item2.userInfo.username }}</text>
														<block v-if="item2.isThumbs">
															<view class="thumbs"
																@click.stop="cancelThumbs(item2.id, indexs, index2)">
																<text class="num">{{ item2.thumbs }}</text>
																<u-icon class="icon" size="40" name="heart-fill"
																	color="#e62e00"></u-icon>
															</view>
														</block>
														<block v-else>
															<view class="thumbs"
																@click.stop="onThumbs(item2.id, indexs, index2)">
																<text class="num">{{ item2.thumbs }}</text>
																<u-icon class="icon" size="40" name="heart-fill"
																	color="#bfbfbf"></u-icon>
															</view>
														</block>
													</view>
												</view>
												<view class="reply">
													<view>
														<text>@</text>
														<view @click="navigateToUserHome(item2.toUser.uid)" hover-class="none"
															class="name">
															{{ item2.toUser.username }}
														</view>
														<text>：{{ item2.content }}</text>
													</view>
													<image style="width: 200rpx; margin-top: 20rpx; border-radius: 16rpx" mode="widthFix"
														v-if="item2.img&&item2.img !== ''" :src="item2.img" @click.stop="commentPreviewImage(item2.img)" />
													<view class="time">{{ timeFormat(item2.createTime) }}</view>
												</view>
											</view>
										</view>
										<view class="more-comment-btn" v-if="item.childrenCount>2 && item.showBtn"
											@click.stop="remainComment(item,index)">展开剩余回复</view>
										<u-line margin="40rpx 0 0 110rpx" length="85%"></u-line>
									</view>
									<!-- 加载状态 -->
									<view v-if="commentList.length > 0">
										<view style="margin: 30rpx;">
											<u-loadmore :status="loadStatus" />
										</view>
									</view>
									<view v-else>
										<u-empty text="暂无评论" mode="message"></u-empty>
									</view>
									<view style="height: 100rpx;"></view>
								</view>
							</scroll-view>
							<!-- 评论输入框 -->
							<view class="comment-tool">
								<view class="wrap">
									<view style="height: 60rpx; display: flex; align-items: center">
										<input ref="lfinput" type="text" :placeholder="placeholder" class="comment-input" @blur="onBlur" @focus="focusEvent" v-model="form.content" confirm-type="send" @confirm="addComment" />
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
					
					
					<!-- 进度条 -->
					<view v-if="k === index" @touchstart="touchstart" @touchmove="touchmove" @touchend="touchend"
						:style="'width: '+ (windowWidth - (windowWidth*0.10)) +'px; margin-left: '+ (windowWidth * 0.05) +'px; height: 40px; position: absolute; bottom: 36px;'">
						<!-- 不拖动情况下 -->
						<view>
							<!-- 播放的进度条 -->
							<view v-if="!isTouch"
								:style="'width: '+ ((windowWidth - (windowWidth*0.10)) * progressBarPercent) +'px; position: absolute; margin-top: 18px; height: 5px; border-radius: 10px; background-color: #e6e4e7; '">
							</view>
							<!--  -->
							<view v-if="isTouch"
								:style="'width: '+ (videoStartPositon + addPositon) +'px; position: absolute; margin-top: 18px; height: 5px; border-radius: 10px; background-color: #e6e4e7; '">
							</view>
						</view>
					</view>
				</view>
			</swiper-item>
		</swiper>
		<!-- #endif -->
		<!-- #ifdef H5 -->
		<swiper :style="'width: '+ windowWidth +'px; height: '+ windowHeight +'px; background-color: #000000;'"
			:vertical="true" @animationfinish="animationfinish" @change="change" :current="current"
			:indicator-dots="false" :duration="duration">
			<swiper-item v-for="(list,index) in dataList" :key="index">
				<view v-if="Math.abs(k-index)<=1">
					<view>
						<video v-if="isShow" :id="list._id+''+index" :loop="true" :muted="list.isplay" :controls="false"
							:http-cache="true" :page-gesture="false" :show-fullscreen-btn="false" :show-loading="false"
							:show-center-play-btn="false" :enable-progress-gesture="false" :src="list.src"
							@ended="ended" @click="tapVideoHover(list.state,$event)"
							@timeupdate="timeupdate($event,index)"
							:style="'width: '+ windowWidth +'px; height: '+ windowHeight +'px; background-color: #000000; z-index: -1;'"
							:poster="getVideoThumb(list.src)"></video>
					</view>
					<!-- 播放状态：pause 的时候就会暂停 -->
					<view class="videoHover" @click="tapVideoHover(list.state,$event)"
						:style="'width: '+ windowWidth +'px; height: '+ windowHeight +'px;'">
						<image v-if="list.state=='pause'" class="playState" src="@/static/stop.png"></image>
					</view>

					<!-- 右侧栏目 -->
					<view class="tool-box">
						<view hover-class="none" @click="navigateToUserHome(list.uid)" class="item">
							<u-avatar :src="list.userInfo.avatar"></u-avatar>
						</view>
						<view class="item">
							<image v-show="list.isCollection" @click="cancelCollection" src="/static/fav-1.png"></image>
							<image v-show="!list.isCollection" @click="addCollection" src="/static/fav.png"></image>
							<text>{{ list.collectionCount }}</text>
						</view>
						<view class="item" @click="openCommentPop">
							<image src="/static/comment.png"></image>
							<text>{{ list.commentCount }}</text>
						</view>
						<!-- #ifdef MP -->
						<button open-type="share" class="u-reset-button item">
							<image src="/static/share.png"></image>
						</button>
						<!-- #endif -->
						<!-- #ifdef H5 -->
						<button class="u-reset-button item" @click="copyPageUrl">
							<image src="/static/share.png"></image>
						</button>
						<!-- #endif -->
					</view>
					<!-- 底部内容 -->
					<view class="footer">
						<view @click="navigateToUserHome(list.uid)" hover-class="none" class="username">
							@{{ list.userInfo.username }}</view>
						<view @click="openCommentPop">
							<text class="discuss-title" :data-id="list.discussId" v-if="list.discussId > 0"
								@tap.stop="toDiscuss">#{{ list.discussTitle }}</text>
							<text class="c-txt" @longpress="onCopy">{{ replace(list.content) }}</text>
						</view>
					</view>
					<!-- 评论弹窗 -->
					<lf-popup v-model="commentPopup" height="50vh">
						<scroll-view scroll-y style="height: 45vh;" @scrolltolower="reachBottom">
							<view class="comment-box">
								<view class="title">评论（{{ list.commentCount }}）<text class="com">来自：</text><text class="topicName" @tap.stop="toTopic(list.topicId)">{{list.topicName}}</text></view>
								<u-line color="#eaeaea"></u-line>
								<!-- 帖子内容 -->
								<view class="comment-item" @longpress="onCopy">
									<image @click="jumpUser(list.userInfo.uid)" class="avatar"
										:src="list.userInfo.avatar"></image>
									<view class="c-content">
										<view class="user">
											<text>{{ list.userInfo.username }}</text>
											<text class="official">作者</text>
										</view>
										<view class="c-txt">
											<text class="discuss-title-comment" :data-id="list.discussId" v-if="list.discussId > 0"
												@tap.stop="toDiscuss">#{{ list.discussTitle }}</text>
											<text>{{ list.content }}</text>
										</view>
										<text class="time">{{ timeFormat(list.createTime) }}</text>
									</view>
								</view>
								<u-line color="#f3f3f3"></u-line>
								<view style="margin-bottom: 40rpx;" v-for="(item, index) in commentList" :key="index">
									<!-- 评论列表 -->
									<view class="comment-item" @longpress="delComment(item, index)">
										<image @click="jumpUser(item.userInfo.uid)" class="avatar" :src="item.userInfo.avatar">
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
											<text class="c-txt">{{ item.content }}</text>
											<image style="width: 200rpx; margin-top: 20rpx; border-radius: 16rpx" mode="widthFix"
												v-if="item.img&&item.img !== ''" :src="item.img" @click.stop="commentPreviewImage(item.img)" />
											<text class="time">{{ timeFormat(item.createTime) }}</text>
										</view>
									</view>
									<view v-if="item.children.length > 0">
										<view @click="onReply(item2, index, index2)"
											v-for="(item2, index2) in item.children" :key="item2.id"
											@longpress="delComment(item2, index, index2)" class="sub-comment-item">
											<view class="user">
												<u-avatar class="avatar" :size="40" :src="item2.userInfo.avatar"></u-avatar>
												<view class="u-head">
													<text style="color: #999;">{{ item2.userInfo.username }}</text>
													<block v-if="item2.isThumbs">
														<view class="thumbs" @click.stop="cancelThumbs(item2.id, index, index2)">
															<text class="num">{{ item2.thumbs }}</text>
															<u-icon class="icon" size="40" name="heart-fill" color="#e62e00">
															</u-icon>
														</view>
													</block>
													<block v-else>
														<view class="thumbs" @click.stop="onThumbs(item2.id, index, index2)">
															<text class="num">{{ item2.thumbs }}</text>
															<u-icon class="icon" size="40" name="heart-fill" color="#bfbfbf">
															</u-icon>
														</view>
													</block>
												</view>
											</view>
											<view class="reply">
												<view>
													<text>@</text>
													<view @click="navigateToUserHome(item2.toUser.uid)" hover-class="none"
														class="name">
														{{ item2.toUser.username }}
													</view>
													<text>：{{ item2.content }}</text>
												</view>
												<image style="width: 200rpx; margin-top: 20rpx; border-radius: 16rpx" mode="widthFix"
													v-if="item2.img&&item2.img !== ''" :src="item2.img" @click.stop="commentPreviewImage(item2.img)" />
												<view class="time">{{ timeFormat(item2.createTime) }}</view>
											</view>
										</view>
									</view>
									<view class="more-comment-btn" v-if="item.childrenCount>2 && item.showBtn"
										@click.stop="remainComment(item,index)">展开剩余回复</view>
									<u-line margin="40rpx 0 0 110rpx" length="85%"></u-line>
								</view>
								<!-- 加载状态 -->
								<view v-if="commentList.length > 0">
									<view style="margin: 30rpx 30rpx 46rpx 30rpx;">
										<u-loadmore :status="loadStatus" />
									</view>
								</view>
								<view v-else>
									<u-empty text="暂无评论" mode="message"></u-empty>
								</view>
								<view style="height: 100rpx;"></view>
							</view>
						</scroll-view>
						<!-- 评论输入框 -->
						<view class="comment-tool">
							<view class="wrap">
								<view style="height: 60rpx; display: flex; align-items: center">
									<input ref="lfinput" type="text" :placeholder="placeholder" class="comment-input" @blur="onBlur" @focus="focusEvent" v-model="form.content" confirm-type="send" @confirm="addComment" />
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
					</lf-popup>
					<!-- 进度条 -->
					<view v-if="k === index" @touchstart="touchstart" @touchmove="touchmove" @touchend="touchend"
						:style="'width: '+ (windowWidth - (windowWidth*0.10)) +'px; margin-left: '+ (windowWidth * 0.05) +'px; height: 40px; position: absolute; bottom: 36px;'">
						<!-- 不拖动情况下 -->
						<view>
							<!-- 播放的进度条 -->
							<view v-if="!isTouch"
								:style="'width: '+ ((windowWidth - (windowWidth*0.10)) * progressBarPercent) +'px; position: absolute; margin-top: 18px; height: 5px; border-radius: 10px; background-color: #e6e4e7; '">
							</view>
							<!--  -->
							<view v-if="isTouch"
								:style="'width: '+ (videoStartPositon + addPositon) +'px; position: absolute; margin-top: 18px; height: 5px; border-radius: 10px; background-color: #e6e4e7; '">
							</view>
						</view>
					</view>
				</view>
			</swiper-item>
		</swiper>
		<!-- #endif -->
		<!-- 底部导航栏 -->
		<view class="v-footer v-info s-around a-center">
			<text class="items" @click="goIndex">首页</text>
			<text class="items on">视频</text>
			<text class="items" @click="goMessage">消息</text>
			<text class="items" @click="goMine">我的</text>
		</view>
	</view>
</template>

<script setup>
import { ref, reactive, watch, getCurrentInstance } from 'vue'
import { onLoad, onShow, onHide, onUnload, onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app'
import { timeFormat } from '@/utils/filters.js'
import storageConfig from '@/utils/storage-config.js'

let audo = uni.createInnerAudioContext()
audo.loop = true

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $c = proxy.$c
const $f = proxy.$f
const $u = proxy.$u

// 响应式数据
const postDetail = ref({
	content: '',
	userInfo: {
		username: ''
	},
	createTime: '',
	commentCount: '',
	uid: ''
})

const isSubmitD = ref(false)
const commentPopup = ref(false)
const commentList = ref([])
const focus = ref(false)
const placeholder = ref('说点什么...')
const page = ref(1) //评论分页
const videoPage = ref(1) //视频分页

const form = reactive({
	pid: 0,
	type: 1,
	toUid: '',
	postId: '',
	content: '',
	commentImg: ''
})

const loadStatus = ref('loadmore')
const background = reactive({
	backgroundColor: '#070707',
})

const postId = ref('')
const pageTab = ref([{
	name: '热门'
}, {
	name: '最新'
}])
const pageCurrent = ref(0)
const commentTempImgUrl = ref('') // 评论图片
const tmpId = ref(0)

//插件部分=================
const windowWidth = ref(0)
const windowHeight = ref(0)
const platform = ref("")
const model = ref("")
const deleteHeight = ref(0)
const dataList = ref([])
const k = ref(0)
const oldVideo = ref("")
const voice = ref("")
const timeout = ref("")
const current = ref(0)
const boxStyle = ref({ //视频，图片封面样式🌟💗
	'height': 0,
	'width': 0,
})

const videoID = ref("")
const isShow = ref(false)

const showPlay = ref(false) //转轮显示控制
const rotates = ref(0) //转轮旋转角度
const rotateTime = ref("") //转轮递归事件控制
const xrotats = ref("")

const mpcleartime = ref("")

const isClick = ref(false)

const changeTimeout = ref("")
const mptime = ref(0)
const mpstartTime = ref(0)

const duration = ref(500)
// -- 进度条相关 -- start
const videoStartPositon = ref(0)
const progressBarPercent = ref(0)
const touchStartPosition = ref(0)
const addPositon = ref(0)
const timeduration = ref(0)
const isTouch = ref(false)
// -- 进度条相关 -- end

// 过滤器函数
const replace = (str) => {
	if (!str) return ''
	str = str.replace(/\n/g, '')
	if (str.length > 17) {
		str = str.substring(0, 17) + '...'
	}
	return str
}

// Watch
watch(k, async (newK, old_k) => {
	progressBarPercent.value = 0
	// #ifndef MP
	clearToTime()
	// #endif
	isShow.value = false
	dataList.value[old_k].playIng = false //如果视频暂停，就加载封面
	dataList.value[old_k].isplay = true
	dataList.value[old_k].state = 'pause'
	
	uni.createVideoContext(dataList.value[old_k]._id + '' + old_k, proxy)
		.pause()
	
	dataList.value[newK].state = 'play'
	isShow.value = true
	xrotats.value = setTimeout(() => {
		showPlay.value = true
		// #ifndef MP
		rotateX()
		// #endif
	}, 200)
	
	// #ifdef MP
	// 如果是小程序端
	clearTimeout(changeTimeout.value)
	dataList.value[newK].isplay = false
	setTimeout(() => {
		dataList.value[newK].playIng = true
	}, 250)
	clearTimeout(changeTimeout.value)
	changeTimeout.value = setTimeout(() => {
		uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).play()
	}, 250)
	// #endif
	
	// #ifdef H5
	dataList.value[newK].isplay = true
	audo.src = dataList.value[newK].src
	if (isClick.value) {
		setTimeout(() => {
			if (typeof WeixinJSBridge == "undefined") {
				setTimeout(() => {
					audo.play()
					uni.createVideoContext(dataList.value[newK]._id + '' + newK, proxy).seek(0)
					uni.createVideoContext(dataList.value[newK]._id + '' + newK, proxy).play()
					setTimeout(() => {
						dataList.value[newK].playIng = true
					}, 650)
				}, 500)
			} else {
				WeixinJSBridge.invoke('getNetworkType', {}, e => {
					setTimeout(() => {
						audo.play()
						uni.createVideoContext(dataList.value[newK]._id + '' + newK, proxy)
							.seek(0)
						uni.createVideoContext(dataList.value[newK]._id + '' + newK, proxy)
							.play()
						setTimeout(() => {
							dataList.value[newK].playIng = true
						}, 850)
					}, 200)
				})
			}
		}, 200)
	} else {
		audo.pause()
		dataList.value[newK].state = 'pause'
		uni.createVideoContext(dataList.value[newK]._id + '' + newK, proxy).seek(0)
		uni.createVideoContext(dataList.value[newK]._id + '' + newK, proxy).pause()
	}
	// #endif
	var p = newK + 1
})

// 生命周期
onLoad((options) => {
	if(options.type){
		pageCurrent.value = options.type
	}
	if(options.id){
		tmpId.value = options.id
	}
	platform.value = uni.getSystemInfoSync().platform
	model.value = uni.getSystemInfoSync().model
	var modelStr = model.value
	if (platform.value == 'ios' && (modelStr !== 'iPhone6' || modelStr !== 'iPhone6s' || modelStr !== 'iPhone7' || modelStr !==
			'iPhone8')) {
		deleteHeight.value = 0 //有 tabbar的 修改这里可以改变视频高度
	}
	windowWidth.value = uni.getSystemInfoSync().windowWidth
	windowHeight.value = uni.getSystemInfoSync().windowHeight
	boxStyle.value.width = windowWidth.value + 'px' //给宽度加px
	boxStyle.value.height = windowHeight.value - deleteHeight.value //有 tabbar的 修改这里可以改变视频高度
	getShortVideoList()
	checkVideoOpen()
	// #ifndef MP
	rotateX()
	// #endif
})

onShow(() => {
	if (dataList.value.length !== 0) {
		// #ifdef MP
		dataList.value[k.value].state = 'play'
		uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).play()
		// #endif
		// #ifdef H5
		if (isClick.value) {
			dataList.value[k.value].state = 'play'
			uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).play()
			audo.play()
		}
		// #endif
	}
})

onHide(() => {
	// #ifdef MP
	dataList.value[k.value].state = 'pause'
	uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).pause()
	// #endif
	// #ifdef H5
	if (isClick.value) {
		dataList.value[k.value].state = 'pause'
		uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).pause()
		audo.pause()
	}
	// #endif
})

onUnload(() => {
	// 页面卸载时清理资源
	// #ifdef MP
	if (dataList.value.length > 0 && dataList.value[k.value]) {
		uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).pause()
	}
	// #endif
	// #ifdef H5
	if (dataList.value.length > 0 && dataList.value[k.value]) {
		uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).pause()
	}
	// 停止并销毁音频
	audo.pause()
	audo.stop()
	// #endif
	// 清理定时器
	clearToTime()
})

// 分享配置
onShareAppMessage((res) => {
	let imgURL
	if(!postId.value){
		postId.value = dataList.value[k.value].id
	}
	return {
		title: dataList.value[k.value].content,
		path: '/pages/post/video-detail?id=' + postId.value
	}
})

onShareTimeline(() => {
	let imgURL
	if(!postId.value){
		postId.value = dataList.value[k.value].id
	}
	return {
		title: dataList.value[k.value].content,
		query: 'id=' + postId.value
	}
})

// 方法
const navigateToUserHome = (uid) => {
	uni.navigateTo({
		url: '/pages/user/home?uid=' + uid
	})
}

const toDiscuss = (e) => {
	uni.navigateTo({
		url: '/pages/discuss/detail?id=' + e.currentTarget.dataset.id
	})
}

const toSearch = () => {
	uni.navigateTo({
		url: '/pages/search/search'
	})
}

const goIndex = () => {
	uni.switchTab({
		url: '/pages/index/index'
	})
}

const goMessage = () => {
	uni.switchTab({
		url: '/pages/message/message'
	})
}

const goMine = () => {
	uni.switchTab({
		url: '/pages/my/my'
	})
}

const toTopic = (id) => {
	uni.navigateTo({
		url: '/pages/topic/detail?id='+id
	})
}

const focusEvent = () => {}

const checkVideoOpen = () => {
	// #ifdef MP || H5
	$H.get('user/isOpen').then(res => {
		if (res.result == 0) {
			$f.toast("视频模块未开启")
			setTimeout(function() {
				uni.switchTab({
					url: '/pages/index/index'
				})
			}, 1500)
		}
	})
	// #endif
	// #ifdef APP-PLUS
	setTimeout(function() {
		uni.redirectTo({
			url: '/pages/my/post?type=2'
		})
	}, 500)
	// #endif
}

const pageTabChange = (index) => {
	// 切换 tab 前先停止当前播放的视频和音频
	// #ifdef H5
	if (dataList.value.length > 0 && dataList.value[k.value]) {
		dataList.value[k.value].state = 'pause'
		uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).pause()
		audo.pause()
		audo.stop()
	}
	// #endif
	// #ifdef MP
	if (dataList.value.length > 0 && dataList.value[k.value]) {
		dataList.value[k.value].state = 'pause'
		uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).pause()
	}
	// #endif
	
	pageCurrent.value = index
	page.value = 1
	dataList.value = []
	uni.redirectTo({
		url: '/pages/post/video?type='+index
	})
}

const mpTouchend = () => {
	mptime.value = (new Date() / 1000) - mpstartTime.value
}

const mpTouchstart = () => {
	mpstartTime.value = (new Date() / 1000)
}

const clearToTime = () => {
	//清理定时器
	for (let i = 0; i < 20; i++) {
		clearTimeout(rotateTime.value)
		clearTimeout(xrotats.value)
		showPlay.value = false
		rotates.value = 0
	}
}

const clearTime = () => {
	//清理定时器
	for (let i = 0; i < 20; i++) {
		clearTimeout(rotateTime.value)
		clearTimeout(xrotats.value)
	}
}

const rotateX = () => {
	rotateTime.value = setTimeout(() => {
		rotateX()
		showPlay.value = true
		rotates.value += 1
	}, 30)
}

const ended = () => {
	// 播放当前视频结束时触发，自动切换下一个视频
}

// ---- 进度条相关 --- start
const touchstart = (e) => {
	isTouch.value = true
	// #ifdef H5
	if (isClick.value) {
		addPositon.value = 0
		videoStartPositon.value = (windowWidth.value - (windowWidth.value * 0.10)) * progressBarPercent.value
		touchStartPosition.value = e.changedTouches[0].clientX
	}
	// #endif
	// #ifdef MP
	addPositon.value = 0
	videoStartPositon.value = (windowWidth.value - (windowWidth.value * 0.10)) * progressBarPercent.value
	touchStartPosition.value = e.changedTouches[0].clientX
	// #endif
}

const touchmove = (e) => {
	// #ifdef H5
	if (isClick.value) {
		let num = e.changedTouches[0].clientX - touchStartPosition.value
		if ((videoStartPositon.value + num) <= (windowWidth.value - (windowWidth.value * 0.10))) {
			addPositon.value = e.changedTouches[0].clientX - touchStartPosition.value
		} else {
			addPositon.value = 0
			videoStartPositon.value = (windowWidth.value - (windowWidth.value * 0.10))
		}
	}
	// #endif
	// #ifdef MP
	let num = e.changedTouches[0].clientX - touchStartPosition.value
	if ((videoStartPositon.value + num) <= (windowWidth.value - (windowWidth.value * 0.10))) {
		addPositon.value = e.changedTouches[0].clientX - touchStartPosition.value
	} else {
		addPositon.value = 0
		videoStartPositon.value = (windowWidth.value - (windowWidth.value * 0.10))
	}
	// #endif
}

const touchend = (e) => {
	// #ifdef H5
	if (isClick.value) {
		let per = Number((videoStartPositon.value + addPositon.value) / (windowWidth.value - (windowWidth.value *
			0.10)))
		let timeSubmit = parseInt(timeduration.value * per)
		audo.seek(timeSubmit)
		audo.play()
		uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).seek(timeSubmit)
		uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).play()
		dataList.value[k.value].state = 'play'
		setTimeout(() => {
			isTouch.value = false
		}, 500)
	}
	// #endif
	// #ifdef MP
	let per = Number((videoStartPositon.value + addPositon.value) / (windowWidth.value - (windowWidth.value * 0.10)))
	let timeSubmit = parseInt(timeduration.value * per)
	const vc = uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy)
	vc.seek(timeSubmit)
	vc.play()
	setTimeout(() => {
		isTouch.value = false
	}, 500)
	// #endif
}

const timeupdate = (event, index) => {
	// 触发进度条更新
	if (index === k.value) {
		timeduration.value = event.detail.duration
		progressBarPercent.value = parseFloat(Number(event.detail.currentTime / event.detail.duration))
	}
}
// ---- 进度条相关 --- ending

//点击播放&&暂停
const tapVideoHover = (state, event) => {
	if (state == 'play' || state == 'continue') {
		dataList.value[k.value].state = 'pause'
	} else {
		dataList.value[k.value].state = 'continue'
	}
	if (dataList.value[k.value].state == 'continue') {
		isClick.value = true
		dataList.value[k.value].playIng = true
		uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).play() //暂停以后继续播放
		
		// #ifdef MP
		// iOS 小程序端：不再改 muted（已固定为 false），给一点延时再 play
		setTimeout(() => {
			uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).play()
		}, 80)
		// #endif
		// #ifdef H5
		audo.play()
		// #endif
	}
	if (dataList.value[k.value].state == 'pause') {
		uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).pause() //暂停以后继续播放
		
		// #ifdef H5
		audo.pause()
		// #endif
	}
}

const change = (event) => {
	k.value = event.detail.current
	postId.value = dataList.value[k.value].id
	//切换视频后评论框全部关闭重置
	commentPopup.value = false
	commentList.value = []
	page.value = 1
	addReadCount(postId.value)
}

const addReadCount = (id) => {
	$H.post('post/addReadCount', {
		postId: id
	}).then(res => {})
}

const animationfinish = (event) => {
	// 1.这里进行判断，如果是最后一个视频就加载视频列表
	if (k.value == dataList.value.length - 1) {
		videoPage.value++
		getVideo()
	}
}

//每一组结束时新的请求
const getVideo = () => {
	$H.post('post/videoList', {
		page: videoPage.value,
		order: pageCurrent.value
	}).then(res => {
		if (res.code == 0) {
			var videoDataList = res.result.data
			//url解密
			videoDataList.forEach(function(obj) {
				obj.src = $f.decryptUrl(obj.src)
			})
			for (let i = 0; i < videoDataList.length; i++) {
				dataList.value.push(videoDataList[i])
			}
		}
	})
}

//初始化加载视频列表
const getShortVideoList = () => {
	$H.post('post/videoList', {
		page: videoPage.value,
		order: pageCurrent.value,
		postId: tmpId.value
	}).then(res => {
		if (res.code == 0) {
			tmpId.value = 0
			isShow.value = false
			var videoDataList = res.result.data
			//url解密
			videoDataList.forEach(function(obj) {
				obj.src = $f.decryptUrl(obj.src)
			})
			if(videoPage.value == 1 && videoDataList.length === 0){
				$u.toast("视频不存在")
				setTimeout(function() {
					uni.switchTab({
						url: '/pages/index/index'
					})
				}, 1500)
			}
			// 2.这里把视频添加到视频列表
			for (let i = 0; i < videoDataList.length; i++) {
				dataList.value.push(videoDataList[i])
			}
			
			// 3.播放当前视频
			setTimeout(() => {
				isShow.value = true
				// #ifdef H5
				dataList.value[k.value].isplay = true
				// #endif
				// #ifdef MP
				// 如果是小程序端
				dataList.value[k.value].state = 'play'
				// #endif
				dataList.value[k.value].playIng = true
				setTimeout(() => {
					// #ifdef H5
					uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).seek(0)
					uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).pause()
					dataList.value[k.value].state = 'pause'
					audo.src = dataList.value[k.value].src
					// #endif
					// #ifdef MP
					uni.createVideoContext(dataList.value[k.value]._id + '' + k.value, proxy).play()
					// #endif
				}, 500)
			}, 200)
			videoID.value = dataList.value[k.value]._id
		}
	})
}

const openCommentPop = () => {
	commentPopup.value = true
	commentList.value = []
	page.value = 1
	postId.value = dataList.value[k.value].id
	getCommentList()
}

const onBack = () => {
	let pages = getCurrentPages()
	if (pages.length > 1) {
		uni.navigateBack()
	} else {
		uni.switchTab({
			url: '/pages/index/index'
		})
	}
}

// 删除评论
const delComment = (e, index, index2) => {
	let user = uni.getStorageSync("userInfo")

	if (e.uid != user.uid) {
		return
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
							commentList.value[index].children.splice(index2, 1)
						} else {
							commentList.value.splice(index, 1)
						}
					}
				})
			} else if (res.cancel) {
				// console.log('用户点击取消')
			}
		}
	})
}

const onCopy = () => {
	uni.setClipboardData({
		data: dataList.value[k.value].content,
		success: function() {
			uni.hideToast()
			$f.toast('复制成功', 'success')
		}
	})
}

const copyPageUrl = () => {
	if(!postId.value){
		postId.value = dataList.value[k.value].id
	}
	uni.setClipboardData({
		data: $c.shareH5Url + 'pages/post/video-detail?id=' + postId.value,
		success: function() {
			uni.hideToast()
			$f.toast('复制成功快分享给好友叭~', 'success')
		}
	})
}

// 评论触底
const reachBottom = () => {
	page.value++
	getCommentList()
}

const jumpUser = (uid) => {
	uni.navigateTo({
		url: '/pages/user/home?uid=' + uid
	})
}

// 点赞
const onThumbs = (id, index, index2) => {
	$H.post('comment/thumbs', {
		id: id
	}).then(res => {
		if (res.code == 0) {
			if (index2 || index2 == 0) {
				commentList.value[index].children[index2].isThumbs = true
				commentList.value[index].children[index2].thumbs++
			} else {
				commentList.value[index].isThumbs = true
				commentList.value[index].thumbs++
			}
		}
	})
}

// 取消点赞
const cancelThumbs = (id, index, index2) => {
	console.log(index + ',' + index2)
	$H.post('comment/cancelThumbs', {
		id: id
	}).then(res => {
		if (res.code == 0) {
			if (index2 || index2 == 0) {
				commentList.value[index].children[index2].isThumbs = false
				commentList.value[index].children[index2].thumbs--
			} else {
				commentList.value[index].isThumbs = false
				commentList.value[index].thumbs--
			}
		}
	})
}

// 回复评论
const onReply = (e) => {
	placeholder.value = '回复：' + e.userInfo.username
	focus.value = true

	let pid = e.pid
	if (pid === 0) {
		form.pid = e.id
	} else {
		form.pid = e.pid
	}

	form.toUid = e.userInfo.uid
	form.postId = postId.value
}

// 输入框失去焦点时
const onBlur = () => {
	placeholder.value = '说点什么...'
	focus.value = false
}

// 获取评论列表
const getCommentList = () => {
	if(!postId.value){
		postId.value = dataList.value[k.value].id
	}
	loadStatus.value = 'loading'
	$H.get('comment/list', {
		postId: postId.value,
		page: page.value
	}).then(res => {
		if (res.code == 0) {
			commentList.value = commentList.value.concat(res.result.data)

			if (res.result.current_page >= res.result.last_page || res.result.total === 0) {
				loadStatus.value = 'nomore'
			} else {
				loadStatus.value = 'loadmore'
			}
		}
	})
}

const cancelCollection = () => {
	$H.post('post/cancelCollection', {
		id: postId.value
	}).then(res => {
		if (res.code === 0) {
			dataList.value[k.value].collectionCount--
			dataList.value[k.value].isCollection = false
		}
	})
}

const addCollection = () => {
	if(!postId.value){
		postId.value = dataList.value[k.value].id
	}
	$H.post('post/addCollection', {
		id: postId.value,
		uid: dataList.value[k.value].uid
	}).then(res => {
		if (res.code === 0) {
			dataList.value[k.value].collectionCount++
			dataList.value[k.value].isCollection = true
		}
	})
}

//评论图片上传接口
const commentImgUpload = () => {
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
						uni.hideLoading()
					}
				}
			})
		}
	})
}

const deleteTempImage = () => {
	commentTempImgUrl.value = ''
	form.commentImg = ''
}

const commentPreviewImage = (url) => {
	uni.previewImage({
		urls: [url]
	})
}

const addComment = () => {
	if(!postId.value){
		postId.value = dataList.value[k.value].id
	}
	
	isSubmitD.value = true
	form.postId = postId.value
	if (form.content == '') {
		$u.toast('评论不能为空')
		isSubmitD.value = false
		return
	}

	uni.showLoading({
		mask: true,
		title: '发布中'
	})

	$H.post('post/addComment', form).then(res => {
		uni.hideLoading()
		if (res.code == 0) {
			if (res.result) {
				uni.showModal({
					title: '提示',
					content: '评论审核通过后发布哦，请耐心等待',
					showCancel: false,
					success: function(res) {
						if (res.confirm) {}
					}
				})
			} else {
				$u.toast('评论成功')
			}
			form.content = ''
			page.value = 1
			commentList.value = []
			getCommentList()
		} else if (res.code == 500) {
			$u.toast(res.msg)
		}
		form.commentImg = ''
		commentTempImgUrl.value = ''
		form.pid = 0
		form.toUid = ''
		isSubmitD.value = false
	})
}

const onMask = () => {
	commentPopup.value = false
}

const getPostDetail = () => {
	$H.get('post/detail', {
		id: postId.value
	}).then(res => {})
}

const remainComment = (item, index) => {
	$H.get('comment/remainComment', {
		id: item.id
	}).then(res => {
		if (res.code == 0) {
			commentList.value[index].children = commentList.value[index].children.concat(res.result)
			commentList.value[index].showBtn = false
		}
	})
}

const getVideoThumb = (url) => {
	return storageConfig.getVideoThumb(url)
}
</script>

<style lang="scss" scoped>
	.shortvideo {
		.videoHover {
			position: absolute;
			top: 0;
			left: 0;
			flex: 1;
			display: flex;
			background-color: rgba(0, 0, 0, 0.1);
			justify-content: center;
			align-items: center;

		}
		.playState {
			width: 160rpx;
			height: 160rpx;
			opacity: 0.2;
		}
		.video-content {
			width: 620rpx;
			z-index: 99;
			position: absolute;
			bottom: 60px;
			padding: 15rpx;
			flex-direction: column;
			justify-content: flex-start;
			color: #ffffff;
		}

		.video-userName {
			font-size: 30rpx;
			color: #ffffff;
			margin-top: 80upx;
		}

		.words {
			margin-top: 10rpx;
			font-size: 30rpx;
			color: #ffffff;
		}

		.root {
			background-color: #000000;
		}
	}



	.video_ {
		width: 100%;
		height: 100vh;
		display: block;
	}

	.tool-box {
		position: absolute;
		bottom: 164rpx;
		right: 30rpx;
		color: #fff;
		font-size: 24rpx;
		z-index: 99;

		.item {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			margin-bottom: 30rpx;

			.iconfont {
				font-size: 60rpx;
			}

			image {
				width: 50rpx;
				height: 50rpx;
			}
		}
	}

	/* 评论tool */
	.comment-tool {
		position: fixed;
		bottom: 100rpx;
		width: 100%;
		background-color: #fff;
		padding: 20rpx;
		display: flex;
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

	// 底部内容框
	.footer {
		position: absolute;
		bottom: 126rpx;
		width: 100%;
		color: #fff;
		padding: 20rpx;

		.username {
			margin-bottom: 20rpx;
			font-weight: 700;
			font-size: 38rpx;
		}
	}
	.search-wrap {
		margin-left: 20rpx;
		margin-right: 30%;
	}
	.v-footer {
		flex-direction: row;
		background-color: #4c4c4c;
		height: 96rpx;
		position: fixed;
		bottom: 0;
		z-index: 9;
		width: 750rpx;
		line-height: 100rpx;
	
		.items {
			position: relative;
			color: #999999;
			font-size: 30rpx;
	
			.cart {
				color: #999999;
				font-size: 30rpx;
			}
	
			&.on {
				color: #fff;
			}
		}
	}
	.v-info {
		display: -webkit-box;
		display: -moz-box;
		display: -webkit-flex;
		display: -ms-flexbox;
		display: flex;
		-webkit-box-lines: multiple;
		-moz-box-lines: multiple;
		-o-box-lines: multiple;
		-webkit-flex-wrap: wrap;
		-ms-flex-wrap: wrap;
		flex-wrap: wrap
	}
	.v-info.s-around {
		justify-content: space-around;
		-webkit-justify-content: space-around
	}
	.v-info.a-center {
		-webkit-box-align: center;
		-moz-box-align: center;
		-o-box-align: center;
		-ms-flex-align: center;
		-webkit-align-items: center;
		align-items: center
	}
	.discuss-title {
		font-size: 32rpx;
		color: #face15;
		margin-right: 10rpx;
	}
	.discuss-title-comment{
		font-size: 32rpx;
		color: #cd870b;
		margin-right: 10rpx;
	}
	.com {
		margin-left: 30rpx;
		font-size: 28rpx;
		color: #484848;
	}
	.topicName{
		font-weight: 600;
		margin-left: 6rpx;
		color: #676788;
	}
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
		background-image: linear-gradient(to right, #e69ba3, #e5666f);
		margin-left: 10rpx;
		margin-top: 14rpx;
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