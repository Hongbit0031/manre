<template>
<view class="container">
  <view class="my-lottery row-between wrap" v-if="status==1">
    <view v-for="(item, index) in lotteryData" :key="index" :class="(index == 4 ? 'lotty-btn' : 'lottery-item') + ' column-center ' + (activeIndex == index ? 'active' : '')" style="width: 180rpx;height: 180rpx;" @tap="onLotteryClick(item.type)">
      <image :src="item.url" style="width: 80rpx;height: 80rpx"></image>
	  <text  v-if="item.type == 1&&item.number" class='xs mt20' style="'color: #ED3720';font-weight: 600;'">{{item.number}}{{item.name}}</text>
	  <text  v-else-if="item.type == 1 && !item.number" class='xs mt20' style="'color: #ED3720';font-weight: 600;'">{{item.name}}</text>
	  <text  v-else-if="item.type == 2" class='nr mt10' style="'color: #743C3C';font-weight: 600;'">{{item.name}}</text>
	  <text  v-else-if="item.type == 3" class='nr mt10' style="'color: #aaaaff';font-weight: 600;'">￥{{item.number}}{{item.name}}</text>
	  <text  v-else-if="item.type == 4" class='nr mt10' style="'color: #aaaaff';font-weight: 600;'">{{item.number}}件{{item.name}}</text>
    </view>
  </view>
  <view class="activity-null row-center" v-else>
    活动未开始哦~
  </view>
</view>
</template>

<script setup>
import { ref, getCurrentInstance, onBeforeMount } from 'vue'

// 顺序
const LOTTERY_ORDER = [0, 1, 2, 5, 8, 7, 6, 3]

const { proxy } = getCurrentInstance()
const $H = proxy.$H

const props = defineProps({
	// 转盘数据
	lotteryData: {
		type: Array,
		default: () => []
	},
	// 最少转动多少圈
	circleTimes: {
		type: Number,
		default: 30
	},
	status: {
		type: [Number, String],
		default: 0
	}
})

const emit = defineEmits(['begin', 'finish'])

// 是否在抽奖中
const isLottery = ref(false)
// 表示位移的总次数
const currentIndex = ref(0)
// 中奖索引
const luckyOrder = ref(-1)
// 转盘的旋转速度，实际是转盘旋转间隔时间setTimeout的值
const speed = ref(200)
// 当前活跃的坐标
const activeIndex = ref(-1)
const result = ref('')

let lotteryTimer = null

onBeforeMount(() => {
	lotteryTimer = null
})

function onLotteryClick(type) {
	if (type == 1) {
		start()
	}
}

function userLotteryFun() {
	$H.get('luckDraw/start').then(res => {
		if (res.code == 0) {
			let { id } = res.result
			let index = props.lotteryData.findIndex(item => {
				return item.id == id
			})
			
			switch (index) {
				case 0:
					index = 0
					break
				case 1:
					index = 1
					break
				case 2:
					index = 2
					break
				case 3:
					index = 7
					break
				case 5:
					index = 3
					break
				case 6:
					index = 6
					break
				case 7:
					index = 5
					break
				case 8:
					index = 4
					break
				default:
					index = -1
					break
			}
			
			luckyOrder.value = index
			result.value = res.result.text
			
			emit("begin")
			startLotteryFun()
		}
	})
}

function startLotteryFun() {
	let currIdx = currentIndex.value
	let activeIdx = getHighLightItemIndexFun(currIdx)
	activeIndex.value = activeIdx
	
	const currentOrder = currIdx % 8
	currentIndex.value = ++currIdx
	
	if (currIdx > props.circleTimes + 8 && luckyOrder.value == currentOrder) {
		if (lotteryTimer) {
			clearTimeout(lotteryTimer)
		}
		
		setTimeout(() => {
			stopCallbackFun(LOTTERY_ORDER[luckyOrder.value])
			setTimeout(() => {
				reset()
				activeIndex.value = -1
			}, 1000)
		}, 500)
	} else {
		if (currIdx < props.circleTimes) {
			speed.value -= 10
		} else if (currIdx > props.circleTimes + 8 && luckyOrder.value == currentOrder + 1) {
			speed.value += 80
		} else {
			speed.value += 20
		}
		
		if (speed.value < 50) {
			speed.value = 50
		}
		
		lotteryTimer = setTimeout(startLotteryFun, speed.value)
	}
}

// luckyIndex: 中奖在列表中的index
function stopCallbackFun(luckyIndex) {
	emit("finish", {
		detail: result.value
	})
}

// 根据 currentIndex 获取当前应该高亮列表中的第几个奖品
function getHighLightItemIndexFun(currIdx) {
	return LOTTERY_ORDER[currIdx % LOTTERY_ORDER.length]
}

function start() {
	// 如果还没开始转动,开始转动转盘
	if (!isLottery.value) {
		isLottery.value = true
		userLotteryFun()
	}
}

// 重设转盘
function reset() {
	isLottery.value = false
	currentIndex.value = 0
	speed.value = 100
	luckyOrder.value = -1
}
</script>
<style>
.my-lottery {
  background-image: url(../../static/images/choujiang_light.png);
  width: 640rpx;
  height: 640rpx;
  background-size: 100% 100%;
  padding: 35rpx 40rpx;
}

.my-lottery .lottery-item {
  background-image: url(../../static/images/choujiang_block.png);
  background-size: 100% 100%;
}

.my-lottery .lotty-btn {
  background-image: url(../../static/images/choujiang_button.png);
  background-size: 100% 100%;
}

.container .activity-null {
  color: #FEE5BF;
  font-size: 40rpx;
  width: 640rpx;
  height: 640rpx;
}

.active {
  opacity: 0.7;
}

.row-between {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.wrap {
	flex-wrap: wrap;
}

.column-center {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}
.mt10 {
    margin-top: 10rpx;
}

.mt20 {
    margin-top: 20rpx;
}
.row-center {
    display: flex;
    align-items: center;
    justify-content: center;
}
.xs {
    font-size: 24rpx;
}
.nr {
  font-size: 28rpx;
}
</style>