package com.imist.italker.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import com.imist.italker.factory.data.message.SessionDataSource;
import com.imist.italker.factory.data.message.SessionRepository;
import com.imist.italker.factory.model.db.Session;
import com.imist.italker.factory.presenter.BaseSourcePresenter;
import com.imist.italker.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 最近的聊天列表的presenter
 */

//第一个session,第二个界面显示的数据
public class SessionPresenter extends BaseSourcePresenter<Session,Session,
        SessionDataSource,SessionContract.View> implements SessionContract.Presenter{
    public SessionPresenter(SessionContract.View view) {

        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> session) {
        SessionContract.View view = getView();
        if (view == null){
            return;
        }
        //差异对比
        List<Session> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Session> callback = new DiffUiDataCallback<>(old,session);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //刷新界面；
        refreshData(result,session);
    }
}
