<template>
    <div>
        <el-dialog
            :title="!dataForm.jobId ? '新增' : '修改'"
            :close-on-click-modal="false"
            v-model="visible"
            width="35%"
        >
            <el-form
                :model="dataForm"
                :rules="dataRule"
                ref="popForm"
                @keyup.enter="dataFormSubmit()"
                label-width="140px"
            >
                <el-form-item label="bean名称" prop="beanName">
                    <el-input
                        v-model="dataForm.beanName"
                        placeholder="spring bean名称, 如: testTask"
                        style="width: 350px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="参数" prop="params">
                    <el-input
                        v-model="dataForm.params"
                        placeholder="参数"
                        style="width: 350px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="cron表达式" prop="cronExpression">
                    <el-input
                        v-model="dataForm.cronExpression"
                        placeholder="如: 0 0 12 * * ?"
                        style="width: 350px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input
                        v-model="dataForm.remark"
                        placeholder="备注"
                        style="width: 350px"
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
    jobId: 0,
    beanName: "",
    params: "",
    cronExpression: "",
    status: "",
    remark: "",
});
const dataRule = reactive({
    beanName: [
        { required: true, message: "bean名称不能为空", trigger: "blur" },
    ],
    cronExpression: [
        { required: true, message: "cron表达式不能为空", trigger: "blur" },
    ],
});
function init(id) {
    dataForm.value.jobId = id || 0;
    visible.value = true;
    nextTick(() => {
        proxy.$refs.popForm.resetFields();
        if (dataForm.value.jobId) {
            proxy
                .$http({
                    url: proxy.$http.adornUrl(
                        `/sys/schedule/info/${dataForm.value.jobId}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.beanName = data.schedule.beanName;
                        dataForm.value.params = data.schedule.params;
                        dataForm.value.cronExpression =
                            data.schedule.cronExpression;
                        dataForm.value.status = data.schedule.status;
                        dataForm.value.remark = data.schedule.remark;
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
                        `/sys/schedule/${
                            !dataForm.value.jobId ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        jobId: dataForm.value.jobId || undefined,
                        beanName: dataForm.value.beanName,
                        params: dataForm.value.params,
                        cronExpression: dataForm.value.cronExpression,
                        status: dataForm.value.status,
                        remark: dataForm.value.remark,
                        createTime: dataForm.value.createTime,
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