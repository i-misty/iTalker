package com.imist.italker.factory.data.helper;

import com.imist.italker.factory.Factory;
import com.imist.italker.factory.R;
import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.model.api.RspModel;
import com.imist.italker.factory.model.api.account.AccountRspModel;
import com.imist.italker.factory.model.api.account.RegisterModel;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.net.Network;
import com.imist.italker.factory.net.RemoteService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountHelper {
    public static void register(RegisterModel model, final DataSource.Callback<User> callback) {

        RemoteService service = Network.getRetrofit().create(RemoteService.class);
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        //异步的请求
        call.enqueue(new Callback<RspModel<AccountRspModel>>() {
            @Override
            public void onResponse(Call<RspModel<AccountRspModel>> call,
                                   Response<RspModel<AccountRspModel>> response) {
                //请求成功返回
                RspModel<AccountRspModel> rspModel = response.body();
                if (rspModel.success()) {
                    AccountRspModel accountRspModel = rspModel.getResult();
                    if (accountRspModel.isBind()) {
                        User user = accountRspModel.getUser();
                        //进行数据库的写入和缓存绑定
                        callback.onDataLoaded(user);
                    } else {
                        //进行绑定的唤起
                        bindPush(callback);
                    }
                } else {
                    Factory.decodeRspCode(rspModel,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
                //请求失败返回
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    /**
     * 对设备的ID进行绑定
     * @param callback
     */
    private static void bindPush(DataSource.Callback<User> callback) {
        callback.onDataNotAvailable(R.string.app_name);
    }
}
