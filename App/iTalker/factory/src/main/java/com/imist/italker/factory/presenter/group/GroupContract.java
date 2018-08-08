package com.imist.italker.factory.presenter.group;

import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.presenter.BaseContract;

/**
 *我的群列表契约
 */
public interface GroupContract {

    //都在基类完成了
    interface View extends BaseContract.RecyclerView<Presenter, Group> {

    }

    //不需要定义，只需要开始的时候 start()即可
    interface Presenter extends BaseContract.Presenter {

    }

}
