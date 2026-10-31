<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi, buildingApi } from '../../api'
import PageHeader from '../../components/PageHeader.vue'

const tab = ref('buildings')
const buildings = ref([])
const logs = ref([])
const logTotal = ref(0)
const dialog = ref(false)
const editingId = ref(null)
const form = reactive({ name: '', region: '', description: '', coverUrl: '', sortOrder:0 })
const loadBuildings = async () => { buildings.value = await buildingApi.list() }
const loadLogs = async () => { const page = await adminApi.logs({ page:1, size:30 }); logs.value = page.records; logTotal.value = page.total }
onMounted(async () => { await loadBuildings(); await loadLogs().catch(() => {}) })
const openCreate = () => { editingId.value = null; Object.assign(form, { name:'', region:'', description:'', coverUrl:'', sortOrder:0 }); dialog.value=true }
const edit = (building) => { editingId.value = building.id; Object.assign(form, building); dialog.value=true }
const save = async () => {
 if (!form.name.trim()) return ElMessage.warning('板块名称不能为空')
 if (editingId.value) await buildingApi.update(editingId.value, form)
 else await buildingApi.create(form)
 dialog.value=false; ElMessage.success('已保存'); await loadBuildings()
}
const remove = async (building) => {
 await ElMessageBox.confirm(`确定删除「${building.name}」？`, '删除板块', { type:'warning' })
 await buildingApi.remove(building.id); ElMessage.success('已删除'); await loadBuildings()
}
</script>
<template>
 <PageHeader eyebrow="执事堂" title="后台管理" description="维护山河板块、用户与操作留痕。">
 <el-button type="primary" @click="openCreate">新建板块</el-button>
 </PageHeader>
 <el-tabs v-model="tab">
 <el-tab-pane label="板块管理" name="buildings">
 <el-table :data="buildings" class="paper w-full">
 <el-table-column prop="id" label="ID" width="70"/><el-table-column prop="name" label="板块"/><el-table-column prop="region" label="地域"/><el-table-column prop="sortOrder" label="排序" width="80"/>
 <el-table-column label="操作" width="150"><template #default="{ row }"><el-button link @click="edit(row)">编辑</el-button><el-button link type="danger" @click="remove(row)">删除</el-button></template></el-table-column>
 </el-table>
 </el-tab-pane>
 <el-tab-pane label="操作日志" name="logs">
 <el-table :data="logs" class="paper w-full"><el-table-column prop="operation" label="操作" width="170"/><el-table-column prop="targetType" label="对象" width="100"/><el-table-column prop="targetId" label="对象ID"/><el-table-column prop="createTime" label="时间" width="180"/></el-table>
 </el-tab-pane>
 </el-tabs>
 <el-dialog v-model="dialog" :title="editingId ? '修改板块' : '新建板块'" width="min(92vw,520px)">
 <el-form label-position="top"><el-form-item label="名称"><el-input v-model="form.name"/></el-form-item><el-form-item label="地域"><el-input v-model="form.region"/></el-form-item><el-form-item label="简介"><el-input v-model="form.description" type="textarea" :rows="3"/></el-form-item><el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0"/></el-form-item></el-form>
 <template #footer><el-button @click="dialog=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
 </el-dialog>
</template>
