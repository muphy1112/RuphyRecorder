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
            return "上传失败！";
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            int size = (int) file.getSize();
            System.out.println(fileName + "-->" + size);

            if (file.isEmpty()) {
                return "上传失败！";
            } else {
                File dest = new File(uploadPath + "/upload/" + fileName);
                if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                    dest.getParentFile().mkdir();
                }
                file.transferTo(dest);
            }
        }
        return "上传成功！";
    }

}
