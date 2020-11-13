package me.muphy.controller;

import me.muphy.entity.ResultEntity;
import me.muphy.servicce.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/record")
public class RecorderController {

    @Autowired
    private RecordService recordService;

    @RolesAllowed({"ADMIN"})
    @RequestMapping("/start")
    public ResultEntity startRecord(int time) {
        return recordService.start(time);
    }

    @RequestMapping("/stop")
    public ResultEntity stopRecord() {
        return recordService.stop();
    }

}
