package me.muphy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class ApplicationConfig {

    @Value("${app.base-path:/workspace/download/}")
    private String basePath;
    @Value("${app.upload.path:/upload/}")
    private String uploadPath;
    @Value("${app.camera.path:/picture/}")
    private String cameraPath;
    @Value("${app.record.path:/record/}")
    private String recordPath;
    @Value("${app.record.time.default:3600}")
    private int defaultTime;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getCameraPath() {
        return cameraPath;
    }

    public void setCameraPath(String cameraPath) {
        this.cameraPath = cameraPath;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public int getDefaultTime() {
        return defaultTime;
    }

    public void setDefaultTime(int defaultTime) {
        this.defaultTime = defaultTime;
    }
}
