<template>
    <div class="mod-config">
        <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
            <!-- <el-form-item>
                <el-input
                    v-model="dataForm.paramKey"
                    placeholder="参数名"
                    clearable
                ></el-input>
            </el-form-item> -->
            <el-form-item>
                <!-- <el-button @click="getDataList()">查询</el-button> -->
                <el-button type="primary" @click="addOrUpdateHandle()"
                    >新增</el-button
                >
                <el-button
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
                prop="id"
                header-align="center"
                align="center"
                width="80"
                label="ID"
            >
            </el-table-column>
            <el-table-column
                prop="paramKey"
                header-align="center"
                align="center"
                label="参数名"
            >
            </el-table-column>
            <el-table-column
                prop="paramValue"
                header-align="center"
                align="center"
                :show-overflow-tooltip="true"
                label="参数值"
            >
            </el-table-column>
            <el-table-column
                prop="remark"
                header-align="center"
                align="center"
                :show-overflow-tooltip="true"
                label="备注"
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
                        type="primary"
                        size="small"
                        @click="addOrUpdateHandle(scope.row.id)"
                        >修改</el-button
                    >
                    <el-button
                        type="danger"
                        size="small"
                        @click="deleteHandle(scope.row.id)"
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
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import AddOrUpdate from "./config-add-or-update";
import { isAuth } from "@/utils/index";
import http from "@/utils/httpRequest";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = reactive({
    key: "",
});
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const addOrUpdateVisible = ref(false);

const getDataList = () => {
    dataListLoading.value = true;
    http({
        url: http.adornUrl("/sys/config/list"),
        method: "get",
        params: http.adornParams({
            page: pageIndex.value,
            limit: pageSize.value,
            key: dataForm.key,
        }),
    }).then(({ data }) => {
        if (data && data.code === 0) {
            dataList.value = data.page.list;
            totalPage.value = data.page.totalCount;
        } else {
            dataList.value = [];
            totalPage.value = 0;
        }
        dataListLoading.value = false;
    });
};
const sizeChangeHandle = (val) => {
    pageSize.value = val;
    pageIndex.value = 1;
    getDataList();
};

const currentChangeHandle = (val) => {
    pageIndex.value = val;
    getDataList();
};

const selectionChangeHandle = (val) => {
    dataListSelections.value = val;
};

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

const deleteHandle = (id) => {
    var ids = id
        ? [id]
        : dataListSelections.value.map((item) => {
              return item.id;
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
        http({
                url: http.adornUrl("/sys/config/delete"),
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
};

onMounted(getDataList);
</script>
