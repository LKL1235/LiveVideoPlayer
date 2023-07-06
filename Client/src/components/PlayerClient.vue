<template>
  <div>
    <span class="span">当前集数为{{ url }}</span>
  </div>
  <div>
    <video autoplay ref="videoRef" style="margin-top: 5%" height="300" id="myVideo"></video>
  </div>
  <div>
    <textarea disabled v-model="tips" class="tips">
    </textarea>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, type Ref } from "vue";
import { useUserStore } from "@/stores/userStore";
import { getMessageClient, WebSocketHandle} from "@/function/VideoFunction"
import type{WebSocketMessage} from "@/function/VideoFunction"

const user = useUserStore()

let socket: WebSocket
const url = ref("")
const src = ref("")
const tips = ref<Array<string>>([])
const isLive = ref(false)
const videoRef = ref<HTMLVideoElement>()
const HlsRef = ref()

// 检测浏览器是否支持 flv.js

// const init = (socket, user, onOpen, onError, onMessage, onClose) => {
//   if (typeof (WebSocket) === "undefined") {
//     alert("您的浏览器不支持socket")
//   } else {
//     // 实例化socket
//     socket = new WebSocket("ws://139.9.32.27:9555/ws/" + user.$state.roomId + '/' + user.$state.username)
//     // 监听socket连接
//     socket.onopen = onOpen
//     // 监听socket错误信息
//     socket.onerror = onError
//     // 监听socket消息
//     socket.onmessage = onMessage

//     socket.onclose = onClose
//   }
// }

const init = ()=>{
  WebSocketHandle(socket, user, onOpen, onError, onMessage, onClose)
}


const onOpen = () => {
  console.log("socket连接成功")
  tips.value && tips.value.push("连接房间成功")
}
const onError = () => {
  console.log("连接错误")
}

const onMessage = (msg: WebSocketMessage) => {
  // getMessage(msg: WebSocketMessage, videoRef: Ref<any>, HlsRef: Ref<any>, tips: Ref<string[]>, url: Ref<string>, src: Ref<any>)
  getMessageClient(msg, videoRef, HlsRef, tips, url)
}

const onClose = () => {
  console.log("socket已经关闭")
  tips.value && tips.value.push('\n连接断开了')
}
onMounted(() => {
  // 初始化
  init()
})

</script>

<style>
.tips {
  position: fixed;
  top: 51%;
  left: 20%;
  height: 130px;
  width: 350px
}

@media screen and (min-width: 1200px) {
  .tips {
    position: relative;
    top: 10px;
    left: 33%;
    height: 300px;
    width: 500px;
  }
}</style>
