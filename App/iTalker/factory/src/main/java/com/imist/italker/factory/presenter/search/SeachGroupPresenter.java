package com.imist.italker.factory.presenter.search;

import com.imist.italker.factory.presenter.BasePresenter;

/**
 * 搜索群的逻辑实现
 */
public class SeachGroupPresenter extends BasePresenter<SearchContract.GroupView> implements SearchContract.Presenter{

    public SeachGroupPresenter(SearchContract.GroupView view) {
        super(view);
    }

    @Override
    public void search(String content) {

    }
}
