<script setup>
import { onMounted, ref } from 'vue'
import { buildingApi } from '../api'
import PageHeader from '../components/PageHeader.vue'
import { ArrowUpRight, MapPin } from 'lucide-vue-next'
const buildings = ref([])
onMounted(async () => { buildings.value = await buildingApi.list() })
</script>
<template>
 <PageHeader eyebrow="山河图" title="择一处游历" description="每一座建筑自有其气息，落笔成章，自有同道来寻。"/>
 <div class="grid gap-4 md:grid-cols-2">
 <router-link v-for="building in buildings" :key="building.id" :to="`/building/${building.id}`" class="paper group flex min-h-40 items-end justify-between p-6 transition hover:-translate-y-0.5 hover:border-pine/40">
 <div>
 <p class="mb-3 flex items-center gap-1.5 text-xs text-cinnabar"><MapPin :size="14"/>{{ building.region || '山河之间' }}</p>
 <h2 class="brush-title text-2xl">{{ building.name }}</h2>
 <p class="mt-2 max-w-md text-sm leading-6 text-ink/55">{{ building.description || '此处尚待道友留下笔墨。' }}</p>
 </div>
 <ArrowUpRight class="shrink-0 text-ink/25 transition group-hover:text-cinnabar" :size="22"/>
 </router-link>
 </div>
</template>
