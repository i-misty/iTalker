package com.imist.italker.factory.data.message;

import android.support.annotation.NonNull;

import com.imist.italker.factory.data.BaseDbRepository;
import com.imist.italker.factory.model.db.Session;
import com.imist.italker.factory.model.db.Session_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

public class SessionRepository extends BaseDbRepository<Session>
        implements SessionDataSource{

    @Override
    public void load(SuccessCallback<List<Session>> callback) {
        super.load(callback);
        SQLite.select()
                .from(Session.class)
                .orderBy(Session_Table.modifyAt,false)//倒序
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Session session) {
        //所有的会话我都需要，不需要过滤
        return true;
    }

    @Override
    protected void insert(Session session) {
        //super.insert(session);
        //复写方法，让新的数据加载到头部
        dataList.addFirst(session);
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Session> tResult) {
        //复写数据库回来的数据进行一次反转
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);

    }
}
