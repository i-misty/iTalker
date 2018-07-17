package com.imist.italker.factory.data.helper;

import android.text.TextUtils;

import com.imist.italker.factory.Factory;
import com.imist.italker.factory.R;
import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.model.api.RspModel;
import com.imist.italker.factory.model.api.account.AccountRspModel;
import com.imist.italker.factory.model.api.account.LoginModel;
import com.imist.italker.factory.model.api.account.RegisterModel;
import com.imist.italker.factory.model.db.AppDatabase;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.net.Network;
import com.imist.italker.factory.net.RemoteService;
import com.imist.italker.factory.persistence.Account;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountHelper {

    /**
     * 注册的接口异步调用
     *
     * @param model    注册的接口
     * @param callback
     */
    public static void register(final RegisterModel model, final DataSource.Callback<User> callback) {
        //调用retrofit对我们的网络请求接口做代理
        //RemoteService service = Network.getRetrofit().create(RemoteService.class);
        RemoteService service = Network.remote();
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 登陆的接口，异步调用
     *
     * @param model    登陆的model
     * @param callback
     */
    public static void login(final LoginModel model, final DataSource.Callback<User> callback) {
        //调用retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        Call<RspModel<AccountRspModel>> call = service.accountLogin(model);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 对设备的ID进行绑定
     *
     * @param callback
     */
    public static void bindPush(DataSource.Callback<User> callback) {
        //检查是否为空
        String pushId = Account.getPushId();
        if (TextUtils.isEmpty(pushId)) return;
        RemoteService service = Network.remote();
        Call<RspModel<AccountRspModel>> call = service.accountBind(pushId);
        call.enqueue(new AccountRspCallback(callback));
    }

    private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>> {
        private DataSource.Callback<User> callback;

        public AccountRspCallback(DataSource.Callback<User> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call,
                               Response<RspModel<AccountRspModel>> response) {
            //请求成功返回
            RspModel<AccountRspModel> rspModel = response.body();
            if (rspModel.success()) {
                AccountRspModel accountRspModel = rspModel.getResult();
                User user = accountRspModel.getUser();
                //进行数据库的写入和缓存绑定
                //第一种，直接保存一列
                user.save();
                   /* //2.ModelAdapter 可以保存集合
                    FlowManager.getModelAdapter(User.class)
                            .save(user);
                    //3.事务保存
                    DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
                    definition.beginTransactionAsync(new ITransaction() {
                        @Override
                        public void execute(DatabaseWrapper databaseWrapper) {
                            FlowManager.getModelAdapter(User.class)
                                    .save(user);
                        }
                    }).build().execute();*/
                //同步到xml持久化中
                Account.login(accountRspModel);
                if (accountRspModel.isBind()) {
                    Account.setBind(true);
                    if (callback != null)
                        callback.onDataLoaded(user);
                } else {
                    //进行绑定的唤起
                    bindPush(callback);
                }
            } else {
                Factory.decodeRspCode(rspModel, callback);
            }
        }

        @Override
        public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
            //请求失败返回
            if (callback != null)
                callback.onDataNotAvailable(R.string.data_network_error);
        }
    }
}
