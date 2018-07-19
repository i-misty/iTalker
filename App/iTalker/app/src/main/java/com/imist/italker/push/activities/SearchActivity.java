package com.imist.italker.push.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.imist.italker.common.app.Fragment;
import com.imist.italker.common.app.ToolbarActivity;
import com.imist.italker.push.R;
import com.imist.italker.push.frags.search.SearchGroupFragment;
import com.imist.italker.push.frags.search.SearchUserFragment;

public class SearchActivity extends ToolbarActivity {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final int TYPE_USER = 1;
    public static final int TYPE_GROUP = 2;
    private int type;
    private SearchFragment mSearchFragment;

    public static void show(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        type = bundle.getInt(EXTRA_TYPE);
        //是搜索人或者搜索群
        return type == TYPE_USER || type == TYPE_GROUP;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        Fragment fragment ;
        if (type == TYPE_USER){
            SearchUserFragment searchUserFragment = new SearchUserFragment();
            fragment = searchUserFragment;
            mSearchFragment = searchUserFragment;
        }else{
            SearchGroupFragment searchGroupFragment = new SearchGroupFragment();
            fragment = searchGroupFragment;
            mSearchFragment = searchGroupFragment;
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container,fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        //这里要是v7包下的
        final SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //当点击提交按钮的时候
                    /*if (!searchView.isIconified()) {
                        searchView.setIconified(true);
                    }
                    searchItem.collapseActionView();*/
                    search(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    //当文字改变的时候，不会及时搜索，只在为mull的情况下进行搜索
                    if (TextUtils.isEmpty(s)) {
                        search("");
                        return true;
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 搜索的发起点，
     * @param query
     */
    private void search(String query) {
        if (mSearchFragment == null) return;
        mSearchFragment.search(query);
    }

    /**
     * search 搜索的fragment必须继承的接口,这样在调度的时候直接用接口定义的方法
     * 从而隐藏子类的实现
     */
    public interface SearchFragment{
        void search(String content);
    }
}
