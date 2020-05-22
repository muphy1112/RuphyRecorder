package me.muphy.camera;

import me.muphy.servicce.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CameraController {

    @Autowired
    private CameraService cameraService;

    @GetMapping("/tp")
    public String takePictures(String[] args) {
        String msg = cameraService.takePictures();
        return "<div><span>" + msg + "</span></div><div>" +
                "<span><a href=\"/ll?d=/picture\" >点击查看所有照片</a></span>" +
                "<span style=\"margin-left: 20px;\"><a href=\"/\" >返回首页</a></span></div>";
    }
}
