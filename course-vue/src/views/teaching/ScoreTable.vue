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
        <span style="margin-top: 5px">学生</span>
        <select class="commInput" v-model="studentId">
          <option value="0">请选择...</option>
          <option v-for="item in studentList" :key="item.id" :value="item.id">
            {{ item.title }}
          </option>
        </select>
        <span style="margin-top: 5px">课程</span>
        <select class="commInput" v-model="courseId">
          <option value="0">请选择...</option>
          <option v-for="item in courseList" :key="item.id" :value="item.id">
            {{ item.title }}
          </option>
        </select>
        <button style="margin-left: 5px" class="commButton" @click="query()">
          查询
        </button>
      </div>
    </div>
    <div class="table_center" style="margin-top: 5px">
      <table class="content">
        <tr class="table_th">
          <td>学号</td>
          <td>姓名</td>
          <td>班级</td>
          <td>课程号</td>
          <td>课程名</td>
          <td>学分</td>
          <td>成绩</td>
          <td>操作</td>
        </tr>
        <tr v-for="item in scoreList" :key="item.scoreId">
          <td>{{ item.studentNum }}</td>
          <td>{{ item.studentName }}</td>
          <td>{{ item.className }}</td>
          <td>{{ item.courseNum }}</td>
          <td>{{ item.courseName }}</td>
          <td>{{ item.credit }}</td>
          <td>{{ item.mark }}</td>
          <td>
            <button class="table_edit_button" @click="editItem(item)">
              编辑
            </button>
            <button
              class="table_delete_button"
              @click="deleteItem(item.scoreId)"
            >
              删除
            </button>
          </td>
        </tr>
      </table>
    </div>
  </div>
  <!-- 成绩修改对话框显示 -->
  <dialog
    id="favDialog"
    onclose="close()"
    style="
      position: absolute;
      top: 300px;
      left: 300px;
      width: 300px;
      height: 210px;
    "
  >
    <div class="base_title">成绩添加修改对话框</div>
    <div class="dialog-div" style="margin-top: 5px">
      <table class="dialog-content">
        <tr>
          <td colspan="1" style="text-align: right">课程号</td>
          <td colspan="1">
            <select class="commInput" v-model="editedItem.studentId">
              <option value="0">请选择...</option>
              <option
                v-for="item in studentList"
                :key="item.id"
                :value="item.id"
              >
                {{ item.title }}
              </option>
            </select>
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">课程名</td>
          <td colspan="1">
            <select class="commInput" v-model="editedItem.courseId">
              <option value="0">请选择...</option>
              <option
                v-for="item in courseList"
                :key="item.id"
                :value="item.id"
              >
                {{ item.title }}
              </option>
            </select>
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">成绩</td>
          <td colspan="1">
            <input v-model="editedItem.mark" class="commInput" />
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <button
              class="commButton"
              @click="close()"
              style="margin-right: 30px"
            >
              取消
            </button>
            <button class="commButton" @click="confirm()">确认</button>
          </td>
        </tr>
      </table>
    </div>
  </dialog>
</template>
<script lang="ts">
import { defineComponent } from "vue";
import {
  getScoreList,
  getStudentItemOptionList,
  getCourseItemOptionList,
  scoreSave,
  scoreDelete,
} from "~/services/teachingServ";
import { type OptionItem, type ScoreItem } from "~/models/general";
import { message, messageConform } from "~/tools/messageBox";
export default defineComponent({
  data: () => ({
    scoreList: [] as ScoreItem[],
    studentId: null,
    courseId: null,
    editedItem: {} as ScoreItem,
    studentList: [] as OptionItem[],
    courseList: [] as OptionItem[],
    deleteId: -1,
  }),
  computed: {},
  created() {
    this.initialize();
  },

  methods: {
    // 初始化,获取学生选择项列表和课程选择项列表
    async initialize() {
      this.scoreList = await getScoreList(this.studentId, this.courseId);
      this.studentList = await getStudentItemOptionList();
      this.courseList = await getCourseItemOptionList();
    },
    // 查询
    async query() {
      this.scoreList = await getScoreList(this.studentId, this.courseId);
    },
    // 添加成绩,显示成绩修改对画框
    addItem() {
      this.editedItem = {} as ScoreItem;
      const dialog = document.getElementById("favDialog") as HTMLDialogElement;
      dialog.show();
    },
    // 编辑成绩,显示成绩修改对画框
    editItem(item: ScoreItem) {
      this.editedItem = item;
      const dialog = document.getElementById("favDialog") as HTMLDialogElement;
      dialog.show();
    },
    // 关闭成绩修改对话框
    close() {
      const dialog = document.getElementById("favDialog") as HTMLDialogElement;
      dialog.close();
    },
    // 确认成绩修改对话框
    async confirm() {
      this.close();
      const res = await scoreSave(
        this.editedItem.scoreId,
        this.editedItem.studentId,
        this.editedItem.courseId,
        this.editedItem.mark
      );
      if (res.code == 0) {
        message(this, "保存成功");
        this.query();
      } else {
        message(this, res.msg);
      }
    },
    // 删除成绩
    async deleteItem(scoreId: number) {
      const result = await messageConform("确认删除吗?");
      if (!result) {
        return;
      }
      const res = await scoreDelete(scoreId);
      if (res.code == 0) {
        message(this, "删除成功");
        this.query();
      } else {
        message(this, res.msg);
      }
    },
  },
});
</script>
