<template>
    <el-dialog
        title="日志列表"
        :close-on-click-modal="false"
        v-model="visible"
        width="75%"
    >
        <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
            <el-form-item>
                <el-input
                    v-model="dataForm.id"
                    placeholder="任务ID"
                    clearable
                ></el-input>
            </el-form-item>
            <el-form-item>
                <el-button @click="getDataList()">查询</el-button>
            </el-form-item>
        </el-form>
        <el-table
            :data="dataList"
            border
            v-loading="dataListLoading"
            height="460"
            style="width: 100%"
        >
            <el-table-column
                prop="logId"
                header-align="center"
                align="center"
                width="80"
                label="日志ID"
            >
            </el-table-column>
            <el-table-column
                prop="jobId"
                header-align="center"
                align="center"
                width="80"
                label="任务ID"
            >
            </el-table-column>
            <el-table-column
                prop="beanName"
                header-align="center"
                align="center"
                label="bean名称"
            >
            </el-table-column>
            <el-table-column
                prop="params"
                header-align="center"
                align="center"
                label="参数"
            >
            </el-table-column>
            <el-table-column
                prop="status"
                header-align="center"
                align="center"
                label="状态"
            >
                <template v-slot="scope">
                    <el-tag v-if="scope.row.status === 0" size="small"
                        >成功</el-tag
                    >
                    <el-tag
                        v-else
                        @click="showErrorInfo(scope.row.logId)"
                        size="small"
                        type="danger"
                        style="cursor: pointer"
                        >失败</el-tag
                    >
                </template>
            </el-table-column>
            <el-table-column
                prop="times"
                header-align="center"
                align="center"
                label="耗时(单位: 毫秒)"
            >
            </el-table-column>
            <el-table-column
                prop="createTime"
                header-align="center"
                align="center"
                width="180"
                label="执行时间"
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
    </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = reactive({
    id: "",
});
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const addOrUpdateVisible = ref(false);
const visible = ref(false);

// 获取数据列表
function getDataList() {
    dataListLoading.value = true;
    proxy
        .$http({
            url: proxy.$http.adornUrl("/sys/scheduleLog/list"),
            method: "get",
            params: proxy.$http.adornParams({
                page: pageIndex.value,
                limit: pageSize.value,
                jobId: dataForm.id,
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
              return item.jobId;
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
                    url: proxy.$http.adornUrl("/job/job/delete"),
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
    dataForm.id = "";
    pageIndex.value = 1;
    pageSize.value = 10;
    getDataList();
}

// 失败信息
function showErrorInfo(id) {
    proxy
        .$http({
            url: proxy.$http.adornUrl(`/sys/scheduleLog/info/${id}`),
            method: "get",
            params: proxy.$http.adornParams(),
        })
        .then(({ data }) => {
            if (data && data.code === 0) {
                ElMessage(data.log.error);
            } else {
                ElMessage.error(data.msg);
            }
        });
}

function init() {
    visible.value = true;
    getDataList();
}

defineExpose({
    init,
});
</script>
