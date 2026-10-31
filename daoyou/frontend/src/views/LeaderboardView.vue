<script setup>
import { onMounted, ref } from 'vue'
import { userApi } from '../api'
import PageHeader from '../components/PageHeader.vue'
import { Crown } from 'lucide-vue-next'

const users = ref([])
const loading = ref(true)
onMounted(async () => {
 try { users.value = await userApi.leaderboard() } finally { loading.value = false }
})
</script>
<template>
 <PageHeader eyebrow="天椒榜" title="此间风云人物" description="榜上留名，皆是一步一印，当属绝世天椒！！！">
 <router-link to="/buildings"><el-button>游历八方</el-button></router-link>
 </PageHeader>
 <div v-loading="loading" class="paper overflow-hidden">
 <div v-for="(user, index) in users" :key="user.uuid" class="group grid grid-cols-[3rem,1fr,auto] items-center gap-3 border-b ink-divider px-4 py-4 last:border-0 md:grid-cols-[4rem,1fr,10rem] md:px-8">
 <div class="brush-title text-2xl" :class="index <3 ? 'text-cinnabar' : 'text-ink/35'">
 <Crown v-if="index ===0" :size="22"/>
 <span v-else>{{ String(index +1).padStart(2,'0') }}</span>
 </div>
 <router-link :to="`/user/${user.uuid}/stele`" class="flex min-w-0 items-center gap-3">
 <el-avatar :size="42" :src="user.avatar">{{ (user.username || '道')[0] }}</el-avatar>
 <div class="min-w-0"><p class="truncate text-base text-ink group-hover:text-cinnabar">{{ user.username || '无名道友' }}</p><p class="mt-1 text-xs text-ink/40">观其石碑</p></div>
 </router-link>
 <div class="text-right"><p class="text-xl text-pine">{{ Number(user.cultivation).toFixed(1) }}</p><p class="text-[11px] tracking-widest text-ink/35">修为</p></div>
 </div>
 <el-empty v-if="!loading && !users.length" description="榜上尚无人名" />
 </div>
</template>
