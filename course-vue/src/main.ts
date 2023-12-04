import { createApp } from "vue";
import App from "./App.vue";
import pinia from "./stores";

// import "~/styles/element/index.scss";

import ElementPlus from "element-plus";
// import all element css, uncommented next line
import "element-plus/dist/index.css";

// or use cdn, uncomment cdn link in `index.html`

import "~/styles/index.scss";
import "uno.css";

// If you want to use ElMessage, import it.
import "element-plus/theme-chalk/src/message.scss";
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