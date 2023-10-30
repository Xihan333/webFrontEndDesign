<template>
  <div class="base_form">
    <div class="base_header">
      <div class="blue_column"></div>
      <div class="base_title">学生信息</div>
    </div>
    <div class="form-div" style="margin-top: 5px">
      <table class="content">
        <tr>
          <td colspan="1" style="text-align: right">学号</td>
          <td colspan="1"><input v-model="form.num" style="width: 97%" /></td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">姓名</td>
          <td colspan="1"><input v-model="form.name" style="width: 97%" /></td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">学院</td>
          <td colspan="1"><input v-model="form.dept" style="width: 97%" /></td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">专业</td>
          <td colspan="1"><input v-model="form.major" style="width: 97%" /></td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">班级</td>
          <td colspan="1">
            <input v-model="form.className" style="width: 97%" />
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">证件号码</td>
          <td colspan="1"><input v-model="form.card" style="width: 97%" /></td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">性别</td>
          <td colspan="1">
            <select class="commInput" v-model="form.gender" style="width: 97%">
              <option value="0">请选择...</option>
              <option
                v-for="item in genderList"
                :key="item.value"
                :value="item.value"
              >
                {{ item.title }}
              </option>
            </select>
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">出生日期</td>
          <td colspan="1">
            <el-date-picker
              v-model="form.birthday"
              type="date"
              style="width: 100%"
              value-format="yyyy-MM-dd"
              placeholder="选择出生日期"
            />
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">邮箱</td>
          <td colspan="1"><input v-model="form.email" style="width: 97%" /></td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">电话</td>
          <td colspan="1"><input v-model="form.phone" style="width: 97%" /></td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">地址</td>
          <td colspan="1">
            <input v-model="form.address" style="width: 97%" />
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <button class="commButton" @click="submit">提交</button>
          </td>
        </tr>
      </table>
    </div>
  </div>
</template>
<script lang="ts">
import { getDictionaryOptionItemList } from "~/services/systemServ";
import { getStudentInfo, studentEditSave } from "~/services/personServ";
import { defineComponent } from "vue";
import router from "~/router";
import { type OptionItem, type StudentItem } from "~/models/general";
import { getOptionItem } from "~/tools/comMethod";

export default defineComponent({
  //数据
  data: () => ({
    valid: false,
    studentId: null as number | null,
    form: {} as StudentItem,
    gender: {} as OptionItem,
    genderList: [] as OptionItem[],
    nameRules: [],
    emailRules: [],
  }),
  //页面加载方法, 获取性别选择列表,获取学生信息,注意async和await的使用
  async created() {
    //获取获取路由参数,上一个页面传过来的学生id
    const res = this.$route.query.studentId;
    if (res != null) {
      this.studentId = parseInt(res.toString());
    }
    this.genderList = await getDictionaryOptionItemList("XBM");
    if (this.studentId != null) {
      this.form = await getStudentInfo(this.studentId);
      this.gender = getOptionItem(this.genderList, this.form.gender);
    }
  },
  methods: {
    //提交表单
    async submit() {
      //      this.form.gender = this.gender.value;
      const res = await studentEditSave(this.studentId, this.form);
      if (res.code == 0) {
        router.push("student-panel");
      } else {
        alert(res.msg);
      }
    },
  },
});
</script>
