<!-- 编辑课外活动的弹窗组件 -->
<template>
    <div class="ActivityDialog">
        <el-dialog :model-value="show"
                   :before-close="handleClose"
                   class="dialog" width="37%"
        >
        <template #header>
            <div style="text-align: center; font-size: 18px;">新增课外活动</div>
        </template>
        <el-form :model="form" size="large">
            <el-form-item label="活动主题">
                <el-input v-model="rowData.title"
                maxlength="50"
                placeholder="请输入活动主题"
                show-word-limit 
                />         
            </el-form-item>
            <el-form-item label="活动时间">
                <!-- 看不懂，什么叫禁用时间 -->
                <el-date-picker
                 v-model="rowData.day"
                 type="date"
                 :disabled-date="disabledDate"
                 palceholder="请选择时间"
                 :size="size"
                 format="YYYY/MM/DD"
                 value-format="YYYY/MM/DD"
                 />
            </el-form-item>
            <el-form-item label="活动地点">
                <el-input v-model="rowData.location"
                placeholder="请输入活动地点"
                />
            </el-form-item>
            <el-form-item label="活动内容">
                <el-input
                v-model="rowData.introduction"
                maxlength="200"
                :autosize="{ minRows: 2}"
                type="textarea"
                placeholder="请输入活动内容"
                show-word-limit
                />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="cancel">取消</el-button>
                <el-button type="primary" @click="submit">
                    提交
                </el-button>
            </span>
        </template>
    </el-dialog>
    </div>
</template>

<script setup>
import {defineProps, defineEmits, ref, watchEffect} from 'vue'
import {ElMessageBox } from 'element-plus'
import request from '../request/axios_config.js'

const rowData= ref({
    activityId : '',
    title: '',
    day:'',
    location:'',
    introduction:''
})

const props = defineProps({
    show: {
        type: Boolean,
        required :true,
    },
    rowData:Object,
    dialogMode: String
})

const disabledDate = (time) => {
    return time.getTime() > Date.now()
}

watchEffect(() => {
    if(props.dialogMode ==='add'){
        //新增则清空或重置表单数据
        rowData.value = {activityId :'', title:'', day:'', location:'', introduction:''}
    }
    else if(props.dialogMode === 'view'){
        rowData.value = {...props.rowData}
        console.log(rowData.value)
    }
})

const emit = defineEmits(['update:show','updateTable'])

const submit = async () => {
    try{
        if(props.dialogMode === 'view'){
            console.log(props.rowData.activityId)
            await request.post('/activity/ActivityStudentEditSave',{
                data:{
                    activityId:props.rowData,activityId,
                    form:{
                        title: rowData.value.title,
                        day:rowData.value.day,
                        location:rowData.value.location,
                        introduction:rowData.value.introduction
                    }
                }
            })
        }
        else{
            await request.post('/activity/ActivityStudentEditSave',{
                data:{
                    activityId:null,
                    form:{
                        title: rowData.value.title,
                        day:rowData.value.day,
                        location:rowData.value.location,
                        introduction:rowData.value.introduction
                    }
                }
            })
        }
        emit('update:show', false)
        emit('updateTable')
    }
    catch(error){
        console.error('请求失败：',error)
    }
    emit('update:show', false)
}

const handleClose = done => {
    ElMessageBox.confirm('您确定要关闭吗？')
    .then(() => {
        cancel()
        done()
    })
    .catch(() => {})
}

const cancel = () =>emit('update:show', false)
</script>

<style scoped lang="scss">
.SecondaryAdminDialog{

}
</style>