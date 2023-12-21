<template>
  <div class="background">
    <div class="mark">
      <img src="../assets/img/blueLogo.png">
      <p>教学服务平台</p>
    </div>
  
    <div class = "auth-container">
      <div v-if = "showLogin" class="login">
        <div class="side">
          <el-button 
            class="registerBtn" 
            size="large"
            type="primary" 
            plain 
            round
            @click="showLogin=false"
          >
            <el-icon size="large"><ArrowLeft /></el-icon> 
            <p class="registerText" >注册</p>  
          </el-button>
        </div>
        <div class="main">
          <div class="content">
            <br/>
            <br/>
            <br/>
            <h1>Welcome</h1>
            <br/>
            <br/>
            <el-input class="input" v-model="account" placeholder="用户名" />
            <br/>
            <el-input
              class="input"
              v-model="password"
              type="password"
              placeholder="密码"
              show-password
            />
            <br/>
            <div class="verify">
              <el-input class="verifyInput" v-model="verification" placeholder="请输入验证码" />
              <img
                @click="changeValiCode"
                class="verifyImg"
                referrerpolicy="no-referrer"
                :src="img2"
              />
            </div>
            <el-button 
              class="loginBtn" 
              type="primary"
              @click="login"  
            >登录</el-button>
            <br/>
            <el-link type="primary" @click="forget">忘记密码？</el-link>
            <p class="copyright">Copyright©2023</p>
          </div>
        </div>
      </div>
      <div v-else class="register" :rules="registerRules">
        <div class="main2">
          <div class="content">
            <h1>Register</h1>
            <el-input class="input" v-model="register.account" placeholder="用户名" />
            <br/>
            <el-input class="input" v-model="register.name" placeholder="姓名" />
            <br/>
            <el-input
              class="input"
              v-model="register.password"
              type="password"
              placeholder="密码"
              show-password
            />
            <div v-if="isInputInvalidPassword" class="error-message-password">{{ inputErrorMessagePassword }}</div>
            <br/>
            <el-input
              class="input"
              v-model="register.confirmPassword"
              type="password"
              placeholder="确认密码"
              show-password
            />
            <br/>
            <el-form-item >
              <el-radio-group v-model="register.role">
                <el-radio label="STUDENT" border>学生</el-radio>
                <el-radio label="TEACHER" border>教师</el-radio>
              </el-radio-group>
            </el-form-item>
            <br/>
            <div class="verify">
              <el-input class="email" v-model="register.email" placeholder="注册邮箱" />
              <el-button class="verifyBtn" :disabled="disabled" type="primary" @click="getCode">{{valiBtn}}</el-button>
            </div>
            <div v-if="isInputInvalid" class="error-message">{{ inputErrorMessage }}</div>
            <br/>
            <el-input class="verifyInput" v-model="register.mailVerificationCode" placeholder="请输入验证码" />
            <br/>
            <el-button 
              class="registerBtn" 
              type="primary"
              @click="registerUser"  
            >注册</el-button>
            <br/>
            <p class="copyright">Copyright©2023</p>
          </div>
        </div>
        <div class="side2">
          <el-button 
            class="loginBtn" 
            size="large"
            type="primary" 
            plain 
            round
            @click="showLogin=true"
          >
            <el-icon size="large"><ArrowLeft /></el-icon> 
            <p class="loginText">登录</p>  
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import router from '~/router';
import { ElMessage,ElMessageBox } from 'element-plus'
import request from '../request/axios_config.js'
import { useCommonStore } from "~/stores/app"

const showLogin = ref(true);
const store=useCommonStore();
const account=ref('');
const password=ref('');
const img2=ref('');
const verificationId=ref('');
const verification=ref('');
const register = ref({
  account: '',
  password: '',
  confirmPassword: '',
  role: 'STUDENT',
  name: '',
  email: '',
  mailVerificationCode: ''
})
const valiBtn = ref('获取验证码');
const disabled = ref(false)
const isInputInvalid = ref(false);
const inputErrorMessage = ref('');
const isInputInvalidPassword = ref(false);
const inputErrorMessagePassword = ref('');
onMounted(() => {
  // 发起请求获取当前表格数据
  updateTableData()
})
const updateTableData = async () => {
  changeValiCode()
}
  
function validateInput() {
  const regEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // 正确的邮箱验证正则表达式
  if (regEmail.test(register.value.email)) {
    inputErrorMessage.value = '';
    isInputInvalid.value = false;
  } else {
    console.log("邮箱错了");
    inputErrorMessage.value = '请输入正确的邮箱！';
    isInputInvalid.value = true;
  }
}

function validatePassword(){
  const regPassword = /^(?=.*[a-zA-Z])(?=.*[0-9])[A-Za-z0-9]{8,18}$/;
  if(regPassword.test(register.value.password)) {
    inputErrorMessagePassword.value = '';
    isInputInvalidPassword.value = false;
  } else {
    inputErrorMessagePassword.value = '密码必须由字母、数字组成，区分大小写且密码长度为8-18位';
    isInputInvalidPassword.value = true;
  }
}
  
async function login(){
  const res2 = await request.post('/auth/testValidateInfo',{
    data:{
      'validateCodeId' : verificationId.value,
      'validateCode' : verification.value
    }
  })
  if(res2.data.code!=200){
    ElMessage({
           message: '验证码错误',
           type: 'error',
           offset: 150
         })
  }else {
      const res = await request.post('/auth/login',{
      data:{
        'username': account.value,
        'password': password.value
      } 
    })
    console.log('请看请求',res.data)
    if(res.data == undefined){
      ElMessage({
        message: '账号或密码错误',
        type: 'error',
        offset: 200
      })
    } else if(res.data.code==200){
      store.setUserInfo(res.data.data)
      let role=store.userInfo.roles;
      if(role=='ROLE_ADMIN'){
        // 管理员端的标签页单独处理
        store.adminInitTab('学科管理','subject-manage')
        router.push('/admin');
      }
      else if(role=='ROLE_STUDENT'){
        router.push('/student');
      }
      else if(role=='ROLE_TEACHER'){
        router.push('/teacher');
      }
    }
  }
}

const forget = () =>{
    router.push('forget-password')
}

async function registerUser(){
  validatePassword()
  if(!isInputInvalidPassword.value){
    if(register.value.password == register.value.confirmPassword){
      const res = await request.post('/auth/registerUser',{
        data:{
          'username': register.value.account,
          'password': register.value.password,
          'name' : register.value.name,
          'email': register.value.email,
          'role': register.value.role,
          'mailVerificationCode' : register.value.mailVerificationCode
        }
      })
      if(res.data.code!=200){
        ElMessage({
          message: res.data.msg,
          type: 'error',
          offset: 150
        })
        changeValiCode()
      } else {
        ElMessageBox.alert('注册成功！',{
          confirmButtonText: 'OK'
        })
        showLogin.value = true
      }
    } 
  } else {
    ElMessageBox.alert('两次密码输入不一致！',{
      confirmButtonText: 'OK'
    })
  }
}
         

async function getCode(){
  validateInput()
  disabled: false
  if(!isInputInvalid.value){
    const res = await request.post('/auth/sendEmail',{
      data:{
        'email': register.value.email,
        'subject': "注册账号"
      }
    })
    if(res.data.code==200){
      ElMessage({
        message: '验证码发送成功',
        type: 'success',
        offset: 150
      })
    }else{
      ElMessage({
        message: '验证码发送失败',
        type: 'error',
        offset: 150
      })
    }
  }
}

function tackBtn(){       //验证码倒数60秒
  let time = 60;
  let timer = setInterval(() => {
      if(time == 0){
          clearInterval(timer);
          valiBtn.value = '获取验证码';
          disabled.value = false;
      }else{
          disabled.value= true;
          valiBtn.value = time + '秒后重试';
          time--;
      }
  }, 1000);
}

async function changeValiCode(){
  try {
    const res = await request.get('/auth/getValidateCode')
    verificationId.value = res.data.data.validateCodeId;
    console.log(verificationId)
    img2.value = res.data.data.img;
  } catch (error) {
    console.error('Failed to fetch verification image:', error);
  }
}

function refreshVerification() {
  changeValiCode()
}

</script>

<style lang="scss" scoped>
.background{
  position: relative;
  display: flex;
  height: 100%;
  width: 100%;
  background-image: url('../assets/img/background.jpg');
  background-repeat: no-repeat;
  background-size: cover;
  .mark{
    display: flex;
    align-items: center;
    position: absolute;
    top: 15px;
    left: 35px;
    img{
      width: 130px;
      height: 41px;
      object-fit: cover;
      padding-right: 10px;
      border-right: 2px solid #03498E;
    }
    p{
      margin-left: 10px;
      color: #03498E;
      font-size: 14px;
      font-weight: bold;
      letter-spacing: 2px;
    }
  }
  .auth-container{
    position: relative;
    display: flex;
    height: 100%;
    width: 100%;
    .login{
    margin: auto;
    height: 650px;
    width: 800px;
    border-radius: 20px;
    box-shadow: 0px 0px 10px #658d93;
    border:1px solid #228FA0;
    overflow: hidden;
    .side{
      display: inline-block;
      vertical-align: top;
      height: 100%;
      width: 30%;
      background-color: #6FB6C1;
      text-align: center;
      .registerBtn{
        padding: 5px 10px;
        margin-top: 540px;
        .registerText{
          margin: 0px 10px 0px 10px;
          font-size: large;
        }
      }
    }
    .main{
      display: inline-block;
      display: inline-flex;
      vertical-align: top;
      height: 100%;
      width: 70%;
      background-color: #ffffff;
      .content{
        margin: 26px auto 10px auto;
        h1{
          text-align: center;
          margin-bottom: 30px;
        }
        .input{
          width: 350px;
          height: 42px;
          margin-bottom: 20px;
        }
        .verify{
          display: flex;
          justify-content: space-between;
          width: 350px;
          margin-bottom: 20px;
          .email{
            width: 100px;
            height: 40px;
          }
          .verifyInput{
            width: 200px;
            height: 42px;
          }
          img{
            width: 100px;
            height: 40px;
          }
        }
        .loginBtn{
          height: 40px;
          width: 350px;
        }
        .copyright{
          margin-top: 45px;
          color: #b0b0b0;
          text-align: center;
          font-size: 14px;
        }
      }
    }
  }
  .register{
    margin: auto;
    height: 650px;
    width: 800px;
    border-radius: 20px;
    box-shadow: 0px 0px 10px #658d93;
    border:1px solid #228FA0;
    overflow: hidden;
    .side2{
      display: inline-block;
      vertical-align: top;
      height: 100%;
      width: 30%;
      background-color: #6FB6C1;
      text-align: center;
      .loginBtn{
        padding: 5px 10px;
        margin-top: 540px;
        .loginText{
          margin: 0px 10px 0px 10px;
          font-size: large;
        }
      }
    }
    .main2{
      display: inline-block;
      display: inline-flex;
      vertical-align: top;
      height: 100%;
      width: 70%;
      background-color: #ffffff;
      .content{
        margin: 26px auto 10px auto;
        h1{
          text-align: center;
          margin-bottom: 30px;
        }
        .input{
          width: 350px;
          height: 42px;
          margin-bottom: 20px;
        }
        .verify{
          display: flex;
          justify-content: space-between;
          width: 350px;
          margin-bottom: 0px;
          .verifyBtn{
            width: 100px;
            height: 40px;
          }
        }
        .error-message {
            margin-top: 0px;
            color: red;
            font-size: 12px;

          }
        .is-invalid .el-input__inner {
          border-color: red;
        }
        .error-message-password {
          margin-top: 0px;
          color: red;
          font-size: 12px;

        }
        .verifyInput{
            width: 350px;
            height: 40px;
            margin-bottom: 20px;
          }
        .registerBtn{
          height: 40px;
          width: 350px;
        }
        .copyright{
          margin-top: 45px;
          color: #b0b0b0;
          text-align: center;
          font-size: 14px;
        }
      }
    }
  }
  }

}
</style>
