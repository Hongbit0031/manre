<template>
    <el-dialog title="修改密码" v-model="visible" :append-to-body="true"  width="30%">
        <el-form
            :model="dataForm"
            :rules="dataRule"
            ref="popForm"
            @keyup.enter="dataFormSubmit()"
            label-width="80px"
        >
            <el-form-item label="账号">
                <span>{{ uname }}</span>
            </el-form-item>
            <el-form-item label="原密码" prop="password">
                <el-input
                    type="password"
                    v-model="dataForm.password"
                    style="width: 250px"
                ></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
                <el-input
                    type="password"
                    v-model="dataForm.newPassword"
                    style="width: 250px"
                ></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                    type="password"
                    v-model="dataForm.confirmPassword"
                    style="width: 250px"
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
</template>

<script setup>
import { clearLoginInfo } from "@/utils";
import { ElMessage } from "element-plus";
import { ref, computed, nextTick, getCurrentInstance } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
const store = useStore();
const router = useRouter();
const { proxy } = getCurrentInstance();
// const uname = computed(() => store.getters.uname);
const uname = sessionStorage.getItem("uname");
const visible = ref(false);
const dataForm = ref({
    password: "",
    newPassword: "",
    confirmPassword: "",
});

const dataRule = ref({
    password: [{ required: true, message: "原密码不能为空", trigger: "blur" }],
    newPassword: [
        { required: true, message: "新密码不能为空", trigger: "blur" },
    ],
    confirmPassword: [
        { required: true, message: "确认密码不能为空", trigger: "blur" },
    ],
});

// 初始化
const init = () => {
    visible.value = true;
    nextTick(() => {
        proxy.$refs["popForm"].resetFields();
    });
};

function inits() {
    visible.value = true;
}

// 表单提交
const dataFormSubmit = () => {
    if (dataForm.value.newPassword != dataForm.value.confirmPassword) {
        ElMessage.error("确认密码与新密码不一致");
        return;
    }
    proxy.$refs["popForm"].validate((valid) => {
        if (valid) {
            proxy
                .$http({
                    url: proxy.$http.adornUrl("/sys/user/password"),
                    method: "post",
                    data: proxy.$http.adornData({
                        password: dataForm.value.password,
                        newPassword: dataForm.value.newPassword,
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
                                nextTick(() => {
                                    clearLoginInfo();
                                    router.replace({ name: "Login" });
                                });
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
    inits,
});
</script>
