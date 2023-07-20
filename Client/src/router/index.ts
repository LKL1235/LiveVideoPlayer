import { createRouter, createWebHistory } from 'vue-router'
import GlobalView from "@/views/GlobalView.vue"
import PlayerHost from "@/components/PlayerHost.vue"
import PlayerClient from "@/components/PlayerClient.vue"
import TestPage from "@/components/TestPage.vue"
import TestPage2 from "@/components/TestPage2.vue"
import MainView from "@/views/MainView.vue"
import VideoPlayer from "@/components/VideoPlayer.vue"
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
    },
    {
        path:'/test',
        name:'test',
        component:TestPage
    },
    {
        path:'/test2',
        name:'test2',
        component:TestPage2,
        children:[
            {  path:'video',
                name:'video',
                component:VideoPlayer
            }
        ]
    }
  ]
})
let store:any=null
router.beforeEach((to, from,next) => {
  if (store === null) {
    store = useUserStore();
  }
  if(to.path === "/"|| to.path === "/main" || to.path === "/test"|| to.path === "/test2") {
    next()
  }else if(store.roomId==0&&store.username==''){
    return next({ name: 'main' })
  }else {
    next()
  }

})
export default router
