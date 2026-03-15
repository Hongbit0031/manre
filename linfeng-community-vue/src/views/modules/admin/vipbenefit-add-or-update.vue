<template>
  <div>
    <el-dialog
      :title="!dataForm.id ? '新增' : '修改'"
      :close-on-click-modal="false"
      width="45%"
      v-model="visible"
    >
      <el-form
        :model="dataForm"
        :rules="dataRule"
        ref="popForm"
        @keyup.enter="dataFormSubmit()"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="dataForm.title"
            placeholder="会员权益标题"
            style="width: 300px"
          ></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="describes">
          <el-input
            v-model="dataForm.describes"
            placeholder="会员权益描述"
            style="width: 300px"
          ></el-input>
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-upload
            class="avatar-uploader"
            :action="url"
            :show-file-list="false"
            :on-success="handleIconSuccess"
          >
            <img v-if="dataForm.icon" :src="dataForm.icon" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="0">有效</el-radio>
            <el-radio :label="1">无效</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input
            v-model="dataForm.sort"
            placeholder="排序"
            style="width: 100px"
          ></el-input>
        </el-form-item>
        <p class="formInfo">
            数值越大排序越靠前
        </p>
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
  title: "",
  describes: "",
  icon: "",
  status: 0,
  sort: 1,
});
const dataRule = reactive({
  title: [{ required: true, message: "会员权益标题不能为空", trigger: "blur" }],
  describes: [
    { required: true, message: "会员权益描述不能为空", trigger: "blur" },
  ],
  icon: [{ required: true, message: "图标不能为空", trigger: "blur" }],
  status: [
    { required: true, message: "状态不能为空", trigger: "blur" },
  ],
  sort: [{ required: true, message: "排序不能为空", trigger: "blur" }],
});
const url = ref("");


function init(id) {
  dataForm.value.id = id || 0;
  url.value = proxy.$http.adornUrl(
        `/sys/oss/upload?token=${sessionStorage.getItem("token")}`
    );
  visible.value = true;
  nextTick(() => {
    proxy.$refs.popForm.resetFields();
    if (dataForm.value.id) {
      proxy
        .$http({
          url: proxy.$http.adornUrl(
            `/admin/vipbenefit/info/${dataForm.value.id}`
          ),
          method: "get",
          params: proxy.$http.adornParams(),
        })
        .then(({ data }) => {
          if (data && data.code === 0) {
            dataForm.value.title = data.vipBenefit.title;
            dataForm.value.describes = data.vipBenefit.describes;
            dataForm.value.icon = data.vipBenefit.icon;
            dataForm.value.status = data.vipBenefit.status;
            dataForm.value.sort = data.vipBenefit.sort;
          }
        });
    }
  });
}

function handleIconSuccess(response) {
    // 检查响应的code字段，判断是否真正成功
    if (response && response.code === 0) {
        // 上传成功
        dataForm.value.icon = response.url;
        proxy.$forceUpdate();
    } else {
        // 上传失败，显示后端返回的错误信息
        const errorMessage = response && response.msg ? response.msg : '图标上传失败';
        ElMessage.error(errorMessage);
    }
}

// 表单提交
function dataFormSubmit() {
  proxy.$refs["popForm"].validate((valid) => {
    if (valid) {
      proxy
        .$http({
          url: proxy.$http.adornUrl(
            `/admin/vipbenefit/${!dataForm.value.id ? "save" : "update"}`
          ),
          method: "post",
          data: proxy.$http.adornData({
            id: dataForm.value.id || undefined,
            title: dataForm.value.title,
            describes: dataForm.value.describes,
            icon: dataForm.value.icon,
            status: dataForm.value.status,
            sort: dataForm.value.sort,
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