<template>
  <div style="border: 1px solid #ccc">
    <Toolbar
      style="border-bottom: 1px solid #ccc"
      :editor="editorRef"
      :defaultConfig="toolbarConfig"
      :mode="mode"
    />
    <Editor
      style="height: 500px; overflow-y: hidden"
      v-model="valueHtml"
      :defaultConfig="editorConfig"
      :mode="mode"
      @onCreated="handleCreated"
    />
  </div>
</template>
<script>
import "@wangeditor/editor/dist/css/style.css"; // 引入 css

import {
  onBeforeUnmount,
  ref,
  shallowRef,
  onMounted,
  getCurrentInstance,
} from "vue";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";


export default {
  components: { Editor, Toolbar },
  setup() {
    const { proxy } = getCurrentInstance();
    // 编辑器实例，必须用 shallowRef
    const editorRef = shallowRef();
    // 内容 HTML
    const valueHtml = ref("");
    const editorConfig = {
      MENU_CONF: {},
    };
    editorConfig.MENU_CONF["uploadImage"] = {
      server: proxy.$http.adornUrl(
        `/sys/oss/uploadToEditor?token=${sessionStorage.getItem("token")}`
      ),
      fieldName: "file",
    };
    const toolbarConfig = {};
    toolbarConfig.excludeKeys = [
      "group-video",// 排除菜单组，写菜单组 key 的值即可
      "insertTable",
      "todo",
      "insertLink",
      "blockquote"
    ];

    onMounted(() => {

    });

    // 组件销毁时，也及时销毁编辑器
    onBeforeUnmount(() => {
      const editor = editorRef.value;
      if (editor == null) return;
      editor.destroy();
    });

    const handleCreated = (editor) => {
      editorRef.value = editor; // 记录 editor 实例，重要！
    };

    return {
      editorRef,
      valueHtml,
      mode: "default", // 或 'simple'
      toolbarConfig,
      handleCreated,
      editorConfig,
    };
  },
};
</script>