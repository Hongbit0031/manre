<template>
  <div>
    <el-dialog
      :title="!dataForm.uid ? '新增' : '修改'"
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
        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            :action="url"
            :show-file-list="false"
            :on-success="handleIconSuccess"
          >
            <img v-if="dataForm.avatar" :src="dataForm.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="昵称" prop="username">
          <el-input
            v-model="dataForm.username"
            placeholder="用户昵称"
            style="width: 200px"
          ></el-input>
        </el-form-item>
        <el-form-item label="签名" prop="intro">
          <el-input
            v-model="dataForm.intro"
            placeholder="个性签名"
            style="width: 200px"
          ></el-input>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">封号</el-radio>
            <el-radio :label="2">禁言</el-radio>
            <el-radio :label="3">注销</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="dataForm.type">
            <el-radio :label="0">普通用户</el-radio>
            <el-radio :label="1">官方账号</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="账户余额" prop="changeMoney">
          <el-radio-group v-model="changeMoney">
            <el-radio :label="1">不修改</el-radio>
            <el-radio :label="0">修改</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="账户余额"
          prop="money"
          v-if="changeMoney == 0"
          style="width: 300px"
        >
          <el-input v-model="dataForm.money" disabled></el-input>
        </el-form-item>
        <el-form-item label="余额增减" prop="upOrDown" v-if="changeMoney == 0">
          <el-radio-group v-model="upOrDown">
            <el-radio :label="0">增加</el-radio>
            <el-radio :label="1">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="增减额度"
          prop="money"
          v-if="changeMoney == 0"
          style="width: 300px"
        >
          <el-input
            v-model="changeValue"
            placeholder="增减余额"
            type="number"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="账户积分" prop="changeIntegral">
          <el-radio-group v-model="changeIntegral">
            <el-radio :label="1">不修改</el-radio>
            <el-radio :label="0">修改</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="账户积分"
          prop="money"
          v-if="changeIntegral == 0"
          style="width: 300px"
        >
          <el-input v-model="dataForm.integral" disabled></el-input>
        </el-form-item>
        <el-form-item
          label="积分增减"
          prop="upOrDownIntegral"
          v-if="changeIntegral == 0"
        >
          <el-radio-group v-model="upOrDownIntegral">
            <el-radio :label="0">增加</el-radio>
            <el-radio :label="1">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="增减额度"
          prop="money"
          v-if="changeIntegral == 0"
          style="width: 300px"
        >
          <el-input
            v-model="changeIntegralValue"
            placeholder="增减积分(必须为整数)"
            type="number"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="会员设置" prop="changeVip">
          <el-radio-group v-model="changeVip">
            <el-radio :label="1">不修改</el-radio>
            <el-radio :label="0">修改</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="会员状态" prop="vip" v-if="changeVip == 0">
          <el-radio-group v-model="dataForm.vip">
            <el-radio :label="0">普通用户</el-radio>
            <el-radio :label="1">会员用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          label="有效天数"
          prop="money"
          v-if="dataForm.vip == 1 && changeVip == 0"
          style="width: 300px"
        >
          <el-input
            v-model="vipValidDay"
            placeholder="会员有效天数(必须为整数)"
            type="number"
            clearable
          ></el-input>
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
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
  uid: 0,
  email: "",
  mobile: "",
  username: "",
  password: "",
  groupId: "",
  avatar: "",
  gender: "",
  province: "",
  city: "",
  openid: "",
  mpOpenid: "",
  unionid: "",
  status: "",
  intro: "",
  money: "",
  signNum: "",
  integral: "",
  lastLoginIp: "",
  tagStr: "",
  vip: "",
  vipExpireTime: "",
  type: "",
  updateTime: "",
  createTime: "",
  level: "",
});

const changeMoney = ref(1); //余额不修改1，修改0
const upOrDown = ref(0); //余额 增加0 减少1
const changeValue = ref(""); //余额增减数量
const changeIntegral = ref(1); //积分不修改1，修改0
const upOrDownIntegral = ref(0); //积分 增加0 减少1
const changeIntegralValue = ref(""); //积分增减数量
const vipValidDay = ref(0); //会员有效天数
const changeVip = ref(1); //会员状态不修改1，修改0
const url = ref("");
const dataRule = reactive({
  email: [{ required: true, message: "邮箱不能为空", trigger: "blur" }],
  mobile: [{ required: true, message: "手机号不能为空", trigger: "blur" }],
  username: [{ required: true, message: "用户名不能为空", trigger: "blur" }],
  password: [{ required: true, message: "密码不能为空", trigger: "blur" }],
  groupId: [{ required: true, message: "用户组不能为空", trigger: "blur" }],
  avatar: [{ required: true, message: "头像不能为空", trigger: "blur" }],
  gender: [
    {
      required: true,
      message: "性别(0未知，1男，2女)不能为空",
      trigger: "blur",
    },
  ],
  province: [{ required: true, message: "省份不能为空", trigger: "blur" }],
  city: [{ required: true, message: "城市不能为空", trigger: "blur" }],
  openid: [
    { required: true, message: "小程序openid不能为空", trigger: "blur" },
  ],
  mpOpenid: [
    { required: true, message: "公众号openid不能为空", trigger: "blur" },
  ],
  unionid: [{ required: true, message: "unionid不能为空", trigger: "blur" }],
  status: [{ required: true, message: "状态不能为空", trigger: "blur" }],
  intro: [{ required: true, message: "个性签名不能为空", trigger: "blur" }],
  money: [{ required: true, message: "用户余额不能为空", trigger: "blur" }],
  signNum: [
    { required: true, message: "连续签到天数不能为空", trigger: "blur" },
  ],
  integral: [{ required: true, message: "积分不能为空", trigger: "blur" }],
  lastLoginIp: [
    { required: true, message: "最后登录ip不能为空", trigger: "blur" },
  ],
  tagStr: [{ required: true, message: "用户标签不能为空", trigger: "blur" }],
  vip: [{ required: true, message: "0普通用户1会员不能为空", trigger: "blur" }],
  vipExpireTime: [
    { required: true, message: "会员过期时间不能为空", trigger: "blur" },
  ],
  type: [
    {
      required: true,
      message: "0为普通用户，1官方账号2马甲虚拟用户不能为空",
      trigger: "blur",
    },
  ],
  updateTime: [
    { required: true, message: "更新时间不能为空", trigger: "blur" },
  ],
  createTime: [
    { required: true, message: "创建时间不能为空", trigger: "blur" },
  ],
  level: [{ required: true, message: "用户经验等级不能为空", trigger: "blur" }],
});

function init(id) {
  dataForm.value.uid = id || 0;
  url.value = proxy.$http.adornUrl(
    `/sys/oss/upload?token=${sessionStorage.getItem("token")}`
  );
  visible.value = true;
  nextTick(() => {
    proxy.$refs.popForm.resetFields();
    if (dataForm.value.uid) {
      proxy
        .$http({
          url: proxy.$http.adornUrl(`/admin/user/info/${dataForm.value.uid}`),
          method: "get",
          params: proxy.$http.adornParams(),
        })
        .then(({ data }) => {
          if (data && data.code === 0) {
            dataForm.value.email = data.user.email;
            dataForm.value.mobile = data.user.mobile;
            dataForm.value.username = data.user.username;
            dataForm.value.password = data.user.password;
            dataForm.value.groupId = data.user.groupId;
            dataForm.value.avatar = data.user.avatar;
            dataForm.value.gender = data.user.gender;
            dataForm.value.province = data.user.province;
            dataForm.value.city = data.user.city;
            dataForm.value.openid = data.user.openid;
            dataForm.value.mpOpenid = data.user.mpOpenid;
            dataForm.value.unionid = data.user.unionid;
            dataForm.value.status = data.user.status;
            dataForm.value.intro = data.user.intro;
            dataForm.value.money = data.user.money;
            dataForm.value.signNum = data.user.signNum;
            dataForm.value.integral = data.user.integral;
            dataForm.value.lastLoginIp = data.user.lastLoginIp;
            dataForm.value.tagStr = data.user.tagStr;
            dataForm.value.vip = data.user.vip;
            dataForm.value.vipExpireTime = data.user.vipExpireTime;
            dataForm.value.type = data.user.type;
            dataForm.value.updateTime = data.user.updateTime;
            dataForm.value.createTime = data.user.createTime;
            dataForm.value.level = data.user.level;
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
            `/admin/user/${!dataForm.value.uid ? "save" : "update"}`
          ),
          method: "post",
          data: proxy.$http.adornData({
            uid: dataForm.value.uid || undefined,
            email: dataForm.value.email,
            mobile: dataForm.value.mobile,
            username: dataForm.value.username,
            password: dataForm.value.password,
            groupId: dataForm.value.groupId,
            avatar: dataForm.value.avatar,
            gender: dataForm.value.gender,
            province: dataForm.value.province,
            city: dataForm.value.city,
            openid: dataForm.value.openid,
            mpOpenid: dataForm.value.mpOpenid,
            unionid: dataForm.value.unionid,
            status: dataForm.value.status,
            intro: dataForm.value.intro,
            money: dataForm.value.money,
            signNum: dataForm.value.signNum,
            integral: dataForm.value.integral,
            lastLoginIp: dataForm.value.lastLoginIp,
            tagStr: dataForm.value.tagStr,
            vip: dataForm.value.vip,
            vipExpireTime: dataForm.value.vipExpireTime,
            type: dataForm.value.type,
            updateTime: dataForm.value.updateTime,
            createTime: dataForm.value.createTime,
            level: dataForm.value.level,

            changeValue: changeValue.value,
            upOrDown: upOrDown.value,
            changeMoney: changeMoney.value,
            changeIntegralValue: changeIntegralValue.value,
            upOrDownIntegral: upOrDownIntegral.value,
            changeIntegral: changeIntegral.value,
            vipValidDay: vipValidDay.value,
            changeVip: changeVip.value,
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
  // 检查响应的code字段，判断是否真正成功
  if (response && response.code === 0) {
    // 上传成功
    dataForm.value.avatar = response.url;
    proxy.$forceUpdate();
  } else {
    // 上传失败，显示后端返回的错误信息
    const errorMessage = response && response.msg ? response.msg : '头像上传失败';
    ElMessage.error(errorMessage);
  }
}

function reset() {
  changeMoney.value = 1;
  upOrDown.value = 0;
  changeValue.value = 0;
  changeIntegral.value = 1;
  upOrDownIntegral.value = 0;
  changeIntegralValue.value = 0;
  vipValidDay.value = 0;
  changeVip.value = 1;
}

defineExpose({
  init,
});
</script>