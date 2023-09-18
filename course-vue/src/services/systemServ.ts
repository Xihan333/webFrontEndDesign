import { generalRequest } from "~/services/genServ";
import {
  type DataResponse,
  type OptionItem,
  type TreeNode,
} from "~/models/general";
//获取角色选择项列表后台数据请求方法
export async function getRoleOptionItemList(): Promise<OptionItem[]> {
  const res = await generalRequest("/api/base/getRoleOptionItemList", {});
  return res.itemList as OptionItem[];
}
//获取字典选择项列表后台数据请求方法
export async function getDictionaryOptionItemList(
  code: string
): Promise<OptionItem[]> {
  const res = await generalRequest("/api/base/getDictionaryOptionItemList", {
    code: code,
  });
  return res.itemList as OptionItem[];
}
//获取菜单列表数据后台数据请求方法
export async function getMenuTreeNodeList(): Promise<TreeNode[]> {
  const res = await generalRequest("/api/base/getMenuTreeNodeList", {});
  return res as TreeNode[];
}
//菜单删除后台数据请求方法
export async function menuDelete(id: number): Promise<DataResponse> {
  const res = await generalRequest("/api/base/menuDelete", {
    id: id,
  });
  return res as DataResponse;
}
//菜单保存后台数据请求方法
export async function menuSave(
  editType: number,
  node: TreeNode
): Promise<DataResponse> {
  const res = await generalRequest("/api/base/menuSave", {
    editType: editType,
    node: node,
  });
  return res as DataResponse;
}
//获取字典树列表数据后台数据请求方法
export async function getDictionaryTreeNodeList(): Promise<TreeNode[]> {
  const res = await generalRequest("/api/base/getDictionaryTreeNodeList", {});
  return res as TreeNode[];
}
//字典删除后台数据请求方法
export async function dictionaryDelete(id: number): Promise<DataResponse> {
  const res = await generalRequest("/api/base/dictionaryDelete", {
    id: id,
  });
  return res as DataResponse;
}
//字典保存后台数据请求方法
export async function dictionarySave(node: TreeNode): Promise<DataResponse> {
  const res = await generalRequest("/api/base/dictionarySave", node);
  return res as DataResponse;
}
