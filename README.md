# RuphyRecorder 

# 配置文件src/main/resources/application.properties
```properties
server.port=8080

#文件 相关
app.base-path=E:/workspace/share/
app.upload.path=/upload/
app.camera.path=/picture/
app.record.path=/record/
app.record.time.default=3600
app.payload.path=/payload/

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=100MB

#datasource 相关
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://47.***.***.21/hacker
spring.datasource.password=***
spring.datasource.username=azi

#日志 相关
logging.level.me.muphy=debug

#security 相关
#spring.security.user.password=...
#spring.security.user.name=admin
#spring.security.user.roles=admin
```

#登录账号目前在WebSecurityConfig类中查看

#功能列表
本项目所以功能都通过http操作，使用方便，减少不必要的担心和麻烦，比如文件传输

## 1.录音机
文件保存路径${app.base-path}/record
使用基于Rest API的页面操作，方便远程控制录音，支持设置录音时长，默认1个小时

## 2.照相机
文件保存路径${app.base-path}/picture
使用基于Rest API的页面操作，方便远程控制拍照

## 3.文件操作
操作路径：${app.base-path}
### 文件下载
当路径是一个文件的时候点击下载或者文件名可以下载，目录可以点击压缩后下载
### 文件压缩
目录或者文件都可以被压缩成zip文件
### 文件解压
压缩文件可在线解压，解压需要提供密码认证
### 文件删除
只能删除文件，不能删除目录
### 文件浏览
目前支持文本文件，图片，音频和视频
### 文件上传
上传固定位置：${app.base-path}/upload/


# 部署方式
## 拉去源代码
```text
git clone git@github.com:muphy1112/RuphyRecorder.git
cd RuphyRecorder
```
## 运行方式1
```text
mvn package
cd target
java -jar   recorder-0.0.1-SNAPSHOT.jar
```
## 运行方式2
```text
mvn spring-boot:run
```
浏览器访问服务器路径即可，如：http://localhost:8080
