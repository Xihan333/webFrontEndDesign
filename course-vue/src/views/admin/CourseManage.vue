<!-- 课程管理 -->
<template>
  <!-- 开启选课 -->
  <div class="selectCourse">
    <el-button v-if="selectCourseBtn" type="warning" @click="selectDialogVisible=true" plain>开启选课</el-button>
    <el-button v-else type="warning" @click="selectDialogVisible=true" plain>关闭选课</el-button>
  </div>
  <el-dialog
    v-model="selectDialogVisible"
    title="提示"
    width="30%"
    :before-close="handleClose"
  >
    <span v-if="selectCourseBtn">确定要开启选课吗？</span>
    <span v-else>确定要关闭选课吗？</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="selectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="selectConfirm">确认</el-button>
      </span>
    </template>
  </el-dialog>
  <!--  -->
  <div class="search">
    <el-button type="primary" @click="add">新增</el-button>
    <div class="select">
      <div>
        课程名/课序号
        <el-input
          class="firstInput"
          v-model="courseNumOrName"
          placeholder="请输入"
        />
      </div>
      <div>
        上课教师
        <el-input
          class="secondInput"
          v-model="teacherNameSelect"
          placeholder="请输入"
        />
      </div>
      <div>
        课程类型
        <el-select class="thirdInput" v-model="typeSelect" placeholder="请选择">
        <el-option
          v-for="item in types"
          :key="item"
          :label="item.label"
          :value="item.id"
        />
        </el-select>
      </div>
      <el-button type="primary" plain @click="reset">重置</el-button>
      <el-button type="primary" @click="search">查询</el-button>
    </div>
  </div>
  <el-table border :data="filterTableData">
    <el-table-column prop="campusName" label="开设单位" width="auto" /> 
    <el-table-column prop="gradeName" label="开设年级" width="auto" />     
    <el-table-column prop="courseName" label="课程名称" width="140px" /> 
    <el-table-column prop="courseNum" label="课序号" width="auto" /> 
    <el-table-column prop="teacherName" label="上课教师" width="auto" />
    <el-table-column prop="teacherNum" label="教师工号" width="auto" />
    <el-table-column prop="hour" label="学时" width="auto" />  
    <el-table-column prop="credit" label="学分" width="auto" /> 
    <el-table-column :mapatter="typeFormat" label="课程类型" width="auto" />
    <el-table-column prop="courseCapacity" label="课容量" width="auto" />
    <el-table-column :mapatter="timeFormat" label="上课时间" width="auto" />
    <el-table-column prop="place" label="上课地点" width="auto" />  
    <el-table-column prop="introduction" label="课程介绍" width="auto" />  
    <el-table-column label="操作" width="170px" >
      <template #default="scope">
        <el-button size="default" @click="edit(scope.row)">
          编辑
        </el-button>
        <el-button  type="danger" plain size="default" @click="remove(scope.row)">
          删除
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
    title="课程编辑"
    width="350px"
    :before-close="handleClose"
  >
    <div class="dialogContent">
      <div class="item">
        <p>开设单位</p>
        <el-select class="input" v-model="campus" placeholder="请选择">
        <el-option
          v-for="item in campuses"
          :key="item"
          :label="item.label"
          :value="item.id"
        />
        </el-select>
      </div>
      <div class="item">
        <p>开设年级</p>
        <el-select class="input" v-model="grade" placeholder="请选择">
        <el-option
          v-for="item in grades"
          :key="item"
          :label="item.label"
          :value="item.id"
        />
        </el-select>
      </div>
      <div class="item">
        <p>课序号</p>
        <el-input
          class="input"
          v-model="courseNum"
        />
      </div>
      <div class="item">
        <p>教师工号</p>
        <el-input
          class="input"
          v-model="teacherNum"
        />
      </div>
      <div class="item">
        <p>学时</p>
        <el-input
          class="input"
          v-model="hour"
        />
      </div>
      <div class="item">
        <p>学分</p>
        <el-input
          class="input"
          v-model="credit"
        />
      </div>
      <div class="item">
        <p>课程类型</p>
        <el-select class="input" v-model="type" placeholder="请选择">
        <el-option
          v-for="item in types"
          :key="item"
          :label="item.label"
          :value="item.id"
        />
        </el-select>
      </div>
      <div class="item">
        <p>课容量</p>
        <el-input
          class="input"
          v-model="courseCapacity"
        />
      </div>
      <div class="item">
        <p>上课时间</p>
        <el-select class="input1" v-model="day" placeholder="请选择">
        <el-option
          class="input"
          v-for="(item,index) in days"
          :key="item"
          :label="item"
          :value="index"
        />
        </el-select>
        <el-select class="input1" v-model="timeOrder" placeholder="请选择">
        <el-option
          v-for="(item,index) in timeOrders"
          :key="item"
          :label="item"
          :value="index"
        />
        </el-select>
      </div>
      <div class="item">
        <p>上课地点</p>
        <el-input
          class="input"
          v-model="place"
        />
      </div>
      <div class="item">
        <p>课程介绍</p>
        <el-input
          class="input"
          v-model="introduction"
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
import {filterOption} from '../../assets/js/config.js'

const courseNumOrName=ref("");
const teacherNameSelect=ref("");
const typeSelect=ref();
const types=filterOption.types;

let tableData = []
const filterTableData=ref([]);
onMounted(async() => {
  const res = await request.post('/course/getByCourseNumName',{
      data:{
          numName:''
      }
  })
  if(res.data!=undefined && res.data.code==200){
      tableData=res.data.data;
      filterTableData.value=tableData;
      selectCourseBtn.value=(res.data.data[0].selectAvailable.value==1);
  }
  else{
      ElMessage({
          message: '加载失败，请重试！',
          type: 'error',
          offset: 150
      })
  }
})

function reset(){
  courseNumOrName.value="";
  teacherName.value="";
  type.value=3;
}

function search(){
  // 筛选, 非模糊匹配
  filterTableData.value = tableData.filter((item) => {
      return (courseNumOrName.value==""||(item.courseName==courseNumOrName.value||item.courseNum==courseNumOrName.value))
              &&(teacherName.value==""||item.teacherName==teacherName.value)
              &&(type.value==3||item.type==type.value);
  });
}

function typeFormat(row, column) {
  return types[row.type];
}

function timeFormat(row, column){
  return days[row.day]+timeOrders[row.timeOrder];
}

// 分页相关,不需要修改,直接复制
// 当前页数,默认设置为1
const currentPage = ref(1)
const pageSize = ref(15)
const paginatedTableData = computed(() => searchedTableData.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value))
const handleSizeChange = () => {
  currentPage.value = 1
}

//开启选课相关
const selectDialogVisible=ref(false);
const selectCourseBtn=ref();
async function selectConfirm(){
  let selectAvailable=!(selectCourseBtn.value);
  const res = await request.post('/course/changeCourseSelectAvailable',{
      data:{
        selectAvailable: (+selectAvailable).toString() //转成数字
      }
  })
  if(res.data!=undefined && res.data.code==200){
    ElMessage({
        message: '操作成功！',
        type: 'success',
        offset: 150
    })
    selectCourseBtn.value=!(selectCourseBtn.value)
  }
  else{
      ElMessage({
          message: '操作失败，请重试！',
          type: 'error',
          offset: 150
      })
  }
  selectDialogVisible.value=false;
}

//弹窗相关
console.log("xiala",filterOption)
const dialogVisible=ref(false)
const mode=ref();//弹窗类型
const campus=ref();//开设单位
const campuses=filterOption.allCampuses;
const grade=ref();//开设年级
const grades=filterOption.allGrades;
const courseNum=ref();//课序号
const teacherNum=ref();//教师工号
const hour=ref();//学时
const credit=ref();//学分
const type=ref();//课程类型
const day=ref();//上课星期
const days=['请选择','星期一','星期二','星期三','星期四','星期五','星期六','星期日']
const timeOrder=ref();//上课节次
const timeOrders=['请选择','第一节','第二节','第三节','第四节','第五节']
const place=ref();//上课地点
const courseCapacity=ref();//课容量
const introduction=ref();//课程介绍
//为新增和编辑初始化弹窗
function add(){
  dialogVisible.value=true;
  mode.value='add'
  campus.value="";
  grade.value=""
  courseNum.value=""
  teacherNum.value=""
  hour.value=""
  credit.value=""
  type.value=""
  day.value=""
  timeOrder.value=""
  place.value=""
  courseCapacity.value=""
  introduction.value=""
}
function edit(row){
  dialogVisible.value=true;
  mode.value='edit'
  //type day timeorder grade campus 下拉
  campus.value=row.campusId;
  grade.value=row.gradeId
  courseNum.value=row.courseNum
  teacherNum.value=row.teacherNum
  hour.value=row.hour
  credit.value=row.credit
  type.value=row.type
  day.value=row.day
  timeOrder.value=row.timeOrder
  place.value=row.place
  courseCapacity.value=row.courseCapacity
  introduction.value=row.introduction
}
async function addConfirm(){
  let map=new Map();
  map.set('campusId',campus.value);
  map.set('gradeId',grade.value);
  map.set('courseNum',courseNum.value);
  map.set('teacherNum',teacherNum.value);
  map.set('hour',hour.value);
  map.set('type',type.value);
  map.set('day',day.value);
  map.set('timeOrder',timeOrder.value);
  map.set('place',place.value);
  map.set('courseCapacity',courseCapacity.value);
  map.set('introduction',introduction.value);
  const form=Object.fromEntries(map);
  const res = await request.post('/course/addCourse',{
      data:{
          form:form
      }
  })
  if(res.data!=undefined && res.data.code==200){
      //📌要不要更新呢
  }
  else{
      ElMessage({
          message: '加载失败，请重试！',
          type: 'error',
          offset: 150
      })
  }
}
async function editConfirm(){
  const res = await request.post('/course/getByCourseNumName',{
      data:{
          numName:''
      }
  })
  if(res.data!=undefined && res.data.code==200){
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
function confirm(){
  dialogVisible.value = false;
  if(mode.value=='add'){
    addConfirm();
  }
  else{
    editConfirm();
  }
}
function remove(){

}
</script>
  
<style lang="scss" scoped>
.selectCourse{
  float: right;
  margin-bottom: 10px;
}
.search{
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  width: 100%;
  .select{
      display: flex;
      gap: 5px 0px;
      .firstInput,.secondInput,.thirdInput{
        margin-right: 20px;
      }
      .firstInput{
          width: 150px;
      }
      .secondInput{
          width: 100px;
      }
      .thirdInput{
          width: 90px;
      }
  }
}
.dialogContent{
  .item{
    display: flex;
    align-items: center;
    margin-bottom: 5px;
    p{
      width: 70px;
      margin: 0px;
      margin-right: 10px;
    }
    .input{
      width: 180px;
    }
    .input1{
      width: 87px;
      margin-right: 5px;
    }
  }
}
.pagination{
  margin-top: 10px;
}
</style>