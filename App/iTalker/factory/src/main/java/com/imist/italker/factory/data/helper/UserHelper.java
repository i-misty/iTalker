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
import com.imist.italker.factory.model.db.view.UserSampleModel;
import com.imist.italker.factory.net.Network;
import com.imist.italker.factory.net.RemoteService;
import com.imist.italker.factory.persistence.Account;
import com.imist.italker.utils.CollectionUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHelper {
    //更新用户信息
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<UserCard>> call = service.userUpdate(model);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    //唤起进行保存的操作
                    Factory.getUserCenter().dispatch(userCard);
                    //返回成功
                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
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
    public static Call search(String name, final DataSource.Callback<List<UserCard>> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<List<UserCard>>> call = service.userSearch(name);
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    Factory.decodeRspCode(rspModel, callback);
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
     *
     * @param userId
     * @param callback
     */
    public static void follow(final String userId, final DataSource.Callback<UserCard> callback) {

        RemoteService service = Network.remote();
        Call<RspModel<UserCard>> call = service.userFollow(userId);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    //唤起进行保存的操作
                    Factory.getUserCenter().dispatch(userCard);
                    //返回数据
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }


    //刷新联系人的操作，不需要callback,直接存到数据库
    //并且通过数据库观察者进行通知界面刷新
    //界面更新的时候进行对比，然后进行差异更新
    public static void refreshContacts() {
        RemoteService service = Network.remote();
        Call<RspModel<List<UserCard>>> call = service.userContacts();
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    //拿到集合
                    List<UserCard> cards = rspModel.getResult();
                    if (cards == null || cards.size() == 0)
                        return;
                    UserCard[] cards1 = cards.toArray(new UserCard[0]);
                    //CollectionUtil.toArray(cards,UserCard.class);
                    Factory.getUserCenter().dispatch(cards1);

                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                //callback.onDataNotAvailable(R.string.data_network_error);
                //nothing ...
            }
        });
    }


    /**
     * 从本地查询一个用户信息
     *
     * @param id
     * @return
     */
    public static User findFromLocal(String id) {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    /**
     * 从网络查询一个用户信息
     *
     * @param id
     * @return
     */
    public static User findFromNet(String id) {
        RemoteService service = Network.remote();
        try {
            Response<RspModel<UserCard>> rspModel = service.userFind(id).execute();
            UserCard card = rspModel.body().getResult();
            if (card != null) {
                User user = card.build();
                //数据库的存储并且通知
                Factory.getUserCenter().dispatch(card);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 搜索一个用户，优先本地缓存，没有就从网络拉取
     *
     * @param id
     * @return
     */
    public static User searchFirstLocal(String id) {
        User user = findFromLocal(id);
        if (user == null) {
            return findFromNet(id);
        }
        return user;
    }

    /**
     * 搜索一个用户，优先网络拉取，没有就从本地缓存
     *
     * @param id
     * @return
     */
    public static User searchFirstNet(String id) {
        User user = findFromNet(id);
        if (user == null) {
            return findFromLocal(id);
        }
        return user;
    }

    /**
     * 获取联系人
     *
     * @return
     */
    public static List<User> getContacts() {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .queryList();
    }

    /**
     * 获取联系人
     * 但是是一个简单的数据的
     *
     * @return
     */
    public static List<UserSampleModel> getSampleContacts() {
        return SQLite.select(User_Table.id.withTable().as("id"),
                User_Table.name.withTable().as("name"),
                User_Table.portrait.withTable().as("portrait"))
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .queryCustomList(UserSampleModel.class);
    }
}
