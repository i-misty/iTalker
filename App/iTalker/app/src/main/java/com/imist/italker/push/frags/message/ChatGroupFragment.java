package com.imist.italker.push.frags.message;


import android.app.Fragment;

import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.presenter.message.ChatContact;
import com.imist.italker.push.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatGroupFragment extends ChatFragment<Group>
        implements ChatContact.GroupView {


    public ChatGroupFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat_group;
    }

    @Override
    protected ChatContact.Presenter initPresenter() {
        return null;
    }

    @Override
    public void onInit(Group group) {

    }
}
