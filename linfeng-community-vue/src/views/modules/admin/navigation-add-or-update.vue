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
                         style="width:250px"
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
                <p class="formInfo">建议尺寸：200*200像素，jpg、png图片类型</p>
                <el-form-item label="跳转路径" prop="url">
                    <el-input
                        v-model="dataForm.url"
                        placeholder="跳转路径"
                         style="width:250px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="跳转类型" prop="type">
                    <el-radio-group v-model="dataForm.type">
                        <el-radio :label="0">页面</el-radio>
                        <el-radio :label="1">外链</el-radio>
                    </el-radio-group>
                </el-form-item>
                <p class="formInfo">
                    外链就是外部网站的链接，页面指项目内部页面的跳转
                </p>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="dataForm.status">
                        <el-radio :label="0">正常</el-radio>
                        <el-radio :label="1">禁用</el-radio>
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
    img: "",
    url: "",
    type: "",
    status: "",
    createTime: "",
    updateTime: "",
});
const url = ref("");
const dataRule = reactive({
    title: [{ required: true, message: "标题不能为空", trigger: "blur" }],
    img: [{ required: true, message: "图片不能为空", trigger: "blur" }],
    url: [{ required: true, message: "跳转路径不能为空", trigger: "blur" }],
    type: [
        {
            required: true,
            message: "跳转类型0页面1外链不能为空",
            trigger: "blur",
        },
    ],
    status: [
        { required: true, message: "状态0正常1禁用不能为空", trigger: "blur" },
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
                        `/admin/navigation/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.title = data.navigation.title;
                        dataForm.value.img = data.navigation.img;
                        dataForm.value.url = data.navigation.url;
                        dataForm.value.type = data.navigation.type;
                        dataForm.value.status = data.navigation.status;
                        dataForm.value.createTime = data.navigation.createTime;
                        dataForm.value.updateTime = data.navigation.updateTime;
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
                        `/admin/navigation/${
                            !dataForm.value.id ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        title: dataForm.value.title,
                        img: dataForm.value.img,
                        url: dataForm.value.url,
                        type: dataForm.value.type,
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
function handleIconSuccess(response) {
    dataForm.value.img = response.url;
    proxy.$forceUpdate();
}
defineExpose({
    init,
});
</script>