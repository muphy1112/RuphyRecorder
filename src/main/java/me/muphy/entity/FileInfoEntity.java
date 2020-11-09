package me.muphy.entity;

import org.springframework.util.StringUtils;

import java.io.Serializable;

public class FileInfoEntity implements Serializable, Comparable<FileInfoEntity> {

    private String type;
    private String fileName;
    private String ksize;
    private String msize;
    private String lastModified;
    private String operate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getKsize() {
        return ksize;
    }

    public void setKsize(String ksize) {
        this.ksize = ksize;
    }

    public String getMsize() {
        return msize;
    }

    public void setMsize(String msize) {
        this.msize = msize;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    @Override
    public String toString() {
        return "FileInfoEntity{" +
                "type='" + type + '\'' +
                ", fileName='" + fileName + '\'' +
                ", ksize=" + ksize +
                ", msize=" + msize +
                ", lastModified=" + lastModified +
                ", operate='" + operate + '\'' +
                '}';
    }

    @Override
    public int compareTo(FileInfoEntity o) {
        if (StringUtils.isEmpty(this.type)) {
            return -1;
        }
        int i = this.type.compareTo(o.type);
        if (i == 0) {
            if (StringUtils.isEmpty(this.lastModified)) {
                return -1;
            }
            return this.lastModified.compareTo(o.lastModified);
        }
        return -i;
    }

}
