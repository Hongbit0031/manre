<template>
    <div>
        <el-dialog
            :title="!dataForm.id ? '新增' : '修改'"
            :close-on-click-modal="false"
            width="30%"
            v-model="visible"
        >
            <el-form
                :model="dataForm"
                :rules="dataRule"
                ref="popForm"
                @keyup.enter="dataFormSubmit()"
                label-width="80px"
            >
                <el-form-item label="名称" prop="name" style="width: 300px">
                    <el-input
                        v-model="dataForm.name"
                        placeholder="名称"
                    ></el-input>
                </el-form-item>
                <el-form-item
                    label="有效天数"
                    prop="validDays"
                    style="width: 300px"
                >
                    <el-input
                        v-model="dataForm.validDays"
                        placeholder="有效天数"
                        type="number"
                    ></el-input>
                </el-form-item>
                <el-form-item label="价格" prop="price" style="width: 300px">
                    <el-input
                        v-model="dataForm.price"
                        placeholder="价格"
                        type="number"
                    ></el-input>
                </el-form-item>
                <el-form-item label="描述" prop="remark" style="width: 300px">
                    <el-input
                        v-model="dataForm.remark"
                        placeholder="描述"
                    ></el-input>
                </el-form-item>
                <el-form-item label="排序" prop="sort" style="width: 300px">
                    <el-input
                        v-model="dataForm.sort"
                        placeholder="排序"
                        type="number"
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
    name: "",
    validDays: "",
    price: "",
    remark: "",
    sort: "",
});
const dataRule = reactive({
    name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
    validDays: [
        { required: true, message: "有效天数不能为空", trigger: "blur" },
    ],
    price: [{ required: true, message: "价格不能为空", trigger: "blur" }],
    remark: [{ required: true, message: "描述不能为空", trigger: "blur" }],
    sort: [{ required: true, message: "排序不能为空", trigger: "blur" }],
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
                        `/admin/vipoption/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.name = data.vipOption.name;
                        dataForm.value.validDays = data.vipOption.validDays;
                        dataForm.value.price = data.vipOption.price;
                        dataForm.value.remark = data.vipOption.remark;
                        dataForm.value.sort = data.vipOption.sort;
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
                        `/admin/vipoption/${
                            !dataForm.value.id ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        name: dataForm.value.name,
                        validDays: dataForm.value.validDays,
                        price: dataForm.value.price,
                        remark: dataForm.value.remark,
                        sort: dataForm.value.sort,
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