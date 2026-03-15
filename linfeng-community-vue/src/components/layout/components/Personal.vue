<template>
    <div class="lf_contain">
        <el-button-group>
             <el-button
                title="慢热官网"
                style="border: none; font-size: 20px"
                circle
                plain
                @click="officialWebsite()"
            >
                <el-icon :size="20" style="vertical-align: middle" color="#ce6872">
                    <Platform />
                </el-icon>
            </el-button>
            <el-button
                title="刷新"
                style="border: none; font-size: 20px"
                circle
                plain
                @click="reload()"
            >
                <el-icon :size="20" style="vertical-align: middle">
                    <Refresh />
                </el-icon>
            </el-button>
            <el-button
                title="全屏"
                style="border: none; font-size: 20px"
                circle
                plain
                @click="toggle()"
            >
                <el-icon :size="14" style="vertical-align: middle">
                    <full-screen />
                </el-icon>
            </el-button>
            <el-button
                :title="dark ? '黑色侧边栏' : '白色侧边栏'"
                style="border: none; font-size: 20px"
                circle
                plain
                @click="toggleTheme()"
            >
                <el-icon :size="14" style="vertical-align: middle">
                    <component :is="dark ? 'moon' : 'sunny'" />
                </el-icon>
            </el-button>
        </el-button-group>
        <el-divider direction="vertical"></el-divider>
        <el-dropdown @command="handleCommand">
            <span class="lf_drop_nav">
                {{ uname }}
                <el-icon>
                    <arrow-down-bold />
                </el-icon>
            </span>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item command="updatePassword">
                        修改密码
                    </el-dropdown-item>
                </el-dropdown-menu>
                <el-dropdown-menu>
                    <el-dropdown-item command="outLogin">
                        退出登录
                    </el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
        <!-- 弹窗, 修改密码 -->
        <update-password
            v-if="updatePassowrdVisible"
            ref="updatePassowrd"
        ></update-password>
    </div>
</template>

<script setup>
import UpdatePassword from "../main-navbar-update-password";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import {
    inject,
    computed,
    ref,
    nextTick,
    getCurrentInstance,
    onMounted,
} from "vue";
import { useFullscreen } from "@vueuse/core";
import { sideBgColor, sideTextColor } from "@/styles/variables.scss.js";
const { proxy } = getCurrentInstance();
const { toggle } = useFullscreen();
const store = useStore();
const router = useRouter();
// const uname = computed(() => store.getters.uname);
const uname = sessionStorage.getItem("uname");

const reload = inject("reload");
const updatePassowrdVisible = ref(false);
const handleCommand = (command) => {
    if (command == "updatePassword") {
        updatePasswordHandle();
    } else if (command == "outLogin") {
        sessionStorage.removeItem("uname");
        sessionStorage.removeItem("token");
        sessionStorage.removeItem("menuList");
        sessionStorage.removeItem("permissions");
        router.push({ name: "Login" });
    }
};
// 修改密码
const updatePasswordHandle = () => {
    updatePassowrdVisible.value = true;
    nextTick(() => {
        proxy.$refs["updatePassowrd"].inits();
    });
};
const dark = ref(false);

const checkStyle = () => {
    if (sessionStorage.getItem("sideBgColor")) {
        if (sessionStorage.getItem("sideBgColor") == sideBgColor) {
            dark.value = true;
        } else {
            dark.value = false;
        }
    } else {
        dark.value = true;
    }
};
const toggleTheme = () => {
    dark.value = !dark.value;
    if (dark.value) {
        sessionStorage.setItem("sideBgColor", sideBgColor);
        sessionStorage.setItem("sideTextColor", sideTextColor);
    } else {
        sessionStorage.setItem("sideBgColor", "#fff");
        sessionStorage.setItem("sideTextColor", "#3c4147");
    }
    location.reload();
};

const officialWebsite = () => {
    window.open("https://m.scs.baby/")
};

onMounted(() => {
    checkStyle();
});
</script>

<style lang="scss" scoped>
.lf_contain {
    flex: 1;
    align-items: center;
    text-align: right;
    :deep(.el-dropdown) {
        vertical-align: baseline !important;
    }
    .lf_drop_nav {
        font-weight: bold;
    }
}
</style>
