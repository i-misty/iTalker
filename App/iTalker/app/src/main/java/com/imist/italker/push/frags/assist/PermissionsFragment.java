package com.imist.italker.push.frags.assist;


import android.Manifest;
import android.app.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imist.italker.common.app.Application;
import com.imist.italker.push.R;
import com.imist.italker.push.frags.media.GalleryFragment;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * 权限申请弹出框
 */
public class PermissionsFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks {

    private static final int RC = 0x0100;

    public PermissionsFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new BottomSheetDialog(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_permissions, container, false);
        root.findViewById(R.id.btn_submit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestPerm();
                    }
                });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshState(getView());
    }

    private void refreshState(View root) {
        if (root == null)
            return;
        Context context = getContext();
        root.findViewById(R.id.im_state_permission_network).setVisibility(haveNetworkPerm(context) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_read).setVisibility(haveReadPerm(context) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_write).setVisibility(haveWritePerm(context) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_record_audio).setVisibility(haveRecordAudioPerm(context) ? View.VISIBLE : View.GONE);
    }

    private static boolean haveNetworkPerm(Context context) {
        //准备需要检查的权限
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE

        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static boolean haveReadPerm(Context context) {
        //准备需要检查的权限
        String[] perms = new String[]{

                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        return EasyPermissions.hasPermissions(context, perms);
    }

    private static boolean haveWritePerm(Context context) {
        //准备需要检查的权限
        String[] perms = new String[]{

                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static boolean haveRecordAudioPerm(Context context) {
        //准备需要检查的权限
        String[] perms = new String[]{
                Manifest.permission.RECORD_AUDIO

        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    private static void show(FragmentManager manager) {
        new PermissionsFragment().show(manager, PermissionsFragment.class.getName());
    }

    public static boolean haveAll(Context context, FragmentManager maneger) {
        boolean haveAll = haveNetworkPerm(context)
                && haveReadPerm(context)
                && haveWritePerm(context)
                && haveRecordAudioPerm(context);
        if (!haveAll) {
            show(maneger);
        }
        return haveAll;
    }


    /**
     * 申请权限的方法, 若是一个一个申请的话每个权限单独标示，然后进入不同的权限申请方法
     */
    @AfterPermissionGranted(RC)
    private void requestPerm() {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            Application.showToast(R.string.label_permission_ok);
            //Fragment 中调用getView拿到根布局，前提是onCreateView
            refreshState(getView());
        } else {
            //请求权限
            EasyPermissions.requestPermissions(this, getString(R.string.title_assist_permissions), RC,perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //如果权限被拒绝则弹出弹出框，用户去到设置界面设置
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .build()
                    .show();
        }
    }

    /**
     * 权限申请的回调方法，将权限的申请状态交给EasyPermissions框架
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //传递对应的参数，并且告知权限的处理者是我自己
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
