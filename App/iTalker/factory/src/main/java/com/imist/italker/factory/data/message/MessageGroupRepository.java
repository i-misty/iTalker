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
 * 关注的内容一定是我发送群或者别人发送到群的消息
 */
public class MessageGroupRepository extends BaseDbRepository<Message>
        implements MessageDataSource {

    //聊天的群id
    private String receiverId;

    public MessageGroupRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SuccessCallback<List<Message>> callback) {
        super.load(callback);
        //无论是自己发，还是别人发的，只要是发到这个群的，这个group_id就是receiverId
        SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(receiverId))
                .orderBy(Message_Table.createAt, false)
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();


    }

    @Override
    protected boolean isRequired(Message message) {
        //如果消息的group不为null，则一定是发送到一个群的
        //如果群ID等于我们需要的，那就是通过;
        return message.getGroup() != null
                && receiverId.equalsIgnoreCase(message.getGroup().getId());

    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        //反转返回的集合在倒序
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
