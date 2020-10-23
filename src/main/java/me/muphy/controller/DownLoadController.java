package me.muphy.controller;

import me.muphy.util.BeautifulStringUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
public class DownLoadController {

    private static String downloadPath;
    @Value("${download.passwd:123...}")
    private String passwd;

    public String getDownloadPath() {
        return downloadPath;
    }

    @Value("${download.path:E:/workspace/download/}")
    public void setDownloadPath(String downloadPath) {
        DownLoadController.downloadPath = downloadPath;
    }

    @GetMapping("/dl")
    public void download(String f, HttpServletResponse response) throws IOException {
        // 下载本地文件
        File baseFile = new File(downloadPath);
        File file = new File(downloadPath + f);
        response.reset();
        if (!file.isFile() || !file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(BeautifulStringUtil.message("文件不存在！"));
            writer.close();
            return;
        }
        System.out.println("文件下载：" + file.getName() + "-->" + file.length());
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

    //@GetMapping("/sd")
    public String setDownloadPath(String p, String d) {
        if (Strings.isEmpty(d)) {
            return BeautifulStringUtil.message("目录不能为空！", BeautifulStringUtil.ERROR);
        }
        if (!passwd.equals(p)) {
            return BeautifulStringUtil.message("你没有设置目录权限！", BeautifulStringUtil.ERROR);
        }
        File file = new File(d);
        if (!file.isDirectory()) {
            return BeautifulStringUtil.message("目录不存在！", BeautifulStringUtil.ERROR);
        }
        downloadPath = d;
        return BeautifulStringUtil.message("设置下载路劲成功！");
    }

    @GetMapping("/ll")
    public String ListFiles(String d) throws IOException {
        File file = GetFile(d);
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head><style>tr th,td { padding-right: 10; }</style><title>若非文件浏览器</title></head>");
        sb.append("<body>");
        sb.append("<div style=\"max-height: 450px; overflow: auto;\"><table style=\"text-align: left;\">");
        sb.append("<thead><tr><th>类型</th><th>文件名</th><th>文件大小KB</th><th>文件大小MB</th><th>创建时间</th><th>操作</th></tr></thead>");
        sb.append("<tbody>");
        if (file == null) {
            sb.append("<tr><td colspan=\"6\"><h1>当前目录不存在！<h1></td></tr>");
        } else {
            sb.append(getTableBody(file));
        }
        sb.append("</tbody>");
        sb.append("</table></div>");
        sb.append("<div style=\"border-bottom: 1px solid #ccc; margin-bottom: 20px; padding: 10px 0;\">" +
                "<span style=\"margin-right: 20px;\">当前目录：" + getCurrentPath(file) + "</span>" +
                "<span><a href=\"/\" >返回首页</a></span></div>");
        sb.append("<iframe onload='this.height=(document.body.clientHeight-570)' id=\"view-file\" src=\"/ruphy.html\" frameborder=\"0\" style=\"width: 100%;min-height: 400px\"></iframe>");
        sb.append("<div style=\"text-align: center; color: blue\"><span>@copyright by ruphy.<span></div>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    private String getTableBody(File file) throws IOException {
        File baseFile = new File(downloadPath);
        File[] files = file.listFiles();
        List<String> fns = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                File tmpFile = files[i];
                String fileName = tmpFile.getName();
                while (tmpFile.listFiles().length == 1) {
                    tmpFile = tmpFile.listFiles()[0];
                    fileName += "/" + tmpFile.getName();
                    if (tmpFile.isFile()) {
                        fns.add(getFileRow(tmpFile, baseFile, fileName));
                        break;
                    }
                }
                if (tmpFile.isDirectory()) {
                    String queryParameter = getQueryParameter(tmpFile, baseFile);
                    fns.add("<tr><td>目录：</td><td colspan=\"4\"><a href=\"/ll?d="
                            + getQueryParameter(tmpFile, baseFile) + "\" >" + fileName + "</a></td><td>" +
                            "<a href=\"/ll?d=" + getQueryParameter(tmpFile, baseFile) + "\" >查看</a>"
                            + "<a style=\"margin-left: 10px;\" onclick=\"var p = prompt('输入认证码：'); if(!p) return false; document.getElementById('view-file').setAttribute('src', '/zip?d=" + queryParameter + "&p=' + p);\" href=\"#\">压缩</a>"
                            + "</td></tr>");
                }
                continue;
            }
            if (files[i].canRead()) {
                fns.add(getFileRow(files[i], baseFile, files[i].getName()));
            }
        }
        Collections.sort(fns, new Comparator<String>() {
            @Override
            public int compare(String x, String y) {
                return y.compareTo(x);
            }
        });
        if (!file.getCanonicalPath().equals(baseFile.getAbsolutePath())) {
            fns.add(0, "<tr><td>目录：</td><td colspan=\"4\"><a href=\"/ll?d="
                    + getQueryParameter(file, baseFile).replaceAll("/+[^/]+/?$", "/")
                    + "\" >..</a></td><td><a href=\"/ll?d="
                    + getQueryParameter(file, baseFile).replaceAll("/+[^/]+/?$", "/")
                    + "\" >返回上级目录</a></td></tr>");
        }
        return StringUtils.join(fns, ' ');
    }

    private String getFileRow(File file, File baseFile, String fileName) throws IOException {
        String queryParameter = getQueryParameter(file, baseFile);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return "<tr><td>文件：</td><td><a href=\"/dl?f=" + queryParameter + "\" >"
                + fileName + "</a></td><td>"
                + file.length() / 1024 + "KB</td><td>"
                + file.length() / 1024 / 1024 + "MB</td><td>"
                + format.format(file.lastModified()) + "</td><td>"
                + "<a href=\"/dl?f=" + queryParameter + "\" >下载</a>"
                + "<a style=\"margin-left: 10px;\" onclick=\"var p = prompt('输入认证码：'); if(!p) return false; document.getElementById('view-file').setAttribute('src', '/unzip?f=" + queryParameter + "&p=' + p);\" href=\"#\">解压</a>"
                + "<a style=\"margin-left: 10px;\" onclick=\"var p = prompt('输入认证码：'); if(!p) return false; document.getElementById('view-file').setAttribute('src', '/df?f=" + queryParameter + "&p=' + p);\" href=\"#\" >删除</a>"
                + "<a href=\"#\" style=\"margin-left: 10px;\" onclick=\"document.getElementById('view-file').setAttribute('src', '/vf?f=" + queryParameter + "')\">查看</a></td></tr>";
    }

    private File GetFile(String d) throws IOException {
        File baseFile = new File(downloadPath);
        if (!baseFile.isDirectory()) {
            return null;
        }
        String path = baseFile.getAbsolutePath();
        if (d != null && !d.isEmpty()) {
            path += d;
        }
        File file = new File(path.replaceAll("/+", "/").replaceAll("\\+", "/"));
        if (!file.isDirectory()) {
            return null;
        }
        if (!file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            return null;
        }
        return file;
    }

    private String getCurrentPath(File file) throws IOException {
        if (file == null) {
            return "";
        }
        File tmpFile = file;
        String path = "";
        File baseFile = new File(downloadPath);
        while (!tmpFile.equals(baseFile)) {
            path = "/<a href=\"/ll?d=" + getQueryParameter(tmpFile, baseFile) + "\" >" + tmpFile.getName() + "</a>" + path;
            tmpFile = tmpFile.getParentFile();
        }
        return path;
    }

    @GetMapping("/df")
    public String delFile(String p, String f) throws IOException {
        if (Strings.isEmpty(f)) {
            return BeautifulStringUtil.message("文件不能为空！", BeautifulStringUtil.ERROR);
        }
        if (!passwd.equals(p)) {
            return BeautifulStringUtil.message("你没有删除文件权限！", BeautifulStringUtil.ERROR);
        }
        File baseFile = new File(downloadPath);
        String path = baseFile.getAbsolutePath() + f;
        File file = new File(path.replaceAll("/+", "/").replaceAll("\\+", "/"));
        if (!file.isFile() || !file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            return BeautifulStringUtil.message("文件不存在！", BeautifulStringUtil.ERROR);
        }
        System.out.println("文件删除：" + file.getName());
        if (!file.delete()) {
            return BeautifulStringUtil.message("删除失败！", BeautifulStringUtil.ERROR);
        }
        return BeautifulStringUtil.message("删除成功！");
    }

    @GetMapping("/vf")
    public String ViewFile(String f) throws IOException {
        // 下载本地文件
        File baseFile = new File(downloadPath);
        File file = new File(downloadPath + f);
        if (!file.isFile() || !file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            return BeautifulStringUtil.message("文件不存在！", BeautifulStringUtil.ERROR);
        }
        if (file.length() > 1024 * 1024 * 25) {
            return BeautifulStringUtil.message("文件太大！", BeautifulStringUtil.ERROR);
        }
        String contentType = Files.probeContentType(file.toPath());
        String fileType = contentType == null ? null : contentType.replaceAll("\\/+.*", "");
        System.out.println("查看文件：" + file.getName() + "-->" + file.length() + "-->" + contentType);
        if ("video".equals(fileType)) {
            return "<div>当前视频文件：" + file.getName() + "</div><hr><div><video src=\"/vs?f=" + f + "\" controls=\"controls\" width=\"800px\" autoplay=\"\">当前浏览器不支持video标签</video></div>";
        }
        if ("image".equals(fileType)) {
            return "<div>当前图片文件：" + file.getName() + "</div><hr><div><img src=\"/vs?f=" + f + "\" width=\"800px\" alt=\"预览失败\" /></div>";
        }
        if ("audio".equals(fileType)) {
            return "<div>当前音频文件：" + file.getName() + "</div><hr><div><audio src=\"/vs?f=" + f + "\" controls=\"controls\" width=\"800px\" autoplay=\"\">当前浏览器不支持audio标签</audio></div>";
        }
        FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        if (!"text/html".equals(contentType)) {
            sb.insert(0, "<p><xmp>\n");
            sb.append("</xmp></p>");
        }
        br.close();
        reader.close();
        return "<div>当前文件：" + file.getName() + "</div><hr>" + sb.toString();
    }

    @GetMapping(value = "/vs")
    public void viewStream(String f, HttpServletResponse response) {
        try {
            File baseFile = new File(downloadPath);
            File file = new File(downloadPath + f);
            if (!file.isFile() || !file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName().replace(" ", "_"));
            InputStream in = new FileInputStream(file);
            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
        } catch (java.nio.file.NoSuchFileException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private String getQueryParameter(File file, File baseFile) throws IOException {
        return file.getCanonicalPath()
                .replaceAll("\\\\", "/")
                .replaceAll(baseFile.getAbsolutePath().replaceAll("\\\\", "/"), "");
    }

}