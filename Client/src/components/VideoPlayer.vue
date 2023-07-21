<template>
    <div >
        <video  
                ref="videoPlayer" 
                autoplay 
                controls 
                @play="$emit('play')"
                @pause="$emit('pause')"
                @ended="$emit('ended')"
                class="video-js vjs-defaultskin" 
                style="width: 55vw; height: 55vh;"
                >
            <source ref="sourceRef" />
        </video>
    </div>
</template>

<script setup lang="ts">
import videojs from 'video.js';
import 'video.js/dist/video-js.css';
import { onMounted, ref,  type Ref, onUnmounted } from 'vue';

const videoPlayer: Ref = ref<HTMLElement>();
const playerRef = ref();
const sourceRef = ref();
const props = defineProps({
    videoOptions: {
        type: Object,
        default: () => {
            return {
                controls: true,
                autoplay: true,
                preload: 'auto',
                fluid: true,
                // textTrackSettings: false,
                source:[{
                src:"",
                // // src:"http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8",
                // src:"https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
                type: ''
                }]
            }
        }
    }
})


onMounted(() => {
    console.log("chirld mounted")
    console.log(props.videoOptions)
    if (props.videoOptions.source != undefined && props.videoOptions.source != null){
    sourceRef.value.type = props.videoOptions.source[0].type;
    sourceRef.value.src = props.videoOptions.source[0].src;
    // videoSrc.value = "https://v8.dious.cc/20221210/1nVKudfc/1500kb/hls/index.m3u8"
    playerRef.value = videojs(videoPlayer.value, props.videoOptions,()=>{console.log("callback")}); 
    }

})


onUnmounted(() =>{
    if (playerRef.value) {
        playerRef.value.dispose();
        console.log("chirld unmounted");
    }
}
)




</script>

<style scoped>
</style>