<template>
    <div class="homepage">
        <div class="head">
            <el-image style="width: 120px; height: 160px" src="url" fit="cover" class="photo" />
            <div class="info">
                <h2 class="name">{{ userInfo.name }}</h2>
                <p class="clazz">{{ userInfo.clazz.campusName }} · {{ userInfo.clazz.clazzName }} · {{ userInfo.clazz.gradeName }}</p>
                
            </div>
        </div>
        <div class="GPA">
            这里放绩点的饼状图
            <div id="gpachart" ref="gpa" class="gpaChart" ></div>
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
import router from "~/router";
import * as echarts from 'echarts';
import request from '../../request/axios_config.js'

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
    introduce: '',
    clazzName:'软工1班',
    gradeName:'大二',
    campusName:'软件学院',
    
})
const AchievementData = ref([])
const SocialData = ref([])

onMounted(() =>{
    updateTableData()
})
const updateTableData = async () => {
    const info = await request.get('/student/getMyInfo')
    const achievement = await request.get('/achievement/getStudentAchievement')
    const social = await request.get('/social/getStudentSocial')
    userInfo.value = info.data.data
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
    .info{
        display: inline-block;
        vertical-align:middle;
        margin-left: 10px;
        width: 70%;
        .name{
            float: left;
            margin-right: 20px;
        }
        .clazz{
            font-size: 14px;
            color: rgb(141, 141, 141);
            
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