<template>
    <div>
        <VideoPlayerVue 
            @play="play" 
            @pause="pause" 
            @ended="ended"
            :options="options"
            ></VideoPlayerVue>
    </div>
</template>

<script setup lang="ts">
import Hls from 'hls.js';
import { onMounted, ref, type Ref } from 'vue';
import type VideoPlayerVue from './VideoPlayer.vue';

const videoPlayer: Ref = ref<HTMLElement>();
const playerRef = ref();

const options = ref({})
const controls = ref(true);

const play= () =>{
// playerRef.value.src({
//     // src:"https://v8.dious.cc/20221210/1nVKudfc/1500kb/hls/index.m3u8",
//     src:"http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8",
//     type: 'application/x-mpegURL'
// });
// playerRef.value.play();
}

const pause= () =>{}
const ended= () =>{}

const stop = () => {
playerRef.value.pause();
console.log(playerRef.value.currentTime());
}

onMounted(() => {
    options.value = {
    controls: controls.value,
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
    }
})



</script>

<style scoped>
</style>