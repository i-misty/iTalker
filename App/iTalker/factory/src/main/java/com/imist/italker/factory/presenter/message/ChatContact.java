package com.imist.italker.factory.presenter.message;

import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.model.db.Message;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.model.db.view.MemberUserModel;
import com.imist.italker.factory.presenter.BaseContract;

import java.util.List;

public interface ChatContact {
    interface Presenter extends BaseContract.Presenter {
        void pushText(String content);

        void pushAudio(String path);

        //可以发送多张图片
        void pushImage(String[] paths);

        //重新发送
        boolean rePush(Message message);
    }

    //抽象一个View
    interface View<InitModel> extends BaseContract.RecyclerView<Presenter, Message> {
        //初始化的model
        void onInit(InitModel model);

    }

    //联系人聊天界面
    interface UserView extends View<User> {

    }

    //群聊天的界面
    interface GroupView extends View<Group> {
        //是否是管理员
        void showAdminOption(boolean isAdmin);

        //初始化成员
        void onInitGroupMembers(List<MemberUserModel> members, long moreCount);


    }
}
