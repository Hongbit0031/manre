<template>
	<view>
		<view class="m-field">
			<view class="label">
				<text>{{ label }}</text>
				<text class="required" v-if="required">*</text>
				<view @click="onClick" class="field-body">
					<block v-if="type == 'textarea'">
						<textarea
							style="min-height: 150rpx;"
							:auto-height="true"
							@input="onInput"
							@focus="onFocus"
							@confirm="onConfirm"
							:value="modelValue"
							:placeholder="placeholder"
							:disabled="disabled2"
						></textarea>
					</block>
					<block v-else>
						<input
							:disabled="type == 'select' || disabled"
							class="input"
							@input="onInput"
							@focus="onFocus"
							@confirm="onConfirm"
							:value="modelValue"
							:placeholder="placeholder"
						/>
						<view v-if="type == 'select'" class="right" @tap="onRightIcon"><u-icon name="grid"></u-icon></view>
						<view v-if="rightIcon" class="right" @tap="onRightIcon"><u-icon :name="rightIcon"></u-icon></view>
					</block>
				</view>
			</view>
		</view>
		<!-- Select选择弹窗 -->
		<lf-select v-model="showPopup" :mode="mode" @confirm="onSelectConfirm" :list="list"></lf-select>
	</view>
</template>

<script setup>
import { ref } from 'vue'

// Props定义
const props = defineProps({
	list: {
		type: Array,
		default: () => []
	},
	//输入类型
	type: {
		type: String,
		default: 'text'
	},
	// input的label提示语
	label: {
		type: String,
		default: ''
	},
	// 是否显示必填*号
	required: {
		type: Boolean,
		default: false
	},
	// placeholder
	placeholder: {
		type: String,
		default: '请填写内容'
	},
	modelValue: {
		type: String,
		default: ''
	},
	mode: {
		type: String,
		default: 'selector'
	},
	disabled: {
		type: Boolean,
		default: false
	},
	disabled2: {
		type: Boolean,
		default: false
	},
	isClick: {
		type: Boolean,
		default: false
	},
	rightIcon: {
		type: String,
		default: null
	}
})

// Emits定义
const emit = defineEmits(['update:modelValue', 'click', 'focus', 'blur', 'confirm', 'selectConfirm', 'onRightIcon'])

// 响应式数据
const showPopup = ref(false)

// 方法
//Select时点击事件
const onClick = () => {
	if (props.type == 'select' && props.isClick === false) {
		if (showPopup.value) {
			showPopup.value = false
		} else {
			showPopup.value = true
		}
	} else {
		emit('click', '')
	}
}

const onInput = (event) => {
	let value = event.detail.value
	emit('update:modelValue', value)
}

const onFocus = (event) => {
	emit('focus', event)
}

const onBlur = (event) => {
	emit('blur', event)
}

const onConfirm = (e) => {
	emit('confirm', e.detail.value)
}

const onSelectConfirm = (event) => {
	showPopup.value = false
	emit('selectConfirm', props.list[event])

	if (props.mode == 'date') {
		emit('update:modelValue', event)
	} else {
		emit('update:modelValue', props.list[event].value)
	}
}

const onRightIcon = () => {
	emit('onRightIcon')
}
</script>

<style lang="scss" scoped>
.m-field {
	padding: 30rpx 30rpx 0 30rpx;
	background-color: #ffffff;

	.label {
		.required {
			color: red;
			margin-left: 8rpx;
		}

		.field-body {
			display: flex;
			align-items: center;
			background-color: #f5f5f5;
			padding: 30rpx;
			border-radius: 10rpx;
			font-size: 24rpx;
			margin-top: 10rpx;
			.right {
				margin-left: auto;
			}
			.input {
				flex: 1;
				margin-right: 20rpx;
			}
		}
	}
}
</style>
