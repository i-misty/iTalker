package com.imist.italker.factory.presenter.contact;

import com.imist.italker.common.widget.recycler.RecyclerAdapter;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.presenter.BaseContract;

import java.util.List;

/**
 *
 */
public interface ContactContract {

    //都在基类完成了
    interface View extends BaseContract.RecyclerView<Presenter, User> {

    }

    //不需要定义，只需要开始的时候 start()即可
    interface Presenter extends BaseContract.Presenter {

    }

}
