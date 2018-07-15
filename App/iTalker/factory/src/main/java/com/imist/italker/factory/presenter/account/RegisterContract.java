package com.imist.italker.factory.presenter.account;


import com.imist.italker.factory.presenter.BaseContract;

/**
 * @version 1.0.0
 */
//***这里得是接口才可以跨包，跨模块访问接口
public interface RegisterContract {
    interface View extends BaseContract.View<Presenter> {
        // 注册成功
        void registerSuccess();
    }

    interface Presenter extends BaseContract.Presenter {
        // 发起一个注册
        void register(String phone, String name, String password);

        // 检查手机号是否正确
        boolean checkMobile(String phone);
    }

}
