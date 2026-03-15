<template>
    <div>
        <el-dialog
            :title="!dataForm.id ? '新增' : '修改'"
            :close-on-click-modal="false"
            v-model="visible"
            width="35%"
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
                        placeholder="标题"
                        style="width:300px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="图片" prop="img">
                    <el-upload
                        class="avatar-uploader"
                        :action="url"
                        :show-file-list="false"
                        :on-success="handleIconSuccess"
                    >
                        <img
                            v-if="dataForm.img"
                            :src="dataForm.img"
                            class="avatar"
                        />
                        <el-icon v-else class="avatar-uploader-icon"
                            ><Plus
                        /></el-icon>
                    </el-upload>
                </el-form-item>
                <p class="formInfo" style="margin-bottom:20px">
                    建议长宽比例: 个人页3.4:1 广场页2.3:1 jpg、png图片类型
                </p>
                <el-form-item label="路径" prop="url">
                    <el-input
                        v-model="dataForm.url"
                        placeholder="路径"
                        style="width:350px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="跳转类型" prop="type">
                    <el-radio-group v-model="dataForm.type">
                        <el-radio :label="3">页面</el-radio>
                        <el-radio :label="1">外链</el-radio>
                    </el-radio-group>
                </el-form-item>
                <p class="formInfo">
                    外链就是外部网站的链接，页面指项目内部页面的跳转
                </p>
                <el-form-item label="分类" prop="cateId">
                    <el-radio-group v-model="dataForm.cateId">
                        <el-radio :label="0">广场页</el-radio>
                        <el-radio :label="1">个人页</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template v-slot:footer>
                <span class="dialog-footer">
                    <el-button @click="visible = false">取消</el-button>
                    <el-button type="primary" @click="dataFormSubmit()"
                        >确定</el-button
                    >
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
    url: "",
    img: "",
    type: "",
    createTime: "",
    cateId: "",
});
const url = ref("");
const dataRule = reactive({
    title: [{ required: true, message: "标题不能为空", trigger: "blur" }],
    url: [{ required: true, message: "路径不能为空", trigger: "blur" }],
    img: [{ required: true, message: "图片不能为空", trigger: "blur" }],
    type: [
        {
            required: true,
            message: "跳转类型1外链3页面不能为空",
            trigger: "blur",
        },
    ],
    createTime: [
        { required: true, message: "创建时间不能为空", trigger: "blur" },
    ],
    cateId: [{ required: true, message: "分类不能为空", trigger: "blur" }],
});
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
                        `/admin/link/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.title = data.link.title;
                        dataForm.value.url = data.link.url;
                        dataForm.value.img = data.link.img;
                        dataForm.value.type = data.link.type;
                        dataForm.value.createTime = data.link.createTime;
                        dataForm.value.cateId = data.link.cateId;
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
                        `/admin/link/${!dataForm.value.id ? "save" : "update"}`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        title: dataForm.value.title,
                        url: dataForm.value.url,
                        img: dataForm.value.img,
                        type: dataForm.value.type,
                        createTime: dataForm.value.createTime,
                        cateId: dataForm.value.cateId,
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


function handleIconSuccess(response) {
  if (response && response.code === 0) {
    dataForm.value.img = response.url;
    proxy.$forceUpdate();
  } else {
    const errorMessage = response && response.msg ? response.msg : '图片上传失败';
    ElMessage.error(errorMessage);
  }
}

defineExpose({
    init,
});
</script>