<!-- 编辑成果奖励的弹窗组件 -->
<template>
    <div class="AchievementDialog">
      <!-- 使用v-model的话不能直接赋值为props中的属性, 但是
       element-plus还贴心地准备了model-value这个属性 -->
      <el-dialog :model-value="show"
                 :before-close="handleClose"
                 class="dialog" width="37%"
      >
        <template #header>
          <div style="text-align: center; font-size: 18px">新增成果奖励</div>
        </template>
        <el-form :model="form" size="large">
          <el-form-item label="成果奖励名称">
            <el-input v-model="rowData.achievementName" 
              maxlength="50"
              placeholder="请输入成果奖励名称"
              show-word-limit 
            />
          </el-form-item>
          <el-form-item label="级别">
            <el-input v-model="rowData.level"
              maxlength="20"
              placeholder="请输入成果奖励级别"
              show-word-limit 
            />
          </el-form-item>
          <el-form-item label="类别">
            <el-input v-model="rowData.type"
              maxlength="20"
              placeholder="请输入成果奖励类别"
              show-word-limit 
            />
          </el-form-item>
          <el-form-item label="时间">
            <!-- 禁用时间，复制的时候记得连同script里面的disabledDate方法一起带着 -->
            <el-date-picker
              v-model="rowData.time"
              type="date"
              :disabled-date="disabledDate"
              placeholder="请选择时间"
              :size="size"
              format="YYYY/MM/DD"
              value-format="YYYY/MM/DD"
            />
          </el-form-item>
          <el-form-item label="内容">
            <el-input
              v-model="rowData.content"
              maxlength="200"
              :autosize="{ minRows: 2 }"
              type="textarea"
              placeholder="请输入内容"
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
  import { defineProps, defineEmits, ref, watchEffect } from 'vue'
  import { ElMessageBox } from 'element-plus'
  import request from '../request/axios_config.js'

  
  const rowData= ref({
    achievementId: '',
    achievementName: '',
    level: '',
    type: '',
    time: '',
    content:''
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

  // 监听 dialogMode 的变化
  watchEffect(() => {
    if(props.dialogMode ==='add'){
        //新增则清空或重置表单数据
        rowData.value = {achievementId :'',level: '',type: '',time: '',content:''}    
      }
    else if(props.dialogMode === 'view'){
        rowData.value = {...props.rowData}
        console.log(rowData.value)
    }
})


  const emit = defineEmits(['update:show','updateTable'])
  
  const submit = async () => {
    try {
      if (props.dialogMode === 'view') {
        // 调用修改接口
        console.log(props.rowData.achievementId)
        await request.post('/achievement/achievementStudentEditSave', {
          data:{
              achievementId:props.rowData.achievementId,
              form:{
                  achievementName: rowData.value.achievementName,
                  level: rowData.value.level,
                  type: rowData.value.type,
                  time: rowData.value.time,
                  content:rowData.value.content
              }
          }
        })
    } else {
      // 则调用新增接口
        await request.post('/achievement/achievementStudentEditSave', {
          data:{
              achievementId:null,
              form:{
                  achievementName: rowData.value.achievementName,
                  level: rowData.value.level,
                  type: rowData.value.type,
                  time: rowData.value.time,
                  content:rowData.value.content
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
  