package com.imist.italker.push.frags.search;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imist.italker.common.app.Fragment;
import com.imist.italker.push.R;
import com.imist.italker.push.activities.SearchActivity;

/**
 * 搜索群的界面
 */
public class SearchGroupFragment extends Fragment implements SearchActivity.SearchFragment{


    public SearchGroupFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    public void search(String content) {

    }
}
