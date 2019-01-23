package com.imist.italker.factory.presenter.group;

import com.imist.italker.factory.presenter.BaseContract;

/**
 * 群成员添加契约
 */
public interface GroupMemberAddContract {

    interface Presenter extends BaseContract.Presenter{
        //提交成员
        void submit();
        //更改一个Model的选中状态
        void changeSelect(GroupCreateContract.ViewModel model , boolean isSelect);
    }

    interface View extends BaseContract.RecyclerView<Presenter,GroupCreateContract.ViewModel>{
        //添加群成员成功;
        void onAddSuccessed();

        //获取群成员的id
        String getGroupId();
    }
}
