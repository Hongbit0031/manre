import { SET_MENU_LIST, SET_PERMISSION_LIST } from "@/store/modules/app/type";

import globalRoutes from "@/router/globalRoutes";
import mainRoutes from "@/router/mainRoutes";
import NProgress from "nprogress";
import http from '@/utils/httpRequest'
import { isURL } from '@/utils/validate'
/**
 * 判断当前路由类型, global: 全局路由, main: 主入口路由
 * @param {*} route 当前路由
 */
function fnCurrentRouteType(route, globalRoutes = []) {
	let temp = [];
	for (let i = 0; i < globalRoutes.length; i++) {
		if (route.name === globalRoutes[i].name) {
			return "global";
		} else if (
			globalRoutes[i].children &&
			globalRoutes[i].children.length >= 1
		) {
			temp = temp.concat(globalRoutes[i].children);
		}
	}
	return temp.length >= 1 ? fnCurrentRouteType(route, temp) : "main";
}

export default {
	install: (app, { router, store }) => {
		router.beforeEach((to, from, next) => {
			if (
				router.options.isAddDynamicMenuRoutes ||
				fnCurrentRouteType(to, globalRoutes) === "global"
			) {
				//* 1. 已经添加 or 全局路由, 直接访问
				if (to.meta.title) {
					document.title = to.meta.title;
				}
				NProgress.start();
				next();
			} else {
				let token = sessionStorage.getItem("token");
				if (!token || !/\S/.test(token)) {
					next({ name: "Login" });
				} else {
					http({
						url: http.adornUrl('/sys/menu/nav'),
						method: 'get',
						params: http.adornParams(),
					}).then(({ data }) => {
						if (data && data.code === 0) {
							fnAddDynamicMenuRoutes(data.menuList)
							router.options.isAddDynamicMenuRoutes = true
							sessionStorage.setItem(
								'menuList',
								JSON.stringify(data.menuList || '[]')
							)
							sessionStorage.setItem(
								'permissions',
								JSON.stringify(data.permissions || '[]')
							)
							store.dispatch(`app/${SET_MENU_LIST}`, data.menuList);
							store.dispatch(
								`app/${SET_PERMISSION_LIST}`,
								data.permissions
							);
							NProgress.start();
							next({ ...to, replace: true })
						} else {
							next({ name: "Login" });
						}
					})
						.catch((e) => {
							console.log(
								`%c${e} 请求菜单列表和权限失败,跳转至登录页`,
								'color:blue'
							)
							next({ name: "Login" });
						})
				}
			}
		});
		router.afterEach(() => {
			NProgress.done();
		});


		/**
 * 添加动态(菜单)路由
 * @param {*} menuList 菜单列表
 * @param {*} routes 递归创建的动态(菜单)路由
 */
		function fnAddDynamicMenuRoutes(menuList = [], routes = []) {
			var temp = []
			for (var i = 0; i < menuList.length; i++) {
				if (menuList[i].list && menuList[i].list.length >= 1) {
					temp = temp.concat(menuList[i].list)
				} else if (menuList[i].url && /\S/.test(menuList[i].url)) {
					menuList[i].url = menuList[i].url.replace(/^\//, '')
					var route = {
						path: menuList[i].url.replace('/', '-'),
						component: null,
						name: menuList[i].url.replace('/', '-'),
						meta: {
							menuId: menuList[i].menuId,
							title: menuList[i].name,
							isDynamic: true,
							isTab: true,
							iframeUrl: ''
						}
					}
					// url以http[s]://开头, 通过iframe展示
					if (isURL(menuList[i].url)) {
						route['path'] = `i-${menuList[i].menuId}`
						route['name'] = `i-${menuList[i].menuId}`
						route['meta']['iframeUrl'] = menuList[i].url
					} else {
						try {
							const l = "views/modules/" + menuList[i].url;
							route["component"] = () => import("@/" + l + ".vue");
						} catch (e) { }
					}
					routes.push(route)
				}
			}
			if (temp.length >= 1) {
				fnAddDynamicMenuRoutes(temp, routes)
			} else {
				mainRoutes.name = 'main-dynamic'
				mainRoutes.children = routes

				router.addRoute(mainRoutes);
				router.addRoute({
					path: "/:w+",
					redirect: { name: "404" },
				});
				sessionStorage.setItem('dynamicMenuRoutes', JSON.stringify(mainRoutes.children || '[]'))
			}
		}

	},
};
