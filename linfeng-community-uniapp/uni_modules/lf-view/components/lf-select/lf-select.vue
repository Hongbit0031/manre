<template>
	<view>
		<lf-popup v-model="show">
			<view class="popup-wrap">
				<!-- 普通选择器 -->
				<block v-if="mode == 'selector'">
					<picker-view class="picker-view" :value="selectValue" @change="onChange">
						<picker-view-column>
							<view class="item" v-for="(item, index) in list" :key="index">{{ item.lable }}</view>
						</picker-view-column>
					</picker-view>
				</block>
				<!-- 日期选择器 -->
				<block v-if="mode == 'date'">
					<picker-view v-if="visible" :indicator-style="indicatorStyle" :value="dateValue" @change="dateChange" class="picker-view">
						<picker-view-column>
							<view class="item" v-for="(item, index) in years" :key="index">{{ item }}年</view>
						</picker-view-column>
						<picker-view-column>
							<view class="item" v-for="(item, index) in months" :key="index">{{ item }}月</view>
						</picker-view-column>
						<picker-view-column>
							<view class="item" v-for="(item, index) in days" :key="index">{{ item }}日</view>
						</picker-view-column>
					</picker-view>
				</block>
				<view class="btn-wrap">
					<view @click="onCancel" class="btn">取消</view>
					<view @click="onConfirm" class="btn">确认</view>
				</view>
			</view>
		</lf-popup>
	</view>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

// Props定义
const props = defineProps({
	mode: {
		type: String,
		default: 'selector'
	},
	list: {
		type: Array,
		default: () => []
	},
	modelValue: {
		type: Boolean,
		default: false
	}
})

// Emits定义
const emit = defineEmits(['update:modelValue', 'confirm', 'cancel', 'change'])

// 响应式数据
const show = ref(false)
const selectValue = ref([0])
const title = ref('picker-view')
const visible = ref(true)
const indicatorStyle = ref(`height: 50px;`)
const selectDate = ref('')

// 日期相关数据
const date = new Date()
const years = ref([])
const year = ref(date.getFullYear())
const months = ref([])
const month = ref(date.getMonth() + 1)
const days = ref([])
const day = ref(date.getDate())
const dateValue = ref([0, 0, 0])

// 初始化日期数据
const initDateData = () => {
	for (let i = 1990; i <= date.getFullYear(); i++) {
		years.value.push(i)
	}
	for (let i = 1; i <= 12; i++) {
		months.value.push(i)
	}
	for (let i = 1; i <= 31; i++) {
		days.value.push(i)
	}
	
	selectDate.value = years.value[0] + '-' + months.value[0] + '-' + days.value[0]
}

// 监听器
watch(() => props.modelValue, (n) => {
	show.value = n
})

watch(show, (n) => {
	emit('update:modelValue', n)
})

// 生命周期
onMounted(() => {
	initDateData()
})

// 方法
const onCancel = () => {
	show.value = false
	emit('cancel', '')
}

const onConfirm = () => {
	show.value = false
	if (props.mode == 'selector') {
		emit('confirm', selectValue.value)
		emit('update:modelValue', selectValue.value)
	}

	if (props.mode == 'date') {
		emit('confirm', selectDate.value)
		emit('update:modelValue', selectDate.value)
	}
}

const onChange = (e) => {
	selectValue.value = e.detail.value
	emit('change', e.detail.value)
}

const dateChange = (e) => {
	const val = e.detail.value
	year.value = years.value[val[0]]
	month.value = months.value[val[1]]
	day.value = days.value[val[2]]

	selectDate.value = year.value + '-' + month.value + '-' + day.value
}
</script>

<style lang="scss" scoped>
// picker-view
.picker-view {
	width: 750rpx;
	height: 300rpx;
	margin-top: 20rpx;
}
.item {
	height: 50px;
	align-items: center;
	justify-content: center;
	text-align: center;
}
.popup-wrap {
	position: relative;
	height: 50vh;
	.btn-wrap {
		position: absolute;
		bottom: 30rpx;
		width: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
		.btn {
			padding: 20rpx 100rpx;
			border-radius: 10rpx;
			&:nth-child(1) {
				margin-right: 30rpx;
				background-color: #f5f5f5;
				color: #19be6b;
			}

			&:nth-child(2) {
				background-color: #19be6b;
				margin-right: 30rpx;
				color: #fff;
			}
		}
	}
}
</style>
