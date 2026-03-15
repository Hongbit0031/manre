<template>
    <div style="padding-bottom: 0">
        <el-row :gutter="20" class="baseInfo">
            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false" dis-hover :padding="12">
                    <div class="acea-row row-between-wrapper contrain-bottom">
                        <div class="acea-row align-center">
                            <span class="main_tit">充值金额</span>
                        </div>
                        <el-tag type="success">总额</el-tag>
                    </div>
                    <div class="content" v-if="viewData">
                        <span class="content-number spBlock my15">{{
                            viewData.rechargeMoney
                        }}</span>
                        <div class="acea-row row-between-wrapper contrain-top">
                            <span class="content-time">本月</span>
                            <span class="content-time"
                                >{{ viewData.rechargeMoneyByMonth }}元</span
                            >
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false" dis-hover :padding="12">
                    <div class="acea-row row-between-wrapper contrain-bottom">
                        <div class="acea-row align-center">
                            <span class="main_tit">用户量</span>
                        </div>
                        <el-tag type="success">总量</el-tag>
                    </div>
                    <div class="content" v-if="viewData">
                        <span class="content-number spBlock my15">{{
                            viewData.totalUser
                        }}</span>
                        <div class="acea-row row-between-wrapper contrain-top">
                            <span class="content-time">今日签到用户</span>
                            <span class="content-time"
                                >{{ viewData.userSignCount }}人</span
                            >
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false" dis-hover :padding="12">
                    <div class="acea-row row-between-wrapper contrain-bottom">
                        <div class="acea-row align-center">
                            <span class="main_tit">帖子数</span>
                        </div>
                        <el-tag type="success">全部</el-tag>
                    </div>
                    <div class="content" v-if="viewData">
                        <span class="content-number spBlock my15">{{
                            viewData.totalPost
                        }}</span>
                        <div class="acea-row row-between-wrapper contrain-top">
                            <span class="content-time">待审核</span>
                            <span class="content-time"
                                >{{ viewData.totalPostOfReview }}条</span
                            >
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col v-bind="grid" class="ivu-mb">
                <el-card :bordered="false" dis-hover :padding="12">
                    <div class="acea-row row-between-wrapper contrain-bottom">
                        <div class="acea-row align-center">
                            <span class="main_tit">新增用户</span>
                        </div>
                        <el-tag type="success">今日</el-tag>
                    </div>
                    <div class="content" v-if="viewData">
                        <span class="content-number spBlock my15">{{
                            viewData.newUserNum
                        }}</span>
                        <div class="acea-row row-between-wrapper contrain-top">
                            <span class="content-time">昨日数据</span>
                            <span class="content-time"
                                >{{ viewData.yesterdayNewUserNum }} 人</span
                            >
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import http from "@/utils/httpRequest";
export default {
    data() {
        return {
            grid: { xl: 6, lg: 6, md: 12, sm: 12, xs: 24 },
            viewData: {},
        };
    },
    methods: {
        statisticsOrder() {
            this.dataListLoading = true;
            http({
                url: http.adornUrl("/admin/statistics/home"),
                method: "get",
            }).then(({ data }) => {
                if (data && data.code === 0) {
                    // console.log(data.result)
                    this.viewData = data.result;
                }
                this.dataListLoading = false;
            });
        },
    },
    mounted() {
        this.statisticsOrder();
    },
};
</script>

<style lang="scss" scoped>
.ivu-mb {
    margin-bottom: 20px;
}
.up,
.el-icon-caret-top {
    color: #f5222d;
    font-size: 12px;
    opacity: 1 !important;
}
.down,
.el-icon-caret-bottom {
    color: #39c15b;
    font-size: 12px;
}
.main_tit {
    color: #333;
    font-size: 14px;
    font-weight: 500;
}
.content-time {
    font-size: 14px;
    color: #333;
    font-weight: 500;
}
.main_badge {
    width: 30px;
    height: 30px;
    border-radius: 5px;
    margin-right: 10px;
    background: #2c90ff;
    color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
}
.my15 {
    margin: 15px 0 15px;
}
.align-center {
    align-items: center;
}
// .baseInfo {
//   /deep/ .el-card__header {
//     padding: 15px 20px !important;
//   }
// }
.baseInfo {
    .el-card__header {
        padding: 15px 20px !important;
    }
}
.content {
    &-number {
        font-size: 30px;
        font-weight: 600;
        font-family: PingFangSC-Semibold, PingFang SC;
        color: #333;
    }
    &-time {
        font-size: 14px;
        color: #333333;
        font-weight: 400;
    }
}
.acea-row {
    display: -webkit-box;
    display: -moz-box;
    display: -webkit-flex;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-lines: multiple;
    -moz-box-lines: multiple;
    -o-box-lines: multiple;
    -webkit-flex-wrap: wrap;
    -ms-flex-wrap: wrap;
    flex-wrap: wrap;
}
.acea-row.row-between-wrapper {
    -webkit-box-align: center;
    -moz-box-align: center;
    -o-box-align: center;
    -ms-flex-align: center;
    -webkit-align-items: center;
    align-items: center;
    -webkit-box-pack: justify;
    -moz-box-pack: justify;
    -o-box-pack: justify;
    -ms-flex-pack: justify;
    -webkit-justify-content: space-between;
    justify-content: space-between;
}
.contrain-bottom {
    border-bottom: 1px solid #e6ebf5;
    box-sizing: border-box;
    padding-bottom: 15px;
}
.contrain-top {
    border-top: 1px solid #e6ebf5;
    box-sizing: border-box;
    padding-top: 15px;
}
</style>
