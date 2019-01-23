package com.imist.italker.factory.presenter.group;

import com.imist.italker.factory.Factory;
import com.imist.italker.factory.data.helper.GroupHelper;
import com.imist.italker.factory.data.helper.UserHelper;
import com.imist.italker.factory.model.db.GroupMember;
import com.imist.italker.factory.model.db.view.MemberUserModel;
import com.imist.italker.factory.model.db.view.UserSampleModel;
import com.imist.italker.factory.presenter.BaseRecyclerPresenter;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberPresenter extends BaseRecyclerPresenter<MemberUserModel,GroupMemberContract.View>
    implements GroupMemberContract.Presenter{

    public GroupMemberPresenter(GroupMemberContract.View view) {
        super(view);
    }

    @Override
    public void refresh() {
        //显示loading
        start();
        //异步加载
        Factory.runOnAsync(loader);
    }

    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            GroupMemberContract.View view = getView();
            if (view == null)
                return;
            String groupId = view.getGroupId();
            // -1 代表查询所有
            List<MemberUserModel> models = GroupHelper.getMemberUsers(groupId,-1);
            refreshData(models);
        }
    };
}
