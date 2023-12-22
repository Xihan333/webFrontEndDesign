<template>
    <div class="blogInfo">
        <h1 class="title">{{ blogInfo.BlogTitle }}</h1>
        <hr/><br/>
        <div class="authorBox">
            <p class="authorInfo">{{ blogInfo.name }} 于  {{ blogInfo.createTime }} 发布  <el-tag>{{ blogInfo.BlogTag }}</el-tag> </p>
        </div>
        <!-- 使用 v-html 指令安全地输出解析后的 HTML -->
        <v-md-preview :text="text" class="content"></v-md-preview>
    </div>
</template>

<script setup>
import {ref,computed} from 'vue'
import { useAppStore } from '../../stores/app.ts'
import { storeToRefs } from 'pinia'

const store = useAppStore()



const { blogInfo } = storeToRefs(store);
const text = blogInfo.value.content

// const blogInfo = ref({
//       createTime: "2023-12-17 01:38:51",
//       num: "2022030003",
//       BlogId: 5,
//       name: "杨平",
//       BlogTag: "Sample Tag",
//       updateTime: null,
//       personId: 2,
//       BlogTitle: "Sample Title",
//       content: "Sample Content"
//   })
</script>

<style scoped lang="scss">
    .blogInfo{
        margin-left: 2%;
        h1{
            margin-left: 2%;
        }
        hr{
            opacity:0.3;
            width: 70%;
            float: left;
            margin-top: -5px;
            margin-left: 2%;
        }
    }
    .authorBox{
        width: 70%;
        height: 38px;
        margin-left: 2%;
        border-radius: 5px;
        margin-top: -30px;
        background-color: rgb(244, 243, 243);
        // box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        .authorInfo{
            padding-top: 10px;
            padding-left: 10px;
            color: rgb(122, 122, 122);
            font-size: 14px;

            el-tag{
                margin-left: 10px;
            }
        }
    }
    .content{
        width: 80%;
    }
    
</style>