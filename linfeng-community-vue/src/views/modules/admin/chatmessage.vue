<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.content" placeholder="会话内容" :prefix-icon="Search" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.sessionId" placeholder="用户会话唯一标识" :prefix-icon="Search" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.senderId" placeholder="发送用户ID" :prefix-icon="Search" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.receiverId" placeholder="接收用户ID" :prefix-icon="Search" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()" style="margin-left: 10px" type="primary">查询</el-button>
          <el-button @click="refresh()">重置</el-button>
        <el-button v-if="isAuth('admin:chatmessage:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%;">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="id"
        header-align="center"
        align="center"
        label="私聊ID">
      </el-table-column>
      <el-table-column
        prop="sessionId"
        header-align="center"
        align="center"
        label="用户会话唯一标识">
      </el-table-column>
      <el-table-column
        prop="senderId"
        header-align="center"
        align="center"
        width="150"
        label="发送者"
      >
        <template v-slot="scope">
          <div>
            <div style="color: #333; font-weight: 600">
              UID:{{ scope.row.senderId }}
            </div>
            <div>{{ scope.row.senderUsername }}</div>
            <img style="width: 40px; height: 40px" :src="scope.row.senderAvatar" />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="receiverId"
        header-align="center"
        align="center"
        width="150"
        label="接收者"
      >
        <template v-slot="scope">
          <div>
            <div style="color: #333; font-weight: 600">
              UID:{{ scope.row.receiverId }}
            </div>
            <div>{{ scope.row.receiverUsername }}</div>
            <img style="width: 40px; height: 40px" :src="scope.row.receiverAvatar" />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="sendTime"
        header-align="center"
        align="center"
        label="发送时间">
      </el-table-column>
      <el-table-column
        prop="content"
        header-align="center"
        align="center"
        label="内容">
        <template v-slot="scope">
          <div v-if="scope.row.messageType === 'text'">
            <span :title="scope.row.content">{{ scope.row.content }}</span>
          </div>
          <div v-else-if="scope.row.messageType === 'image'">
            <el-button type="primary" text size="small" @click="previewImage(scope.row.content)">预览图片</el-button>
          </div>
          <div v-else-if="scope.row.messageType === 'video'">
            <el-button type="success" text size="small" @click="previewVideo(scope.row.content)">预览视频</el-button>
          </div>
          <div v-else-if="scope.row.messageType === 'file'">
            <el-button type="danger" text size="small" @click="downloadFile(scope.row.content)">下载文件</el-button>
          </div>
          <div v-else>
            <span :title="scope.row.content">{{ scope.row.content }}</span>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column
        prop="messageType"
        header-align="center"
        align="center"
        label="类型"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.messageType == 'text'" type="info">文本</el-tag>
            <el-tag v-else-if="scope.row.messageType == 'video'" type="success">视频</el-tag>
            <el-tag v-else-if="scope.row.messageType == 'file'" type="danger">文件</el-tag>
            <el-tag v-else-if="scope.row.messageType == 'image'" type="primary">图片</el-tag>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column
        prop="isWithdrawn"
        header-align="center"
        align="center"
        label="是否撤回"
      >
        <template v-slot="scope">
          <div>
            <el-tag v-if="scope.row.isWithdrawn == 0" type="info">未撤回</el-tag>
            <el-tag v-else-if="scope.row.isWithdrawn == 1" type="primary">已撤回</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="updateTime"
        header-align="center"
        align="center"
        label="更新时间">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="190"
        label="操作">
        <template v-slot="scope">
          <el-button type="primary" text size="small" @click="findChatBySessionId(scope.row.sessionId)">查看会话</el-button>
          <el-button type="danger" text size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
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
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>

    <!-- 图片预览弹窗 -->
    <el-dialog title="图片预览" v-model="imagePreviewVisible" width="50%">
      <div style="text-align: center;">
        <img :src="previewImageUrl" style="max-width: 100%; max-height: 70vh;" />
      </div>
      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="imagePreviewVisible = false">关 闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 视频预览弹窗 -->
    <el-dialog title="视频预览" v-model="videoPreviewVisible" width="50%">
      <div style="text-align: center;">
        <video :src="previewVideoUrl" controls style="max-width: 100%; max-height: 70vh;"></video>
      </div>
      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="videoPreviewVisible = false">关 闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>

  import AddOrUpdate from './chatmessage-add-or-update'
  import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
  import { isAuth } from "@/utils/index";
    import { ElMessage, ElMessageBox } from "element-plus";
    import { Search } from "@element-plus/icons-vue";
    import { DEV_API, PROD_API } from "../../../../config/index";
  const { proxy } = getCurrentInstance();

  // 根据环境选择API地址
  const baseApi = process.env.NODE_ENV === 'development' ? DEV_API : PROD_API;

  const dataForm = reactive({
      content: "",
      sessionId: "",
      senderId: "",
      receiverId: "",
  });
  const dataList = ref([]);
  const pageIndex = ref(1);
  const pageSize = ref(10);
  const totalPage = ref(0);
  const dataListLoading = ref(false);
  const dataListSelections = ref([]);
  const addOrUpdateVisible = ref(false);
  const imagePreviewVisible = ref(false);
  const videoPreviewVisible = ref(false);
  const previewImageUrl = ref("");
  const previewVideoUrl = ref("");

  function previewImage(content) {
    // 判断是否已经是完整URL
    if (content.startsWith('http://') || content.startsWith('https://')) {
      previewImageUrl.value = content;
    } else {
      previewImageUrl.value = `${baseApi}/resource/file/${content}`;
    }
    imagePreviewVisible.value = true;
  }

  function previewVideo(content) {
    // 判断是否已经是完整URL
    if (content.startsWith('http://') || content.startsWith('https://')) {
      previewVideoUrl.value = content;
    } else {
      previewVideoUrl.value = `${baseApi}/resource/file/${content}`;
    }
    videoPreviewVisible.value = true;
  }

  function downloadFile(content) {
    // 拼接完整的文件下载地址
    const fileUrl = `${baseApi}/resource/file/${content}`;
    window.open(fileUrl);
  }

  // 获取数据列表
  function getDataList(){
    let user = sessionStorage.getItem("uname");
    //如果是演示账号，拒绝数据请求
    if (user.startsWith("test")) {
      ElMessage.error("涉及敏感数据，演示账号没有聊天记录数据获取权限");
      return;
    }
      dataListLoading.value = true;
      proxy.$http({
          url: proxy.$http.adornUrl('/admin/chatmessage/list'),
          method: "get",
            params: proxy.$http.adornParams({
              page: pageIndex.value,
              limit: pageSize.value,
              content: dataForm.content,
              sessionId: dataForm.sessionId,
              senderId: dataForm.senderId,
              receiverId: dataForm.receiverId,
          }),
      }).then(({ data }) => {
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
      var ids = id ? [id] : dataListSelections.value.map(item => {
          return item.id
      })
  ElMessageBox.confirm(`确定对[id=${ids.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,"提示",{
                  confirmButtonText: "确定",
                  cancelButtonText: "取消",
                  type: "warning",
                  center: true,
              }).then(() => {
                proxy.$http({
                  url: proxy.$http.adornUrl('/admin/chatmessage/delete'),
                  method: "post",
                  data: proxy.$http.adornData(ids, false),
              }).then(({ data }) => {
          if (data && data.code === 0) {
              ElMessage.success("删除成功");
              getDataList();
          } else {
              ElMessage.error(data.msg)
          }
      });
    })
  .catch(() => {});
  }

  //重置查询
  function refresh() {
      dataForm.content = "";
      dataForm.sessionId = "";
      dataForm.senderId = "";
      dataForm.receiverId = "";
      pageIndex.value = 1;
      pageSize.value = 10;
      getDataList();
  }

  function findChatBySessionId(sessionId) {
    dataForm.sessionId = sessionId;
    getDataList()
  }

  onMounted(() => {
      getDataList()
  });


</script>
