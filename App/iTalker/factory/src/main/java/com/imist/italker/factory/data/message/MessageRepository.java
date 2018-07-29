package com.imist.italker.factory.data.message;


import android.support.annotation.NonNull;

import com.imist.italker.factory.data.BaseDbRepository;
import com.imist.italker.factory.model.db.Message;
import com.imist.italker.factory.model.db.Message_Table;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;


/**
 * 跟某人聊天的时候的聊天列表
 * 关注的内容一定是我发送给他的或者他发送给我的
 */
public class MessageRepository extends BaseDbRepository<Message>
        implements MessageDataSource {

    //聊天的对象id
    private String receiverId;

    public MessageRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SuccessCallback<List<Message>> callback) {
        super.load(callback);

        SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(receiverId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(receiverId))
                .orderBy(Message_Table.createAt, false)
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Message message) {
        //receiverId如果是发送者，那么Group == null 情况下一定是发送给我的消息；
        //如果这个消息的接收者不为空，那么一定是发送给某个人的，这个人只能是我活着某个人
        //如果这个某个人就是receiverid那么就是我需要关注的信息
        return (receiverId.equalsIgnoreCase(message.getSender().getId())
                && message.getGroup() == null)
                || (message.getReceiver() != null
                && receiverId.equalsIgnoreCase(message.getReceiver().getId()));
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        //反转返回的集合在倒序
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
