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
                <el-form-item label="描述" prop="content">
                    <el-input
                        v-model="dataForm.content"
                        placeholder="描述"
                        type="textarea"
                        disabled
                    ></el-input>
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="dataForm.status">
                        <el-radio :label="2">驳回</el-radio>
                        <el-radio :label="1">受理</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="平台反馈" prop="feedback">
                    <el-input
                        v-model="dataForm.feedback"
                        placeholder="反馈信息将发送给举报用户"
                        type="textarea"
                    ></el-input>
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
    media: "",
    content: "",
    uid: "",
    type: "",
    status: "",
    feedback: "",
    linkId: "",
    createTime: "",
    updateTime: "",
});
const dataRule = reactive({
    media: [{ required: true, message: "文件不能为空", trigger: "blur" }],
    content: [{ required: true, message: "描述不能为空", trigger: "blur" }],
    uid: [{ required: true, message: "用户id不能为空", trigger: "blur" }],
    type: [
        {
            required: true,
            message: "类型1帖子 2评论 3用户 4圈子不能为空",
            trigger: "blur",
        },
    ],
    status: [
        {
            required: true,
            message: "状态0待审核 1已处理 2已驳回不能为空",
            trigger: "blur",
        },
    ],
    feedback: [
        { required: true, message: "平台反馈不能为空", trigger: "blur" },
    ],
    linkId: [{ required: true, message: "关联id不能为空", trigger: "blur" }],
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
                        `/admin/report/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.media = data.report.media;
                        dataForm.value.content = data.report.content;
                        dataForm.value.uid = data.report.uid;
                        dataForm.value.type = data.report.type;
                        dataForm.value.status = data.report.status;
                        dataForm.value.feedback = data.report.feedback;
                        dataForm.value.linkId = data.report.linkId;
                        dataForm.value.createTime = data.report.createTime;
                        dataForm.value.updateTime = data.report.updateTime;
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
                        `/admin/report/${
                            !dataForm.value.id ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        media: dataForm.value.media,
                        content: dataForm.value.content,
                        uid: dataForm.value.uid,
                        type: dataForm.value.type,
                        status: dataForm.value.status,
                        feedback: dataForm.value.feedback,
                        linkId: dataForm.value.linkId,
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