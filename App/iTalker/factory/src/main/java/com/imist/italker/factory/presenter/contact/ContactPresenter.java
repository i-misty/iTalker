package com.imist.italker.factory.presenter.contact;

import android.support.annotation.NonNull;

import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.model.db.User_Table;
import com.imist.italker.factory.persistence.Account;
import com.imist.italker.factory.presenter.BasePresenter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.List;

/**
 * 联系人的presenter实现
 */
public class ContactPresenter extends BasePresenter<ContactContract.View>
        implements ContactContract.Presenter{
    public ContactPresenter(ContactContract.View view) {
        super(view);
    }

    //实现方法已经在基类实现

    //重写基类的start 开始加载数据
    @Override
    public void start() {
        super.start();
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name,true)
                .limit(100)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<User>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction, @NonNull List<User> tResult) {
                        getView().getRecyclerAdapter().replace(tResult);
                        getView().onAdapterDataChanged();
                    }
                }).execute();
    }
}
