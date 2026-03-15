<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
      <el-form-item>
        <el-input
          v-model="dataForm.key"
          placeholder="推送标题 搜索"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <!-- 选择框 -->
        <el-select v-model="dataForm.type" clearable placeholder="请选择类型">
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
          v-if="isAuth('admin:message:save')"
          type="primary"
          @click="addOrUpdateHandle()"
          >发送消息</el-button
        >
        <el-button
          v-if="isAuth('admin:message:delete')"
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
        prop="mid"
        header-align="center"
        align="center"
        label="消息id"
      >
      </el-table-column>
      <el-table-column
        prop="fromUid"
        header-align="center"
        align="center"
        width="200"
        label="发送者"
      >
        <template v-slot="scope">
          <div v-if="scope.row.fromUid!=0">
            <div style="color: #333; font-weight: 600">
              UID:{{ scope.row.fromUid }}
            </div>
            <div>{{ scope.row.fromUsername }}</div>
            <img style="width: 40px; height: 40px" :src="scope.row.fromAvatar" />
          </div>
          <div v-if="scope.row.fromUid==0">
            <el-tag type="success">系统</el-tag>
          </div>
        </template>
      </el-table-column>
    <el-table-column
        prop="toUid"
        header-align="center"
        align="center"
        width="200"
        label="接收者"
      >
        <template v-slot="scope">
          <div>
            <div style="color: #333; font-weight: 600">
              UID:{{ scope.row.toUid }}
            </div>
            <div>{{ scope.row.username }}</div>
            <img style="width: 40px; height: 40px" :src="scope.row.avatar" />
          </div>
        </template>
      </el-table-column>
      <!-- <el-table-column
        prop="toUid"
        header-align="center"
        align="center"
        label="接收者uid"
      >
      </el-table-column> -->
      <el-table-column
        prop="postId"
        header-align="center"
        align="center"
        label="帖子id"
      >
      </el-table-column>
      <el-table-column
        prop="title"
        header-align="center"
        align="center"
        label="推送标题"
      >
      </el-table-column>

      <el-table-column
        prop="content"
        align="center"
        label="消息内容"
        width="200"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="是否已读"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.status == 0" type="success">未读</el-tag>
            <el-tag v-else-if="scope.row.status == 1" type="danger"
              >已读</el-tag
            >
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="type"
        header-align="center"
        align="center"
        label="消息类型"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.type == 1" type="success">点赞</el-tag>
            <el-tag v-else-if="scope.row.type == 2" type="warning">评论</el-tag>
            <el-tag v-else-if="scope.row.type == 3" type="info">收藏</el-tag>
            <el-tag v-else-if="scope.row.type == 4">关注</el-tag>
            <el-tag v-else-if="scope.row.type == 5" type="danger"
              >系统通知</el-tag
            >
            <el-tag v-else type="danger">私聊</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
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
            size="small"
            @click="deleteHandle(scope.row.mid)"
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
    <!-- 评论弹窗 -->
    <el-dialog v-model="dialogVisible3" width="30%" title="内容">
      <div>{{ postContent }}</div>
    </el-dialog>
  </div>
</template>

<script setup>
import AddOrUpdate from "./message-add-or-update";
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
const options = reactive([
  {
    value: 3,
    label: "点赞收藏",
  },
  {
    value: 2,
    label: "评论",
  },
  {
    value: 4,
    label: "关注",
  },
  {
    value: 5,
    label: "系统通知",
  },
]);
const dialogVisible3 = ref(false);
const postContent = ref("");
// 获取数据列表
function getDataList() {
  dataListLoading.value = true;
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/message/list"),
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
        return item.mid;
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
          url: proxy.$http.adornUrl("/admin/message/delete"),
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
function showDialog(row) {
  dialogVisible3.value = true;
  postContent.value = row.content;
}

function refresh() {
  dataForm.key = "";
  dataForm.type = "";
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}
onMounted(() => {
  getDataList();
});
</script>
