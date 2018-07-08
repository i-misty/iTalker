package com.imist.italker.push.frags.main;


import com.imist.italker.common.app.Fragment;
import com.imist.italker.common.widget.GalleryView;
import com.imist.italker.push.R;

import butterknife.BindView;

public class ActiveFragment extends Fragment {

    @BindView(R.id.GalleryView)
    GalleryView mGallery;
    public ActiveFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initData() {
        super.initData();
        mGallery.setup(getLoaderManager(), new GalleryView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {

            }
        });
    }
}
