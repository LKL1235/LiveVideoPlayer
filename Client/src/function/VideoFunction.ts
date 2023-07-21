import Hls from "hls.js";
import type { Store, _UnwrapAll } from "pinia";
import type { Ref } from 'vue'



export const baseVideoUrl = import.meta.env.VITE_BASE_VIDEO_URL as string
export const baseLiveUrl = import.meta.env.VITE_BASE_LIVE_URL as string
export const baseApiUrl = import.meta.env.VITE_BASE_API as string
export const baseWSUrl = import.meta.env.VITE_BASE_WS as string

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
        if (!isNullOrUndefined(HlsRef.value)) {
            HlsRef.value.destroy()
        }
        //初始化
        // var hls = new Hls();
        HlsRef.value = HlsInit(HlsRef) //return new Hls();
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

export function changeVideo(videoSrc: string, videoRef: Ref, HlsRef: Ref) {
    HlsRef.value.destroy()
    HlsRef.value = new Hls()
    HlsRef.value.loadSource(videoSrc)
    HlsRef.value.attachMedia(videoRef.value)
}


export function WebSocketHandle(socketRef: Ref<WebSocket>, user: Store<"user", _UnwrapAll<Pick<{ username: Ref<string>; roomId: Ref<number>; }, "roomId" | "username">>, Pick<{ username: Ref<string>; roomId: Ref<number>; }, never>, Pick<{ username: Ref<string>; roomId: Ref<number>; }, never>>, onOpen: any, onError: any, onMessage: any, onClose: any) {
    if (typeof (WebSocket) === "undefined") {
        alert("您的浏览器不支持socket")
    } else {
        // 实例化socket
        socketRef.value = new WebSocket(`${baseWSUrl}/${user.$state.roomId}/${user.$state.username}`)
        // 监听socket连接
        socketRef.value.onopen = onOpen
        // 监听socket错误信息
        socketRef.value.onerror = onError
        // 监听socket消息
        socketRef.value.onmessage = onMessage

        socketRef.value.onclose = onClose
    }
}

export function getVideoOption(controls:boolean,videoSrc:string){
    const option = {
        controls: controls,
        autoplay: true,
        preload: 'auto',
        fluid: true,
        // textTrackSettings: false,
        source:[{
        src:videoSrc,
        // "https://v8.dious.cc/20221210/1nVKudfc/1500kb/hls/index.m3u8"
        // // src:"http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8",
        // src:"https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
        type: videoSrc.endsWith(".m3u8")?'application/x-mpegURL':'video/mp4'
        }]
        }
    return option
}



export function getMessageClient(msg: WebSocketMessage, videoRef: Ref, HlsRef: Ref, tips: Ref<Array<string>>, filePath: Ref<string>) {
    const data = JSON.parse(msg.data)
    let src: string
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
                src = baseVideoUrl + data.filePath
                changeVideo(src, videoRef, HlsRef)
                console.log("src:" + src)
                videoRef.value.currentTime = data.time
                videoRef.value.play()
            }
        }
    }
}

export function getMessageHost(msg: WebSocketMessage, tips: Ref) {
    console.log(`getMessage:收到消息${msg.data}`)
    const data = JSON.parse(msg.data)
    tips.value.push(data.tips)
}

export function isNullOrUndefined(obj: object) {
    if ((obj === undefined) || (obj === null)) {
        return true
    }
    return false
}