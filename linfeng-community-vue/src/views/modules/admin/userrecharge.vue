<template>
    <div class="mod-config">
        <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
            <el-form-item>
                <el-input
                    v-model="dataForm.key"
                    placeholder="用户ID或者订单号 搜索"
                    clearable
                ></el-input>
            </el-form-item>
            <el-form-item>
                <!-- 选择框1 -->
                <el-select
                    v-model="dataForm.type"
                    clearable
                    placeholder="是否充值"
                >
                    <el-option
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    >
                    </el-option>
                </el-select>
                </el-form-item>
                <!-- 选择框2 -->
                <el-form-item>
                <el-select
                    v-model="dataForm.type2"
                    clearable
                    placeholder="充值类型"
                >
                    <el-option
                        v-for="item in options2"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    >
                    </el-option>
                </el-select>
                </el-form-item>
                <el-form-item>
                <el-button type="primary" @click="getDataList()">查询</el-button>
                <el-button @click="refresh()">重置</el-button>
            </el-form-item>
        </el-form>
        <el-table
            :data="dataList"
            border
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandle"
            style="width: 100%"
        >
            <el-table-column
                type="selection"
                header-align="center"
                align="center"
                width="50"
            >
            </el-table-column>
            <el-table-column
                prop="id"
                header-align="center"
                align="center"
                label="序号"
            >
            </el-table-column>
            <el-table-column
                prop="uid"
                header-align="center"
                align="center"
                label="用户UID"
            >
            </el-table-column>
            <el-table-column
                prop="nickname"
                header-align="center"
                align="center"
                label="昵称"
            >
            </el-table-column>
            <el-table-column
                prop="orderId"
                header-align="center"
                align="center"
                label="订单号"
            >
            </el-table-column>
            <el-table-column
                prop="price"
                header-align="center"
                align="center"
                label="充值金额"
            >
            </el-table-column>
            <el-table-column
                prop="rechargeType"
                header-align="center"
                align="center"
                label="充值类型"
            >
                <template v-slot="scope">
                    <div>
                        <el-tag v-if="scope.row.rechargeType == 'wxh5'">公众号支付</el-tag>
                        <el-tag v-else-if="scope.row.rechargeType == 'weixin'">小程序支付</el-tag>
                        <el-tag v-else-if="scope.row.rechargeType == 'h5'">H5支付</el-tag>
                        <el-tag v-else>App支付</el-tag>
                    </div>
                </template>
            </el-table-column>
            <el-table-column
                prop="paid"
                header-align="center"
                align="center"
                label="是否充值"
            >
                <template v-slot="scope">
                    <div>
                        <el-tag v-if="scope.row.paid == 1" type="success"
                            >已充值</el-tag
                        >
                        <el-tag v-else type="info">未充值</el-tag>
                    </div>
                </template>
            </el-table-column>
            <el-table-column
                prop="type"
                header-align="center"
                align="center"
                label="类型"
            >
                <template v-slot="scope">
                    <div>
                        <el-tag v-if="scope.row.type == 0">钱包充值</el-tag>
                        <el-tag v-else type="warning">会员充值</el-tag>
                    </div>
                </template>
            </el-table-column>
            <el-table-column
                prop="payTime"
                header-align="center"
                align="center"
                label="充值支付时间"
            >
            </el-table-column>
            <el-table-column
                prop="addTime"
                header-align="center"
                align="center"
                label="充值时间"
            >
            </el-table-column>
            <el-table-column
                prop="transactionId"
                header-align="center"
                align="center"
                label="支付生成订单号"
            >
            </el-table-column>
            <el-table-column
                prop="outTradeNo"
                header-align="center"
                align="center"
                label="订单支付编号"
            >
            </el-table-column>
        </el-table>
        <el-pagination
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            :total="totalPage"
            layout="total, sizes, prev, pager, next, jumper"
        >
        </el-pagination>
        <!-- 弹窗, 新增 / 修改 -->
        <add-or-update
            v-if="addOrUpdateVisible"
            ref="addOrUpdate"
            @refreshDataList="getDataList"
        ></add-or-update>
    </div>
</template>

<script setup>
import AddOrUpdate from "./userrecharge-add-or-update";
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = reactive({
    key: "",
    type: "",
    type2: "",
});
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const addOrUpdateVisible = ref(false);
const options = reactive([
    {
        value: 1,
        label: "已充值",
    },
    {
        value: 0,
        label: "未充值",
    },
]);
const options2 = reactive([
    {
        value: 1,
        label: "会员充值",
    },
    {
        value: 0,
        label: "钱包充值",
    },
]);
// 获取数据列表
function getDataList() {
    dataListLoading.value = true;
    proxy
        .$http({
            url: proxy.$http.adornUrl("/admin/userrecharge/list"),
            method: "get",
            params: proxy.$http.adornParams({
                page: pageIndex.value,
                limit: pageSize.value,
                key: dataForm.key,
                type: dataForm.type,
                type2: dataForm.type2,
            }),
        })
        .then(({ data }) => {
            if (data && data.code === 0) {
                dataList.value = data.page.list;
                totalPage.value = data.page.totalCount;
            } else {
                dataList.value = [];
                totalPage.value = 0;
            }
            dataListLoading.value = false;
        });
}
// 每页数
function sizeChangeHandle(pageSizeVal) {
    pageSize.value = pageSizeVal;
    pageIndex.value = 1;
    getDataList();
}
// 当前页
function currentChangeHandle(val) {
    pageIndex.value = val;
    getDataList();
}
// 多选
function selectionChangeHandle(selections) {
    dataListSelections.value = selections;
}
// 新增 / 修改
function addOrUpdateHandle(id) {
    addOrUpdateVisible.value = true;
    nextTick(() => {
        if (id) {
            // 编辑操作
            proxy.$refs.addOrUpdate.init(id);
        } else {
            // 新增操作
            proxy.$refs.addOrUpdate.init();
        }
    });
}
//删除
function deleteHandle(id) {
    var ids = id
        ? [id]
        : dataListSelections.value.map((item) => {
              return item.id;
          });
    ElMessageBox.confirm(
        `确定对[id=${ids.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,
        "提示",
        {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
            center: true,
        }
    )
        .then(() => {
            proxy
                .$http({
                    url: proxy.$http.adornUrl("/admin/userrecharge/delete"),
                    method: "post",
                    data: proxy.$http.adornData(ids, false),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        ElMessage.success("删除成功");
                        getDataList();
                    } else {
                        ElMessage.error(data.msg);
                    }
                });
        })
        .catch(() => {});
}

//重置查询
function refresh() {
    dataForm.key = "";
    dataForm.type = "";
    dataForm.type2 = "";
    pageIndex.value = 1;
    pageSize.value = 10;
    getDataList();
}

onMounted(() => {
    getDataList();
});
</script>
