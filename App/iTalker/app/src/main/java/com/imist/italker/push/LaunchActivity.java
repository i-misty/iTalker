package com.imist.italker.push;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.imist.italker.common.app.Activity;
import com.imist.italker.push.activities.MainActivity;
import com.imist.italker.push.frags.assist.PermissionsFragment;

public class LaunchActivity extends Activity {


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionsFragment.haveAll(this,getSupportFragmentManager())){
            MainActivity.show(this);
            finish();
        }
    }
}
