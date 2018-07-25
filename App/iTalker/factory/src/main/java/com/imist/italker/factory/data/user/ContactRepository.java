package com.imist.italker.factory.data.user;

import android.support.annotation.NonNull;

import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.data.helper.DbHelper;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.model.db.User_Table;
import com.imist.italker.factory.persistence.Account;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 联系人数据仓库
 */
public class ContactRepository  implements ContactDataSource ,
        QueryTransaction.QueryResultListCallback<User>,
        DbHelper.ChangedListener<User>{

    private DataSource.SuccessCallback<List<User>> callback;

    @Override
    public void load(DataSource.SuccessCallback<List<User>> callback) {
        this.callback = callback;
        //对数据辅助工具类添加一个数据更新的监听
        DbHelper.addChangeListener(User.class,this);
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    public void dispose() {
        this.callback = null;
        //取消对数据集合的监听
        DbHelper.removeChangeListener(User.class,this);
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<User> tResult) {
        //数据库加载数据成功
        if (tResult.size() == 0){
            users.clear();
            notifyDataChange();
            return;
        }
        //转变为数组
        User[] users = tResult.toArray(new User[0]);
        //回到数据集更新的操作中
        onDataSave(users);
    }


    /**
     * 数据库库变更的操作
     * @param list
     */
    @Override
    public void onDataSave(User... list) {
        boolean isChanged = false;
        for (User user : list) {
            //是关注的人但是不是我自己
            if (isRequired(user)){
                insertOrUpdate(user);
                isChanged = true;
            }
        }
        //有数据变更，则进行界面刷新
        if (isChanged){
            notifyDataChange();
        }
    }

    /**
     * 数据库销毁的操作
     * @param list
     */
    @Override
    public void onDataDelete(User... list) {
        boolean isChanged = false;
        //数据库删除的操作
        for (User user : list) {
            if (users.remove(user))
                isChanged = true;
        }
        if (isChanged){
            notifyDataChange();
        }
    }


    private List<User> users = new LinkedList<>();
    private void insertOrUpdate(User user){
        int index  = indexOf(user);
        if (index >= 0){
            replace(index,user);
        }else {
            insert(user);
        }
    }
    private void replace(int index ,User user){
        users.remove(index);
        users.add(index,user);
    }

    private void insert(User user){
        users.add(user);
    }

    private int indexOf(User user){
        int index = -1;
        for (User user1 : users) {
            index ++;
            if (user1.isSame(user)){
                return index;
            }
        }
        return -1;
    }
    private void notifyDataChange(){
        if (callback != null){
            callback.onDataLoaded(users);
        }
    }



    /**
     * 检查一个user 是否是我关注的数据
     * @param user
     * @return true 是我关注的数据
     */
   private boolean isRequired(User user){
       return user.isFollow() && !user.getId().equals(Account.getUserId());
   }
}
