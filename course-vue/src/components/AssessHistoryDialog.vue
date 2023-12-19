<template>
    <div class="AssessHistoryDialog">
        <el-dialog :model-value="show"
                   :before-close="handleClose"
                   class="dialog" width="37%"
        >
        <template #header>
          <div style="text-align: center; font-size: 18px">评论详情</div>
        </template>
        <el-form :model="form" size="large">
            <el-form-item label="被评价人">
                <el-input v-model="rowData.student.person.name"
                    maxlength="20"
                    disabled placeholder="被评价人姓名"
                    show-word-limit
                    />
            </el-form-item>
            <el-form-item label="学号">
                <el-input v-model="rowData.student.person.num"
                    maxlength="20"
                    disabled placeholder="被评价人学号"
                    show-word-limit
                    />
            </el-form-item>
            <el-form-item label="评价时间">
                <el-input v-model="rowData.evalTime"
                    maxlength="20"
                    disabled placeholder="评价时间"
                    show-word-limit
                    />
            </el-form-item>
            <el-form-item label="评价内容">
                <el-input v-model="rowData.eval"
                    maxlength="200"
                    disabled placeholder="评价内容"
                    :autosize="{ minRows: 3}"
                    type="textarea"
                    show-word-limit
                    />
            </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="cancel">取消</el-button>
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
    evaluationId:'',
    name:'',
    num:'',
    evalTime:'',
    eval:''
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
    if(props.dialogMode === 'view') {
      // 如果是查看模式，填充数据
      rowData.value = { ...props.rowData }
      console.log(rowData.value)
    }
})

const emit = defineEmits(['update:show','updateTable'])


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
  
