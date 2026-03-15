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
                <el-form-item label="名称" prop="name">
                    <el-input
                        v-model="dataForm.name"
                        placeholder="名称"
                        style="width: 250px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="奖品类型" prop="prizeType">
                    <el-radio-group v-model="dataForm.prizeType">
                        <el-radio :label="1">积分</el-radio>
                        <el-radio :label="2">谢谢惠顾</el-radio>
                        <el-radio :label="3">红包</el-radio>
                        <el-radio :label="4">自定义奖品</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="图片" prop="image">
                    <el-upload
                        class="avatar-uploader"
                        :action="url"
                        :show-file-list="false"
                        :on-success="handleIconSuccess"
                    >
                        <img
                            v-if="dataForm.image"
                            :src="dataForm.image"
                            class="avatar"
                        />
                        <el-icon v-else class="avatar-uploader-icon"
                            ><Plus
                        /></el-icon>
                    </el-upload>
                </el-form-item>
                <p class="formInfo">建议尺寸：200*200像素，jpg、png图片类型</p>
                <el-form-item label="奖品数量" prop="number">
                    <el-input
                        v-model="dataForm.number"
                        placeholder="奖品数量"
                         style="width: 250px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="抽奖几率" prop="probability">
                    <el-input
                        v-model="dataForm.probability"
                        placeholder="抽奖几率"
                         style="width: 250px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="排序" prop="sort">
                    <el-input
                        v-model="dataForm.sort"
                        placeholder="排序"
                         style="width: 250px"
                    ></el-input>
                </el-form-item>

                <el-form-item label="抽奖状态" prop="status">
                    <el-radio-group v-model="dataForm.status">
                        <el-radio :label="1">正常</el-radio>
                        <el-radio :label="0">禁用</el-radio>
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
    prizeType: "",
    name: "",
    image: "",
    number: "",
    probability: "",
    sort: "",
    status: "",
    createTime: "",
    updateTime: "",
});
const url = ref("");
const dataRule = reactive({
    prizeType: [
        {
            required: true,
            message: "奖品类型不能为空",
            trigger: "blur",
        },
    ],
    name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
    image: [{ required: true, message: "图片不能为空", trigger: "blur" }],
    number: [{ required: true, message: "奖品数量不能为空", trigger: "blur" }],
    probability: [
        { required: true, message: "抽奖几率不能为空", trigger: "blur" },
    ],
    sort: [{ required: true, message: "排序不能为空", trigger: "blur" }],
    status: [
        {
            required: true,
            message: "抽奖状态不能为空",
            trigger: "blur",
        },
    ],
    createTime: [
        { required: true, message: "创建时间不能为空", trigger: "blur" },
    ],
    updateTime: [
        { required: true, message: "更新时间不能为空", trigger: "blur" },
    ],
});
function init(id) {
    dataForm.value.id = id || 0;
    url.value = proxy.$http.adornUrl(
        `/sys/oss/upload?token=${sessionStorage.getItem("token")}`
    );
    visible.value = true;
    nextTick(() => {
        proxy.$refs.popForm.resetFields();
        if (dataForm.value.id) {
            proxy
                .$http({
                    url: proxy.$http.adornUrl(
                        `/admin/luckdraw/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.prizeType = data.luckdraw.prizeType;
                        dataForm.value.name = data.luckdraw.name;
                        dataForm.value.image = data.luckdraw.image;
                        dataForm.value.number = data.luckdraw.number;
                        dataForm.value.probability = data.luckdraw.probability;
                        dataForm.value.sort = data.luckdraw.sort;
                        dataForm.value.status = data.luckdraw.status;
                        dataForm.value.createTime = data.luckdraw.createTime;
                        dataForm.value.updateTime = data.luckdraw.updateTime;
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
                        `/admin/luckdraw/${
                            !dataForm.value.id ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        prizeType: dataForm.value.prizeType,
                        name: dataForm.value.name,
                        image: dataForm.value.image,
                        number: dataForm.value.number,
                        probability: dataForm.value.probability,
                        sort: dataForm.value.sort,
                        status: dataForm.value.status,
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

function handleIconSuccess(response) {
    dataForm.value.image = response.url;
    proxy.$forceUpdate();
}

defineExpose({
    init,
});
</script>