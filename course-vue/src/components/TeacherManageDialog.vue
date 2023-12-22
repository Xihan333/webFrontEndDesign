<template>
    <div class="TeacherManageDialog">
        <el-dialog :model-value="show"
                   :before-close="handleClose"
                   class="dialog" width="37%"
        >
        <template #header>
            <div style="text-align: center; font-size: 18px ">新增教师</div>
        </template>
        <el-form :model="rowData" size="large">
            <el-form-item label="姓名">
                <el-input v-model="rowData.name"
                maxlength="20"
                placeholder="请输入姓名"
                show-word-limit
                />
            </el-form-item>
            <el-form-item label="工号">
                <el-input v-model="rowData.num"
                maxlength="20"
                placeholder="请输入工号"
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
            <el-form-item label="学位">
                <el-input v-model="rowData.degree"
                maxlength="18"
                placeholder="请输入学位"
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
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="cancel">取消</el-button>
                <el-button type="primary" @click="submit">提交</el-button>
            </span>
        </template>
    </el-dialog>
    </div>
</template>

<script setup>
import {defineProps, defineEmits, ref, watchEffect} from 'vue'
import {ElMessageBox} from 'element-plus'
import request from '../request/axios_config.js'

const nameInvalid = ref(false);
const emailInvalid = ref(false);
const phoneInvailid = ref(false);
const radio2 = ref('1')
const rowData= ref({
    teacherId:'',
    name: '',
    num: '',
    gender:'',
    genderName: '',
    degree: '',
    birthday: '',
    card: '',
    email: '',
    phone: ''
})

const props = defineProps({
    show:{
        type: Boolean,
        required :true,
    },
    rowData:Object,
    dialogMode: String
})

const disabledDate = (time) => {
    return time.getTime() > Date.now()
}


watchEffect(() =>  {
    if (props.dialogMode === 'add') {
      // 如果是新增模式，清空或重置表单数据
      rowData.value = {teacherId:'',name:'', num:'', gender:'',genderName:'',degree:'',birthday:'',card:'',email:'',phone:''}
  }
     else if (props.dialogMode === 'view') {
      // 如果是查看模式，填充数据
      rowData.value = { ...props.rowData }
      console.log(rowData.value)
    }
  })

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
        phoneInvailid.value = false;
    }
    else{
        console.log("电话号码错了");
        phoneInvailid.value = true;
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

const emit = defineEmits(['update:show', 'updateTable'])
const submit = async () => {
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
    if(phoneInvailid.value){
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
    const res = ref({})
    if(props.dialogMode === 'view'){
        console.log(props.rowData.teacherId)
        res.value = await request.post('/teacher/teacherEditSave',{
            data:{
                teacherId:props.rowData,teacherId,
                form:{
                    name:rowData.value.name,
                    num:rowData.value.num,
                    gender:rowData.value.gender,
                    birthday:rowData.value.birthday,
                    degree:rowData.value.degree,
                    card:rowData.value.card,
                    phone:rowData.value.phone,
                    email:rowData.value.email
                }
            }
        })
    }else{
        res.value = await request.post('/teacher/teacherEditSave',{
            data:{
                teacherId: null,
                form:{
                    name:rowData.value.name,
                    num:rowData.value.num,
                    gender:rowData.value.gender,
                    birthday:rowData.value.birthday,
                    degree:rowData.value.degree,
                    card:rowData.value.card,
                    phone:rowData.value.phone,
                    email:rowData.value.email
                }
            }
        })
    }
    console.log(res.value)
    if(res.value.data.code == 200){
        emit('update:show', false)
        emit('updateTable')
    }
    else{
        ElMessage({
            message: res.value.data.msg,
            type:'error',
            offset: 150
        })
    }
}

const handleClose = done => {
    ElMessageBox.confirm('您确定要关闭吗？')
    .then(() => {
        cancel()
        done()
    })
    .catch(() => {})
}
const cancel = () => {
    console.log('关了')
    emit('update:show', false)
}
</script>

<style scoped lang="scss">
.SecondaryAdminDialog{

}
</style>