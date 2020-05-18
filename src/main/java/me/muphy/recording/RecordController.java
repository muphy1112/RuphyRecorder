package me.muphy.recording;

import me.muphy.servicce.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.sound.sampled.*;
import java.io.*;

@RestController
@RequestMapping("/record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @GetMapping("/start")
    public String startRecord(int t, HttpServletRequest request) {
        String msg = recordService.start(t);
        return "<span>" + msg + "</span><br><span><a href=\"/record/stop\" >点击停止录音</a>" +
                "</span><span style=\"margin-left: 20px;\"><a href=\"/ll?d=/record\" >点击查看所有录音文件</a></span>";
    }

    @GetMapping("/stop")
    public String stopRecord(HttpServletRequest request) {
        String msg = recordService.stop();
        return "<span>" + msg + "</span><br><span><a href=\"/ll?d=/record\" >点击查看所有录音文件</a></span>";
    }

}
