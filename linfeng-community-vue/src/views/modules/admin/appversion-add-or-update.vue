<template>
  <div>
    <el-dialog
      :title="!dataForm.id ? '新增' : '修改'"
      :close-on-click-modal="false"
      v-model="visible"
    >
      <el-form
        :model="dataForm"
        :rules="dataRule"
        ref="popForm"
        label-width="170px"
      >
        <el-form-item label="版本号" prop="version">
          <el-input
            v-model="dataForm.version"
            placeholder="版本号(如1.0.1)"
          ></el-input>
        </el-form-item>
        <el-form-item label="最低版本号" prop="minVersion">
          <el-input
            v-model="dataForm.minVersion"
            placeholder="最低要求版本号(用于强制更新判断)"
          ></el-input>
        </el-form-item>
        <el-form-item label="是否强制更新" prop="isForce">
          <el-radio-group v-model="dataForm.isForce">
            <el-radio :label="1">强制更新</el-radio>
            <el-radio :label="0">非强制</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="更新内容描述" prop="content">
          <el-input
            v-model="dataForm.content"
            type="textarea"
            placeholder="更新内容描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="通用下载地址" prop="downloadUrl">
          <el-input
            v-model="dataForm.downloadUrl"
            placeholder="通用下载地址"
          ></el-input>
        </el-form-item>
        <el-form-item label="Android应用市场地址" prop="androidUrl">
          <el-input
            v-model="dataForm.androidUrl"
            placeholder="Android应用市场地址"
          ></el-input>
        </el-form-item>
        <el-form-item label="iOS应用市场地址" prop="iosUrl">
          <el-input
            v-model="dataForm.iosUrl"
            placeholder="iOS应用市场地址"
          ></el-input>
        </el-form-item>
        <el-form-item label="版本状态" prop="status">
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
  id: 0,
  version: "",
  minVersion: "",
  isForce: "",
  content: "",
  downloadUrl: "",
  androidUrl: "",
  iosUrl: "",
  status: 1,
  createTime: "",
  updateTime: "",
});
const dataRule = reactive({
  version: [
    { required: true, message: "版本号(如1.0.1)不能为空", trigger: "blur" },
  ],
  minVersion: [
    {
      required: true,
      message: "最低要求版本号(用于强制更新判断)不能为空",
      trigger: "blur",
    },
  ],
  isForce: [
    {
      required: true,
      message: "是否强制更新(1:强制更新,0:非强制)不能为空",
      trigger: "blur",
    },
  ],
  content: [
    { required: true, message: "更新内容描述不能为空", trigger: "blur" },
  ],
  // downloadUrl: [
  //   { required: true, message: "通用下载地址不能为空", trigger: "blur" },
  // ],
  // androidUrl: [
  //   { required: true, message: "Android应用市场地址不能为空", trigger: "blur" },
  // ],
  // iosUrl: [
  //   { required: true, message: "iOS应用市场地址不能为空", trigger: "blur" },
  // ],
  status: [
    {
      required: true,
      message: "版本状态(1:启用,0:禁用)不能为空",
      trigger: "blur",
    },
  ],
  createTime: [
    { required: true, message: "创建时间不能为空", trigger: "blur" },
  ],
  updateTime: [
    { required: true, message: "更新时间不能为空", trigger: "blur" },
  ],
});
function init(id) {
  dataForm.value.id = id || 0;
  visible.value = true;
  nextTick(() => {
    proxy.$refs.popForm.resetFields();
    if (dataForm.value.id) {
      proxy
        .$http({
          url: proxy.$http.adornUrl(
            `/admin/appversion/info/${dataForm.value.id}`
          ),
          method: "get",
          params: proxy.$http.adornParams(),
        })
        .then(({ data }) => {
          if (data && data.code === 0) {
            dataForm.value.version = data.appVersion.version;
            dataForm.value.minVersion = data.appVersion.minVersion;
            dataForm.value.isForce = data.appVersion.isForce;
            dataForm.value.content = data.appVersion.content;
            dataForm.value.downloadUrl = data.appVersion.downloadUrl;
            dataForm.value.androidUrl = data.appVersion.androidUrl;
            dataForm.value.iosUrl = data.appVersion.iosUrl;
            dataForm.value.status = data.appVersion.status;
            dataForm.value.createTime = data.appVersion.createTime;
            dataForm.value.updateTime = data.appVersion.updateTime;
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
            `/admin/appversion/${!dataForm.value.id ? "save" : "update"}`
          ),
          method: "post",
          data: proxy.$http.adornData({
            id: dataForm.value.id || undefined,
            version: dataForm.value.version,
            minVersion: dataForm.value.minVersion,
            isForce: dataForm.value.isForce,
            content: dataForm.value.content,
            downloadUrl: dataForm.value.downloadUrl,
            androidUrl: dataForm.value.androidUrl,
            iosUrl: dataForm.value.iosUrl,
            status: dataForm.value.status,
            createTime: dataForm.value.createTime,
            updateTime: dataForm.value.updateTime,
          }),
        })
        .then(({ data }) => {
          if (data && data.code === 0) {
            ElMessage({
              message: "操作成功",
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