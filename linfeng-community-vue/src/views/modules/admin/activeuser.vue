<template>
  <div>
    <div style="padding-bottom: 0">
      <el-row :gutter="20" class="baseInfo">
        <el-col v-bind="grid" class="ivu-mb">
          <el-card :bordered="false" dis-hover :padding="12">
            <div class="acea-row row-between-wrapper contrain-bottom">
              <div class="acea-row align-center">
                <span class="main_tit">访客记录总数</span>
              </div>
              <el-tag type="primary">今日</el-tag>
            </div>
            <div class="content">
              <span class="content-number my15">{{
                viewData.totalActiveToday || 0
              }}</span>
              <div class="acea-row row-between-wrapper contrain-top">
                <span class="content-time">昨日数据</span>
                <span class="content-num"
                  >{{ viewData.totalActiveYesterday || 0 }} 条</span
                >
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col v-bind="grid" class="ivu-mb">
          <el-card :bordered="false" dis-hover :padding="12">
            <div class="acea-row row-between-wrapper contrain-bottom">
              <div class="acea-row align-center">
                <span class="main_tit">访问IP总数</span>
              </div>
              <el-tag type="primary">今日</el-tag>
            </div>
            <div class="content">
              <span class="content-number my15">{{
                viewData.todayIp || 0
              }}</span>
              <div class="acea-row row-between-wrapper contrain-top">
                <span class="content-time">昨日数据</span>
                <span class="content-num"
                  >{{ viewData.yesterdayIp || 0 }} 条</span
                >
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col v-bind="grid" class="ivu-mb">
          <el-card :bordered="false" dis-hover :padding="12">
            <div class="acea-row row-between-wrapper contrain-bottom">
              <div class="acea-row align-center">
                <span class="main_tit">活跃登录用户</span>
              </div>
              <el-tag type="primary">今日</el-tag>
            </div>
            <div class="content">
              <span class="content-number my15">{{
                viewData.todayUser || 0
              }}</span>
              <div class="acea-row row-between-wrapper contrain-top">
                <span class="content-time">昨日数据</span>
                <span class="content-num"
                  >{{ viewData.yesterdayUser || 0 }} 人</span
                >
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col v-bind="grid" class="ivu-mb">
          <el-card :bordered="false" dis-hover :padding="12">
            <div class="acea-row row-between-wrapper contrain-bottom">
              <div class="acea-row align-center">
                <span class="main_tit">新增注册用户</span>
              </div>
              <el-tag type="primary">今日</el-tag>
            </div>
            <div class="content">
              <span class="content-number my15">{{
                viewData.newRegisterUserCount || 0
              }}</span>
              <div class="acea-row row-between-wrapper contrain-top">
                <span class="content-time">昨日数据</span>
                <span class="content-num"
                  >{{ viewData.yesterdayNewUserNum || 0 }} 人</span
                >
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
      <el-form-item>
        <el-input
          v-model="dataForm.key"
          placeholder="用户ID或IP查询"
          clearable
        ></el-input>
      </el-form-item>

      <el-form-item>
        <el-select
          v-model="dataForm.type"
          clearable
          placeholder="请选择访问类型"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
        <el-select
          v-model="dataForm.ter"
          clearable
          placeholder="请选择访问终端"
        >
          <el-option
            v-for="item in options2"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
        <el-select v-model="dataForm.orders" clearable placeholder="排序方式">
          <el-option
            v-for="item in options3"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
        <el-button
          @click="getDataList()"
          style="margin-left: 10px"
          type="primary"
          >查询</el-button
        >
        <el-button @click="refresh()">重置</el-button>

        <el-button
          v-if="isAuth('admin:activeuser:delete')"
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
        prop="ip"
        header-align="center"
        align="center"
        label="IP"
      >
      </el-table-column>
      <el-table-column
        prop="address"
        header-align="center"
        align="center"
        label="IP地址"
      >
        <template v-slot="scope">
          <div v-if="scope.row.address">
            <div>{{ scope.row.address }}</div>
          </div>
          <div v-if="!scope.row.address">
            <div
              style="color: #ec3131;font-weight: bold"
              @click="getAddressByIp(scope.row.ip, scope.row.id)"
            >
              查询
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="uid"
        header-align="center"
        align="center"
        width="200"
        label="用户信息"
      >
        <template v-slot="scope">
          <div v-if="scope.row.uid != 0">
            <div class="user-info">用户ID:{{ scope.row.uid }}</div>
            <div>{{ scope.row.username }}</div>
            <img style="width: 40px; height: 40px" :src="scope.row.avatar" />
          </div>
          <div v-if="scope.row.uid == 0">
            <div class="user-info-unlogin">/</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="activeCount"
        header-align="center"
        align="center"
        label="活跃频率"
      >
      </el-table-column>

      <el-table-column
        prop="terminal"
        header-align="center"
        align="center"
        label="访问终端"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.terminal == 'miniapp'" type="success"
              >小程序</el-tag
            >
            <el-tag v-else-if="scope.row.terminal == 'H5'" type="primary"
              >H5</el-tag
            >
            <el-tag v-else-if="scope.row.terminal == 'App'" type="warning"
              >App</el-tag
            >
            <el-tag v-else-if="scope.row.terminal == 'pc'" type="info"
              >PC</el-tag
            >
            <el-tag v-else type="danger">未知</el-tag>
          </div>
        </template>
      </el-table-column>

      <el-table-column
        prop="type"
        header-align="center"
        align="center"
        width="100"
        label="访问类型"
      >
        <template v-slot="scope">
          <div>
            <!-- <el-tag v-if="scope.row.type == 1" type="success">注册用户</el-tag> -->
            <!-- <el-tag v-else type="warning">游客</el-tag> -->
            <div class="user-info-login" v-if="scope.row.type == 1">
              已注册用户
            </div>
            <div class="user-info-unlogin" v-else>游客</div>
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
        prop="updateTime"
        header-align="center"
        align="center"
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
            type="danger"
            text
            size="small"
            @click="deleteHandle(scope.row.id)"
            >删除</el-button
          >
          <el-button
            type="warning"
            text
            size="small"
            @click="findUser(scope.row.uid)"
            >查用户</el-button
          >
          <el-button
            type="primary"
            text
            size="small"
            @click="findIp(scope.row.ip)"
            >查IP</el-button
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
import AddOrUpdate from "./activeuser-add-or-update";
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
const { proxy } = getCurrentInstance();
const grid = ref({ xl: 6, lg: 6, md: 12, sm: 12, xs: 24 });
const viewData = ref({});
const dataForm = reactive({
  key: "",
  type: "",
  ter: "",
  orders: "",
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
    label: "游客",
  },
  {
    value: 1,
    label: "注册用户",
  },
]);
const options2 = reactive([
  {
    value: "miniapp",
    label: "小程序",
  },
  {
    value: "App",
    label: "App",
  },
  {
    value: "H5",
    label: "H5",
  },
    {
    value: "pc",
    label: "pc",
  },
]);
const options3 = reactive([
  {
    value: 0,
    label: "访问记录ID",
  },
  {
    value: 1,
    label: "最近访问时间",
  },
]);
// 获取数据列表
function getDataList() {
  let user = sessionStorage.getItem("uname");
  // console.log(user)
  //如果是演示账号，拒绝数据请求
  if (user.startsWith("test")) {
    ElMessage.error("涉及敏感数据，演示账号没有访客数据获取权限");
    return;
  }
  dataListLoading.value = true;
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/activeuser/list"),
      method: "get",
      params: proxy.$http.adornParams({
        page: pageIndex.value,
        limit: pageSize.value,
        key: dataForm.key,
        type: dataForm.type,
        ter: dataForm.ter,
        orders: dataForm.orders,
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
          url: proxy.$http.adornUrl("/admin/activeuser/delete"),
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
function getAddressByIp(ipVal, ids) {
  const loadingInstance = ElLoading.service({
    lock: true,
    text: "查询中",
    background: "rgba(0, 0, 0, 0.7)",
  });
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/activeuser/getAddressByIp"),
      method: "get",
      params: proxy.$http.adornParams({
        ip: ipVal,
        id: ids,
      }),
    })
    .then(({ data }) => {
      loadingInstance.close();
      if (data && data.code === 0) {
        getDataList();
      } else {
        ElMessage.error("IP地址查询失败");
      }
    });
}

function findUser(uid){
  if(uid == 0){
   ElMessage.error("未登录游客只能查IP");
   return;
  }
  dataForm.key = uid;
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}

function findIp(ip){
  dataForm.key = ip;
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}
//重置查询
function refresh() {
  dataForm.key = "";
  dataForm.type = "";
  dataForm.ter = "";
  dataForm.orders = "";
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}

//统计访客
function statisticsOrder() {
  let user = sessionStorage.getItem("uname");
  console.log(user);
  //如果是演示账号，拒绝数据请求
  if (user.startsWith("test")) {
    ElMessage.error("涉及敏感数据，演示账号没有访客数据获取权限");
    return;
  }
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/statistics/visitor"),
      method: "get",
    })
    .then(({ data }) => {
      if (data && data.code === 0) {
        // console.log(data.result);
        viewData.value = data.result;
      }
    });
}
onMounted(() => {
  getDataList();
  statisticsOrder();
});
</script>
<style lang="scss" scoped>
.ivu-mb {
  margin-bottom: 20px;
}
.up,
.el-icon-caret-top {
  color: #f5222d;
  font-size: 12px;
  opacity: 1 !important;
}
.down,
.el-icon-caret-bottom {
  color: #b278dc;
  font-size: 12px;
}
.main_tit {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}
.content-time {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.content-num {
  font-size: 14px;
  color: #4d85ed;
  font-weight: 800;
}

.main_badge {
  width: 30px;
  height: 30px;
  border-radius: 5px;
  margin-right: 10px;
  background: #2c90ff;
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
}
.my15 {
  margin: 15px 0 15px;
}
.align-center {
  align-items: center;
}

.baseInfo {
  display: flex;
  .el-card__header {
    padding: 15px 20px !important;
  }
}
.content {
  &-number {
    font-size: 30px;
    font-weight: 600;
    font-family: PingFangSC-Semibold, PingFang SC;
    color: #ef4a55;
  }
  &-time {
    font-size: 14px;
    color: #333333;
    font-weight: 400;
  }
}
.acea-row {
  display: -webkit-box;
  display: -moz-box;
  display: -webkit-flex;
  display: -ms-flexbox;
  display: flex;
  -webkit-box-lines: multiple;
  -moz-box-lines: multiple;
  -o-box-lines: multiple;
  -webkit-flex-wrap: wrap;
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
}
.acea-row.row-between-wrapper {
  -webkit-box-align: center;
  -moz-box-align: center;
  -o-box-align: center;
  -ms-flex-align: center;
  -webkit-align-items: center;
  align-items: center;
  -webkit-box-pack: justify;
  -moz-box-pack: justify;
  -o-box-pack: justify;
  -ms-flex-pack: justify;
  -webkit-justify-content: space-between;
  justify-content: space-between;
}
.contrain-bottom {
  border-bottom: 1px solid #e6ebf5;
  box-sizing: border-box;
  padding-bottom: 15px;
}
.contrain-top {
  border-top: 1px solid #e6ebf5;
  box-sizing: border-box;
  padding-top: 15px;
}
</style>
