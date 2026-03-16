<template>
  <view class="integral">
    <view class="integral__header">
      <view class="integral__header-score">我的积分</view>
      <view>{{ info.integral || 0 }}</view>
      <view class="integral__header-line"></view>
    </view>
    <view class="integral__content">
      <view class="integral__nav acea-row">
        <view
          class="integral__nav-item acea-row row-center-wrapper"
          :class="{'integral__nav-item--active': current === navListIndex}"
          v-for="(item, navListIndex) in navList"
          :key="navListIndex"
          @click="nav(navListIndex)"
        >
          <text class="iconfont" :class="item.icon"></text>
          {{ item.name }}
        </view>
      </view>
      
      <!-- 积分明细列表 -->
      <view class="integral__list" v-show="current === 0">
        <view
          class="integral__list-item"
          v-for="(item, listIndex) in list"
          :key="listIndex"
        >
          <view class="integral__list-item-content">
            <text class="state">{{ item.title }}</text>
            <view class="mark" v-if="item.mark">{{ item.mark }}</view>
            <view class="date">{{ item.addTime }}</view>
          </view>
          <text 
            class="integral__list-item-number"
            :class="item.pm == 1 ? 'integral__list-item-number--plus' : 'integral__list-item-number--minus'"
          >{{ item.pm == 1 ? '+' : '-' }}{{ item.number }}</text>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view v-if="current === 0 && list.length === 0 && loadStatus === 'nomore'" class="empty-state">
        <text class="empty-icon">📭</text>
        <text class="empty-text">暂无积分明细</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $c = proxy.$c
const integralHeaderBg = `url('${$c.imgResource}/images/interal-bg.png')`

const navList = ref([
	{ name: "积分明细" }
])
const current = ref(0)
const where = ref({
	page: 1,
	limit: 10
})
const list = ref([])
const info = ref({})
const type = ref(0) // 分类
const loadStatus = ref('loading') // loading, loadmore, nomore

onLoad((options) => {
	if (options.type) {
		type.value = options.type
	}
	getUserInfo()
	getIntegralList()
})

onReachBottom(() => {
	where.value.page++
	getIntegralList()
})

function nav(index) {
	current.value = index
}

function getIntegralList() {
	loadStatus.value = 'loading'
	$H.get('sign/integralList', {
		page: where.value.page,
		limit: where.value.limit,
		type: type.value
	}).then(res => {
		const data = Array.isArray(res.result) ? res.result : []
		
		if (where.value.page === 1) {
			list.value = data
		} else {
			list.value = list.value.concat(data)
		}
		
		// 更新加载状态
		if (data.length < where.value.limit) {
			loadStatus.value = 'nomore'
		} else {
			loadStatus.value = 'loadmore'
		}
	}).catch(err => {
		console.error('获取积分列表失败：', err)
		loadStatus.value = 'nomore'
	})
}

function getUserInfo() {
	$H.post('sign/userInfo', {}).then(res => {
		info.value = res.result
	})
}
</script>
<style lang="scss" scoped>
.integral {
  &__header {
    background-image: v-bind(integralHeaderBg);
    background-repeat: no-repeat;
    background-size: 100% 100%;
    width: 100%;
    height: 360rpx;
    font-size: 72rpx;
    color: #fff;
    padding: 31rpx 0 45rpx 0;
    text-align: center;
    font-family: 'GuildfordProBook 5';

    &-score {
      font-size: 26rpx;
      color: rgba(255, 255, 255, 0.8);
      text-align: center;
      margin-bottom: 5rpx;
    }

    &-line {
      width: 60rpx;
      height: 3rpx;
      background-color: #fff;
      margin: 20rpx auto 0 auto;
    }
  }

  &__nav {
    display: flex;
    width: 690rpx;
    border-radius: 20rpx 20rpx 0 0;
    margin: -96rpx auto 0 auto;
    background-color: #f7f7f7;
    height: 96rpx;
    font-size: 30rpx;
    color: #bbb;

    &-item {
      text-align: center;
      width: 100%;

      &--active {
        background-color: #fff;
        color: #00aaff;
        font-weight: bold;
        border-radius: 20rpx 20rpx 0 0;
      }

      .iconfont {
        font-size: 38rpx;
        margin-right: 10rpx;
      }
    }
  }

  &__list {
    background-color: #fff;
    padding: 24rpx 30rpx;
    min-height: 400rpx;

    &-item {
      height: auto;
      min-height: 124rpx;
      border-bottom: 1px solid #eee;
      font-size: 24rpx;
      color: #999;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 0;

      &-content {
        flex: 1;
        padding-right: 20rpx;
        
        .state {
          font-size: 28rpx;
          color: #282828;
          margin-bottom: 8rpx;
          display: block;
        }

        .mark {
          font-size: 24rpx;
          color: #666;
          margin-bottom: 8rpx;
          line-height: 1.4;
          display: block;
        }

        .date {
          font-size: 24rpx;
          color: #999;
        }
      }

      &-number {
        font-size: 36rpx;
        font-family: 'GuildfordProBook 5';
        white-space: nowrap;
        
        &--plus {
          color: #ff0000;
        }

        &--minus {
          color: #ff0000;
        }
      }
    }

    &-tip {
      font-size: 25rpx;
      width: 690rpx;
      height: 60rpx;
      border-radius: 50rpx;
      background-color: #fff5e2;
      border: 1px solid #ffeac1;
      color: #c8a86b;
      padding: 0 20rpx;
      margin-bottom: 24rpx;
      display: flex;
      align-items: center;

      .iconfont {
        font-size: 35rpx;
        margin-right: 15rpx;
      }
    }
  }

  &__rewards {
    background-color: #fff;
    padding: 24rpx 0;

    &-item {
      background-image: linear-gradient(to right, #fff7e7 0%, #fffdf9 100%);
      width: 690rpx;
      height: 180rpx;
      position: relative;
      border-radius: 10rpx;
      margin: 0 auto 20rpx auto;
      padding: 0 25rpx 0 180rpx;
      display: flex;
      align-items: center;
      justify-content: space-between;

      &-image {
        width: 90rpx;
        height: 150rpx;
        position: absolute;
        bottom: 0;
        left: 45rpx;

        image {
          width: 100%;
          height: 100%;
        }
      }

      &-name {
        width: 285rpx;
        font-size: 30rpx;
        font-weight: bold;
        color: #c8a86b;
      }

      &-earn {
        font-size: 26rpx;
        color: #c8a86b;
        border: 2rpx solid #c8a86b;
        text-align: center;
        line-height: 52rpx;
        height: 52rpx;
        width: 160rpx;
        border-radius: 50rpx;
      }
    }
  }
  
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80rpx 0;
    background-color: #fff;
    
    .empty-icon {
      font-size: 80rpx;
      margin-bottom: 20rpx;
    }
    
    .empty-text {
      font-size: 28rpx;
      color: #999;
    }
  }
}
</style>
