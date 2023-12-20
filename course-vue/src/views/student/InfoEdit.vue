<template>
<div class = "auth-container">
  <div class="forget-password">
    <div class="main">
      <template #header>
        <div style="text-align: center; font-size: 18px ">新增学生</div>
      </template>
      <el-form :model="rowData" size="large">
            <el-form-item label="姓名">
                <el-input v-model="rowData.name"
                maxlength="20"
                placeholder="请输入姓名"
                show-word-limit
                />
            </el-form-item>
            <el-form-item label="学号">
                <el-input v-model="rowData.num"
                maxlength="20"
                placeholder="请输入学号"
                show-word-limit
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
            <el-form-item label="专业">
                <el-input v-model="rowData.dept"
                maxlength="18"
                placeholder="请输入专业"
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
        </el-form>
        
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
