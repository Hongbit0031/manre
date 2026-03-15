<template>
    <router-link to="/">
        <logo />
    </router-link>
    <div class="lf_el_menu">
        <el-scrollbar>
            <el-menu
                :default-active="defaultActive"
                :collapse="opened"
                :collapseTransition="false"
                unique-opened
                :background-color="sideBgColorValue"
                :text-color="sideTextColorValue"
                :active-text-color="sideActiveTextColor"
            >
                <el-menu-item index="home" @click="goHome">
                    <template #title>
                        <el-icon><HomeFilled /></el-icon>
                        <span>首页</span>
                    </template>
                </el-menu-item>
                <slide-menu
                    v-for="item in menus"
                    :key="item.id"
                    :menu="item"
                ></slide-menu>
            </el-menu>
        </el-scrollbar>
    </div>
</template>

<script setup>
import {
    nav_height,
    sideBgColor,
    sideTextColor,
    sideActiveTextColor,
} from "@/styles/variables.scss.js";
import { computed } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
import Logo from "./components/Logo";
import SlideMenu from "./components/SlideMenu";

const store = useStore();
const route = useRoute();
const router = useRouter();
const opened = computed(() => store.getters.opened);
const menus = computed(() => store.getters.menuList);
const sideBgColorValue = computed(
    () => sessionStorage.getItem("sideBgColor") || sideBgColor
);

const sideTextColorValue = computed(
    () => sessionStorage.getItem("sideTextColor") || sideTextColor
);

// console.log("lf", menus.value);
const defaultActive = computed(() => {
    const name = route.name;
    // 找到匹配的菜单项
    const findMatchingMenu = (menuList) => {
        for (const menu of menuList) {
            if (menu.type === 1) {
                const menuIndex = setIndex(menu);
                if (menu.url.replace(/\//g, "-") === name) {
                    return menuIndex;
                }
            }
            if (menu.list && menu.list.length > 0) {
                const found = findMatchingMenu(menu.list);
                if (found) return found;
            }
        }
        return null;
    };
    
    return findMatchingMenu(menus.value) || "home";
});

// 添加 setIndex 函数（与 SlideMenu.vue 中的相同）
const setIndex = (menu) => {
    let index = `/${menu.url.replace(/\//g, "-")}-${menu.menuId}`;
    if (menu.iframe == 1) {
        index = `/i-${menu.menuId}`;
    }
    return index;
};

function goHome() {
    router.push({ name: "AppMain" });
}
</script>

<style lang="scss" scoped>
a:active {
    color: $base-color;
}
.lf_el_menu {
    background: $sideBgColor;
    height: calc(100vh - v-bind(nav_height));
}
</style>
