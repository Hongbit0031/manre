<template>
    <div class="mod-oss">
        <el-form :inline="true" :model="dataForm">
            <el-form-item>
                <el-button type="warning" @click="configHandle()"
                    >文件存储配置</el-button
                >
                <el-button type="primary" @click="uploadHandle()"
                    >上传文件</el-button
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
                prop="url"
                header-align="center"
                align="center"
                label="URL地址"
            >
            </el-table-column>
            <el-table-column
                prop="createDate"
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
                        type="danger"
                        text
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
        <!-- 弹窗, 云存储配置 -->
        <config v-if="configVisible" ref="config"></config>
        <!-- 弹窗, 上传文件 -->
        <upload
            v-if="uploadVisible"
            ref="upload"
            @refreshDataList="getDataList"
        ></upload>
    </div>
</template>

<script setup>
import Config from "./oss-config";
import Upload from "./oss-upload";

import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = reactive({});
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const configVisible = ref(false);
const uploadVisible = ref(false);

// 获取数据列表
function getDataList() {
    dataListLoading.value = true;
    proxy
        .$http({
            url: proxy.$http.adornUrl("/sys/oss/list"),
            method: "get",
            params: proxy.$http.adornParams({
                page: pageIndex.value,
                limit: pageSize.value,
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

// 云存储配置
function configHandle() {
    configVisible.value = true;
    nextTick(() => {
        proxy.$refs.config.init();
    });
}
// 上传文件
function uploadHandle() {
    uploadVisible.value = true;
    nextTick(() => {
        proxy.$refs.upload.init();
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
                    url: proxy.$http.adornUrl("/sys/oss/delete"),
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

onMounted(() => {
    getDataList();
});
</script>
