import { createRouter, createWebHistory } from 'vue-router'

const routes = [
 { path: '/', name: 'leaderboard', component: () => import('../views/LeaderboardView.vue') },
 { path: '/login', name: 'login', component: () => import('../views/LoginView.vue'), meta: { guest: true } },
 { path: '/register', name: 'register', component: () => import('../views/RegisterView.vue'), meta: { guest: true } },
 { path: '/buildings', name: 'buildings', component: () => import('../views/BuildingsView.vue') },
 { path: '/relics', name: 'relics', component: () => import('../views/RelicsView.vue') },
 { path: '/building/:id', name: 'building', component: () => import('../views/BuildingView.vue') },
 { path: '/article/:id', name: 'article', component: () => import('../views/ArticleView.vue') },
 { path: '/user/:uuid/stele', name: 'stele', component: () => import('../views/SteleView.vue') },
 { path: '/me', name: 'me', component: () => import('../views/MeView.vue'), meta: { auth: true } },
 { path: '/admin', name: 'admin', component: () => import('../views/admin/AdminView.vue'), meta: { auth: true } }
]

const router = createRouter({ history: createWebHistory(), routes })
router.beforeEach((to) => {
 if (to.meta.auth && !localStorage.getItem('satoken')) return { name: 'login', query: { redirect: to.fullPath } }
 if (to.meta.guest && localStorage.getItem('satoken')) return { name: 'leaderboard' }
 return true
})

export default router
