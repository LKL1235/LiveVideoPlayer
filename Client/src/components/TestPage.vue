<template>
        <video v-if="shouldLoad" ref="videoPlayer" autoplay controls class="video-js vjs-defaultskin" >
            <source ref="sourceRef" :src="videoSrc" type="application/x-mpegURL"/>
        </video>
        <button @click="load">加载</button>
        <button @click="play">播放</button>
        <button @click="stop">暂停</button>
        <button @click="destroy">销毁</button>
</template>

<script setup lang="ts">
import videojs from 'video.js';
import 'video.js/dist/video-js.css';
import { onMounted, ref, watch, type Ref } from 'vue';

const videoPlayer: Ref = ref<HTMLElement>();
const playerRef = ref();
const sourceRef = ref();
const videoSrc = ref("");
const shouldLoad = ref(false);

const load = () =>{
    videoSrc.value = "https://v8.dious.cc/20221210/1nVKudfc/1500kb/hls/index.m3u8"
    shouldLoad.value = true;

    // sourceRef.value.src = "https://v8.dious.cc/20221210/1nVKudfc/1500kb/hls/index.m3u8"
    // sourceRef.value.type = "application/x-mpegURL"
    // videoPlayer.value.src = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    // videoPlayer.value.type = "application/x-mpegURL"
    // playerRef.value.src({type:"application/x-mpegURL",src:"https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"});
    console.log("load");
}
const play = () =>{
    playerRef.value.play();
    console.log("play");
    
}

const stop = () => {
    playerRef.value.pause();
    console.log(playerRef.value.currentTime());
}
const destroy = () => {
    playerRef.value.dispose();
    shouldLoad.value = false;
    console.log("destroy");
}

watch(videoPlayer,()=>{
    playerRef.value = videojs(videoPlayer.value, {
        controls: true,
        autoplay: true,
        preload: 'auto',
        fluid: true,
        // textTrackSettings: false,
        // source:[{
        // // src:"https://v8.dious.cc/20221210/1nVKudfc/1500kb/hls/index.m3u8",
        // // src:"http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8",
        // src:"https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
        // type: 'application/x-mpegURL'
        // }]
    },()=>{console.log("callback")} );
    console.log("watch");
});

onMounted(() => {

    
    // player.play();
})



</script>

<style scoped>
</style>