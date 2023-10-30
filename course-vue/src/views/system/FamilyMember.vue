<template>
  <div class="base_form">
    <!-- 界面标题部分 -->
    <div class="base_header">
      <div class="blue_column"></div>
      <div class="base_title">学生家庭成员信息</div>
    </div>
    <!-- 界面上部操作按钮区 -->
    <div class="base_query_oneLine">
      <div class="query_left">
        <el-button class="commButton" @click="addRow()">添加一行</el-button>
      </div>
    </div>
    <!-- 数据表区 -->
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
        <el-table-column label="关系" width="150" color="black" align="center">
          <template v-slot="scope">
            <el-input v-model="scope.row.relation" style="width: 90%" />
          </template>
        </el-table-column>
        <el-table-column label="姓名" width="150" color="black" align="center">
          <template v-slot="scope">
            <el-input v-model="scope.row.name" style="width: 90%" />
          </template>
        </el-table-column>
        <el-table-column label="性别" width="150" color="black" align="center">
          <template v-slot="scope">
            <el-select
              v-model="scope.row.gender"
              placeholder="请选择性别"
              style="width: 90%"
            >
              <el-option
                v-for="items in genderList"
                :key="items.value"
                :label="items.title"
                :value="items.value"
              >
              </el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="年龄" width="100" color="black" align="center">
          <template v-slot="scope">
            <el-input v-model="scope.row.age" style="width: 90%" />
          </template>
        </el-table-column>
        <el-table-column label="单位" width="150" color="black" align="center">
          <template v-slot="scope">
            <el-input v-model="scope.row.unit" style="width: 90%" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" color="black" align="center">
          <template v-slot="scope">
            <el-button class="commButton" @click="saveItem(scope.$index)"
              >保存</el-button
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
<!-- TS处理 -->
<script lang="ts">
import { defineComponent } from "vue";
import {
  getFamilyMemberList,
  familyMemberSave,
  familyMemberDelete,
} from "~/services/personServ";
import { type FamilyMemberItem, type OptionItem } from "~/models/general";
import { message, messageConform } from "~/tools/messageBox";

export default defineComponent({
  // 双向绑定数据
  data: () => ({
    genderList: [
      {
        value: "1",
        title: "男",
      },
      {
        value: "2",
        title: "女",
      },
    ] as OptionItem[],
    studentId: null as number | null,
    dataList: [] as FamilyMemberItem[],
  }),
  //页面初始创建时执行一次
  async created() {
    const res = this.$route.query.studentId;
    if (res != null) {
      this.studentId = parseInt(res.toString());
    }
    if (this.studentId != null) {
      this.dataList = await getFamilyMemberList(this.studentId);
    }
  },
  methods: {
    //在数据表中添加一行
    addRow() {
      this.dataList.push({ studentId: this.studentId } as FamilyMemberItem);
    },
    //保存数据表中的一行
    async saveItem(index: number) {
      const item = this.dataList[index];
      const res = await familyMemberSave(item);
      if (res.code == 0) {
        message(this, "保存成功");
      } else {
        message(this, res.msg);
      }
    },
    //删除数据表中的一行
    async deleteItem(index: number) {
      const result = await messageConform("确认删除当前成员吗?");
      if (!result) {
        return;
      }
      const res = await familyMemberDelete(this.dataList[index].memberId);
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
