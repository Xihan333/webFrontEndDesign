<template>
  <div class="base_form">
    <div class="base_header">
      <div class="blue_column"></div>
      <div class="base_title">教师管理</div>
    </div>
    <div class="base_query_oneLine">
      <div class="query_left">
        <el-button class="commButton" @click="addItem()">添加</el-button>
      </div>
      <div class="query_right">
        <el-input v-model="numName">学号或姓名</el-input>
        <el-button class="commButton" @click="doQuery()">查询</el-button>
      </div>
    </div>
    <div class="table-content">
      <el-table
        :data="dataList"
        :header-cell-style="{
          color: '#2E2E2E',
          fontSize: '10px',
          fontWeight: '400',
          background: 'rgb(242,242,242)',
        }"
        :row-style="{ height: '10px' }"
        :cell-style="{ padding: '2px' }"
        style="width: 100%"
      >
        <el-table-column label="序号" fixed="left" width="50" color="black">
          <template v-slot="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="工号" color="black" align="center" width="100">
          <template v-slot="scope">
            {{ scope.row.num }}
          </template>
        </el-table-column>
        <el-table-column label="姓名" color="black" align="center" width="100">
          <template v-slot="scope">
            {{ scope.row.name }}
          </template>
        </el-table-column>
        <el-table-column label="院系" color="black" align="center" width="100">
          <template v-slot="scope">
            {{ scope.row.dept }}
          </template>
        </el-table-column>
        <el-table-column label="职称" color="black" align="center" width="100">
          <template v-slot="scope">
            {{ scope.row.title }}
          </template>
        </el-table-column>
        <el-table-column label="学位" color="black" align="center" width="100">
          <template v-slot="scope">
            {{ scope.row.degree }}
          </template>
        </el-table-column>
        <el-table-column
          label="证件号码"
          color="black"
          align="center"
          width="100"
        >
          <template v-slot="scope">
            {{ scope.row.card }}
          </template>
        </el-table-column>
        <el-table-column label="性别" color="black" align="center" width="100">
          <template v-slot="scope">
            {{ scope.row.genderName }}
          </template>
        </el-table-column>
        <el-table-column label="出生日期" color="black" align="center">
          <template v-slot="scope">
            {{ scope.row.birthday }}
          </template>
        </el-table-column>
        <el-table-column label="邮箱" color="black" align="center">
          <template v-slot="scope">
            {{ scope.row.email }}
          </template>
        </el-table-column>
        <el-table-column label="电话" color="black" align="center">
          <template v-slot="scope">
            {{ scope.row.phone }}
          </template>
        </el-table-column>
        <el-table-column label="地址" color="black" align="center">
          <template v-slot="scope">
            {{ scope.row.address }}
          </template>
        </el-table-column>
        <el-table-column label="操作" color="black" align="center" width="230">
          <template v-slot="scope">
            <el-button class="commButton" @click="editItem(scope.row.teacherId)"
              >编辑</el-button
            >
            <el-button class="commButton" @click="deleteItem(scope.$index)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent } from "vue";
import { getTeacherList, teacherDelete } from "~/services/personServ";

import { type TeacherItem } from "~/models/general";
import { message, messageConform } from "~/tools/messageBox";
import router from "~/router";

export default defineComponent({
  //数据
  data: () => ({
    numName: "",
    dataList: [] as TeacherItem[],
  }),
  //初始加载一次,直接获取教师列表
  created() {
    this.doQuery();
  },
  methods: {
    //查询教师列表
    async doQuery() {
      this.dataList = await getTeacherList(this.numName);
    },
    //添加教师,跳转到教师信息页面
    addItem() {
      router.push({ name: "TeacherInfo" });
    },
    //编辑教师,跳转到教师信息页面
    editItem(teacherId: number) {
      router.push({
        name: "TeacherInfo",
        query: { teacherId: teacherId },
      });
    },
    //删除教师
    async deleteItem(index: number) {
      const result = await messageConform("确认删除教师吗?");
      if (!result) {
        return;
      }
      console.log(this.dataList[index]);
      const res = await teacherDelete(this.dataList[index].teacherId);
      if (res.code == 0) {
        message(this, "删除成功");
        this.dataList.splice(index, 1);
      } else {
        message(this, res.msg);
      }
    },
  },
});
</script>
