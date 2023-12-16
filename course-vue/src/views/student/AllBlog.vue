<!-- 所有文章 -->
<template>
<div class="allBlog">
    <div class="card">
        <div class="blog" v-for="blog in blogs" :key="blog.BlogId">
            <div class="content">
                <h2 class="title">{{ blog.BlogTitle }}</h2>
                <p class="date">发布于 {{blog.createTime}} | {{ blog.name }}</p>
                <p class="summary">{{ ellipsis(blog.content) }}</p>
            </div>
        </div>
    </div>
    <div class="sidebar">
        <div class="myinfo">
            <img src="path-to-avatar.jpg" alt="Avatar" class="avatar">
            <h3 class="nickname">小明同学</h3>
            <p class="articles-count">文章 233</p>
            <button class="btn">查看文章</button>
        </div>
        <div class="comment">
            我的评论
        </div>
    </div>
</div>
</template>
<script setup>
import {ref,onMounted} from 'vue';
import request from '../../request/axios_config.ts'

const blogs = ref([
        {
            "createTime": "2023-12-03 21:15:32",
            "num": "202200300095",
            "BlogId": 2,
            "name": "黄婉姗",
            "BlogTag": "Sample Tag",
            "updateTime": "2023-12-03 21:15:48",
            "personId": 17,
            "BlogTitle": "Sample Title",
            "content": "如果您正在使用 Vue 2，您可能会创建一个过滤器，但是在 Vue 3 中，您应该使用计算属性或方法来达到同样的效果。上面的代码是一个方法的例子，直接在模板中被调用，用来截断文本并添加省略号。如果您需要在多个地方使用这个逻辑，您可能会考虑使用一个全局的方法或者将这个逻辑放在一个 mixin 中。如果您正在使用 Vue 2，您可能会创建一个过滤器，但是在 Vue 3 中，您应该使用计算属性或方法来达到同样的效果。上面的代码是一个方法的例子，直接在模板中被"
        },
        {
            "createTime": "2023-12-03 22:33:39",
            "num": "202200300219",
            "BlogId": 3,
            "name": "许新鑫",
            "BlogTag": "Sample Tag",
            "updateTime": null,
            "personId": 16,
            "BlogTitle": "Sample Title",
            "content": "Sample Content"
        },
        {
            "createTime": "2023-12-03 22:33:42",
            "num": "202200300219",
            "BlogId": 4,
            "name": "许新鑫",
            "BlogTag": "Sample Tag",
            "updateTime": null,
            "personId": 16,
            "BlogTitle": "Sample Title",
            "content": "Sample Content"
        }
    ])
// onMounted(() => {
//     // 发起请求获取当前表格数据
//     updateTableData()
// })
// const updateTableData = async () => {
//     const res = await request.get('/blog/getBlogList',{
//     })
//     console.log(res.data.data)
//     blogs.value = res.data.data
// }

const ellipsis = (value) => {
    if (value && value.length > 150) {
        return value.substring(0, 150) + '...[查看全文]';
    }
    return value;
};
</script>

<style scoped lang="scss">
.card{
    display: inline-block;
    vertical-align:top;
    width: 70%;
    margin-left: 1%;
    // height: 100%;
    // position: absolute;

    .blog{
        background: #fff;
        width: 100%;
        height: 180px;
        box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        margin-bottom: 20px;
        overflow: hidden;

        .content{
            margin: auto 0;
        }
        .title {
            margin: 10px 0;
            font-size: 20px;
            font-weight: 700;
            margin-left: 20px;
        }

        .date {
            font-size: 12px;
            color: #9d9d9d;
            margin-left: 20px;
        }

        .summary {
            font-size: 14px;
            line-height: 25px;
            color: #333;
            margin-left: 20px;
            margin-right: 80px;
        }
    }
}
.sidebar{
    display: inline-block;
    vertical-align:top;
    width: 27%;
    margin-left: 1%;
    background-color: bisque;
}
.myinfo{
    background-color: beige;
    width: 100%;
}
.comment{
    background-color: black;
    width: 100%;
}

</style>