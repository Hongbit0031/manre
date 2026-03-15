<template>
    <div>
        <el-dialog
            title="上传文件"
            :close-on-click-modal="false"
            @close="closeHandle"
            v-model="visible"
        >
            <el-upload
                drag
                :action="url"
                :before-upload="beforeUploadHandle"
                :on-success="successHandle"
                multiple
                :file-list="fileList"
                style="text-align: center"
            >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">
                    将文件拖到此处，或<em>点击上传</em>
                </div>
                <div class="el-upload__tip">
                    支持jpg、png、gif格式的图片以及mp4视频
                </div>
            </el-upload>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const visible = ref(false);
const fileList = ref([]);
const url = ref("");
const num = ref(0);
const successNum = ref(0);

function init(id) {
    url.value = proxy.$http.adornUrl(
        `/sys/oss/upload?token=${sessionStorage.getItem("token")}`
    );
    visible.value = true;
}

// 上传之前
function beforeUploadHandle(file) {
    console.log(file.type)
    if (
        file.type !== "image/jpg" &&
        file.type !== "image/jpeg" &&
        file.type !== "image/png" &&
        file.type !== "image/gif" &&
        file.type !== "video/mp4"
    ) {
        ElMessage.error("只支持jpg、png、gif格式的图片！");
        return false;
    }
    num.value++;
}

// 上传成功
function successHandle(response, file, fileLists) {
    fileList.value = fileLists;
    successNum.value++;
    if (response && response.code === 0) {
        if (num.value === successNum.value) {
            ElMessageBox.confirm("操作成功, 是否继续操作?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "warning",
            }).catch(() => {
                visible.value = false;
            });
        }
    } else {
        ElMessage.error(response.msg);
    }
}
// 弹窗关闭时
function closeHandle() {
    fileList.value = [];
    proxy.$emit("refreshDataList");
}

defineExpose({
    init,
});
</script>
