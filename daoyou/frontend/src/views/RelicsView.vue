<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Landmark, PenLine } from 'lucide-vue-next'
import { buildingApi, relicApi } from '../api'
import PageHeader from '../components/PageHeader.vue'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()
const relics = ref([])
const buildings = ref([])
const loading = ref(true)
const dialog = ref(false)
const submitting = ref(false)
const form = reactive({ buildingId: '', title: '', body: '' })

const load = async () => {
 loading.value = true
 try { relics.value = await relicApi.list() } finally { loading.value = false }
}

onMounted(async () => {
 try {
 [relics.value, buildings.value] = await Promise.all([relicApi.list(), buildingApi.list()])
 } finally { loading.value = false }
})

const openCreate = async () => {
 if (!store.token) return router.push('/login')
 if (!buildings.value.length) buildings.value = await buildingApi.list()
 dialog.value = true
}

const create = async () => {
 if (!form.buildingId) return ElMessage.warning('请选择残碑所属地点')
 if (!form.title.trim() || !form.body.trim()) return ElMessage.warning('请写下碑题与碑文')
 submitting.value = true
 try {
 const relic = await relicApi.create({
 buildingId: Number(form.buildingId),
 title: form.title,
 body: form.body
 })
 dialog.value = false
 ElMessage.success('残碑已立')
 router.push(`/article/${relic.id}`)
 } finally { submitting.value = false }
}
</script>
<template>
 <PageHeader eyebrow="残碑" title="残篇待续，同道共书" description="碑文散落于此，循一缕旧墨，续写未完的故事。">
 <el-button type="primary" @click="openCreate"><PenLine :size="16" class="mr-1.5"/>立一方残碑</el-button>
 </PageHeader>
 <div v-loading="loading" class="grid gap-4 md:grid-cols-2">
 <router-link v-for="relic in relics" :key="relic.id" :to="`/article/${relic.id}`" class="paper group flex min-h-56 flex-col justify-between p-6 transition hover:-translate-y-0.5 hover:border-pine/40">
 <div>
 <p class="flex items-center gap-1.5 text-xs text-cinnabar"><Landmark :size="14"/>第 {{ relic.relicGeneration }}代 ·第 {{ relic.relicPoolRound +1 }}轮</p>
 <h2 class="brush-title mt-4 text-2xl text-ink group-hover:text-cinnabar">{{ relic.title }}</h2>
 <p class="mt-3 line-clamp-3 text-sm leading-6 text-ink/55">{{ relic.body }}</p>
 </div>
 <div class="mt-6 flex items-center justify-between border-t border-dashed ink-divider pt-4 text-xs text-ink/40">
 <span>{{ relic.agreeCount }}赞同</span>
 <span>{{ relic.createTime }}</span>
 </div>
 </router-link>
 <el-empty v-if="!loading && !relics.length" class="md:col-span-2" description="尚无残碑立于世间"/>
 </div>
 <el-dialog v-model="dialog" title="立一方残碑" width="min(92vw,620px)">
 <el-form label-position="top">
 <el-form-item label="所属地点">
 <el-select v-model="form.buildingId" placeholder="选择残碑所在之地" class="w-full">
 <el-option v-for="building in buildings" :key="building.id" :label="building.name" :value="building.id"/>
 </el-select>
 </el-form-item>
 <el-form-item label="碑题"><el-input v-model="form.title" maxlength="128" show-word-limit/></el-form-item>
 <el-form-item label="碑文"><el-input v-model="form.body" type="textarea" :rows="9" placeholder="写下一段未完成的旧事..."/></el-form-item>
 </el-form>
 <template #footer><el-button @click="dialog = false">收笔</el-button><el-button type="primary" :loading="submitting" @click="create">立碑</el-button></template>
 </el-dialog>
</template>
