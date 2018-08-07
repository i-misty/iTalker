package com.imist.italker.factory.data.helper;

import com.imist.italker.factory.Factory;
import com.imist.italker.factory.R;
import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.model.api.RspModel;
import com.imist.italker.factory.model.api.group.GroupCreateModel;
import com.imist.italker.factory.model.card.GroupCard;
import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.model.db.Group_Table;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.net.Network;
import com.imist.italker.factory.net.RemoteService;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;

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
}
