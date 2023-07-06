<template>
    <div>
        <span class="span">当前集数为{{ url }}</span>
    </div>

    <div>
        <video autoplay controls ref="videoRef" @pause="pause" @play="play" @ended="end" style="margin-top: 5%" height="300"
            id="myVideo"></video>
    </div>

    <div>
        <div>
            <input type="checkbox" class="checkbox1" v-model="isLive">
            <span class="label1">是否为直播流</span>
            <button class="button_load" @click="load">加载视频</button>
        </div>

        <div class="div_select">
            <select id="ldir" ref="ldir" class="select1" @change="fdirChange">
                <option v-for="index in option1" :value="index.url" :key="index.url">{{ index.url }}</option>
            </select>
            <select id="sdir" ref="sdir" class="select2">
                <option v-for="index in option" :value="index.url" :key="index.url">{{ index.url }}</option>
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
import { onMounted, ref } from "vue";
import { useUserStore } from "@/stores/userStore";
import axios from "axios";
import { createdPlayer, getMessageHost, WebSocketHandle } from "@/function/VideoFunction"
import type { WebSocketMessage } from "@/function/VideoFunction"

const url = ref("")
const videoRef = ref()
const tips = ref<Array<string>>([])
const isLive = ref(false)
const user = useUserStore()
const isPause = ref(false)
const fdir = ref("")
const ldir = ref()
const sdir = ref()
var socket:WebSocket
const option = ref([
    {
        url: "mv2.mp4"
    }
])
const option1 = ref([
    {
        url: ""
    }
])
const play = () => {
    isPause.value = false
    send()
}
const pause = () => {
    isPause.value = true
    send()
}
const end = () => {
    var index = sdir.value.selectedIndex;
    sdir.value.selectedIndex += 1
    if (fdir.value != "") {
        url.value = fdir.value + '/' + option.value[index + 1].url
    } else {
        url.value = option.value[index + 1].url
    }
    src.value = baseVideoUrl + url.value
    send()
}
const change = () => {
    if (fdir.value != "") {
        url.value = fdir.value + '/' + sdir.value.value
    } else {
        url.value = sdir.value.value
    }
}
const baseVideoUrl = "http://139.9.32.27/src/video/"
const baseLiveUrl = "http://139.9.32.27/live?port=1935&app=live&stream=video"

const load = () => {
    if (isLive.value) {
        createdPlayer(videoSrc, videoRef, HlsRef)
    } else {
        tips.value.push("\n当前播放视频为：" + url.value)
        src.value = baseVideoUrl + url.value
        console.log("src:" + src.value)
    }
}

const init = ()=>{
  WebSocketHandle(socket, user, onOpen, onError, onMessage, onClose)
}

const onOpen = () => {
    console.log("socket连接成功")
    tips.value.push("连接房间成功")
}
const onError = () => {
    console.log("连接错误")
}
const onMessage = (msg: any) => {
    getMessageHost(msg, tips)
}
const send = () => {
    tips.value.push("\n房间已同步")
    let Video = videoRef.value
    let message = { url: url.value, time: Video.currentTime, isLive: isLive.value, tips: tips.value, isPause: isPause.value }
    socket.send(JSON.stringify(message))
}
const onClose = () => {
    console.log("socket已经关闭")
    tips.value.push('\n连接断开了')
}

const fdirChange = () => {
    fdir.value = ldir.value.value
    getDir(fdir.value)
}

const getDir = (dir: string) => {
    if (dir != "") {
        axios.get("/sdir/" + dir).then((respon) => {
            console.log(respon.data)
            option.value = respon.data
        }).catch(error => {
            console.log(error)
        })
    } else {
        axios.get("/sdir").then((respon) => {
            console.log(respon.data)
            option.value = respon.data
        }).catch(error => {
            console.log(error)
        })
    }
}
const listDir = () => {
    axios.get("/ldir").then((respon) => {
        console.log(respon.data)
        respon.data.forEach(item => option1.value.push(item))
    }).catch(error => { console.log(error) })
    var index = ldir.value.selectedIndex
    fdir.value = option1.value[index].url
    getDir(fdir.value)
}
onMounted(() => {
    // 初始化
    init()
    listDir()
})
</script>

<style>
.span {
    margin-left: 35%;
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
