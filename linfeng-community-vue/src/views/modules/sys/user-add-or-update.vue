<template>
    <div>
        <el-dialog
            :title="!dataForm.id ? '新增' : '修改'"
            :close-on-click-modal="false"
            v-model="visible"
            append-to-body
            width="32%"
        >
            <el-form
                :model="dataForm"
                :rules="dataRule"
                ref="popForm"
                label-width="80px"
            >
                <el-form-item label="用户名" prop="username">
                    <el-input
                        v-model="dataForm.username"
                        placeholder="登录帐号"
                        style="width: 250px"
                    ></el-input>
                </el-form-item>
                <el-form-item
                    label="密码"
                    prop="password"
                    :class="{ 'is-required': !dataForm.id }"
                >
                    <el-input
                        v-model="dataForm.password"
                        type="password"
                        placeholder="密码"
                        style="width: 250px"
                    ></el-input>
                </el-form-item>
                <el-form-item
                    label="确认密码"
                    prop="comfirmPassword"
                    :class="{ 'is-required': !dataForm.id }"
                >
                    <el-input
                        v-model="dataForm.comfirmPassword"
                        type="password"
                        placeholder="确认密码"
                        style="width: 250px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input
                        v-model="dataForm.email"
                        placeholder="邮箱"
                        style="width: 250px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="手机号" prop="mobile">
                    <el-input
                        v-model="dataForm.mobile"
                        placeholder="手机号"
                        style="width: 250px"
                    ></el-input>
                </el-form-item>
                <el-form-item label="角色" size="small" prop="roleIdList">
                    <el-checkbox-group v-model="dataForm.roleIdList">
                        <el-checkbox
                            v-for="role in roleList"
                            :key="role.roleId"
                            :label="role.roleId"
                            >{{ role.roleName }}</el-checkbox
                        >
                    </el-checkbox-group>
                </el-form-item>
                <el-form-item label="状态" size="small" prop="status">
                    <el-radio-group v-model="dataForm.status">
                        <el-radio :label="0">禁用</el-radio>
                        <el-radio :label="1">正常</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="dataFormSubmit"
                        >确 定</el-button
                    >
                    <el-button @click="visible = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {
    ref,
    reactive,
    nextTick,
    getCurrentInstance,
    onMounted,
    watch,
} from "vue";
import { isEmail, isMobile } from "@/utils/validate";
import { isAuth } from "@/utils/index";
import http from "@/utils/httpRequest";
import { ElMessage, ElMessageBox } from "element-plus";
const { proxy } = getCurrentInstance();

const validatePassword = (rule, value, callback) => {
    if (!dataForm.id && !/\S/.test(value)) {
        callback(new Error("密码不能为空"));
    } else {
        callback();
    }
};

const validateComfirmPassword = (rule, value, callback) => {
    if (!dataForm.id && !/\S/.test(value)) {
        callback(new Error("确认密码不能为空"));
    } else if (dataForm.password !== value) {
        callback(new Error("确认密码与密码输入不一致"));
    } else {
        callback();
    }
};

const validateEmail = (rule, value, callback) => {
    if (!isEmail(value)) {
        callback(new Error("邮箱格式错误"));
    } else {
        callback();
    }
};

const validateMobile = (rule, value, callback) => {
    if (!isMobile(value)) {
        callback(new Error("手机号格式错误"));
    } else {
        callback();
    }
};

const visible = ref(false);
const roleList = ref([]);
const dataForm = reactive({
    id: 0,
    username: "",
    password: "",
    comfirmPassword: "",
    salt: "",
    email: "",
    mobile: "",
    roleIdList: [],
    status: 1,
});

const dataRule = reactive({
    username: [{ required: true, message: "用户名不能为空", trigger: "blur" }],
    password: [{ validator: validatePassword, trigger: "blur" }],
    comfirmPassword: [{ validator: validateComfirmPassword, trigger: "blur" }],
    email: [
        { required: true, message: "邮箱不能为空", trigger: "blur" },
        { validator: validateEmail, trigger: "blur" },
    ],
    mobile: [
        { required: true, message: "手机号不能为空", trigger: "blur" },
        { validator: validateMobile, trigger: "blur" },
    ],
});

const init = (id) => {
    dataForm.id = id || 0;
    http({
        url: http.adornUrl("/sys/role/select"),
        method: "get",
        params: http.adornParams(),
    })
        .then(({ data }) => {
            roleList.value = data && data.code === 0 ? data.list : [];
        })
        .then(() => {
            visible.value = true;
            nextTick(() => {
                proxy.$refs["popForm"].resetFields();
            });
        })
        .then(() => {
            if (dataForm.id) {
                http({
                    url: http.adornUrl(`/sys/user/info/${dataForm.id}`),
                    method: "get",
                    params: http.adornParams(),
                }).then(({ data }) => {
                    if (data && data.code === 0) {
                        dataForm.username = data.user.username;
                        dataForm.salt = data.user.salt;
                        dataForm.email = data.user.email;
                        dataForm.mobile = data.user.mobile;
                        dataForm.roleIdList = data.user.roleIdList;
                        dataForm.status = data.user.status;
                    }
                });
            }
        });
};

const dataFormSubmit = () => {
    proxy.$refs["popForm"].validate((valid) => {
        if (valid) {
            http({
                url: http.adornUrl(
                    `/sys/user/${!dataForm.id ? "save" : "update"}`
                ),
                method: "post",
                data: http.adornData({
                    userId: dataForm.id || undefined,
                    username: dataForm.username,
                    password: dataForm.password,
                    salt: dataForm.salt,
                    email: dataForm.email,
                    mobile: dataForm.mobile,
                    status: dataForm.status,
                    roleIdList: dataForm.roleIdList,
                }),
            }).then(({ data }) => {
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
};

defineExpose({
    init,
});
</script>