import { createApp } from 'vue'
import { createPinia } from 'pinia'
import axios from "axios";
// @ts-ignore
import VueAxios from "vue-axios";
import App from './App.vue'
import router from './router'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'

axios.defaults.baseURL = import.meta.env.VITE_BASE_API as string
axios.defaults.withCredentials = true

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(VueAxios, axios)
app.use(ElementPlus)
app.mount('#app')
