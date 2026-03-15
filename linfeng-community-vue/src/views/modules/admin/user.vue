<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
      <el-form-item class="input-search">
        <el-input
          v-model="dataForm.key"
          placeholder="用户ID/用户名/手机号/IP"
          clearable
        ></el-input>
      </el-form-item>
      <!-- 选择框 -->
      <el-select v-model="dataForm.type" clearable placeholder="请选择用户类型">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
      <el-select
        v-model="dataForm.status"
        clearable
        placeholder="请选择用户状态"
      >
        <el-option
          v-for="item in options2"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
      <el-select
        v-model="dataForm.vipStatus"
        clearable
        placeholder="请选择会员状态"
      >
        <el-option
          v-for="item in options3"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
      <el-form-item class="button-group">
        <el-button @click="getDataList()" type="primary">查询</el-button>
        <el-button @click="refresh()">重置</el-button>
        <el-button @click="createVirtualUser()" type="warning">创建人机账号</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      stripe
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%"
      scrollbar-always-on
    >
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50"
      >
      </el-table-column>
      <el-table-column
        prop="uid"
        header-align="center"
        align="center"
        label="用户ID"
        fixed
      >
      </el-table-column>
      <el-table-column
        prop="username"
        header-align="center"
        align="center"
        label="用户名"
        width="150"
        :show-overflow-tooltip="true"
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
        prop="gender"
        header-align="center"
        align="center"
        label="性别"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.gender == 1" type="success">男</el-tag>
            <el-tag v-else-if="scope.row.gender == 0">保密</el-tag>
            <el-tag v-else type="danger">女</el-tag>
          </div>
        </template>
      </el-table-column>

      <el-table-column
        prop="mobile"
        header-align="center"
        align="center"
        width="110"
        label="手机号"
      >
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        width="70"
        label="状态"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.status == 0" type="success">正常</el-tag>
            <el-tag v-else-if="scope.row.status == 1" type="danger"
              >已封号</el-tag
            >
            <el-tag v-else-if="scope.row.status == 2" type="warning"
              >禁言</el-tag
            >
            <el-tag v-else-if="scope.row.status == 3" type="danger"
              >已注销</el-tag
            >
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="intro"
        header-align="center"
        align="center"
        width="150"
        label="个性签名"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="integral"
        header-align="center"
        align="center"
        width="60"
        label="积分"
      >
      </el-table-column>
      <el-table-column
        prop="money"
        header-align="center"
        align="center"
        width="60"
        label="余额"
      >
      </el-table-column>

      <el-table-column
        prop="tagStr"
        header-align="center"
        align="center"
        width="150"
        label="标签"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="vip"
        header-align="center"
        align="center"
        width="110"
        label="用户类型"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.vip == 0" type="success">普通用户</el-tag>
            <el-tag v-else-if="scope.row.vip == 1">会员用户</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="type"
        header-align="center"
        align="center"
        width="110"
        label="账号类型"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.type == 1" type="success">官方账号</el-tag>
            <el-tag v-else-if="scope.row.type == 0">普通账号</el-tag>
            <el-tag v-else type="info">虚拟账号</el-tag>
          </div>
        </template>
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
        prop="city"
        header-align="center"
        align="center"
        label="IP城市"
        width="180"
        :show-overflow-tooltip="true"
      >
      </el-table-column>
      <el-table-column
        prop="lastLoginIp"
        header-align="center"
        align="center"
        label="登录ip"
      >
      </el-table-column>
      <el-table-column
        prop="openid"
        header-align="center"
        align="center"
        width="150"
        label="小程序openid"
      >
      </el-table-column>
      <el-table-column
        prop="email"
        header-align="center"
        align="center"
        label="邮箱"
        width="120"
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
            type="primary"
            text
            size="small"
            @click="addOrUpdateHandle(scope.row.uid)"
            >更改</el-button
          >
          <el-button
            type="primary"
            text
            size="small"
            @click="punishOpen(scope.row.uid)"
            >处理</el-button
          >
          <el-button
            type="primary"
            text
            size="small"
            @click="addPostByUid(scope.row.uid)"
            >发帖</el-button
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
    <!-- 处理框 -->
    <el-dialog title="处罚管理" v-model="dialogVisible" width="35%" label-width="80px">
      <el-form>
        <el-form-item label="帖子" prop="resetPost">
          <el-radio-group v-model="punish.resetPost">
            <el-radio :label="2">全部删除</el-radio>
            <el-radio :label="1">全部下架</el-radio>
            <el-radio :label="0">不处理</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="头像" prop="resetAvatar">
          <el-radio-group v-model="punish.resetAvatar">
            <el-radio :label="1">重置</el-radio>
            <el-radio :label="0">不重置</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="昵称" prop="resetUsername">
          <el-radio-group v-model="punish.resetUsername">
            <el-radio :label="1">重置</el-radio>
            <el-radio :label="0">不重置</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="签名" prop="resetIntro">
          <el-radio-group v-model="punish.resetIntro">
            <el-radio :label="1">重置</el-radio>
            <el-radio :label="0">不重置</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="背景图" prop="resetBg">
          <el-radio-group v-model="punish.resetBg">
            <el-radio :label="1">重置</el-radio>
            <el-radio :label="0">不重置</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="doPunish">确 定</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 发帖 -->
    <el-dialog
      title="发帖"
      :close-on-click-modal="false"
      v-model="addPostHandle"
      @close="handleClose"
    >
      <el-form :model="postForm" label-width="180px">
        <el-form-item label="发帖类型" prop="postTypeValue">
          <el-radio-group v-model="postTypeValue" @change="changePostType">
            <el-radio :label="1">图文贴</el-radio>
            <el-radio :label="2">视频贴</el-radio>
            <el-radio :label="3">长文贴</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" prop="money">
          <el-input
            v-model="postForm.title"
            placeholder="请填写标题"
            clearable
            maxlength="20"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item
          label="内容"
          prop="money"
          v-if="postForm.type == 1 || postForm.type == 2"
        >
          <el-input
            v-model="postForm.content"
            placeholder="请填写内容"
            type="textarea"
            clearable
            maxlength="400"
            show-word-limit
          ></el-input>
        </el-form-item>
        <!-- 富文本编辑器 -->
        <el-form-item label="内容" prop="content" v-if="postForm.type == 3">
          <TextEditor ref="childRef" />
        </el-form-item>

        <el-form-item label="图片" prop="img" v-if="postForm.type == 1">
          <el-upload
            :action="url"
            list-type="picture-card"
            :auto-upload="true"
            :on-success="handleIconSuccess"
            :on-remove="removeFile"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="视频上传方式" prop="videoUploadType" v-if="postForm.type == 2">
          <el-radio-group
            v-model="videoUploadType"
            @change="changeVideoUploadType"
          >
            <el-radio :label="0">本地上传</el-radio>
            <el-radio :label="1">链接提交</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="视频" prop="img" v-if="postForm.type == 2">
          <el-upload
            v-if="videoUploadType == 0"
            drag
            :action="url"
            multiple
            :auto-upload="true"
            :on-success="handleIconSuccess"
            :on-remove="removeFile"
            limit="1"
            style="text-align: center"
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <div class="el-upload__tip">支持mp4视频</div>
          </el-upload>
          <el-input
            v-if="videoUploadType == 1"
            v-model="videoPostUrl"
            placeholder="请填写视频链接"
            type="textarea"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="封面图" prop="img" v-if="postForm.type == 3">
          <el-upload
            :action="url"
            list-type="picture-card"
            :auto-upload="true"
            :on-success="handleIconSuccess"
            :on-remove="removeFile"
            limit="1"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <!-- 选择框 -->
        <el-form-item label="圈子" prop="topicId">
          <el-select
            v-model="postForm.topicId"
            clearable
            placeholder="请选择圈子"
            @change="changeTopic"
          >
            <el-option
              v-for="item in optionsTopic"
              :key="item.id"
              :label="item.topicName"
              :value="item.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="话题" prop="checkDiscuss" v-if="postForm.topicId">
          <el-radio-group v-model="checkDiscuss" @change="changeDiscuss">
            <el-radio :label="1">不选择</el-radio>
            <el-radio :label="0">选择</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="话题"
          prop="discussId"
          v-if="checkDiscuss == 0 && postForm.topicId"
        >
          <el-select
            v-model="postForm.discussId"
            clearable
            placeholder="请选择话题"
          >
            <el-option
              v-for="item in optionsDiscuss"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="cancelPostPops()">取消</el-button>
          <el-button type="primary" @click="postSubmit()">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import TextEditor from "@/components/content/textEdit.vue";
import AddOrUpdate from "./user-add-or-update";
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";
const { proxy } = getCurrentInstance();
const router = useRouter();

const dataForm = reactive({
  key: "",
  status: "",
  type: "",
  vipStatus: "",
});
const punish = reactive({
  resetPost: 0,
  resetAvatar: 0,
  resetIntro: 0,
  resetUsername: 0,
  resetBg: 0,
  uid: 0,
});
const dataList = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const totalPage = ref(0);
const dataListLoading = ref(false);
const dataListSelections = ref([]);
const addOrUpdateVisible = ref(false);

const dialogVisible = ref(false);
const addPostHandle = ref(false);
const checkDiscuss = ref(1);
const postTypeValue = ref(1);
const optionsDiscuss = ref([]);
const optionsTopic = ref([]);
const url = ref("");
const childRef = ref(null);
const videoUploadType = ref(0);
const videoPostUrl = ref("");
const postForm = reactive({
  title: "",
  type: 1,
  topicId: "",
  discussId: "",
  content: "",
  media: [],
  longitude: 0,
  latitude: 0,
  address: "",
  cut: 0,
  pay: "",
  uid: "",
});
const options = reactive([
  {
    value: 0,
    label: "普通用户",
  },
  {
    value: 1,
    label: "官方账户",
  },
  {
    value: 2,
    label: "虚拟用户",
  },
]);

const options2 = reactive([
  {
    value: 0,
    label: "正常",
  },
  {
    value: 1,
    label: "封号",
  },
  {
    value: 2,
    label: "禁言",
  },
      {
    value: 3,
    label: "注销",
  }
]);
const options3 = reactive([
  {
    value: 0,
    label: "普通用户",
  },
  {
    value: 1,
    label: "会员用户",
  },
]);

// 获取数据列表
function getDataList() {
  dataListLoading.value = true;
  url.value = proxy.$http.adornUrl(
    `/sys/oss/upload?token=${sessionStorage.getItem("token")}`
  );
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/user/list"),
      method: "get",
      params: proxy.$http.adornParams({
        page: pageIndex.value,
        limit: pageSize.value,
        key: dataForm.key,
        vipStatus: dataForm.vipStatus,
        status: dataForm.status,
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
        return item.uid;
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
          url: proxy.$http.adornUrl("/admin/user/delete"),
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
  dataForm.type = "";
  dataForm.vipStatus = "";
  pageIndex.value = 1;
  pageSize.value = 10;
  getDataList();
}
function punishOpen(val) {
  punish.uid = val;
  dialogVisible.value = true;
}
//对账户处罚
function doPunish() {
  dialogVisible.value = false;
  proxy
    .$http({
      url: proxy.$http.adornUrl(`/admin/user/punish`),
      method: "post",
      data: punish,
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
}

// 禁用
function banHandle(id) {
  ElMessageBox.confirm(`确定对该用户禁用操作?`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    proxy
      .$http({
        url: proxy.$http.adornUrl(`/admin/user/ban/${id}`),
        method: "get",
        params: proxy.$http.adornParams(),
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
// 解除禁用
function openBanHandle(id) {
  ElMessageBox.confirm(`确定对该用户解除禁用操作?`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    proxy
      .$http({
        url: proxy.$http.adornUrl(`/admin/user/openBan/${id}`),
        method: "get",
        params: proxy.$http.adornParams(),
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
function createVirtualUser(){
  proxy
    .$http({
      url: proxy.$http.adornUrl(`/admin/user/createVirtualUser`),
      method: "post"
    })
    .then(({ data }) => {
      if (data && data.code === 0) {
        ElMessage({
            message: "虚拟人机账户创建成功",
            type: "success",
            duration: 1500,
            onClose: () => {
              getDataList();
            },
          });
      }else{
        ElMessage({
            message: data.msg,
            type: "error",
            duration: 1500
          });
      }
    });
}
function getJoinTopicList() {
  var id = postForm.uid;
  proxy
    .$http({
      url: proxy.$http.adornUrl(`/admin/topic/getJoinTopicList/${id}`),
      method: "get",
      params: proxy.$http.adornParams(),
    })
    .then(({ data }) => {
      if (data && data.code === 0) {
        optionsTopic.value = data.result;
      }
    });
}

function addPostByUid(uidInfo) {
  postForm.uid = uidInfo;
  addPostHandle.value = true;
  getJoinTopicList();
}

function cancelPostPops() {
  addPostHandle.value = false;
}

//切换圈子触发
function changeTopic(i) {
  postForm.topicId = i;
  postForm.discussId = "";
  checkDiscuss.value = 1;
}
function changeDiscuss(disVal) {
  postForm.discussId = "";
  if (disVal == 0) {
    getDiscussList();
  }
}

function changePostType(postVal) {
  postForm.type = postVal;
  postForm.title = "";
  postForm.content = "";
  postForm.discussId = "";
  postForm.topicId = "";
  postForm.media = [];
}
function changeVideoUploadType(videoUrlVal) {}
function getDiscussList() {
  if (checkDiscuss.value == 1 && !postForm.topicId) {
    return;
  }
  var id = postForm.topicId;
  proxy
    .$http({
      url: proxy.$http.adornUrl(`/admin/discuss/getDiscussList/${id}`),
      method: "get",
      params: proxy.$http.adornParams(),
    })
    .then(({ data }) => {
      if (data && data.code === 0) {
        optionsDiscuss.value = data.result;
      }
    });
}
function handleIconSuccess(response) {
  // 检查响应的code字段，判断是否真正成功
  if (response && response.code === 0) {
    // 上传成功
    ElMessage({
        message: "上传成功",
        type: "success",
    });
    postForm.media.push(response.url);
  } else {
    // 上传失败，显示后端返回的错误信息
    const errorMessage = response && response.msg ? response.msg : '文件上传失败';
    ElMessage.error(errorMessage);
  }
}
function handleRemove(file) {
  console.log("handleRemove", file);
}
function removeFile(file, fileLists) {
  let mediaList = [];
  fileLists.forEach(function (item, index) {
    mediaList.push(item.response.url);
  });
  postForm.media = mediaList;
}
function handlePictureCardPreview(fileInfo) {
  dialogImageUrl.value = fileInfo.url;
  dialogVisible.value = true;
}
function handleDownload(file) {
  console.log(file);
}
function postSubmit(uid) {
  if (postForm.type == 3 && childRef.value) {
    postForm.content = childRef.value.valueHtml;
  }
  if (postForm.type == 3 && postForm.media.length == 0) {
    ElMessage({
      message: "封面图不能为空",
      type: "error",
    });
    return;
  }
  if (postForm.type == 3 && postForm.content == "<p><br></p>") {
    ElMessage({
      message: "富文本内容不能为空",
      type: "error",
    });
    return;
  }
  if (postForm.type == 2 && videoUploadType.value == 1) {
    if (!videoPostUrl.value) {
      ElMessage({
        message: "视频链接不能为空",
        type: "error",
      });
      return;
    }
    const regex = /^(http|https):\/\/.*\.mp4$/;
    if(!regex.test(videoPostUrl.value)){
        ElMessage({
        message: "视频链接不合法",
        type: "error",
      });
      return;
    }
    postForm.media = [];
    postForm.media.push(videoPostUrl.value);
  }
  if (!postForm.title) {
    ElMessage({
      message: "标题不能为空",
      type: "error",
    });
    return;
  }
  if (!postForm.content) {
    ElMessage({
      message: "内容不能为空",
      type: "error",
    });
    return;
  }
  if (!postForm.topicId) {
    ElMessage({
      message: "必须选择圈子",
      type: "error",
    });
    return;
  }
  proxy
    .$http({
      url: proxy.$http.adornUrl("/admin/post/postSubmit"),
      method: "post",
      data: postForm,
    })
    .then(({ data }) => {
      if (data && data.code === 0) {
        addPostHandle.value = false;

        ElMessage({
          message: "发布成功",
          type: "success",
        });
        location.reload();
      } else {
        ElMessage({
          message: data.msg,
          type: "error",
        });
      }
    });
}

function handleClose() {
  postForm.title = "";
  postForm.content = "";
  postForm.discussId = "";
  postForm.topicId = "";
  postForm.uid = "";
  postForm.media = [];
  // proxy.$refs.uploads.clearFiles();
}
onMounted(() => {
  getDataList();
});
</script>
<style lang="scss" scoped>
.input-search {
  margin-top: 16px;
  margin-left: 15px;
}
.button-group {
  margin-top: 18px;
  margin-left: 15px;
}
</style>
