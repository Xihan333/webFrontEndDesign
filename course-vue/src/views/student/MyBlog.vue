<!-- 我的文章 -->
<template>
    <div class="my-blog">
        <div class="myInfo">
            <img src="../../../public/avatar.jpg" alt="Avatar" class="avatar">
            <div class="info">
                <h3 class="nickname">{{ name }}</h3>
                <p class="articles-count">文章   {{ blogNum }}</p>
            </div>
            <el-button type="primary" @click="goNewBlog" color="#6FB6C1" class="btn">写新文章</el-button>
        </div>
        <div class="card">
            <div class="blog" v-for="blog in paginatedBlogs" :key="blog.BlogId">
                <div class="content" @click="handleClickBlog(blog)">
                    <h2 class="title">{{ blog.BlogTitle }}</h2>
                    <p class="date">发布于 {{blog.createTime}} </p>
                    <p class="summary">{{ ellipsis(blog.digest,blog.content) }}</p>
                </div>
                <el-row class="operation">
                    <template #default="scope">
                        <el-button type="primary" :icon="Edit" circle @click="toBlogEdit(blog)"/>
                        <el-button type="danger" :icon="Delete" circle @click="handleDel(blog)" />
                    </template>
                </el-row>
            </div>
            <el-row class="pagination">
                <el-col>
                    <el-pagination
                    background
                    :hide-on-single-page = true
                    v-model:current-page ="currentPage"
                    default-page-size = "8"
                    :pager-count = "7"
                    layout="total, prev, pager, next, jumper"
                    :total="blogs.length"
                    @size-change="handleSizeChange"
                    />
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script setup>
import {ref,onMounted,computed} from 'vue';
import {Delete,Edit} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useAppStore } from '../../stores/app.ts'
import router from "~/router";
import request from '../../request/axios_config.js'

const store = useAppStore()
const blogs = ref([])
const blogNum = ref()
const name = ref('小明同学')
onMounted(() => {
     // 发起请求获取当前页面信息
        updateTableData()
})
const updateTableData = async () => {
    const res = await request.get('/blog/getMyBlogList')
    const res1 = await request.get('/blog/getMyBlogNumber')
    console.log(res.data.data)
    blogs.value = res.data.data
    blogNum.value = res1.data.data
}

// 超出部分显示省略号
const ellipsis = (value,content) => {
    if(!value)  value = content
    if (value && value.length > 150) {
        return value.substring(0, 150) + '...[查看全文]';
    }
    return value;
};

// 分页相关
const currentPage = ref(1)
const paginatedBlogs = computed(() => blogs.value.slice((currentPage.value - 1) * 8, currentPage.value * 8))
const handleSizeChange = () => {
  currentPage.value = 1
}

// 写新文章
const goNewBlog = () => {
    router.push('new-blog')
}

//编辑博客
const toBlogEdit = ({ blog }) => {
  store.personId = row.personId
  store.examRecord = true
  router.push({ path: ch('/管理员/报名信息/报名人信息') })
}
// 查看博客内容
const handleClickBlog = (blog) => {
  console.log(blog);
  store.blogInfo = blog
  router.push('blog-info')
}

//编辑博客
const handleEdit = (blog) => {
  console.log(blog)
}
async function handleDel(blog)  {
    console.log(blog)
  const res = await request.post('/blog/blogDelete',{
    data:{
      blogId: blog.BlogId
    } 
  })
  console.log(res.code)
  console.log(res)
  updateTableData()
  if(res.data.code==200){
     ElMessage({
       message: '删除成功！',
       type: 'success',
       offset: 150
     })
  }
  else{
    ElMessage({
      message: '删除失败，请重试！',
      type: 'error',
      offset: 150
    })
  }
}

</script>

<style scoped lang="scss">
.myInfo{   
    width: 98%;
    background-color: #fff;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    margin-left: 1%;
    margin-bottom: 1%;
    height: 100px;
    .avatar {
        margin: 1% 2%;
        box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        border-radius: 50%;
        width: 80px;
        height: 80px;
        display: inline-block;
        vertical-align:middle;
    }
    .info{
        display: inline-block;
        vertical-align:middle;
        text-align: center;
        .nickname {
            margin: 10px 0;
        }
        .articles-count {
            color: #666;
            font-size: 14px;
        }
    }
    .btn {
        display: inline-block;
        vertical-align:middle;
        color: #fff;
        margin-left: 70%;
        cursor: pointer;
    }
}
.card{
    .blog{
        background: #fff;
        width: 98%;
        margin-left: 1%;
        height: 180px;
        box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        margin-bottom: 20px;
        overflow: hidden;
        position: relative;
        .content{
            width: 70%;
            margin: auto 0;
            vertical-align:top;
            display: inline-block;
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
            // width: 60%;
        }
        .operation{
            position: absolute;
            margin-left: 10%;
            margin-top: 20px;
            vertical-align:top;
            display: inline-block;
        }
    }
}
.pagination{
    margin-left: 1%;
}
</style>