<template>
    <div>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    v-model="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="popForm" @keyup.enter="dataFormSubmit()" label-width="80px">
    <el-form-item label="充值用户UID" prop="uid">
      <el-input v-model="dataForm.uid" placeholder="充值用户UID"></el-input>
    </el-form-item>
    <el-form-item label="" prop="nickname">
      <el-input v-model="dataForm.nickname" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="订单号" prop="orderId">
      <el-input v-model="dataForm.orderId" placeholder="订单号"></el-input>
    </el-form-item>
    <el-form-item label="充值金额" prop="price">
      <el-input v-model="dataForm.price" placeholder="充值金额"></el-input>
    </el-form-item>
    <el-form-item label="购买赠送金额" prop="givePrice">
      <el-input v-model="dataForm.givePrice" placeholder="购买赠送金额"></el-input>
    </el-form-item>
    <el-form-item label="充值类型" prop="rechargeType">
      <el-input v-model="dataForm.rechargeType" placeholder="充值类型"></el-input>
    </el-form-item>
    <el-form-item label="是否充值" prop="paid">
      <el-input v-model="dataForm.paid" placeholder="是否充值"></el-input>
    </el-form-item>
    <el-form-item label="充值支付时间" prop="payTime">
      <el-input v-model="dataForm.payTime" placeholder="充值支付时间"></el-input>
    </el-form-item>
    <el-form-item label="充值时间" prop="addTime">
      <el-input v-model="dataForm.addTime" placeholder="充值时间"></el-input>
    </el-form-item>
    <el-form-item label="退款金额" prop="refundPrice">
      <el-input v-model="dataForm.refundPrice" placeholder="退款金额"></el-input>
    </el-form-item>
    <el-form-item label="支付生成的订单号" prop="transactionId">
      <el-input v-model="dataForm.transactionId" placeholder="支付生成的订单号"></el-input>
    </el-form-item>
    <el-form-item label="订单支付编号" prop="outTradeNo">
      <el-input v-model="dataForm.outTradeNo" placeholder="订单支付编号"></el-input>
    </el-form-item>
    <el-form-item label="类型0钱包充值1会员购买充值" prop="type">
      <el-input v-model="dataForm.type" placeholder="类型0钱包充值1会员购买充值"></el-input>
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
                                    id: 0,
                                                uid: '',
                                                nickname: '',
                                                orderId: '',
                                                price: '',
                                                givePrice: '',
                                                rechargeType: '',
                                                paid: '',
                                                payTime: '',
                                                addTime: '',
                                                refundPrice: '',
                                                transactionId: '',
                                                outTradeNo: '',
                                                type: ''
                        });
    const dataRule = reactive({
                                                        uid: [
                { required: true, message: '充值用户UID不能为空', trigger: 'blur' }
            ],
                                                nickname: [
                { required: true, message: '不能为空', trigger: 'blur' }
            ],
                                                orderId: [
                { required: true, message: '订单号不能为空', trigger: 'blur' }
            ],
                                                price: [
                { required: true, message: '充值金额不能为空', trigger: 'blur' }
            ],
                                                givePrice: [
                { required: true, message: '购买赠送金额不能为空', trigger: 'blur' }
            ],
                                                rechargeType: [
                { required: true, message: '充值类型不能为空', trigger: 'blur' }
            ],
                                                paid: [
                { required: true, message: '是否充值不能为空', trigger: 'blur' }
            ],
                                                payTime: [
                { required: true, message: '充值支付时间不能为空', trigger: 'blur' }
            ],
                                                addTime: [
                { required: true, message: '充值时间不能为空', trigger: 'blur' }
            ],
                                                refundPrice: [
                { required: true, message: '退款金额不能为空', trigger: 'blur' }
            ],
                                                transactionId: [
                { required: true, message: '支付生成的订单号不能为空', trigger: 'blur' }
            ],
                                                outTradeNo: [
                { required: true, message: '订单支付编号不能为空', trigger: 'blur' }
            ],
                                                type: [
                { required: true, message: '类型0钱包充值1会员购买充值不能为空', trigger: 'blur' }
            ]
                        });
    function init(id){
        dataForm.value.id = id || 0
        visible.value = true
        nextTick(() => {
            proxy.$refs.popForm.resetFields();
        if (dataForm.value.id) {
            proxy.$http({
                url: proxy.$http.adornUrl(`/admin/userrecharge/info/${dataForm.value.id}`),
                    method: 'get',
        params: proxy.$http.adornParams()
        }).then(({data}) => {
                if (data && data.code === 0) {
                                                                    dataForm.value.uid = data.userRecharge.uid
                                                        dataForm.value.nickname = data.userRecharge.nickname
                                                        dataForm.value.orderId = data.userRecharge.orderId
                                                        dataForm.value.price = data.userRecharge.price
                                                        dataForm.value.givePrice = data.userRecharge.givePrice
                                                        dataForm.value.rechargeType = data.userRecharge.rechargeType
                                                        dataForm.value.paid = data.userRecharge.paid
                                                        dataForm.value.payTime = data.userRecharge.payTime
                                                        dataForm.value.addTime = data.userRecharge.addTime
                                                        dataForm.value.refundPrice = data.userRecharge.refundPrice
                                                        dataForm.value.transactionId = data.userRecharge.transactionId
                                                        dataForm.value.outTradeNo = data.userRecharge.outTradeNo
                                                        dataForm.value.type = data.userRecharge.type
                                    }
        })
        }
    })
    };

    // 表单提交
    function dataFormSubmit () {
    proxy.$refs['popForm'].validate((valid) => {
        if (valid) {
            proxy.$http({
                url: proxy.$http.adornUrl(`/admin/userrecharge/${!dataForm.value.id ? 'save' : 'update'}`),
                    method: 'post',
        data: proxy.$http.adornData({
                                'id': dataForm.value.id || undefined,
                                    'uid': dataForm.value.uid,
                                    'nickname': dataForm.value.nickname,
                                    'orderId': dataForm.value.orderId,
                                    'price': dataForm.value.price,
                                    'givePrice': dataForm.value.givePrice,
                                    'rechargeType': dataForm.value.rechargeType,
                                    'paid': dataForm.value.paid,
                                    'payTime': dataForm.value.payTime,
                                    'addTime': dataForm.value.addTime,
                                    'refundPrice': dataForm.value.refundPrice,
                                    'transactionId': dataForm.value.transactionId,
                                    'outTradeNo': dataForm.value.outTradeNo,
                                    'type': dataForm.value.type
                        })
        }).then(({data}) => {
                if (data && data.code === 0) {
            ElMessage({
                message: '操作成功',
                        type: 'success',
                        duration: 1500,
                        onClose: () => {
                    visible.value = false;
                proxy.$emit('refreshDataList')
                }
            })
            } else {
            ElMessage.error(data.msg)
            }
        })
        }
    })
    };

    defineExpose({
        init,
    });
</script>