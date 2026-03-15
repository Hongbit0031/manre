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
                <el-form-item label="用户uid" prop="uid">
                    <el-input
                        v-model="dataForm.uid"
                        placeholder="用户uid"
                    ></el-input>
                </el-form-item>
                <el-form-item label="关联id" prop="linkId">
                    <el-input
                        v-model="dataForm.linkId"
                        placeholder="关联id"
                    ></el-input>
                </el-form-item>
                <el-form-item label="0 = 支出 1 = 获得" prop="pm">
                    <el-input
                        v-model="dataForm.pm"
                        placeholder="0 = 支出 1 = 获得"
                    ></el-input>
                </el-form-item>
                <el-form-item label="账单标题" prop="title">
                    <el-input
                        v-model="dataForm.title"
                        placeholder="账单标题"
                    ></el-input>
                </el-form-item>
                <el-form-item label="明细种类" prop="category">
                    <el-input
                        v-model="dataForm.category"
                        placeholder="明细种类"
                    ></el-input>
                </el-form-item>
                <el-form-item label="明细类型" prop="type">
                    <el-input
                        v-model="dataForm.type"
                        placeholder="明细类型"
                    ></el-input>
                </el-form-item>
                <el-form-item label="明细数字" prop="number">
                    <el-input
                        v-model="dataForm.number"
                        placeholder="明细数字"
                    ></el-input>
                </el-form-item>
                <el-form-item label="剩余" prop="balance">
                    <el-input
                        v-model="dataForm.balance"
                        placeholder="剩余"
                    ></el-input>
                </el-form-item>
                <el-form-item label="备注" prop="mark">
                    <el-input
                        v-model="dataForm.mark"
                        placeholder="备注"
                    ></el-input>
                </el-form-item>
                <el-form-item label="添加时间" prop="addTime">
                    <el-input
                        v-model="dataForm.addTime"
                        placeholder="添加时间"
                    ></el-input>
                </el-form-item>
                <el-form-item label="0待确定 1有效 -1无效" prop="status">
                    <el-input
                        v-model="dataForm.status"
                        placeholder="0待确定 1有效 -1无效"
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
    uid: "",
    linkId: "",
    pm: "",
    title: "",
    category: "",
    type: "",
    number: "",
    balance: "",
    mark: "",
    addTime: "",
    status: "",
});
const dataRule = reactive({
    uid: [{ required: true, message: "用户uid不能为空", trigger: "blur" }],
    linkId: [{ required: true, message: "关联id不能为空", trigger: "blur" }],
    pm: [
        {
            required: true,
            message: "0 = 支出 1 = 获得不能为空",
            trigger: "blur",
        },
    ],
    title: [{ required: true, message: "账单标题不能为空", trigger: "blur" }],
    category: [
        { required: true, message: "明细种类不能为空", trigger: "blur" },
    ],
    type: [{ required: true, message: "明细类型不能为空", trigger: "blur" }],
    number: [{ required: true, message: "明细数字不能为空", trigger: "blur" }],
    balance: [{ required: true, message: "剩余不能为空", trigger: "blur" }],
    mark: [{ required: true, message: "备注不能为空", trigger: "blur" }],
    addTime: [{ required: true, message: "添加时间不能为空", trigger: "blur" }],
    status: [
        {
            required: true,
            message: "0待确定 1有效 -1无效不能为空",
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
                        `/admin/bill/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.uid = data.bill.uid;
                        dataForm.value.linkId = data.bill.linkId;
                        dataForm.value.pm = data.bill.pm;
                        dataForm.value.title = data.bill.title;
                        dataForm.value.category = data.bill.category;
                        dataForm.value.type = data.bill.type;
                        dataForm.value.number = data.bill.number;
                        dataForm.value.balance = data.bill.balance;
                        dataForm.value.mark = data.bill.mark;
                        dataForm.value.addTime = data.bill.addTime;
                        dataForm.value.status = data.bill.status;
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
                        `/admin/bill/${!dataForm.value.id ? "save" : "update"}`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        uid: dataForm.value.uid,
                        linkId: dataForm.value.linkId,
                        pm: dataForm.value.pm,
                        title: dataForm.value.title,
                        category: dataForm.value.category,
                        type: dataForm.value.type,
                        number: dataForm.value.number,
                        balance: dataForm.value.balance,
                        mark: dataForm.value.mark,
                        addTime: dataForm.value.addTime,
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

defineExpose({
    init,
});
</script>