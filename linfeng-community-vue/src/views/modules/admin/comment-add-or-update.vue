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
                <el-form-item label="评论内容" prop="content">
                    <el-input
                        v-model="dataForm.content"
                        placeholder="评论内容"
                        disabled
                        type="textarea"
                        style="width: 400px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="评论时间" prop="createTime">
                    <el-input
                        v-model="dataForm.createTime"
                        placeholder="创建时间"
                        disabled
                         style="width: 200px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="评论状态" prop="status">
                    <el-radio-group v-model="dataForm.status">
                        <el-radio :label="1">上架</el-radio>
                        <el-radio :label="0">下架</el-radio>
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
    pid: "",
    type: "",
    uid: "",
    toUid: "",
    postId: "",
    content: "",
    status: "",
    createTime: "",
});
const dataRule = reactive({
    pid: [{ required: true, message: "父级id不能为空", trigger: "blur" }],
   
    uid: [{ required: true, message: "评论作者ID不能为空", trigger: "blur" }],
    toUid: [
        { required: true, message: "被回复用户ID不能为空", trigger: "blur" },
    ],
    postId: [
        { required: true, message: "评论帖子ID不能为空", trigger: "blur" },
    ],
    content: [{ required: true, message: "评论内容不能为空", trigger: "blur" }],
    status: [{ required: true, message: "评论状态不能为空", trigger: "blur" }],
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
                        `/admin/comment/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.pid = data.comment.pid;
                        dataForm.value.type = data.comment.type;
                        dataForm.value.uid = data.comment.uid;
                        dataForm.value.toUid = data.comment.toUid;
                        dataForm.value.postId = data.comment.postId;
                        dataForm.value.content = data.comment.content;
                        dataForm.value.status = data.comment.status;
                        dataForm.value.createTime = data.comment.createTime;
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
                        `/admin/comment/${
                            !dataForm.value.id ? "save" : "update"
                        }`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        pid: dataForm.value.pid,
                        type: dataForm.value.type,
                        uid: dataForm.value.uid,
                        toUid: dataForm.value.toUid,
                        postId: dataForm.value.postId,
                        content: dataForm.value.content,
                        status: dataForm.value.status,
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