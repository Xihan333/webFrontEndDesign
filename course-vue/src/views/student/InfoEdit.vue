<template>
  <div class="edit-profile">
    <h1>个人信息修改</h1>
     <el-form :model="form">
        <el-form-item label="姓名">
            <el-input v-model="rowData.name"
            disabled
            />
        </el-form-item>
        <el-form-item label="学号">
            <el-input v-model="rowData.num"
            disabled
            />
        </el-form-item>
        <el-form-item label="性别">
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
        <el-form-item label="个人简介">
        </el-form-item>   
      </el-form>
      <div class="content">
        <v-md-editor v-model="rowData.introduce" height="500px"
            placeholder="请输入个人介绍"></v-md-editor>
      </div>
            <br/>
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
  const res = await request.get('/student/getMyInfo')
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

const nameInvalid = ref(false);
const emailInvalid = ref(false);
const phoneInvalid = ref(false);
  
function validateEmail() {
  const regEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // 正确的邮箱验证正则表达式
  if (regEmail.test(rowData.value.email)) {
    emailInvalid.value = false;
  } else {
    console.log("邮箱错了");
    emailInvalid.value = true;
  }
}
function validatePhone(){
    const regPhone = /^1(3[0-9]|4[01456879]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[0-35-9])\d{8}$/;
    if(regPhone.test(rowData.value.phone)){
      phoneInvalid.value = false;
    }
    else{
      console.log("电话号码错了");
      phoneInvalid.value = true;
    }
}

function validateName(){
    const regName = /^[\u4e00-\u9fa5]{2,4}$/;
    if(regName.test(rowData.value.name)){
      nameInvalid.value = false;
    }
    else{
      console.log("名字输的不对");
      nameInvalid.value = true;
    }
}


async function submit(){
  validateName()
  if(nameInvalid.value){
      ElMessage({
          message: "请输入正确的姓名格式！",
          type:'error',
          offset: 150
      })
      return;
  }
  validatePhone()
  if(phoneInvalid.value){
      ElMessage({
          message: "请输入正确的电话号码！",
          type:'error',
          offset: 150
      })
      return;
  }
  validateEmail()
  if(emailInvalid.value){
      ElMessage({
          message: "邮箱格式错误！",
          type:'error',
          offset: 150
      })
      return;
  }
  const res = await request.post('/student/studentEdit',{
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
           message: res.data.msg,
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
  /* height: 700px; */
   /* Adjust this height based on your layout */
}

h1 {
  font-size: xx-large;
  margin-bottom: 20px;
  margin-top: 20px;
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

.in{
  width:50%;
  font-size: 16px;
}

.content{
  width: 800px;
}
</style>
