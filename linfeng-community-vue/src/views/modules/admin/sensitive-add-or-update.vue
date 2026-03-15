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
                @keyup.enter="dataFormSubmit()"
                label-width="80px"
            >
                <el-form-item label="敏感词库" prop="sensitiveWord">
                    <el-input
                        v-model="dataForm.sensitiveWord"
                        placeholder="敏感词库"
                        type="textarea"
                    ></el-input>
                    <p class="formInfo">敏感词请用英文逗号","隔开</p>
                </el-form-item>
                <el-form-item label="是否开启" prop="state">
                    <el-radio-group v-model="dataForm.state">
                        <el-radio :label="1">是</el-radio>
                        <el-radio :label="0">否</el-radio>
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
    sensitiveWord: "",
    state: "",
    handleMeasures: "",
});
const dataRule = reactive({
    sensitiveWord: [
        { required: true, message: "敏感词库不能为空", trigger: "blur" },
    ],
    state: [
        {
            required: true,
            message: "是否开启 1-是 0-否不能为空",
            trigger: "blur",
        },
    ],
    handleMeasures: [
        {
            required: true,
            message: "处理措施  1-禁止发布 2-需审核不能为空",
            trigger: "blur",
        },
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
                        `/admin/sensitive/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.sensitiveWord =
                            data.sensitive.sensitiveWord;
                        dataForm.value.state = data.sensitive.state;
                        dataForm.value.handleMeasures =
                            data.sensitive.handleMeasures;
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
                        `/admin/sensitive/${
                            !dataForm.value.id ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        sensitiveWord: dataForm.value.sensitiveWord,
                        state: dataForm.value.state,
                        handleMeasures: dataForm.value.handleMeasures,
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