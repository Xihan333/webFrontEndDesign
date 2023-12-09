import { createApp } from "vue";
import App from "./App.vue";
import pinia from "./stores";

// 全部引入element-plus, 非按需引入
import ElementPlus from "element-plus";
// import "element-plus/dist/index.css"; // 默认的样式
import "uno.css";
import './styles/normalize.css'
import './styles/element/global.css'

// If you want to use ElMessage, import it.
// import "element-plus/theme-chalk/src/message.scss";
import router from "./router";
import * as ElementPlusIconsVue from '@element-plus/icons-vue'


const app = createApp(App);
app.use(pinia);
app.use(ElementPlus);
app.use(router);
app.mount("#app");
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
