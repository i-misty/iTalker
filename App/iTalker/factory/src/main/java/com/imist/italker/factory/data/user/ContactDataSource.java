package com.imist.italker.factory.data.user;

import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.model.db.User;

import java.util.List;

/**
 * 联系人数据源
 */
public interface ContactDataSource {
    /**
     * 对数据进行加载的职责
     * @param callback
     */
    void load (DataSource.SuccessCallback<List<User>> callback);

    /**
     * 销毁操作
     */
    void dispose();
}
