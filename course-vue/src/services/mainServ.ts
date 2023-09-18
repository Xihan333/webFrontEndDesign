import { generalRequest } from "~/services/genServ";
import {
  type MenuInfo,
  type DataResponse,
  type ValidateCode,
} from "~/models/general";

//获取菜单列表后台数据请求方法
export async function getMenuList(): Promise<MenuInfo[]> {
  const res = await generalRequest("/api/base/getMenuList", null);
  return res.data as MenuInfo[];
}
//获取验证码后台数据请求方法
export async function getValidateCode(): Promise<ValidateCode> {
  const res = await generalRequest("/auth/getValidateCode", null);
  return res.data as ValidateCode;
}
//后台检验验证码数据请求方法
export function testValidateInfo(data: Object): Promise<DataResponse> {
  const res = generalRequest("/auth/testValidateInfo", data);
  return res as Promise<DataResponse>;
}
//重置密码后台数据请求方法
export function resetPassWord(data: Object) {
  return generalRequest("/auth/resetPassWord", data);
}
//用户注册后台数据请求方法
export function registerUser(data: Object) {
  return generalRequest("/auth/registerUser", data);
}
//获取主页面展示数据的后台数据请求方法
export async function getMainPageData(): Promise<DataResponse> {
  const res = await generalRequest("/api/statistics/getMainPageData", null);
  return res as DataResponse;
}
