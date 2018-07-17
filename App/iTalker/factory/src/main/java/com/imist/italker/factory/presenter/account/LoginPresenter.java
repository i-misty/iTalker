package com.imist.italker.factory.presenter.account;


import android.text.TextUtils;

import com.imist.italker.factory.R;
import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.data.helper.AccountHelper;
import com.imist.italker.factory.model.api.account.LoginModel;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.persistence.Account;
import com.imist.italker.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * 登录的逻辑实现
 */
public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter,DataSource.Callback<User>{
    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    @Override
    public void login(String phone, String password) {
        start();
        final LoginContract.View view = getView();
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)){
            view.showError(R.string.data_rsp_error_parameters);
        }else {
            LoginModel model = new LoginModel(phone,password//,Account.getPushId()
            );
            AccountHelper.login(model,this);
        }

    }

    @Override
    public void onDataLoaded(User data) {
        final LoginContract.View view = getView();
        if (view == null) return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.loginSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final LoginContract.View view = getView();
        if (view == null) return;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });

    }
}
