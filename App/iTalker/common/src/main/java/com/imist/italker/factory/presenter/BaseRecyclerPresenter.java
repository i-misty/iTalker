package com.imist.italker.factory.presenter;

import android.support.v7.util.DiffUtil;

import com.imist.italker.common.widget.recycler.RecyclerAdapter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

/**
 * 对recyclerView 进行的一个简单的recycler封装
 *
 * @param <ViewModel>
 * @param <View>
 */
public class BaseRecyclerPresenter<ViewModel, View extends BaseContract.RecyclerView>
        extends BasePresenter<View> {
    public BaseRecyclerPresenter(View view) {
        super(view);
    }

    /**
     * 刷新一堆新数据在主线城中；
     *
     * @param dataList
     */
    protected void refreshData(final List<ViewModel> dataList) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                View view = getView();
                if (view == null) {
                    return;
                }
                RecyclerAdapter<ViewModel> adapter = view.getRecyclerAdapter();
                adapter.replace(dataList);
                view.onAdapterDataChanged();
            }
        });
    }

    /**
     * 刷新界面操作，该操作可以保证界面的结果在主线程完成
     *
     * @param diffResult
     * @param dataList
     */
    protected void refreshData(final DiffUtil.DiffResult diffResult, final List<ViewModel> dataList) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                refreshDataOnUiThread(diffResult, dataList);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void refreshDataOnUiThread(final DiffUtil.DiffResult diffResult, final List<ViewModel> dataList) {
        View view = getView();
        if (view == null) {
            return;
        }
        //基本的更新数据并且刷新页面
        RecyclerAdapter<ViewModel> adapter = view.getRecyclerAdapter();
        //改变界面集合并不通知界面刷新
        adapter.getItems().clear();
        adapter.getItems().addAll(dataList);
        //通知界面刷新占位布局
        view.onAdapterDataChanged();
        //进行增量更新
        diffResult.dispatchUpdatesTo(adapter);
    }

}
