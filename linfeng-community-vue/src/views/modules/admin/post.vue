<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
      <el-form-item>
        <el-input
          v-model="dataForm.key"
          placeholder="帖子内容搜索"
          :prefix-icon="Search"
          clearable
        />
      </el-form-item>

      <el-form-item>
        <el-input
          v-model="dataForm.title"
          placeholder="帖子标题搜索"
          :prefix-icon="Search"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="dataForm.pid"
          placeholder="帖子ID搜索"
          :prefix-icon="Search"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="dataForm.tid"
          placeholder="圈子ID搜索"
          :prefix-icon="Search"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="dataForm.uid"
          placeholder="用户ID搜索"
          :prefix-icon="Search"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-select v-model="dataForm.status" clearable placeholder="请选择状态">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      
        <!-- 选择框 -->
        <el-form-item>
          <el-select v-model="dataForm.type" clearable placeholder="请选择类型">
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
          <el-select
            v-model="dataForm.private"
            clearable
            placeholder="公开类型"
          >
            <el-option
              v-for="item in options3"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
        <el-button
          @click="getDataList()"
          type="primary"
          style="margin-left: 10px"
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
        label="ID"
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
            <div>{{ scope.row.userInfo.username }}</div>
            <img style="width: 40px; height: 40px" :src="scope.row.avatar" />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="topicId"
        header-align="center"
        align="center"
        width="160"
        label="归属圈子"
      >
        <template v-slot="scope">
          <div>
            <div class="user-info">圈子ID:{{ scope.row.topicId }}</div>
            <div>{{ scope.row.topicName }}</div>
            <img
              style="width: 40px; height: 40px"
              :src="scope.row.coverImage"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="title"
        header-align="center"
        align="center"
        label="标题"
        width="150"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="content"
        align="center"
        label="内容"
        width="200"
        :show-overflow-tooltip="true"
      >
      </el-table-column>

      <el-table-column
        prop="type"
        header-align="center"
        align="center"
        label="帖子类型"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.type == 1" type="success">图文</el-tag>
            <el-tag v-else-if="scope.row.type == 2" type="warning">视频</el-tag>
            <el-tag v-else-if="scope.row.type == 3" type="info">文章</el-tag>
            <el-tag v-else type="danger">投票</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="media"
        header-align="center"
        align="center"
        width="150"
        label="文件"
      >
        <template v-slot="scope">
          <el-button
            v-if="scope.row.type == 2"
            type="primary"
            text
            @click="openVideo(scope.row.media[0])"
            >点击预览</el-button
          >
          <el-button
            v-if="scope.row.type == 1 && scope.row.media[0]"
            type="primary"
            text
            @click="openPic(scope.row.media)"
            >点击查看</el-button
          >
          <el-button
            v-if="scope.row.type == 3"
            type="primary"
            text
            @click="openArticle(scope.row.content)"
            >查看文章</el-button
          >
          <el-button
            v-if="scope.row.type == 4"
            type="primary"
            text
            @click="openVote(scope.row.voteInfo)"
            >查看投票</el-button
          >
        </template>
      </el-table-column>
      <el-table-column
        prop="readCount"
        header-align="center"
        align="center"
        label="浏览量"
      >
      </el-table-column>
      <el-table-column
        prop="commentCount"
        header-align="center"
        align="center"
        label="评论数"
      >
      </el-table-column>
      <el-table-column
        prop="collectionCount"
        header-align="center"
        align="center"
        label="点赞数"
      >
      </el-table-column>
      <el-table-column
        prop="cut"
        header-align="center"
        align="center"
        width="120"
        label="是否付费"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.cut == 0" type="info">普通贴</el-tag>
            <el-tag v-else type="warning">付费贴</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="isPrivate"
        header-align="center"
        align="center"
        width="120"
        label="隶属"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.isPrivate == 0" type="success"
              >公开圈帖子</el-tag
            >
            <el-tag v-else type="warning">私密圈帖子</el-tag>
          </div>
        </template>
      </el-table-column>

      <el-table-column
        prop="postTop"
        header-align="center"
        align="center"
        label="置顶"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.postTop == 1" type="success">是</el-tag>
            <el-tag v-else type="danger">否</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="状态"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.status == 0" type="success">上架</el-tag>
            <el-tag v-else-if="scope.row.status == 1" type="warning"
              >待审核</el-tag
            >
            <el-tag v-else type="danger">下架</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        width="170"
        label="发帖时间"
      >
      </el-table-column>
      <el-table-column
        prop="discussTitle"
        header-align="center"
        align="center"
        label="话题"
        width="120"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="address"
        header-align="center"
        align="center"
        label="发帖地址"
        width="140"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="200"
        label="操作"
      >
        <template v-slot="scope">
          <el-button
            v-if="scope.row.status == 1 || scope.row.status == 2"
            type="primary"
            text
            size="small"
            @click="statusHandle(scope.row.id)"
            >上架</el-button
          >
          <el-button
            v-if="scope.row.status == 0"
            type="primary"
            text
            size="small"
            @click="statusDownHandle(scope.row.id)"
            >下架</el-button
          >

          <el-button
            type="warning"
            text
            size="small"
            @click="addOrUpdateHandle(scope.row.id)"
            >修改</el-button
          >
          <el-button
            type="danger"
            text
            size="small"
            @click="deleteHandleById(scope.row.id)"
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

    <el-dialog
      title="视频预览"
      v-model="dialogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <video
        class="video"
        :src="videoUrl"
        enable-danmu
        danmu-btn
        controls
      ></video>

      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="dialogVisible = false"
            >确定</el-button
          >
        </span>
      </template>
    </el-dialog>

    <el-dialog
      title="图片预览"
      v-model="dialogVisible2"
      width="60%"
      :before-close="handleClose"
    >
      <div class="images">
        <div v-for="(item, index) in media" :key="index" class="image-middle">
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

    <el-dialog v-model="dialogVisible3" width="30%" title="内容">
      <div v-html="formatRichText(postContent)" class="contents"></div>
    </el-dialog>
    <el-dialog v-model="dialogVisible6" width="30%" title="内容">
      <div v-html="formatRichText(article)" class="contents"></div>
    </el-dialog>
    <!-- 投票结果弹窗 -->
    <el-dialog v-model="dialogVisible7" width="30%" title="投票详情">
      <div class="vote-info">
        <h3>{{ voteInfo.title }}</h3>
        <div v-for="(option, index) in voteInfo.options" :key="index" class="vote-option">
          <div class="option-header">
            <span>{{ option.content }}</span>
            <span class="vote-count">{{ option.ticketNum }}票</span>
          </div>
          <el-progress 
            :percentage="calculatePercentage(option.ticketNum, getTotalVotes())" 
            :format="format"
            :stroke-width="18"
          />
        </div>
        <div class="vote-time">
          <p>投票截止时间：{{ formatTime(voteInfo.expireTime) }}</p>
        </div>
      </div>
    </el-dialog>
    <!-- 下架 -->
    <el-dialog v-model="dialogVisible4" width="35%">
      <el-form label-width="80px">
        <el-form-item label="告知用户">
          <el-radio-group v-model="isSend">
            <el-radio :label="0">否</el-radio>
            <el-radio :label="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <p class="formInfo" v-if="isSend == 0">下架后不会告知用户具体原因</p>
        <el-form-item label="下架原因" v-if="isSend == 1">
          <el-input
            v-model="downReason"
            placeholder="下架原因将以系统消息形式告知用户"
            type="textarea"
            maxlength="200"
          ></el-input>
        </el-form-item>
      </el-form>

      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible4 = false">取 消</el-button>
          <el-button type="primary" @click="downClick">确定</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 删除 -->
    <el-dialog v-model="dialogVisible5" width="35%">
      <el-form label-width="80px">
        <el-form-item label="告知用户">
          <el-radio-group v-model="isSendDeleteInfo">
            <el-radio :label="0">否</el-radio>
            <el-radio :label="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <p class="formInfo" v-if="isSendDeleteInfo == 0">
          删除后不会向用户发送通知信息
        </p>
        <el-form-item label="删除原因" v-if="isSendDeleteInfo == 1">
          <el-input
            v-model="deleteReason"
            placeholder="删除原因将以系统消息形式告知用户"
            type="textarea"
            maxlength="200"
          ></el-input>
        </el-form-item>
      </el-form>

      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible5 = false">取 消</el-button>
          <el-button type="primary" @click="deleteClick">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import AddOrUpdate from "./post-add-or-update";
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search } from "@element-plus/icons-vue";
const { proxy } = getCurrentInstance();

const dataForm = reactive({
  key: "",
  pid: "",
  title: "",
  status: "",
  type: "",
  tid: "",
  uid: "",
  private: "",
});
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const addOrUpdateVisible = ref(false);

const dialogVisible = ref(false);
const dialogVisible2 = ref(false);
const dialogVisible3 = ref(false);
const dialogVisible4 = ref(false);
const dialogVisible5 = ref(false);
const dialogVisible6 = ref(false);
const dialogVisible7 = ref(false);
const postContent = ref("");
const videoUrl = ref("");
const media = ref([]);
const downReason = ref("");
const deleteReason = ref("");
const isSend = ref(1);
const isSendDeleteInfo = ref(1);
const objectId = ref("");
const deleteId = ref("");
const article = ref({});
const voteInfo = ref({});
const options = reactive([
  {
    value: 0,
    label: "正常",
  },
  {
    value: 1,
    label: "待审核",
  },
  {
    value: 2,
    label: "已下架",
  },
]);
const options2 = reactive([
  {
    value: 1,
    label: "图文贴",
  },
  {
    value: 2,
    label: "视频贴",
  },
  {
    value: 3,
    label: "长文贴",
  },
  {
    value: 4,
    label: "投票贴",
  },
]);
const options3 = reactive([
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
      url: proxy.$http.adornUrl("/admin/post/list"),
      method: "get",
      params: proxy.$http.adornParams({
        page: pageIndex.value,
        limit: pageSize.value,
        key: dataForm.key,
        pid: dataForm.pid,
        title: dataForm.title,
        status: dataForm.status,
        type: dataForm.type,
        tid: dataForm.tid,
        uid: dataForm.uid,
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
          url: proxy.$http.adornUrl("/admin/post/delete"),
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
  dataForm.pid = "";
  dataForm.title = "";
  dataForm.type = "";
  dataForm.status = "";
  dataForm.tid = "";
  dataForm.uid = "";
  dataForm.private = "";
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}

function downClick() {
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/post/down"),
      method: "post",
      data: proxy.$http.adornData({
        id: objectId.value,
        isSend: isSend.value,
        downReason: downReason.value,
      }),
    })
    .then(({ data }) => {
      if (data && data.code === 0) {
        dialogVisible4.value = false;
        ElMessage.success("下架成功");
        getDataList();
      } else {
        ElMessage.error(data.msg);
      }
    });
}

function deleteClick() {
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/post/deleteById"),
      method: "post",
      data: proxy.$http.adornData({
        id: deleteId.value,
        isSendDeleteInfo: isSendDeleteInfo.value,
        deleteReason: deleteReason.value,
      }),
    })
    .then(({ data }) => {
      if (data && data.code === 0) {
        dialogVisible5.value = false;
        ElMessage.success("删除成功");
        getDataList();
      } else {
        ElMessage.error(data.msg);
      }
    });
}

function statusDownHandle(id) {
  dialogVisible4.value = true;
  objectId.value = id;
}
function openVote(val) {
  dialogVisible7.value = true;
  voteInfo.value = val;
}
function calculatePercentage(votes, total) {
  if (total === 0) return 0;
  return Math.round((votes / total) * 100);
}

function getTotalVotes() {
  if (!voteInfo.value.options) return 0;
  return voteInfo.value.options.reduce((sum, option) => sum + option.ticketNum, 0);
}

function format(percentage) {
  return percentage + '%';
}

function formatTime(timestamp) {
  if (!timestamp) return '';
  // 时间戳是毫秒数，需要乘以1000转换
  const date = new Date(timestamp * 1000);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
function deleteHandleById(id) {
  dialogVisible5.value = true;
  deleteId.value = id;
}
// 审核
function statusHandle(id) {
  var ids = id
    ? [id]
    : dataListSelections.value.map((item) => {
        return item.id;
      });
  ElMessageBox.confirm(
    `确定对[id=${ids.join(",")}]进行[${id ? "上架" : "批量上架"}]操作?`,
    "提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }
  ).then(() => {
    proxy
      .$http({
        url: proxy.$http.adornUrl("/admin/post/up"),
        method: "post",
        data: proxy.$http.adornData(ids, false),
      })
      .then(({ data }) => {
        if (data && data.code === 0) {
          ElMessage({
            message: "操作成功",
            type: "success",
            duration: 1500,
            onClose: () => {
              getDataList();
            },
          });
        } else {
          ElMessage.error(data.msg);
        }
      });
  });
}

function handleClose(done) {
  ElMessageBox.confirm("确认关闭？")
    .then((_) => {
      done();
    })
    .catch((_) => {});
}
function openVideo(urls) {
  dialogVisible.value = true;
  videoUrl.value = urls;
}
function openPic(mediaValue) {
  dialogVisible2.value = true;

  media.value = mediaValue;
}
function openArticle(val) {
  dialogVisible6.value = true;
  article.value = val;
}
function goPic(url) {
  window.open(url);
}

function formatRichText(html) {
  let newContent = html.replace(/<img[^>]*>/gi, function (match, capture) {
    match = match.replace(/style="[^"]+"/gi, "").replace(/style='[^']+'/gi, "");
    match = match.replace(/width="[^"]+"/gi, "").replace(/width='[^']+'/gi, "");
    match = match
      .replace(/height="[^"]+"/gi, "")
      .replace(/height='[^']+'/gi, "");
    return match;
  });
  newContent = newContent.replace(/style="[^"]+"/gi, function (match, capture) {
    match = match
      .replace(/width:[^;]+;/gi, "max-width:100%;")
      .replace(/width:[^;]+;/gi, "max-width:100%;");
    return match;
  });
  newContent = newContent.replace(/<br[^>]*\/>/gi, "");
  newContent = newContent.replace(
    /\<img/gi,
    '<img style="max-width:100%;height:auto;display:block;margin-top:0;margin-bottom:0;"'
  );
  return newContent;
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
.post-content {
  font-size: 10px;
  color: rgb(58, 91, 222);
}
.contents {
  p {
    font-size: 14px;
    color: #333333;
    line-height: 24px;
    text-indent: 1em;
    margin-bottom: 25px;
    img {
      width: 10px;
    }
  }

  img {
    width: 100%;
    height: 100px;
  }
}
.formInfo {
  margin-left: 20px;
  line-height: 0px;
  color: #999999;
  font-size: 12px;
}
.vote-info {
  padding: 10px;
  
  h3 {
    margin-bottom: 20px;
    text-align: center;
    color: #303133;
  }

  .vote-option {
    margin-bottom: 20px;

    .option-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;
      
      span {
        color: #606266;
        font-size: 14px;
      }

      .vote-count {
        color: #409EFF;
        font-weight: bold;
      }
    }
  }

  .vote-time {
    margin-top: 20px;
    text-align: right;
    color: #909399;
    font-size: 13px;
  }
}
</style>
