package me.muphy.controller;

import me.muphy.entity.FileInfoEntity;
import me.muphy.servicce.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/dl")
    public void download(String f) throws IOException {
        fileService.exportFile(f);
    }

    //@GetMapping("/sd")
    public String setDownloadPath(String p, String d) {
        return fileService.setBasePath(p, d);
    }

    @GetMapping("/ll")
    public void index(HttpServletResponse response) throws IOException {
        response.sendRedirect("/download/index.html");
    }

    @GetMapping("/lf")
    public List<FileInfoEntity> ListFiles(String d) throws IOException {
        return fileService.listFiles(d);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("/df")
    public String delFile(String p, String f) throws IOException {
        return fileService.delFile(p, f);
    }

    @GetMapping("/vf")
    public String ViewFile(String f) throws IOException {
        // 下载本地文件
        return fileService.ViewFile(f);
    }

    @GetMapping(value = "/vs")
    public void viewStream(String f) throws IOException {
        fileService.exportFile(f);
    }

    /**
     * 实现多文件上传
     */
    @RolesAllowed({"ADMIN"})
    @PostMapping(value = "/ul")
    public String multiFileUpload(HttpServletRequest request) throws IOException {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("fileName");
        return fileService.multiFileUpload(files);
    }

    @RolesAllowed({"ADMIN"})
    @RequestMapping("/zip")
    public String zip(String d) throws IOException {
        return fileService.zip(d);
    }

    @RolesAllowed({"ADMIN"})
    @RequestMapping("/unzip")
    public String unZip(String f) throws IOException {
        return fileService.unZip(f);
    }

}