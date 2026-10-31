<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { BookOpen, Building2, Landmark, LogIn, ScrollText, UserRound } from 'lucide-vue-next'
import { useUserStore } from './stores/user'
import { notificationApi } from './api'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const unread = ref(0)

const refreshUnread = async () => {
 if (!store.token) return
 try { unread.value = await notificationApi.unreadCount() } catch (_) { unread.value =0 }
}
onMounted(async () => {
 if (store.token && !store.userInfo) await store.fetchMe().catch(() => {})
 await refreshUnread()
})
watch(() => route.fullPath, refreshUnread)
const logout = async () => {
 await store.logout()
 router.push('/login')
}
</script>

<template>
 <div class="min-h-screen">
 <header class="sticky top-0 z-20 border-b border-pine/15 bg-rice/95 backdrop-blur">
 <div class="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 md:px-8">
 <router-link to="/" class="flex items-center gap-3">
 <span class="grid h-9 w-9 place-items-center rounded-full bg-pine text-rice"><BookOpen :size="18"/></span>
 <span class="brush-title text-xl text-ink">道友，请留步！</span>
 </router-link>
 <nav class="hidden items-center gap-6 text-sm md:flex">
 <router-link to="/" class="flex items-center gap-1.5 hover:text-cinnabar"><ScrollText :size="16"/>天骄榜</router-link>
 <router-link to="/buildings" class="flex items-center gap-1.5 hover:text-cinnabar"><Building2 :size="16"/>游历</router-link>
 <router-link to="/relics" class="flex items-center gap-1.5 hover:text-cinnabar"><Landmark :size="16"/>残碑</router-link>
 <router-link v-if="store.token" to="/me" class="relative flex items-center gap-1.5 hover:text-cinnabar">
 <UserRound :size="16"/>洞府
 <span v-if="unread" class="grid h-4 min-w-4 place-items-center rounded-full bg-cinnabar px-1 text-[10px] text-white">{{ unread >9 ? '9+' : unread }}</span>
 </router-link>
 <button v-if="store.token" @click="logout" class="text-ink/55 hover:text-cinnabar">离席</button>
 <router-link v-else to="/login" class="flex items-center gap-1.5 hover:text-cinnabar"><LogIn :size="16"/>入世</router-link>
 </nav>
 <router-link v-if="!store.token" to="/login" class="text-sm text-pine md:hidden">入世</router-link>
 </div>
 </header>
 <main class="mx-auto max-w-6xl px-4 py-8 md:px-8"><router-view /></main>
 </div>
</template>
