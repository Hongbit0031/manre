<template>
    <div>
        <el-dialog
            :title="!dataForm.id ? '新增' : '修改'"
            :close-on-click-modal="false"
            v-model="visible"
            width="40%"
        >
            <el-form
                :model="dataForm"
                :rules="dataRule"
                ref="popForm"
                @keyup.enter="dataFormSubmit()"
                label-width="80px"
            >
                <el-form-item label="参数名" prop="paramKey">
                    <el-input
                        v-model="dataForm.paramKey"
                        placeholder="参数名"
                        style="width: 400px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="参数值" prop="paramValue">
                    <el-input
                        v-model="dataForm.paramValue"
                        placeholder="参数值"
                        style="width: 400px"
                        type="textarea"
                    ></el-input>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input
                        v-model="dataForm.remark"
                        placeholder="备注"
                        style="width: 400px"
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
    paramKey: "",
    paramValue: "",
    remark: "",
});
const dataRule = {
    paramKey: [
        {
            required: true,
            message: "参数名不能为空",
            trigger: "blur",
        },
    ],
    paramValue: [
        {
            required: true,
            message: "参数值不能为空",
            trigger: "blur",
        },
    ],
};

function init(id) {
    dataForm.value.id = id || 0;
    visible.value = true;
    nextTick(() => {
        proxy.$refs.popForm.resetFields();
        if (dataForm.value.id) {
            proxy
                .$http({
                    url: proxy.$http.adornUrl(
                        `/sys/config/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.paramKey = data.config.paramKey;
                        dataForm.value.paramValue = data.config.paramValue;
                        dataForm.value.remark = data.config.remark;
                    }
                });
        }
    });
}

function dataFormSubmit() {
    proxy.$refs.popForm.validate((valid) => {
        if (valid) {
            proxy
                .$http({
                    url: proxy.$http.adornUrl(
                        `/sys/config/${!dataForm.value.id ? "save" : "update"}`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        paramKey: dataForm.value.paramKey,
                        paramValue: dataForm.value.paramValue,
                        remark: dataForm.value.remark,
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
