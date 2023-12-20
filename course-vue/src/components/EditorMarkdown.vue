<!-- author: Mr.J -->
<!-- date: 2023-03-29 15:01:56 -->
<!-- description: Vue3+JS代码块模板 -->
<!-- author: Mr.J -->
<!-- date: 2023-03-29 15:01:56 -->
<!-- description: Vue3+JS代码块模板 -->
<template>
    <!-- :disabled-menus="[]"把禁用的title放在此数组中
         :include-level="[1,2,3,4,5,6]" 点击目录导航的层级
         @upload-image="handleUploadImage"图片上传
         @change="change"双向绑定效果
      -->
   <VueMarkdownEditor v-model="(modelValue)" :disabled-menus="[]" :include-level="[1,2,3,4,5,6]"
     @upload-image="handleUploadImage" @change="change" :height="height+'px'"></VueMarkdownEditor>
 </template>
 
 <script setup>
 import VueMarkdownEditor from "@kangc/v-md-editor";
 import "@kangc/v-md-editor/lib/style/base-editor.css";
 import vuepressTheme from "@kangc/v-md-editor/lib/theme/vuepress.js";
 import "@kangc/v-md-editor/lib/theme/style/vuepress.css";
 
 
 
 import Prism from "prismjs";
 import { getCurrentInstance } from "vue";
 const {proxy} = getCurrentInstance()
 VueMarkdownEditor.use(vuepressTheme, {
   Prism,
 });
 
 const props = defineProps({
   modelValue: {
     type: String,
     default: "",
   },
   height: {
     type: Number,
     default: 500,
   },
 });
 
 const emit = defineEmits()
 const change=(markdownContent,htmlContent)=>{
     emit('update:modelValue',markdownContent)
     emit('htmlContent',htmlContent)
 }
 
 const handleUploadImage = async(event, insertImage, files) => {
     console.log(files);
     // 这里做自定义图片上传
     let result = await proxy.Request({
         url:'/file/uploadImage',
         dataType:'file',
         params:{
             file:files[0],
             type:1,
         }
     })
     if (!result) {
         return
     }
     const url = proxy.globaInfo.imageUrl+ result.data.fileName
     insertImage({
         url:url,
         desc: '博客图片',
         // width: 'auto',
         // height: 'auto',
       });
 };
 </script>
 
 <style>
 </style>
 