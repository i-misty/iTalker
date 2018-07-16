package com.imist.italker.factory.presenter.account;

import android.text.TextUtils;

import com.imist.italker.common.Common;
import com.imist.italker.factory.R;
import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.data.helper.AccountHelper;
import com.imist.italker.factory.model.api.account.RegisterModel;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.persistence.Account;
import com.imist.italker.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.regex.Pattern;

public class RegisterPresenter extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter, DataSource.Callback<User> {
    public RegisterPresenter(RegisterContract.View mView) {
        super(mView);
    }

    @Override
    public void register(String phone, String name, String password) {
        //调用start，启动loading
        start();
        //得到view接口
        RegisterContract.View view = getView();

        if (!checkMobile(phone)) {
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        } else if (name.length() < 2) {
            view.showError(R.string.data_account_register_invalid_parameter_name);
        } else if (password.length() < 6) {
            view.showError(R.string.data_account_register_invalid_parameter_password);
        } else {
            //进行网络请求
            //构造model进行网络请求
            RegisterModel model = new RegisterModel(phone, password, name, Account.getPushId());
            //进行网络请求，并且设置回送接口为自己
            AccountHelper.register(model, this);
        }
    }

    /**
     * 检查手机号是否合法
     *
     * @param phone 手机号码
     * @return 合法为True
     */
    @Override
    public boolean checkMobile(String phone) {
        // 手机号不为空，并且满足格式
        return !TextUtils.isEmpty(phone)
                && Pattern.matches(Common.Constance.REGEX_MOBILE, phone);
    }

    @Override
    public void onDataLoaded(User user) {
        //当网络请求成功，回送一个用户信息进来
        final RegisterContract.View view = getView();
        if (view == null) return;
        //强制执行到主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.registerSuccess();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        //网络请求告知注册失败
        //当网络请求失败，回送一个用户信息进来
        final RegisterContract.View view = getView();
        if (view == null) return;
        //强制执行到主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });
    }
}
