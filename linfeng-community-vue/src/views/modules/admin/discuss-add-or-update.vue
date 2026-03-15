<template>
    <div>
        <el-dialog
            :title="!dataForm.id ? '新增' : '修改'"
            :close-on-click-modal="false"
            v-model="visible"
             width="30%"
        >
            <el-form
                :model="dataForm"
                :rules="dataRule"
                ref="popForm"
                @keyup.enter="dataFormSubmit()"
                label-width="80px"
            >
                <el-form-item label="标题" prop="title">
                    <el-input
                        v-model="dataForm.title"
                        placeholder="标题"
                        type="textarea"
                        style="width: 400px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="描述" prop="introduce">
                    <el-input
                        v-model="dataForm.introduce"
                        placeholder="描述"
                        type="textarea"
                        style="width: 400px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="浏览量" prop="readCount">
                    <el-input
                        v-model="dataForm.readCount"
                        type="number"
                        placeholder="浏览量"
                         style="width: 120px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="用户UID" prop="uid" v-if="!dataForm.id">
                    <el-input
                        v-model="dataForm.uid"
                        type="number"
                        placeholder="用户UID"
                         style="width: 120px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="圈子ID" prop="topicId" v-if="!dataForm.id">
                    <el-input
                        v-model="dataForm.topicId"
                        type="number"
                        placeholder="圈子ID"
                         style="width: 120px"
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
    topicId: "",
    title: "",
    introduce: "",
    readCount: "",
    topType: "",
    createTime: "",
});
const dataRule = reactive({
    uid: [{ required: true, message: "用户id不能为空", trigger: "blur" }],
    topicId: [{ required: true, message: "圈子id不能为空", trigger: "blur" }],
    title: [{ required: true, message: "标题不能为空", trigger: "blur" }],
    introduce: [{ required: true, message: "描述不能为空", trigger: "blur" }],
    readCount: [{ required: true, message: "浏览量不能为空", trigger: "blur" }],
    topType: [
        {
            required: true,
            message: "推荐位置0不推荐1首页推荐不能为空",
            trigger: "blur",
        },
    ],
    createTime: [
        { required: true, message: "创建时间不能为空", trigger: "blur" },
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
                        `/admin/discuss/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.uid = data.discuss.uid;
                        dataForm.value.topicId = data.discuss.topicId;
                        dataForm.value.title = data.discuss.title;
                        dataForm.value.introduce = data.discuss.introduce;
                        dataForm.value.readCount = data.discuss.readCount;
                        dataForm.value.topType = data.discuss.topType;
                        dataForm.value.createTime = data.discuss.createTime;
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
                        `/admin/discuss/${
                            !dataForm.value.id ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        uid: dataForm.value.uid,
                        topicId: dataForm.value.topicId,
                        title: dataForm.value.title,
                        introduce: dataForm.value.introduce,
                        readCount: dataForm.value.readCount,
                        topType: dataForm.value.topType,
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