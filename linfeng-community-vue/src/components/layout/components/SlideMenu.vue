<template>
    <el-sub-menu
        :index="menu.menuId + ''"
        v-if="menu.type == 0 && filerMenus(menu.list)"
    >
        <template #title>
            <el-icon :size="16" style="margin-right: 6px">
                <component :is="menu.icon" />
            </el-icon>
            <span>{{ menu.name }}</span>
        </template>
        <slide-menu
            v-for="child in menu.list"
            :key="child.menuId"
            :menu="child"
        ></slide-menu>
    </el-sub-menu>
    <!--menu.type 0：目录   1：菜单   2：按钮 -->
    <el-menu-item
        v-else-if="menu.type == 1"
        :index="setIndex(menu)"
        @click="clickMenu(menu)"
    >
        <template #title>
            <el-icon :size="16" style="margin-right: 6px">
                <component :is="menu.icon" />
            </el-icon>
            <span>{{ menu.name }}</span>
        </template>
    </el-menu-item>
</template>

<script setup>
import { useRouter } from "vue-router";
import { toRefs } from "vue";
import { isURL } from "@/utils/validate";
const props = defineProps(["menu"]);
const { menu } = toRefs(props);

const router = useRouter();
const clickMenu = (menu) => {
    let name = menu.url.replace(/\//g, "-");
    if (isURL(menu.url)) {
        window.open(menu.url, "_blank");
        return;
    }
    router.push({
        name,
    });
};

const setIndex = (menu) => {
    let index = `/${menu.url.replace(/\//g, "-")}-${menu.menuId}`;
    if (menu.iframe == 1) {
        index = `/i-${menu.menuId}`;
    }
    return index;
};
/**
 * @description:过滤空目录
 * @param {*}
 * @return {*}
 */
const filerMenus = (menus) => {
    if (menus && menus.length > 0) {
        // let _menus = XE.toTreeArray(menus);
        // return _menus.some((item) => item.type == 1);
        return true;
    } else {
        return false;
    }
};
</script>

<style lang="scss">
li.el-menu-item.is-active {
    // 方案1：深紫方案 - 优雅
    // background-color: #5c6bc0 !important;
    // color: #ffffff !important;

    // 方案2：柔和渐变
    background: linear-gradient(90deg, #3f51b5, #7986cb) !important;
    color: #ffffff !important;
    border-left: 4px solid #5c6bc0 !important;
}
.el-menu-item .el-icon svg {
    vertical-align: unset;
}
.el-sub-menu__title .el-icon svg {
    vertical-align: unset;
}
// .el-sub-menu.is-active:not(.is-opened) .el-sub-menu__title i {
//     color: $base-color;
// }
</style>
