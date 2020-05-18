package me.muphy.download;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DownLoadController {
    @Value("${download.file.default:}")
    private String downloadDefaultFile;
    @Value("${download.path:E:/workspace/download/}")
    private String downloadPath;
    @Value("${download.url:http://(ip):(port)}")
    private String downloadUrl;

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
        writer.print("文件不存在!");
        writer.close();
        return;
    }

    @GetMapping("/ll")
    public String ListFiles(String d, HttpServletRequest request) throws IOException {
        File baseFile = new File(downloadPath);
        if (!baseFile.isDirectory()) {
            System.out.println(downloadPath + " 目录不存在，只能下载此目录下面的文件，请确保配置路径（download.path={path}）存在");
            return "目录不存在!";
        }
        String ip = request.getServerName();
        int port = request.getServerPort();
        List<String> fns = new ArrayList<>();
        String path = baseFile.getAbsolutePath();
        if (d != null && !d.isEmpty()) {
            path += d;
        }

        File file = new File(path.replaceAll("/+", "/"));
        if (file.isDirectory()) {
            if (!file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
                return "目录不存在!";
            }
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    fns.add("<span>目录：<a href=\"" + downloadUrl.replace("(ip)", ip).replace("(port)", port + "") + "/ll?d=" + getQueryParameter(files[i], baseFile) + "\" >" + files[i].getName() + "</a></span>");
                    continue;
                }
                fns.add("<span>文件：<a href=\"" + downloadUrl.replace("(ip)", ip).replace("(port)", port + "") + "/dl?f=" + getQueryParameter(files[i], baseFile) + "\" >" + files[i].getName() + "</a></span>");
            }
        } else {
            return "目录不存在!";
        }
        fns.sort((x, y) -> y.compareTo(x));
        if (!file.getCanonicalPath().equals(baseFile.getAbsolutePath())) {
            fns.add(0, "<span>目录：<a href=\"" + downloadUrl.replace("(ip)", ip).replace("(port)", port + "") + "/ll?d=" + getQueryParameter(file, baseFile) + "/..\" >..</a></span>");
        }
        return String.join("<br>", fns);
    }

    private String getQueryParameter(File file, File baseFile) throws IOException {
        return file.getCanonicalPath()
                .replaceAll("\\\\", "/")
                .replaceAll(baseFile.getAbsolutePath().replaceAll("\\\\", "/"), "");
    }

}