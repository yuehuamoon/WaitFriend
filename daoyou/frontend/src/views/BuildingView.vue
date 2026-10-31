<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { PenLine } from 'lucide-vue-next'
import { articleApi, buildingApi } from '../api'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const store = useUserStore()
const building = ref(null)
const articles = ref([])
const total = ref(0)
const page = ref(1)
const tab = ref('hot')
const loading = ref(false)
const dialog = ref(false)
const submitting = ref(false)
const form = reactive({ title: '', body: '' })

const load = async () => {
 loading.value = true
 try {
 const [b, result] = await Promise.all([
 buildingApi.get(route.params.id),
 articleApi.list({ buildingId: route.params.id, tab: tab.value, page: page.value, size:10 })
 ])
 building.value = b
 articles.value = result.records
 total.value = result.total
 } finally { loading.value = false }
}
watch([tab, page], load)
onMounted(load)
const publish = async () => {
 if (!store.token) return router.push('/login')
 if (!form.title.trim() || !form.body.trim()) return ElMessage.warning('请写下标题与正文')
 submitting.value = true
 try {
 const result = await articleApi.create({ buildingId: Number(route.params.id), ...form, type: 'normal' })
 dialog.value = false
 ElMessage.success('文章已发布')
 router.push(`/article/${result.id}`)
 } finally { submitting.value = false }
}
</script>
<template>
 <section v-if="building">
 <div class="mb-7 flex flex-col gap-5 border-b ink-divider pb-6 md:flex-row md:items-end md:justify-between">
 <div><p class="text-xs tracking-[.2em] text-cinnabar">{{ building.region || '山河之间' }}</p><h1 class="brush-title mt-2 text-4xl">{{ building.name }}</h1><p class="mt-2 text-sm text-ink/55">{{ building.description }}</p></div>
 <el-button type="primary" @click="dialog = true"><PenLine :size="16" class="mr-1.5"/>落笔</el-button>
 </div>
 <el-tabs v-model="tab" class="mb-4">
 <el-tab-pane label="热门" name="hot"/>
 <el-tab-pane label="最新" name="new"/>
 </el-tabs>
 <div v-loading="loading" class="space-y-3">
 <router-link v-for="article in articles" :key="article.id" :to="`/article/${article.id}`" class="paper block p-5 transition hover:border-pine/40 md:p-6">
 <div class="flex items-start justify-between gap-4">
 <div class="min-w-0"><div class="mb-2 flex items-center gap-2"><span class="text-xs text-cinnabar">文章</span><span class="text-xs text-ink/35">{{ article.createTime }}</span></div><h2 class="truncate text-xl text-ink">{{ article.title }}</h2><p class="mt-2 line-clamp-2 text-sm leading-6 text-ink/55">{{ article.body }}</p></div>
 <div class="shrink-0 text-right text-xs text-ink/45"><p class="text-lg text-pine">{{ article.score }}</p><p>热度</p><p class="mt-2">{{ article.agreeCount }}赞同</p></div>
 </div>
 </router-link>
 <el-empty v-if="!loading && !articles.length" description="此处尚无人留墨"/>
 </div>
 <el-pagination v-if="total >10" v-model:current-page="page" class="mt-6 justify-end" layout="prev, pager, next" :total="total" :page-size="10"/>
 </section>
 <el-dialog v-model="dialog" title="落笔留痕" width="min(92vw,620px)">
 <el-form label-position="top">
 <el-form-item label="标题"><el-input v-model="form.title" maxlength="128" show-word-limit/></el-form-item>
 <el-form-item label="正文"><el-input v-model="form.body" type="textarea" :rows="9" placeholder="将此刻心境写下来..."/></el-form-item>
 </el-form>
 <template #footer><el-button @click="dialog = false">收笔</el-button><el-button type="primary" :loading="submitting" @click="publish">发布</el-button></template>
 </el-dialog>
</template>
