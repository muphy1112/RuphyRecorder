package me.muphy.util;

import me.muphy.entity.ResultEntity;

public final class JsonMessageUtils {

    public static ResultEntity error(String msg) {
        return entity(msg, -1);
    }

    public static ResultEntity success(String msg) {
        return entity(msg, 0);
    }

    public static ResultEntity entity(String msg) {
        return entity(msg, 0);
    }

    public static ResultEntity entity(String msg, int code) {
        return entity(msg, code, null);
    }

    public static ResultEntity entity(String msg, Object data) {
        return entity(msg, 0, data);
    }

    public static ResultEntity entity(String msg, int code, Object data) {
        return new ResultEntity(msg, code, data);
    }

}
