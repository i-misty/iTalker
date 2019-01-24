package com.imist.italker.factory.presenter.message;

import com.imist.italker.factory.model.db.Session;
import com.imist.italker.factory.presenter.BaseContract;

public interface SessionContract {
    //开始就直接调用基类
    interface Presenter extends BaseContract.Presenter {

    }

    //都在基类完成了
    interface View extends BaseContract.RecyclerView<Presenter, Session> {

    }
}
