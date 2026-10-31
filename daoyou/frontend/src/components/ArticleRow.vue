<script setup>
defineProps({ article: Object })
</script>
<template>
 <router-link :to="`/article/${article.id}`" class="paper block p-5 transition hover:border-pine/40">
 <div class="flex items-start justify-between gap-4"><div class="min-w-0"><p class="mb-2 text-xs text-ink/35">{{ article.createTime }}</p><h3 class="truncate text-lg text-ink">{{ article.title }}</h3><p class="mt-2 line-clamp-2 text-sm leading-6 text-ink/55">{{ article.body }}</p></div><div class="shrink-0 text-right text-xs text-ink/40"><p class="text-lg text-pine">{{ article.score }}</p>热度</div></div>
 </router-link>
</template>
