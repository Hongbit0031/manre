<template>
    <div class="mod-config">
        <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
            <el-form-item>
                <el-button
                    v-if="isAuth('admin:luckdraw:save')"
                    type="primary"
                    @click="addOrUpdateHandle()"
                    >新增</el-button
                >
                <el-button
                    v-if="isAuth('admin:luckdraw:delete')"
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
                label="ID"
            >
            </el-table-column>
            <el-table-column
                prop="name"
                header-align="center"
                align="center"
                label="名称"
            >
            </el-table-column>
            <el-table-column
                prop="prizeType"
                header-align="center"
                align="center"
                label="奖品类型"
            >
                <template v-slot="scope">
                    <div>
                        <el-tag v-if="scope.row.prizeType == 1" type="success"
                            >积分</el-tag
                        >
                        <el-tag
                            v-else-if="scope.row.prizeType == 2"
                            type="warning"
                            >谢谢惠顾</el-tag
                        >
                        <el-tag
                            v-else-if="scope.row.prizeType == 4"
                            type="info"
                            >自定义奖品</el-tag
                        >
                        <el-tag v-else type="danger">红包</el-tag>
                    </div>
                </template>
            </el-table-column>

            <el-table-column
                prop="image"
                header-align="center"
                align="center"
                width="100"
                label="图片"
            >
                <template v-slot="scope">
                    <img
                        style="width: 40px; height: 40px"
                        :src="scope.row.image"
                    />
                </template>
            </el-table-column>
            <el-table-column
                prop="number"
                header-align="center"
                align="center"
                label="奖品数量"
            >
            </el-table-column>
            <el-table-column
                prop="probability"
                header-align="center"
                align="center"
                label="抽奖几率"
            >
            </el-table-column>
            <el-table-column
                prop="sort"
                header-align="center"
                align="center"
                label="排序"
            >
            </el-table-column>
            <el-table-column
                prop="status"
                header-align="center"
                align="center"
                label="抽奖状态"
            >
                <template v-slot="scope">
                    <div>
                        <el-tag v-if="scope.row.status == 1" type="success"
                            >正常</el-tag
                        >
                        <el-tag v-else type="danger">禁用</el-tag>
                    </div>
                </template>
            </el-table-column>
            <el-table-column
                prop="createTime"
                header-align="center"
                align="center"
                width="170px"
                label="创建时间"
            >
            </el-table-column>
            <el-table-column
                prop="updateTime"
                header-align="center"
                align="center"
                width="170px"
                label="更新时间"
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
import AddOrUpdate from "./luckdraw-add-or-update";
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
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

// 获取数据列表
function getDataList() {
    dataListLoading.value = true;
    proxy
        .$http({
            url: proxy.$http.adornUrl("/admin/luckdraw/list"),
            method: "get",
            params: proxy.$http.adornParams({
                page: pageIndex.value,
                limit: pageSize.value,
                key: dataForm.key,
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
                    url: proxy.$http.adornUrl("/admin/luckdraw/delete"),
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
