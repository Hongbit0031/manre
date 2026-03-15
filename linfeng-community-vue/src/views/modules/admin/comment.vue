<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
      <el-form-item>
        <el-input
          v-model="dataForm.key"
          placeholder="内容/帖子ID搜索"
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
        <el-input
          v-model="dataForm.pid"
          placeholder="父评论ID搜索"
          clearable
        ></el-input>
      </el-form-item>
      <!-- 选择框 -->
      <el-select
        v-model="dataForm.status"
        clearable
        placeholder="请选择状态"
        style="margin: 0px 10px 20px 4px"
      >
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
      <el-form-item>
        <el-button @click="getDataList()" type="primary">查询</el-button>
        <el-button @click="refresh()">重置</el-button>
        <el-button
          v-if="isAuth('admin:comment:delete')"
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
        label="id"
        fixed
      >
      </el-table-column>

      <el-table-column
        prop="uid"
        header-align="center"
        align="center"
        width="200"
        label="评论用户"
      >
        <template v-slot="scope">
          <div>
            <div style="color: #333; font-weight: 600">
              UID:{{ scope.row.uid }}
            </div>
            <div>{{ scope.row.username }}</div>
            <img style="width: 40px; height: 40px" :src="scope.row.avatar" />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="uid"
        header-align="center"
        align="center"
        width="200"
        label="被回复用户"
      >
        <template v-slot="scope">
          <div v-if="scope.row.toUid != 0">
            <div style="color: #333; font-weight: 600">
              UID:{{ scope.row.toUid }}
            </div>
            <div>{{ scope.row.toUsername }}</div>
            <img
              style="width: 40px; height: 40px"
              :src="scope.row.toUserAvatar"
            />
          </div>
          <div v-if="scope.row.toUid == 0">
            <el-tag type="info">无</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="postId"
        header-align="center"
        align="center"
        label="帖子ID"
      >
      </el-table-column>
      <el-table-column
        prop="content"
        align="center"
        label="评论内容"
        width="200"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="img"
        header-align="center"
        align="center"
        label="评论图"
      >
        <template v-slot="scope">
          <div v-if="scope.row.img">
            <img style="width: 40px; height: 40px" :src="scope.row.img" @click="goPic(scope.row.img)"/>
          </div>
          <div v-else>
            <div>/</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        width="100"
        label="评论状态"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.status == 1" type="success">上架</el-tag>
            <el-tag v-else-if="scope.row.status == 0" type="danger"
              >下架</el-tag
            >
            <el-tag v-else-if="scope.row.status == 2">待审核</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="parentComment"
        align="center"
        label="父评论"
        width="200"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="parentImg"
        header-align="center"
        align="center"
        width="120"
        label="父评论图"
      >
        <template v-slot="scope">
          <div v-if="scope.row.parentImg">
            <img style="width: 40px; height: 40px" :src="scope.row.parentImg" @click="goPic(scope.row.parentImg)"/>
          </div>
          <div v-else>
            <div>/</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="pid"
        header-align="center"
        align="center"
        label="子评论"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.pid == 0" type="success">否</el-tag>
            <el-tag v-else type="warning">是</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        width="180px"
        prop="createTime"
        header-align="center"
        align="center"
        label="评论时间"
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
            >处理</el-button
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
import AddOrUpdate from "./comment-add-or-update";
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = reactive({
  key: "",
  status: "",
  uid: "",
  pid: "",
});
const options = reactive([
  {
    value: 1,
    label: "正常",
  },
  {
    value: 0,
    label: "已下架",
  },
  {
    value: 2,
    label: "待审核",
  },
]);
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const addOrUpdateVisible = ref(false);

function goPic(url) {
    window.open(url);
}

// 获取数据列表
function getDataList() {
  dataListLoading.value = true;
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/comment/list"),
      method: "get",
      params: proxy.$http.adornParams({
        page: pageIndex.value,
        limit: pageSize.value,
        key: dataForm.key,
        status: dataForm.status,
        uid: dataForm.uid,
        pid: dataForm.pid,
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
          url: proxy.$http.adornUrl("/admin/comment/delete"),
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
  dataForm.status = "";
  dataForm.uid = "";
  dataForm.pid = "";
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}
onMounted(() => {
  getDataList();
});
</script>
