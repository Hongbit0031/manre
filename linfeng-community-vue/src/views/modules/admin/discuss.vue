<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
      <el-form-item>
        <el-input
          v-model="dataForm.key"
          placeholder="圈子ID搜索"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="dataForm.uid"
          placeholder="用户ID搜索"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()" type="primary">查询</el-button>
        <el-button @click="refresh()">重置</el-button>
        <el-button
          v-if="isAuth('admin:discuss:delete')"
          type="danger"
          @click="deleteHandle()"
          :disabled="dataListSelections.length <= 0"
          >批量删除</el-button
        >
      </el-form-item>
      <el-button
        v-if="isAuth('admin:discuss:save')"
        type="warning"
        @click="addOrUpdateHandle()"
        style="margin-bottom: 20px"
        >新建话题</el-button
      >
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
        width="120"
        label="话题ID"
      >
      </el-table-column>
      <el-table-column
        prop="uid"
        header-align="center"
        align="center"
        width="200"
        label="用户信息"
      >
        <template v-slot="scope">
          <div>
            <div class="user-info">用户ID:{{ scope.row.uid }}</div>
            <div>{{ scope.row.username }}</div>
            <img style="width: 40px; height: 40px" :src="scope.row.avatar" />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="topicId"
        header-align="center"
        align="center"
        width="160"
        label="圈子信息"
      >
        <template v-slot="scope">
          <div>
            <div class="user-info">圈子ID:{{ scope.row.topicId }}</div>
            <div>{{ scope.row.topicName }}</div>
            <img style="width: 40px; height: 40px" :src="scope.row.coverImage" />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="title"
        header-align="center"
        align="center"
        label="标题"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="introduce"
        header-align="center"
        align="center"
        label="描述"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="readCount"
        header-align="center"
        align="center"
        label="浏览量"
      >
      </el-table-column>

      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        width="130"
        label="创建时间"
      >
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="220"
        label="操作"
      >
        <template v-slot="scope">
          <el-button
            type="primary"
            size="small"
            text
            @click="addOrUpdateHandle(scope.row.id)"
            >修改</el-button
          >
          <el-button
            type="danger"
            size="small"
            text
            @click="deleteHandle(scope.row.id)"
            >删除</el-button
          >
          <el-button
            type="warning"
            size="small"
            text
            @click="deleteForce(scope.row.id)"
            >清除帖子</el-button
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
import AddOrUpdate from "./discuss-add-or-update";
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = reactive({
  key: "",
  uid: ""
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
      url: proxy.$http.adornUrl("/admin/discuss/list"),
      method: "get",
      params: proxy.$http.adornParams({
        page: pageIndex.value,
        limit: pageSize.value,
        key: dataForm.key,
        uid: dataForm.uid,
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
          url: proxy.$http.adornUrl("/admin/discuss/delete"),
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

//清理话题下的所有帖子
function deleteForce(id) {
  var ids = id
    ? [id]
    : dataListSelections.value.map((item) => {
        return item.id;
      });
  ElMessageBox.confirm(
    `确定对[id=${ids.join(",")}]下的所有帖子进行[${
      id ? "删除" : "批量删除"
    }]操作(话题不会删除)?`,
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
          url: proxy.$http.adornUrl("/admin/discuss/deletePostInDiscuss"),
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

function refresh() {
  dataForm.key = "";
  dataForm.uid = "";
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}

onMounted(() => {
  getDataList();
});
</script>
