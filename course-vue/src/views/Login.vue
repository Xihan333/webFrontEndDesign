<template>
  <div class="background">
    <div class="mark">
      <img src="../assets/img/blueLogo.png">
      <p>教学服务平台</p>
    </div>
    <div class="login">
      <div class="side">
        <el-button 
          class="registerBtn" 
          size="large"
          type="primary" 
          plain 
          round
          @click="register"
        >
          <el-icon size="large"><ArrowLeft /></el-icon> 
          <p class="registerText">注册</p>  
        </el-button>
      </div>
      <div class="main">
        <div class="content">
          <h1>Welcome</h1>
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
            <el-input class="verifyInput" v-model="verification" placeholder="验证码" />
            <img src="../assets/img/1.jpeg">
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
  </div>
</template>

<script setup>
import { ref } from 'vue';
import router from '~/router';
import request from '../request/axios_config.js'
import { useCommonStore } from "~/stores/app"

const store=useCommonStore();
const account=ref('');
const password=ref('');
const verification=ref('');
async function login(){
  const res = await request.post('/auth/login',{
    data:{
      'username': account.value,
      'password':password.value
    } 
  })
  console.log('请看请求',res)
  if(res.data.code==200){
    store.setUserInfo(res.data.data)
    let role=store.userInfo.roles;
    if(role=='ROLE_ADMIN'){
      router.push('/admin');
    }
    else if(role=='ROLE_STUDENT'){
      router.push('/student');
    }
    else if(role=='ROLE_TEACHER'){
      router.push('/teacher');
    }
  }
  else{
    ElMessage({
      message: '登录失败，请重试！',
      type: 'error',
      offset: 150
    })
  }
}
function forget(){
  //跳转到找回密码页面
}
function register(){
  //跳转到注册页面
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
  .login{
    margin: auto;
    height: 450px;
    width: 700px;
    border-radius: 20px;
    box-shadow: 0px 0px 10px #658d93;
    border:1px solid #228FA0;
    overflow: hidden;
    .side{
      display: inline-block;
      vertical-align: top;
      height: 100%;
      width: 20%;
      background-color: #6FB6C1;
      text-align: center;
      .registerBtn{
        padding: 5px 10px;
        margin-top: 340px;
        .registerText{
          margin: 0px 10px 0px 10px;
          font-size: large;
        }
      }
    }
    .main{
      // display: inline-block;
      display: inline-flex;
      vertical-align: top;
      height: 100%;
      width: 80%;
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
          .verifyInput{
            width: 100px;
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
}
</style>
