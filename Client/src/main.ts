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

// axios.defaults.baseURL = import.meta.env.VITE_BASE_API as string
axios.defaults.baseURL = "http://127.0.0.1:9555"
axios.defaults.withCredentials = true
// axios.defaults.crossDomain = true
// axios.defaults.headers.common['Access-Control-Allow-Origin'] = process.env.VUE_APP_Access_Control_Allow_Origin;

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(VueAxios, axios)
app.use(ElementPlus)
app.mount('#app')
