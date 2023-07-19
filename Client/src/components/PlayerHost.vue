<template>
    <div style="font-size: 3vh;text-align: center;">
        当前播放:{{ filePath }}
    </div>
    <span style="font-size: 24px;margin-left: 5vw;margin-right: 4vw;">请输入要解析的网页</span>
    <el-input v-model="inputUrl" 
        placeholder="请输入视频路径" 
        clearable
        style="width: 50vw;margin-right: 2vw;"
        ></el-input>
    <el-button type="primary" :loading="parse_loading" @click="parse">解析</el-button>
    <div>
        <video 
        autoplay 
        controls 
        ref="videoRef" 
        @pause="pause" 
        @play="play" 
        @ended="end" 
        style="" height="300"
        class="video-js vjs-defaultskin"></video>
    </div>

    <div>
        <!-- <div> -->
            <!-- <input type="checkbox" class="checkbox1" v-model="isLive"> -->
            <!-- <span class="label1">是否为直播流</span> -->
            <!-- <button class="button_load" @click="load">加载视频</button> -->
        <!-- </div> -->

        <div class="div_select">
            <select id="ldir" ref="dirList" class="select1" @change="videoChange">
                <option v-for="index in dirOption" :value="index.url" :key="index.url">{{ index.url }}
                </option>
            </select>
            <select id="sdir" ref="fileList" class="select2">
                <option v-for="index in fileOption" :value="index.url" :key="index.url">{{ index.url }}
                </option>
            </select>
            <button class="button_change" @click="change">选集</button>
        </div>
        <div>
            <button class="button_send" @click="send">为房间推送同步</button>
        </div>
        <div>
            <textarea disabled v-model="tips" class="tips1">
      </textarea>
        </div>

    </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, type Ref } from "vue";
import { useUserStore } from "@/stores/userStore";
import axios from "axios";
import { createdPlayer, getMessageHost, WebSocketHandle, isNullOrUndefined, baseVideoUrl } from "@/function/VideoFunction"
import type { WebSocketMessage } from "@/function/VideoFunction"
import { ElMessage } from 'element-plus'
import videojs from 'video.js';
import 'video.js/dist/video-js.css';

const user = useUserStore()

// 组件绑定
const inputUrl = ref("")
const parse_loading = ref(false)
const videoRef = ref()
const tips = ref<Array<string>>([])
// 视频地址
const filePath = ref("")
const videoSrc = ref("")
// videojs实例
const PlayerRef = ref()
// data
const isLive = ref(false)
const isPause = ref(false)
const currentFile = ref("")
const dirList = ref()
const fileList = ref()
const socketRef:Ref<WebSocket> = ref<WebSocket>() as Ref<WebSocket>
const fileOption = ref([
    {
        url: "mv2.mp4"
    }
])
const dirOption = ref([
    {
        url: ""
    }
])

// 视频控制
const play = () => {
    isPause.value = false
    send()
}
const pause = () => {
    isPause.value = true
    send()
}
const end = () => {
    var index = fileList.value.selectedIndex;
    fileList.value.selectedIndex += 1
    if (currentFile.value != "") {
        filePath.value = `${currentFile.value}/${fileOption.value[index + 1].url}`
    } else {
        filePath.value = fileOption.value[index + 1].url
    }
    videoSrc.value = baseVideoUrl + filePath.value
    send()
}

const parse = () =>{
    parse_loading.value = true
    if (inputUrl.value == "") {
        parse_loading.value = false
        return
    }
    // https://www.kanmtv.com/dongman/227177-1-1.html
    if (inputUrl.value.endsWith(".html")){
        axios.get(`/getSrcFromIframeByHtml?url=${inputUrl.value}&type=m3u8`).then((respon) => {
            console.log(respon.data.data)
            if (respon.data.data === "null"){
                parse_loading.value = false
                return
            }
            videoSrc.value = respon.data.data
            let retrycount = 0
            let noExcption = false
            while(retrycount<4 && noExcption == false){
                console.log("try to get video");
                try {
                    PlayerRef.value.src({
                        src: videoSrc.value,
                        type: 'application/x-mpegURL'
                        // type: 'video/mp4'
                    })
                    PlayerRef.value.play()
                    noExcption = true
                } catch (error) {
                    retrycount++
                    console.log(`${error},retry ${retrycount} times`);
                }
            }
            // createdPlayer(videoSrc.value, videoRef, HlsRef)
            parse_loading.value = false
        }).catch(error => {
            console.log(error)
            parse_loading.value = false
        })
    }else if(inputUrl.value.endsWith(".mp4")){
        videoSrc.value = inputUrl.value
        PlayerRef.value.src({
            src: videoSrc.value,
            type: 'video/mp4'
        })
        createdPlayer(videoSrc.value, videoRef, HlsRef)
        parse_loading.value = false
    }else if(inputUrl.value.endsWith(".m3u8")){
        videoSrc.value = inputUrl.value
        createdPlayer(videoSrc.value, videoRef, HlsRef)
        parse_loading.value = false
    }else{
        parse_loading.value = false
        ElMessage.error("不支持的视频格式")
    }

}

// 选择器改变
const change = () => {
    if (currentFile.value != "") {
        filePath.value = currentFile.value + '/' + fileList.value.value
    } else {
        filePath.value = fileList.value.value
    }
    load()
}

// 加载视频
const load = () => {
    tips.value.push("\n当前播放视频为：" + filePath.value)
    videoSrc.value = baseVideoUrl + filePath.value
    createdPlayer(videoSrc.value, videoRef, HlsRef)
}
// 初始化
const init = () => {
    WebSocketHandle(socketRef, user, onOpen, onError, onMessage, onClose)
}
// websocket方法
const onOpen = () => {
    console.log("socket连接成功")
    tips.value.push("连接房间成功")
}
const onError = () => {
    console.log("连接错误")
    WebSocketHandle(socketRef, user, onOpen, onError, onMessage, onClose)
}
const onMessage = (msg: any) => {
    getMessageHost(msg, tips)
}
const send = () => {
    tips.value.push("\n房间已同步")
    let Video = videoRef.value
    let message = { filePath: filePath.value, time: Video.currentTime, isLive: isLive.value, tips: tips.value, isPause: isPause.value }
    try {
        socketRef.value.send(JSON.stringify(message))
    } catch (error) {
        WebSocketHandle(socketRef, user, onOpen, onError, onMessage, onClose)
        socketRef.value.send(JSON.stringify(message))
    }

}
const onClose = () => {
    console.log("socket已经关闭")
    tips.value.push('\n连接断开了')
}

// 视频切换
const videoChange = () => {
    currentFile.value = dirList.value.value
    getDir(currentFile.value)
}

const getDir = (dir: string) => {
    if ((dir != "")) {
        axios.get(`/getFileList?DirName=${dir}`).then((respon) => {
            console.log(respon.data.data)
            fileOption.value = respon.data.data
        }).catch(error => {
            console.log(error)
        })
    } else {
        axios.get("/getFileList").then((respon) => {
            console.log(respon.data.data)
            fileOption.value = respon.data.data
        }).catch(error => {
            console.log(error)
        })
    }
}
const listDir = () => {
    axios.get("/getDir").then((respon) => {
        console.log(respon.data.data)
        respon.data.data.forEach((item: { url: string; }) => dirOption.value.push(item))
    }).catch(error => { console.log(error) })
    var index = dirList.value.selectedIndex
    currentFile.value = dirOption.value[index].url
    getDir(currentFile.value)
}
onMounted(() => {
    // 初始化
    init()
    listDir()
    PlayerRef.value = videojs(videoRef.value, {
        controls: true,
        autoplay: true,
        preload: 'auto',
        fluid: true,
        textTrackSettings: false,
        sources: [{
            src: 'http://vjs.zencdn.net/v/oceans.mp4',
            type: 'video/mp4'
        }]
    });
})
</script>

<style>
.span {
    margin-left: 10%;
    color: #FFFFFF;
    font-size: 24px;
}

.label1 {
    color: #FFFFFF;
    font-size: 20px;
}

.checkbox1 {
    margin-left: 15%;
}

.button_load {
    margin-left: 5%;
    height: 50px;
    width: 150px;
    background-image: linear-gradient(to right, #A7E8FF, #3ba7e8);
    border-radius: 5px;
    border-style: none;
}

.div_select {
    margin-top: 5%;
}

.button_change {
    margin-left: 5%;
    height: 50px;
    width: 150px;
    background-image: linear-gradient(to right, #A7E8FF, #3ba7e8);
    border-radius: 5px;
    border-style: none;
}

.button_send {
    margin-top: 5%;
    margin-left: 30%;
    height: 125px;
    width: 300px;
    background-image: linear-gradient(to right, #A7E8FF, #3ba7e8);
    border-radius: 5px;
    border-style: none;
}

.select1 {
    margin-left: 15%;

}

.select2 {
    margin-left: 5%;
    margin-right: 5%;
}

.tips1 {
    margin-left: 20%;
    height: 130px;
    width: 350px
}


@media screen and (min-width: 1200px) {
    #myVideo {
        margin-left: 30%;

    }

    .span {
        margin-left: 40%;
        color: #FFFFFF;
        font-size: 24px;
    }

    .label1 {
        color: #FFFFFF;
        font-size: 20px;
    }

    .checkbox1 {
        margin-left: 35%;
    }

    .button_load {
        margin-left: 5%;
        height: 50px;
        width: 150px;
        background-image: linear-gradient(to right, #A7E8FF, #3ba7e8);
        border-radius: 5px;
        border-style: none;
    }

    .div_select {
        margin-top: 1%;
    }

    .button_change {
        margin-left: 0%;
        height: 50px;
        width: 150px;
        background-image: linear-gradient(to right, #A7E8FF, #3ba7e8);
        border-radius: 5px;
        border-style: none;
    }

    .button_send {
        margin-top: 1%;
        margin-left: 37%;
        height: 125px;
        width: 300px;
        background-image: linear-gradient(to right, #A7E8FF, #3ba7e8);
        border-radius: 5px;
        border-style: none;
    }

    .select1 {
        margin-left: 35%;
    }

    .select2 {
        margin-left: 5%;
        margin-right: 5%;
    }

    .tips1 {
        margin-left: 35%;
        height: 130px;
        width: 350px
    }
}
</style>
