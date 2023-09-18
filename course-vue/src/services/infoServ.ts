import { generalRequest, uploadRequest } from "~/services/genServ";
import { type DataResponse } from "~/models/general";
//修改口令后台数据请求方法
export async function updatePassword(data: Object): Promise<DataResponse> {
  const res = await generalRequest("/api/base/updatePassword", data);
  return res as DataResponse;
}
//获取学生个人简介信息后台数据请求方法
export async function getStudentIntroduceData(
  studentId: number | null
): Promise<DataResponse> {
  const res = await generalRequest("/api/student/getStudentIntroduceData", {
    studentId: studentId,
  });
  return res as DataResponse;
}
//获取学生照片数据后台数据请求方法
export async function getPhotoImageStr(
  fileName: String
): Promise<DataResponse> {
  const res = await generalRequest("/api/base/getPhotoImageStr", {
    fileName: fileName,
  });
  return res as DataResponse;
}
//上传学生照片数据后台数据请求方法
export async function uploadPhoto(remoteFile: string, file: any): Promise<any> {
  console.log(remoteFile);
  console.log(file);
  const formData = new FormData();
  formData.append("file", file);
  const res = await uploadRequest(
    "/api/base/uploadPhotoWeb?remoteFile=" + remoteFile,
    formData
  );
  return res as DataResponse;
}
