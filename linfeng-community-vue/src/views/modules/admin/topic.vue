<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
      <el-form-item>
        <el-input
          v-model="dataForm.key"
          placeholder="用户ID/圈子名称/描述"
          clearable
        ></el-input>
      </el-form-item>
      <!-- 选择框 -->
      <el-form-item>
        <el-select
          v-model="dataForm.status"
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
        
        <el-select v-model="dataForm.private" clearable placeholder="公开类型">
          <el-option
            v-for="item in options2"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()" type="primary">查询</el-button>
        <el-button @click="refresh()">重置</el-button>
        <el-button
          v-if="isAuth('admin:topic:delete')"
          type="danger"
          @click="deleteHandle()"
          :disabled="dataListSelections.length <= 0"
          >批量删除</el-button
        >
      </el-form-item>
      <el-button
        v-if="isAuth('admin:usermenu:save')"
        type="success"
        @click="addOrUpdateHandle()"
        style="margin-bottom: 20px"
        >新增圈子</el-button
      >
    </el-form>
    <el-table
      :data="dataList"
      border
      stripe
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
        label="圈子ID"
      >
      </el-table-column>
      <el-table-column
        prop="uid"
        header-align="center"
        align="center"
        width="180"
        label="用户信息"
      >
        <template v-slot="scope">
          <div>
            <div class="user-info">用户ID:{{ scope.row.uid }}</div>
            <div>{{ scope.row.userInfo.username }}</div>
            <img style="width: 40px; height: 40px" :src="scope.row.avatar" />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="cateName"
        header-align="center"
        align="center"
        label="分类"
      >
      </el-table-column>
      <el-table-column
        prop="topicName"
        header-align="center"
        align="center"
        label="圈子名称"
        width="120"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="description"
        align="center"
        label="描述"
        width="150"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="coverImage"
        header-align="center"
        align="center"
        width="100"
        label="圈子头像"
      >
        <template #default="scope">
          <img style="width: 40px; height: 40px" :src="scope.row.coverImage" />
        </template>
      </el-table-column>
      <el-table-column
        prop="bgImage"
        header-align="center"
        align="center"
        width="100"
        label="背景图"
      >
        <template #default="scope">
          <img style="width: 80px; height: 50px" :src="scope.row.bgImage" />
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="圈子状态"
      >
        <template #default="scope">
          <div>
            <el-tag v-if="scope.row.status == 0" type="success">正常</el-tag>
            <el-tag v-else-if="scope.row.status == 1" type="danger"
              >禁用</el-tag
            >
            <el-tag v-else type="warning">审核中</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="isPrivacy"
        header-align="center"
        align="center"
        label="圈子类型"
      >
        <template #default="scope">
          <div>
            <el-tag v-if="scope.row.isPrivacy == 0" type="success"
              >公开圈子</el-tag
            >
            <el-tag type="warning" v-else>私密圈子</el-tag>
          </div>
        </template>
      </el-table-column>

      <el-table-column
        prop="userNum"
        header-align="center"
        align="center"
        label="加入人数"
      >
      </el-table-column>

      <el-table-column
        prop="rest"
        header-align="center"
        align="center"
        label="进圈条件"
      >
        <template #default="scope">
          <div>
            <el-tag v-if="scope.row.rest == 0" type="success">无限制</el-tag>
            <el-tag v-else>有限制</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="question"
        align="center"
        label="进圈问题"
        width="150"
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
            @click="addOrUpdateHandle(scope.row.id)"
            >修改</el-button
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
    <!-- 内容弹窗 -->
    <el-dialog v-model="dialogVisible3" width="30%" title="内容">
      <div>{{ postContent }}</div>
    </el-dialog>
  </div>
</template>

<script setup>
import AddOrUpdate from "./topic-add-or-update";
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = reactive({
  key: "",
  status: "",
  private: "",
});
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const addOrUpdateVisible = ref(false);

const dialogVisible3 = ref(false);
const postContent = ref("");
const options = reactive([
  {
    value: 0,
    label: "正常",
  },
  {
    value: 1,
    label: "已下架",
  },
  {
    value: 2,
    label: "待审核",
  },
]);
const options2 = reactive([
  {
    value: 0,
    label: "公开圈",
  },
  {
    value: 1,
    label: "私密圈",
  },
]);
// 获取数据列表
function getDataList() {
  dataListLoading.value = true;
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/topic/list"),
      method: "get",
      params: proxy.$http.adornParams({
        page: pageIndex.value,
        limit: pageSize.value,
        key: dataForm.key,
        status: dataForm.status,
        private: dataForm.private,
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
          url: proxy.$http.adornUrl("/admin/topic/delete"),
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
  dataForm.status = "";
  dataForm.private = "";
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}

function showDialog(row) {
  dialogVisible3.value = true;
  postContent.value = row.description;
}

onMounted(() => {
  getDataList();
});
</script>
