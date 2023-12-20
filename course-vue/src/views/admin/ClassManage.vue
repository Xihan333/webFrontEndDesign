<!-- 班级管理 -->
<template>
    <div class="class-manage">
        <div>
        <ClassManageDialog v-model:show="show" :rowData="currentRowData" :dialogMode="dialogMode" @updateTable="updateTableData" />
        <el-button @click="handleAdd" color="#6FB6C1" class="add">新 增</el-button>
        <div class="select">
            <el-select v-model="campus" value-key="id"
                       placeholder="请选择学院" clearable @change="changeSelect">
                <el-option
                   v-for="item in campusType"
                   :key="item.id"
                   :label="item.label"
                   :value="item.value"
                   />
            </el-select>
        </div>
        <div class="select">
            <el-select v-model="grade" value-key="id"
                       placeholder="请选择年级" clearable @change="changeSelect">
                <el-option
                   v-for="item in gradeType"
                   :key="item.id"
                   :label="item.label"
                   :value="item.value"
                   />
            </el-select>
        </div>
        <el-button class="query" type="primary" size="default" @click="fetchDate(campus,grade)">查 询</el-button>

        <div class="query">
            <el-input
                clearable
                class="search"
                v-model="inputSearch"
                placeholder="搜索主题和团队名称..."
                @keyup.enter="searchFn"
            />
            <el-button type="primary" @click="searchFn" color="#6FB6C1" class="searchBtn">查 询</el-button>
        </div>
    </div>
    <el-table
     :data="paginatedTableData"
     style="width: 100%">
        <el-table-column prop="clazzName" label="班级名称" width="200" />
        <el-table-column prop="gradeName" label="年级" width="300" />
        <el-table-column prop="campusName" label="学院" width="250" />
        <el-table-column label="操作" width="180" >
        <!-- 操作部分，根据需要修改 -->
        <template #default="scope">
          <el-button size="default" @click="handleEdit(scope.row)">
            编辑
          </el-button>
          <el-button size="default" @click="handleDel(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
     </el-table>
     <el-row class="pagination">
      <el-col>
        <el-pagination
          background
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[15, 25, 35, 50]"
          :pager-count="7"
          layout="total, sizes, prev, pager, next, jumper"
          :total="searchedTableData.length"
          @size-change="handleSizeChange"
        />
      </el-col>
    </el-row>
</div>
</template>


<script setup>
import { ref,computed,onMounted } from 'vue'
//引入弹窗页面
import ClassManageDialog from '../../components/ClassManageDialog.vue'
import { ElMessage } from 'element-plus'
import request from '../../request/axios_config.js'
import { filterOption } from '../../assets/js/config.js'

const campus = ref('')
const grade = ref('')

const campusType = computed(() => filterOption.allCampuses)
const gradeType = computed(() => filterOption.allGrades) 

const tableData = ref([])
const filterTableData = ref([])


onMounted(async ()=> {
    updateTableData()
})

const updateTableData = async () => {
  const res = await request.post('/clazz/getClazzOptionItemListByGradeId',{
    data:{
      gradeId:1
    }
  })
  console.log(res.data.data)
  tableData.value = res.data.data
  filterTableData.value = res.data.data
}

const show = ref(false)

const search = ref('')
const inputSearch = ref('')

// filiterTableData.value = computed(() => tableData.value) 
const fetchDate = (campus, grade) => {
  const filtered = computed(() => tableData.value.filter(
        person =>
          (!campus&&!grade) ||
          (person.campusName.toLowerCase().includes(campus) && person.gradeName.toLowerCase().includes(grade)) ||
          (person.campusName.toLowerCase().includes(campus) && !grade) ||
          (person.gradeName.toLowerCase().includes(grade) && !campus)
    )) 
    console.log(filterTableData.value)
    filterTableData.value = filtered.value
}
const searchedTableData = computed(() => filterTableData.value.filter(
    item =>
    //空参的情况
    !search.value ||
    // 按照主题theme和团队名称groupName搜索，忽略大小写
    item.clazzName.toLowerCase().includes(search.value.toLowerCase())
))

const searchFn = () => {
  // 点击查询按钮后才开始查询
  search.value = inputSearch.value
}

const currentPage = ref(1)
const pageSize = ref(15)
const paginatedTableData = computed(() => searchedTableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
const handleSizeChange = () => {
  currentPage.value = 1
}

const currentRowData = ref({})
const dialogMode = ref('') // 'add' 或 'view'
const handleAdd = () => {
  currentRowData.value = {}  // 清空数据
  dialogMode.value = 'add'
  show.value = true
}

//查看
const handleEdit = (rowData) => {
  console.log(rowData)
  currentRowData.value = rowData
  dialogMode.value = 'view'
  show.value = true
}

async function handleDel(rowData)  {
  const res = await request.post('/clazz/clazzDelete',{
    data:{
      clazzId: rowData.clazzId
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

const changeSelect = computed(() => {
  console.log(campus.value)
  console.log(grade.value)
})

// const tableData = computed(() => filiterTableData.value)




</script>