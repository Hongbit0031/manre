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
                <el-form-item label="名称" prop="name">
                    <el-input
                        v-model="dataForm.name"
                        placeholder="名称"
                        style="width:250px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="跳转路径" prop="url">
                    <el-input
                        v-model="dataForm.url"
                        placeholder="跳转路径"
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
                <el-form-item label="排序" prop="sort">
                    <el-input
                        v-model="dataForm.sort"
                        placeholder="排序"
                        style="width:250px"
                    ></el-input>
                    <p class="formInfo">按照降序排列，数字越大越靠前</p>
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="dataForm.status">
                        <el-radio :label="0">显示</el-radio>
                        <el-radio :label="1">不显示</el-radio>
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
    url: "",
    img: "",
    name: "",
    sort: "",
    status: "",
});
const url = ref("");
const dataRule = reactive({
    url: [{ required: true, message: "跳转路径不能为空", trigger: "blur" }],
    img: [{ required: true, message: "图片地址不能为空", trigger: "blur" }],
    name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
    sort: [{ required: true, message: "排序不能为空", trigger: "blur" }],
    status: [{ required: true, message: "状态不能为空", trigger: "blur" }],
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
                        `/admin/usermenu/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.url = data.userMenu.url;
                        dataForm.value.img = data.userMenu.img;
                        dataForm.value.name = data.userMenu.name;
                        dataForm.value.sort = data.userMenu.sort;
                        dataForm.value.status = data.userMenu.status;
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
                        `/admin/usermenu/${
                            !dataForm.value.id ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        url: dataForm.value.url,
                        img: dataForm.value.img,
                        name: dataForm.value.name,
                        sort: dataForm.value.sort,
                        status: dataForm.value.status,
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
    // 检查响应的code字段，判断是否真正成功
    if (response && response.code === 0) {
        // 上传成功
        dataForm.value.img = response.url;
        proxy.$forceUpdate();
    } else {
        // 上传失败，显示后端返回的错误信息
        const errorMessage = response && response.msg ? response.msg : '图片上传失败';
        ElMessage.error(errorMessage);
    }
}

defineExpose({
    init,
});
</script>