package com.imist.italker.factory.presenter.group;

import com.imist.italker.factory.model.db.view.MemberUserModel;
import com.imist.italker.factory.presenter.BaseContract;

/**
 * 群成员的契约
 */
public interface GroupMemberContract {
    interface Presenter extends BaseContract.Presenter {
        //具有一个刷新的方法
        void refresh();

    }

    interface View extends BaseContract.RecyclerView<Presenter, MemberUserModel> {
        //通过界面获取群的ID而不是通过构造函数传递
        String getGroupId();
    }
}
