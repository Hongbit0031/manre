import { createApp } from "vue";
import App from "@/App.vue";
import router from "@/router";
import store from "@/store";
import installElementPlus from "@/plugins/element";
import elementIcon from "@/plugins/svgicon";
import permission from "@/plugins/permission";
import http from "@/utils/httpRequest";
import "normalize.css/normalize.css";
import "nprogress/nprogress.css";
import "@/styles/common.scss";
const app = createApp(App);


// 全局方法挂载
app.config.globalProperties.$http = http

app.use(elementIcon)
    .use(store)
    .use(router)
    .use(installElementPlus)
    .use(permission, { router, store })
    .mount("#app");

const debounce = (fn, delay) => {
    let timer = null;
    return function () {
        let context = this;
        let args = arguments;
        clearTimeout(timer);
        timer = setTimeout(function () {
            fn.apply(context, args);
        }, delay);
    }
}

const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
    constructor(callback) {
        callback = debounce(callback, 16);
        super(callback);
    }
}