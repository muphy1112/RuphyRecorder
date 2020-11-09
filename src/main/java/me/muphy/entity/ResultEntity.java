package me.muphy.entity;

import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

public class ResultEntity {
    private String message;
    private boolean success;
    private int code;
    private Object data;

    public ResultEntity(String message, int code, Object data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public boolean getSuccess() {
        this.success = 0 == code;
        return this.success;
    }

    public String getMessage() {
        if (StringUtils.isEmpty(message)) {
            return this.getSuccess() ? "success" : "error";
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
