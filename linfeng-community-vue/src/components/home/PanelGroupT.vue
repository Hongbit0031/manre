<template>
    <div class="divBox">
        <el-row :gutter="24" class="dashboard-console-grid">
            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false">
                    <router-link :to="{ path: '/admin-activeuser' }">
                        <el-icon style="color: #b26c4c"><Printer /></el-icon>
                        <p>访客管理</p>
                    </router-link>
                </el-card>
            </el-col>
            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false">
                    <router-link :to="{ path: '/admin-post' }">
                        <el-icon style="color: #ff9c6e"><MessageBox /></el-icon>
                        <p>帖子管理</p>
                    </router-link>
                </el-card>
            </el-col>
            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false">
                    <router-link :to="{ path: '/admin-topic' }">
                        <el-icon style="color: #276deec3"
                            ><DocumentCopy
                        /></el-icon>
                        <p>圈子管理</p>
                    </router-link>
                </el-card>
            </el-col>
            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false">
                    <router-link :to="{ path: '/admin-comment' }">
                        <el-icon style="color: #f57773"
                            ><PictureFilled
                        /></el-icon>
                        <p>评论管理</p>
                    </router-link>
                </el-card>
            </el-col>

            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false">
                    <router-link :to="{ path: '/admin-user' }">
                        <el-icon style="color: #95de64"><Avatar /></el-icon>
                        <p>会员管理</p>
                    </router-link>
                </el-card>
            </el-col>

            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false">
                    <router-link :to="{ path: '/admin-message' }">
                        <el-icon style="color: #ab98e8"><Bell /></el-icon>
                        <p>消息管理</p>
                    </router-link>
                </el-card>
            </el-col>
            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false">
                    <router-link :to="{ path: '/admin-system' }">
                        <el-icon style="color: #ffd666"><Setting /></el-icon>
                        <p>配置管理</p>
                    </router-link>
                </el-card>
            </el-col>
            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false">
                    <router-link :to="{ path: '/admin-report' }">
                        <el-icon style="color: #69c0ff"><Goods /></el-icon>
                        <p>举报管理</p>
                    </router-link>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
const count = {
    todayPrice: 0,
    todayCount: 0,
    proPrice: 0,
    proCount: 0,
    monthPrice: 0,
    monthCount: 0,
    lastWeekCount: 0,
    lastWeekPrice: 0,
};

const grid = {
    xl: 3,
    lg: 3,
    md: 6,
    sm: 8,
    xs: 8,
};
</script>

<style lang="scss" rel="stylesheet/scss" scoped>
.panel-group {
    margin-top: 18px;
    .card-panel-col {
        margin-bottom: 32px;
    }
    .card-panel {
        height: 108px;
        font-size: 12px;
        position: relative;
        overflow: hidden;
        cursor: pointer;
        color: #666;
        background: #fff;
        box-shadow: 4px 4px 40px rgba(0, 0, 0, 0.05);
        border-color: rgba(0, 0, 0, 0.05);

        .icon-message {
            color: #ee4827c3;
        }

        .card-panel-icon-wrapper {
            float: left;
            margin: 14px 0 0 14px;
            padding: 16px;
            transition: all 0.38s ease-out;
            border-radius: 6px;
        }
        .card-panel-icon {
            float: left;
            font-size: 48px;
        }
        .card-panel-description {
            float: right;
            font-weight: bold;
            margin: 26px;
            margin-left: 0px;
            .card-panel-text {
                line-height: 18px;
                color: rgba(0, 0, 0, 0.45);
                font-size: 16px;
                margin-bottom: 12px;
            }
            .card-panel-num {
                font-size: 20px;
            }
        }
    }
}
.ivu-mb {
    margin-bottom: 10px;
} /*// .divBox*/

.dashboard-console-grid {
    text-align: center;
    .ivu-card-body {
        padding: 0;
    }
    i {
        font-size: 32px;
    }
    a {
        display: block;
        color: #0d0d0d;
        text-decoration: none;
    }
    p {
        margin-top: 8px;
    }
}
</style>
