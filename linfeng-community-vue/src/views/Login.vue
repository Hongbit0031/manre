<template>
    <div class="site-wrapper site-page--login">
        <div class="login-wrapper">
            <div class="box">
                <div class="left">
                    <img
                        class="login_bk"
                        src="@/assets/img/login_bk.png"
                        alt=""
                    />
                </div>
                <div class="right">
                    <div class="right-wrapper">
                        <h3 class="title">慢热 V3.1.0 后台管理系统</h3>
                        <el-form
                            :model="dataForm"
                            :rules="dataRule"
                            ref="dataFormRef"
                            @keyup.enter="dataFormSubmit()"
                            status-icon
                        >
                            <el-form-item prop="userName">
                                <el-input
                                    v-model="dataForm.userName"
                                    placeholder="帐号"
                                ></el-input>
                            </el-form-item>
                            <el-form-item prop="password">
                                <el-input
                                    v-model="dataForm.password"
                                    type="password"
                                    placeholder="密码"
                                ></el-input>
                            </el-form-item>
                            <el-form-item prop="captcha">
                                <el-row :gutter="20">
                                    <el-col :span="14">
                                        <el-input
                                            v-model="dataForm.captcha"
                                            placeholder="验证码"
                                        ></el-input>
                                    </el-col>
                                    <el-col :span="10" class="login-captcha">
                                        <img
                                            :src="captchaPath"
                                            @click="getCaptcha()"
                                            alt=""
                                        />
                                    </el-col>
                                </el-row>
                            </el-form-item>
                            <el-form-item>
                                <el-button
                                    class="login-btn-submit"
                                    type="primary"
                                    @click="dataFormSubmit()"
                                    >登录</el-button
                                >
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
            </div>
        </div>

        <!-- 底部 -->
        <div class="login-footer" @click="goBeian()">
            © 2022-2026 慢热 All Rights Reserved
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from "vue";
import { getUUID } from "@/utils";
import http from "@/utils/httpRequest";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { SET_UNAME } from "@/store/modules/app/type";

const { proxy } = getCurrentInstance();
const router = useRouter();
const captchaPath = ref("");

const dataForm = ref({
    userName: "",
    password: "",
    uuid: "",
    captcha: "",
});

const dataRule = ref({
    userName: [
        {
            required: true,
            message: "帐号不能为空",
            trigger: "blur",
        },
    ],
    password: [
        {
            required: true,
            message: "密码不能为空",
            trigger: "blur",
        },
    ],
    captcha: [
        {
            required: true,
            message: "验证码不能为空",
            trigger: "blur",
        },
    ],
});

onMounted(() => {
    getCaptcha();
    // getRandomUsername();
});

// 该方法仅适用于linfeng-community演示站点获取随机用户名，非演示环境请勿使用
function getRandomUsername() {
    const list = ["test", "test2", "test3", "test4"];
    const randomElement = list[Math.floor(Math.random() * list.length)];
    dataForm.value.userName = randomElement;
}

// 获取验证码
function getCaptcha() {
    dataForm.value.uuid = getUUID();
    captchaPath.value = http.adornUrl(
        `/captcha.jpg?uuid=${dataForm.value.uuid}`
    );
}
// 提交表单
function dataFormSubmit() {
    proxy.$refs["dataFormRef"].validate((valid) => {
        if (valid) {
            http({
                url: http.adornUrl("/sys/login"),
                method: "post",
                data: http.adornData({
                    username: dataForm.value.userName,
                    password: dataForm.value.password,
                    uuid: dataForm.value.uuid,
                    captcha: dataForm.value.captcha,
                }),
            }).then(({ data }) => {
                if (data && data.code === 0) {
                    sessionStorage.setItem("token", data.token);
                    getAdminUserInfo();
                    // router.push({ name: "AppMain" });
                } else {
                    ElMessage.error(data.msg);
                    getCaptcha();
                }
            });
        }
    });
}

function getAdminUserInfo() {
    proxy
        .$http({
            url: proxy.$http.adornUrl("/sys/user/info"),
            method: "get",
            params: proxy.$http.adornParams(),
        })
        .then(({ data }) => {
            if (data && data.code === 0) {
                sessionStorage.setItem("uname", data.user.username);
                router.push({ name: "AppMain" });
            }
        });
}

function goBeian() {
    window.open("https://beian.miit.gov.cn/");
}
</script>

<style lang="scss">
.login-wrapper {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    .box {
        display: flex;
        width: 1050px;
        height: 510px;
        background-color: #f5f7fa;
        border-radius: 10px;
        overflow: hidden;
        box-shadow: 0 0 10px 2px rgba($color: #000000, $alpha: 0.15);

        .left {
            width: 40%;
            height: 100%;
            display: flex;
            justify-content: center;

            .login_bk {
                height: 100%;
            }
        }

        .right {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 60%;
            background-color: #fff;

            .title {
                font-size: 24px;
                font-weight: bold;
                margin-bottom: 100px;
            }
        }
    }
}
.site-wrapper.site-page--login {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    background-color: #f4f5f6;
    background-image: url("/src/assets/img/bg.jpeg");
    overflow: hidden;
    .site-content__wrapper {
        position: absolute;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        padding: 0;
        margin: 0;
        overflow-x: hidden;
        overflow-y: auto;
        background-color: transparent;
    }

    .site-content {
        min-height: 100%;
        padding: 30px 500px 30px 30px;
    }

    .brand-info {
        margin: 220px 100px 0 90px;
        color: #fff;
    }

    .brand-info__text {
        margin: 0 0 22px 0;
        font-size: 48px;
        font-weight: 400;
        text-transform: uppercase;
    }

    .brand-info__intro {
        margin: 10px 0;
        font-size: 16px;
        line-height: 1.58;
        opacity: 0.6;
    }

    .login-main {
        position: absolute;
        top: 0;
        right: 0;
        padding: 150px 60px 180px;
        width: 470px;
        min-height: 100%;
        background-color: #fff;
    }

    .login-title {
        font-size: 16px;
    }

    .login-captcha {
        overflow: hidden;

        > img {
            width: 100%;
            cursor: pointer;
        }
    }

    .login-btn-submit {
        width: 100%;
        margin-top: 38px;
    }

    .login-footer {
        color: #ffffff;
        font-size: 12px;
        position: fixed;
        bottom: 20px;
        width: 100%;
        text-align: center;
    }
}
</style>
