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

        //Todo
    }

    @Override
    protected boolean isRequired(Message message) {
        return false;
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        //反转返回的集合在倒序
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
