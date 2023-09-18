<template>
  <div class="base_form">
    <div class="base_header">
      <div class="blue_column"></div>
      <div class="base_title">菜单管理</div>
    </div>

    <div class="base_query_oneLine">
      <div class="query_left">
        <button style="margin-left: 5px" class="commButton" @click="addItemFirst">
          添加一级菜单
        </button>
      </div>
    </div>
    <div class="custom-tree-container" v-if="nodes !== null">
      <el-tree
        :data="nodes"
        show-checkbox
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <span>{{ node.label }}</span>
            <span>
              <a @click="addItem(data)"> 添加 </a>
              <a style="margin-left: 5px" @click="editItem(data)"> 修改 </a>
              <a style="margin-left: 5px" @click="deleteItem(data)"> 删除 </a>
            </span>
          </span>
        </template>
      </el-tree>
    </div>
  </div>
  <dialog
    id="favDialog"
    onclose="close()"
    style="position: absolute; top: 300px; left: 300px; width: 300px; height: 310px"
  >
    <div class="base_title">{{ dialogTitle }}</div>
    <div class="dialog-div" style="margin-top: 5px">
      <table class="dialog-content">
        <tr>
          <td colspan="1" style="text-align: right">父节点</td>
          <td colspan="1">
            {{ editedNode.parentTitle }}
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">主键ID</td>
          <td colspan="1">
            <input v-model="editedNode.id" class="commInput" :disabled="disabled" />
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">菜单名称</td>
          <td colspan="1">
            <input v-model="editedNode.value" class="commInput" />
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">菜单标题</td>
          <td colspan="1">
            <input v-model="editedNode.title" class="commInput" />
          </td>
        </tr>
        <tr>
          <td colspan="1" style="text-align: right">角色</td>
          <td colspan="1">
            <input type="checkbox" :checked="roleAdmin" />管理员
            <input type="checkbox" :checked="roleStudent" />学生
            <input type="checkbox" :checked="roleTeacher" />教师
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
import { defineComponent } from 'vue'
import { type TreeNode, type OptionItem } from '~/models/general'
import { getDialog } from '~/tools/comMethod'
import {
  getRoleOptionItemList,
  getMenuTreeNodeList,
  menuSave,
  menuDelete
} from '~/services/systemServ'
import { message, messageConform } from '~/tools/messageBox'
export default defineComponent({
  data: () => ({
    nodes: [] as TreeNode[],
    editedNode: {} as TreeNode,
    currentNode: {} as TreeNode,
    roleList: [] as OptionItem[],
    dialogTitle: '',
    disabled: false,
    editType: 0,
    roleAdmin: false,
    roleStudent: false,
    roleTeacher: false,
    defaultProps: {
      children: 'children',
      label: 'label'
    }
  }),
  async created() {
    this.roleList = await getRoleOptionItemList()
    this.nodes = await getMenuTreeNodeList()
  },
  methods: {
    addItemFirst() {
      this.editedNode = {} as TreeNode
      this.editedNode.pid = null
      this.editedNode.parentTitle = ''
      this.roleAdmin = false
      this.roleStudent = false
      this.roleTeacher = false
      this.dialogTitle = '添加一级菜单对话框'
      this.disabled = false
      this.editType = 0
      getDialog('favDialog').show()
    },
    addItem(node: TreeNode) {
      this.currentNode = node
      this.editedNode = {} as TreeNode
      this.editedNode.pid = this.currentNode.id
      this.editedNode.parentTitle = this.currentNode.title
      this.roleAdmin = false
      this.roleStudent = false
      this.roleTeacher = false
      this.dialogTitle = '添加菜单对话框'
      this.disabled = false
      this.editType = 1
      getDialog('favDialog').show()
    },
    editItem(node: TreeNode) {
      this.currentNode = node
      this.editedNode = this.currentNode
      this.roleAdmin = false
      this.roleStudent = false
      this.roleTeacher = false
      if (
        this.currentNode.userTypeIds != null &&
        this.currentNode.userTypeIds != undefined &&
        this.currentNode.userTypeIds != ''
      ) {
        if (this.currentNode.userTypeIds.indexOf('1') >= 0) {
          this.roleAdmin = true
        } else {
          this.roleAdmin = false
        }
        if (this.currentNode.userTypeIds.indexOf('2') >= 0) {
          this.roleStudent = true
        } else {
          this.roleStudent = false
        }
        if (this.currentNode.userTypeIds.indexOf('3') >= 0) {
          this.roleTeacher = true
        } else {
          this.roleTeacher = false
        }
      }
      this.dialogTitle = '修改菜单对话框'
      this.disabled = true
      this.editType = 2
      getDialog('favDialog').show()
    },
    close() {
      getDialog('favDialog').close()
    },
    async confirm() {
      this.close()
      let s = ''
      if (this.roleAdmin) {
        if (s == '') s = '1'
        else s = s + ',1'
      }
      if (this.roleStudent) {
        if (s == '') s = '2'
        else s = s + ',2'
      }
      if (this.roleAdmin) {
        if (s == '') s = '3'
        else s = s + ',3'
      }
      const res = await menuSave(this.editType, this.editedNode)
      if (res.code == 0) {
        message(this, '保存成功')
        this.nodes = await getMenuTreeNodeList()
      } else {
        message(this, res.msg)
      }
    },
    async deleteItem(node: TreeNode) {
      this.currentNode = node
      const result = await messageConform('确认删除吗?')
      if (!result) {
        return
      }
      const res = await menuDelete(this.currentNode.id)
      if (res.code == 0) {
        message(this, '删除成功')
        this.nodes = await getMenuTreeNodeList()
      } else {
        message(this, res.msg)
      }
    }
  }
})
</script>
<style>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
  margin-top: 5px;
}
</style>
