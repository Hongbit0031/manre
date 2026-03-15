import { createSSRApp } from 'vue'
import App from './App'

// 引入 uView UI
import uView from './uni_modules/vk-uview-ui';

// 工具类导入
import request from './utils/request.js'
import config from './utils/config.js'
import fun from './utils/function.js'

// 挂载Vuex
import store from './store';

// 组件导入
import toast from "./components/diy-loading/loading"

import { timeFormat, numberFormat } from './utils/filters.js'

// loading函数
function loading(tf){
	if(tf){
		store.commit("switch_loading",tf)
	}else{
		store.commit("switch_loading")
	}
}

export function createApp() {
	const app = createSSRApp(App)
	
	// 使用 uView UI
	app.use(uView)
	
	// 使用 store
	app.use(store)
	
	// 注册全局组件
	app.component('toast', toast)
	
	// 配置全局属性
	app.config.globalProperties.$H = request
	app.config.globalProperties.$store = store
	app.config.globalProperties.$c = config
	app.config.globalProperties.$IMG = config.imgResource
	app.config.globalProperties.$f = fun
	app.config.globalProperties.$loading = loading
	app.config.globalProperties.$timeFormat = timeFormat
	app.config.globalProperties.$numberFormat = numberFormat
	
	return { app }
}


