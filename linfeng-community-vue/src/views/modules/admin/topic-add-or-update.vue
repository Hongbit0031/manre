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
                <el-form-item label="圈子名称" prop="topicName">
                    <el-input
                        v-model="dataForm.topicName"
                        placeholder="圈子名称"
                        style="width: 200px"
                    ></el-input>
                </el-form-item>
                <!-- 选择框 -->
                <el-form-item label="圈子分类" prop="cateId">
                    <el-select
                        v-model="dataForm.cateId"
                        clearable
                        placeholder="请选择圈子分类"
                        @change="changeCate"
                    >
                        <el-option
                            v-for="item in optionsCateList"
                            :key="item.cateId"
                            :label="item.cateName"
                            :value="item.cateId"
                        >
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input
                        v-model="dataForm.description"
                        placeholder="描述"
                        type="textarea"
                        style="width: 350px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="背景图" prop="bgImage">
                    <el-upload
                        class="avatar-uploader"
                        :action="url"
                        :show-file-list="false"
                        :on-success="handleBgImageSuccess"
                    >
                        <img
                            v-if="dataForm.bgImage"
                            :src="dataForm.bgImage"
                            class="avatar"
                        />
                        <el-icon v-else class="avatar-uploader-icon"
                            ><Plus
                        /></el-icon>
                    </el-upload>
                    <p class="formInfo">
                        建议尺寸：400*200像素，jpg、png图片类型
                    </p>
                </el-form-item>

                <el-form-item label="圈子头像" prop="coverImage">
                    <el-upload
                        class="avatar-uploader"
                        :action="url"
                        :show-file-list="false"
                        :on-success="handleLogoSuccess"
                    >
                        <img
                            v-if="dataForm.coverImage"
                            :src="dataForm.coverImage"
                            class="avatar"
                        />
                        <el-icon v-else class="avatar-uploader-icon"
                            ><Plus
                        /></el-icon>
                    </el-upload>
                    <p class="formInfo">
                        建议尺寸：100*100像素，jpg、png图片类型
                    </p>
                </el-form-item>
                <el-form-item label="圈子状态" prop="status">
                    <el-radio-group v-model="dataForm.status">
                        <el-radio :label="0">正常</el-radio>
                        <el-radio :label="1">禁用</el-radio>
                        <el-radio :label="2">审核中</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="加入人数" prop="userNum">
                    <el-input
                        v-model="dataForm.userNum"
                        placeholder="加入人数"
                        style="width: 150px"
                        type="number"
                    ></el-input>
                </el-form-item>
                <el-form-item label="私密类型" prop="isPrivacy">
                    <el-radio-group v-model="dataForm.isPrivacy">
                        <el-radio :label="0">公开圈子</el-radio>
                        <el-radio :label="1">私密圈子</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="进圈限制" prop="rest">
                    <el-radio-group v-model="dataForm.rest">
                        <el-radio :label="0">不限制</el-radio>
                        <el-radio :label="1">限制</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item
                    label="进圈问答"
                    prop="question"
                    v-if="dataForm.rest == 1"
                >
                    <el-input
                        v-model="dataForm.question"
                        placeholder="进圈问答"
                        type="textarea"
                        style="width: 350px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="圈主UID" prop="topicName">
                    <el-input
                        v-model="dataForm.uid"
                        placeholder="圈主用户ID"
                        style="width: 150px"
                        type="number"
                    ></el-input>
                    <p class="formInfo">用户ID在会员管理查看</p>
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
const url = ref("");
const dataForm = ref({
    id: 0,
    uid: "",
    cateId: "",
    topicName: "",
    description: "",
    coverImage: "",
    bgImage: "",
    topType: "",
    status: 0,
    indexRecommend: "",
    userNum: "",
    createTime: "",
    rest: 0,
    question: "",
    isPrivacy: 0,
});
const optionsCateList = ref([]);

const dataRule = reactive({
    uid: [{ required: true, message: "创建用户id不能为空", trigger: "blur" }],
    cateId: [{ required: true, message: "分类id不能为空", trigger: "blur" }],
    topicName: [
        { required: true, message: "圈子名称不能为空", trigger: "blur" },
    ],
    description: [{ required: true, message: "描述不能为空", trigger: "blur" }],
    coverImage: [{ required: true, message: "logo不能为空", trigger: "blur" }],
    bgImage: [{ required: true, message: "背景图不能为空", trigger: "blur" }],
    topType: [
        {
            required: true,
            message: "推荐类型：0 不推荐1首页推荐2圈子页推荐不能为空",
            trigger: "blur",
        },
    ],
    status: [
        {
            required: true,
            message: "圈子状态：0 正常 ，1禁用不能为空",
            trigger: "blur",
        },
    ],
    indexRecommend: [
        {
            required: true,
            message: "是否首页推荐圈子内容不能为空",
            trigger: "blur",
        },
    ],
    userNum: [{ required: true, message: "加入人数不能为空", trigger: "blur" }],
    createTime: [
        { required: true, message: "创建时间不能为空", trigger: "blur" },
    ],
    rest: [
        {
            required: true,
            message: "进圈条件不能为空",
            trigger: "blur",
        },
    ],
    question: [
        { required: true, message: "问题内容设置不能为空", trigger: "blur" },
    ],
    isPrivacy: [
        { required: true, message: "私密类型不能为空", trigger: "blur" },
    ]
});
function init(id) {
    getCateList();
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
                        `/admin/topic/info/${dataForm.value.id}`
                    ),
                    method: "get",
                    params: proxy.$http.adornParams(),
                })
                .then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.value.uid = data.topic.uid;
                        dataForm.value.cateId = data.topic.cateId;
                        dataForm.value.topicName = data.topic.topicName;
                        dataForm.value.description = data.topic.description;
                        dataForm.value.coverImage = data.topic.coverImage;
                        dataForm.value.bgImage = data.topic.bgImage;
                        dataForm.value.topType = data.topic.topType;
                        dataForm.value.status = data.topic.status;
                        dataForm.value.indexRecommend =
                            data.topic.indexRecommend;
                        dataForm.value.userNum = data.topic.userNum;
                        dataForm.value.createTime = data.topic.createTime;
                        dataForm.value.rest = data.topic.rest;
                        dataForm.value.question = data.topic.question;
                        dataForm.value.isPrivacy = data.topic.isPrivacy;
                    }
                });
        }
    });
}

// 表单提交
function dataFormSubmit() {
    if (dataForm.rest == 1 && !dataForm.question) {
        ElMessage({
            message: "进圈问答未填写",
            duration: 2000,
        });
        return;
    }
    proxy.$refs["popForm"].validate((valid) => {
        if (valid) {
            proxy
                .$http({
                    url: proxy.$http.adornUrl(
                        `/admin/topic/${!dataForm.value.id ? "save" : "update"}`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        id: dataForm.value.id || undefined,
                        uid: dataForm.value.uid,
                        cateId: dataForm.value.cateId,
                        topicName: dataForm.value.topicName,
                        description: dataForm.value.description,
                        coverImage: dataForm.value.coverImage,
                        bgImage: dataForm.value.bgImage,
                        topType: dataForm.value.topType,
                        status: dataForm.value.status,
                        indexRecommend: dataForm.value.indexRecommend,
                        userNum: dataForm.value.userNum,
                        createTime: dataForm.value.createTime,
                        rest: dataForm.value.rest,
                        question: dataForm.value.question,
                        isPrivacy: dataForm.value.isPrivacy,
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
function getCateList() {
    proxy
        .$http({
            url: proxy.$http.adornUrl(`/admin/category/getList`),
            method: "get",
        })
        .then(({ data }) => {
            if (data && data.code === 0) {
                optionsCateList.value = data.result;
            }
        });
}

function changeCate(value) {
    dataForm.cateId = value;
}
function handleBgImageSuccess(res) {
if (res && res.code === 0) {
        // 上传成功
        dataForm.value.bgImage = res.url;
        proxy.$forceUpdate();
    } else {
        // 上传失败，显示后端返回的错误信息
        const errorMessage = res && res.msg ? res.msg : '背景图上传失败';
        ElMessage.error(errorMessage);
    }
}
function handleLogoSuccess(response) {
    if (response && response.code === 0) {
        // 上传成功
        dataForm.value.coverImage = response.url;
        proxy.$forceUpdate();
    } else {
        // 上传失败，显示后端返回的错误信息
        const errorMessage = response && response.msg ? response.msg : '圈子头像上传失败';
        ElMessage.error(errorMessage);
    }
}
defineExpose({
    init,
});
</script>