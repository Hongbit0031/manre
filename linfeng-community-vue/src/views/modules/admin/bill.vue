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
        <el-select v-model="dataForm.type2" clearable placeholder="请选择分类">
          <el-option
            v-for="item in options2"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
        <el-button @click="getDataList()" type="primary">查询</el-button>
        <el-button @click="refresh()">重置</el-button>
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
        label="用户账单id"
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
            <div>{{ scope.row.user.username }}</div>
            <img style="width: 40px; height: 40px" :src="scope.row.avatar" />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="pm"
        header-align="center"
        align="center"
        label="类型"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.pm == 0" type="danger">支出</el-tag>
            <el-tag v-else-if="scope.row.pm == 1" type="success">收入</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="title"
        header-align="center"
        align="center"
        label="账单标题"
      >
      </el-table-column>
      <el-table-column
        prop="category"
        header-align="center"
        align="center"
        label="明细种类"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.category == 'integral'" type="primary"
              >积分</el-tag
            >
            <el-tag v-else-if="scope.row.category == 'now_money'" type="warning"
              >金额</el-tag
            >
            <el-tag v-else>未知</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="number"
        header-align="center"
        align="center"
        label="明细数字"
      >
      </el-table-column>
      <el-table-column
        prop="mark"
        header-align="center"
        align="center"
        label="备注"
      >
      </el-table-column>
      <el-table-column
        width="160px"
        prop="addTime"
        header-align="center"
        align="center"
        label="添加时间"
      >
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="状态"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.status == 1" type="success">有效</el-tag>
            <el-tag v-else-if="scope.row.status == 0" type="warning"
              >待确定</el-tag
            >
            <el-tag v-else type="danger">无效</el-tag>
          </div>
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
import AddOrUpdate from "./bill-add-or-update";
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const dataForm = reactive({
  key: "",
  type: "",
  type2: "",
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
    value: 0,
    label: "支出",
  },
  {
    value: 1,
    label: "获得",
  },
]);
const options2 = reactive([
  {
    value: "now_money",
    label: "金额",
  },
  {
    value: "integral",
    label: "积分",
  },
]);

function refresh() {
  dataForm.key = "";
  dataForm.type = "";
  dataForm.type2 = "";
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}
// 获取数据列表
function getDataList() {
  dataListLoading.value = true;
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/bill/list"),
      method: "get",
      params: proxy.$http.adornParams({
        page: pageIndex.value,
        limit: pageSize.value,
        key: dataForm.key,
        type: dataForm.type,
        type2: dataForm.type2,
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
          url: proxy.$http.adornUrl("/admin/bill/delete"),
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
