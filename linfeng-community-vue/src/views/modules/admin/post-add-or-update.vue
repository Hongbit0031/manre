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
                <el-form-item label="浏览量" prop="readCount">
                    <el-input
                        v-model="dataForm.readCount"
                        placeholder="浏览量"
                        style="width:200px"
                        type="number"
                    ></el-input>
                </el-form-item>
                <el-form-item label="平台置顶" prop="postTop">
                    <el-radio-group v-model="dataForm.postTop">
                        <el-radio :label="0">否</el-radio>
                        <el-radio :label="1">是</el-radio>
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
    uid: "",
    topicId: "",
    discussId: "",
    voteId: "",
    title: "",
    content: "",
    media: "",
    readCount: "",
    postTop: "",
    type: "",
    address: "",
    longitude: "",
    latitude: "",
    createTime: "",
    status: "",
    cut: "",
    pay: "",
});
const dataRule = reactive({
    uid: [{ required: true, message: "用户id不能为空", trigger: "blur" }],
    topicId: [{ required: true, message: "圈子id不能为空", trigger: "blur" }],
    discussId: [{ required: true, message: "话题id不能为空", trigger: "blur" }],
    voteId: [{ required: true, message: "投票id不能为空", trigger: "blur" }],
    title: [{ required: true, message: "标题不能为空", trigger: "blur" }],
    content: [{ required: true, message: "内容不能为空", trigger: "blur" }],
    media: [{ required: true, message: "文件不能为空", trigger: "blur" }],
    readCount: [{ required: true, message: "浏览量不能为空", trigger: "blur" }],
    postTop: [{ required: true, message: "置顶不能为空", trigger: "blur" }],
    type: [
        {
            required: true,
            message: "帖子类型：1图文2视频3文章4投票不能为空",
            trigger: "blur",
        },
    ],
    address: [{ required: true, message: "地址名称不能为空", trigger: "blur" }],
    longitude: [{ required: true, message: "经度不能为空", trigger: "blur" }],
    latitude: [{ required: true, message: "纬度不能为空", trigger: "blur" }],
    createTime: [
        { required: true, message: "创建时间不能为空", trigger: "blur" },
    ],
    status: [
        { required: true, message: "状态0正常1审核不能为空", trigger: "blur" },
    ],
    cut: [
        {
            required: true,
            message: "0 普通贴  1 付费贴  2 红包贴不能为空",
            trigger: "blur",
        },
    ],
    pay: [{ required: true, message: "付费贴价格不能为空", trigger: "blur" }],
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
                        `/admin/post/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.uid = data.post.uid;
                        dataForm.value.topicId = data.post.topicId;
                        dataForm.value.discussId = data.post.discussId;
                        dataForm.value.voteId = data.post.voteId;
                        dataForm.value.title = data.post.title;
                        dataForm.value.content = data.post.content;
                        dataForm.value.media = data.post.media;
                        dataForm.value.readCount = data.post.readCount;
                        dataForm.value.postTop = data.post.postTop;
                        dataForm.value.type = data.post.type;
                        dataForm.value.address = data.post.address;
                        dataForm.value.longitude = data.post.longitude;
                        dataForm.value.latitude = data.post.latitude;
                        dataForm.value.createTime = data.post.createTime;
                        dataForm.value.status = data.post.status;
                        dataForm.value.cut = data.post.cut;
                        dataForm.value.pay = data.post.pay;
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
                        `/admin/post/${!dataForm.value.id ? "save" : "update"}`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        uid: dataForm.value.uid,
                        topicId: dataForm.value.topicId,
                        discussId: dataForm.value.discussId,
                        voteId: dataForm.value.voteId,
                        title: dataForm.value.title,
                        content: dataForm.value.content,
                        media: dataForm.value.media,
                        readCount: dataForm.value.readCount,
                        postTop: dataForm.value.postTop,
                        type: dataForm.value.type,
                        address: dataForm.value.address,
                        longitude: dataForm.value.longitude,
                        latitude: dataForm.value.latitude,
                        createTime: dataForm.value.createTime,
                        status: dataForm.value.status,
                        cut: dataForm.value.cut,
                        pay: dataForm.value.pay,
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