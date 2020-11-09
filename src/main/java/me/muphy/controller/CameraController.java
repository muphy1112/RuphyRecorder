package me.muphy.controller;

import me.muphy.entity.ResultEntity;
import me.muphy.servicce.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/camera")
public class CameraController {

    @Autowired
    private CameraService cameraService;

    @RequestMapping("/takePicture")
    public ResultEntity takePictures(String[] args) {
        return cameraService.takePictures();
    }
}
