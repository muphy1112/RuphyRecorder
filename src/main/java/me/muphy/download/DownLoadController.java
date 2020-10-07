package me.muphy.download;

import org.apache.logging.log4j.util.Strings;
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

    private static String downloadPath;

    public String getDownloadPath() {
        return downloadPath;
    }

    @Value("${download.path:E:/workspace/download/}")
    public void setDownloadPath(String downloadPath) {
        DownLoadController.downloadPath = downloadPath;
    }

    private static String noPath = "<div><span>目录不存在！</span></div><div><span style=\"margin-left: 20px;\"><a href=\"/\" >返回首页</a></span></div>";
    private static String noFile = "<div><span>文件不存在！</span></div><div><span style=\"margin-left: 20px;\"><a href=\"/\" >返回首页</a></span></div>";

    @GetMapping("/dl")
    public void download(String f, HttpServletResponse response) throws IOException {
        // 下载本地文件
        File baseFile = new File(downloadPath);
        File file = new File(downloadPath + f);
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

    @GetMapping("/sd")
    public String setDownloadPath(String p, String d) {
        if (Strings.isEmpty(d)) {
            return "<script>alert('目录不能为空！');window.open(\"\\/\");</script>";
        }
        if (!"123...".equals(p)) {
            return "<script>alert('你没有设置目录权限！');window.open(\"\\/\");</script>";
        }
        File file = new File(d);
        if (!file.isDirectory()) {
            return "<script>alert('目录不存在！');window.open(\"\\/\");</script>";
        }
        downloadPath = d;
        return "<script>alert('设置下载路劲成功！');window.open(\"\\/\");</script>";
    }

    @GetMapping("/ll")
    public String ListFiles(String d, HttpServletRequest request) throws IOException {
        File baseFile = new File(downloadPath);
        if (!baseFile.isDirectory()) {
            System.out.println(downloadPath + " 目录不存在，只能下载此目录下面的文件，请确保配置路径（download.path={path}）存在");
            return noPath;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head><style>tr th,td { padding-right: 10; }</style><title>若非文件浏览器</title></head>");
        sb.append("<body>");
        sb.append("<div><table style=\"text-align: left;\">");
        sb.append("<thead><tr><th>类型</th><th>文件名</th><th>文件大小KB</th><th>文件大小MB</th><th>创建时间</th><th>操作</th></tr></thead>");
        sb.append("<tbody>");
        String path = baseFile.getAbsolutePath();
        if (d != null && !d.isEmpty()) {
            path += d;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        File file = new File(path.replaceAll("/+", "/"));
        List<String> fns = new ArrayList<>();
        if (file.isDirectory()) {
            if (!file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
                return noPath;
            }
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    fns.add("<tr><td>目录：</td><td colspan=\"4\"><a href=\"/ll?d=" + getQueryParameter(files[i], baseFile) + "\" >" + files[i].getName() + "</a></td><td><a href=\"/ll?d=" + getQueryParameter(files[i], baseFile) + "\" >查看</a></td></tr>");
                    continue;
                }
                if (files[i].canRead()) {
                    fns.add("<tr><td>文件：</td><td><a href=\"/dl?f=" + getQueryParameter(files[i], baseFile) + "\" >"
                            + files[i].getName() + "</a></td><td>"
                            + files[i].length() / 1024 + "KB</td><td>"
                            + files[i].length() / 1024 / 1024 + "MB</td><td>"
                            + simpleDateFormat.format(files[i].lastModified()) + "</td><td>"
                            + "<a href=\"/dl?f=" + getQueryParameter(files[i], baseFile) + "\" >下载</a>"
                            + "<a href=\"#\" style=\"margin-left: 10px;\" onclick=\"document.getElementById('view-file').setAttribute('src', '/vf?f=" + getQueryParameter(files[i], baseFile) + "')\">查看</a></td></tr>");
                }
            }
        } else {
            return noPath;
        }
        fns.sort((x, y) -> y.compareTo(x));
        if (!file.getCanonicalPath().equals(baseFile.getAbsolutePath())) {
            fns.add(0, "<tr><td>目录：</td><td colspan=\"4\"><a href=\"/ll?d=" + getQueryParameter(file, baseFile) + "/..\" >..</a></td><td><a href=\"/ll?d=" + getQueryParameter(file, baseFile) + "/..\" >返回上级目录</a></td></tr>");
        }
        sb.append(String.join("", fns));
        sb.append("</tbody>");
        sb.append("</table></div>");
        sb.append("<div style=\"border-bottom: 1px solid #ccc; margin-bottom: 20px; padding: 10px 0;\"><span><a href=\"/\" >返回首页</a></span></div>");
        sb.append("<div><iframe id=\"view-file\" src=\"http://www.baidu.com\" frameborder=\"0\" style=\"width: 100%; height: 100%\"></div></body></iframe></div>");
        sb.append("</body");
        sb.append("</html");
        return sb.toString();
    }

    @GetMapping("/vf")
    public String ViewFile(String f) throws IOException {
        // 下载本地文件
        File baseFile = new File(downloadPath);
        File file = new File(downloadPath + f);
        if (!file.isFile() || !file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            return "文件不存在！";
        }
        if (file.length() > 1024 * 1024 * 25) {
            return "文件太大！";
        }
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null){
            sb.append(line);
            sb.append("<br>");
        }
        br.close();
        reader.close();
        return sb.toString();
    }

    private String getQueryParameter(File file, File baseFile) throws IOException {
        return file.getCanonicalPath()
                .replaceAll("\\\\", "/")
                .replaceAll(baseFile.getAbsolutePath().replaceAll("\\\\", "/"), "");
    }

}