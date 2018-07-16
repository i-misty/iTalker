package com.imist.italker.factory.model.db;


import java.util.Date;

public class User {

    private String id;


    private String name;


    private String phone;

    private String portrait;


    private String desc;


    private int sex = 0;
    //我对某人的备注信息
    private String alias;
    //用户关注人的数量

    private int follows;
    //用户粉丝数量

    private int following;
    //我与当前的user的关系状态，是否关注了这人

    private boolean isFollow;
    //用户信息最后的更新时间

    private Date modifyAt ;

}
