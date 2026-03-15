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
                <el-form-item label="用户ID" prop="userId">
                    <el-input
                        v-model="dataForm.userId"
                        placeholder="用户ID"
                    ></el-input>
                </el-form-item>
                <el-form-item label="奖品ID" prop="prizeId">
                    <el-input
                        v-model="dataForm.prizeId"
                        placeholder="奖品ID"
                    ></el-input>
                </el-form-item>
                <el-form-item label="奖品类型" prop="prizeType">
                    <el-input
                        v-model="dataForm.prizeType"
                        placeholder="奖品类型"
                    ></el-input>
                </el-form-item>
                <el-form-item label="奖品名称" prop="prizeName">
                    <el-input
                        v-model="dataForm.prizeName"
                        placeholder="奖品名称"
                    ></el-input>
                </el-form-item>
                <el-form-item label="奖品图片" prop="prizeImage">
                    <el-input
                        v-model="dataForm.prizeImage"
                        placeholder="奖品图片"
                    ></el-input>
                </el-form-item>
                <el-form-item label="获得数量" prop="number">
                    <el-input
                        v-model="dataForm.number"
                        placeholder="获得数量"
                    ></el-input>
                </el-form-item>
                <el-form-item label="抽奖时间" prop="createTime">
                    <el-input
                        v-model="dataForm.createTime"
                        placeholder="抽奖时间"
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
    userId: "",
    prizeId: "",
    prizeType: "",
    prizeName: "",
    prizeImage: "",
    number: "",
    createTime: "",
});
const dataRule = reactive({
    userId: [{ required: true, message: "用户ID不能为空", trigger: "blur" }],
    prizeId: [{ required: true, message: "奖品ID不能为空", trigger: "blur" }],
    prizeType: [
        { required: true, message: "奖品类型不能为空", trigger: "blur" },
    ],
    prizeName: [
        { required: true, message: "奖品名称不能为空", trigger: "blur" },
    ],
    prizeImage: [
        { required: true, message: "奖品图片不能为空", trigger: "blur" },
    ],
    number: [{ required: true, message: "获得数量不能为空", trigger: "blur" }],
    createTime: [
        { required: true, message: "抽奖时间不能为空", trigger: "blur" },
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
                        `/admin/luckdrawrecord/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.userId = data.luckdrawRecord.userId;
                        dataForm.value.prizeId = data.luckdrawRecord.prizeId;
                        dataForm.value.prizeType =
                            data.luckdrawRecord.prizeType;
                        dataForm.value.prizeName =
                            data.luckdrawRecord.prizeName;
                        dataForm.value.prizeImage =
                            data.luckdrawRecord.prizeImage;
                        dataForm.value.number = data.luckdrawRecord.number;
                        dataForm.value.createTime =
                            data.luckdrawRecord.createTime;
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
                        `/admin/luckdrawrecord/${
                            !dataForm.value.id ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        userId: dataForm.value.userId,
                        prizeId: dataForm.value.prizeId,
                        prizeType: dataForm.value.prizeType,
                        prizeName: dataForm.value.prizeName,
                        prizeImage: dataForm.value.prizeImage,
                        number: dataForm.value.number,
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