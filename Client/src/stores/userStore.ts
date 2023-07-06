import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
    const username=ref("")
    const roomId=ref(0)
    return { username,roomId }
})
