<template>
  <h1 >当前房间号为{{user.$state.roomId}}</h1>
  <router-view/>
</template>

<script setup lang="ts">
import {useUserStore} from "@/stores/userStore";

const user=useUserStore()
</script>

<style scoped>
h1{
  margin-left: 35vw;
}
</style>
