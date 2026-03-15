<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
      <el-form-item>
        <el-input
          v-model="dataForm.key"
          placeholder="用户ID搜索"
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

        <el-button type="primary" @click="getDataList()" style="margin-left: 10px"
          >查询</el-button
        >
        <el-button @click="refresh()">重置</el-button>
        <el-button
          v-if="isAuth('admin:post:delete')"
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
        prop="userId"
        header-align="center"
        align="center"
        label="用户ID"
      >
      </el-table-column>
      <el-table-column
        prop="user.username"
        header-align="center"
        align="center"
        label="用户名"
      >
      </el-table-column>
      <el-table-column
        prop="avatar"
        header-align="center"
        align="center"
        label="头像"
        width="100"
      >
        <template v-slot="scope">
          <img style="width: 40px; height: 40px" :src="scope.row.avatar" />
        </template>
      </el-table-column>

      <el-table-column
        prop="prizeId"
        header-align="center"
        align="center"
        label="奖品ID"
      >
      </el-table-column>
      <el-table-column
        prop="prizeType"
        header-align="center"
        align="center"
        label="奖品类型"
      >
        <template #default="scope">
          <div>
            <el-tag v-if="scope.row.prizeType == 1" type="success">积分</el-tag>
            <el-tag v-else-if="scope.row.prizeType == 2" type="warning"
              >谢谢惠顾</el-tag
            >
            <el-tag v-else-if="scope.row.prizeType == 4" type="info"
              >自定义奖品</el-tag
            >
            <el-tag v-else-if="scope.row.prizeType == 3" type="danger"
              >红包</el-tag
            >
           
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="prizeName"
        header-align="center"
        align="center"
        label="奖品名称"
      >
      </el-table-column>

      <el-table-column
        prop="prizeImage"
        header-align="center"
        align="center"
        width="100"
        label="奖品图片"
      >
        <template #default="scope">
          <img style="width: 40px; height: 40px" :src="scope.row.prizeImage" />
        </template>
      </el-table-column>
      <el-table-column
        prop="number"
        header-align="center"
        align="center"
        label="获得数量"
      >
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        width="160px"
        label="抽奖时间"
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
import AddOrUpdate from "./luckdrawrecord-add-or-update";
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
    value: 1,
    label: "积分",
  },
  {
    value: 2,
    label: "谢谢惠顾",
  },
  {
    value: 3,
    label: "红包",
  },
  {
    value: 4,
    label: "自定义奖品",
  }
]);
// 获取数据列表
function getDataList() {
  dataListLoading.value = true;
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/luckdrawrecord/list"),
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
          url: proxy.$http.adornUrl("/admin/luckdrawrecord/delete"),
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
  dataForm.type = "";
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}
onMounted(() => {
  getDataList();
});
</script>
