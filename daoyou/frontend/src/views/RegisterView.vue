<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const submit = async () => {
 if (!form.username || !form.password) return ElMessage.warning('请完整填写道号与密钥')
 loading.value = true
 try {
 await store.register(form)
 ElMessage.success('入世成功')
 router.push('/')
 } finally { loading.value = false }
}
</script>
<template>
 <section class="mx-auto max-w-xl paper my-10 px-7 py-10 md:px-12 md:py-14">
 <p class="text-xs tracking-[.2em] text-cinnabar">初入江湖</p>
 <h1 class="brush-title mt-3 text-4xl">立一方道号</h1>
 <p class="mt-3 text-sm leading-6 text-ink/55">道号不可重复，密码8至32位且须含字母与数字。</p>
 <el-form class="mt-8" @submit.prevent="submit">
 <el-form-item label="道号"><el-input v-model="form.username" size="large" placeholder="输入道号"/></el-form-item>
 <el-form-item label="密钥"><el-input v-model="form.password" size="large" type="password" show-password placeholder="至少8位，含字母与数字" @keyup.enter="submit"/></el-form-item>
 <el-button type="primary" class="mt-3 w-full" size="large" :loading="loading" @click="submit">落笔入世</el-button>
 </el-form>
 <p class="mt-6 text-center text-sm text-ink/55">已有道号？<router-link to="/login" class="ml-1 text-cinnabar">故人归来</router-link></p>
 </section>
</template>
