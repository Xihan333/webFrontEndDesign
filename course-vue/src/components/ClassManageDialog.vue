<template>
    <div class="ClassManageDialog">
        <el-dialog :model-value="show"
                   :before-close="handleClose"
                   class="dialog" width="37%"
        >
            <template #header>
                <div style="text-align: center; font-size: 18px">查看详情</div>
            </template>
            <el-form :model="form" size="large">
                <el-form-item label="班级名称">
                    <el-input v-model="rowdata.clazzName"
                      maxlength="20"
                      placeholder="请输入班级名称"
                      show-word-limit
                      />
                </el-form-item>
                <el-form-item label="学院">
                    <el-input v-model="rowdata.campus"
                      maxlength="20"
                      placeholder="请输入学院"
                      show-word-limit
                      />
                </el-form-item>
                <el-form-item label="年级">
                    <el-input v-model="rowdata.grade"
                      maxlength="2"
                      placeholder="请输入年级"
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
import { defineProps, defineEmits, ref,watchEffect } from 'vue'
import { ElMessageBox } from 'element-plus'
import request from '../request/axios_config.js'

const rowData= ref({
    clazzId:'',
    clazzName:'',
    campus:'',
    grade:''
})

const props = defineProps({
    show: {
      type: Boolean,
      required: true,
    },
    rowData:Object,
    dialogMode: String
})

watchEffect(() => {
    if (props.dialogMode === 'add') {
      // 如果是新增模式，清空或重置表单数据
      rowData.value = {clazzId :'',clazzName: '',campus: '',grade: ''}
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
      await request.post('/clazz/clazzEditSave', {
        data:{
            clazzId:props.rowData.clazzId,
            form:{
                clazzName: rowData.value.clazzName,
                campus: rowData.value.campus,
                grade: rowData.value.grade
            }
        }
      })
    } else {
      // 则调用新增接口
      await request.post('/clazz/clazzEditSave', {
        data:{
            clazzId:null,
            form:{
                clazzName: rowData.value.clazzName,
                campus: rowData.value.campus,
                grade: rowData.value.grade
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