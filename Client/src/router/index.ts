import { createRouter, createWebHistory } from 'vue-router'
import GlobalView from "@/views/GlobalView.vue"
import PlayerHost from "@/components/PlayerHost.vue"
import PlayerClient from "@/components/PlayerClient.vue"
import MainView from "@/views/MainView.vue"
import {useUserStore} from "@/stores/userStore";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/global',
      name: 'global',
      component: GlobalView,
      children:[
        {
          path:'/host',
          name:'host',
          component:PlayerHost,
        },
        {
          path:'/client',
          name:'client',
          component:PlayerClient,
        },
      ]
    },
    {
        path:'/main',
        name:'main',
        component:MainView,
    },
    {
      path:'/',
      redirect:'/main',
    }
  ]
})
let store:any=null
router.beforeEach((to, from,next) => {
  if (store === null) {
    store = useUserStore();
  }
  if(to.path === "/"|| to.path === "/main") {
    next()
  }else if(store.roomId==0&&store.username==''){
    return next({ name: 'main' })
  }else {
    next()
  }

})
export default router
