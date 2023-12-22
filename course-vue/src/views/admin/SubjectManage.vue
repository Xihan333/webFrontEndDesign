<!-- 学科管理 -->
<template>
    <!-- 操作栏 -->
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
          课程类型
          <el-select class="thirdInput" v-model="typeSelect" placeholder="请选择">
          <el-option
            v-for="item in filterTypes"
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
    <!-- 表格 -->
    <el-table border :data="filterTableData">
      <el-table-column prop="campusName" label="开设单位" width="auto" /> 
      <el-table-column prop="gradeName" label="开设年级" width="auto" />     
      <el-table-column prop="courseName" label="课程名称" width="140px" /> 
      <el-table-column prop="courseNum" label="课序号" width="auto" /> 
      <el-table-column prop="hour" label="学时" width="auto" />  
      <el-table-column prop="credit" label="学分" width="auto" /> 
      <el-table-column :formatter="typeFormat" label="课程类型" width="auto" />
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
    <!-- 学科编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="350px"
      :before-close="handleClose"
    >
      <div class="dialogContent">
        <div class="item">
          <p>开设单位</p>
          <el-select class="input" v-model="campusId" placeholder="请选择">
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
          <el-select class="input" v-model="gradeId" placeholder="请选择">
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
          <p>课程名称</p>
          <el-input
            class="input"
            v-model="courseName"
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
            v-for="item in optionTypes"
            :key="item"
            :label="item.label"
            :value="item.id"
          />
          </el-select>
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
    <!-- 删除对话框 -->
    <el-dialog
        v-model="deleteDialogVisible"
        title="删除提示"
        width="30%"
        :before-close="handleClose"
    >
        <span>确定要删除该学科吗？</span>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="deleteDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="deleteConfirm">确定</el-button>
            </span>
        </template>
    </el-dialog>
  </template>
    
  <script setup>
  import { ref,computed,onMounted } from 'vue'
  import { ElMessage } from 'element-plus'
  import request from '../../request/axios_config.js'
  import { filterOption } from '../../assets/js/config.js'
  
  //筛选
  const courseNumOrName=ref("");
  const typeSelect=ref(3);
  const filterTypes=filterOption.filterTypes;
  const optionTypes=filterOption.optionTypes;
  
  let tableData = []
  const filterTableData=ref([]);
  onMounted(async() => {
    updateTableData();
  })
  
  async function updateTableData(){
    const res = await request.get('/course/getAllCourses')
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
  

  function reset(){
    courseNumOrName.value="";
    typeSelect.value=3;
  }
  
  function search(){
    // 筛选, 非模糊匹配
    filterTableData.value = tableData.filter((item) => {
        return (courseNumOrName.value==""||(item.courseName==courseNumOrName.value||item.courseNum==courseNumOrName.value))
                &&(typeSelect.value==3||item.type==typeSelect.value);
    });
  }
  
  function typeFormat(row) {
    if(row.type==null){
      return ""
    }
    return optionTypes[row.type].label;
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
const dialogTitle=ref();//弹窗标题兼弹窗类型判断
const dialogVisible=ref(false)
const campusId=ref();//开设单位
const campuses=filterOption.allCampuses;
const gradeId=ref();//开设年级
const grades=filterOption.allGrades;
const courseId=ref();//课程id
const courseNum=ref();//课序号
const courseName=ref();//课程名称
const hour=ref();//学时
const credit=ref();//学分
const type=ref();//课程类型
const introduction=ref();//课程介绍
//为新增和编辑初始化弹窗
function add(){
    dialogVisible.value=true;
    dialogTitle.value='学科新增'
    campusId.value="";
    gradeId.value=""
    courseNum.value=""
    courseName.value=""
    hour.value=""
    credit.value=""
    type.value=""
    introduction.value=""
}
function edit(row){
    dialogVisible.value=true;
    dialogTitle.value='学科编辑'
    courseId.value=row.courseId;
    campusId.value=row.campusId;
    gradeId.value=row.gradeId
    courseNum.value=row.courseNum
    courseName.value=row.courseName
    hour.value=row.hour
    credit.value=row.credit
    type.value=row.type
    introduction.value=row.introduction
}

// 封装用于网络请求的数据
function getMap(){
    let map=new Map();
    map.set('campusId',campusId.value);
    map.set('gradeId',gradeId.value);
    map.set('courseNum',courseNum.value);
    map.set('courseName',courseName.value);
    map.set('hour',hour.value);
    map.set('credit',credit.value);
    map.set('courseType',type.value);
    map.set('introduction',introduction.value);
    return map;
}

async function addConfirm(){
    let map=getMap();
    const form=Object.fromEntries(map);
    const res = await request.post('/course/addCourse',{
        data:{
            form:form
        }
    })
    if(res.data!=undefined && res.data.code==200){
        updateTableData();
        dialogVisible.value = false;
        ElMessage({
            message: '新增成功！',
            type: 'success',
            offset: 150
        })
    }
    else{
        ElMessage({
            message: res.data.msg,
            type: 'error',
            offset: 150
        })
    }
}
async function editConfirm(){
    let map=getMap();
    map.set('courseId',courseId.value)
    const form=Object.fromEntries(map);
    const res = await request.post('/course/editCourse',{
        data:{
            form:form
        }
    })
    if(res.data!=undefined && res.data.code==200){
        updateTableData();
        dialogVisible.value = false;
        ElMessage({
        message: '编辑成功！',
        type: 'success',
        offset: 150
        })
    }
    else{
        ElMessage({
            message: '操作失败，请重试！',
            type: 'error',
            offset: 150
        })
    }
}
function confirm(){
  // 校验
  if(isNaN(courseNum.value)){
    ElMessage({
      message: '课序号应为数字！',
      type: 'error',
      offset: 150
    })
    return;
  }
  if(isNaN(hour.value)){
    ElMessage({
      message: '学时应为数字！',
      type: 'error',
      offset: 150
    })
    return;
  }
  if(isNaN(credit.value)){
    ElMessage({
      message: '学分应为数字！',
      type: 'error',
      offset: 150
    })
    return;
  }
  if(dialogTitle.value=='学科新增'){
      addConfirm();
  }
  else{
      editConfirm();
  }
}

// 删除对话框
const deleteDialogVisible=ref(false)
function remove(row){
    deleteDialogVisible.value=true;
    courseId.value=row.courseId;
}
async function deleteConfirm(row){
    const res = await request.post('/course/deleteCourse',{
        data:{
            courseId:courseId.value
        }
    })
    if(res.data!=undefined && res.data.code==200){
        updateTableData();
        deleteDialogVisible.value=false;
        ElMessage({
        message: '删除成功！',
        type: 'success',
        offset: 150
        })
    }
    else{
        ElMessage({
            message: '操作失败，请重试！',
            type: 'error',
            offset: 150
        })
    }
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