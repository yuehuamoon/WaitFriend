<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Pencil, Trash2 } from 'lucide-vue-next'
import { articleApi, interactionApi, notificationApi, relicApi } from '../api'
import { useUserStore } from '../stores/user'
import PageHeader from '../components/PageHeader.vue'
import ArticleRow from '../components/ArticleRow.vue'

const store = useUserStore()
const collections = ref([])
const myArticles = ref([])
const notifications = ref([])
const tab = ref('articles')
const editorVisible = ref(false)
const saving = ref(false)
const editingArticle = ref(null)
const editForm = reactive({ title: '', body: '' })
const editableRelicContent = ref([])
onMounted(async () => {
 await store.fetchMe()
 await Promise.all([loadArticles(), loadCollections(), loadNotifications()])
})
const loadArticles = async () => { myArticles.value = await articleApi.userArticles(store.userInfo.uuid) }
const loadCollections = async () => { collections.value = await interactionApi.collections() }
const loadNotifications = async () => { notifications.value = await notificationApi.list(false) }
const openEditor = async (article) => {
 editingArticle.value = article
 editForm.title = article.title
 editForm.body = article.body
 editableRelicContent.value = []
 if (article.type === 'relic') {
 const detail = await relicApi.detail(article.id, { page:1, size:50 })
 const allContent = [...detail.content]
 const pageCount = Math.ceil(detail.contentTotal /50)
 for (let page =2; page <= pageCount; page +=1) {
 const next = await relicApi.detail(article.id, { page, size:50 })
 allContent.push(...next.content)
 }
 editableRelicContent.value = allContent.filter(item => item.editable)
 }
 editorVisible.value = true
}
const saveArticle = async () => {
 if (!editForm.title.trim()) return ElMessage.warning('请填写标题')
 if (editingArticle.value.type !== 'relic' && !editForm.body.trim()) return ElMessage.warning('请填写正文')
 saving.value = true
 try {
 if (editingArticle.value.type === 'relic') {
 await articleApi.update(editingArticle.value.id, { title: editForm.title, body: editForm.body, imgUrls: [] })
 for (const item of editableRelicContent.value) {
 if (item.kind === 'root') await relicApi.updateRoot(editingArticle.value.id, item.content)
 else await relicApi.updateSegment(editingArticle.value.id, item.segmentId, item.content)
 }
 } else {
 await articleApi.update(editingArticle.value.id, { title: editForm.title, body: editForm.body, imgUrls: [] })
 }
 editorVisible.value = false
 ElMessage.success('文章已更新')
 await loadArticles()
 } finally { saving.value = false }
}
const removeArticle = async (article) => {
 await ElMessageBox.confirm(`确定删除「${article.title}」？`, '删除文章', { type: 'warning' })
 await articleApi.remove(article.id)
 ElMessage.success('文章已删除')
 await loadArticles()
}
const readNotification = async (item) => {
 if (!item.readAt) { await notificationApi.read(item.id); item.readAt = new Date().toISOString() }
}
</script>
<template>
 <PageHeader eyebrow="洞府" title="我的江湖" description="此间收着你的修为、收藏与来信。">
 <div v-if="store.userInfo" class="paper px-6 py-4 text-right"><p class="text-2xl text-cinnabar">{{ Number(store.userInfo.cultivation).toFixed(1) }}</p><p class="text-xs tracking-widest text-ink/40">修为</p></div>
 </PageHeader>
 <el-tabs v-model="tab">
 <el-tab-pane label="我的创作" name="articles">
 <div class="grid gap-3">
 <div v-for="article in myArticles" :key="article.id" class="paper p-5">
 <router-link :to="`/article/${article.id}`" class="block transition hover:text-cinnabar"><p class="mb-2 flex items-center gap-2 text-xs text-ink/35"><span :class="article.type === 'relic' ? 'text-cinnabar' : 'text-pine'">{{ article.type === 'relic' ? '残碑' : '文章' }}</span><span>{{ article.createTime }}</span></p><h3 class="truncate text-lg text-ink">{{ article.title }}</h3><p class="mt-2 line-clamp-2 text-sm leading-6 text-ink/55">{{ article.body }}</p></router-link>
 <div class="mt-4 flex justify-end gap-2 border-t border-dashed ink-divider pt-3"><el-button link @click="openEditor(article)"><Pencil :size="14" class="mr-1"/>编辑</el-button><el-button link type="danger" @click="removeArticle(article)"><Trash2 :size="14" class="mr-1"/>删除</el-button></div>
 </div>
 <el-empty v-if="!myArticles.length" description="尚未落笔"/>
 </div>
 </el-tab-pane>
 <el-tab-pane label="我的收藏" name="collections">
 <div class="grid gap-3"><ArticleRow v-for="article in collections" :key="article.id" :article="article"/><el-empty v-if="!collections.length" description="尚无收藏"/></div>
 </el-tab-pane>
 <el-tab-pane label="来信" name="notifications">
 <div class="paper divide-y ink-divider">
 <button v-for="item in notifications" :key="item.id" class="block w-full p-5 text-left transition hover:bg-pine/[.03]" @click="readNotification(item)">
 <div class="flex items-center justify-between gap-3"><span class="text-sm text-cinnabar">{{ item.title }}</span><span class="shrink-0 text-xs text-ink/30">{{ item.createTime }}</span></div>
 <p class="mt-2 text-sm text-ink/60">{{ item.content }}</p>
 <span v-if="!item.readAt" class="mt-2 inline-block h-1.5 w-1.5 rounded-full bg-cinnabar"></span>
 </button>
 <el-empty v-if="!notifications.length" description="暂无来信"/>
 </div>
 </el-tab-pane>
 </el-tabs>
 <el-dialog v-model="editorVisible" :title="editingArticle?.type === 'relic' ? '编辑残碑' : '编辑文章'" width="min(92vw,620px)">
 <el-form label-position="top">
 <el-form-item label="标题"><el-input v-model="editForm.title" maxlength="128" show-word-limit/></el-form-item>
 <template v-if="editingArticle?.type === 'relic'">
 <el-form-item v-for="item in editableRelicContent" :key="item.kind + '-' + (item.segmentId || 'root')" :label="item.kind === 'root' ? '原碑文' : `第 ${item.seq}段`">
 <el-input v-model="item.content" type="textarea" :rows="5" maxlength="500" show-word-limit/>
 </el-form-item>
 <el-empty v-if="!editableRelicContent.length" description="没有可编辑的残碑段落" :image-size="60"/>
 </template>
 <el-form-item v-else label="正文"><el-input v-model="editForm.body" type="textarea" :rows="9"/></el-form-item>
 </el-form>
 <template #footer><el-button @click="editorVisible = false">取消</el-button><el-button type="primary" :loading="saving" @click="saveArticle">保存</el-button></template>
 </el-dialog>
</template>
