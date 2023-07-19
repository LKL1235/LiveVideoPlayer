<template>
    <div style="text-align: center;">
        <h1 >当前房间号为{{user.$state.roomId}}</h1>
    </div>
  <router-view/>
</template>

<script setup lang="ts">
import {useUserStore} from "@/stores/userStore";

const user=useUserStore()
</script>

<style scoped>
</style>
