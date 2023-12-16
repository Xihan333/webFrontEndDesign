<!-- 查看课表 -->
<template>
    <table>
        <thead>
            <tr>
                <th>星期/节次</th>
                <th v-for="item in weeks" :key="item">
                    {{ item }}
                </th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="(order,orderIndex) in classOrders" :key="order">
                <td>
                    {{ order }}
                </td>
                <td v-for="(week,weekIndex) in weeks" :key="week">
                    <template v-for="item in courseList" :key="item">
                        <div v-if="item.day==(weekIndex+1)&&item.timeOrder==(orderIndex+1)">
                            {{ item.courseName }}
                        </div>
                    </template>
                </td>
            </tr>
        </tbody>
    </table>
</template>
<script setup>
import { onMounted } from 'vue';
import { useStudentStore, useCommonStore } from '~/stores/app'
import request from '../../request/axios_config.ts'

const commonStore=useCommonStore();
let courseList=[
    {
        'courseName':'数学',
        'teacherName':'皮卡丘',
        'courseNum':'001',
        'type':1,
        'credit':4,
        'place':'四区102',
        'day':1,
        'timeOrder':2
    }
]
onMounted(async()=>{
    commonStore.updateLoading(true);
    const res = await request.get('/course/getMyCourses')
    console.log('请看请求',res)
    if(res.data.code==200){
        courseList=res.data.data;
        commonStore.updateLoading(false);
    }
    else{
        commonStore.updateLoading(false);
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