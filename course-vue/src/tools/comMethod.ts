import { type OptionItem } from "../models/general";
//搜索框选择项列表，获取索引值
export function getOptionItemIndex(
  itemList: OptionItem[],
  value: string
): number {
  let index = -1;
  for (let i = 0; i < itemList.length; i++) {
    if (itemList[i].value === value) {
      index = i;
      break;
    }
  }
  return index;
}
//搜索框选择项列表，获取当前值的选择项对象
export function getOptionItem(
  itemList: OptionItem[],
  value: string
): OptionItem {
  const index = getOptionItemIndex(itemList, value);
  if (index < 0) {
    return {} as OptionItem;
  }
  return itemList[index];
}
//日期转换成 yyyy-MM-dd 格式的字符串
export function formatDate(d: Date) {
  const year = d.getFullYear();
  let month = "" + (d.getMonth() + 1);
  let day = "" + d.getDate();

  if (month.length < 2) month = "0" + month;
  if (day.length < 2) day = "0" + day;

  return [year, month, day].join("-");
}
//日期转换成 yyyy-MM-dd HH:mm:ss格式的字符串
export function formatDateTime(d: Date) {
  const year = d.getFullYear();
  let month = "" + (d.getMonth() + 1);
  let day = "" + d.getDate();
  let hour = "" + d.getHours();
  let minute = "" + d.getMinutes();
  let second = "" + d.getSeconds();

  if (month.length < 2) month = "0" + month;
  if (day.length < 2) day = "0" + day;
  if (hour.length < 2) hour = "0" + hour;
  if (minute.length < 2) minute = "0" + minute;
  if (second.length < 2) second = "0" + second;
  const str1 = [year, month, day].join("-");
  const str2 = [hour, minute, second].join(":");
  return str1 + " " + str2;
}
//日期转换成 HH:mm:ss格式的字符串
export function formatTime(d: Date) {
  let hour = "" + d.getHours();
  let minute = "" + d.getMinutes();
  let second = "" + d.getSeconds();
  if (hour.length < 2) hour = "0" + hour;
  if (minute.length < 2) minute = "0" + minute;
  if (second.length < 2) second = "0" + second;
  return [hour, minute, second].join(":");
}
//根据ID获取Html元素
export function getDialog(id: string): HTMLDialogElement {
  return document.getElementById(id) as HTMLDialogElement;
}
//字符串转换成字符数组
export function getStringFromArrayJoin(array: any[]): string {
  return array.join(",");
}
//数组转换成字符串
export function getStringFromArray(array: any[]): string {
  let str = "";
  if (array == null || array.length === 0) {
    return "";
  }
  str = array[0] + "";
  for (let i = 1; i < array.length; i++) {
    str += "," + array[i];
  }
  return str;
}
//字符串转换成整数数组
export function getIntegerArrayFromStr(str: string): number[] {
  if (str == null || str.length === 0) {
    return [] as number[];
  }
  const array = str.split(",") as string[];
  const ret = [] as number[];
  for (let i = 0; i < array.length; i++) {
    ret.push(parseInt(array[i]));
  }
  return ret;
}

export function getHomeWorkList(homework: string): OptionItem[] {
  const a = homework.split(",") as string[];
  let i: number;
  const noList = [] as OptionItem[];
  let b = [] as string[];
  for (i = 0; i < a.length; i++) {
    b = a[i].split(":");
    noList.push({
      id: i + 1,
      title: b[0],
    } as OptionItem);
  }
  return noList;
}
