package com.imist.italker.push.frags.media;


import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.imist.italker.common.tools.UiTool;
import com.imist.italker.common.widget.GalleryView;
import com.imist.italker.push.R;

import net.qiujuer.genius.ui.Ui;


public class GalleryFragment extends BottomSheetDialogFragment implements GalleryView.SelectedChangeListener {

    private GalleryView mGallery;
    private OnSelectedListener mListener;

    public GalleryFragment() {

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new BottomSheetDialog(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGallery = (GalleryView) root.findViewById(R.id.galleryView);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGallery.setup(getLoaderManager(), this);
    }

    @Override
    public void onSelectedCountChanged(int count) {
        // 如果选中了一张推按
        if (count > 0) {
            //隐藏自己
            dismiss();
            if (mListener != null) {
                // 得到所有图片的返回路径
                String[] paths = mGallery.getSelectedPath();
                mListener.onSelectedImage(paths[0]);
                // 取消和唤醒着之间的引用
                mListener = null;
            }
        }
    }


    /**
     * 设置事件监听，并返回自己
     *
     * @param listener OnSelectedListener
     * @return GalleryFragment
     */
    public GalleryFragment setListener(OnSelectedListener listener) {
        mListener = listener;
        return this;
    }

    /**
     * 选中图片的监听器
     */
    public interface OnSelectedListener {

        void onSelectedImage(String path);
    }

    private static class TransStatusBottomDheetDialog extends BottomSheetDialog {

        public TransStatusBottomDheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomDheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }

        protected TransStatusBottomDheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Window window = getWindow();
            if (window == null) {
                return;
            }
            int screenHeight = UiTool.getScreenHeight(getOwnerActivity());
            int statusHeight = UiTool.getStatusBarHeight(getOwnerActivity());
            int dialogHeight = screenHeight = statusHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    dialogHeight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);

        }
    }
}
