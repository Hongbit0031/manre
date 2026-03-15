<template>
    <el-tooltip :content="title" placement="right-end" effect="customized">
        <div class="lf_menu_logo">
            <div class="lf_logo_img">
                <el-image
                    style="height: 100%"
                    :src="require('../../../assets/logo.png')"
                    fit="contain"
                ></el-image>
            </div>
            <h3 v-show="!opened" class="lf_logo_title">
                {{ title2 }}
            </h3>
        </div>
    </el-tooltip>
</template>

<script setup>
import { computed } from "vue";
import { useStore } from "vuex";
const store = useStore();
const opened = computed(() => store.getters.opened);
const title =
    "慢热后台管理系统, 基于 Vue3 + Element Plus 开发";
const title2 = "慢热";
</script>

<style lang="scss" scoped>
.lf_menu_logo {
    width: 100%;
    height: $nav-height;
    white-space: nowrap;
    overflow: hidden;
    .lf_logo_img {
        height: 100%;
        text-align: center;
        width: $side-close-width;
        display: inline-block;
        box-sizing: border-box;
        vertical-align: middle;
    }
    .lf_logo_title {
        width: $side-open-width;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        display: inline-block;
        margin: 0;
        vertical-align: middle;
        color: #000;
        &:hover {
            color: $base-color;
        }
    }
}
</style>
<style>
.el-popper.is-customized {
    /* Set padding to ensure the height is 32px */
    padding: 6px 12px;
    background: linear-gradient(90deg, rgb(178, 184, 218), rgb(109, 96, 209));
    color: #fff;
}

.el-popper.is-customized .el-popper__arrow::before {
    background: linear-gradient(45deg, #c5d3e7, #837ae3);
    right: 0;
    color: #fff;
}
</style>
