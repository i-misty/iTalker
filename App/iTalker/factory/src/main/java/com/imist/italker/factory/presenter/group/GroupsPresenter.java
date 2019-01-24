package com.imist.italker.factory.presenter.group;

import android.support.v7.util.DiffUtil;

import com.imist.italker.factory.data.group.GroupsDataSource;
import com.imist.italker.factory.data.group.GroupsRespository;
import com.imist.italker.factory.data.helper.GroupHelper;
import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.presenter.BaseSourcePresenter;
import com.imist.italker.factory.utils.DiffUiDataCallback;

import java.util.List;

public class GroupsPresenter extends BaseSourcePresenter<Group, Group,
        GroupsDataSource, GroupContract.View> implements GroupContract.Presenter {

    public GroupsPresenter(GroupContract.View view) {
        super(new GroupsRespository(), view);
    }

    @Override
    public void start() {
        super.start();
        //加载网络数据，可以优化到下拉刷新中；
        //只有用户进行下拉的时候进行网络请求；
        GroupHelper.refreshGroups();
    }

    @Override
    public void onDataLoaded(List<Group> groups) {
        final GroupContract.View view = getView();
        if (view == null)
            return;
        //对比差异
        List<Group> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Group> callback = new DiffUiDataCallback<>(old, groups);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //界面刷新
        refreshData(result, groups);

    }
}
