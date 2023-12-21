<template>
  <div class="background">
    <div class="mark">
      <img src="../assets/img/blueLogo.png">
      <p>教学服务平台</p>
    </div>
  
    <div class = "auth-container">
      <div class="forget-password">
        <div class="side">
          <el-button 
            class="registerBtn" 
            size="large"
            type="primary" 
            plain 
            round
            @click="toLogin"
          >
            <el-icon size="large"><ArrowLeft /></el-icon> 
            <p class="registerText" >返回登陆</p>  
          </el-button>
        </div>
        <div class="main">
          <div class="content">
            <br/>
            <h1>忘记密码</h1>
            <br/>
            <el-input class="input" v-model="register.account" placeholder="用户名" />
            <br/>
            <el-input
              class="input"
              v-model="register.password"
              type="password"
              placeholder="新密码"
              show-password
            />
            <br/>
            <el-input
              class="input"
              v-model="register.confirmPassword"
              type="password"
              placeholder="确认新密码"
              show-password
            />
            <br/>
            <div class="verify">
              <el-input class="email" v-model="register.email" placeholder="注册邮箱" />
              <el-button class="verifyBtn" :disabled="disabled" type="primary" @click="getCode">{{valiBtn}}</el-button>
            </div>
            <div v-if="isInputInvalid" class="error-message">{{ inputErrorMessage }}</div>
            <br/>
            <el-input class="verifyInput" v-model="register.mailVerificationCode" placeholder="请输入验证码" />
            <br/>
            <br/>
            <el-button 
              class="loginBtn" 
              type="primary"
              @click="resetPassWord"  
            >重置密码</el-button>
            <br/>
            <p class="copyright">Copyright©2023</p>
          </div>
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

const store=useCommonStore();
const valiBtn = ref('获取验证码');
const disabled = ref(false)
const isInputInvalid = ref(false);
const inputErrorMessage = ref('');
const register = ref({
  password: '123456',
  confirmPassword: '123456',
  account: '202203000222',
  email: '',
  mailVerificationCode: ''
})
onMounted(() => {
  // 发起请求获取当前表格数据
  updateTableData()
})
const updateTableData = async () => {
  changeValiCode()
}
  
async function resetPassWord(){
  const res = await request.post('/auth/resetPassWord',{
    data:{
      'username': register.value.account,
      'newPassword': register.value.password,
      'email': register.value.email,
      'mailVerificationCode' : register.value.mailVerificationCode
    }
  })
  if(res.data.code!=200){
    ElMessage({
           message: '验证码错误',
           type: 'error',
           offset: 150
         })
  } else {
    ElMessageBox.alert('密码重置成功！',{
      confirmButtonText: 'OK'
    })
    tologin()
  }
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
}

async function getCode(){
  validateInput()
  disabled: false
  if(!isInputInvalid.value){
    console.log("aaaaaaaaaaaa")
    const res = await request.post('/auth/sendEmail',{
      data:{
        'email': register.value.email,
        'subject': "忘记密码"
      }
    })
    if(res.data.code==200){
      ElMessage({
        message: '验证码发送成功',
        type: 'success',
        offset: 150
      })
      tackBtn()
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

const toLogin = () =>{
    router.push('login')
}

</script>

<style lang="scss" scoped>
.background{
  position: relative;
  display: flex;
  height: 100%;
  width: 100%;
  background-image: url('../../assets/img/background.jpg');
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
    .forget-password{
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
          margin-bottom: 10px;
        }
        .verify{
          display: flex;
          justify-content: space-between;
          width: 350px;
          margin-bottom: 0px;
          .email{
            width: 250px;
            height: 42px;
          }
          .verifyBtn{
            width: 150px;
            height: 42px;
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
        .verifyInput{
            width: 350px;
            height: 42px;
          }
        .loginBtn{
          height: 42px;
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
