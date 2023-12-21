<template>
  <div class="edit-profile">
    <h1>个人信息修改</h1>
     <el-form :model="form">
        <el-form-item label="性  别">
          <el-radio-group v-model="rowData.gender">
            <el-radio label="1" border>男</el-radio>
            <el-radio label="2" border>女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期">                
            <el-date-picker
              v-model="rowData.birthday"
              type="date"
              :disabled-date="disabledDate"
              palceholder="请选择时间"
              :size="size"
              format="YYYY/MM/DD"
              value-format="YYYY/MM/DD"
              />
        </el-form-item>
        <el-form-item label="身份证号">
            <el-input v-model="rowData.card"
            maxlength="18"
            placeholder="请输入身份证号"
            show-word-limit
            />
        </el-form-item>
        <el-form-item label="电话号码">
            <el-input v-model="rowData.phone"
            maxlength="11"
            placeholder="请输入电话号码"
            show-word-limit
            />
        </el-form-item>
        <el-form-item label="电子邮箱">
            <el-input v-model="rowData.email"
            maxlength="30"
            placeholder="请输入电子邮箱"
            show-word-limit
            />
        </el-form-item>   
        <el-form-item label="个人介绍">
            <el-input v-model="rowData.introduce"
            maxlength="30"
            placeholder="请输入个人介绍"
            show-word-limit
            />
        </el-form-item>   
      </el-form>
    <div class="form-group">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
      </div>
  </div>
</template>

<script setup>
import { defineEmits, ref, watchEffect, onMounted } from 'vue'
import {ElMessageBox,ElMessage} from 'element-plus'
import request from '../../request/axios_config.js'

onMounted(() => {
  // 发起请求获取当前表格数据
  updateData()
})
const updateData = async () => {
  const res = await request.get('/teacher/getMyInfo')
  rowData.value = res.data.data
}

const radio2 = ref('1')
const rowData= ref({
    studentId:'',
    name: '',
    num: '',
    gender:'',
    genderName: '',
    dept: '',
    birthday: '',
    card: '',
    email: '',
    phone: '',
    introduce: ''
})


  
async function submit(){
  const res = await request.post('/teacher/teacherEdit',{
    data:{
      form:{
          gender:rowData.value.gender,
          birthday:rowData.value.birthday,
          card:rowData.value.card,
          phone:rowData.value.phone,
          email:rowData.value.email,
          introduce:rowData.value.introduce
      }
    }
  })
  if(res.data.code!=200){
    ElMessage({
           message: '修改错误',
           type: 'error',
           offset: 150
         })
  } else {
    ElMessageBox.alert('修改成功！',{
      confirmButtonText: 'OK'
    })
    updateData()
  }
} 

</script>

<style>
.edit-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  height: 700px; /* Adjust this height based on your layout */
}

h1 {
  font-size: xxx-large;
  margin-bottom: 50px;
}

.el-form {
  margin-top: 30px;
  width: 70%; /* Adjust the width as needed */
  margin-left: auto; /* 将左侧外边距设置为自动，使表单向右对齐 */

  .el-radio-group{
    font-size: 16px;
  }
}



.el-form-item {
  margin-bottom: 30px; /* Increase space between form items */
  
}

.el-form-item .el-input {
  width: 50%; /* Adjust the width of the input fields */
  height:40px;
  font-size: 16px;
}
</style>
