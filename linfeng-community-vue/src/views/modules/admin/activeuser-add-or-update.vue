<template>
    <div>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    v-model="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="popForm" @keyup.enter="dataFormSubmit()" label-width="80px">
    <el-form-item label="IP" prop="ip">
      <el-input v-model="dataForm.ip" placeholder="IP"></el-input>
    </el-form-item>
    <el-form-item label="用户ID" prop="uid">
      <el-input v-model="dataForm.uid" placeholder="用户ID"></el-input>
    </el-form-item>
    <el-form-item label="活跃次数" prop="activeCount">
      <el-input v-model="dataForm.activeCount" placeholder="活跃次数"></el-input>
    </el-form-item>
    <el-form-item label="访问终端" prop="terminal">
      <el-input v-model="dataForm.terminal" placeholder="访问终端"></el-input>
    </el-form-item>
    <el-form-item label="访问类型:0匿名1登录" prop="type">
      <el-input v-model="dataForm.type" placeholder="访问类型:0匿名1登录"></el-input>
    </el-form-item>
    <el-form-item label="创建时间" prop="createTime">
      <el-input v-model="dataForm.createTime" placeholder="创建时间"></el-input>
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
                                                ip: '',
                                                uid: '',
                                                activeCount: '',
                                                terminal: '',
                                                type: '',
                                                createTime: '',
                                                updateTime: ''
                        });
    const dataRule = reactive({
                                                        ip: [
                { required: true, message: 'IP不能为空', trigger: 'blur' }
            ],
                                                uid: [
                { required: true, message: '用户ID不能为空', trigger: 'blur' }
            ],
                                                activeCount: [
                { required: true, message: '活跃次数不能为空', trigger: 'blur' }
            ],
                                                terminal: [
                { required: true, message: '访问终端不能为空', trigger: 'blur' }
            ],
                                                type: [
                { required: true, message: '访问类型:0匿名1登录不能为空', trigger: 'blur' }
            ],
                                                createTime: [
                { required: true, message: '创建时间不能为空', trigger: 'blur' }
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
                url: proxy.$http.adornUrl(`/admin/activeuser/info/${dataForm.value.id}`),
                    method: 'get',
        params: proxy.$http.adornParams()
        }).then(({data}) => {
                if (data && data.code === 0) {
                                                                    dataForm.value.ip = data.activeUser.ip
                                                        dataForm.value.uid = data.activeUser.uid
                                                        dataForm.value.activeCount = data.activeUser.activeCount
                                                        dataForm.value.terminal = data.activeUser.terminal
                                                        dataForm.value.type = data.activeUser.type
                                                        dataForm.value.createTime = data.activeUser.createTime
                                                        dataForm.value.updateTime = data.activeUser.updateTime
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
                url: proxy.$http.adornUrl(`/admin/activeuser/${!dataForm.value.id ? 'save' : 'update'}`),
                    method: 'post',
        data: proxy.$http.adornData({
                                'id': dataForm.value.id || undefined,
                                    'ip': dataForm.value.ip,
                                    'uid': dataForm.value.uid,
                                    'activeCount': dataForm.value.activeCount,
                                    'terminal': dataForm.value.terminal,
                                    'type': dataForm.value.type,
                                    'createTime': dataForm.value.createTime,
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