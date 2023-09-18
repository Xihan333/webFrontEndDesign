import { generalRequest, uploadRequest } from "~/services/genServ";
import {
  type DataResponse,
  type CourseItem,
  type OptionItem,
  type TreeNode,
  type TaskStudentItem,
  type TermItem,
} from "~/models/general";
//获取课程数据表后台数据请求方法
export async function getCourseList(
  numName: string | null
): Promise<CourseItem[]> {
  const res = await generalRequest("/api/course/getCourseList", {
    numName: numName,
  });
  return res.data as CourseItem[];
}
//删除课程后台数据请求方法
export async function courseDelete(courseId: number): Promise<DataResponse> {
  const res = await generalRequest("/api/course/courseDelete", {
    courseId: courseId,
  });
  return res as DataResponse;
}
//课程保存后台数据请求方法
export async function courseSave(data: CourseItem): Promise<DataResponse> {
  const res = await generalRequest("/api/course/courseSave", data);
  return res as DataResponse;
}
//获取学生选择项列表后台数据请求方法
export async function getStudentItemOptionList(): Promise<OptionItem[]> {
  const res = await generalRequest("/api/score/getStudentItemOptionList", null);
  return res.itemList as OptionItem[];
}
//获取课程选择项列表后台数据请求方法
export async function getCourseItemOptionList(): Promise<OptionItem[]> {
  const res = await generalRequest("/api/score/getCourseItemOptionList", null);
  return res.itemList as OptionItem[];
}
//获取成绩列表后台数据请求方法
export async function getScoreList(
  studentId: number | null,
  courseId: number | null
): Promise<[]> {
  const res = await generalRequest("/api/score/getScoreList", {
    studentId: studentId,
    courseId: courseId,
  });
  return res.data as [];
}
//成绩保存后台数据请求方法
export async function scoreSave(
  scoreId: number | null,
  studentId: number,
  courseId: number,
  mark: number
): Promise<DataResponse> {
  const res = await generalRequest("/api/score/scoreSave", {
    scoreId: scoreId,
    studentId: studentId,
    courseId: courseId,
    mark: mark,
  });
  return res as DataResponse;
}
//成绩删除后台数据请求方法
export async function scoreDelete(scoreId: number): Promise<DataResponse> {
  const res = await generalRequest("/api/score/scoreDelete", {
    scoreId: scoreId,
  });
  return res as DataResponse;
}

// team 下面这些函数不是框架示例程序代码部分，适用于我们的课程学管理的分组和成绩管理相关后台处理函数，这里不在一一说明

export async function getMaterialCourseItemOptionList(
  role: string
): Promise<OptionItem[]> {
  const res = await generalRequest(
    "/api/material/getMaterialCourseItemOptionList",
    { role: role }
  );
  return res.itemList as OptionItem[];
}

export async function getMaterialTreeNodeList(
  courseId: number
): Promise<TreeNode[]> {
  const res = await generalRequest("/api/material/getMaterialTreeNodeList", {
    courseId: courseId,
  });
  return res as TreeNode[];
}
export async function materialDelete(id: number): Promise<DataResponse> {
  const res = await generalRequest("/api/material/materialDelete", {
    id: id,
  });
  return res as DataResponse;
}
export async function materialSave(
  courseId: number,
  node: TreeNode
): Promise<DataResponse> {
  const res = await generalRequest("/api/material/materialSave", {
    courseId: courseId,
    node: node,
  });
  return res as DataResponse;
}

export async function getTaskStudentDataList(
  currentTaskId: number | null
): Promise<TaskStudentItem[]> {
  const res = await generalRequest("/api/team/getTaskStudentDataList", {
    currentTaskId: currentTaskId,
  });
  return res.data as TaskStudentItem[];
}

export async function importStudentExcl(
  currentTaskId: number | null,
  file: any
): Promise<any> {
  const formData = new FormData();
  formData.append("file", file);
  const res = await uploadRequest(
    "/api/team/importStudentExcl?currentTaskId=" + currentTaskId,
    formData
  );
  return res as DataResponse;
}
export async function deleteTaskStudent(
  taskStudentId: number
): Promise<DataResponse> {
  const res = await generalRequest("/api/team/deleteTaskStudent", {
    taskStudentId: taskStudentId,
  });
  return res as DataResponse;
}

export async function getTeamOptionItemList(
  currentTaskId: number | null
): Promise<OptionItem[]> {
  const res = await generalRequest("/api/team/getTeamOptionItemList", {
    currentTaskId: currentTaskId,
  });
  return res.data as OptionItem[];
}
export async function getTaskOptionItemList(): Promise<OptionItem[]> {
  const res = await generalRequest("/api/team/getTaskOptionItemList", {});
  return res.data as OptionItem[];
}

export async function getStudentTeamInfo(
  taskStudentId: number | null
): Promise<DataResponse> {
  const res = await generalRequest("/api/team/getStudentTeamInfo", {
    taskStudentId: taskStudentId,
  });
  return res as DataResponse;
}
export async function saveStudentTeamInfo(
  taskStudentId: number,
  weight: number
): Promise<DataResponse> {
  const res = await generalRequest("/api/team/saveStudentTeamInfo", {
    taskStudentId: taskStudentId,
    weight: weight,
  });
  return res as DataResponse;
}
export async function setStudentTeam(
  taskStudentId: number,
  teamId: number
): Promise<DataResponse> {
  const res = await generalRequest("/api/team/setStudentTeam", {
    taskStudentId: taskStudentId,
    teamId: teamId,
  });
  return res as DataResponse;
}
export async function addNewTeam(
  taskStudentId: number | null,
  currentTaskId: number | null
): Promise<DataResponse> {
  const res = await generalRequest("/api/team/addNewTeam", {
    taskStudentId: taskStudentId,
    currentTaskId: currentTaskId,
  });
  return res as DataResponse;
}
export async function newTeamInfo(
  taskStudentId: number,
  currentTaskId: number | null
): Promise<DataResponse> {
  const res = await generalRequest("/api/team/newTeamInfo", {
    taskStudentId: taskStudentId,
    currentTaskId: currentTaskId,
  });
  return res as DataResponse;
}

export async function uploadTeamFile(
  taskStudentId: number | null,
  remoteFile: string,
  file: any
): Promise<any> {
  const formData = new FormData();
  formData.append("file", file);
  const res = await uploadRequest(
    "/api/team/uploadTeamFileWeb?remoteFile=" +
      remoteFile +
      "&taskStudentId=" +
      taskStudentId,
    formData
  );
  return res as DataResponse;
}

export async function getTeamManageData(
  currentTaskId: number | null
): Promise<DataResponse> {
  const res = await generalRequest("/api/team/getTeamManageData", {
    currentTaskId: currentTaskId,
  });
  return res as DataResponse;
}
export async function getTeamDataList(
  currentTaskId: number | null
): Promise<TermItem[]> {
  const res = await generalRequest("/api/team/getTeamDataList", {
    currentTaskId: currentTaskId,
  });
  return res.data as TermItem[];
}

export async function saveTeamScore(
  type: number,
  dataList: TermItem[]
): Promise<DataResponse> {
  const res = await generalRequest("/api/team/saveTeamScore", {
    type: type,
    dataList: dataList,
  });
  return res as DataResponse;
}
