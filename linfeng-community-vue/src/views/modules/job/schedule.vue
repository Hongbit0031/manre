<template>
    <div class="mod-schedule">
        <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
            <el-form-item>
                <el-input
                    v-model="dataForm.beanName"
                    placeholder="bean名称"
                    clearable
                ></el-input>
            </el-form-item>
            <el-form-item>
                <el-button @click="getDataList()">查询</el-button>
                <el-button
                    v-if="isAuth('sys:schedule:save')"
                    type="primary"
                    @click="addOrUpdateHandle()"
                    >新增</el-button
                >
                <el-button
                    v-if="isAuth('sys:schedule:delete')"
                    type="danger"
                    @click="deleteHandle()"
                    :disabled="dataListSelections.length <= 0"
                    >批量删除</el-button
                >
                <el-button
                    v-if="isAuth('sys:schedule:pause')"
                    type="danger"
                    @click="pauseHandle()"
                    :disabled="dataListSelections.length <= 0"
                    >批量暂停</el-button
                >
                <el-button
                    v-if="isAuth('sys:schedule:resume')"
                    type="danger"
                    @click="resumeHandle()"
                    :disabled="dataListSelections.length <= 0"
                    >批量恢复</el-button
                >
                <el-button
                    v-if="isAuth('sys:schedule:run')"
                    type="danger"
                    @click="runHandle()"
                    :disabled="dataListSelections.length <= 0"
                    >批量立即执行</el-button
                >
                <el-button
                    v-if="isAuth('sys:schedule:log')"
                    type="success"
                    @click="logHandle()"
                    >日志列表</el-button
                >
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
                prop="jobId"
                header-align="center"
                align="center"
                width="80"
                label="ID"
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
                prop="cronExpression"
                header-align="center"
                align="center"
                label="cron表达式"
            >
            </el-table-column>
            <el-table-column
                prop="remark"
                header-align="center"
                align="center"
                label="备注"
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
                        >正常</el-tag
                    >
                    <el-tag v-else size="small" type="danger">暂停</el-tag>
                </template>
            </el-table-column>
            <el-table-column
                fixed="right"
                header-align="center"
                align="center"
                width="150"
                label="操作"
            >
                <template v-slot="scope">
                    <el-button
                        v-if="isAuth('sys:schedule:update')"
                        type="primary"
                        text
                        size="small"
                        @click="addOrUpdateHandle(scope.row.jobId)"
                        >修改</el-button
                    >
                    <el-button
                        v-if="isAuth('sys:schedule:delete')"
                        type="danger"
                        text
                        size="small"
                        @click="deleteHandle(scope.row.jobId)"
                        >删除</el-button
                    >
                    <el-button
                        v-if="isAuth('sys:schedule:pause')"
                        type="primary"
                        text
                        size="small"
                        @click="pauseHandle(scope.row.jobId)"
                        >暂停</el-button
                    >
                    <el-button
                        v-if="isAuth('sys:schedule:resume')"
                        type="primary"
                        text
                        size="small"
                        @click="resumeHandle(scope.row.jobId)"
                        >恢复</el-button
                    >
                    <el-button
                        v-if="isAuth('sys:schedule:run')"
                        type="primary"
                        text
                        size="small"
                        @click="runHandle(scope.row.jobId)"
                        >立即执行</el-button
                    >
                </template>
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
        <!-- 弹窗, 日志列表 -->
        <log v-if="logVisible" ref="logs"></log>
    </div>
</template>

<script setup>
import AddOrUpdate from "./schedule-add-or-update";
import Log from "./schedule-log";
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = reactive({
    beanName: "",
});
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const addOrUpdateVisible = ref(false);
const logVisible = ref(false);
// 获取数据列表
function getDataList() {
    dataListLoading.value = true;
    proxy
        .$http({
            url: proxy.$http.adornUrl("/sys/schedule/list"),
            method: "get",
            params: proxy.$http.adornParams({
                page: pageIndex.value,
                limit: pageSize.value,
                beanName: dataForm.beanName,
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
              return item.logId;
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
                    url: proxy.$http.adornUrl("/sys/schedule/delete"),
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
    dataForm.beanName = "";
    pageIndex.value = 1;
    pageSize.value = 10;
    getDataList();
}

// 暂停
function pauseHandle(id) {
    var ids = id
        ? [id]
        : dataListSelections.value.map((item) => {
              return item.jobId;
          });
    ElMessageBox.confirm(
        `确定对[id=${ids.join(",")}]进行[${id ? "暂停" : "批量暂停"}]操作?`,
        "提示",
        {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
        }
    )
        .then(() => {
            proxy
                .$http({
                    url: proxy.$http.adornUrl("/sys/schedule/pause"),
                    method: "post",
                    data: proxy.$http.adornData(ids, false),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        ElMessage({
                            message: "操作成功",
                            type: "success",
                            duration: 1500,
                            onClose: () => {
                                getDataList();
                            },
                        });
                    } else {
                        ElMessage.error(data.msg);
                    }
                });
        })
        .catch(() => {});
}

// 恢复
function resumeHandle(id) {
    var ids = id
        ? [id]
        : dataListSelections.value.map((item) => {
              return item.jobId;
          });
    ElMessageBox.confirm(
        `确定对[id=${ids.join(",")}]进行[${id ? "恢复" : "批量恢复"}]操作?`,
        "提示",
        {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
        }
    )
        .then(() => {
            proxy
                .$http({
                    url: proxy.$http.adornUrl("/sys/schedule/resume"),
                    method: "post",
                    data: proxy.$http.adornData(ids, false),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        ElMessage({
                            message: "操作成功",
                            type: "success",
                            duration: 1500,
                            onClose: () => {
                                getDataList();
                            },
                        });
                    } else {
                        ElMessage.error(data.msg);
                    }
                });
        })
        .catch(() => {});
}

// 立即执行
function runHandle(id) {
    var ids = id
        ? [id]
        : dataListSelections.value.map((item) => {
              return item.jobId;
          });
    ElMessageBox.confirm(
        `确定对[id=${ids.join(",")}]进行[${
            id ? "立即执行" : "批量立即执行"
        }]操作?`,
        "提示",
        {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
        }
    )
        .then(() => {
            proxy
                .$http({
                    url: proxy.$http.adornUrl("/sys/schedule/run"),
                    method: "post",
                    data: proxy.$http.adornData(ids, false),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        ElMessage({
                            message: "操作成功",
                            type: "success",
                            duration: 1500,
                            onClose: () => {
                                getDataList();
                            },
                        });
                    } else {
                        ElMessage.error(data.msg);
                    }
                });
        })
        .catch(() => {});
}

// 日志列表
function logHandle() {
    logVisible.value = true;
    nextTick(() => {
        proxy.$refs.logs.init();
    });
}

onMounted(() => {
    getDataList();
});
</script>
