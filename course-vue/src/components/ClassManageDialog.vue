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
                    <el-input v-model="rowData.value.clazzName"
                      maxlength="20"
                      placeholder="请输入班级名称"
                      show-word-limit
                      />
                </el-form-item>
                <el-form-item label="学院">
                  <el-select v-model="rowData.value.campusId" value-key="id"
                       placeholder="请选择学院" clearable @change="changeSelect">
                      <el-option
                        v-for="item in campusType"
                        :key="item.id"
                        :label="item.label"
                        :value="item.value"
                        />
                  </el-select>
                </el-form-item>
                <el-form-item label="年级">
                  <el-select v-model="rowData.value.gradeId" value-key="id"
                       placeholder="请选择年级" clearable @change="changeSelect">
                    <el-option
                      v-for="item in gradeType"
                      :key="item.id"
                      :label="item.label"
                      :value="item.value"
                      />
                    </el-select>
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
import { defineProps, defineEmits, ref,watchEffect,computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import request from '../request/axios_config.js'
import { filterOptions } from '../assets/js/config.js'


const rowData= ref({
    clazzId:'',
    clazzName:'',
    campusId:'',
    campusName:'',
    gradeId:'',
    gradeName:'',
})

const props = defineProps({
    show: {
      type: Boolean,
      required: true,
    },
    rowData:Object,
    dialogMode: String
})

const campusType = computed(() => filterOptions.allCampuses)
const gradeType = computed(() => filterOptions.allGrades) 

watchEffect(() => {
    if (props.dialogMode === 'add') {
      // 如果是新增模式，清空或重置表单数据
      rowData.value = {clazzId :'',clazzName: '',campusName: '',gradeName: '',campusId:'',gradeId:''}
  }
     else if (props.dialogMode === 'view') {
      // 如果是查看模式，填充数据
      rowData.value = { ...props.rowData }
      console.log(rowData.value.clazzName)
    }
})

const emit = defineEmits(['update:show','updateTable'])

const submit = async () => {
    try {
    if (props.dialogMode === 'view') {
      // 调用修改接口
      console.log(props.rowData.clazzId)
      await request.post('/clazz/clazzEditSave', {
        data:{
            clazzId:props.rowData.clazzId,
            clazz:{
              clazzName:rowData.value.clazzName,
              campusId: rowData.value.campusId,
              gradeId: rowData.value.gradeId
            }
        }
      })
    } else {
      // 则调用新增接口
      await request.post('/clazz/clazzEditSave', {
        data:{
            clazzId:'',
            clazz:{
              clazzName:rowData.value.clazzName,
              campusId: rowData.value.campusId,
              gradeId: rowData.value.gradeId
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