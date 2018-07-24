package com.imist.italker.factory.data.helper;

import com.imist.italker.factory.model.db.Session;
import com.imist.italker.factory.model.db.Session_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * 会话的辅助工具类
 */
public class SessionHelper {
    //从本地查询session
    public static Session findFromLocal(String id) {
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}
