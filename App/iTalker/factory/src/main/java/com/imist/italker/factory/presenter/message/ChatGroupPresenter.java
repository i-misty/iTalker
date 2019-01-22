package com.imist.italker.factory.presenter.message;

import com.imist.italker.factory.data.helper.GroupHelper;
import com.imist.italker.factory.data.helper.UserHelper;
import com.imist.italker.factory.data.message.MessageGroupRepository;
import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.model.db.Message;
import com.imist.italker.factory.model.db.User;

public class ChatGroupPresenter  extends ChatPresenter<ChatContact.GroupView>
        implements ChatContact.Presenter {


    public ChatGroupPresenter(ChatContact.GroupView view, String mReceiverId) {
        super(new MessageGroupRepository(mReceiverId), view, mReceiverId, Message.RECEIVER_TYPE_GROUP);
    }

    @Override
    public void start() {
        super.start();
        //从本地拿到群的信息
        Group group = GroupHelper.findFromLocal( mReceiverId);
        if (group != null){
            //初始化操作
        }

    }

}
