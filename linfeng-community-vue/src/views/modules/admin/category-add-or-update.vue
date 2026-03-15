<template>
    <div>
        <el-dialog
            :title="!dataForm.cateId ? '新增' : '修改'"
            :close-on-click-modal="false"
            v-model="visible"
            width="30%"
        >
            <el-form
                :model="dataForm"
                :rules="dataRule"
                ref="popForm"
                @keyup.enter="dataFormSubmit()"
                label-width="80px"
            >
                <el-form-item label="分类名称" prop="cateName">
                    <el-input
                        v-model="dataForm.cateName"
                        placeholder="分类名称"
                        style="width: 250px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="是否推荐" prop="isTop">
                    <el-radio-group v-model="dataForm.isTop">
                        <el-radio :label="1">是</el-radio>
                        <el-radio :label="0">否</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="分类图片" prop="coverImage">
                    <el-upload
                        class="avatar-uploader"
                        :action="url"
                        :show-file-list="false"
                        :on-success="handleIconSuccess"
                    >
                        <img
                            v-if="dataForm.coverImage"
                            :src="dataForm.coverImage"
                            class="avatar"
                        />
                        <el-icon v-else class="avatar-uploader-icon"
                            ><Plus
                        /></el-icon>
                    </el-upload>
                </el-form-item>
                <p class="formInfo">建议尺寸：100*100像素，jpg、png图片类型</p>
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
    cateId: 0,
    cateName: "",
    isTop: 0,
    coverImage: "",
});
const url = ref("");
const dataRule = reactive({
    cateName: [
        { required: true, message: "分类名称不能为空", trigger: "blur" },
    ],
    isTop: [
        {
            required: true,
            message: "是否推荐(0不推荐1推荐)不能为空",
            trigger: "blur",
        },
    ],
    coverImage: [{ required: true, message: "图片不能为空", trigger: "blur" }],
});
function init(id) {
    dataForm.value.cateId = id || 0;
    url.value = proxy.$http.adornUrl(
        `/sys/oss/upload?token=${sessionStorage.getItem("token")}`
    );
    visible.value = true;
    nextTick(() => {
        proxy.$refs.popForm.resetFields();
        if (dataForm.value.cateId) {
            proxy
                .$http({
                    url: proxy.$http.adornUrl(
                        `/admin/category/info/${dataForm.value.cateId}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.cateName = data.category.cateName;
                        dataForm.value.isTop = data.category.isTop;
                        dataForm.value.coverImage = data.category.coverImage;
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
                        `/admin/category/${
                            !dataForm.value.cateId ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        cateId: dataForm.value.cateId || undefined,
                        cateName: dataForm.value.cateName,
                        isTop: dataForm.value.isTop,
                        coverImage: dataForm.value.coverImage,
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
    dataForm.value.coverImage = response.url;
    proxy.$forceUpdate();
}

defineExpose({
    init,
});
</script>