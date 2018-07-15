package com.imist.italker.factory.presenter.account;

import com.imist.italker.factory.presenter.BaseContract;

public class LoginContact {
    interface View extends BaseContract.View{
        void loginSuccess();
    }

    interface Presenter extends BaseContract.Presenter{
        void login(String phone, String name, String password);

    }

}
