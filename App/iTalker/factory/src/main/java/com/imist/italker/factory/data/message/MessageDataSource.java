package com.imist.italker.factory.data.message;

import com.imist.italker.factory.data.DbDataSource;
import com.imist.italker.factory.model.db.Message;

/**
 * 消息数据源的定义，他的实现类是 MessageRepository;
 * 关注的对象是Message表
 */
public interface MessageDataSource extends DbDataSource<Message> {

}
