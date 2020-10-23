package me.muphy.controller;

import me.muphy.entity.ClientRequestEntity;
import me.muphy.mapper.RemoteHostMapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/p")
public class RemoteHostController {

    @Value("${payload.path:/download/payload}")
    public String downloadPath;
    @Autowired
    private RemoteHostMapper remoteHostMapper;

    @RequestMapping("/s")
    public String save(ClientRequestEntity requestInfo, HttpServletRequest request) {
        int count = remoteHostMapper.SaveRemoteFilterInfo(requestInfo);
        if (count > 0) {
            return "保存成功！";
        }
        return "保存失败！";
    }

    @RequestMapping("/qr")
    public List<ClientRequestEntity> queryRemoteFilterInfo(Integer page, Integer size, String ip) {
        return remoteHostMapper.queryRemoteFilterInfo(page,size, ip);
    }

    @RequestMapping("/ql")
    public List<ClientRequestEntity> queryLocalFilterInfo(String ip) {
        return remoteHostMapper.queryLocalFilterInfo(0,0, ip);
    }

    @RequestMapping("/dl")
    public void download(String f, HttpServletResponse response) {
        try {
            File baseFile = new File(downloadPath);
            File file = new File((downloadPath + "/" + f).replaceAll("\\\\+", "/"));
            if (!baseFile.exists() || !file.isFile() || !file.getCanonicalPath().startsWith(baseFile.getCanonicalPath())) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName().replace(" ", "_"));
            InputStream in = new FileInputStream(file);
            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
            in.close();
        } catch (java.nio.file.NoSuchFileException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
