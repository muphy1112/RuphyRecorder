package me.muphy.upload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class UploadController {
    @Value("${download.path:E:/workspace/download/}")
    private String uploadPath;

    /**
     * 实现多文件上传
     */
    @PostMapping(value = "ul")
    public @ResponseBody
    String multiFileUpload(HttpServletRequest request) throws IOException {

        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("fileName");

        if (files.isEmpty()) {
            return getPageMsg("没有选择文件，上传失败！");
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            int size = (int) file.getSize();
            System.out.println(fileName + "-->" + size);
            if(size > 4*1024*1024){
                return getPageMsg("上传失败，文件太大！");
            }
            if (file.isEmpty()) {
                return getPageMsg("没有选择文件，上传失败！");
            } else {
                File dest = new File(uploadPath + "/upload/" + fileName);
                if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                    dest.getParentFile().mkdir();
                }
                file.transferTo(dest);
            }
        }
        return getPageMsg("上传成功！");
    }

    private String getPageMsg(String msg){
        return "<div><span>" + msg + "</span></div><div>" +
                "<span><a href=\"/ll?d=/upload/\" >查看所有已上传文件</a></span>" +
                "<span style=\"margin-left: 20px;\"><a href=\"/\" >返回首页</a></span></div>";
    }
}
