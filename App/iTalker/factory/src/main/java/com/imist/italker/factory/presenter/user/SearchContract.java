package com.imist.italker.factory.presenter.user;

import com.imist.italker.factory.model.card.GroupCard;
import com.imist.italker.factory.model.card.UserCard;
import com.imist.italker.factory.presenter.BaseContract;

import java.util.List;


public interface SearchContract {
    interface Presenter extends BaseContract.Presenter{
        //搜索内容
        void search(String content);
    }

    interface UserView extends BaseContract.View<Presenter>{
        void onSearchDone(List<UserCard> userCards);
    }

    interface GroupView extends BaseContract.View<Presenter>{
        void onSearchDone(List<GroupCard> userCards);
    }

}
