package com.imist.italker.factory.data.user;

import com.imist.italker.factory.model.card.UserCard;

/**
 * 用户中心的基本定义
 */
public interface UserCenter {
    //分发处理一堆卡片，并且更新到数据库
    void dispatch(UserCard... cards);
}
