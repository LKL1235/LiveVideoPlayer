import Hls from "hls.js";
import type { Store, _UnwrapAll } from "pinia";
import type { Ref } from 'vue'

export const baseVideoUrl = "http://139.9.32.27/src/video/"
export const baseLiveUrl = "http://139.9.32.27/live?port=1935&app=live&stream=video"

export type WebSocketMessage = {
  data: string,
  Length: number,
  time: string
}


export function HlsInit(HlsRef: Ref) {
  if (isNullOrUndefined(HlsRef.value)) {
    //初始化
    return new Hls();
  } else {
    return HlsRef.value
  }
}

export function createdPlayer(videoSrc: string, videoRef: Ref, HlsRef: Ref) {
  if (isNullOrUndefined(videoRef.value)) {
    return false
  }
  if (Hls.isSupported()) {
    if (!isNullOrUndefined(HlsRef.value)){
        HlsRef.value.destroy()
    }
    //初始化
    HlsRef.value = HlsInit(HlsRef.value)
    HlsRef.value.loadSource(videoSrc)
    HlsRef.value.attachMedia(videoRef.value)
    return true
  } else if (videoRef.value.canPlayType('application/vnd.apple.mpegurl')) {
    // 在不支持Hls的平台尝试其他方式
    videoRef.value.src = videoSrc;
    return true
  } else {
    return false
  }
}

export function changeVideo(videoSrc:string, videoRef:Ref, HlsRef:Ref){
    HlsRef.value.destroy()
    HlsRef.value = new Hls()
    HlsRef.value.loadSource(videoSrc)
    HlsRef.value.attachMedia(videoRef.value)
}


export function WebSocketHandle(socket: WebSocket, user: Store<"user", _UnwrapAll<Pick<{ username: Ref<string>; roomId: Ref<number>; }, "roomId" | "username">>, Pick<{ username: Ref<string>; roomId: Ref<number>; }, never>, Pick<{ username: Ref<string>; roomId: Ref<number>; }, never>>, onOpen: any, onError: any, onMessage: any, onClose: any) {
  if (typeof (WebSocket) === "undefined") {
    alert("您的浏览器不支持socket")
  } else {
    // 实例化socket
    socket = new WebSocket("ws://139.9.32.27:9555/ws/" + user.$state.roomId + '/' + user.$state.username)
    // 监听socket连接
    socket.onopen = onOpen
    // 监听socket错误信息
    socket.onerror = onError
    // 监听socket消息
    socket.onmessage = onMessage

    socket.onclose = onClose
  }
}

export function getMessageClient(msg: WebSocketMessage, videoRef: Ref, HlsRef: Ref, tips: Ref<Array<string>>, filePath: Ref<string>) {
  const data = JSON.parse(msg.data)
  let src:string
  tips.value.push(data.tips)
  if (data.isLive) {
    createdPlayer(baseLiveUrl, videoRef, HlsRef)
  } else {
    if (isNullOrUndefined(videoRef)) {
      return false
    }
    if (filePath.value === data.filePath) {
      if (videoRef.value.currentTime != data.time) {
        videoRef.value.currentTime = data.time
        videoRef.value.play()
      }
      if (data.isPause) {
        videoRef.value.pause()
        console.log("isPause暂停")
      } else {
        videoRef.value.play()
        console.log("isPause播放")
      }
    } else {
      if (data.filePath != "") {
        filePath.value = data.filePath
        src= baseVideoUrl + data.filePath
        changeVideo(src, videoRef, HlsRef)
        console.log("src:" + src)
        videoRef.value.currentTime = data.time
        videoRef.value.play()
      }
    }
  }
}

export function getMessageHost(msg:WebSocketMessage, tips:Ref){
    console.log(`getMessage:收到消息${msg}`)
    const data = JSON.parse(msg.data)
    tips.value.push(data.tips)
}

export function isNullOrUndefined(obj:object){
    if ((obj === undefined ) || (obj === null)){
        return true
    }
    return false
}