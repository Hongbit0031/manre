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
                <el-form-item label="类型" prop="type">
                    <el-radio-group v-model="dataForm.type">
                        <el-radio
                            v-for="(type, index) in dataForm.typeList"
                            :label="index"
                            :key="index"
                            >{{ type }}</el-radio
                        >
                    </el-radio-group>
                </el-form-item>
                <el-form-item
                    :label="dataForm.typeList[dataForm.type] + '名称'"
                    prop="name"
                >
                    <el-input
                        v-model="dataForm.name"
                        :placeholder="dataForm.typeList[dataForm.type] + '名称'"
                    ></el-input>
                </el-form-item>
                <el-form-item label="上级菜单" prop="parentName">
                    <el-popover
                        ref="popoverRef"
                        :virtual-ref="buttonRef"
                        trigger="click"
                        placement="right-start"
                        virtual-triggering
                    >
                        <el-tree
                            :data="menuList"
                            :props="menuListTreeProps"
                            node-key="menuId"
                            ref="menuListTree"
                            @current-change="menuListTreeCurrentChangeHandle"
                            :default-expand-all="false"
                            :highlight-current="true"
                            :expand-on-click-node="false"
                        >
                        </el-tree>
                    </el-popover>
                    <el-input
                        v-model="dataForm.parentName"
                        :readonly="true"
                        placeholder="点击右侧按钮选择上级菜单"
                        class="menu-list__input"
                        style="width: 200px"
                    ></el-input>
                    <el-button ref="buttonRef" v-click-outside="onClickOutside"
                        >点击选择</el-button
                    >
                </el-form-item>

                <el-form-item
                    v-if="dataForm.type === 1"
                    label="菜单路由"
                    prop="url"
                >
                    <el-input
                        v-model="dataForm.url"
                        placeholder="菜单路由"
                    ></el-input>
                </el-form-item>
                <el-form-item
                    v-if="dataForm.type !== 0"
                    label="授权标识"
                    prop="perms"
                >
                    <el-input
                        v-model="dataForm.perms"
                        placeholder="多个用逗号分隔, 如: user:list,user:create"
                    ></el-input>
                </el-form-item>
                <el-form-item
                    v-if="dataForm.type !== 2"
                    label="排序号"
                    prop="orderNum"
                >
                    <el-input-number
                        v-model="dataForm.orderNum"
                        controls-position="right"
                        :min="0"
                        label="排序号"
                    ></el-input-number>
                </el-form-item>

                <el-form-item
                    label="图标"
                    prop="icon"
                    style="width: 550px"
                    v-show="dataForm.type !== 2"
                >
                    <el-select
                        style="width: 100%"
                        v-model="dataForm.icon"
                        placeholder=""
                        clearable
                        filterable
                        @visible-change="handelOptionsChange"
                        popper-class="lf_option_box"
                    >
                        <template v-slot:prefix>
                            <el-icon
                                :size="20"
                                style="color: #000"
                                v-if="dataForm.type !== 2"
                            >
                                <component :is="dataForm.icon" />
                            </el-icon>
                        </template>
                        <el-option
                            style="
                                display: inline-block;
                                height: auto;
                                padding: 10px 11px 0px;
                            "
                            v-for="item in dataForm.iconList"
                            :key="item"
                            :label="item"
                            :value="item"
                        >
                            <el-icon :title="item" :size="30">
                                <component :is="item" />
                            </el-icon>
                        </el-option>
                    </el-select>
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
import {
    ref,
    reactive,
    nextTick,
    getCurrentInstance,
    onMounted,
    unref,
} from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { icons } from "@/utils";
const { proxy } = getCurrentInstance();

import { ClickOutside as vClickOutside } from "element-plus";
const buttonRef = ref();
const popoverRef = ref();
const onClickOutside = () => {
    unref(popoverRef).popperRef?.delayHide?.();
};

const validateUrl = (rule, value, callback) => {
    if (dataForm.type === 1 && !/\S/.test(value)) {
        callback(new Error("菜单URL不能为空"));
    } else {
        callback();
    }
};

const visible = ref(false);
const dataForm = reactive({
    id: 0,
    type: 1,
    typeList: ["目录", "菜单", "按钮"],
    name: "",
    parentId: 0,
    parentName: "",
    url: "",
    perms: "",
    orderNum: 0,
    icon: "",
    iconList: [],
});
const dataRule = reactive({
    name: [
        {
            required: true,
            message: "菜单名称不能为空",
            trigger: "blur",
        },
    ],
    parentName: [
        {
            required: true,
            message: "上级菜单不能为空",
            trigger: "change",
        },
    ],
    url: [{ validator: validateUrl, trigger: "blur" }],
});
const menuList = ref([]);
const menuListTreeProps = {
    label: "name",
    children: "children",
};

const init = (id) => {
    handelOptionsChange();
    dataForm.id = id || 0;
    proxy
        .$http({
            url: proxy.$http.adornUrl("/sys/menu/select"),
            method: "get",
            params: proxy.$http.adornParams(),
        })
        .then(({ data }) => {
            menuList.value = treeDataTranslate(data.menuList, "menuId");
        })
        .then(() => {
            visible.value = true;
            nextTick(() => {
                proxy.$refs.popForm.resetFields();
            });
        })
        .then(() => {
            if (!dataForm.id) {
                // 新增
                menuListTreeSetCurrentNode();
            } else {
                // 修改
                proxy
                    .$http({
                        url: proxy.$http.adornUrl(
                            `/sys/menu/info/${dataForm.id}`
                        ),
                        method: "get",
                        params: proxy.$http.adornParams(),
                    })
                    .then(({ data }) => {
                        dataForm.id = data.menu.menuId;
                        dataForm.type = data.menu.type;
                        dataForm.name = data.menu.name;
                        dataForm.parentId = data.menu.parentId;
                        dataForm.url = data.menu.url;
                        dataForm.perms = data.menu.perms;
                        dataForm.orderNum = data.menu.orderNum;
                        dataForm.icon = data.menu.icon;
                        menuListTreeSetCurrentNode();
                    });
            }
        });
};

const handelOptionsChange = (flag) => {
    dataForm.iconList = icons();
};

const menuListTreeCurrentChangeHandle = (data, node) => {
    dataForm.parentId = data.menuId;
    dataForm.parentName = data.name;
};

const menuListTreeSetCurrentNode = () => {
    proxy.$refs.menuListTree.setCurrentKey(dataForm.parentId);
    dataForm.parentName = (proxy.$refs.menuListTree.getCurrentNode() || {})[
        "name"
    ];
};

const iconActiveHandle = (iconName) => {
    dataForm.icon = iconName;
};

const dataFormSubmit = () => {
    proxy.$refs.popForm.validate((valid) => {
        if (valid) {
            proxy
                .$http({
                    url: proxy.$http.adornUrl(
                        `/sys/menu/${!dataForm.id ? "save" : "update"}`
                    ),
                    method: "post",
                    data: proxy.$http.adornData({
                        menuId: dataForm.id || undefined,
                        type: dataForm.type,
                        name: dataForm.name,
                        parentId: dataForm.parentId,
                        url: dataForm.url,
                        perms: dataForm.perms,
                        orderNum: dataForm.orderNum,
                        icon: dataForm.icon,
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
};

onMounted(() => {
    init();
});
defineExpose({
    init,
});
</script>

<style lang="scss">
.mod-menu {
    .menu-list__input,
    .icon-list__input {
        > .el-input__inner {
            cursor: pointer;
        }
    }
    &__icon-popover {
        width: 458px;
        overflow: hidden;
    }
    &__icon-inner {
        width: 478px;
        max-height: 258px;
        overflow-x: hidden;
        overflow-y: auto;
    }
    &__icon-list {
        width: 458px;
        padding: 0;
        margin: -8px 0 0 -8px;
        > .el-button {
            padding: 8px;
            margin: 8px 0 0 8px;
            > span {
                display: inline-block;
                vertical-align: middle;
                width: 18px;
                height: 18px;
                font-size: 18px;
            }
        }
    }
    .icon-list__tips {
        font-size: 18px;
        text-align: center;
        color: #e6a23c;
        cursor: pointer;
    }
}
</style>
<style>
.el-popper {
    max-width: 500px !important;
}
</style>
