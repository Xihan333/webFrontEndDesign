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
                        <el-descriptions-item label="学号">{{ userInfo.num }}</el-descriptions-item>
                        <el-descriptions-item label="性别">{{ userInfo.genderName ? userInfo.genderName:'钝角' }}</el-descriptions-item>
                        <el-descriptions-item label="班级">{{ userInfo.campusName }} · {{ userInfo.className }} · {{ userInfo.gradeName }}</el-descriptions-item>
                        <el-descriptions-item label="生日">{{ userInfo.birthday ? userInfo.birthday:'暂无' }}</el-descriptions-item>
                        <el-descriptions-item label="电话">{{ userInfo.phone ? userInfo.phone:'暂无' }}</el-descriptions-item>
                        <el-descriptions-item label="邮箱">{{ userInfo.email ? userInfo.email:'暂无' }}</el-descriptions-item>
                    </el-descriptions>
                </div>
            </div>
        </div>
        <div class="GPA">
            <p class="title">学期绩点</p>
            <div id="myChart" ref="EChart" style="width: 400px; height: 300px;" class="gpaChart"></div>
        </div>
        <div class="score">
            <p class="title">分数区间</p>
            <div id="scoreChart" ref="SChart" style="width: 400px; height: 250px;" class="scoreChart"></div>
        </div>
        <div class="achievement">
            <p class="title">成果奖励</p>
            <el-table border
                :data="AchievementTable" 
                style="width: 90%" class="achievementTable">
                <el-table-column prop="achievementName" label="成就名称" width="auto" align="center" />   
                <el-table-column prop="type" label="成就类别" width="auto" align="center"/> 
                <el-table-column prop="level" label="成就级别" width="auto" align="center"/> 
                <!-- 时间 -->
                <el-table-column sortable prop="time" label="时间" width="auto" align="center"/>   
                <el-table-column prop="content" label="内容" width="auto" align="center"/>   
                </el-table>
        </div>
        <div class="social">
            <p class="title">社会实践</p>
            <el-table border
                :data="SocialTable" 
                style="width: 90%" class="socialTable">
                <el-table-column prop="theme" label="实践主题" width="auto" />   
                <el-table-column prop="groupName" label="团队名称" width="auto" />   
                <el-table-column sortable prop="day" label="时间" width="auto" />   
                <el-table-column prop="location" label="实践地点" width="auto" />   
                <el-table-column prop="harvest" label="实践成果" width="auto" />   
            </el-table>
        </div>
    </div>
</template>

<script setup>
import {ref,onMounted,computed,inject} from 'vue';
import { useAppStore } from '../../stores/app.ts'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts';
import router from "~/router";       
import {getPhotoImageStr,uploadPhoto} from "~/services/infoServ";
import request from '../../request/axios_config.js'

const store = useAppStore()
const userInfo = ref({
    studentId:'',
    personId:'',
    name: '',
    gpa:'',
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
    className:'软工1班',
    gradeName:'大二',
    campusName:'软件学院',
    
})
const AchievementData = ref([])
const SocialData = ref([])
const mark = ref([])

onMounted(() =>{
    updateTableData()
})
const updateTableData = async () => {
    const info = await request.get('/student/getStudentIntroduceData')
    console.log(info.data.data)
    userInfo.value = info.data.data.info
    store.nameInfo = info.data.data
    console.log(store.nameInfo)
    userInfo.value.gpa = info.data.data.gpa
    AchievementData.value = info.data.data.achievementList
    SocialData.value = info.data.data.socialList
    mark.value = info.data.data.markList
    getRenderer()
}
const AchievementTable = computed(() => AchievementData.value.filter(
    item =>
        item.status===1
))
const SocialTable = computed(() => SocialData.value.filter(
    item =>
        item.auditStatus===1
))
const markList = computed(() => mark.value.filter(
    item =>
        item.value!=0
))

const gpa = ref()
const getRenderer = async () => {
    console.log(userInfo.value.gpa)
    const dom1 = document.getElementById('myChart');
    const dom2 = document.getElementById('scoreChart');
      const myChart = echarts.init(dom1); // 初始化echarts实例
      const scoreChart = echarts.init(dom2);
      const option1 = {
        series: [{
            color:["#6FB6C1","#e6e6e6"],
            type: 'pie',
            data: [
                {
                value: userInfo.value.gpa,
                name: 'GPA:'+ userInfo.value.gpa
                },
                {
                value: 5-userInfo.value.gpa,
                name: '满分5'
                },
            ],
            radius: '50%'
        }]
    };
    const option2 = {
        series: [{
        color:["#6FB6C1","#e6e6e6"],
        type: 'pie',
        data: markList.value,
        roseType: 'area'
        }
    ]};
    // 设置实例参数
    myChart.setOption(option1);
    scoreChart.setOption(option2);
}
// 上传图片
function select(){
    document.getElementById('upload').click();
}

const avatar=ref('')
avatar.value=localStorage.getItem('personId'+userInfo.value.personId)

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
    height: 180px;
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
                color: #8d8d8d;   
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
.achievement,.social{
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
    .achievementTable,.socialTable{
        width: 70%;
        margin-left: 20px;
        padding-bottom: 20px;
    }
}
.GPA{
    .title{
        padding-top: 10px;
        margin-left: 20px;
        font-weight: bold;
    }
    .gpaChart{
        margin-top: -75px;
    }
}
.score{
    .title{
        padding-top: 10px;
        margin-left: 20px;
        font-weight: bold;
    }
    .scoreChart{
        margin-top: -60px;
        margin-left: 20px;
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