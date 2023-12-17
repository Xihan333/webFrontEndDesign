<!-- 教师管理弹窗 -->
<template>
    <div class="DissertationDialog">
        <el-dialog :model-value="show"
                 :before-close="handleClose"
                 class="dialog" width="37%"
        >
        <template #header>
          <div style="text-align: center; font-size: 18px">新增论文</div>
        </template>
        <el-form :model="form" size="large">
            <el-form-item label="论文主题">
                <el-input v-model="rowData.paperName"
                maxlength="50"
                placeholder="请输入论文名称"
                show-word-limit
                />
            </el-form-item>
            <el-form-item label="论文作者">
                <el-input v-model="rowData.author"
                maxlength="50"
                placeholder="请输入论文名称"
                show-word-limit
                />
            </el-form-item>
            <el-form-item label="时间">
            <!-- 禁用时间，复制的时候记得连同script里面的disabledDate方法一起带着 -->
                <el-date-picker
                v-model="rowData.day"
                type="date"
                :disabled-date="disabledDate"
                placeholder="请选择时间"
                :size="size"
                format="YYYY/MM/DD"
                value-format="YYYY/MM/DD"
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
import { defineProps, defineEmits, ref,watchEffect } from 'vue'
import { ElMessageBox } from 'element-plus'
import request from '../request/axios_config.js'

const rowData=ref({
    scientificPayoffsId:'',
    paperName:'',
    author:'',
    day:''
})

const props = defineProps({
    show: {
      type: Boolean,
      required: true,
    },
    rowData:Object,
    dialogMode: String
}) 

//禁用时间
const disabledDate = (time) => {
    return time.getTime() > Date.now()
}

watchEffect(() => {
    if (props.dialogMode === 'add') {
      // 如果是新增模式，清空或重置表单数据
      rowData.value = {scientificPayoffsId :'',paperName: '',author: '',day: ''}
}
    else if (props.dialogMode === 'view') {
      // 如果是查看模式，填充数据
      rowData.value = { ...props.rowData }
      console.log(rowData.value)
    }
})

const emit = defineEmits(['update:show','updateTable'])

const submit = async () => {
    try {
    if (props.dialogMode === 'view') {
      // 调用修改接口
    console.log(props.rowData.socialId)
    await request.post('/scientificPayoffs/scientificPayoffsTeacherEditSave', {
        data:{
            scientificPayoffsId:props.rowData.scientificPayoffsId,
            form:{
                paperName: rowData.value.paperName,
                author: rowData.value.author,
                day: rowData.value.day
            }
        }
      })
    } else {
      // 则调用新增接口
    await request.post('/scientificPayoffs/scientificPayoffsTeacherEditSave', {
        data:{
            scientificPayoffsId:null,
            form:{
                paperName: rowData.value.paperName,
                author: rowData.value.author,
                day: rowData.value.day
            }
        }
      })
    }
    emit('update:show', false) // 关闭对话框
    emit('updateTable') // 通知父组件更新表格
} catch (error) {
    console.error('请求失败:', error)
}
    emit('update:show', false)
}
  
  // 点击对话框背后黑幕时也会触发关闭
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