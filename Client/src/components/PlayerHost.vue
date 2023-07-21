<template>
    <div style="font-size: 3vh;text-align: center;">
        当前播放:{{ filePath }}
    </div>
    <div >
        <el-button type="primary" style="margin-left: 75vw;" @click="send">为房间推送同步</el-button>
    </div>
    <span style="font-size: 24px;margin-left: 5vw;margin-right: 4vw;">请输入要解析的网页</span>
    <el-input v-model="inputUrl" placeholder="请输入视频路径" clearable style="width: 50vw;margin-right: 2vw;"></el-input>
    <el-button type="primary" :loading="parse_loading" @click="parse">解析</el-button>
    <el-button type="primary" :loading="addToPlayList_loading" @click="addToPlayList">添加到播放单</el-button>
    <div>
        <VideoPlayerVue 
            :key="VideoPlayerKey"
            @play="play" 
            @pause="pause" 
            @ended="end" 
            :video-options="videoOptions"
                        />
    </div>

    <div>
        <!-- <div> -->
        <!-- <input type="checkbox" class="checkbox1" v-model="isLive"> -->
        <!-- <span class="label1">是否为直播流</span> -->
        <!-- <button class="button_load" @click="load">加载视频</button> -->
        <!-- </div> -->

        <div class="div_select">
            <select id="ldir" ref="dirList" class="select1" @change="dirChange">
                <option v-for="index in dirOption" :value="index.url" :key="index.url">{{ index.url }}
                </option>
            </select>
            <select id="sdir" ref="fileList" class="select2">
                <option v-for="index in fileOption" :value="index.url" :key="index.url">{{ index.url }}
                </option>
            </select>
            <el-button type="primary" @click="change">从服务器列表播放</el-button>
        </div>

        <div>
            <textarea disabled v-model="tips" class="tips1">
      </textarea>
        </div>

    </div>
</template>

<script lang="ts" setup>
import { onBeforeMount, onMounted, onUnmounted, ref, type Ref } from "vue";
import { useUserStore } from "@/stores/userStore";
import axios from "axios";
import { createdPlayer, getMessageHost,
        WebSocketHandle, isNullOrUndefined,
        baseVideoUrl, getVideoOption } from "@/function/VideoFunction"
import type { WebSocketMessage } from "@/function/VideoFunction"
import { ElMessage } from 'element-plus'
import VideoPlayerVue from './VideoPlayer.vue';


const user = useUserStore()

// 组件绑定
const inputUrl = ref("")
const parse_loading = ref(false)
const addToPlayList_loading = ref(false)
const videoRef = ref()
const tips = ref<Array<string>>([])
// 视频地址
const filePath = ref("")
const videoSrc = ref("")
// videojs实例
const PlayerRef = ref()
// data
const VideoPlayerKey = ref(Math.random()+10)
const isLive = ref(false)
const isPause = ref(false)
const currentFile = ref("")
const dirList = ref()
const fileList = ref()
const socketRef: Ref<WebSocket> = ref<WebSocket>() as Ref<WebSocket>
const videoOptions = ref({})
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
    console.log("play");
    send()
}
const pause = () => {
    isPause.value = true
    console.log("pause");
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
    console.log("end");
    send()
}

const parse = () => {
    parse_loading.value = true
    if (inputUrl.value == "") {
        parse_loading.value = false
        return
    }
    try {
        // https://www.kanmtv.com/dongman/227177-1-1.html
        if (inputUrl.value.endsWith(".html")) {
            axios.get(`/getSrcFromIframeByHtml?url=${inputUrl.value}&type=m3u8`).then((respon) => {
                console.log(respon.data.data)
                if (respon.data.data === "null") {
                    parse_loading.value = false
                    return
                }
                // https://www.kanmtv.com/bofangqi/dp/dp.html?v=https://v8.dious.cc/20221210/1nVKudfc/index.m3u8
                videoSrc.value = respon.data.data as string
                const lastIndex = videoSrc.value.lastIndexOf("http")
                if (lastIndex !== -1){
                    videoSrc.value = videoSrc.value.substring(lastIndex)
                }
                console.log("videoSrc: " + videoSrc.value);
                loadFromUrl()
                parse_loading.value = false
            }).catch(error => {
                console.log(error)
                parse_loading.value = false
            })
        } else if (inputUrl.value.endsWith(".mp4")) {
            videoSrc.value = inputUrl.value
            loadFromUrl()
        } else if (inputUrl.value.endsWith(".m3u8")) {
            videoSrc.value = inputUrl.value
            loadFromUrl()
        } else {
            parse_loading.value = false
            ElMessage.error("不支持的视频格式")
        }
    }catch (error:any) {
        parse_loading.value = false
        console.log(`parse error: ${error.message}`);
        ElMessage.error("解析失败,请输入支持的网站或重试")
    }
}

const addToPlayList = () =>{

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
    videoOptions.value = getVideoOption(true,videoSrc.value)
    VideoPlayerKey.value = Math.random()+10
}

const loadFromUrl = () =>{
    tips.value.push("\n当前播放视频为：" + filePath.value)
    videoOptions.value = getVideoOption(true,videoSrc.value)
    VideoPlayerKey.value = Math.random()+10
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
const dirChange = () => {
    currentFile.value = dirList.value.value
    listDir(currentFile.value)
}

const listDir = (dir: string) => {
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
const getDir = () => {
    axios.get("/getDir").then((respon) => {
        console.log(respon.data.data)
        respon.data.data.forEach((item: { url: string; }) => dirOption.value.push(item))
    }).catch(error => { console.log(error) })
    var index = dirList.value.selectedIndex
    currentFile.value = dirOption.value[index].url
    listDir(currentFile.value)
}


onMounted(() => {
    // 初始化
    init()
    getDir()
})

onUnmounted(() => {
    socketRef.value.close()
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
