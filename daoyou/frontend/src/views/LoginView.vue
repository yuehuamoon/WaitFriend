<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const store = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const submit = async () => {
 if (!form.username || !form.password) return ElMessage.warning('请输入用户名和密码')
 loading.value = true
 try {
 await store.login(form)
 router.push(route.query.redirect || '/')
 } finally { loading.value = false }
}
</script>
<template>
 <section class="mx-auto grid min-h-[calc(100vh-10rem)] max-w-5xl items-center gap-10 md:grid-cols-[1.1fr_.9fr]">
 <div class="relative overflow-hidden bg-pine px-8 py-16 text-rice md:px-14 md:py-24">
 <div class="absolute -right-16 -top-20 h-64 w-64 rounded-full border border-rice/15"></div>
 <div class="absolute -bottom-24 -left-20 h-72 w-72 rounded-full border border-rice/10"></div>
 <p class="mb-6 text-xs tracking-[.35em] text-rice/55">DA0 YOU</p>
 <h1 class="brush-title max-w-sm text-5xl leading-tight md:text-6xl">道友,<br/>请留步</h1>
 <p class="mt-7 max-w-xs text-sm leading-7 text-rice/65">一纸江湖，八方来客。登楼入世，与你共续未完的残篇。</p>
 </div>
 <div class="paper px-7 py-10 md:px-10">
 <p class="text-xs tracking-[.2em] text-cinnabar">入世</p>
 <h2 class="brush-title mt-3 text-3xl">故人归来</h2>
 <p class="mt-2 text-sm text-ink/50">以道号与密钥叩门。</p>
 <el-form class="mt-8" @submit.prevent="submit">
 <el-form-item label="道号"><el-input v-model="form.username" size="large" placeholder="输入用户名" /></el-form-item>
 <el-form-item label="密钥"><el-input v-model="form.password" size="large" type="password" show-password placeholder="输入密码" @keyup.enter="submit"/></el-form-item>
 <el-button type="primary" class="mt-3 w-full" size="large" :loading="loading" @click="submit">入世问道</el-button>
 </el-form>
 <p class="mt-6 text-center text-sm text-ink/55">尚未有榜名？<router-link to="/register" class="ml-1 text-cinnabar">初入江湖</router-link></p>
 </div>
 </section>
</template>
