<script setup>
defineProps({ eyebrow: String, title: String, description: String })
</script>
<template>
 <div class="mb-7 flex flex-col gap-3 border-b ink-divider pb-5 md:flex-row md:items-end md:justify-between">
 <div>
 <p v-if="eyebrow" class="mb-2 text-xs uppercase tracking-[.18em] text-cinnabar">{{ eyebrow }}</p>
 <h1 class="brush-title text-3xl text-ink md:text-4xl">{{ title }}</h1>
 <p v-if="description" class="mt-2 max-w-2xl text-sm leading-6 text-ink/60">{{ description }}</p>
 </div>
 <slot/>
 </div>
</template>
