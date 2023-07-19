<template>
    <video  v-if="shouldLoad" 
            ref="videoPlayer" 
            autoplay 
            controls 
            @play="$emit('play')"
            @pause="$emit('pause')"
            @ended="$emit('ended')"
            class="video-js vjs-defaultskin" >
        <source ref="sourceRef" :src="videoSrc" type="application/x-mpegURL"/>
    </video>
</template>

<script setup lang="ts">
import videojs from 'video.js';
import 'video.js/dist/video-js.css';
import { onMounted, ref,  type Ref, onUnmounted } from 'vue';

const videoPlayer: Ref = ref<HTMLElement>();
const playerRef = ref();
const sourceRef = ref();
const videoSrc = ref("");
const shouldLoad = ref(false);
const props = defineProps({
    options: {
        type: Object,
        default: () => ({})
    }
})

onMounted(() => {
    playerRef.value = videojs(videoPlayer.value, props.options,()=>{console.log("callback")});
    playerRef.value.dispose();
// player.play();
})

onUnmounted(() =>{
    if (playerRef.value) {
        playerRef.value.dispose();
    }
}
)




</script>

<style scoped>
</style>