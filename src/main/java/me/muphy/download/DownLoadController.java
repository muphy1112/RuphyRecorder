package me.muphy.download;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DownLoadController {
    @Value("${download.file.default:}")
    private String downloadDefaultFile;
    @Value("${download.path:E:/workspace/download/}")
    private String downloadPath;

    private static String noPath = "<div><span>目录不存在！</span></div><div><span style=\"margin-left: 20px;\"><a href=\"/\" >返回首页</a></span></div>";
    private static String noFile = "<div><span>文件不存在！</span></div><div><span style=\"margin-left: 20px;\"><a href=\"/\" >返回首页</a></span></div>";

    @GetMapping("/dl")
    public void download(String f, HttpServletResponse response) throws IOException {
        // 下载本地文件
        File baseFile = new File(downloadPath);
        String fileName = downloadDefaultFile; // 文件的默认保存名
        if (f != null && !"".equals(f.trim())) {
            fileName = f;
        }
        File file = new File(downloadPath + fileName);
        if (!file.isFile() || !file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            error(response);
            return;
        }
        // 读到流中
        InputStream inStream = new FileInputStream(file);// 文件的存放路径
        // 设置输出的格式
        response.reset();
        response.setContentType("bin");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        // 循环取出流中的数据
        byte[] b = new byte[100];
        int len;
        while ((len = inStream.read(b)) > 0)
            response.getOutputStream().write(b, 0, len);
        inStream.close();
    }

    private void error(HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "utf-8"));
        writer.print(noFile);
        writer.close();
        return;
    }

    @GetMapping("/ll")
    public String ListFiles(String d, HttpServletRequest request) throws IOException {
        File baseFile = new File(downloadPath);
        if (!baseFile.isDirectory()) {
            System.out.println(downloadPath + " 目录不存在，只能下载此目录下面的文件，请确保配置路径（download.path={path}）存在");
            return noPath;
        }
        List<String> fns = new ArrayList<>();
        String path = baseFile.getAbsolutePath();
        if (d != null && !d.isEmpty()) {
            path += d;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        File file = new File(path.replaceAll("/+", "/"));
        if (file.isDirectory()) {
            if (!file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
                return noPath;
            }
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    fns.add("<div><span>目录：<a href=\"/ll?d=" + getQueryParameter(files[i], baseFile) + "\" >" + files[i].getName() + "</a></span></div>");
                    continue;
                }
                if(files[i].canRead()){
                    fns.add("<div><span styl>文件：<a href=\"/dl?f=" + getQueryParameter(files[i], baseFile) + "\" >"
                            + files[i].getName() + "</a></span><span style=\"margin-left:20px;\">"
                            + files[i].length() / 1024 + "KB</span><span style=\"margin-left:20px;\">"
                            + files[i].length() / 1024 / 1024 + "MB</span><span style=\"margin-left:20px;\">"
                            + simpleDateFormat.format(files[i].lastModified()) + "</span></div>");
                }
            }
        } else {
            return noPath;
        }
        fns.sort((x, y) -> y.compareTo(x));
        if (!file.getCanonicalPath().equals(baseFile.getAbsolutePath())) {
            fns.add(0, "<span>目录：<a href=\"/ll?d=" + getQueryParameter(file, baseFile) + "/..\" >..</a></span>");
        }
        fns.add("<div><span><a href=\"/\" >返回首页</a></span></div>");
        return String.join("", fns);
    }

    private String getQueryParameter(File file, File baseFile) throws IOException {
        return file.getCanonicalPath()
                .replaceAll("\\\\", "/")
                .replaceAll(baseFile.getAbsolutePath().replaceAll("\\\\", "/"), "");
    }

}