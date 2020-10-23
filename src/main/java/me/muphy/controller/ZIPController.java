package me.muphy.controller;

import me.muphy.servicce.ZipService;
import me.muphy.util.BeautifulStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@RestController
@RequestMapping("/")
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
            return BeautifulStringUtil.message("没有权限压缩！", BeautifulStringUtil.ERROR);
        }
        File baseFile = new File(downloadPath);
        if (!baseFile.isDirectory()) {
            return BeautifulStringUtil.message("当前目录不存在！", BeautifulStringUtil.ERROR);
        }
        String path = baseFile.getAbsolutePath();
        if (d != null && !d.isEmpty()) {
            path += d;
        }
        File file = new File(path.replaceAll("/+", "/").replaceAll("\\+", "/"));
        if (!file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            return BeautifulStringUtil.message("当前目录不存在！", BeautifulStringUtil.ERROR);
        }
        if (!file.isDirectory()){
            return BeautifulStringUtil.message("当前路径不是目录！", BeautifulStringUtil.ERROR);
        }
        if(zipService.zip(file.getAbsolutePath())){
            return BeautifulStringUtil.message("压缩成功！");
        }
        return BeautifulStringUtil.message("压缩失败！", BeautifulStringUtil.ERROR);
    }

    @RequestMapping("/unzip")
    public String unZip(String f, String p) throws IOException {
        if(!passwd.equals(p)){
            return BeautifulStringUtil.message("没有权限解压！", BeautifulStringUtil.ERROR);
        }
        File baseFile = new File(downloadPath);
        if (!baseFile.isDirectory()) {
            return BeautifulStringUtil.message("当前目录不存在！", BeautifulStringUtil.ERROR);
        }
        String path = baseFile.getAbsolutePath() + f;
        File file = new File(path.replaceAll("/+", "/").replaceAll("\\+", "/"));
        if (!file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            return BeautifulStringUtil.message("当前目录不存在！", BeautifulStringUtil.ERROR);
        }
        if(zipService.unZip(file.getAbsolutePath())){
            return BeautifulStringUtil.message("解压成功！");
        }
        return BeautifulStringUtil.message("解压失败！", BeautifulStringUtil.ERROR);
    }

}
