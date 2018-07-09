package com.imist.italker.push.frags.assist;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imist.italker.common.widget.GalleryView;
import com.imist.italker.push.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionsFragment extends BottomSheetDialog {


    public PermissionsFragment(@NonNull Context context) {
        super(context);
    }

    public PermissionsFragment(@NonNull Context context, int theme) {
        super(context, theme);
    }

    protected PermissionsFragment(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
