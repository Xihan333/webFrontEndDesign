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
            <el-button type="primary" @click="pass">
              审核通过
            </el-button>
            <el-button type="primary" @click="fail">
              审核不通过
            </el-button>
            <el-button @click="cancel">取消</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { defineProps, defineEmits, ref,watch } from 'vue'
  import { ElMessageBox } from 'element-plus'
  import { ElMessage } from 'element-plus'
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
  watch(() => props.dialogMode, (newMode) => {
    if (newMode === 'add') {
      // 如果是新增模式，清空或重置表单数据
      rowData.value = {achievementId :'',level: '',type: '',time: '',content:''}
  }
     else if (newMode === 'view') {
      // 如果是查看模式，填充数据
      rowData.value = { ...props.rowData }
      console.log(rowData.value)
    }
  })


  const emit = defineEmits(['update:show','updateTable'])
  const pass = async () => {
    const res = await request.post('/achievement/examine/achievementPass',{
        data:{
          achievementId:props.rowData.achievementId
        } 
      })
      console.log(res.code)
      console.log(res)
      if(res.data.code==200){
        ElMessage({
          message: '审核通过提交成功！',
          type: 'success',
          offset: 150
        })
      }
      else{
        ElMessage({
          message: '提交失败，请重试！',
          type: 'error',
          offset: 150
        })
      }
      emit('update:show', false)
      emit('updateTable')
  }

  const fail = async () => {
    const res = await request.post('/achievement/examine/achievementFail',{
        data:{
          achievementId:props.rowData.achievementId
        } 
      })
      console.log(res.code)
      console.log(res)
      if(res.data.code==200){
        ElMessage({
          message: '审核不通过提交成功！',
          type: 'success',
          offset: 150
        })
      }
      else{
        ElMessage({
          message: '提交失败，请重试！',
          type: 'error',
          offset: 150
        })
      }
      emit('update:show', false)
      emit('updateTable')
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
  