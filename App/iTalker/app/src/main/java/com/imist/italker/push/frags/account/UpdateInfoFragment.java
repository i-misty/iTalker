package com.imist.italker.push.frags.account;


import com.imist.italker.common.app.Fragment;
import com.imist.italker.common.widget.PortraitView;
import com.imist.italker.push.R;
import com.imist.italker.push.frags.media.GalleryFragment;

import butterknife.BindView;
import butterknife.OnClick;


public class UpdateInfoFragment extends Fragment {

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    public UpdateInfoFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @OnClick(R.id.im_portrait)
    void onPortraitViewClick() {
        new GalleryFragment().setListener(new GalleryFragment.OnSelectedListener() {
            @Override
            public void onSelectedImage(String path) {

            }
            //show的时候建议使用getChildFragmentManager
        }).show(getChildFragmentManager(), GalleryFragment.class.getName());
    }
}
