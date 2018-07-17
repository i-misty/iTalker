package com.imist.italker.common;



public class Common {
    /**
     * 一些不可变得永痕参数，通常用于写配置
     */
    public interface Constance{
        //手机正则
        String REGEX_MOBILE = "[1][3,4,5,7,8][0-9]{9}$";

        //String API_URL = "http://192.168.1.120:8080/api/";
        String API_URL = "http://192.168.10.178:8080/api/";
    }
}
