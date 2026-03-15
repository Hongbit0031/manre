<template>
    <div class="mod-user">
        <el-form :inline="true" v-model="dataForm" @keyup.enter="getDataList()">
            <el-form-item>
                <el-input
                    v-model="dataForm.userName"
                    placeholder="用户名"
                    clearable
                ></el-input>
            </el-form-item>
            <el-form-item>
                <el-button @click="getDataList()">查询</el-button>
                <el-button
                    v-if="isAuth('sys:user:save')"
                    type="primary"
                    @click="addOrUpdateHandle()"
                    >新增</el-button
                >
                <el-button
                    v-if="isAuth('sys:user:delete')"
                    type="danger"
                    @click="deleteHandle()"
                    :disabled="dataListSelections.length <= 0"
                    >批量删除</el-button
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
                prop="userId"
                header-align="center"
                align="center"
                width="80"
                label="ID"
            >
            </el-table-column>
            <el-table-column
                prop="username"
                header-align="center"
                align="center"
                label="用户名"
            >
            </el-table-column>
            <el-table-column
                prop="email"
                header-align="center"
                align="center"
                label="邮箱"
            >
            </el-table-column>
            <el-table-column
                prop="mobile"
                header-align="center"
                align="center"
                label="手机号"
            >
            </el-table-column>
            <el-table-column
                prop="status"
                header-align="center"
                align="center"
                label="状态"
            >
                <template #default="{ row }">
                    <el-tag v-if="row.status === 0" size="small" type="danger"
                        >禁用</el-tag
                    >
                    <el-tag v-else size="small">正常</el-tag>
                </template>
            </el-table-column>
            <el-table-column
                prop="createTime"
                header-align="center"
                align="center"
                width="180"
                label="创建时间"
            >
            </el-table-column>
            <el-table-column
                fixed="right"
                header-align="center"
                align="center"
                width="150"
                label="操作"
            >
                <template #default="{ row }">
                    <el-button
                        v-if="isAuth('sys:user:update')"
                        type="primary"
                        size="small"
                        @click="addOrUpdateHandle(row.userId)"
                        >修改</el-button
                    >
                    <el-button
                        v-if="isAuth('sys:user:delete')"
                        type="danger"
                        size="small"
                        @click="deleteHandle(row.userId)"
                        >删除</el-button
                    >
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            v-model:current-page="pageIndex"
            :page-sizes="[10, 20, 50, 100]"
            v-model:page-size="pageSize"
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
import { ref, reactive, watch, nextTick, getCurrentInstance } from "vue";
import AddOrUpdate from "./user-add-or-update";
import { isAuth } from "@/utils/index";
import http from "@/utils/httpRequest";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = reactive({
    userName: "",
});

const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const addOrUpdateVisible = ref(false);

function getDataList() {
    dataListLoading.value = true;
    http({
        url: http.adornUrl("/sys/user/list"),
        method: "get",
        params: http.adornParams({
            page: pageIndex.value,
            limit: pageSize.value,
            username: dataForm.userName,
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
        })
        .finally(() => {
            dataListLoading.value = false;
        });
}

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

//批量删除
function deleteHandle(id) {
    var ids = id
        ? [id]
        : dataListSelections.value.map((item) => {
              return item.userId;
          });
    if (ids.length === 0) {
        return;
    }
    ElMessageBox.confirm(
        `确定对[id=${ids.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,
        "提示",
        {
            confirmButtonText: "是的",
            cancelButtonText: "取消",
            type: "warning",
            center: true,
        }
    ).then(() => {
        // 发送删除请求
        proxy
            .$http({
                url: http.adornUrl("/sys/user/delete"),
                method: "post",
                data: http.adornData(ids, false),
            })
            .then(({ data }) => {
                if (data && data.code === 0) {
                    ElMessage.success("删除成功");
                    getDataList();
                } else {
                    ElMessage.error("删除失败");
                }
            })
            .catch(() => {
                ElMessage.error("删除失败");
            });
    });
}

function selectionChangeHandle(selections) {
    dataListSelections.value = selections;
}

function sizeChangeHandle(pageSizeVal) {
    pageSize.value = pageSizeVal;
    pageIndex.value = 1;
    getDataList();
}

function currentChangeHandle(val) {
    pageIndex.value = val;
    getDataList();
}

watch([pageIndex, pageSize], () => {
    getDataList();
});

getDataList();
</script>