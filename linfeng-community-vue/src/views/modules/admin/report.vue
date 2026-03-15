<template>
    <div class="mod-config">
        <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
            <el-form-item>
                <el-input
                    v-model="dataForm.key"
                    placeholder="用户ID或描述 搜索"
                    clearable
                ></el-input>
            </el-form-item>

            <el-form-item>
                <!-- 选择框 -->
                <el-select
                    v-model="dataForm.type"
                    clearable
                    placeholder="请选择状态"
                >
                    <el-option
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    >
                    </el-option>
                </el-select>

                <el-button @click="getDataList()">查询</el-button>
                <el-button @click="refresh()">重置</el-button>
                <el-button
                    v-if="isAuth('admin:report:delete')"
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
                prop="uid"
                header-align="center"
                align="center"
                label="用户id"
            >
            </el-table-column>
            <el-table-column
                prop="userInfo.username"
                header-align="center"
                align="center"
                label="用户名"
                width="150"
                :show-overflow-tooltip="true"
            >
            </el-table-column>

            <el-table-column
                prop="type"
                header-align="center"
                align="center"
                label="类型"
            >
                <template #default="scope">
                    <div>
                        <el-tag v-if="scope.row.type == 1" type="success"
                            >帖子</el-tag
                        >
                        <el-tag v-else-if="scope.row.type == 2">评论</el-tag>
                        <el-tag v-else-if="scope.row.type == 3" type="warning"
                            >用户</el-tag
                        >
                        <el-tag v-else type="danger">圈子</el-tag>
                    </div>
                </template>
            </el-table-column>
            <el-table-column
                prop="media"
                header-align="center"
                align="center"
                width="130"
                label="文件"
            >
                <template #default="scope">
                    <el-button
                        type="primary"
                        text
                        @click="openPic(scope.row.media)"
                        >点击查看</el-button
                    >
                </template>
            </el-table-column>
            <el-table-column
                prop="content"
                header-align="center"
                align="center"
                label="描述"
                width="150"
                :show-overflow-tooltip="true"
            >
            </el-table-column>
            <el-table-column
                prop="status"
                header-align="center"
                align="center"
                label="状态"
            >
                <template #default="scope">
                    <div>
                        <el-tag v-if="scope.row.status == 0" type="warning"
                            >待审核</el-tag
                        >
                        <el-tag v-else-if="scope.row.status == 1" type="success"
                            >已处理</el-tag
                        >
                        <el-tag v-else-if="scope.row.status == 2" type="danger"
                            >已驳回</el-tag
                        >
                    </div>
                </template>
            </el-table-column>
            <el-table-column
                prop="feedback"
                header-align="center"
                align="center"
                width="180"
                label="平台反馈"
                :show-overflow-tooltip="true"
            >
            </el-table-column>

            <el-table-column
                prop="createTime"
                header-align="center"
                align="center"
                width="170"
                label="创建时间"
            >
            </el-table-column>
            <el-table-column
                prop="updateTime"
                header-align="center"
                align="center"
                width="170"
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
                <template #default="scope">
                    <el-button
                        type="primary"
                        text
                        size="small"
                        v-if="scope.row.status == 0"
                        @click="addOrUpdateHandle(scope.row.id)"
                        >处理</el-button
                    >
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
        <!-- 弹窗, 新增 / 修改 -->
        <add-or-update
            v-if="addOrUpdateVisible"
            ref="addOrUpdate"
            @refreshDataList="getDataList"
        ></add-or-update>
        <!-- 预览弹窗 -->
        <el-dialog
            title="图片预览"
            v-model="dialogVisible2"
            width="60%"
            :before-close="handleClose"
        >
            <div class="images">
                <div
                    v-for="(item, index) in media"
                    :key="index"
                    class="image-middle"
                >
                    <el-card shadow="hover" :body-style="{ padding: '10px' }">
                        <img
                            :src="media[index]"
                            class="image"
                            @click="goPic(media[index])"
                        />
                        <div style="text-align: center; padding-top: 12px">
                            <span>图{{ index + 1 }}</span>
                        </div>
                    </el-card>
                </div>
            </div>

            <template v-slot:footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible2 = false">取 消</el-button>
                    <el-button type="primary" @click="dialogVisible2 = false"
                        >确定</el-button
                    >
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import AddOrUpdate from "./report-add-or-update";
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = reactive({
    key: "",
    type: "",
});
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const addOrUpdateVisible = ref(false);
const dialogVisible2 = ref(false);
const media = ref([]);
const options = reactive([
    {
        value: 0,
        label: "待审核",
    },
    {
        value: 1,
        label: "已处理",
    },
    {
        value: 2,
        label: "已驳回",
    },
]);
// 获取数据列表
function getDataList() {
    dataListLoading.value = true;
    proxy
        .$http({
            url: proxy.$http.adornUrl("/admin/report/list"),
            method: "get",
            params: proxy.$http.adornParams({
                page: pageIndex.value,
                limit: pageSize.value,
                key: dataForm.key,
                type: dataForm.type,
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
                    url: proxy.$http.adornUrl("/admin/report/delete"),
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
    pageIndex.value = 1;
    pageSize.value = 10;
    getDataList();
}

function openPic(mediaValue) {
    dialogVisible2.value = true;

    media.value = mediaValue;
}

function goPic(url) {
    window.open(url);
}
function handleClose(done) {
    ElMessageBox.confirm("确认关闭？")
        .then((_) => {
            done();
        })
        .catch((_) => {});
}
onMounted(() => {
    getDataList();
});
</script>

<style lang="scss" scoped>
.video {
    width: 100%;
    margin-bottom: 10px;
}
.position {
    margin-left: 15px;
    font-size: 30px;
    font-weight: 600;
}
/* 图片总布局，样式 */
.images {
    display: flex;
    margin-top: 20px;
    margin-left: 21px;
    margin-right: 20px;
    flex-wrap: wrap;
}
/* 图片之间 */
.image-middle {
    margin-right: 15px;
    margin-bottom: 15px;
}
/* 单张图片样式 */
.image {
    width: 110px;
    height: 110px;
}
</style>