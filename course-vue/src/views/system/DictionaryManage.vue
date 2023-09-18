<template>
  <div class="base_form">
    <div class="base_header">
      <div class="blue_column"></div>
      <div class="base_title">字典管理</div>
    </div>

    <div class="base_query_oneLine">
      <div class="query_left">
        <button
          style="margin-left: 5px"
          class="commButton"
          @click="addItemFirst"
        >
          添加数据项名
        </button>
        <button class="commButton" style="margin-left: 5px" @click="addItem">
          添加数据项
        </button>
        <button class="commButton" style="margin-left: 5px" @click="editItem">
          修改
        </button>
        <button class="commButton" style="margin-left: 5px" @click="deleteItem">
          删除
        </button>
      </div>
    </div>
    <div style="margin-top: 5px" v-if="nodes !== null">
      <el-tree
        :data="nodes"
        :props="defaultProps"
        accordion
        @node-click="onNodeClick"
      ></el-tree>
    </div>
  </div>
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
    <div class="base_title">{{ dialogTitle }}</div>
    <div class="dialog-div" style="margin-top: 5px">
      <table class="dialog-content">
        <tr>
          <td colspan="1" style="text-align: right">字典名</td>
          <td colspan="1">
            {{ editedNode.parentTitle }}
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">数据值</td>
          <td colspan="1">
            <input v-model="editedNode.value" class="commInput" />
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">数据标题</td>
          <td colspan="1">
            <input v-model="editedNode.title" class="commInput" />
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <button
              class="commButton"
              @click="close()"
              style="margin-right: 30px; margin-left: 80px"
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
import { type TreeNode } from "~/models/general";
import { getDialog } from "~/tools/comMethod";
import {
  getDictionaryTreeNodeList,
  dictionarySave,
  dictionaryDelete,
} from "~/services/systemServ";
import { message, messageConform } from "~/tools/messageBox";
export default defineComponent({
  data: () => ({
    nodes: [] as TreeNode[],
    editedNode: {} as TreeNode,
    currentNode: {} as TreeNode,
    deleteId: -1,
    dialogTitle: "",
    defaultProps: {
      id: "id",
      children: "children",
      label: "label",
    },
  }),
  async created() {
    this.nodes = await getDictionaryTreeNodeList();
  },
  methods: {
    onNodeClick(node: TreeNode) {
      this.currentNode = node;
    },
    addItemFirst() {
      this.editedNode = {} as TreeNode;
      this.editedNode.pid = null;
      this.editedNode.parentTitle = "";
      this.dialogTitle = "添加字典名对话框";
      getDialog("favDialog").show();
    },
    addItem() {
      this.editedNode = {} as TreeNode;
      this.editedNode.pid = this.currentNode.id;
      this.editedNode.parentTitle = this.currentNode.title;
      this.dialogTitle = "添加字典项对话框";
      getDialog("favDialog").show();
    },
    editItem() {
      this.editedNode = this.currentNode;
      this.dialogTitle = "修改菜单对话框";
      getDialog("favDialog").show();
    },
    close() {
      getDialog("favDialog").close();
    },
    async confirm() {
      this.close();
      const res = await dictionarySave(this.editedNode);
      if (res.code == 0) {
        message(this, "保存成功");
        this.nodes = await getDictionaryTreeNodeList();
      } else {
        message(this, res.msg);
      }
    },
    async deleteItem() {
      const result = await messageConform("确认删除吗?");
      if (!result) {
        return;
      }
      const res = await dictionaryDelete(this.currentNode.id);
      if (res.code == 0) {
        message(this, "删除成功");
        this.nodes = await getDictionaryTreeNodeList();
      } else {
        message(this, res.msg);
      }
    },
  },
});
</script>
