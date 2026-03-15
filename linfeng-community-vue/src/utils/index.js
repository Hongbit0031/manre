import router from '@/router'
/**
 * @description:树形结构转一维数组
 * @param {*} nodes
 * @return {*}
 */
export function jsonToArray(nodes) {
    let pid = -1;
    const toArray = (nodes) => {
        let r = [];
        if (Array.isArray(nodes)) {
            for (let i = 0, l = nodes.length; i < l; i++) {
                nodes[i].pid = pid;
                r.push(nodes[i]); // 取每项数据放入一个新数组
                if (
                    Array.isArray(nodes[i]["children"]) &&
                    nodes[i]["children"].length > 0
                ) {
                    // 若存在children则递归调用，把数据拼接到新数组中，并且删除该children
                    pid = nodes[i].id;
                    r = r.concat(toArray(nodes[i]["children"]));
                    delete nodes[i]["children"];
                }
            }
        }
        return r;
    };
    return toArray(nodes);
}

/**
 * @description:一维数组转树形结构
 * @param {*} treeArray
 * @return {*}
 */
export function arrayToJson(treeArray) {
    var r = [];
    var tmpMap = {};
    for (var i = 0, l = treeArray.length; i < l; i++) {
        //* 以每条数据的id作为obj的key值，数据作为value值存入到一个临时对象里面
        tmpMap[treeArray[i]["id"]] = treeArray[i];
    }
    for (i = 0, l = treeArray.length; i < l; i++) {
        var key = tmpMap[treeArray[i]["pid"]];
        //*循环每一条数据的pid，假如这个临时对象有这个key值，就代表这个key对应的数据有children，需要Push进去
        //*如果这一项数据属于哪个数据的子级
        if (key) {
            // *如果这个数据没有children
            if (!key["children"]) {
                key["children"] = [];
                key["children"].push(treeArray[i]);
                //* 如果这个数据有children
            } else {
                key["children"].push(treeArray[i]);
            }
        } else {
            //*如果没有这个Key值，就代表找不到属于哪个数据，那就代表没有父级,直接放在最外层
            r.push(treeArray[i]);
        }
    }
    return r;
}

/**
 * @description 获取节点的所有父节点
 * @param {*} tree
 * @param {*} func
 * @param {*} path
 * @return {*}
 */
export const treeFindPath = (tree, func, name = "url", path = []) => {
    if (!tree) return [];
    for (const data of tree) {
        //* 这里按照你的需求来存放最后返回的内容吧
        path.push(data[name]);
        if (func(data)) return path;
        if (data.list) {
            const findChildren = treeFindPath(data.list, func, name, path);
            if (findChildren.length) return findChildren;
        }
        path.pop();
    }
    return [];
};

export const getMenuNameList = (menuList, url, path = []) => {
    var tree = JSON.parse(menuList)
    for (const data of tree) {
        if (data['url'] && data['url'] == url) {
            path.push(data['name']);
        }
        if (data.list) {
            var t = data.list
            for (const item of t) {
                if (item['url'] && item['url'] == url) {
                    path.push(data['name']);
                    path.push(item['name']);
                }
            }
        }
    }
    return path
}


/**
 * @description: 拆箱函数,解决tooltip显示问题
 * @param {*} obj
 * @return {*}
 */
export const unwarp = (obj) => obj && (obj.__v_raw || obj.valueOf() || obj);

/**
 * @description:获取所有的el-svg-icon组件名
 * @param {*}
 * @return {*}
 */
export const icons = () => {
    const components = require("@element-plus/icons-vue");
    const names = [];
    for (const key in components) {
        names.push(components[key].name);
    }
    return names;
};

export function getUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
        return (c === 'x' ? (Math.random() * 16) | 0 : 'r&0x3' | '0x8').toString(16)
    })
}


export function clearLoginInfo() {
    // Cookies.remove('token')
    sessionStorage.removeItem("token")
    // setting.resetStore()
    router.options.isAddDynamicMenuRoutes = false
}


/**
 * 是否有权限
 * @param {*} key
 */
export function isAuth(key) {
    return JSON.parse(sessionStorage.getItem('permissions') || '[]').indexOf(key) !== -1 || false
}


/**
 * @param {Function} func
 * @param {number} wait
 * @param {boolean} immediate
 * @return {*}
 */
export function debounce(func, wait, immediate) {
    let timeout, args, context, timestamp, result

    const later = function () {
        // 据上一次触发时间间隔
        const last = +new Date() - timestamp

        // 上次被包装函数被调用时间间隔 last 小于设定时间间隔 wait
        if (last < wait && last > 0) {
            timeout = setTimeout(later, wait - last)
        } else {
            timeout = null
            // 如果设定为immediate===true，因为开始边界已经调用过了此处无需调用
            if (!immediate) {
                result = func.apply(context, args)
                if (!timeout) context = args = null
            }
        }
    }

    return function (...args) {
        context = this
        timestamp = +new Date()
        const callNow = immediate && !timeout
        // 如果延时不存在，重新设定延时
        if (!timeout) timeout = setTimeout(later, wait)
        if (callNow) {
            result = func.apply(context, args)
            context = args = null
        }

        return result
    }
}



/**
 * 树形数据转换
 * @param {*} data
 * @param {*} id
 * @param {*} pid
 */
export function treeDataTranslate(data, id = 'id', pid = 'parentId') {
    var res = []
    var temp = {}
    for (var i = 0; i < data.length; i++) {
        temp[data[i][id]] = data[i]
    }
    for (var k = 0; k < data.length; k++) {
        if (temp[data[k][pid]] && data[k][id] !== data[k][pid]) {
            if (!temp[data[k][pid]]['children']) {
                temp[data[k][pid]]['children'] = []
            }
            if (!temp[data[k][pid]]['_level']) {
                temp[data[k][pid]]['_level'] = 1
            }
            data[k]['_level'] = temp[data[k][pid]]._level + 1
            temp[data[k][pid]]['children'].push(data[k])
        } else {
            res.push(data[k])
        }
    }
    return res
}