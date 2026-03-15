<template>
    <div>
        <el-dialog
            title="存储配置"
            :close-on-click-modal="false"
            v-model="visible"
        >
            <el-form
                :model="dataForm"
                ref="popForm"
                @keyup.enter="dataFormSubmit()"
                label-width="120px"
            >
                <el-form-item label="存储类型">
                    <el-radio-group v-model="dataForm.local">
                        <el-radio :label="0">云存储</el-radio>
                        <el-radio :label="1">本地存储</el-radio>
                        <el-radio :label="2">minio存储</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="后端接口域名" v-if="dataForm.local === 1">
                    <el-input
                        v-model="dataForm.localUrl"
                        placeholder="填你的后端接口域名,例如：https://api.xxx.com/"
                    ></el-input>
                </el-form-item>
                <el-form-item label="云存储类型" v-if="dataForm.local === 0">
                    <el-radio-group v-model="dataForm.type">
                        <el-radio :label="1">七牛</el-radio>
                        <el-radio :label="2">阿里云</el-radio>
                    </el-radio-group>
                </el-form-item>
                <template v-if="dataForm.type === 1 && dataForm.local === 0">
                    <el-form-item label="域名">
                        <el-input
                            v-model="dataForm.qiniuDomain"
                            placeholder="七牛绑定的域名"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="路径前缀">
                        <el-input
                            v-model="dataForm.qiniuPrefix"
                            placeholder="不设置默认为空"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="AccessKey">
                        <el-input
                            v-model="dataForm.qiniuAccessKey"
                            placeholder="七牛AccessKey"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="SecretKey">
                        <el-input
                            v-model="dataForm.qiniuSecretKey"
                            placeholder="七牛SecretKey"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="空间名">
                        <el-input
                            v-model="dataForm.qiniuBucketName"
                            placeholder="七牛存储空间名"
                        ></el-input>
                    </el-form-item>
                </template>
                <template
                    v-else-if="dataForm.type === 2 && dataForm.local === 0"
                >
                    <el-form-item label="域名">
                        <el-input
                            v-model="dataForm.aliyunDomain"
                            placeholder="阿里云绑定的域名"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="路径前缀">
                        <el-input
                            v-model="dataForm.aliyunPrefix"
                            placeholder="不设置默认为空"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="EndPoint">
                        <el-input
                            v-model="dataForm.aliyunEndPoint"
                            placeholder="阿里云EndPoint"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="AccessKeyId">
                        <el-input
                            v-model="dataForm.aliyunAccessKeyId"
                            placeholder="阿里云AccessKeyId"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="AccessKeySecret">
                        <el-input
                            v-model="dataForm.aliyunAccessKeySecret"
                            placeholder="阿里云AccessKeySecret"
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="BucketName">
                        <el-input
                            v-model="dataForm.aliyunBucketName"
                            placeholder="阿里云BucketName"
                        ></el-input>
                    </el-form-item>
                </template>
            </el-form>
            <p
                style="font-size: 12px; color: #999; margin-left: 40px"
                v-if="dataForm.local === 1"
            >
                本地存储不推荐使用，如果使用本地存储请在Java后端application-xxx.yml中配置文件上传路径
            </p>
            <p
                style="font-size: 12px; color: #999; margin-left: 40px"
                v-if="dataForm.local === 2"
            >
                使用minio存储请在Java后端application.yml中配置minio基本信息
            </p>
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
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from "vue";
import { isAuth } from "@/utils/index";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const visible = ref(false);
const dataForm = reactive({
    aliyunAccessKeyId: "",
    aliyunAccessKeySecret: "",
    aliyunBucketName: "",
    aliyunDomain: "",
    aliyunEndPoint: "",
    aliyunPrefix: "",
    qiniuAccessKey: "",
    qiniuBucketName: "",
    qiniuDomain: "",
    qiniuPrefix: "",
    qiniuSecretKey: "",
    type: "",
    local: "",
    localUrl: "",
});

function init(id) {
    visible.value = true;
    proxy
        .$http({
            url: proxy.$http.adornUrl("/sys/oss/config"),
            method: "get",
            params: proxy.$http.adornParams(),
        })
        .then(({ data }) => {
            if (data.code == 0) {
                dataForm.aliyunAccessKeyId = data.config.aliyunAccessKeyId;
                dataForm.aliyunAccessKeySecret =
                    data.config.aliyunAccessKeySecret;
                dataForm.aliyunBucketName = data.config.aliyunBucketName;
                dataForm.aliyunDomain = data.config.aliyunDomain;
                dataForm.aliyunEndPoint = data.config.aliyunEndPoint;
                dataForm.aliyunPrefix = data.config.aliyunPrefix;
                dataForm.qiniuAccessKey = data.config.qiniuAccessKey;
                dataForm.qiniuBucketName = data.config.qiniuBucketName;
                dataForm.qiniuDomain = data.config.qiniuDomain;
                dataForm.qiniuPrefix = data.config.qiniuPrefix;
                dataForm.qiniuSecretKey = data.config.qiniuSecretKey;
                dataForm.type = data.config.type;
                dataForm.local = parseInt(data.storageMethod);
                dataForm.localUrl = data.localStorageUrl;
            }
        });
}

// 表单提交
function dataFormSubmit() {
    proxy.$refs["popForm"].validate((valid) => {
        if (valid) {
            if (dataForm.local === 0) {
                proxy
                    .$http({
                        url: proxy.$http.adornUrl("/sys/oss/saveConfig"),
                        method: "post",
                        data: proxy.$http.adornData(dataForm),
                    })
                    .then(({ data }) => {
                        if (data && data.code === 0) {
                            ElMessage({
                                message: "操作成功",
                                type: "success",
                                duration: 1500,
                                onClose: () => {
                                    visible.value = false;
                                },
                            });
                        } else {
                            ElMessage.error(data.msg);
                        }
                    });
            } else {
                proxy
                    .$http({
                        url: proxy.$http.adornUrl("/sys/oss/saveLocalConfig"),
                        method: "post",
                        data: proxy.$http.adornData(dataForm),
                    })
                    .then(({ data }) => {
                        if (data && data.code === 0) {
                            ElMessage({
                                message: "操作成功",
                                type: "success",
                                duration: 1500,
                                onClose: () => {
                                    visible.value = false;
                                },
                            });
                        } else {
                            ElMessage.error(data.msg);
                        }
                    });
            }
        }
    });
}

defineExpose({
    init,
});
</script>
