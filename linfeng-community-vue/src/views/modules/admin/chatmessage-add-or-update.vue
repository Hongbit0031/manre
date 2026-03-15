<template>
    <div>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    v-model="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="popForm" @keyup.enter="dataFormSubmit()" label-width="80px">
    <el-form-item label="用户session" prop="sessionId">
      <el-input v-model="dataForm.sessionId" placeholder="用户session"></el-input>
    </el-form-item>
    <el-form-item label="发送者id" prop="senderId">
      <el-input v-model="dataForm.senderId" placeholder="发送者id"></el-input>
    </el-form-item>
    <el-form-item label="接收者id" prop="receiverId">
      <el-input v-model="dataForm.receiverId" placeholder="接收者id"></el-input>
    </el-form-item>
    <el-form-item label="发送时间" prop="sendTime">
      <el-input v-model="dataForm.sendTime" placeholder="发送时间"></el-input>
    </el-form-item>
    <el-form-item label="内容" prop="content">
      <el-input v-model="dataForm.content" placeholder="内容"></el-input>
    </el-form-item>
    <el-form-item label="类型" prop="messageType">
      <el-input v-model="dataForm.messageType" placeholder="类型"></el-input>
    </el-form-item>
    <el-form-item label="是否撤回" prop="isWithdrawn">
      <el-input v-model="dataForm.isWithdrawn" placeholder="是否撤回"></el-input>
    </el-form-item>
    <el-form-item label="更新时间" prop="updateTime">
      <el-input v-model="dataForm.updateTime" placeholder="更新时间"></el-input>
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
                                                sessionId: '',
                                                senderId: '',
                                                receiverId: '',
                                                sendTime: '',
                                                content: '',
                                                messageType: '',
                                                isWithdrawn: '',
                                                updateTime: ''
                        });
    const dataRule = reactive({
                                                        sessionId: [
                { required: true, message: '用户session不能为空', trigger: 'blur' }
            ],
                                                senderId: [
                { required: true, message: '发送者id不能为空', trigger: 'blur' }
            ],
                                                receiverId: [
                { required: true, message: '接收者id不能为空', trigger: 'blur' }
            ],
                                                sendTime: [
                { required: true, message: '发送时间不能为空', trigger: 'blur' }
            ],
                                                content: [
                { required: true, message: '内容不能为空', trigger: 'blur' }
            ],
                                                messageType: [
                { required: true, message: '类型不能为空', trigger: 'blur' }
            ],
                                                isWithdrawn: [
                { required: true, message: '是否撤回不能为空', trigger: 'blur' }
            ],
                                                updateTime: [
                { required: true, message: '更新时间不能为空', trigger: 'blur' }
            ]
                        });
    function init(id){
        dataForm.value.id = id || 0
        visible.value = true
        nextTick(() => {
            proxy.$refs.popForm.resetFields();
        if (dataForm.value.id) {
            proxy.$http({
                url: proxy.$http.adornUrl(`/admin/chatmessage/info/${dataForm.value.id}`),
                    method: 'get',
        params: proxy.$http.adornParams()
        }).then(({data}) => {
                if (data && data.code === 0) {
                                                                    dataForm.value.sessionId = data.chatMessage.sessionId
                                                        dataForm.value.senderId = data.chatMessage.senderId
                                                        dataForm.value.receiverId = data.chatMessage.receiverId
                                                        dataForm.value.sendTime = data.chatMessage.sendTime
                                                        dataForm.value.content = data.chatMessage.content
                                                        dataForm.value.messageType = data.chatMessage.messageType
                                                        dataForm.value.isWithdrawn = data.chatMessage.isWithdrawn
                                                        dataForm.value.updateTime = data.chatMessage.updateTime
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
                url: proxy.$http.adornUrl(`/admin/chatmessage/${!dataForm.value.id ? 'save' : 'update'}`),
                    method: 'post',
        data: proxy.$http.adornData({
                                'id': dataForm.value.id || undefined,
                                    'sessionId': dataForm.value.sessionId,
                                    'senderId': dataForm.value.senderId,
                                    'receiverId': dataForm.value.receiverId,
                                    'sendTime': dataForm.value.sendTime,
                                    'content': dataForm.value.content,
                                    'messageType': dataForm.value.messageType,
                                    'isWithdrawn': dataForm.value.isWithdrawn,
                                    'updateTime': dataForm.value.updateTime
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