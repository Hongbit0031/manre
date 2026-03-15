<template>
    <div class="mod-role">
        <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
            <el-form-item>
                <el-input
                    v-model="dataForm.roleName"
                    placeholder="角色名称"
                    clearable
                ></el-input>
            </el-form-item>
            <el-form-item>
                <el-button @click="getDataList()">查询</el-button>
                <el-button
                    v-if="isAuth('sys:role:save')"
                    type="primary"
                    @click="addOrUpdateHandle()"
                    >新增</el-button
                >
                <el-button
                    v-if="isAuth('sys:role:delete')"
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
                prop="roleId"
                header-align="center"
                align="center"
                width="80"
                label="ID"
            >
            </el-table-column>
            <el-table-column
                prop="roleName"
                header-align="center"
                align="center"
                label="角色名称"
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
                <template v-slot="scope">
                    <el-button
                        v-if="isAuth('sys:role:update')"
                        type="primary"
                        size="small"
                        @click="addOrUpdateHandle(scope.row.roleId)"
                        >修改</el-button
                    >
                    <el-button
                        v-if="isAuth('sys:role:delete')"
                        type="danger"
                        size="small"
                        @click="deleteHandle(scope.row.roleId)"
                        >删除</el-button
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
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick, getCurrentInstance } from "vue";
import AddOrUpdate from "./role-add-or-update";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = ref({
    roleName: "",
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
    proxy
        .$http({
            url: proxy.$http.adornUrl("/sys/role/list"),
            method: "get",
            params: proxy.$http.adornParams({
                page: pageIndex.value,
                limit: pageSize.value,
                roleName: dataForm.value.roleName,
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

function sizeChangeHandle(val) {
    pageSize.value = val;
    pageIndex.value = 1;
    getDataList();
}

function currentChangeHandle(val) {
    pageIndex.value = val;
    getDataList();
}

function selectionChangeHandle(val) {
    dataListSelections.value = val;
}

const addOrUpdateHandle = (id) => {
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
};

function deleteHandle(id) {
    var ids = id
        ? [id]
        : dataListSelections.value.map((item) => {
              return item.roleId;
          });
    ElMessageBox.confirm(
        `确定对[id=${ids.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,
        "提示",
        {
            confirmButtonText: "是的",
            cancelButtonText: "取消",
            type: "warning",
            center: true,
        }
    )
        .then(() => {
            proxy
                .$http({
                    url: proxy.$http.adornUrl("/sys/role/delete"),
                    method: "post",
                    data: proxy.$http.adornData(ids, false),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        ElMessage.success("删除成功");
                        getDataList();
                    } else {
                        ElMessage.error("删除失败");
                    }
                });
        })
        .catch(() => {});
}

onMounted(getDataList);
</script>