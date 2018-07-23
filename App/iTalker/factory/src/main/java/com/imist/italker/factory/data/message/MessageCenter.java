package com.imist.italker.factory.data.message;

import com.imist.italker.factory.model.card.MessageCard;

/**
 * 进行消息卡片的消费
 */
public interface MessageCenter {
    void dispatch(MessageCard ...cards);
}
