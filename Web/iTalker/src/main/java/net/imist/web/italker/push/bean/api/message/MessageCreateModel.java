package net.imist.web.italker.push.bean.api.message;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;
import net.imist.web.italker.push.bean.db.Group;
import net.imist.web.italker.push.bean.db.Message;
import net.imist.web.italker.push.bean.db.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * API请求的model格式
 */
public class MessageCreateModel {
    //ID从客户端生成一个UUID;
    @Expose
    private String id;

    @Expose
    private String content;

    @Expose
    private String attach;
    @Expose
    //消息类型
    private int type;

    //接收者可为空
    @Expose
    private String receiverId;
    //接收者类型 群，人
    @Expose
    private int receiverType = Message.RECEIVER_TYPE_NONE;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }

    public static boolean check(MessageCreateModel model) {
        return model != null
                && !(Strings.isNullOrEmpty(model.id)
                ||Strings.isNullOrEmpty(model.content)
                || Strings.isNullOrEmpty(model.receiverId))//以上都不能为null

                &&(model.receiverType == Message.RECEIVER_TYPE_NONE
                || model.receiverType == Message.RECEIVER_TYPE_GROUP)//接收者为二者之一；

                &&(model.type == Message.TYPE_STR
                        ||model.type == Message.TYPE_AUDIO
                        ||model.type == Message.TYPE_FILE
                        ||model.type == Message.TYPE_PIC);


    }

}
