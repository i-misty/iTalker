package com.imist.italker.factory.data.user;



import com.imist.italker.factory.data.BaseDbRepository;
import com.imist.italker.factory.data.DataSource;

import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.model.db.User_Table;
import com.imist.italker.factory.persistence.Account;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import java.util.List;


/**
 * 联系人数据仓库
 */
public class ContactRepository  extends BaseDbRepository<User> implements ContactDataSource{

    @Override
    public void load(DataSource.SuccessCallback<List<User>> callback) {
        super.load(callback);
        //加载本地数据库数据
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


    /**
     * 检查一个user 是否是我关注的数据
     * @param user
     * @return true 是我关注的数据
     */
   protected boolean isRequired(User user){
       return user.isFollow() && !user.getId().equals(Account.getUserId());
   }
}
