package me.muphy.util;

public enum BeautifulStringUtils {

    BACK,
    ERROR,
    SUCCESS,
    INFO;

    public static String message(String msg){
        return message(msg, SUCCESS);
    }

    public static String message(String msg, BeautifulStringUtils status){
        if(BACK.equals(status)){
            return "<script>alert('" + msg + "');history.back();</script>";
        }
        String color = "#dce";
        if(ERROR.equals(status)){
            color = "red";
        } else if(INFO.equals(status)){
            color = "#ccc";
        }
        return "<h1 style='color: " + color + "'>" + msg + "</h1>";
    }
}
