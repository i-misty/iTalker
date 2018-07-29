package com.imist.italker.factory.presenter.contact;

import com.imist.italker.factory.model.card.UserCard;
import com.imist.italker.factory.presenter.BaseContract;

public interface FollowContract {
    interface Presenter extends BaseContract.Presenter {
        //关注一个人
        void follow(String userId);
    }

    interface View extends BaseContract.View<Presenter> {
        //关注成功的情况下返回一个用户信息
        void onFollowSucceed(UserCard card);
    }
}
