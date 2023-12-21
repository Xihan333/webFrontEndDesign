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

        </div>
        <div class="social">

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
    name:'杨平',
    num:'2022030003',
    clazz:{
        clazzName:'软工1班',
        gradeName:'大二',
        campusName:'软件学院',
    }
})

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
</style>