package com.imist.italker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.imist.italker.common.app.Activity;
import com.imist.italker.common.app.Fragment;
import com.imist.italker.push.R;
import com.imist.italker.push.frags.account.AccountTrigger;
import com.imist.italker.push.frags.account.LoginFragment;
import com.imist.italker.push.frags.account.RegisterFragment;

import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;

public class AccountActivity extends Activity implements AccountTrigger{

    private Fragment mCurFragment;
    private Fragment mLoginFragment;
    private Fragment mRegisterFragment;

    @BindView(R.id.im_bg)
    ImageView mBg;

    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        mCurFragment = mLoginFragment = new LoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mCurFragment)
                .commit();
        Glide.with(this)
                .load(R.drawable.bg_src_tianjin)
                .centerCrop()
                .into(new ViewTarget<ImageView ,GlideDrawable>(mBg) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

                        Drawable drawable = resource.getCurrent();
                        drawable = DrawableCompat.wrap(drawable);
                        drawable.setColorFilter(UiCompat.getColor(getResources(),R.color.colorAccent),
                                PorterDuff.Mode.SCREEN);//设置着色效果和颜色，蒙版模式
                        this.view.setImageDrawable(drawable);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void triggerView() {
        Fragment fragment;
        if (mCurFragment == mLoginFragment){
            if (mRegisterFragment == null){
                mRegisterFragment = new RegisterFragment();
            }
            fragment = mRegisterFragment;
        }else {
            fragment = mLoginFragment;
        }
        //重新复制当前正在显示的fragment
        mCurFragment = fragment;
        //切换显示
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.lay_container,fragment)
                .commit();
    }
}
