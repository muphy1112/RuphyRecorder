package me.muphy.recording;

import me.muphy.servicce.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.sound.sampled.*;
import java.io.*;

@RestController
public class RecordController {

    @Autowired
    private RecordService recordService;

    @Value("${download.url:http://(ip):(port)}")
    private String downloadUrl;

    @GetMapping("/startRecord")
    public String startRecord(int t, HttpServletRequest request) {
        String msg = recordService.start(t);
        String ip = request.getServerName();
        int port = request.getServerPort();
        return "<span>" + msg + "</span><br><span><a href=\"" + downloadUrl.replace("(ip)", ip).replace("(port)", port + "") + "/stopRecord\" >点击停止录音</a>" +
                "</span><span style=\"margin-left: 20px;\"><a href=\"" + downloadUrl.replace("(ip)", ip).replace("(port)", port + "") + "/ll?d=/record\" >点击查看所有录音文件</a></span>";
    }

    @GetMapping("/stopRecord")
    public String stopRecord(HttpServletRequest request) {
        String msg = recordService.stop();
        String ip = request.getServerName();
        int port = request.getServerPort();
        return "<span>" + msg + "</span><br><span><a href=\"" + downloadUrl.replace("(ip)", ip).replace("(port)", port + "") + "/ll?d=/record\" >点击查看所有录音文件</a></span>";
    }

}
