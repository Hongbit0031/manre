<template>
    <div class="mod-log">
        <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
            <el-form-item>
                <el-input
                    v-model="dataForm.key"
                    placeholder="用户名／用户操作"
                    clearable
                ></el-input>
            </el-form-item>
            <el-form-item>
                <el-input
                    v-model="dataForm.ip"
                    placeholder="IP查询"
                    clearable
                ></el-input>
            </el-form-item>
            <el-form-item>
                <el-button @click="getDataList()" type="primary">查询</el-button>
                <el-button @click="refresh()">重置</el-button>
            </el-form-item>
        </el-form>
        <el-table
            :data="dataList"
            border
            v-loading="dataListLoading"
            style="width: 100%"
        >
            <el-table-column
                prop="id"
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
                prop="operation"
                header-align="center"
                align="center"
                label="用户操作"
            >
            </el-table-column>
            <el-table-column
                prop="method"
                header-align="center"
                align="center"
                width="150"
                :show-overflow-tooltip="true"
                label="请求方法"
            >
            </el-table-column>
            <el-table-column
                prop="params"
                header-align="center"
                align="center"
                width="150"
                :show-overflow-tooltip="true"
                label="请求参数"
            >
            </el-table-column>
            <el-table-column
                prop="time"
                header-align="center"
                align="center"
                label="执行时长(毫秒)"
            >
            </el-table-column>
            <el-table-column
                prop="ip"
                header-align="center"
                align="center"
                width="150"
                label="IP"
            >
            </el-table-column>
             <el-table-column
                prop="address"
                header-align="center"
                align="center"
                width="150"
                label="地址"
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
    </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();
const dataForm = ref({
    key: "",
    ip: "",
});
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const selectionDataList = ref([]);

//重置查询
function refresh() {
  dataForm.value.key = "";
  dataForm.value.ip = "";
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}


function getDataList() {
    let user = sessionStorage.getItem("uname")
    //如果是演示账号，拒绝数据请求
    if (user.startsWith("test")) {
        ElMessage.error("涉及敏感数据，演示账号没有数据获取权限哦");
        return;
    }
    dataListLoading.value = true;
    proxy
        .$http({
            url: proxy.$http.adornUrl("/sys/log/list"),
            method: "get",
            params: proxy.$http.adornParams({
                page: pageIndex.value,
                limit: pageSize.value,
                key: dataForm.value.key,
                ip: dataForm.value.ip,
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

onMounted(() => {
    getDataList();
});
</script>