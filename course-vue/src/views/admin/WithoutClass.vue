<template>
    <div class="without-class">
        <div>
          <div class="query">
            <el-input
              clearable
              class="search"
              v-model="inputSearch"
              placeholder="搜索姓名或学号..."
              @keyup.enter="searchFn"
            />
          <el-button type="primary" @click="searchFn" color="#6FB6C1" class="searchBtn">查 询</el-button>
          </div>
        </div>
        <el-table border
          :data="paginatedTableData"
          style="width: 100%"
        >
            <el-table-column prop="num" label="学号" width="auto"/>
            <el-table-column prop="name" label="姓名" width="auto"/>
            <el-table-column prop="genderName" label="性别" width="auto"/>
            <el-table-column prop="card" label="身份证号" width="auto"/>
            <el-table-column prop="birthday" label="出生日期" width="auto"/>
            <el-table-column prop="phone" label="电话号码" width="auto"/>
            <el-table-column label="操作" width="auto">
              <template #default="scope">
                <el-button size="default" @click="handleEdit(scope.row)">
                  编辑
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
      title="添加班级"
      width="600px"
      :before-close="handleClose"
      >
      <div class="dialogContent">
        <div class="clazzName">
          <p>选择班级</p>
          <el-select v-model="clazzId" value-key="id"
          placeholder="请选择班级" clearable @change="changeSelect">
          <el-option
          v-for="item in clazzData"
            :key="item.clazzId"
            :label="item.gradeName+' '+item.clazzName"
            :value="item.clazzId"
          />    
        </el-select>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="confirm">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref,computed,onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../request/axios_config.js'

const tableData = ref([])
const clazzData = ref([])
onMounted(() => {
  // 发起请求获取当前表格数据
  updateTableData()
})

const updateTableData = async () => {
  const res = await request.get('/clazz/getNoClazzStudents')
  tableData.value = res.data.data
  const res1 = await request.post('/clazz/getClazzOptionItemListByGradeId',{
    data:{
      gradeId:''
    }
  })
  clazzData.value = res1.data.data
}

const dialogVisible = ref(false);
const clazzId=ref();
const studentId=ref();
function handleEdit(row){
  dialogVisible.value=true;
  studentId.value=row.studentId;
  console.log(row.studentId)
}

async function confirm(){
  const res = await request.post('/clazz/setClass',{
    data:{
      clazzId:clazzId.value,
      studentId:studentId.value,
    }
  })
  console.log('请看请求',res.data)
  if(res.data.code==200){
    dialogVisible.value=false;
    updateTableData()
  }
  else{
    ElMessage({
      message: '加载失败，请重试！',
      type:'error',
      offset: 150
    })
  }
  console.log('hhhhhhhhh',clazzId.value)
}

// 右上角搜索框查询后的数据内容
const search = ref('')
const inputSearch = ref('')
// 根据搜索框内容搜索, 空参搜索的话相当于显示所有
const searchedTableData = computed(() => tableData.value.filter(
  //这个item相当于给每一列一个命名，可以不改
  item =>
    //空参的情况
    !search.value ||
    // 按照主题theme和团队名称groupName搜索，忽略大小写
    item.theme.toLowerCase().includes(search.value.toLowerCase()) ||
    item.groupName.toLowerCase().includes(search.value.toLowerCase())
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

</script>

<style lang="scss" scoped>
el-table{
  text-align: center;
}

.query{
  float: right;
  border-right: 0px;
  margin-bottom: 10px;
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

</style>