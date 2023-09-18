<template>
  <!-- 最外层区域表单 -->
  <div class="base_form">
    <!-- 区域顶部标题 -->
    <div class="base_header">
      <div class="blue_column"></div>
      <div class="base_title">修改密码</div>
    </div>
    <!-- 页面提示 -->
    <div class="base_prompt">
      密码强度要求三种字符类型（大小写、数字、特殊字符）及以上且密码长度为8位及以上！
    </div>
    <!-- 表单内容 -->
    <div class="oldPassword flex-row">
      <div class="oldPass">旧密码</div>
      <div>
        <input class="inputWidth" v-model="oldPassword" type="password" />
      </div>
    </div>

    <div class="newPassword flex-row">
      <div class="newPass">请输入新密码</div>
      <div>
        <input class="inputWidth" v-model="newPassword" type="password" />
      </div>
    </div>
    <div class="alarm">*密码长度最大为20位</div>

    <div class="reconfirmP flex-row">
      <div class="rPass">再次输入新密码</div>
      <div>
        <input class="inputWidth" v-model="checkPassword" type="password" />
      </div>
    </div>
    <div class="alarm">*密码长度最大为20位</div>
    <div class="main8 flex-row justify-between">
      <div class="group8 flex-col" @click="submitPassword()">
        <span class="word20">修改</span>
      </div>
      <div class="group9 flex-col">
        <span class="info8">重置</span>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent } from "vue";
import { updatePassword } from "~/services/infoServ";
import { message } from "~/tools/messageBox";
export default defineComponent({
  data: () => ({
    passw: "password",
    oldPassword: "",
    newPassword: "",
    checkPassword: "",
    msg: "",
  }),
  methods: {
    // 提交密码
    submitPassword() {
      var msg = "";
      if (this.oldPassword === undefined || this.oldPassword === "") {
        msg = "旧密码为空不能修改";
      } else if (this.newPassword === undefined || this.newPassword === "") {
        msg = "新密码为空不能修改";
      } else if (this.oldPassword === this.newPassword) {
        msg = "新密码和旧密码相同，不能修改";
      } else if (this.checkPassword !== this.newPassword) {
        msg = "新密码和确认密码不相同，不能修改";
      } else {
        var c;
        var ch = false;
        var num = false;
        var other = false;
        for (var i = 0; i < this.newPassword.length; i++) {
          c = this.newPassword.charAt(i);
          if ((c >= "a" && c <= "z") || (c >= "A" && c <= "Z")) {
            ch = true;
          } else if (c >= "0" && c <= "9") {
            num = true;
          } else {
            other = true;
          }
        }
        if (!ch || !num || !other) {
          msg =
            "密码至少包含大写字母、小写字母、数字和符号两种以上的类型，请重新输入！";
        } else if (this.newPassword.length < 8) {
          msg = "密码长度必须大于等于8个字符，请重新输入！";
        }
      }
      if (msg !== "") {
        message(this, msg);
      } else {
        updatePassword({
          oldPassword: this.oldPassword,
          newPassword: this.newPassword,
        }).then((response) => {
          if (response.code == 0) {
            message(this, "提交成功");
          } else {
            message(this, response.msg);
          }
        });
      }
    },
  },
});
</script>
<style>
.alarm {
  margin-left: 594px;
  margin-top: 8px;
  font-family: PingFangTC-Regular;
  font-size: 12px;
  color: #930e14;
  letter-spacing: 0;
  font-weight: 400;
}
.newPassword {
  margin-left: 485px;
  margin-top: 34px;
}
.reconfirmP {
  margin-left: 470px;
  margin-top: 9px;
}

.inputWidth {
  width: 478px;
}
input {
  height: 42px;
  line-height: 42px;
  border-radius: 0px;
}
.rPass {
  width: 120px;
  height: 42px;
  font-size: 14px;
  font-family: PingFangTC-Regular, PingFangTC;
  font-weight: 400;
  color: #202020;
  line-height: 42px;
  text-align: center;
  margin-right: 6px;
}

.newPass {
  width: 100px;
  height: 42px;
  font-size: 14px;
  font-family: PingFangTC-Regular, PingFangTC;
  font-weight: 400;
  color: #202020;
  line-height: 42px;
  text-align: center;
  margin-right: 12px;
}
.oldPass {
  text-align: center;
  width: 42px;
  height: 42px;
  font-size: 14px;
  font-family: PingFangTC-Regular, PingFangTC;
  font-weight: 400;
  color: #202020;
  line-height: 42px;
  margin-right: 12px;
}
.oldPassword {
  margin-left: 542px;
  margin-top: 125px;
}

.main8 {
  width: 640px;
  height: 42px;
  margin: 42px 0 0 457px;
  border: none;
}

.group8 {
  background-color: rgba(147, 14, 20, 1);
  border-radius: 4px;
  height: 42px;
  width: 311px;
  cursor: pointer;
}

.word20 {
  width: 28px;
  height: 14px;
  overflow-wrap: break-word;
  color: rgba(255, 255, 255, 1);
  font-size: 14px;
  font-family: STHeitiSC-Medium;
  text-align: left;
  white-space: nowrap;
  line-height: 14px;
  display: block;
  margin: 15px 0 0 140px;
}

.group9 {
  background-color: rgba(226, 227, 228, 1);
  border-radius: 4px;
  height: 42px;
  width: 311px;
}

.info8 {
  width: 28px;
  height: 14px;
  overflow-wrap: break-word;
  color: rgba(131, 131, 131, 1);
  font-size: 14px;
  font-family: SourceHanSansCN-Regular;
  text-align: left;
  white-space: nowrap;
  line-height: 14px;
  display: block;
  margin: 15px 0 0 140px;
}
.flex-col {
  display: flex;
  flex-direction: column;
}
.flex-row {
  display: flex;
  flex-direction: row;
}
.justify-between {
  display: flex;
  justify-content: space-between;
}
</style>
