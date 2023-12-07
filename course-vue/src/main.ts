import { createApp } from "vue";
import App from "./App.vue";
import pinia from "./stores";

// 全部引入element-plus, 非按需引入
import ElementPlus from "element-plus";
// import "element-plus/dist/index.css"; // 默认的样式
import "uno.css";

import './styles/normalize.css'

// If you want to use ElMessage, import it.
// import "element-plus/theme-chalk/src/message.scss";
import router from "./router";

const app = createApp(App);
app.use(pinia);
app.use(ElementPlus);
app.use(router);
app.mount("#app");
