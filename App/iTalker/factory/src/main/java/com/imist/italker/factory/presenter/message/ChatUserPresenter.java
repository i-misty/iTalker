package com.imist.italker.factory.presenter.message;

import com.imist.italker.factory.data.helper.UserHelper;
import com.imist.italker.factory.data.message.MessageRepository;
import com.imist.italker.factory.model.db.Message;
import com.imist.italker.factory.model.db.User;

public class ChatUserPresenter extends ChatPresenter<ChatContact.UserView>
        implements ChatContact.Presenter {


    public ChatUserPresenter(ChatContact.UserView view, String mReceiverId) {
        super(new MessageRepository(mReceiverId), view, mReceiverId, Message.RECEIVER_TYPE_NONE);
    }

    @Override
    public void start() {
        super.start();
        //从本地拿到这个人的信息
        User mReceiver = UserHelper.findFromLocal(mReceiverId);
        getView().onInit(mReceiver);

    }
}
