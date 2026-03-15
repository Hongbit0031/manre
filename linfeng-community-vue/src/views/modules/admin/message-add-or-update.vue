<template>
  <div>
    <el-dialog
      :title="!dataForm.mId ? '新增' : '修改'"
      :close-on-click-modal="false"
      v-model="visible"
      width="35%"
    >
      <el-form
        :model="dataForm"
        :rules="dataRule"
        ref="popForm"
        @keyup.enter="dataFormSubmit()"
        label-width="120px"
      >
        
        <el-form-item label="接收用户" prop="toUid">
          <el-input
            v-model="dataForm.toUid"
            placeholder="输入用户ID或用户名"
          ></el-input>
        </el-form-item>
        <el-form-item label="推送标题" prop="title">
          <el-input v-model="dataForm.title" placeholder="推送标题"></el-input>
        </el-form-item>
        <el-form-item label="消息内容" prop="content">
          <el-input
            v-model="dataForm.content"
            placeholder="消息内容"
            type="textarea"
          ></el-input>
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>


<script setup>
import { ElMessage } from "element-plus";
import { ref, reactive, nextTick, getCurrentInstance } from "vue";
const { proxy } = getCurrentInstance();
const visible = ref(false);
const dataForm = ref({
  mId: 0,
  fromUid: "",
  toUid: "",
  postId: "",
  title: "系统通知",
  content: "",
  status: "",
  type: "",
  createTime: "",
});

const dataRule = reactive({
  fromUid: [{ required: true, message: "发送者uid不能为空", trigger: "blur" }],
  toUid: [{ required: true, message: "接收者uid不能为空", trigger: "blur" }],
  postId: [{ required: true, message: "帖子id不能为空", trigger: "blur" }],
  title: [{ required: true, message: "推送标题不能为空", trigger: "blur" }],
  content: [{ required: true, message: "消息内容不能为空", trigger: "blur" }],
  status: [
    { required: true, message: "0未读，1已读不能为空", trigger: "blur" },
  ],
  type: [
    {
      required: true,
      message: "1为点赞，2为评论  3为收藏 4为关注  5为推送文章 6私聊不能为空",
      trigger: "blur",
    },
  ],
  createTime: [
    { required: true, message: "创建时间不能为空", trigger: "blur" },
  ],
});
function init(id) {
  dataForm.value.mId = id || 0;
  visible.value = true;
  nextTick(() => {
    proxy.$refs.popForm.resetFields();
    if (dataForm.value.mId) {
      proxy
        .$http({
          url: proxy.$http.adornUrl(
            `/admin/message/info/${dataForm.value.mId}`
          ),
          method: "get",
          params: proxy.$http.adornParams(),
        })
        .then(({ data }) => {
          if (data && data.code === 0) {
            dataForm.value.fromUid = data.message.fromUid;
            dataForm.value.toUid = data.message.toUid;
            dataForm.value.postId = data.message.postId;
            dataForm.value.title = data.message.title;
            dataForm.value.content = data.message.content;
            dataForm.value.status = data.message.status;
            dataForm.value.type = data.message.type;
            dataForm.value.createTime = data.message.createTime;
          }
        });
    }
  });
}

// 表单提交
function dataFormSubmit() {
  proxy.$refs["popForm"].validate((valid) => {
    if (valid) {
      proxy
        .$http({
          url: proxy.$http.adornUrl(
            `/admin/message/${!dataForm.value.mId ? "save" : "update"}`
          ),
          method: "post",
          data: proxy.$http.adornData({
            mId: dataForm.value.mId || undefined,
            fromUid: dataForm.value.fromUid,
            toUid: dataForm.value.toUid,
            postId: dataForm.value.postId,
            title: dataForm.value.title,
            content: dataForm.value.content,
            status: dataForm.value.status,
            type: dataForm.value.type,
            createTime: dataForm.value.createTime,
          }),
        })
        .then(({ data }) => {
          if (data && data.code === 0) {
            ElMessage({
              message: "发送成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                visible.value = false;
                proxy.$emit("refreshDataList");
              },
            });
          } else {
            ElMessage.error(data.msg);
          }
        });
    }
  });
}

defineExpose({
  init,
});
</script>