<!-- 所有文章 -->
<template>
<div class="allBlog">
    <div class="card">
        <!-- 查询 -->
        <div class="query">
            <el-input
            clearable
            class="search"
            v-model="inputSearch"
            placeholder="请输入搜索内容..."
            @keyup.enter="searchFn"
            />
            <el-button type="primary" @click="searchFn" color="#6FB6C1" class="searchBtn" :icon="Search"></el-button>
        </div>
        <!-- 卡片 -->
        <div class="blog" v-for="blog in paginatedBlogs" :key="blog.BlogId">
            <div class="content">
                <h2 class="title">{{ blog.BlogTitle }}</h2>
                <p class="date">发布于 {{blog.createTime}} | {{ blog.name }}</p>
                <p class="summary">{{ ellipsis(blog.content) }}</p>
            </div>
        </div>
        <el-row class="pagination">
        <el-col>
            <el-pagination
            background
            :hide-on-single-page="false"
            v-model:current-page="currentPage"
            default-page-size = "15"
            :pager-count="7"
            layout="total, prev, pager, next, jumper"
            :total="searchedBlogs.length"
            @size-change="handleSizeChange"
            />
        </el-col>
        </el-row>
    </div>
    <div class="sidebar">
        <div class="myinfo">
            <img src="../../../public/avatar.jpg" alt="Avatar" class="avatar">
            <h3 class="nickname">小明同学</h3>
            <p class="articles-count">文章 {{ blogNum }}</p>
            <el-button type="primary" @click="goMyBlog" color="#6FB6C1" class="btn">查看文章</el-button>
        </div>
        <div class="comment">
            <p class="cmtTitle">我的评价</p>
            <el-scrollbar max-height="340px">
                <div class="myCmt" v-for="comment in evals" :key="comment.evaluationId">
                    <img src="../../../public/user.png" alt="未知" class="cmtAvatar">
                    <div class="BesideAvatar">
                        <p class="cmtName">小明同学的同学</p>
                        <p class="cmtContent">{{ cutCmt(comment.eval) }}</p>
                    </div>
                </div>
            </el-scrollbar>
        </div>
    </div>
</div>
</template>
<script setup>
import {ref,onMounted,computed} from 'vue';
import { Search } from '@element-plus/icons-vue'
import router from "~/router";
import request from '../../request/axios_config.js'

const blogs = ref([])
const evals = ref([])
const blogNum = ref()
onMounted(() => {
     // 发起请求获取当前页面信息
        updateTableData()
        updateComment()
})
const updateTableData = async () => {
    const res = await request.get('/blog/getBlogList')
    const res1 = await request.get('/blog/getMyBlogNumber')
    console.log(res.data.data)
    blogs.value = res.data.data
    blogNum.value = res1.data.data
}
const updateComment = async () => {
    const res = await request.get('/evaluation/getMyEvaluationList',)
    console.log(res.data.data)
    evals.value = res.data.data
}

// 超出部分显示省略号
const ellipsis = (value) => {
    if (value && value.length > 150) {
        return value.substring(0, 150) + '...[查看全文]';
    }
    return value;
};
const cutCmt = (value) => {
    if (value && value.length > 16) {
        return value.substring(0, 16) + '...';
    }
    return value;
};

// 搜索相关
const search = ref('')
const inputSearch = ref('')
const searchedBlogs = computed(() => blogs.value.filter(
  item =>
    !search.value ||
    item.BlogTitle.toLowerCase().includes(search.value.toLowerCase()) ||
    item.name.toLowerCase().includes(search.value.toLowerCase()) ||
    item.content.toLowerCase().includes(search.value.toLowerCase())
))
const searchFn = () => {
  search.value = inputSearch.value
}

// 分页相关
const currentPage = ref(1)
const paginatedBlogs = computed(() => searchedBlogs.value.slice((currentPage.value - 1) * 15, currentPage.value * 15))
const handleSizeChange = () => {
  currentPage.value = 1
}

// 查看我的博客
const goMyBlog = () => {
    router.push('my-blog')
}
</script>

<style scoped lang="scss">
.card{
    display: inline-block;
    vertical-align:top;
    width: 70%;
    margin-left: 1%;
    // height: 100%;
    // position: absolute;

    .query{
        margin-bottom: 10px;
        .search{
            width: 90%;
            height: 40px;
            margin-right: 2%;
        }
        .searchBtn{
            font-size: 20px;
            font-weight: bold;
            width: 7%;
            height: 40px;
            margin-right: 1%;
            color: white;
        }
    }
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
}
.myinfo{
    width: 100%;
    background-color: #fff;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    text-align: center;
    height: 300px;
    .avatar {
        margin-top: 30px;
        box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        border-radius: 50%;
        width: 100px;
        height: 100px;
    }
    .nickname {
        margin: 10px 0;
    }
    .articles-count {
        color: #666;
    }
    .btn {
        color: #fff;
        cursor: pointer;
    }
}
.comment{
    width: 100%;
    background-color: #fff;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    height: 400px;
    margin-bottom: 5px;
    .cmtTitle{
        font-size: 18px;
        font-weight: bold;
        text-align: left;
        margin-left: 10px;
        padding-top: 10px;
        font-family: 宋体;
    }
    .cmtAvatar{
        display: inline-block;
        vertical-align:middle;
        border-radius: 50%;
        width: 60px;
        height: 60px;
        margin-left: 10px;
    }
    .BesideAvatar{
        display: inline-block;
        vertical-align:middle;
        margin-left: 10px;
        .cmtName{
            font-size: 16px;
            font-weight: 500;
            font-family: 宋体;
        }
        .cmtContent{
            font-size: 12px;
            margin-top: -10px;
            color: #666;
        }
    }
}

</style>