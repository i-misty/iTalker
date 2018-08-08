package com.imist.italker.factory.net;

import com.imist.italker.factory.model.api.RspModel;
import com.imist.italker.factory.model.api.account.AccountRspModel;
import com.imist.italker.factory.model.api.account.LoginModel;
import com.imist.italker.factory.model.api.account.RegisterModel;
import com.imist.italker.factory.model.api.group.GroupCreateModel;
import com.imist.italker.factory.model.api.group.GroupMemberAddModel;
import com.imist.italker.factory.model.api.message.MsgCreateModel;
import com.imist.italker.factory.model.api.user.UserUpdateModel;
import com.imist.italker.factory.model.card.GroupCard;
import com.imist.italker.factory.model.card.GroupMemberCard;
import com.imist.italker.factory.model.card.MessageCard;
import com.imist.italker.factory.model.card.UserCard;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 网络请求的所有接口
 */
public interface RemoteService {

    /**
     * 注册接口
     *
     * @param model RegisterModel
     * @return AccountRspModel
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登陆接口
     *
     * @param model LoginModel
     * @return AccountRspModel
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定接口
     *
     * @param pushId
     * @return
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true, value = "pushId") String pushId);

    /**
     * 用户更新的接口
     *
     * @param model
     * @return
     */
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    /**
     * 用户搜索的接口
     *
     * @param name
     * @return
     */
    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> userSearch(@Path("name") String name);

    /**
     * 用户关注的接口
     *
     * @param followId
     * @return
     */
    @PUT("user/follow/{userId}")
    Call<RspModel<UserCard>> userFollow(@Path("userId") String followId);

    /**
     * 获取联系人列表
     *
     * @return
     */
    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();

    /**
     * 获取某人的信息
     *
     * @return
     */
    @GET("user/{userId}")
    Call<RspModel<UserCard>> userFind(@Path("userId") String userId);

    /**
     * 发送消息的接口
     *
     * @return
     */
    @POST("msg")
    Call<RspModel<MessageCard>> msgPush(@Body MsgCreateModel moel);

    /**
     *创建群
     *
     * @return
     */
    @POST("group")
    Call<RspModel<GroupCard>> groupCreate(@Body GroupCreateModel model);

    /**
     *创建群
     *
     * @return
     */
    @GET("group/groupId")
    Call<RspModel<GroupCard>> groupFind(@Path("groupId") String groupId);

    /**
     * 群搜索的接口
     *
     * @param name
     * @return
     */
    @GET("group/search/{name}")
    Call<RspModel<List<GroupCard>>> groupSearch(@Path(value ="name" ,encoded = true) String name);

    /**
     * 我的群列表
     *
     * @param date
     * @return
     */
    @GET("group/list/{date}")
    Call<RspModel<List<GroupCard>>> groups(@Path(value = "date",encoded = true)  String date);

    /**
     * 群的成员
     *
     * @param groupId
     * @return
     */
    @GET("group/{groupId}/{member}")
    Call<RspModel<List<GroupMemberCard>>> groupMembers(@Path(value = "groupId")  String groupId);

    /**
     * 群的成员添加
     *
     * @param groupId
     * @return
     */
    @POST("group/{groupId}/{member}")
    Call<RspModel<List<GroupMemberCard>>> groupMemberAdd(@Path(value = "groupId")  String groupId,
                                                         @Body GroupMemberAddModel model);




}
