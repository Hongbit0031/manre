import $store from '@/store/index.js';
import noticeUtil from '@/utils/noticeUtil.js';
import timeUtil from '@/utils/timeUtil.js';
import config from '@/utils/config.js';

// 连接状态枚举
const CONNECTION_STATE = {
    DISCONNECTED: 'disconnected',
    CONNECTING: 'connecting',
    CONNECTED: 'connected',
    RECONNECTING: 'reconnecting'
};

// 配置常量
const CONFIG = {
    HEARTBEAT_INTERVAL: 10000,     // 心跳间隔 10秒
    RECONNECT_INTERVAL: 5000,      // 基础重连间隔 5秒
    MAX_RECONNECT_ATTEMPTS: 5,     // 最大重连次数
    CONNECTION_TIMEOUT: 10000,     // 连接超时 10秒
    MAX_BACKOFF_DELAY: 30000,      // 最大退避延迟 30秒
    RETRY_DELAY: 1000              // 重试前的延迟 1秒
};

// 错误代码映射
const ERROR_CODES = {
    NORMAL_CLOSURE: 1000,
    PROTOCOL_ERROR: 1002,
    ABNORMAL_CLOSURE: 1006,
    INVALID_DATA: 1007,
    POLICY_VIOLATION: 1008
};

class WebSocketManager {
    constructor() {
        this.heartbeatTimer = null;
        this.reconnectTimer = null;
        this.connectionTimer = null;
        this.connectionPromise = null;
        
        this.reconnectAttempts = 0;
        this.connectionState = CONNECTION_STATE.DISCONNECTED;
        this.isEventBound = false;
        this.isDestroyed = false;
        
        // 绑定方法上下文
        this.handleOpen = this.handleOpen.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.handleError = this.handleError.bind(this);
        this.handleMessage = this.handleMessage.bind(this);
    }

    /**
     * 初始化连接
     * @returns {Promise} 连接Promise
     */
    async initConnect() {
        // 防止重复连接
        if (this.connectionPromise) {
            return this.connectionPromise;
        }

        // 检查登录状态
        if (!$store.state.token) {
            const error = new Error('Token不存在，WebSocket无法连接');
            console.error(error.message);
            throw error;
        }

        // 如果已销毁则不连接
        if (this.isDestroyed) {
            throw new Error('WebSocket管理器已销毁');
        }

        this.connectionPromise = this._connect();
        
        try {
            await this.connectionPromise;
            return true;
        } catch (error) {
            this.connectionPromise = null;
            throw error;
        }
    }

    /**
     * 内部连接实现
     * @private
     */
    _connect() {
        return new Promise((resolve, reject) => {
            // 清理之前的状态
            this._cleanup(false);
            
            this.connectionState = CONNECTION_STATE.CONNECTING;
            
            // 绑定事件监听器（只绑定一次）
            this._bindEventListeners();
            
            // 设置连接超时
            this.connectionTimer = setTimeout(() => {
                console.error('WebSocket连接超时');
                this.connectionState = CONNECTION_STATE.DISCONNECTED;
                uni.closeSocket();
                reject(new Error('连接超时'));
            }, CONFIG.CONNECTION_TIMEOUT);

            // 临时存储resolve/reject用于事件处理
            this._connectionResolve = resolve;
            this._connectionReject = reject;

            // 建立连接 - 保持原有的token传输方式
            uni.connectSocket({
                url: config.websocketUrl + $store.state.token,
                success: () => {
                    console.log('WebSocket连接请求发送成功');
                },
                fail: (error) => {
                    console.error('WebSocket连接请求失败:', error);
                    this._clearConnectionTimer();
                    this.connectionState = CONNECTION_STATE.DISCONNECTED;
                    reject(error);
                }
            });
        });
    }

    /**
     * 绑定事件监听器
     * @private
     */
    _bindEventListeners() {
        if (this.isEventBound) return;

        uni.onSocketOpen(this.handleOpen);
        uni.onSocketClose(this.handleClose);
        uni.onSocketError(this.handleError);
        uni.onSocketMessage(this.handleMessage);
        
        this.isEventBound = true;
        console.log('WebSocket事件监听器已绑定');
    }

    /**
     * 解绑事件监听器
     * @private
     */
    _unbindEventListeners() {
        if (!this.isEventBound) return;

        try {
            uni.offSocketOpen(this.handleOpen);
            uni.offSocketClose(this.handleClose);
            uni.offSocketError(this.handleError);
            uni.offSocketMessage(this.handleMessage);
        } catch (error) {
            console.warn('解绑事件监听器时出错:', error);
        }
        
        this.isEventBound = false;
        console.log('WebSocket事件监听器已解绑');
    }

    /**
     * 处理连接打开
     * @private
     */
    handleOpen() {
        console.log('WebSocket连接已打开');
        
        this._clearConnectionTimer();
        this.connectionState = CONNECTION_STATE.CONNECTED;
        $store.state.isSocketOpen = true;
        
        // 重置重连计数
        this.reconnectAttempts = 0;
        
        // 开始心跳
        this._startHeartbeat();
        
        // 通知连接成功
        if (this._connectionResolve) {
            this._connectionResolve();
            this._connectionResolve = null;
            this._connectionReject = null;
        }
        
        this.connectionPromise = null;
    }

    /**
     * 处理连接关闭
     * @private
     */
    handleClose(event) {
        console.log('WebSocket连接已关闭', event);
        
        this._clearConnectionTimer();
        this._stopHeartbeat();
        
        const previousState = this.connectionState;
        this.connectionState = CONNECTION_STATE.DISCONNECTED;
        $store.state.isSocketOpen = false;
        
        // 通知连接失败（如果在连接过程中）
        if (this._connectionReject && previousState === CONNECTION_STATE.CONNECTING) {
            this._connectionReject(new Error('连接被关闭'));
            this._connectionResolve = null;
            this._connectionReject = null;
        }
        
        this.connectionPromise = null;
        
        // 如果不是主动销毁，则尝试重连
        if (!this.isDestroyed) {
            this._scheduleReconnect();
        }
    }

    /**
     * 处理连接错误
     * @private
     */
    handleError(error) {
        console.error('WebSocket连接错误:', error);
        
        this._clearConnectionTimer();
        this._stopHeartbeat();
        
        const previousState = this.connectionState;
        this.connectionState = CONNECTION_STATE.DISCONNECTED;
        $store.state.isSocketOpen = false;
        
        // 通知连接失败
        if (this._connectionReject && previousState === CONNECTION_STATE.CONNECTING) {
            this._connectionReject(error);
            this._connectionResolve = null;
            this._connectionReject = null;
        }
        
        this.connectionPromise = null;
        
        // 根据错误代码决定是否重连
        if (!this.isDestroyed && this._shouldReconnect(error)) {
            this._scheduleReconnect();
        }
    }

    /**
     * 处理接收到的消息
     * @private
     */
    handleMessage(res) {
        try {
            const data = JSON.parse(res.data);
            
            // 验证消息格式
            if (!this._validateMessage(data)) {
                console.error('收到无效消息格式:', res.data);
                return;
            }
            
            this._processMessage(data);
        } catch (error) {
            console.error('消息解析错误:', error);
            console.error('原始消息内容:', res.data);
        }
    }

    /**
     * 开始心跳检测
     * @private
     */
    _startHeartbeat() {
        this._stopHeartbeat();
        
        this.heartbeatTimer = setInterval(() => {
            if (this.connectionState !== CONNECTION_STATE.CONNECTED) {
                this._stopHeartbeat();
                return;
            }
            
            const pingMessage = {
                type: 'ping',
                data: {
                    timestamp: Date.now()
                }
            };
            
            this._sendMessage(pingMessage).catch(error => {
                console.error('发送心跳失败:', error);
                this._stopHeartbeat();
                this._scheduleReconnect();
            });
        }, CONFIG.HEARTBEAT_INTERVAL);
    }

    /**
     * 停止心跳检测
     * @private
     */
    _stopHeartbeat() {
        if (this.heartbeatTimer) {
            clearInterval(this.heartbeatTimer);
            this.heartbeatTimer = null;
        }
    }

    /**
     * 发送消息
     * @param {Object} message 消息对象
     * @returns {Promise}
     */
    _sendMessage(message) {
        return new Promise((resolve, reject) => {
            if (this.connectionState !== CONNECTION_STATE.CONNECTED) {
                reject(new Error('WebSocket未连接'));
                return;
            }
            
            uni.sendSocketMessage({
                data: JSON.stringify(message),
                success: () => {
                    resolve();
                },
                fail: (error) => {
                    reject(error);
                }
            });
        });
    }

    /**
     * 验证消息格式
     * @param {Object} data 消息数据
     * @returns {boolean}
     * @private
     */
    _validateMessage(data) {
        if (!data || typeof data !== 'object') {
            return false;
        }
        
        if (!data.type || typeof data.type !== 'string') {
            return false;
        }
        
        // 可以添加更多验证规则
        const validTypes = [
            'ping', 'pong', 'person-message', 'token-failed', 
            'person-apply', 'count', 'person-apply-agree', 
            'notice-refresh', 'person-withdraw'
        ];
        
        return validTypes.includes(data.type);
    }

    /**
     * 处理业务消息
     * @param {Object} data 消息数据
     * @private
     */
    _processMessage(data) {
        switch (data.type) {
            case 'ping':
                // 响应pong
                this._sendMessage({ type: 'pong', data: { timestamp: Date.now() } });
                break;
                
            case 'pong':
                // 心跳响应，无需处理
                break;
                
            case 'person-message':
                this._handlePersonMessage(data.data);
                break;
                
            case 'token-failed':
                this._handleTokenFailed();
                break;
                
            case 'person-apply':
                this._handlePersonApply(data.data);
                break;
                
            case 'count':
                $store.state.onlineArray = data.data;
                break;
                
            case 'person-apply-agree':
                this._handlePersonApplyAgree(data.data);
                break;
                
            case 'notice-refresh':
                this._handleNoticeRefresh(data.data);
                break;
                
            case 'person-withdraw':
                this._handlePersonWithdraw(data.data);
                break;
                
            default:
                console.warn('未知消息类型:', data.type);
        }
    }

    /**
     * 处理个人消息
     * @private
     */
    _handlePersonMessage(message) {
        // 消息加入消息历史列表
        for (let i = 0; i < $store.state.personMessage.length; i++) {
            if ($store.state.personMessage[i].sessionId == message.sessionId) {
                $store.state.personMessage[i].list.push(message);
                break;
            }
        }
        
        // 更新消息记录
        $store.state.lastUpdateSession = {
            id: message.sessionId,
            count: $store.state.lastUpdateSession.count + 1
        };
        
        // 如果消息不是本人发的则响铃
        if (message.senderId != $store.state.loginUserInfo.uid) {
            if ($store.state.systemConfig.bell) {
                noticeUtil.playMessageAudio();
            }
            // #ifdef APP
            if ($store.state.systemConfig.vibrate) {
                uni.vibrateShort();
            }
            // #endif
        }
        
        // 更新会话列表
        for (let i = 0; i < $store.state.sessionList.length; i++) {
            if ($store.state.sessionList[i].sessionId == message.sessionId) {
                $store.state.sessionList[i].updateTime = message.updateTime;
                
                switch (message.messageType) {
                    case 'image':
                        $store.state.sessionList[i].lastMessage = '【图片】';
                        break;
                    case 'file':
                        $store.state.sessionList[i].lastMessage = '【文件】';
                        break;
                    case 'video':
                        $store.state.sessionList[i].lastMessage = '【视频】';
                        break;
                    default:
                        $store.state.sessionList[i].lastMessage = message.content;
                }
                
                // 没有在这个对话 未读++
                if ($store.state.chattingUserInfo == null || 
                    $store.state.chattingUserInfo.sessionId != $store.state.sessionList[i].sessionId) {
                    if (message.senderId != $store.state.loginUserInfo.uid) {
                        $store.state.sessionList[i].unread++;
                    }
                } else {
                    // 否则已读
                    $store.dispatch('clearUnread', {
                        myId: $store.state.loginUserInfo.uid,
                        friendId: $store.state.chattingUserInfo.chattingUserId
                    });
                }
                
                // 需要把对话移到第一位
                let session = $store.state.sessionList.splice(i, 1);
                $store.state.sessionList = session.concat($store.state.sessionList);
                break;
            }
        }
        
        $store.dispatch('countUnreadMessage');
    }

    /**
     * 处理Token失效
     * @private
     */
    _handleTokenFailed() {
        uni.showToast({
            title: '身份验证已过期',
            icon: 'none'
        });
        
        uni.removeStorageSync("hasLogin");
        uni.removeStorageSync("token");
        uni.removeStorageSync("userInfo");
        
        setTimeout(() => {
            uni.redirectTo({
                url: 'pages/user/go-login'
            });
        }, 1000);
    }

    /**
     * 处理好友申请
     * @private
     */
    _handlePersonApply(data) {
        uni.showToast({
            icon: 'none',
            title: '你收到了一个好友请求'
        });
        
        $store.dispatch('getNoticeList');
        
        if ($store.state.systemConfig.bell) {
            noticeUtil.playSystemAudio();
        }
        // #ifdef APP
        if ($store.state.systemConfig.vibrate) {
            uni.vibrateShort();
        }
        // #endif
    }

    /**
     * 处理好友申请同意
     * @private
     */
    _handlePersonApplyAgree(friend) {
        $store.dispatch('getFriendList');
        
        if (friend.myId == $store.state.loginUserInfo.uid) {
            $store.dispatch('getNoticeList');
            uni.showToast({
                title: '添加成功',
                icon: 'success'
            });
        }
    }

    /**
     * 处理通知刷新
     * @private
     */
    _handleNoticeRefresh(message) {
        $store.dispatch('getNoticeList');
        uni.showToast({
            title: message,
            icon: 'none'
        });
    }

    /**
     * 处理消息撤回
     * @private
     */
    _handlePersonWithdraw(data) {
        for (let i = 0; i < $store.state.personMessage.length; i++) {
            if ($store.state.personMessage[i].sessionId == data.sessionId) {
                for (let j = $store.state.personMessage[i].list.length - 1; j >= 0; j--) {
                    if ($store.state.personMessage[i].list[j].id == data.messageId) {
                        $store.state.personMessage[i].list[j].isWithdrawn = 1;
                        break;
                    }
                }
                break;
            }
        }
    }

    /**
     * 判断是否应该重连
     * @param {Object} error 错误对象
     * @returns {boolean}
     * @private
     */
    _shouldReconnect(error) {
        // 某些错误类型不应该重连
        if (error && error.code) {
            switch (error.code) {
                case ERROR_CODES.PROTOCOL_ERROR:
                case ERROR_CODES.INVALID_DATA:
                case ERROR_CODES.POLICY_VIOLATION:
                    return false;
                default:
                    return true;
            }
        }
        return true;
    }

    /**
     * 计划重连
     * @private
     */
    _scheduleReconnect() {
        if (this.reconnectTimer) {
            clearTimeout(this.reconnectTimer);
            this.reconnectTimer = null;
        }
        
        if (this.reconnectAttempts >= CONFIG.MAX_RECONNECT_ATTEMPTS) {
            console.log('达到最大重连次数，停止重连');
            return;
        }
        
        this.connectionState = CONNECTION_STATE.RECONNECTING;
        
        // 指数退避算法
        const backoffDelay = Math.min(
            CONFIG.RECONNECT_INTERVAL * Math.pow(2, this.reconnectAttempts),
            CONFIG.MAX_BACKOFF_DELAY
        );
        
        console.log(`准备第${this.reconnectAttempts + 1}次重连，延迟${backoffDelay}ms`);
        
        this.reconnectTimer = setTimeout(async () => {
            this.reconnectAttempts++;
            
            try {
                await this.initConnect();
                console.log('重连成功');
            } catch (error) {
                console.error('重连失败:', error);
                // 重连失败会触发错误处理，自动安排下次重连
            }
        }, backoffDelay);
    }

    /**
     * 清理连接定时器
     * @private
     */
    _clearConnectionTimer() {
        if (this.connectionTimer) {
            clearTimeout(this.connectionTimer);
            this.connectionTimer = null;
        }
    }

    /**
     * 清理资源
     * @param {boolean} unbindEvents 是否解绑事件
     * @private
     */
    _cleanup(unbindEvents = true) {
        this._stopHeartbeat();
        this._clearConnectionTimer();
        
        if (this.reconnectTimer) {
            clearTimeout(this.reconnectTimer);
            this.reconnectTimer = null;
        }
        
        if (unbindEvents) {
            this._unbindEventListeners();
        }
        
        if ($store.state.isSocketOpen) {
            uni.closeSocket();
        }
        
        $store.state.isSocketOpen = false;
        this.connectionState = CONNECTION_STATE.DISCONNECTED;
    }

    /**
     * 主动断开连接
     */
    disconnect() {
        console.log('主动断开WebSocket连接');
        this._cleanup();
        this.reconnectAttempts = 0;
        this.connectionPromise = null;
    }

    /**
     * 销毁WebSocket管理器
     */
    destroy() {
        console.log('销毁WebSocket管理器');
        this.isDestroyed = true;
        this.disconnect();
    }

    /**
     * 获取当前连接状态
     * @returns {string}
     */
    getConnectionState() {
        return this.connectionState;
    }

    /**
     * 检查是否已连接
     * @returns {boolean}
     */
    isConnected() {
        return this.connectionState === CONNECTION_STATE.CONNECTED;
    }

    /**
     * 手动重置重连计数
     */
    resetReconnectAttempts() {
        this.reconnectAttempts = 0;
    }
}

// 创建单例实例
const webSocketManager = new WebSocketManager();

// 导出兼容原API的方法
export default {
    // 保持向后兼容
    sendHeader() {
        // 已在新实现中自动处理
        console.warn('sendHeader方法已废弃，心跳检测已自动处理');
    },
    
    initConnect() {
        return webSocketManager.initConnect();
    },
    
    reconnect() {
        return webSocketManager.initConnect();
    },
    
    cleanup() {
        webSocketManager.disconnect();
    },
    
    // 新增方法
    disconnect() {
        webSocketManager.disconnect();
    },
    
    destroy() {
        webSocketManager.destroy();
    },
    
    getConnectionState() {
        return webSocketManager.getConnectionState();
    },
    
    isConnected() {
        return webSocketManager.isConnected();
    },
    
    resetReconnectAttempts() {
        webSocketManager.resetReconnectAttempts();
    }
};