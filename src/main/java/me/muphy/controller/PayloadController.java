package me.muphy.controller;

import me.muphy.entity.ClientRequestEntity;
import me.muphy.mapper.RemoteHostMapper;
import me.muphy.servicce.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/payload")
public class PayloadController {

    @Autowired
    private RemoteHostMapper remoteHostMapper;
    @Autowired
    private FileService fileService;

    @RequestMapping("/saveRequestInfo")
    public String save(ClientRequestEntity requestInfo, HttpServletRequest request) {
        int count = remoteHostMapper.SaveRemoteFilterInfo(requestInfo);
        if (count > 0) {
            return "保存成功！";
        }
        return "保存失败！";
    }

    @RequestMapping("/queryRemoteRequestInfo")
    public List<ClientRequestEntity> queryRemoteFilterInfo(String s) {
        return remoteHostMapper.queryRemoteFilterInfo(0, 0, s);
    }

    @RequestMapping("/queryLocalRequestInfo")
    public List<ClientRequestEntity> queryLocalFilterInfo(String s) {
        return remoteHostMapper.queryLocalFilterInfo(0, 0, s);
    }

    @RequestMapping("/download")
    public void download(String f) throws IOException {
        fileService.exportFile(f);
    }

}
