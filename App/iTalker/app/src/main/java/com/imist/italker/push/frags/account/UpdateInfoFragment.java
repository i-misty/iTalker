package com.imist.italker.push.frags.account;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.imist.italker.common.app.Application;
import com.imist.italker.common.app.Fragment;
import com.imist.italker.common.widget.PortraitView;
import com.imist.italker.factory.Factory;
import com.imist.italker.factory.net.UploadHelper;
import com.imist.italker.push.R;
import com.imist.italker.push.frags.media.GalleryFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


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
                UCrop.Options options = new UCrop.Options();
                // 设置图片的处理格式
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                //压缩之后的图片质量
                options.setCompressionQuality(96);
                File dPath = Application.getPortraitTmpFile();
                UCrop.of(Uri.fromFile(new File(path)) , Uri.fromFile(dPath))
                        .withAspectRatio(1,1)
                        .withMaxResultSize(520,520) // 返回最大的尺寸
                        .withOptions(options)
                        .start(getActivity());

            }
            //show的时候建议使用getChildFragmentManager
        }).show(getChildFragmentManager(), GalleryFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null){
                loadPortait(resultUri);
            }
        }else if (resultCode == UCrop.RESULT_ERROR){
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void loadPortait(Uri resultUri) {
        Glide.with(this)
                .load(resultUri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);

        // 拿到本地文件的地址
        final String localPath = resultUri.getPath();
        Log.e("TAG", "localPath:" + localPath);

        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                String url = UploadHelper.uploadPortrait(localPath);
                Log.e("TAG", "url:" + url);
            }
        });
    }
}
