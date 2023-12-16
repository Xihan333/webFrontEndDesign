<!-- 成果奖励 -->
<template>   
    <!-- <AchievementDialog v-model:show="show" :rowData="currentRowData" :dialogMode="dialogMode" @updateTable="updateTableData" /> --> 
    <div class="search">
        <div class="select">
            <div class="firstSearch">
                课程名/课序号
                <el-input
                    class="firstInput"
                    v-model="courseNameOrNum"
                    placeholder="请输入"
                />
            </div>
            <div class="secondSearch">
                上课教师
                <el-input
                    class="secondInput"
                    v-model="teacherName"
                    placeholder="请输入"
                />
            </div>
            <div>
                节次
                <el-select class="thirdInput" v-model="timeOrder" placeholder="请选择">
                <el-option
                    v-for="(item,index) in timeOrders"
                    :key="item"
                    :label="item"
                    :value="(index+1)"
                />
                </el-select>
            </div>
        </div>
        <div>
            <el-button type="primary" plain>重置</el-button>
            <el-button type="primary">查询</el-button>
        </div>
    </div>
    <el-table border :data="tabledata">
        <el-table-column prop="campusName" label="开设单位" width="150" />   
        <el-table-column prop="courseName" label="课程名称" width="120" /> 
        <el-table-column prop="courseNum" label="课序号" width="120" /> 
        <el-table-column prop="teacherName" label="上课教师" width="120" />
        <el-table-column prop="hour" label="学时" width="120" />  
        <el-table-column prop="credit" label="学分" width="120" /> 
        <el-table-column prop="type" label="课程类型" width="120" /> 
        <el-table-column prop="time" label="上课时间" width="120" /> 
        <el-table-column prop="place" label="上课地点" width="120" />  
        <el-table-column label="操作" width="180" >
        <!-- 操作部分，根据需要修改 -->
        <template #default="scope">
            <el-button size="default" @click="handleEdit(scope.row)">
                选课
            </el-button>
            <el-button size="default" @click="handleDel(scope.row)">
                退课
            </el-button>
        </template>
        </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-row class="pagination">
        <el-col>
        <el-pagination
            background
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[15, 25, 35, 50]"
            :pager-count="7"
            layout="total, sizes, prev, pager, next, jumper"
            :total="tableData.length"
            @size-change="handleSizeChange"
        />
        </el-col>
    </el-row>
</template>
    
<script setup>
import { ref,computed,onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../request/axios_config.js'

const tableData = ref([])
onMounted(() => {
    // 发起请求获取当前表格数据
    //updateTableData()
})
// const updateTableData = async () => {
//     const res = await request.get('/achievement/getStudentAchievement')
//     tableData.value = res.data.data
// }
// 弹窗的显示
const show = ref(false)

// 右上角搜索框查询后的数据内容
const search = ref('')
const inputSearch = ref('')
// 根据搜索框内容搜索, 空参搜索的话相当于显示所有
const searchedTableData = computed(() => tableData.value.filter(
    //这个item相当于给每一列一个命名，可以不改
    item =>
    //空参的情况
    !search.value ||
    // 按照名称搜索，忽略大小写
    item.achievementName.toLowerCase().includes(search.value.toLowerCase())
))

// 分页相关,不需要修改,直接复制
// 当前页数,默认设置为1
const currentPage = ref(1)
const pageSize = ref(15)
const paginatedTableData = computed(() => searchedTableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
const handleSizeChange = () => {
    currentPage.value = 1
}
</script>
    
<style lang="scss" scoped>
.search{
    display: flex;
    justify-content: space-between;
    margin-bottom: 6px;
    width: 100%;
    .select{
        display: flex;
        gap: 5px 15px;
        .firstInput{
            width: 150px;
        }
        .secondInput{
            width: 100px;
        }
        .thirdInput{
            width: 100px;
        }
    }
}
.pagination{
    margin-top: 10px;
}
</style>