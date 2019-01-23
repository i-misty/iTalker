package com.imist.italker.factory.data.helper;

import com.imist.italker.factory.Factory;
import com.imist.italker.factory.R;
import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.model.api.RspModel;
import com.imist.italker.factory.model.api.group.GroupCreateModel;
import com.imist.italker.factory.model.card.GroupCard;
import com.imist.italker.factory.model.card.GroupMemberCard;
import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.model.db.GroupMember;
import com.imist.italker.factory.model.db.GroupMember_Table;
import com.imist.italker.factory.model.db.Group_Table;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.model.db.User_Table;
import com.imist.italker.factory.model.db.view.MemberUserModel;
import com.imist.italker.factory.net.Network;
import com.imist.italker.factory.net.RemoteService;
import com.imist.italker.factory.presenter.search.SeachGroupPresenter;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupHelper {

    public static Group find(String groupId) {

        Group group = findFromLocal(groupId);
        if (group == null)
            group = findFromNet(groupId);
        return group;
    }


    public static Group findFromLocal(String groupId) {
        return SQLite.select()
                .from(Group.class)
                .where(Group_Table.id.eq(groupId))
                .querySingle();
    }

    public static Group findFromNet(String groupId) {
        RemoteService service = Network.remote();
        try {
            Response<RspModel<GroupCard>> response = service.groupFind(groupId).execute();
            GroupCard card = response.body().getResult();
            if (card != null) {
                Factory.getGroupCenter().dispatch(card);
                User user = UserHelper.searchFirstNet(card.getOwnerId());
                if (user != null) {
                    return card.build(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //群创建
    public static void create(GroupCreateModel model, final DataSource.Callback<GroupCard> callback) {

        RemoteService service = Network.remote();
        service.groupCreate(model).enqueue(new Callback<RspModel<GroupCard>>() {
            @Override
            public void onResponse(Call<RspModel<GroupCard>> call, Response<RspModel<GroupCard>> response) {
                RspModel<GroupCard> rspModel = response.body();
                if (rspModel.success()) {
                    GroupCard groupCard = rspModel.getResult();
                    Factory.getGroupCenter().dispatch(groupCard);
                    callback.onDataLoaded(groupCard);
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<GroupCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });

    }

    public static Call search(String name, final DataSource.Callback<List<GroupCard>> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<List<GroupCard>>> call = service.groupSearch(name);
        call.enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()){
                    callback.onDataLoaded(rspModel.getResult());
                }else {
                    Factory.decodeRspCode(rspModel,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        return call;
    }

    //刷新群组列表
    public static void refreshGroups() {
        RemoteService service = Network.remote();
        service.groups("").enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()){
                   List<GroupCard> groupCards = rspModel.getResult();
                   if (groupCards != null && groupCards.size() > 0){
                       //进行调度显示
                       Factory.getGroupCenter().dispatch(groupCards.toArray(new GroupCard[0]));
                   }
                }else {
                    Factory.decodeRspCode(rspModel,null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                //不做任何操作
            }
        });
    }

    //获取一个群的成员数量
    public static long getMemberCount(String id) {
        return SQLite.selectCountOf()//成员数量查询而不是查询列
                .from(GroupMember.class)
                .where(GroupMember_Table.group_id.eq(id))
                .count();
    }

    //从网络去刷新一个成员信息
    public static void refreshGroupMember(Group group) {
        RemoteService service = Network.remote();
        service.groupMembers(group.getId()).enqueue(new Callback<RspModel<List<GroupMemberCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupMemberCard>>> call, Response<RspModel<List<GroupMemberCard>>> response) {
                RspModel<List<GroupMemberCard>> rspModel = response.body();
                if (rspModel.success()){
                    List<GroupMemberCard> memberCards = rspModel.getResult();
                    if (memberCards != null && memberCards.size() > 0){
                        //进行调度显示
                        Factory.getGroupCenter().dispatch(memberCards.toArray(new GroupMemberCard[0]));
                    }
                }else {
                    Factory.decodeRspCode(rspModel,null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupMemberCard>>> call, Throwable t) {
                //不做任何操作
            }
        });
    }

    //关联查询一个用户和群成员的表，返回一个MemberUserModel集合
    public static List<MemberUserModel> getMemberUsers(String groupId, int size) {

        return SQLite.select(GroupMember_Table.alias.withTable().as("alias"),//查询到的结果赋值给对应的字段
                User_Table.id.withTable().as("userId"),
                User_Table.name.withTable().as("name"),
                User_Table.portrait.withTable().as("portrait")
                ).from(GroupMember.class)
                .join(User.class, Join.JoinType.INNER)
                .on(GroupMember_Table.user_id.withTable().eq(User_Table.id.withTable()))
                .where(GroupMember_Table.group_id.withTable().eq(groupId))
                .orderBy(GroupMember_Table.user_id,true)
                .limit(size)
                .queryCustomList(MemberUserModel.class);
    }
}
