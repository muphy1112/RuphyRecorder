package me.muphy.util;

public enum BeautifulStringUtil {

    ERROR,
    SUCCESS,
    INFO;

    public static String message(String msg){
        return message(msg, SUCCESS);
    }

    public static String message(String msg, BeautifulStringUtil status){
        String color = "#dce";
        if(ERROR.equals(status)){
            color = "red";
        } else if(INFO.equals(status)){
            color = "#ccc";
        }
        return "<h1 style='color: " + color + "'>" + msg + "</h1>";
    }
}
