package me.muphy.servicce;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CameraService {

    @Value("${download.path:E:/workspace/download/}")
    private String downloadPath;

    private static class W{
        public static Webcam webcam = Webcam.getDefault();
    }

    public String takePictures() {
            Webcam webcam = W.webcam;
        if (webcam == null) {
            return "没有找到摄像设备！";
        }
        String filePath = downloadPath + "/picture/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File path = new File(filePath);
        if (!path.exists()) {//如果文件不存在，则创建该目录
            path.mkdirs();
        }
        String time = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        File file = new File(filePath + "/" + time + ".jpg");
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        WebcamUtils.capture(webcam, file);
        webcam.close();
        return "拍照成功！";
    }
}
