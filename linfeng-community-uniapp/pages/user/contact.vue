<template>
    <view class="lf-contact">
        <view class="header">
            <view class="header-content">
                <view class="header-title">联系客服</view>
                <view class="header-subtitle">本平台竭诚为您服务</view>
            </view>
        </view>
        
        <view class="content">
            <view class="contact-card">
                <!-- 客服头像区域 -->
                <view class="avatar-section">
                    <view class="avatar-container">
                        <image class="avatar-img" :src="server.image" mode="aspectFill" />
                        <view class="avatar-border"></view>
                    </view>
                   
                </view>
                
                <!-- 微信联系区域 -->
                <view class="wechat-section">
                    <view class="section-title">
                        <image class="wechat-icon" src="/static/images/wechat-icon.png" />
                        <text>微信客服</text>
                    </view>
                    
                    <!-- #ifdef H5 || APP-PLUS -->
                    <view class="action-btn primary-btn" @click="onCopy(server.wechat)">
                        <image class="btn-icon" src="/static/images/wechat-btn-icon.png" />
                        <text>添加微信</text>
                    </view>
                    <!-- #endif -->
                    
                    <!-- #ifdef MP-WEIXIN -->
                    <button open-type="contact" class="action-btn primary-btn">
                        <text>小程序客服</text>
                    </button>
                    <!-- #endif -->
                    
                    <view class="service-time">
                        <image class="time-icon" src="/static/images/time-icon.png" />
                        <text>{{server.time}}</text>
                    </view>
                </view>
                
                <!-- 电话联系区域 -->
                <view class="phone-section">
                    <view class="section-title">
                        <image class="phone-icon" src="/static/images/phone-icon.png" />
                        <text>电话客服</text>
                    </view>
                    
                    <view class="phone-info">
                        <text class="phone-number">{{server.phone}}</text>
                        <view class="phone-actions">
                            <!-- #ifdef H5 -->
                            <view class="action-btn call-btn" @click="onCall">
                                <text>拨打</text>
                            </view>
                            <!-- #endif -->
                            <!-- #ifdef MP-WEIXIN -->
                            <view class="action-btn call-btn" @click="showTelTips">
                                <text>拨打</text>
                            </view>
                            <!-- #endif -->
                            <view class="action-btn copy-btn" @click="onCopy2(server.phone)">
                                <text>复制</text>
                            </view>
                        </view>
                    </view>
                </view>
                
                <!-- 提示信息 -->
                <view class="tips-section">
                    <view class="tips-text">
                        <image class="info-icon" src="/static/images/info-icon.png" />
                        <text>如无法添加微信，可拨打客服电话</text>
                    </view>
                </view>
            </view>
        </view>
        
        <!-- 拨打电话确认弹窗 -->
        <u-modal 
            :content="content"
            v-model="showPhoneCall"
            show-cancel-button
            confirm-text='呼叫'
            @confirm="onCall"
        >
        </u-modal>
    </view>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const { proxy } = getCurrentInstance()
const $H = proxy.$H
const $f = proxy.$f

const server = ref({})
const showPhoneCall = ref(false)
const content = ref('即将打电话给')

onLoad(() => {
    getContact()
})

function getContact(){
    $H.get('user/getContact').then(res => {
        if(res.code == 0){
            server.value = res.result
        }
    })
}

function onCopy(str) {
    uni.setClipboardData({
        data: str,
        success: () => {
            uni.hideToast()
            $f.toast('微信号复制成功', 'success')
        }
    })
}

function onCopy2(str) {
    uni.setClipboardData({
        data: str,
        success: () => {
            uni.hideToast()
            $f.toast('手机号复制成功', 'success')
        }
    })
}

function showTelTips() {
    showPhoneCall.value = true
    content.value = '即将打电话给' + server.value.phone
}

function onCall() {
    // #ifdef H5
    window.location.href = 'tel:' + server.value.phone
    // #endif
    // #ifdef MP-WEIXIN
    wx.makePhoneCall({
        phoneNumber: server.value.phone.toString(),
        success(e) {
            console.log('成功', e)
        },
        fail(err) {
            console.log('失败', err)
        }
    })
    // #endif
}
</script>

<style lang="scss">
    .lf-contact {
        min-height: 100vh;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        position: relative;
        
        .header {
            height: 300rpx;
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            
            &::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 20"><path d="M0,10 Q25,0 50,10 T100,10 L100,20 L0,20 Z" fill="rgba(255,255,255,0.1)"/></svg>');
                background-size: cover;
            }
            
            .header-content {
                text-align: center;
                z-index: 1;
                
                .header-title {
                    font-size: 48rpx;
                    font-weight: 600;
                    color: #ffffff;
                    margin-bottom: 16rpx;
                }
                
                .header-subtitle {
                    font-size: 28rpx;
                    color: rgba(255, 255, 255, 0.8);
                }
            }
        }
        
        .content {
            padding: 0 40rpx;
            margin-top: -80rpx;
            
            .contact-card {
                background: #ffffff;
                border-radius: 32rpx;
                padding: 60rpx 40rpx 40rpx;
                box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.1);
                position: relative;
                
                .avatar-section {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    margin-bottom: 60rpx;
                    
                    .avatar-container {
                        position: relative;
                        margin-bottom: 20rpx;
                        
                        .avatar-img {
                            width: 300rpx;
                            height: 300rpx;
                            border-radius: 20rpx;
                        }
                        
                        .avatar-border {
                            position: absolute;
                            top: -8rpx;
                            left: -8rpx;
                            right: -8rpx;
                            bottom: -8rpx;
                            border-radius: 20rpx;
                        }
                    }
                    
                    .service-badge {
                        display: none;
                    }
                }
                
                .wechat-section, .phone-section {
                    margin-bottom: 50rpx;
                    
                    .section-title {
                        display: flex;
                        align-items: center;
                        margin-bottom: 30rpx;
                        font-size: 32rpx;
                        font-weight: 600;
                        color: #333333;
                        
                        image {
                            width: 32rpx;
                            height: 32rpx;
                            margin-right: 16rpx;
                        }
                    }
                    
                    .action-btn {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        height: 88rpx;
                        border-radius: 44rpx;
                        font-size: 32rpx;
                        font-weight: 500;
                        margin-bottom: 24rpx;
                        transition: all 0.3s ease;
                        
                        .btn-icon {
                            width: 32rpx;
                            height: 32rpx;
                            margin-right: 12rpx;
                        }
                        
                        &.primary-btn {
                            background: linear-gradient(135deg, #667eea, #764ba2);
                            color: #ffffff;
                            box-shadow: 0 8rpx 24rpx rgba(102, 126, 234, 0.3);
                            
                            &:active {
                                transform: translateY(2rpx);
                                box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
                            }
                        }
                        
                        &.call-btn {
                            background: linear-gradient(135deg, #667eea, #764ba2);
                            color: #ffffff;
                            flex: 1;
                            margin-right: 20rpx;
                            margin-bottom: 0;
                        }
                        
                        &.copy-btn {
                            background: #f8f9fa;
                            color: #666666;
                            border: 2rpx solid #e9ecef;
                            flex: 1;
                            margin-bottom: 0;
                        }
                    }
                    
                    .service-time {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        color: #999999;
                        font-size: 26rpx;
                        
                        .time-icon {
                            width: 24rpx;
                            height: 24rpx;
                            margin-right: 12rpx;
                        }
                    }
                }
                
                .phone-section {
                    .phone-info {
                        .phone-number {
                            display: block;
                            text-align: center;
                            font-size: 36rpx;
                            font-weight: 600;
                            color: #333333;
                            margin-bottom: 30rpx;
                            letter-spacing: 2rpx;
                        }
                        
                        .phone-actions {
                            display: flex;
                            gap: 20rpx;
                        }
                    }
                }
                
                .tips-section {
                    .tips-text {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        background: #f8f9fa;
                        padding: 24rpx;
                        border-radius: 16rpx;
                        color: #666666;
                        font-size: 26rpx;
                        line-height: 1.5;
                        
                        .info-icon {
                            width: 24rpx;
                            height: 24rpx;
                            margin-right: 12rpx;
                            opacity: 0.7;
                        }
                    }
                }
            }
        }
    }
    
    // 按钮点击效果
    .action-btn:active {
        opacity: 0.8;
        transform: scale(0.98);
    }
    
    // 响应式适配
    @media (max-width: 750rpx) {
        .lf-contact .content .contact-card {
            margin: -60rpx 20rpx 0;
            padding: 40rpx 30rpx;
        }
    }
</style>