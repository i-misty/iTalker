package com.imist.italker.factory.presenter.message;

import com.imist.italker.factory.data.helper.GroupHelper;
import com.imist.italker.factory.data.helper.UserHelper;
import com.imist.italker.factory.data.message.MessageGroupRepository;
import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.model.db.Message;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.model.db.view.MemberUserModel;
import com.imist.italker.factory.persistence.Account;

import java.util.List;

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
            //初始化操作，这里的view不可能为null，因为刚刚start
            ChatContact.GroupView view = getView();
            boolean isAdmin = Account.getUserId().equalsIgnoreCase(group.getOwner().getId());

            view.showAdminOption(isAdmin);
            //基础信息初始化
            view.onInit(group);
            //群成员信息初始化
            List<MemberUserModel> models = group.getLatelyGroupMembers();
            final long memberCount = group.getGroupMemberCount();
            //现在的全部成员去掉之前懒加载的部分成员得到多余的成员
            long moreCount = memberCount - models.size();
            view.onInitGroupMembers(models,moreCount);
        }

    }

}
