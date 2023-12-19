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

const app = createApp(App);
const pinia = createPinia();
pinia.use(persist)
app.use(pinia);
app.use(ElementPlus);
app.use(router);
app.mount("#app");
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
