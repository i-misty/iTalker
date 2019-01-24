package com.imist.italker.factory.model.api.message;

import com.imist.italker.factory.model.card.MessageCard;
import com.imist.italker.factory.model.db.Message;
import com.imist.italker.factory.persistence.Account;

import java.util.Date;
import java.util.UUID;

public class MsgCreateModel {

    private String id;


    private String content;


    private String attach;


    private int type = Message.TYPE_STR;


    private String receiverId;

    private int receiverType = Message.RECEIVER_TYPE_NONE;

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAttach() {
        return attach;
    }

    public int getType() {
        return type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    private MsgCreateModel() {
        //随机产生一个UUID
        this.id = UUID.randomUUID().toString();
    }

    public void refreshByCard() {
        if (card == null)
            return;
        //刷新内容和附件信息
        this.content = card.getContent();
        this.attach = card.getAttach();
    }

    /**
     * 建造者模式快速建立一个发送一个model
     */
    public static class Builder {
        private MsgCreateModel model;

        public Builder() {
            this.model = new MsgCreateModel();
        }

        //设置接收者
        public Builder receiver(String receiverId, int receiverType) {
            this.model.receiverId = receiverId;
            this.model.receiverType = receiverType;
            return this;
        }

        //设置内容
        public Builder content(String content, int type) {
            this.model.content = content;
            this.model.type = type;
            return this;
        }

        //设置内容
        public Builder attach(String attach) {
            this.model.attach = attach;
            return this;
        }

        public MsgCreateModel build() {
            return this.model;
        }
    }


    //当我们需要发送一个文件的时候content 刷新的问题

    private MessageCard card;

    public MessageCard buildCard() {
        if (card == null) {
            MessageCard card = new MessageCard();
            card.setId(id);
            card.setContent(content);
            card.setAttach(attach);
            card.setType(type);
            card.setSenderId(Account.getUserId());

            if (receiverType == Message.RECEIVER_TYPE_GROUP) {
                card.setGroupId(receiverId);
            } else {
                card.setReceiverId(receiverId);
            }
            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date());
            this.card = card;
        }
        return this.card;
    }

    /**
     * 把一个message消息转换为一个创建状态的CreateModel
     *
     * @param message
     * @return
     */
    public static MsgCreateModel buildWithMessage(Message message) {
        MsgCreateModel model = new MsgCreateModel();
        model.id = message.getId();//因为没有set方法；重发（更新）消息就需要内部设置，
        model.content = message.getContent();
        model.type = message.getType();
        model.attach = message.getAttach();

        if (message.getReceiver() != null) {
            model.receiverId = message.getReceiver().getId();
            model.receiverType = Message.RECEIVER_TYPE_NONE;
        } else {
            model.receiverId = message.getGroup().getId();
            model.receiverType = Message.RECEIVER_TYPE_GROUP;
        }
        return model;
    }

}
