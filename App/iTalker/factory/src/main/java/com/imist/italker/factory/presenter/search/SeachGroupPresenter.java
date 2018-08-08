package com.imist.italker.factory.presenter.search;

import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.data.helper.GroupHelper;
import com.imist.italker.factory.data.helper.UserHelper;
import com.imist.italker.factory.model.card.GroupCard;
import com.imist.italker.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import retrofit2.Call;

/**
 * 搜索群的逻辑实现
 */
public class SeachGroupPresenter extends BasePresenter<SearchContract.GroupView>
        implements SearchContract.Presenter,DataSource.Callback<List<GroupCard>> {

    private Call searchCall;

    public SeachGroupPresenter(SearchContract.GroupView view) {
        super(view);
    }

    @Override
    public void search(String content) {
        start();
        Call call = searchCall;
        //如果有上一次的请求，并且没有取消，则取消请求操作
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
        //这里的请求肯定是要执行的，但是执行之前需要判断之前的请求有没有取消，之前这里犯错直接else
        // ,导致搜索的时候直接取消了，没有发起请求，迟迟没有回调结果，界面也没有刷新
        searchCall = GroupHelper.search(content, this);
    }


    @Override
    public void onDataNotAvailable(final int strRes) {
        //搜索失败，
        final SearchContract.GroupView view = getView();
        if (view != null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                view.showError(strRes);
                }
            });
        }
    }

    @Override
    public void onDataLoaded(final List<GroupCard> groupCards) {
        final SearchContract.GroupView view = getView();
        if (view != null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onSearchDone(groupCards);
                }
            });
        }
    }
}
