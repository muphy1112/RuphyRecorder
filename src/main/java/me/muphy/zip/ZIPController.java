package me.muphy.zip;

import me.muphy.download.DownLoadController;
import me.muphy.servicce.ZipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RestController
public class ZIPController {

    @Autowired
    private ZipService zipService;

    @Value("${download.path:E:/workspace/download/}")
    private String downloadPath;
    @Value("${download.passwd:123...}")
    private String passwd;

    @RequestMapping("/zip")
    public String zip(String d, String p) throws IOException {
        if(!passwd.equals(p)){
            return "没有权限压缩！";
        }
        File baseFile = new File(downloadPath);
        if (!baseFile.isDirectory()) {
            return "当前目录不存在！";
        }
        String path = baseFile.getAbsolutePath();
        if (d != null && !d.isEmpty()) {
            path += d;
        }
        File file = new File(path.replaceAll("/+", "/").replaceAll("\\+", "/"));
        if (!file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            return "当前目录不存在！";
        }
        if (!file.isDirectory()){
            return "当前路径不是目录！";
        }
        if(zipService.zip(file.getAbsolutePath())){
            return "压缩成功！";
        }
        return "压缩失败！";
    }

    @RequestMapping("unzip")
    public String unZip(String f, String p) throws IOException {
        if(!passwd.equals(p)){
            return "没有权限解压！";
        }
        File baseFile = new File(downloadPath);
        if (!baseFile.isDirectory()) {
            return "当前目录不存在！";
        }
        String path = baseFile.getAbsolutePath() + f;
        File file = new File(path.replaceAll("/+", "/").replaceAll("\\+", "/"));
        if (!file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            return "当前目录不存在！";
        }
        if(zipService.unZip(file.getAbsolutePath())){
            return "解压成功！";
        }
        return "解压失败！";
    }

}
