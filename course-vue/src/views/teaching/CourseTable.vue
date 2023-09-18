<template>
  <div class="base_form">
    <div class="base_header">
      <div class="blue_column"></div>
      <div class="base_title">课程管理</div>
    </div>

    <div class="base_query_oneLine">
      <div class="query_left">
        <button class="commButton" @click="addItem()">添加</button>
      </div>
      <div class="query_right">
        <span style="margin-top: 5px">课程号或课程名</span>
        <input type="text" v-model="numName" style="margin-left: 10px; width: 230px" />
        <button style="margin-left: 5px" class="commButton" @click="query()">查询</button>
      </div>
    </div>
    <div class="table_center" style="margin-top: 5px">
      <table class="content">
        <tr class="table_th">
          <td>课程号</td>
          <td>课程名</td>
          <td>学分</td>
          <td>材料路径</td>
          <td>前序课</td>
          <td>操作</td>
        </tr>
        <tr v-for="item in courseList" :key="item.courseId">
          <td>{{ item.num }}</td>
          <td>{{ item.name }}</td>
          <td>{{ item.credit }}</td>
          <td>{{ item.coursePath }}</td>
          <td>{{ item.preCourse }}</td>
          <td>
            <button class="table_edit_button" @click="editItem(item)">编辑</button>
            <button class="table_delete_button" @click="deleteItem(item.courseId)">删除</button>
          </td>
        </tr>
      </table>
    </div>
  </div>
  <!-- 对话框显示 -->
  <dialog
    id="favDialog"
    onclose="close()"
    style="position: absolute; top: 300px; left: 300px; width: 300px; height: 310px"
  >
    <div class="base_title">课程添加修改对话框</div>
    <div class="dialog-div" style="margin-top: 5px">
      <table class="content">
        <tr>
          <td colspan="1" style="text-align: right">课程号</td>
          <td colspan="1"><input v-model="form.num" style="width: 97%" /></td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">课程名</td>
          <td colspan="1"><input v-model="form.name" style="width: 97%" /></td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">学分</td>
          <td colspan="1">
            <input v-model="form.credit" style="width: 97%" />
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">资料路径</td>
          <td colspan="1">
            <input v-model="form.coursePath" style="width: 97%" />
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">前序课</td>
          <td colspan="1">
            <select class="commInput" v-model="form.preCourseId">
              <option v-for="item in courseSelectList" :key="item.id" :value="item.id">
                {{ item.title }}
              </option>
            </select>
          </td>
        </tr>

        <tr>
          <td colspan="2">
            <button class="commButton" @click="close()" style="margin-right: 30px">取消</button>
            <button class="commButton" @click="confirm()">确认</button>
          </td>
        </tr>
      </table>
    </div>
  </dialog>
</template>

<script lang="ts">
import { type CourseItem, type OptionItem } from '~/models/general'
import { defineComponent } from 'vue'
import { getCourseList, courseDelete, courseSave } from '~/services/teachingServ'
import { message, messageConform } from '~/tools/messageBox'
import { getDialog } from '~/tools/comMethod'
export default defineComponent({
  // 双向绑定数据
  data: () => ({
    numName: '',
    courseList: [] as CourseItem[],
    courseSelectList: [] as OptionItem[],
    deleteId: -1,
    form: {} as CourseItem
  }),
  //初始加载一次,直接获取教师列表
  created() {
    this.query()
  },

  methods: {
    //设置课程选择列表
    makeSelectCourseList() {
      this.courseSelectList = []
      for (let i = 0; i < this.courseList.length; i++) {
        const item = this.courseList[i]
        this.courseSelectList.push({
          id: item.courseId,
          value: item.num,
          title: item.num + '-' + item.name
        })
      }
    },
    //查询课程列表
    async query() {
      this.courseList = await getCourseList(this.numName)
      this.makeSelectCourseList()
    },
    //添加课程,显示对话框
    addItem() {
      this.form = {} as CourseItem
      getDialog('favDialog').show()
    },
    //编辑课程,显示对话框
    editItem(item: CourseItem) {
      this.form = item
      getDialog('favDialog').show()
    },
    //关闭对话框
    close() {
      getDialog('favDialog').close()
    },
    //确认对话框
    async confirm() {
      this.close()
      const res = await courseSave(this.form)
      if (res.code == 0) {
        message(this, '保存成功')
        this.query()
      } else {
        message(this, res.msg)
      }
    },
    //删除课程
    async deleteItem(courseId: number) {
      const result = await messageConform('确认删除吗?')
      if (!result) {
        return
      }
      const res = await courseDelete(courseId)
      if (res.code == 0) {
        message(this, '删除成功')
        this.query()
      } else {
        message(this, res.msg)
      }
    }
  }
})
</script>
<style></style>
