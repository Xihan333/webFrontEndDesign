<!-- 成绩管理 -->
<template>   
  <div class="search">
    <div class="select">
      <div>
        学号
        <el-input
          class="firstInput"
          v-model="studentNum"
          placeholder="请输入"
        />
      </div>
      <div>
        课程
        <el-select class="secondInput" v-model="course" placeholder="请选择">
          <el-option
            v-for="item in courses"
            :key="item"
            :label="item.courseNum+'-'+item.courseName"
            :value="item.courseId"
          />
        </el-select>
      </div>
    </div>
    <div>
      <el-button type="primary" plain @click="reset">重置</el-button>
      <el-button type="primary" @click="search">查询</el-button>
    </div>
  </div>
  <el-table border :data="filterTableData">
      <el-table-column prop="courseNum" label="课序号" width="auto" /> 
      <el-table-column prop="courseName" label="课程名称" width="auto" /> 
      <el-table-column prop="studentNum" label="学号" width="auto" />  
      <el-table-column prop="studentName" label="姓名" width="auto" /> 
      <el-table-column prop="clazzName" label="班级" width="auto" />
      <el-table-column prop="commonMark" label="平时成绩" width="auto" />
      <el-table-column prop="finalMark" label="期末成绩" width="auto" />
      <el-table-column label="操作" width="auto" >
        <template #default="scope">
          <el-button size="default" @click="edit(scope.row)">
            编辑
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
  <el-dialog
    v-model="dialogVisible"
    title="成绩编辑"
    width="300px"
    :before-close="handleClose"
  >
    <div class="dialogContent">
      <div class="commonMark">
        <p>平时成绩</p>
        <el-input
          class="input"
          v-model="commonMark"
        />
      </div>
      <div class="finalMark">
        <p>期末成绩</p>
        <el-input
          class="input"
          v-model="finalMark"
        />
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirm">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>
  
<script setup>
import { ref,computed,onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../request/axios_config.js'
import { useCommonStore } from '~/stores/app'

const commonStore=useCommonStore();
const userInfo=commonStore.userInfo;

const studentNum=ref("");
const course=ref(null);
let courses=ref([])
let timeOrders=['','第一节','第二节','第三节','第四节','第五节']
let types=['必修','限选','任选','']
let tableData = []
const filterTableData=ref([]);
onMounted(async() => {
  updateTableData();
  const res = await request.get('/course/getMyTeacherCourses')
  console.log('请看请求',res)
  if(res.data.code==200){
      courses.value=res.data.data;
  }
  else{
      ElMessage({
          message: '加载失败，请重试！',
          type: 'error',
          offset: 150
      })
  }
})

async function updateTableData(){
  const res = await request.get('/score/getTeacherCourseScores')
  if(res.data.code==200){
    tableData=res.data.data;
    filterTableData.value=tableData;
  }
  else{
    ElMessage({
      message: '加载失败，请重试！',
      type: 'error',
      offset: 150
    })
  }
}

function reset(){
  studentNum.value="";
  course.value=null;
}

function search(){
  // 筛选, 非模糊匹配
  filterTableData.value = tableData.filter((item) => {
    return (course.value==null||item.courseId==course.value)
            &&(studentNum.value==''||item.studentNum==studentNum.value);
  });
}

// 分页相关,不需要修改,直接复制
// 当前页数,默认设置为1
const currentPage = ref(1)
const pageSize = ref(15)
const paginatedTableData = computed(() => searchedTableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
const handleSizeChange = () => {
  currentPage.value = 1
}

//弹窗相关
const dialogVisible = ref(false);
const commonMark=ref(12);
const finalMark=ref(13);
const scoreId=ref();
function edit(row){
  dialogVisible.value=true;
  commonMark.value=row.commonMark;
  finalMark.value=row.finalMark;
  scoreId.value=row.scoreId;
}
async function confirm(){
  let form=new Map();
  form.set('commonMark',commonMark.value);
  form.set('finalMark',finalMark.value);
  const h=Object.fromEntries(form);
  const res = await request.post('/score/scoreSave',{
    data:{
      scoreId:scoreId.value,
      form:h,
    },
  })
  if(res.data.code==200){
      dialogVisible.value=false;
      updateTableData();
      ElMessage({
          message: '操作成功！',
          type: 'success',
          offset: 150
      })
  }
  else{
      ElMessage({
          message: '加载失败，请重试！',
          type: 'error',
          offset: 150
      })
  }
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
      gap: 5px 20px;
      .firstInput{
          width: 150px;
      }
      .secondInput{
          width: 150px;
      }
  }
}
.pagination{
  margin-top: 10px;
}
.dialogContent{
  .commonMark,.finalMark{
    display: flex;
    align-items: center;
    margin-bottom: 5px;
    p{
      margin: 0px;
      margin-right: 10px;
    }
    .input{
      width: 180px;
    }
  }
}
</style>

