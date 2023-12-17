<template>
    <div class="blogInfo">
        <h2 class="title">{{ blogInfo.BlogTitle }}</h2>
        <hr/><br/>
        <div class="authorBox">
            <p class="authorInfo">{{ blogInfo.name }} 于 {{ blogInfo.createTime }} 发布</p>
        </div>
        <p class="content">{{ blogInfo.content }}</p>
    </div>
</template>

<script setup>
import { useAppStore } from '../../stores/app.ts'
import { storeToRefs } from 'pinia'

const store = useAppStore()
const { blogInfo } = storeToRefs(store);

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
        hr{
            opacity:0.3;
            width: 70%;
            float: left;
            margin-top: -5px;
        }
    }
    .authorBox{
        width: 70%;
        height: 35px;
        border-radius: 5px;
        margin-top: -30px;
        background-color: rgb(244, 243, 243);
        // box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        .authorInfo{
            padding-top: 10px;
            padding-left: 10px;
            color: rgb(122, 122, 122);
            font-size: 14px;
        }
    }
    
</style>