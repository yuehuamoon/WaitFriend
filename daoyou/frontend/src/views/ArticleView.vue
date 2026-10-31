<script setup>
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bookmark, MessageCircle, PenLine, ThumbsUp } from 'lucide-vue-next'
import { articleApi, interactionApi, relicApi, userApi } from '../api'
import { useUserStore } from '../stores/user'

const route = useRoute()
const store = useUserStore()
const article = ref(null)
const author = ref(null)
const chats = ref([])
const relic = ref(null)
const chatText = ref('')
const sending = ref(false)
const segmentText = ref('')
const segmentSubmitting = ref(false)
const contentSaving = ref(false)
const editingContent = ref(null)
const editingText = ref('')
const relicContent = ref(null)
const relicPage = ref(1)
const relicPageSize =10
const load = async () => {
 article.value = await articleApi.get(route.params.id)
 author.value = await userApi.brief(article.value.authorUuid).catch(() => null)
 chats.value = await interactionApi.chats(route.params.id)
 if (article.value.type === 'relic') relic.value = await relicApi.detail(route.params.id, { page: relicPage.value, size: relicPageSize })
}
onMounted(load)
watch(relicPage, async (page) => {
 if (article.value?.type !== 'relic' || page === relic.value?.segmentPage) return
 relic.value = await relicApi.detail(route.params.id, { page, size: relicPageSize })
 await nextTick()
 relicContent.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
})
const relicPageCount = computed(() => Math.max(1, Math.ceil((relic.value?.contentTotal ||0) / relicPageSize)))
const agree = async () => { await interactionApi.agree(route.params.id); ElMessage.success('已赞同'); await load() }
const collect = async () => { await interactionApi.collect(route.params.id); ElMessage.success('已收藏'); await load() }
const sendChat = async () => {
 if (!store.token) return ElMessage.warning('请先入世')
 if (!chatText.value.trim()) return
 sending.value = true
 try { await interactionApi.chat(route.params.id, chatText.value.trim()); chatText.value=''; chats.value = await interactionApi.chats(route.params.id) } finally { sending.value=false }
}
const submitSegment = async () => {
 if (!store.token) return ElMessage.warning('请先入世')
 if (!segmentText.value.trim()) return ElMessage.warning('请填写续写内容')
 segmentSubmitting.value = true
 try {
 await relicApi.submit(route.params.id, segmentText.value.trim())
 segmentText.value = ''
 ElMessage.success('续写已放入续写池')
 relic.value = await relicApi.detail(route.params.id, { page: relicPage.value, size: relicPageSize })
 } finally { segmentSubmitting.value = false }
}
const likePool = async (id) => {
 if (!store.token) return ElMessage.warning('请先入世')
 await relicApi.like(id)
 ElMessage.success('已点赞')
 relic.value = await relicApi.detail(route.params.id, { page: relicPage.value, size: relicPageSize })
}
const startContentEdit = (item) => {
 editingContent.value = item
 editingText.value = item.content
}
const saveContent = async () => {
 if (!editingText.value.trim()) return ElMessage.warning('内容不能为空')
 contentSaving.value = true
 try {
 if (editingContent.value.kind === 'root') await relicApi.updateRoot(route.params.id, editingText.value)
 else await relicApi.updateSegment(route.params.id, editingContent.value.segmentId, editingText.value)
 editingContent.value = null
 ElMessage.success('残碑内容已更新')
 relic.value = await relicApi.detail(route.params.id, { page: relicPage.value, size: relicPageSize })
 } finally { contentSaving.value = false }
}
</script>
<template>
 <article v-if="article" class="mx-auto max-w-4xl">
 <header class="mb-8 border-b ink-divider pb-7">
 <p class="mb-3 text-xs tracking-[.18em] text-cinnabar">{{ article.type === 'relic' ? '残碑续写' : '山间留墨' }}</p>
 <h1 class="brush-title text-4xl leading-tight md:text-5xl">{{ article.title }}</h1>
 <div class="mt-5 flex flex-wrap items-center gap-4 text-sm text-ink/50">
 <router-link :to="`/user/${article.authorUuid}/stele`" class="flex items-center gap-2 hover:text-cinnabar"><el-avatar :size="32" :src="author?.avatar">{{ author?.username?.[0] || '道' }}</el-avatar>{{ author?.username || '无名道友' }}</router-link>
 <span>{{ article.createTime }}</span><span>热度 {{ article.score }}</span>
 </div>
 </header>
 <div class="grid gap-8 md:grid-cols-[1fr,230px]">
 <div>
 <div v-if="article.type !== 'relic'" class="paper whitespace-pre-wrap px-6 py-8 text-[17px] leading-9 text-ink/85 md:px-10">{{ article.body }}</div>
 <div v-else-if="relic" ref="relicContent" class="scroll-mt-24 paper px-6 py-8 md:px-10">
 <div class="space-y-10">
 <article v-for="item in relic.content" :key="item.kind + '-' + (item.segmentId || 'root')" class="text-[17px] leading-9 text-ink/75">
 <p class="whitespace-pre-wrap">{{ item.content }}</p>
 <div class="mt-4 flex items-center gap-4 text-xs"><router-link v-if="item.kind !== 'root'" :to="`/user/${item.userUuid}/stele`" class="text-cinnabar hover:text-pine">---{{ item.username || '无名道友' }}</router-link><span v-else class="text-ink/40">原碑文</span><button v-if="item.editable" class="text-pine hover:text-cinnabar" @click="startContentEdit(item)">编辑</button></div>
 </article>
 </div>
 <el-empty v-if="!relic.segments.length" description="碑上尚无内容" :image-size="60"/>
 <el-pagination v-if="relicPageCount >1" v-model:current-page="relicPage" class="mt-8 justify-end" layout="prev, pager, next" :page-size="relicPageSize" :total="relicPageCount * relicPageSize" :pager-count="7"/>
 </div>
 <section v-if="article.type === 'relic' && relic" class="mt-8 paper p-6">
 <div class="flex items-center justify-between"><h2 class="brush-title text-xl">续写池</h2><span class="text-xs text-ink/40">点赞最多的续写将合璧入碑</span></div>
 <div v-if="relic.pool.length" class="mt-5 space-y-4">
 <article v-for="item in relic.pool" :key="item.id" class="border-l-2 border-gold/50 pl-4">
 <p class="whitespace-pre-wrap text-sm leading-7 text-ink/75">{{ item.content }}</p>
 <div class="mt-2 flex items-center justify-between text-xs text-ink/45"><router-link :to="`/user/${item.userUuid}/stele`" class="hover:text-cinnabar">---{{ item.username || '无名道友' }}</router-link><button class="flex items-center gap-1 text-pine" @click="likePool(item.id)"><ThumbsUp :size="13"/>{{ item.likeCount }}</button></div>
 </article>
 </div>
 <el-empty v-else description="续写池尚空" :image-size="60"/>
 </section>
 <section class="mt-8 paper p-6">
 <h2 class="brush-title flex items-center gap-2 text-xl"><MessageCircle :size="19"/>论道</h2>
 <div class="mt-5 space-y-4">
 <div v-for="chat in chats" :key="chat.id"><p class="text-xs text-ink/40">{{ chat.username || '无名道友' }} · {{ chat.createTime }}</p><p class="mt-1 text-sm leading-6">{{ chat.content }}</p></div>
 <p v-if="!chats.length" class="text-sm text-ink/40">尚无人开言。</p>
 </div>
 <div class="mt-5 flex gap-2"><el-input v-model="chatText" maxlength="200" placeholder="留下你的见解" @keyup.enter="sendChat"/><el-button :loading="sending" @click="sendChat">发言</el-button></div>
 </section>
 <el-dialog :model-value="!!editingContent" title="编辑残碑内容" width="min(92vw,620px)" @close="editingContent = null">
 <el-input v-model="editingText" type="textarea" :rows="8" maxlength="500" show-word-limit/>
 <template #footer><el-button @click="editingContent = null">取消</el-button><el-button type="primary" :loading="contentSaving" @click="saveContent">保存</el-button></template>
 </el-dialog>
 </div>
 <aside class="md:sticky md:top-24 md:self-start">
 <div v-if="article.type === 'relic'" class="paper mb-4 p-4">
 <h3 class="brush-title flex items-center gap-2 text-lg"><PenLine :size="17"/>续写</h3>
 <el-input v-model="segmentText" class="mt-3" type="textarea" :rows="6" maxlength="500" show-word-limit placeholder="续一段未完之意"/>
 <el-button class="mt-3 w-full" type="primary" :loading="segmentSubmitting" @click="submitSegment">投稿入池</el-button>
 </div>
 <div class="paper p-4"><el-button class="w-full" @click="agree"><ThumbsUp :size="16" class="mr-2"/>赞同 {{ article.agreeCount }}</el-button><el-button class="mt-2 w-full" @click="collect"><Bookmark :size="16" class="mr-2"/>收藏 {{ article.collectCount }}</el-button></div>
 </aside>
 </div>
 </article>
</template>
