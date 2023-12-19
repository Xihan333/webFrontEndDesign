import { createApp } from "vue";
import { createPinia } from 'pinia'
import App from "./App.vue";

// 全部引入element-plus, 非按需引入
import ElementPlus from "element-plus";
// import "element-plus/dist/index.css"; // 默认的样式
import "uno.css";
import './styles/normalize.css'
import './styles/element/global.scss'

// If you want to use ElMessage, import it.
import "element-plus/theme-chalk/src/message.scss";
import "element-plus/theme-chalk/el-message-box.css";
import router from "./router";
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import persist from 'pinia-plugin-persistedstate'

// If you want to use Md-editor, import it.
import VMdEditor from '@kangc/v-md-editor';
import '@kangc/v-md-editor/lib/style/base-editor.css';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import '@kangc/v-md-editor/lib/theme/style/github.css';

// highlightjs
import hljs from 'highlight.js';

VMdEditor.use(githubTheme, {
  Hljs: hljs,
});


const app = createApp(App);
const pinia = createPinia();
pinia.use(persist)
app.use(pinia);
app.use(ElementPlus);
app.use(router);
app.use(VMdEditor);
// app.component('EditorMarkdown',EditorMarkdown)
app.mount("#app");
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
