package com.imist.italker.push.activities;

import android.content.Context;
import android.content.Intent;

import com.imist.italker.common.app.Activity;
import com.imist.italker.common.app.Fragment;
import com.imist.italker.push.R;
import com.imist.italker.push.frags.user.UpdateInfoFragment;

public class UserActivity extends Activity {

    private Fragment mCurFragment;

    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_user;
    }
    @Override
    protected void initWidget() {
        super.initWidget();
        mCurFragment = new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mCurFragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurFragment.onActivityResult(requestCode, resultCode, data);
    }
}
