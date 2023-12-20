<!-- 修改密码 -->
<template>
    <div class = "auth-container">
      <div class="forget-password">
        <div class="main">
          <div class="content">
            <br/>
            <br/>
            <br/>
            <h1>修改密码</h1>
            <el-input
              class="input"
              v-model="register.oldPassword"
              type="password"
              placeholder="旧密码"
              show-password
            />
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
            <br/>
            <el-button 
              class="loginBtn" 
              type="primary"
              @click="resetPassWord"  
            >修改密码</el-button>
            <br/>
            <p class="copyright">Copyright©2023</p>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import request from '../../request/axios_config.js'
import { updatePassword } from "~/services/infoServ";
import { ElMessage,ElMessageBox } from 'element-plus'
import { useCommonStore } from "~/stores/app"

const store=useCommonStore();
const register = ref({
  password: '',
  confirmPassword: '',
  oldPassword: '',
})

async function resetPassWord(){
  const res = await request.post('/auth/changePassword',{
    data:{
      'oldPassword': register.value.oldPassword,
      'newPassword': register.value.password
    }
  })
  console.log(res.data)
  if(res.data == undefined){
    ElMessage({
      message: '账号或密码错误',
      type: 'error',
      offset: 200
    })
    store.updateLoading(false);
  } else if(res.data.code!=200){
    ElMessage({
      message: '修改密码错误',
      type: 'error',
      offset: 150
    })
    store.updateLoading(false);
  } else {
      ElMessageBox.alert('密码修改成功！',{
      confirmButtonText: 'OK'
    })
  }
} 

// export default defineComponent({
//   data: () => ({
//     passw: "password",
//     oldPassword: "",
//     newPassword: "",
//     checkPassword: "",
//     msg: "",
//   }),
//   methods: {
//     // 提交密码
//     submitPassword() {
//       var msg = "";
//       if (this.oldPassword === undefined || this.oldPassword === "") {
//         msg = "旧密码为空不能修改";
//       } else if (this.newPassword === undefined || this.newPassword === "") {
//         msg = "新密码为空不能修改";
//       } else if (this.oldPassword === this.newPassword) {
//         msg = "新密码和旧密码相同，不能修改";
//       } else if (this.checkPassword !== this.newPassword) {
//         msg = "新密码和确认密码不相同，不能修改";
//       } else {
//         var c;
//         var ch = false;
//         var num = false;
//         var other = false;
//         for (var i = 0; i < this.newPassword.length; i++) {
//           c = this.newPassword.charAt(i);
//           if ((c >= "a" && c <= "z") || (c >= "A" && c <= "Z")) {
//             ch = true;
//           } else if (c >= "0" && c <= "9") {
//             num = true;
//           } else {
//             other = true;
//           }
//         }
//         if (!ch || !num || !other) {
//           msg =
//             "密码至少包含大写字母、小写字母、数字和符号两种以上的类型，请重新输入！";
//         } else if (this.newPassword.length < 8) {
//           msg = "密码长度必须大于等于8个字符，请重新输入！";
//         }
//       }
//       if (msg !== "") {
//         message(this, msg);
//       } else {
//         updatePassword({
//           oldPassword: this.oldPassword,
//           newPassword: this.newPassword,
//         }).then((response) => {
//           if (response.code == 0) {
//             message(this, "提交成功");
//           } else {
//             message(this, response.msg);
//           }
//         });
//       }
//     },
//   },
// });

</script>

<style lang="scss" scoped>
  .auth-container{
    margin-top: 30px;
    position: relative;
    display: flex;
    height: 100%;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    .forget-password{
      vertical-align: middle;
      margin: auto;
      height: 800px;
      width: 1200px;
      border-radius: 20px;
      box-shadow: 0px 0px 10px #658d93;
      border:1px solid #228FA0;
      overflow: hidden;
    .main{
      display: inline-block;
      display: inline-flex;
      vertical-align: top;
      height: 100%;
      width: 100%;
      background-image: url('../../assets/img/background.jpg');
      background-size: auto;
      .content{
        margin: 26px auto 10px auto;
        h1{
          text-align: center;
          font-size: xxx-large;
          margin-bottom: 50px;
        }
        .input{
          :deep(input::placeholder ,input::-webkit-input-placeholder, input::-moz-placeholder, input:-ms-input-placeholder, input::-webkit-input-placeholder) {
            color: #b9bec4;
            font-size: 16px;
          }
          margin: auto,0;
          width: 450px;
          height: 52px;
          margin-bottom: 20px;
        }
        .loginBtn{
          margin: auto;
          width: 450px;
          height: 52px;
          font-size: 16px;
        }
        .copyright{
          margin: auto;
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