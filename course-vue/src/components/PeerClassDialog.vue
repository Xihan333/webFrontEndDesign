<!-- 编辑学生互评班级管理的弹窗组件 -->
<!-- 主要是评价 -->
<template>
    <div class="PeerClassDialog">
        <el-dialog :model-value="show"
                   :before-close="handleClose"
                   class="dialog" width="37%"
        >
            <template #header>
                <div style="text-align: center; font-size: 18px;">评价</div>
            </template>
            <el-form :model="form" size="large">
                <el-form-item label="被评价人姓名">
                    <el-input v-model="rowData.name"
                    maxlength="20"
                    disabled placeholder="请输入评价人姓名"
                    show-word-limit
                    />
                </el-form-item>
                <el-form-item label="被评价人学号">
                    <el-input v-model="rowData.num"
                    maxlength="20"
                    disabled placeholder="请输入评价人学号"
                    show-word-limit
                    />
                </el-form-item>
                <el-form-item label="评价内容">
                    <el-input v-model="content"
                    maxlength="200"
                    type="textarea"
                    :autosize="{ minRows: 3}"
                    placeholder="请输入评价内容"
                    show-word-limit
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button type="primary" @click="submit">
                    提交
                    </el-button>
                </span>
            </template>          
        </el-dialog>
    </div>
</template>

<script setup>
import {defineProps, defineEmits,ref,watchEffect} from 'vue'
import {ElMessageBox} from'element-plus'
import request from '../request/axios_config.ts'

const rowData=ref({
    name:'',
    num:'',
    studentId:''
})
const content = ref()
const props = defineProps({
    show:{
        type: Boolean,
        required:true,
    },
    rowData:Object,
})

watchEffect(() => {
    rowData.value = {...props.rowData }
    console.log(rowData.value)
})

const emit = defineEmits(['update:show','updateTableData'])

const submit = async () => {
    try{
        const res = await request.post('/evaluation/evaluationEditSave',{
            data:{
                evaluationId:"",
                evaluation:{
                    eval:content.value
                },
                studentId:rowData.value.studentId
            }
        })
        console.log(res.data)
        
        emit('update:show', false)
        emit('updateTable')
    }
    catch(error){
        console.error('请求失败',error)
    }
    emit('update:show',false)
}

const handleClose = done => {
    ElMessageBox.confirm('您确定要关闭吗?')
      .then(() => {
        cancel()
        done()
      })
      .catch(() => {})
  }
  
  const cancel = () => emit('update:show', false)
</script>



<style scoped lang="scss">
.SecondaryAdminDialog{
}
</style>
