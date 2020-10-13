# RuphyRecorder 

# 配置文件src/main/resources/application.properties
```properties
server.port=8080

download.passwd=123...

#文件下载
download.path=E:/workspace/share/

#录音
record.time.default=3600

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=100MB
```

#功能列表
本项目所以功能都通过http操作，使用方便，减少不必要的担心和麻烦，比如文件传输

## 1.录音机
文件保存路径${download.path}/record
使用基于Rest API的页面操作，方便远程控制录音，支持设置录音时长，默认1个小时

## 2.照相机
文件保存路径${download.path}/picture
使用基于Rest API的页面操作，方便远程控制拍照

## 3.文件操作
操作路径：${download.path}
### 文件下载
当路径是一个文件的时候点击下载或者文件名可以下载，目录可以点击压缩后下载
### 文件压缩
目录或者文件都可以被压缩成zip文件
### 文件删除
删除文件需要提供密码认证，只能删除文件，不能删除目录
### 文件浏览
目前支持文本文件，图片，音频和视频
### 文件上传
上传固定位置：${download.path}/upload


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