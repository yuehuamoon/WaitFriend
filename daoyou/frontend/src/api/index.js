import http from './http'

export const userApi = {
 login: (data) => http.post('/user/login', data),
 register: (data) => http.post('/user/register', data),
 logout: () => http.post('/user/logout'),
 me: () => http.get('/user/me'),
 leaderboard: () => http.get('/user/leaderboard'),
 brief: (uuid) => http.get(`/user/${uuid}`)
}

export const buildingApi = {
 list: () => http.get('/map/buildings'),
 get: (id) => http.get(`/map/buildings/${id}`),
 create: (data) => http.post('/map/buildings', data),
 update: (id, data) => http.put(`/map/buildings/${id}`, data),
 remove: (id) => http.delete(`/map/buildings/${id}`)
}

export const articleApi = {
 list: (params) => http.get('/content/articles', { params }),
 get: (id) => http.get(`/content/articles/${id}`),
 create: (data) => http.post('/content/articles', data),
 update: (id, data) => http.put(`/content/articles/${id}`, data),
 remove: (id) => http.delete(`/content/articles/${id}`),
 search: (params) => http.get('/content/search', { params }),
 stele: (uuid) => http.get(`/content/user/${uuid}/stele`),
 userArticles: (uuid) => http.get(`/content/user/${uuid}/articles`)
}

export const interactionApi = {
 agree: (id) => http.post(`/interaction/agree/${id}`),
 collect: (id) => http.post(`/interaction/collect/${id}`),
 collections: () => http.get('/interaction/my/collections'),
 chats: (id) => http.get(`/interaction/chat/${id}`),
 chat: (id, content) => http.post(`/interaction/chat/${id}`, { content })
}

export const relicApi = {
 create: (data) => http.post('/rewrite/relic', data),
 list: () => http.get('/rewrite/relic/list'),
 detail: (id, params = {}) => http.get(`/rewrite/relic/${id}`, { params }),
 updateRoot: (id, content) => http.put(`/rewrite/relic/${id}/content`, { content }),
 updateSegment: (id, segmentId, content) => http.put(`/rewrite/relic/${id}/content/${segmentId}`, { content }),
 submit: (id, content) => http.post(`/rewrite/relic/${id}/segment`, { content }),
 like: (id) => http.post(`/rewrite/relic/pool/${id}/like`),
 destroyVote: (id) => http.post(`/rewrite/relic/${id}/destroy-vote`)
}

export const notificationApi = {
 list: (unreadOnly = false) => http.get('/notification/list', { params: { unreadOnly } }),
 read: (id) => http.post(`/notification/${id}/read`),
 unreadCount: () => http.get('/notification/unread-count')
}

export const adminApi = {
 ban: (uuid) => http.post(`/admin/user/${uuid}/ban`),
 unban: (uuid) => http.post(`/admin/user/${uuid}/unban`),
 cultivation: (uuid, delta) => http.post(`/admin/user/${uuid}/cultivation`, { delta }),
 removeArticle: (id) => http.delete(`/admin/article/${id}`),
 logs: (params) => http.get('/admin/logs', { params })
}
