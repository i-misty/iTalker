package com.imist.italker.push.activities;

import android.content.Context;
import android.content.Intent;

import com.imist.italker.common.app.Activity;
import com.imist.italker.push.MainActivity;
import com.imist.italker.push.R;
import com.imist.italker.push.frags.account.UpdateInfoFragment;

public class AccountActivity extends Activity {


    public static void show(Context context){
        context.startActivity(new Intent(context, AccountActivity.class));
    }
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container,new UpdateInfoFragment())
                .commit();
    }
}
