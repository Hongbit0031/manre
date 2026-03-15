<template>
    <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-for="(item, index) in list" :key="index">
            {{ item }}
        </el-breadcrumb-item>
    </el-breadcrumb>
</template>

<script setup>
import { getMenuNameList } from "@/utils";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
import { computed } from "vue";
const store = useStore();
const route = useRoute();
const router = useRouter();
const menuList = sessionStorage.getItem("menuList");
const list = computed(() => {
    let name = route.name;
    name = name.replace("-", "/");
    if (!menuList) {
        location.reload();
        return;
    }
    var res = getMenuNameList(menuList, name);
    return res;
});
</script>

<style lang="scss" scoped></style>
