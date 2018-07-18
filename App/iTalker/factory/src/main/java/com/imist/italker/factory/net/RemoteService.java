package com.imist.italker.factory.net;

import com.imist.italker.factory.model.api.RspModel;
import com.imist.italker.factory.model.api.account.AccountRspModel;
import com.imist.italker.factory.model.api.account.LoginModel;
import com.imist.italker.factory.model.api.account.RegisterModel;
import com.imist.italker.factory.model.api.user.UserUpdateModel;
import com.imist.italker.factory.model.card.UserCard;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 网络请求的所有接口
 */
public interface RemoteService {

    /**
     * 注册接口
     * @param model RegisterModel
     * @return AccountRspModel
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登陆接口
     * @param model LoginModel
     * @return AccountRspModel
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定接口
     * @param pushId
     * @return
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true ,value = "pushId") String pushId);

    /**
     * 用户更新的接口
     * @param model
     * @return
     */
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);
}
