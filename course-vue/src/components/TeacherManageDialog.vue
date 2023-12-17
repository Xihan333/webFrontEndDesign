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

const emit = defineEmits(['update:show', 'updateTable'])
const submit = async () => {
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