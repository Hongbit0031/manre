<template>
	<view class="filter-page">
		<!-- 筛选按钮 -->
		<view class="filter-trigger" @tap="showFilterPopup">
			<u-icon name="grid" size="34" color="#1d1d1d"></u-icon>
		</view>

		<!-- 筛选弹框 -->
		<u-popup v-model="showPopup" mode="bottom" border-radius="24" height="80%">
			<view class="filter-popup">
				<!-- 标题栏 -->
				<view class="popup-header">
					<text>切换</text>
				</view>

				<!-- 内容区域 -->
				<view class="popup-content">
					<!-- 排序选择 -->
					<view class="section">
						<view class="section-title">排序</view>
						<view class="tag-group">
							<view v-for="(item, index) in filterList" :key="index"
								:class="['tag-item', { active: activeFilter === item.value }]"
								@tap="selectFilter(item.value)">
								{{ item.label }}
							</view>
						</view>
					</view>
					<!-- 样式选择 -->
					<view class="section">
						<view class="section-title">样式</view>
						<view class="tag-group">
							<view v-for="(item, index) in styleList" :key="index"
								:class="['tag-item', { active: activeStyle === item.value }]"
								@tap="selectStyle(item.value)">
								{{ item.label }}
							</view>
						</view>
					</view>
					<!-- 类型选择 -->
					<view class="section">
						<view class="section-title">类型</view>
						<view class="tag-group">
							<view v-for="(item, index) in typeList" :key="index"
								:class="['tag-item', { 
									active: activeType === item.value,
									disabled: isTypeDisabled
								}]"
								@tap="selectType(item.value, $event)">
								{{ item.label }}
							</view>
						</view>
					</view>
				</view>

				<!-- 底部按钮 -->
				<view class="popup-footer">
					<button class="confirm-btn" @tap="confirmFilter">确定</button>
					<button class="cancel-btn" @tap="closePopup">取消</button>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import { ref, computed } from 'vue'
	
	const emit = defineEmits(['filterChange']);
	
	const showPopup = ref(false);
	const activeStyle = ref(uni.getStorageSync('activeStyle') || 'default');
	const activeType = ref(0);
	const activeFilter = ref(0);
	
	const typeList = ref([{
		label: '全部',
		value: 0
	}, {
		label: '图文',
		value: 1
	}, {
		label: '长文',
		value: 3
	}, {
		label: '投票',
		value: 4
	}]);
	
	const filterList = ref([{
		label: '最新发布',
		value: 0
	}, {
		label: '最新回复',
		value: 1
	}]);
	
	const styleList = ref([{
		label: '默认',
		value: 'default'
	}, {
		label: '暗黑',
		value: 'black'
	}, {
		label: '瀑布流',
		value: 'twice'
	}, {
		label: '经典',
		value: 'old'
	}]);
	
	const isTypeDisabled = computed(() => {
		return activeFilter.value === 1; // 当选择最新回复(value=1)时禁用类型选择
	});
	
	function showFilterPopup() {
		showPopup.value = true;
	}
	
	function closePopup() {
		showPopup.value = false;
	}
	
	function selectStyle(value) {
		activeStyle.value = value;
		uni.setStorageSync('activeStyle', value);
	}
	
	function selectType(value, event) {
		if (isTypeDisabled.value) {
			return; // 如果禁用状态，直接返回不执行选择操作
		}
		activeType.value = value;
	}
	
	function selectFilter(value) {
		activeFilter.value = value;
		if (value === 1) { // 选择最新回复时
			activeType.value = 0; // 重置类型选择为"全部"
		}
	}
	
	function confirmFilter() {
		const result = {
			style: activeStyle.value,
			type: activeType.value,
			filter: activeFilter.value
		};
		emit('filterChange', result);
		closePopup();
	}
</script>

<style lang="scss">
	.filter-page {
		.filter-trigger {
			display: inline-flex;
			align-items: center;
			padding: 0 4rpx;
			margin-left: 4rpx;
		}
	}

	.filter-popup {
		display: flex;
		flex-direction: column;
		height: 100%;
		background: #fff;
		position: relative;

		.popup-header {
			flex: none;
			padding: 30rpx;
			text-align: center;
			font-size: 32rpx;
			font-weight: 500;
			color: #333;
			border-bottom: 1rpx solid #f5f5f5;
			text{
				font-weight: 700;
				font-size: 36rpx;
			}
		}
		.popup-content {
			padding: 30rpx;
			overflow-y: auto;
			-webkit-overflow-scrolling: touch;
			padding-bottom: 84rpx;

			.section {
				margin-bottom: 50rpx;

				&:last-child {
					margin-bottom: 30rpx;
				}

				.section-title {
					font-size: 32rpx;
					font-weight: 500;
					color: #333;
					margin-bottom: 30rpx;
				}

				.tag-group {
					display: flex;
					flex-wrap: wrap;
					gap: 24rpx;
				}

				.tag-item {
					padding: 20rpx 40rpx;
					background: #f7f7f7;
					border-radius: 8rpx;
					font-size: 28rpx;
					color: #666;
					transition: all 0.3s;

					&.active {
						background: #8b95e2;
						color: #fff;
					}

					&:active {
						opacity: 0.8;
					}

					&.disabled {
						opacity: 0.5;
						cursor: not-allowed;
						pointer-events: none;
					}
				}
			}
		}

		.popup-footer {
		    bottom: 0;
		    left: 0;
		    right: 0;
		    padding: 20rpx 30rpx;
		    display: flex;
		    gap: 20rpx;
		    background: #fff;
		    box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
		    z-index: 1;
		
		    button {
		        flex: 1;
		        height: 80rpx;
		        line-height: 80rpx;
		        text-align: center;
		        border-radius: 40rpx;
		        font-size: 28rpx;
		        font-weight: 500;
		        border: none;
		
		        &.confirm-btn {
		            background: #8b95e2;
		            color: #fff;
		
		            &:active {
		                background: darken(#8b95e2, 5%);
		            }
		        }
		
		        &.cancel-btn {
		            background: #f5f5f5;
		            color: #333;
		            border: 2rpx solid #eee;
		
		            &:active {
		                background: darken(#f5f5f5, 5%);
		            }
		        }
		    }
		}
	}
</style>