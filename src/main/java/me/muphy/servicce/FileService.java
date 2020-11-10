package me.muphy.servicce;

import me.muphy.config.ApplicationConfig;
import me.muphy.entity.FileInfoEntity;
import me.muphy.util.BeautifulStringUtils;
import me.muphy.util.ZipUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FileService {

    @Autowired
    private ApplicationConfig config;
    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    public List<FileInfoEntity> listFiles(String path) throws IOException {
        File file = getFile(path);
        if (file == null) {
            return null;
        }
        List<FileInfoEntity> fileInfoEntities = new ArrayList<>();
        File[] files = file.isFile() ? new File[]{file} : file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File tmpFile = files[i];
            String fileName = tmpFile.getName();
            if (files[i].isDirectory()) {
                while (tmpFile.listFiles().length == 1 && tmpFile.canRead()) {
                    tmpFile = tmpFile.listFiles()[0];
                    fileName += "/" + tmpFile.getName();
                    if (tmpFile.isFile()) {
                        break;
                    }
                }
            }
            if (tmpFile.canRead()) {
                fileInfoEntities.add(getFileInfoEntity(tmpFile, fileName));
            }
        }
        Collections.sort(fileInfoEntities);
        return fileInfoEntities;
    }

    private FileInfoEntity getFileInfoEntity(File file, String fileName) throws IOException {
        FileInfoEntity entity = new FileInfoEntity();
        String queryParameter = getQueryParameter(file);
        entity.setLastModified(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(file.lastModified()));
        if (file.isFile()) {
            entity.setType("文件");
            entity.setKsize((file.length() / 1024) + "KB");
            entity.setMsize((file.length() / 1024 / 1024) + "MB");
            entity.setFileName("<a href='#' style='margin-left: 10px;' onclick=\"showMessage('/file/vf?f=" + queryParameter + "')\">" + fileName + "</a>");
            entity.setOperate("<a href='/file/dl?f=" + queryParameter + "'>下载</a>"
                    + "<a style='margin-left: 10px;' onclick=\"showMessage('/file/unzip?f=" + queryParameter + "', true)\" href='#'>解压</a>"
                    + "<a style='margin-left: 10px;' onclick=\"showMessage('/file/lf?d=" + queryParameter + "', true)\" href='#' >删除</a>"
                    + "<a href='#' style='margin-left: 10px;' onclick=\"showMessage('/file/vf?f=" + queryParameter + "')\">查看</a>");
        } else {
            entity.setType("目录");
            entity.setFileName("<a onclick=\"listFiles('/file/lf?d=" + queryParameter + "')\" href='#'>" + fileName + "</a>");
            entity.setOperate("<a onclick=\"listFiles('/file/lf?d=" + queryParameter + "')\" href='#'>查看</a>"
                    + "<a style='margin-left: 10px;' onclick=\"showMessage('/file/zip?f=" + queryParameter + "', true)\" href='#'>压缩</a>");
        }

        return entity;
    }

    public String delFile(String pwd, String filename) throws IOException {
        if (Strings.isEmpty(filename)) {
            return BeautifulStringUtils.message("文件不能为空！", BeautifulStringUtils.ERROR);
        }
        File baseFile = new File(config.getBasePath());
        String path = baseFile.getAbsolutePath() + filename;
        File file = new File(path.replaceAll("/+", "/").replaceAll("\\+", "/"));
        if (!file.isFile() || !file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            return BeautifulStringUtils.message("文件不存在！", BeautifulStringUtils.ERROR);
        }
        if (!file.delete()) {
            return BeautifulStringUtils.message("删除失败！", BeautifulStringUtils.ERROR);
        }
        logger.info("文件删除：" + file.getName());
        return BeautifulStringUtils.message("删除成功！");
    }

    public String ViewFile(String filename) throws IOException {
        // 下载本地文件
        File file = getFile(filename);
        if (file == null || !file.isFile()) {
            return BeautifulStringUtils.message("文件不存在！", BeautifulStringUtils.ERROR);
        }
        String contentType = Files.probeContentType(file.toPath());
        String fileType = contentType == null ? null : contentType.replaceAll("\\/+.*", "");
        logger.info("查看文件：" + file.getName() + "-->" + file.length() + "-->" + contentType);
        if ("video".equals(fileType)) {
            return "<div><video src='/file/vs?f=" + filename + "' controls='controls' width='800px' autoplay=''>当前浏览器不支持video标签</video></div>";
        }
        if ("image".equals(fileType)) {
            return "<div><img src='/file/vs?f=" + filename + "' width='800px' alt='预览失败' /></div>";
        }
        if ("audio".equals(fileType)) {
            return "<div><audio src='/file/vs?f=" + filename + "' controls='controls' width='800px' autoplay=''>当前浏览器不支持audio标签</audio></div>";
        }
        if (file.length() > 1024 * 1024 * 4) {
            return BeautifulStringUtils.message("文件太大！", BeautifulStringUtils.ERROR);
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
        return sb.toString();
    }

    public String setBasePath(String pwd, String path) {
        if (Strings.isEmpty(path)) {
            return BeautifulStringUtils.message("目录不能为空！", BeautifulStringUtils.ERROR);
        }
        File file = new File(path);
        if (!file.isDirectory()) {
            return BeautifulStringUtils.message("目录不存在！", BeautifulStringUtils.ERROR);
        }
        config.setBasePath(path);
        return BeautifulStringUtils.message("设置下载路劲成功！");
    }

    public File getFile(String filename, boolean checkExists) throws IOException {
        File baseFile = getBaseFile();
        String path = baseFile.getAbsolutePath();
        if (filename != null && !filename.isEmpty()) {
            path += filename;
        }
        File file = new File(path.replaceAll("/+", "/").replaceAll("\\+", "/"));
        if (checkExists && !file.exists()) {
            return null;
        }
        if (!file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
            return null;
        }
        return file;
    }

    public File getFile(String filename) throws IOException {
        return getFile(filename, true);
    }

    private File getBaseFile() throws FileNotFoundException {
        File baseFile = new File(config.getBasePath());
        if (!baseFile.isDirectory()) {
            throw new FileNotFoundException("根目录不存在！");
        }
        return baseFile;
    }

    private String getCurrentPath(File file) throws IOException {
        if (file == null) {
            return "";
        }
        File tmpFile = file;
        String path = "";
        File baseFile = getBaseFile();
        while (!tmpFile.equals(baseFile)) {
            path = "/<a href='/ll?d=" + getQueryParameter(tmpFile) + "' >" + tmpFile.getName() + "</a>" + path;
            tmpFile = tmpFile.getParentFile();
        }
        return path;
    }

    private String getQueryParameter(File file) throws IOException {
        File baseFile = getBaseFile();
        return file.getCanonicalPath()
                .replaceAll("\\\\", "/")
                .replaceAll(baseFile.getAbsolutePath().replaceAll("\\\\", "/"), "");
    }

    public void exportFile(String filename) throws IOException {
        File file = getFile(filename);
        exportFile(file);
    }

    public void exportFile(File file) throws IOException {
        if (file == null || !file.isFile()) {
            throw new FileNotFoundException(file.getName() + "文件不存在！");
        }
        String fileName = file.getName();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName.replace(" ", "_"));
        InputStream in = new FileInputStream(file);
        IOUtils.copy(in, response.getOutputStream());
        response.flushBuffer();
        in.close();
        logger.info("下载文件:" + fileName);
    }

    public String multiFileUpload(List<MultipartFile> files) throws IOException {
        return multiFileUpload(files, "/upload/");
    }

    public String multiFileUpload(List<MultipartFile> files, String path) throws IOException {
        if (files.isEmpty()) {
            return BeautifulStringUtils.message("没有选择文件，上传失败！", BeautifulStringUtils.BACK);
        }
        if (StringUtils.isEmpty(path)) {
            path = config.getUploadPath();
        }
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            int size = (int) file.getSize();
            logger.info("文件上传：" + fileName + "-->" + size);
            if (size > 10 * 1024 * 1024) {
                return BeautifulStringUtils.message("上传失败，文件太大！", BeautifulStringUtils.BACK);
            }
            if (file.isEmpty()) {
                return BeautifulStringUtils.message("没有选择文件，上传失败！", BeautifulStringUtils.BACK);
            } else {
                File dest = getFile(path + fileName, false);// new File(uploadPath + "/upload/" + fileName);
                if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
                    dest.getParentFile().mkdir();
                }
                file.transferTo(dest);
            }
        }
        return BeautifulStringUtils.message("上传成功！", BeautifulStringUtils.BACK);
    }

    public String zip(String filename) throws IOException {
        File file = getFile(filename);
        if (file == null) {
            return BeautifulStringUtils.message("当前目录不存在！", BeautifulStringUtils.ERROR);
        }
        if (ZipUtils.zip(file.getAbsolutePath())) {
            return BeautifulStringUtils.message("压缩成功！");
        }
        return BeautifulStringUtils.message("压缩失败！", BeautifulStringUtils.ERROR);
    }

    public String unZip(String f) throws IOException {
        File file = getFile(f);
        if (file == null) {
            return BeautifulStringUtils.message("当前目录不存在！", BeautifulStringUtils.ERROR);
        }
        if (ZipUtils.unZip(file.getAbsolutePath())) {
            return BeautifulStringUtils.message("解压成功！");
        }
        return BeautifulStringUtils.message("解压失败！", BeautifulStringUtils.ERROR);
    }

}
