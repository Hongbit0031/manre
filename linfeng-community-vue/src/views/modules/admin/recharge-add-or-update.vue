<template>
    <div>
        <el-dialog
            :title="!dataForm.id ? '新增' : '修改'"
            :close-on-click-modal="false"
            v-model="visible"
            width="25%"
        >
            <el-form
                :model="dataForm"
                :rules="dataRule"
                ref="popForm"
                @keyup.enter="dataFormSubmit()"
                label-width="80px"
            >
                <el-form-item label="充值金额" prop="price">
                    <el-input
                        v-model="dataForm.price"
                        placeholder="充值金额"
                        type="number"
                        style="width: 200px"
                        clearable
                    ></el-input>
                </el-form-item>
                <el-form-item label="赠送金额" prop="givePrice">
                    <el-input
                        v-model="dataForm.givePrice"
                        placeholder="赠送金额"
                        type="number"
                        style="width: 200px"
                        clearable
                    ></el-input>
                </el-form-item>
                <el-form-item label="排序" prop="sort">
                    <el-input
                        v-model="dataForm.sort"
                        placeholder="排序"
                        style="width: 200px"
                        type="number"
                    ></el-input>
                </el-form-item>
                <p class="formInfo">注：排序数越大越靠前哦~</p>
                <el-form-item label="状态" prop="status">
                    <!-- <el-input v-model="dataForm.status" placeholder="状态"></el-input> -->
                    <el-radio-group v-model="dataForm.status">
                        <el-radio :label="0">有效</el-radio>
                        <el-radio :label="1">无效</el-radio>
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
    price: "",
    givePrice: "",
    sort: "",
    status: 0,
});
const dataRule = reactive({
    price: [{ required: true, message: "充值金额不能为空", trigger: "blur" }],
    givePrice: [
        { required: true, message: "赠送金额不能为空", trigger: "blur" },
    ],
    sort: [{ required: true, message: "排序不能为空", trigger: "blur" }],
    status: [{ required: true, message: "状态不能为空", trigger: "blur" }],
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
                        `/admin/recharge/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.price = data.recharge.price;
                        dataForm.value.givePrice = data.recharge.givePrice;
                        dataForm.value.sort = data.recharge.sort;
                        dataForm.value.status = data.recharge.status;
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
                        `/admin/recharge/${
                            !dataForm.value.id ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        price: dataForm.value.price,
                        givePrice: dataForm.value.givePrice,
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

defineExpose({
    init,
});
</script>