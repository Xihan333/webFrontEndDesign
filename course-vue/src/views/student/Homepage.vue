<template>
    <div class="homepage">
        <div class="head">
            <el-image style="width: 120px; height: 160px" src="url" fit="cover" class="photo" />
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
            <!-- <div id="gpachart" ref="gpa" class="gpaChart" ></div> -->
            <div id="myChart" ref="EChart" style="width: 300px; height: 300px;"></div>
        </div>
        <div class="score">
            这里放最高分的科目分数的树状图（拟五门）
        </div>
        <div class="achievement">
            <p class="title">成果奖励</p>
            <el-table border
                :data="AchievementTable" 
                style="width: 80%" class="achievementTable">
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
                style="width: 80%" class="socialTable">
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
import * as echarts from 'echarts';
import router from "~/router";       
import request from '../../request/axios_config.js'

const store = useAppStore()
const userInfo = ref({
    studentId:'',
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
    className:'软工1班',
    gradeName:'大二',
    campusName:'软件学院',
    
})
const AchievementData = ref([])
const SocialData = ref([])

onMounted(() =>{
    updateTableData()
    getRenderer()
})
const updateTableData = async () => {
    const info = await request.get('/student/getMyInfo')
    const achievement = await request.get('/achievement/getStudentAchievement')
    const social = await request.get('/social/getStudentSocial')
    userInfo.value = info.data.data
    store.nameInfo = info.data.data
    console.log(store.nameInfo)
    AchievementData.value = achievement.data.data
    SocialData.value = social.data.data
}
const AchievementTable = computed(() => AchievementData.value.filter(
    item =>
        item.status===1
))
const SocialTable = computed(() => SocialData.value.filter(
    item =>
        item.auditStatus===1
))

const gpa = ref()
const getRenderer = async () => {
    const dom = document.getElementById('myChart');
      const myChart = echarts.init(dom); // 初始化echarts实例
      const option = {
        xAxis: {
          type: 'category',
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: [820, 932, 901, 934, 1290, 1330, 1320],
            type: 'line',
            smooth: true
          }
        ]
      };
      // 设置实例参数
      myChart.setOption(option);
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
</style>