<template>
    <div class="homepage">
        <div class="head">
            <div class="uploader">
                <img v-if="avatar" :src="avatar" @click="select" class="photo-upload" />
                <el-icon v-else class="avatar-uploader-icon" @click="select" ><Plus /></el-icon>
                <input @change="upload" id="upload" type="file" accept="image/*" style="display:none" class="photo-upload"/>
            </div>
            <div class="base-info">
                <div class="firstLine">
                    <h2 class="name">{{ userInfo.name }}</h2>
                    <p class="time">上次登录：{{ userInfo.lastLoginTime ? userInfo.lastLoginTime:'暂无数据' }}</p> 
                </div>
                <div class="user-info">
                    <el-descriptions
                        class="margin-top"
                        :column="3"
                        size="Default"
                        :style="blockMargin"
                    >
                        <el-descriptions-item label="工号">{{ userInfo.num }}</el-descriptions-item>
                        <el-descriptions-item label="性别">{{ userInfo.genderName ? userInfo.genderName:'保密' }}</el-descriptions-item>
                        <el-descriptions-item label="职称">{{ userInfo.title ? userInfo.title:'暂无' }} </el-descriptions-item>
                        <el-descriptions-item label="生日">{{ userInfo.birthday ? userInfo.birthday:'暂无' }}</el-descriptions-item>
                        <el-descriptions-item label="电话">{{ userInfo.phone ? userInfo.phone:'暂无' }}</el-descriptions-item>
                        <el-descriptions-item label="邮箱">{{ userInfo.email ? userInfo.email:'暂无' }}</el-descriptions-item>
                        <el-descriptions-item label="研究方向">{{ userInfo.direction ? userInfo.direction:'暂无' }}</el-descriptions-item>
                        <el-descriptions-item label="地址">{{ userInfo.address ? userInfo.address:'暂无' }}</el-descriptions-item>
                    </el-descriptions>
                </div>
            </div>
        </div>
        <div class="paper">
            <p class="title">发表论文</p>
            <el-table border
                :data="PaperData"
                style="width: 80%" class="paperTable">
                <el-table-column prop="paperName" label="论文名称" width="auto" />
                <el-table-column prop="authors" label="论文作者" width="auto" />
                <el-table-column prop="day" label="发表时间" width="auto" />
            </el-table>
        </div>
    </div>
</template>

<script setup>
import {ref,onMounted,computed,inject} from 'vue';
import { useAppStore } from '../../stores/app.ts'
import router from "~/router";
import * as echarts from 'echarts';           
import request from '../../request/axios_config.js'

const store = useAppStore()
const userInfo = ref({
    studentId:'',
    personId:'',
    name: '',
    num: '',
    gender:'',
    genderName: '',
    dept: '',
    birthday: '',
    card: '',
    email: '',
    phone: '',
    lastLoginTime:'',
    introduce: '',
    degree:'',
    title:'',
    direction:''
    
})
const PaperData = ref([])

onMounted(() =>{
    updateTableData()
})
const updateTableData = async () => {
    const info = await request.get('/teacher/getMyInfo')
    const paper = await request.post('/scientificPayoffs/getTeacherScientificPayoffs',{
        data:{}
    })
    userInfo.value = info.data.data
    console.log(userInfo.value)
    store.nameInfo = info.data.data
    PaperData.value = paper.data.data
    avatar.value=localStorage.getItem('personId'+userInfo.value.personId)
}

// 上传图片
function select(){
    document.getElementById('upload').click();
}

const avatar=ref('')

function upload(e){
    let file=e.target.files[0];
    let reader=new FileReader();
    reader.readAsDataURL(file);
    reader.onload=(data)=>{
        avatar.value=data.target.result;
        localStorage.setItem('personId'+userInfo.value.personId,data.target.result) //键值对,键名为用户唯一标识
    }
}

</script>

<style scoped lang="scss">
.head{
    background-color: white;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    width: 90%;
    height: 200px;
    margin-top: 10px;
    margin-left: 10px;
    margin-bottom: 10px;

    .photo{
        margin-top: 10px;
        margin-left: 10px;
        display: inline-block;
        vertical-align:middle;
    }
    .base-info{
        display: inline-block;
        vertical-align:middle;
        margin-left: 30px;
        width: 70%;
        .firstLine{
            margin-bottom: 10px;
            .name{
                float: left;
                margin-right: 20px;
            }
            .time{
                font-size: 14px;
                padding-top: 25px;
                color: rgb(141, 141, 141);   
            }
        }
        
    }
}
.GPA{
    width: 44%;
    height: 250px;
    margin-top: 10px;
    margin-left: 10px;
    margin-bottom: 10px;
    display: inline-block;
    vertical-align:top;
    background-color: white;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
.score{
    width: 44%;
    height: 250px;
    margin-top: 10px;
    margin-left: 2%;
    margin-bottom: 10px;
    display: inline-block;
    vertical-align:top;
    background-color: white;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}
.paper{
    width: 90%;
    margin-top: 10px;
    margin-left: 10px;
    margin-bottom: 10px;
    background-color: white;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    .title{
        padding-top: 20px;
        margin-left: 20px;
        font-weight: bold;
    }
    .paperTable{
        width: 70%;
        margin-left: 20px;
        padding-bottom: 20px;
    }
}
.uploader{
    border: 1px dashed #bdbdbd;
    border-radius: 6px;
    margin-top: 10px;
    margin-left: 10px;
    width: 120px;
    height: 160px;
    display: inline-block;
    vertical-align:middle;
}
.uploader .photo-upload {
    border-radius: 6px;

    width: 120px;
    height: 160px;
    cursor: pointer;
    transition: var(--el-transition-duration-fast);
}

.uploader .photo-upload:hover {
  border-color: #6FB6C1;
}
.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 160px;
  text-align: center;
}
</style>
