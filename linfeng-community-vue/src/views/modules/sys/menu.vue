<template>
    <div class="mod-menu">
        <el-form :inline="true" :model="dataForm">
            <el-form-item>
                <el-button
                    v-if="isAuth('sys:menu:save')"
                    type="primary"
                    @click="addOrUpdateHandle()"
                    >新增</el-button
                >
            </el-form-item>
        </el-form>

        <el-table :data="dataList" row-key="menuId" border style="width: 100%">
            <el-table-column
                prop="name"
                header-align="center"
                min-width="150"
                label="名称"
            >
            </el-table-column>
            <el-table-column
                prop="parentName"
                header-align="center"
                align="center"
                width="120"
                label="上级菜单"
            >
            </el-table-column>
            <el-table-column
                prop="icon"
                label="图标"
                header-align="center"
                align="center"
            >
                <template v-slot="{ row }">
                    <template v-if="row.icon">
                        <el-icon
                            style="margin-right: 6px; vertical-align: middle"
                        >
                            <component :is="row.icon" />
                        </el-icon>
                    </template>
                    <span v-else>/</span>
                </template>
            </el-table-column>
            <el-table-column
                prop="type"
                header-align="center"
                align="center"
                label="类型"
            >
                <template v-slot="scope">
                    <el-tag v-if="scope.row.type === 0" size="small"
                        >目录</el-tag
                    >
                    <el-tag
                        v-else-if="scope.row.type === 1"
                        size="small"
                        type="success"
                        >菜单</el-tag
                    >
                    <el-tag
                        v-else-if="scope.row.type === 2"
                        size="small"
                        type="info"
                        >按钮</el-tag
                    >
                </template>
            </el-table-column>
            <el-table-column
                prop="orderNum"
                header-align="center"
                align="center"
                label="排序号"
            >
            </el-table-column>
            <el-table-column
                prop="url"
                header-align="center"
                align="center"
                width="150"
                :show-overflow-tooltip="true"
                label="菜单URL"
            >
            </el-table-column>
            <el-table-column
                prop="perms"
                header-align="center"
                align="center"
                width="150"
                :show-overflow-tooltip="true"
                label="授权标识"
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
                        v-if="isAuth('sys:menu:update')"
                        type="primary"
                        size="small"
                        @click="addOrUpdateHandle(scope.row.menuId)"
                        >修改</el-button
                    >
                    <el-button
                        v-if="isAuth('sys:menu:delete')"
                        type="danger"
                        size="small"
                        @click="deleteHandle(scope.row.menuId)"
                        >删除</el-button
                    >
                </template>
            </el-table-column>
        </el-table>
        <!-- 弹窗, 新增 / 修改 -->
        <add-or-update
            v-if="addOrUpdateVisible"
            ref="addOrUpdate"
            @refreshDataList="getDataList"
        ></add-or-update>
    </div>
</template>

<script setup>
import AddOrUpdate from "./menu-add-or-update";
import { treeDataTranslate } from "@/utils";
import { ref, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = ref({});
const dataList = ref([]);
const dataListLoading = ref(false);
const addOrUpdateVisible = ref(false);

const init = () => {
    getDataList();
};

const getDataList = () => {
    dataListLoading.value = true;
    proxy
        .$http({
            url: proxy.$http.adornUrl("/sys/menu/list"),
            method: "get",
            params: proxy.$http.adornParams(),
        })
        .then(({ data }) => {
            dataList.value = treeDataTranslate(data, "menuId");
            dataListLoading.value = false;
        });
};

const addOrUpdateHandle = (id) => {
    addOrUpdateVisible.value = true;
    nextTick(() => {
        proxy.$refs.addOrUpdate.init(id);
    });
};

const deleteHandle = (id) => {
    ElMessageBox.confirm(`确定对[id=${id}]进行[删除]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
    })
        .then(() => {
            proxy
                .$http({
                    url: proxy.$http.adornUrl(`/sys/menu/delete/${id}`),
                    method: "post",
                    data: proxy.$http.adornData(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        ElMessage({
                            message: "操作成功",
                            type: "success",
                            duration: 1500,
                            onClose: () => {
                                addOrUpdateVisible.value = false;
                                getDataList();
                            },
                        });
                    } else {
                        ElMessage.error(data.msg);
                    }
                });
        })
        .catch(() => {});
};

// defineEmits(['refreshDataList'])

init();
</script>
