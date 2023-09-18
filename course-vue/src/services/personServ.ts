import { generalRequest } from "~/services/genServ";
import {
  type DataResponse,
  type StudentItem,
  type FamilyMemberItem,
  type TeacherItem,
} from "~/models/general";
//获取学生列表分页数据后台数据请求方法
export async function getStudentPageData(
  numName: String | null,
  currentPage: number
): Promise<DataResponse> {
  const res = await generalRequest("/api/student/getStudentPageData", {
    numName: numName,
    currentPage: currentPage,
  });
  return res as DataResponse;
}
//删除学生后台数据请求方法
export async function studentDelete(studentId: number): Promise<DataResponse> {
  const res = await generalRequest("/api/student/studentDelete", {
    studentId: studentId,
  });
  return res as DataResponse;
}
//获取学生基本信息后台数据请求方法
export async function getStudentInfo(studentId: number): Promise<StudentItem> {
  const res = await generalRequest("/api/student/getStudentInfo", {
    studentId: studentId,
  });
  return res.data as StudentItem;
}
//保存学生基本信息后台数据请求方法
export async function studentEditSave(
  studentId: number | null,
  form: StudentItem
): Promise<DataResponse> {
  const res = await generalRequest("/api/student/studentEditSave", {
    studentId: studentId,
    form: form,
  });
  return res as DataResponse;
}
//获取学生家庭成员列表数据后台数据请求方法
export async function getFamilyMemberList(
  studentId: number
): Promise<FamilyMemberItem[]> {
  const res = await generalRequest("/api/student/getFamilyMemberList", {
    studentId: studentId,
  });
  return res.data as FamilyMemberItem[];
}
//保存学生家庭成员信息后台数据请求方法
export async function familyMemberSave(
  form: FamilyMemberItem
): Promise<DataResponse> {
  const res = await generalRequest("/api/student/familyMemberSave", {
    form: form,
  });
  return res as DataResponse;
}
//删除学生家庭成员信息后台数据请求方法
export async function familyMemberDelete(
  memberId: number
): Promise<DataResponse> {
  const res = await generalRequest("/api/student/familyMemberDelete", {
    memberId: memberId,
  });
  return res as DataResponse;
}
//获取教师列表数据后台数据请求方法
export async function getTeacherList(numName: String | null): Promise<[]> {
  const res = await generalRequest("/api/teacher/getTeacherList", {
    numName: numName,
  });
  return res.data as [];
}
//获取教师基本信息后台数据请求方法
export async function getTeacherInfo(
  teacherId: number | null
): Promise<TeacherItem> {
  const res = await generalRequest("/api/teacher/getTeacherInfo", {
    teacherId: teacherId,
  });
  return res.data as TeacherItem;
}
//保存教师基本信息后台数据请求方法
export async function teacherEditSave(
  teracherId: number,
  form: TeacherItem
): Promise<DataResponse> {
  const res = await generalRequest("/api/teacher/teacherEditSave", {
    teacherId: teracherId,
    form: form,
  });
  return res as DataResponse;
}
//删除教师后台数据请求方法
export async function teacherDelete(teacherId: number): Promise<DataResponse> {
  const res = await generalRequest("/api/teacher/teacherDelete", {
    teacherId: teacherId,
  });
  return res as DataResponse;
}
