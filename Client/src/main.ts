import { createApp } from 'vue'
import { createPinia } from 'pinia'
import axios from "axios";
// @ts-ignore
import VueAxios from "vue-axios";
import App from './App.vue'
import router from './router'


axios.defaults.baseURL='/api'
axios.defaults.withCredentials = true

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(VueAxios, axios)
app.mount('#app')
