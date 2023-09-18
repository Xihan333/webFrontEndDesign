import { ElMessage, ElMessageBox } from "element-plus";
//消息提示，用户页面显示提示显示
export function message(self: any, msg: string) {
  self.$message({
    message: msg,
    type: "warnning",
  });
}
//确认消息提示，用户页面显示确认消息，用户点击确认后返回true，否则返回false
export function messageConform(content: any) {
  return new Promise((resolve, reject) => {
    ElMessageBox.confirm(content, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    })
      .then(() => {
        resolve(true);
      })
      .catch(() => {
        ElMessage({
          type: "info",
          message: "已取消删除",
        });
        reject(false);
      });
  });
}
