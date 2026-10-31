<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { PenLine } from 'lucide-vue-next'
import { articleApi, interactionApi, userApi } from '../api'
import PageHeader from '../components/PageHeader.vue'

const route = useRoute()
const user = ref(null)
const stele = ref([])
const loading = ref(true)
onMounted(async () => {
 try {
 [user.value, stele.value] = await Promise.all([userApi.brief(route.params.uuid), articleApi.stele(route.params.uuid)])
 } finally { loading.value=false }
})
const urge = (building) => ElMessage.success(`已向${user.value?.username || '道友'}递上一封催更帖`)
</script>
<template>
 <PageHeader v-if="user" eyebrow="个人石碑" :title="`${user.username || '无名道友'}的碑林`" :description="`当前修为 ${Number(user.cultivation).toFixed(1)}。一碑一文，记下走过的地方。`">
 <el-avatar :size="64" :src="user.avatar">{{ (user.username || '道')[0] }}</el-avatar>
 </PageHeader>
 <div v-loading="loading" class="grid gap-4 md:grid-cols-2">
 <div v-for="item in stele" :key="item.buildingId" class="paper min-h-48 p-6">
 <div class="flex items-start justify-between"><div><p class="text-xs text-cinnabar">{{ item.region || '山河之间' }}</p><h2 class="brush-title mt-2 text-2xl">{{ item.buildingName }}</h2></div><span class="text-xs text-ink/30">{{ item.article ? item.article.score + '热度' : '' }}</span></div>
 <router-link v-if="item.article" :to="`/article/${item.article.id}`" class="mt-5 block border-l-2 border-gold/60 pl-4"><h3 class="text-lg text-ink hover:text-cinnabar">{{ item.article.title }}</h3><p class="mt-2 line-clamp-3 text-sm leading-6 text-ink/55">{{ item.article.body }}</p></router-link>
 <div v-else class="mt-7 flex items-center justify-between border-t border-dashed ink-divider pt-4"><span class="text-sm text-ink/35">未曾留墨</span><el-button text @click="urge(item)"><PenLine :size="15" class="mr-1"/>催更</el-button></div>
 </div>
 </div>
</template>
