<!-- 班级管理 -->
<template>
    <!-- <div class="class-manage"> -->
        <div>
        <el-button size="default" @click="handleAdd()" color="#6FB6C1">新 增</el-button>
        
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
    <el-table border
     :data="paginatedTableData"
     style="width: 100%">
        <el-table-column prop="clazzName" label="班级名称" width="350" />
        <el-table-column prop="gradeName" label="年级" width="400" />
        <el-table-column prop="campusName" label="学院" width="500" />
        <el-table-column label="操作" width="350" >
        <!-- 操作部分，根据需要修改 -->
        <template #default="scope">
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
    <el-dialog
      v-model="dialogVisible"
      title="新增班级"
      width="500px"
      :before-close="handleClose"
    >
      <div class="dialogContent">
        <div class="clazzName">
          <p>班级名称</p>
          <el-input
           class="input"
           v-model="clazzName"
           />
        </div>
        <div class="campus">
          <p>学院</p>
          <el-input
           class="input"
           v-model="campus"
           />
        </div>
        <div class="grade">
          <p>年级</p>
          <el-input
           class="input"
           v-model="grade"
           />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="confirm">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  <!-- </div> -->
</template>


<script setup>
import { ref,computed,onMounted } from 'vue'
//引入弹窗页面
import { ElMessage } from 'element-plus'
import request from '../../request/axios_config.js'
import { filterOption } from '../../assets/js/config.js'

const campus = ref('')
const grade = ref('')
const clazzId=ref();

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

const dialogVisible = ref(false);
const clazzName=ref('');


function handleAdd(){
  dialogVisible.value=true;
  clazzName.value='';
  campus.value='';
  grade.value='';
  clazzId='';
}

//查看
// const handleEdit = (rowData) => {
//   console.log(rowData)
//   currentRowData.value = rowData
//   dialogMode.value = 'view'
//   show.value = true
// }

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

async function confirm(){
  let form=new Map();
  form.set('clazzName',clazzName.value);
  form.set('campus',campus.value);
  form.set('grade',grade.value);
  const h=Object.fromEntries(form);
  // console.log(clazzId)
  const res = await request.post('/clazz/clazzEditSave',{
    data:{
      clazzId:clazzId.value,
      form:h,
    },
  })
  console.log('请看请求',res)
  if(res.data.code==200){
    dialogVisible.value=false;
  }
  else{
    ElMessage({
      message: '加载失败，请重试！',
      type:'error',
      offset: 150
    })
  }
}


// const changeSelect = computed(() => {
//   console.log(campus.value)
//   console.log(grade.value)
// })

// const tableData = computed(() => filiterTableData.value)
</script>

<style lang="scss" scoped>
el-table{
  text-align: center;
}

.query{
  float: right;
  border-right: 0px;
  .search{
    border-color: #6FB6C1;
    margin-left: auto;
    margin-right: 10px;
    width: 200px;
  }
  .searchBtn{
    width: 70px;
    margin-right: 10px;
    color: white;
  }
}
.pagination{
  margin-top: 10px;
}

.dialogContent{
  .clazzName,.campus,.grade{
    display: flex;
    align-items: center;
    margin-bottom: 30px;
    p{
      margin: 1px;
      margin-right: 30px;
    }
    .input{
      width: 200px;
    }
  }
}

</style>