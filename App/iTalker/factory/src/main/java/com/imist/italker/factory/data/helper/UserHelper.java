package com.imist.italker.factory.data.helper;

import android.icu.util.IslamicCalendar;

import com.imist.italker.factory.Factory;
import com.imist.italker.factory.R;
import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.model.api.RspModel;
import com.imist.italker.factory.model.api.user.UserUpdateModel;
import com.imist.italker.factory.model.card.UserCard;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.model.db.User_Table;
import com.imist.italker.factory.net.Network;
import com.imist.italker.factory.net.RemoteService;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHelper {
    //更新用户信息
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback){
        RemoteService service =  Network.remote();
        Call<RspModel<UserCard>> call  = service.userUpdate(model);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()){
                    UserCard userCard = rspModel.getResult();
                    //数据库的存储操作
                    User user = userCard.build();
                    user.save();
                    //返回成功
                    callback.onDataLoaded(userCard);
                }else {
                    Factory.decodeRspCode(rspModel,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                //请求失败返回,这里不会为null
               callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    //搜索的方法
    public static Call search(String name, final DataSource.Callback<List<UserCard>> callback){
        RemoteService service =  Network.remote();
        Call<RspModel<List<UserCard>>> call  = service.userSearch(name);
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()){
                    callback.onDataLoaded(rspModel.getResult());
                }else {
                    Factory.decodeRspCode(rspModel,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        //将当前的调度者返回
        return call;
    }


    /**
     * 关注的网络请求
     * @param userId
     * @param callback
     */
    public static void follow(String userId, final DataSource.Callback<UserCard> callback) {

        RemoteService service =  Network.remote();
        Call<RspModel<UserCard>> call  = service.userFollow(userId);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()){
                    UserCard card = rspModel.getResult();
                    User user = card.build();
                    user.save();
                    //TODO 通知联系人列表刷新

                    //返回数据
                    callback.onDataLoaded(rspModel.getResult());
                }else {
                    Factory.decodeRspCode(rspModel,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }


    //获取联系人
    public static void refreshContacts( final DataSource.Callback<List<UserCard>> callback){
        RemoteService service =  Network.remote();
        Call<RspModel<List<UserCard>>> call  = service.userContacts();
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()){
                    callback.onDataLoaded(rspModel.getResult());
                }else {
                    Factory.decodeRspCode(rspModel,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }


    /**
     * 从本地查询一个用户信息
     * @param id
     * @return
     */
    public static User findFromLocal(String id){
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }
    /**
     * 从网络查询一个用户信息
     * @param id
     * @return
     */
    public static User findFromNet(String id){
        RemoteService service = Network.remote();
        try {
            Response<RspModel<UserCard>> rspModel = service.userFind(id).execute();
            UserCard card = rspModel.body().getResult();
            if (card != null){
                //TODO 数据库刷新但是没有通知
                User user = card.build();
                user.save();
                return  user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *搜索一个用户，优先本地缓存，没有就从网络拉取
     * @param id
     * @return
     */
    public static User searchFirstLocal(String id){
           User user = findFromLocal(id);
           if (user == null){
               return findFromNet(id);
           }
           return user;
    }

    /**
     *搜索一个用户，优先网络拉取，没有就从本地缓存
     * @param id
     * @return
     */
    public static User searchFirstNet(String id){
        User user = findFromNet(id);
        if (user == null){
            return findFromLocal(id);
        }
        return user;
    }
}
