package com.imist.italker.factory.presenter.contact;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.data.helper.UserHelper;
import com.imist.italker.factory.model.card.UserCard;
import com.imist.italker.factory.model.db.AppDatabase;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.model.db.User_Table;
import com.imist.italker.factory.persistence.Account;
import com.imist.italker.factory.presenter.BasePresenter;
import com.imist.italker.factory.utils.DiffUiDataCallback;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
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

        //加载网络数据
        UserHelper.refreshContacts(new DataSource.Callback<List<UserCard>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                //网络刷新失败，因为本地有数据，不管错误
            }

            @Override
            public void onDataLoaded(List<UserCard> userCards) {
                final List<User> users = new ArrayList<>();
                for (UserCard userCard : userCards){
                    users.add(userCard.build());
                }
                DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
                definition.beginTransactionAsync(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        FlowManager.getModelAdapter(User.class)
                                .saveAll(users);
                    }
                }).build().execute();
                //网络的数据往往是新的。我们需要直接刷新到界面
                List<User> old = getView().getRecyclerAdapter().getItems();
                //会导致数据顺序全部为新的数据集合
                //getView().getRecyclerAdapter().replace(users);
                diff(old,users);
            }
        });
        /**
         *  // TODO
         // 关注后虽然储存了数据库，但是没有刷新联系人；
         //如果刷新数据库，或者从网络刷新，最终数显的时候都是全局刷新；
         //本地/网络刷新，添加到界面的时候可能会有冲突，有时数据库快有时网络快，显示信息不全
         //如何识别在数据库中已经有了该数据
         */
    }


    private void diff(List<User> oldList,List<User> newList){
        //进行数据对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(oldList,newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        //在比对完成之后对数据进行新的赋值
        getView().getRecyclerAdapter().replace(newList);
        //尝试刷新界面；
        result.dispatchUpdatesTo(getView().getRecyclerAdapter());

        getView().onAdapterDataChanged();
    }


}
