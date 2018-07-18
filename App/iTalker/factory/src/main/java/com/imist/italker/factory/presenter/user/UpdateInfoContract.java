package com.imist.italker.factory.presenter.user;


import com.imist.italker.factory.presenter.BaseContract;

public interface UpdateInfoContract {
    interface Presenter extends BaseContract.Presenter{
        void update(String photeFilePath,String desc,boolean isMan);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateSuccess();
    }
}
