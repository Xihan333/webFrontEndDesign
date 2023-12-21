<!-- 编辑社会实践的弹窗组件 -->
<template>
    <div class="SocialDialog">
      <!-- 使用v-model的话不能直接赋值为props中的属性, 但是
       element-plus还贴心地准备了model-value这个属性 -->
      <el-dialog :model-value="show"
                 :before-close="handleClose"
                 class="dialog" width="37%"
      >
        <template #header>
          <div style="text-align: center; font-size: 18px">新增社会实践</div>
        </template>
        <el-form :model="form" size="large">
          <el-form-item label="实践主题">
            <el-input v-model="rowData.theme" 
              maxlength="50" disabled
              placeholder="请输入实践主题"
              show-word-limit 
            />
          </el-form-item>
          <el-form-item label="团队名称">
            <el-input v-model="rowData.groupName"
              maxlength="20" disabled
              placeholder="请输入团队名称"
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
              :size="size" disabled
              format="YYYY/MM/DD"
              value-format="YYYY/MM/DD"
            />
          </el-form-item>
          <el-form-item label="实践地点">
            <el-input v-model="rowData.location" disabled placeholder="请输入实践地点"/>
          </el-form-item>
          <el-form-item label="摘要">
            <el-input
              v-model="rowData.digest"
              maxlength="200"
              :autosize="{ minRows: 2 }"
              type="textarea" disabled
              placeholder="请输入摘要"
              show-word-limit 
            />
          </el-form-item>
          <el-form-item label="实践成果">
            <el-input
              v-model="rowData.harvest"
              :autosize="{ minRows: 2 }"
              type="textarea" disabled
              placeholder="请阐述实践成果"
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
  import { defineProps, defineEmits, ref, watch} from 'vue'
  import { ElMessageBox, ElMessage } from 'element-plus'
  import request from '../request/axios_config.js'

  
  const rowData= ref({
    socialId : '',
    theme: '',
    groupName: '',
    day: '',
    location:'',
    digest:'',
    harvest:''
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

  // 监听
  watchEffect(() => {
    if(props.dialogMode ==='add'){
        //新增则清空或重置表单数据
        rowData.value = {socialId :'',theme: '',groupName: '',day: '',location:'',digest:'',harvest:''}
    }
    else if(props.dialogMode === 'view'){
        rowData.value = {...props.rowData}
        console.log(rowData.value)
    }
})


  const emit = defineEmits(['update:show','updateTable'])
  
  const pass = async () => {
    const res = await request.post('/social/examine/socialPass',{
        data:{
          socialId:props.rowData.socialId
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
    const res = await request.post('/social/examine/socialFail',{
        data:{
          socialId:props.rowData.socialId
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
  