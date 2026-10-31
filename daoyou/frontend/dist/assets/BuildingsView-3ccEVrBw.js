import{c as l,o as d,a as s,b as i,d as a,l as t,F as o,r as _,k as u,i as m,g as h,w as x,u as c,e as k,t as n,A as g}from"./index-2vOYsDCc.js";import{_ as f}from"./PageHeader-1RdDGIY0.js";/**
 * @license lucide-vue-next v0.468.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const v=l("ArrowUpRightIcon",[["path",{d:"M7 7h10v10",key:"1tivn9"}],["path",{d:"M7 17 17 7",key:"1vkiza"}]]);/**
 * @license lucide-vue-next v0.468.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const y=l("MapPinIcon",[["path",{d:"M20 10c0 4.993-5.539 10.193-7.399 11.799a1 1 0 0 1-1.202 0C9.539 20.193 4 14.993 4 10a8 8 0 0 1 16 0",key:"1r0f0z"}],["circle",{cx:"12",cy:"10",r:"3",key:"ilqhr7"}]]),w={class:"grid gap-4 md:grid-cols-2"},b={class:"mb-3 flex items-center gap-1.5 text-xs text-cinnabar"},M={class:"brush-title text-2xl"},B={class:"mt-2 max-w-md text-sm leading-6 text-ink/55"},N={__name:"BuildingsView",setup(z){const r=u([]);return d(async()=>{r.value=await g.list()}),(A,V)=>{const p=m("router-link");return s(),i(o,null,[a(f,{eyebrow:"山河图",title:"择一处游历",description:"每一座建筑自有其气息，落笔成章，自有同道来寻。"}),t("div",w,[(s(!0),i(o,null,_(r.value,e=>(s(),h(p,{key:e.id,to:`/building/${e.id}`,class:"paper group flex min-h-40 items-end justify-between p-6 transition hover:-translate-y-0.5 hover:border-pine/40"},{default:x(()=>[t("div",null,[t("p",b,[a(c(y),{size:14}),k(n(e.region||"山河之间"),1)]),t("h2",M,n(e.name),1),t("p",B,n(e.description||"此处尚待道友留下笔墨。"),1)]),a(c(v),{class:"shrink-0 text-ink/25 transition group-hover:text-cinnabar",size:22})]),_:2},1032,["to"]))),128))])],64)}}};export{N as default};
