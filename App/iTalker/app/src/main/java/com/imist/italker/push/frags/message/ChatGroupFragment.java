package com.imist.italker.push.frags.message;


import android.app.Fragment;

import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.model.db.view.MemberUserModel;
import com.imist.italker.factory.presenter.message.ChatContact;
import com.imist.italker.factory.presenter.message.ChatGroupPresenter;
import com.imist.italker.push.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatGroupFragment extends ChatFragment<Group>
        implements ChatContact.GroupView {


    public ChatGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_group;
    }

    @Override
    protected ChatContact.Presenter initPresenter() {
        return new ChatGroupPresenter(this,mReceiverId);
    }

    @Override
    public void onInit(Group group) {

    }

    @Override
    public void onInitGroupMembers(List<MemberUserModel> members, int moreCount) {

    }

    @Override
    public void showAdminOption(boolean isAdmin) {

    }



}
