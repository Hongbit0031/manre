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
                <el-form-item label="角色名称" prop="roleName">
                    <el-input
                        v-model="dataForm.roleName"
                        placeholder="角色名称"
                    ></el-input>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input
                        v-model="dataForm.remark"
                        placeholder="备注"
                    ></el-input>
                </el-form-item>
                <el-form-item size="small" label="授权">
                    <el-tree
                        :data="menuList"
                        :props="menuListTreeProps"
                        node-key="menuId"
                        ref="menuListTree"
                        :default-expand-all="true"
                        show-checkbox
                    >
                    </el-tree>
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
import { treeDataTranslate } from "@/utils";
import { ref, nextTick, getCurrentInstance } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

const { proxy } = getCurrentInstance();

const visible = ref(false);
const menuList = ref([]);
const protoMenuList = ref([]);
const menuListTreeProps = ref({
    label: "name",
    children: "children",
});
const dataForm = ref({
    id: 0,
    roleName: "",
    remark: "",
});
const dataRule = ref({
    roleName: [
        {
            required: true,
            message: "角色名称不能为空",
            trigger: "blur",
        },
    ],
});
const tempKey = ref(-666666);

function init(id) {
    dataForm.value.id = id || 0;
    proxy
        .$http({
            url: proxy.$http.adornUrl("/sys/menu/list"),
            method: "get",
            params: proxy.$http.adornParams(),
        })
        .then(({ data }) => {
            protoMenuList.value = data; // 保存原始菜单数据
            menuList.value = treeDataTranslate(data, "menuId");
        })
        .then(() => {
            visible.value = true;
            nextTick(() => {
                proxy.$refs.popForm.resetFields();
                proxy.$refs.menuListTree.setCheckedKeys([]);
            });
        })
        .then(() => {
            if (dataForm.value.id) {
                proxy
                    .$http({
                        url: proxy.$http.adornUrl(
                            `/sys/role/info/${dataForm.value.id}`
                        ),
                        method: "get",
                        params: proxy.$http.adornParams(),
                    })
                    .then(({ data }) => {
                        if (data && data.code === 0) {
                            dataForm.value.roleName = data.role.roleName;
                            dataForm.value.remark = data.role.remark;
                            var idx = data.role.menuIdList.indexOf(
                                tempKey.value
                            );
                            if (idx !== -1) {
                                data.role.menuIdList.splice(
                                    idx,
                                    data.role.menuIdList.length - idx
                                );
                            }
                            // 获取所有存在于树中的菜单ID
                            const allMenuIds = new Set(
                                protoMenuList.value.map((item) => item.menuId)
                            );

                            // 获取所有父节点ID
                            const parentIds = new Set(
                                protoMenuList.value.map((item) => item.parentId)
                            );

                            // 过滤出叶子节点（不是其他节点的父节点的节点）
                            const leafMenuIds = protoMenuList.value
                                .filter((item) => !parentIds.has(item.menuId))
                                .map((item) => item.menuId);

                            // 从角色的菜单ID列表中筛选出叶子节点ID
                            const checkedLeafIds = data.role.menuIdList.filter(
                                (menuId) =>
                                    leafMenuIds.includes(menuId) &&
                                    allMenuIds.has(menuId)
                            );

                            // 调试信息
                            // console.log('原始菜单ID列表:', data.role.menuIdList);
                            // console.log('叶子节点ID:', leafMenuIds);
                            // console.log('最终选中的叶子节点ID:', checkedLeafIds);

                            // 先清空选中状态，再设置新的选中状态
                            proxy.$refs.menuListTree.setCheckedKeys([]);
                            nextTick(() => {
                                proxy.$refs.menuListTree.setCheckedKeys(
                                    checkedLeafIds
                                );
                            });
                        }
                    });
            }
        });
}

function dataFormSubmit() {
    proxy.$refs["popForm"].validate((valid) => {
        if (valid) {
            proxy
                .$http({
                    url: proxy.$http.adornUrl(
                        `/sys/role/${!dataForm.value.id ? "save" : "update"}`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        roleId: dataForm.value.id || undefined,
                        roleName: dataForm.value.roleName,
                        remark: dataForm.value.remark,
                        menuIdList: [].concat(
                            proxy.$refs.menuListTree.getCheckedKeys(),
                            [tempKey.value],
                            proxy.$refs.menuListTree.getHalfCheckedKeys()
                        ),
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
