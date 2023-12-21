<!-- 查看课表 -->
<template>
    <div class="content">
        <ul class="legend">
            <li>
                <span class="necessary"></span>
                必修
            </li>
            <li>
                <span class="limit"></span>
                限选
            </li>
            <li>
                <span class="random"></span>
                任选
            </li>
        </ul>
        <table class="schedule">
            <thead>
                <tr>
                    <td>星期/节次</td>
                    <td v-for="item in weeks" :key="item">
                        {{ item }}
                    </td>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(order,orderIndex) in classOrders" :key="order">
                    <td class="column0">
                        {{ order }}
                    </td>
                    <td class="columnOther" v-for="(week,weekIndex) in weeks" :key="week">
                        <template v-for="item in courseList" :key="item">
                            <el-popover
                                class="popover"
                                placement="right-start"
                                :width="200"
                                trigger="hover"
                            >
                                <h3>{{ item.courseName }}</h3>
                                <hr/>
                                <p>学分：{{ item.credit }}</p>
                                <p>学时：{{ item.hour }}</p>
                                <p>地点：{{ item.place }}</p>
                                <template #reference>
                                    <div 
                                        class="lesson"
                                        :class="{ 'necessary': item.type==0, 'limit': item.type==1, 'random': item.type==2 }" 
                                        v-if="item.day==(weekIndex+1)&&item.timeOrder==(orderIndex+1)"
                                    >
                                        <p class="cName">{{ item.courseName }}</p>
                                        <p class="tName">教师：{{ item.teacherName }}</p>
                                    </div>
                                </template>
                            </el-popover>
                        </template>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useStudentStore, useCommonStore } from '~/stores/app'
import request from '../../request/axios_config.js'

const commonStore=useCommonStore();
let courseList=ref([
    {
        'courseName':'',
        'teacherName':'',
        'courseNum':'',
        'type':3,
        'credit':0,
        'place':'',
        'day':0,
        'timeOrder':0
    }
])
onMounted(async()=>{
    const res = await request.get('/course/getMyCourses')
    console.log('请看请求',res)
    if(res.data.code==200){
        courseList.value=res.data.data;
    }
    else{
        ElMessage({
            message: '加载失败，请重试！',
            type: 'error',
            offset: 150
        })
    }
})
let weeks=['星期一','星期二','星期三','星期四','星期五','星期六','星期日']
let classOrders=['第一节','第二节','第三节','第四节','第五节']
</script>

<style lang="scss" scoped>
.necessary{
    background-color: #ecd3d3;
}
.limit{
    background-color: #d3ecd3	;
}
.random{
    background-color: #d3e6ec	;
}
.el-popper{
    h3{
        margin: 5px;
    }
    p{
        margin: 5px;
    }
}
.content{
    margin: 0px auto 0px auto;
    width: fit-content;
    ul {
        display: flex;
        align-items: center;
        height: 20px;
        margin: 0px;
        margin-bottom: 1px;
        padding: 0px;
        li{
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 10px;
            color: #5f727f;
            letter-spacing: 0;
            line-height: 12px;
            margin-right: 15px;
            span{
                display: inline-block;
                width: 10px;
                height: 10px;
                border-radius: 10px;
                margin-right: 4px;
            }
        }
    }
    .schedule{
        border-collapse: collapse;
        color: rgb(87, 87, 87);
        font-size: 14px;
        thead td{
            height: 40px;
            background-color: #D3E9EC;
        }
        tbody{
            td{
                height: 110px;
                .lesson{
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    border-radius: 5px;
                    width: 100%;
                    height: 100%;
                    .cName{
                        margin: 0px;
                        font-weight: bold;
                        font-size: 15px;
                    }
                    .tName{
                        margin: 0px;
                        margin-top: 5px;
                        font-size: 13px;
                    }
                }
            }
            .column0{
                width: 80px;
            }
            .columnOther{
                width: 135px;
            }
        }
        td{
            border: 1px rgb(159, 159, 159) solid;
            text-align: center;
            padding: 2px;
        }
    }
}
</style>