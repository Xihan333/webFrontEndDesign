# README

## 学生管理系统 ——WEB课程设计

### 邮箱注册及找回密码
验证码存储在本地运行的redis中，若要使用该功能，需在本地运行redis，请设置端口号及密码如下：
```
port: 6379
password: 123456
```
*redis使用教程：*
1. 安装并运行redis （可以在本地直接安装，也可以使用docker运行redis）
2. 修改密码：
```shell
CONFIG SET requirepass "your_new_password"
```
若使用docker：
```shell
docker exec -it redis redis-cli
CONFIG SET requirepass "your_new_password"
```
3. 运行该项目即可

若需查看redis存储内容，可使用如redis desktop等可视化工具



