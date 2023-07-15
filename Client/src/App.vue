<template>
    <n-config-provider :theme="darkTheme">
        <RouterView />
    </n-config-provider>

</template>

<script setup lang="ts">
import { darkTheme } from 'naive-ui'
import { NConfigProvider} from 'naive-ui'
import { RouterView } from 'vue-router'

</script>

<style>

</style>
