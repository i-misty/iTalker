package com.imist.italker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imist.italker.common.app.Application;
import com.imist.italker.common.app.PresenterToolbarActivity;
import com.imist.italker.common.app.ToolbarActivity;
import com.imist.italker.common.widget.PortraitView;
import com.imist.italker.common.widget.recycler.RecyclerAdapter;
import com.imist.italker.factory.presenter.BaseContract;
import com.imist.italker.factory.presenter.BaseRecyclerPresenter;
import com.imist.italker.factory.presenter.group.GroupCreateContact;
import com.imist.italker.factory.presenter.group.GroupCreatePresenter;
import com.imist.italker.push.R;
import com.imist.italker.push.frags.media.GalleryFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class GroupCreateActivity extends PresenterToolbarActivity<GroupCreateContact.Presenter>
        implements GroupCreateContact.View {

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    @BindView(R.id.edit_name)
    EditText mName;

    @BindView(R.id.edit_desc)
    EditText mDesc;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    private String mPortraitPath;

    private Adapter mAdapter;

    public static void show(Context context) {
        context.startActivity(new Intent(context, GroupCreateActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_create;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter = new Adapter());
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create) {
            //进行创建
            onCreateClick();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.im_portrait)
    void onPortraitViewClick() {
        hideSoftKeyboard();
        new GalleryFragment().setListener(new GalleryFragment.OnSelectedListener() {
            @Override
            public void onSelectedImage(String path) {
                UCrop.Options options = new UCrop.Options();
                // 设置图片的处理格式
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                //压缩之后的图片质量
                options.setCompressionQuality(96);
                File dPath = Application.getPortraitTmpFile();
                UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(520, 520) // 返回最大的尺寸
                        .withOptions(options)
                        .start(GroupCreateActivity.this);
            }
            //show的时候建议使用getChildFragmentManager
        }).show(getSupportFragmentManager(), GalleryFragment.class.getName());
    }


    private void loadPortait(Uri resultUri) {
        mPortraitPath = resultUri.getPath();
        Glide.with(this)
                .load(resultUri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }


    private void onCreateClick() {
        hideSoftKeyboard();
        String name = mName.getText().toString().trim();
        String desc = mDesc.getText().toString().trim();
        mPresenter.create(name, desc, mPortraitPath);
    }

    private void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view == null)
            return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @Override
    protected GroupCreateContact.Presenter initPresenter() {
        return new GroupCreatePresenter(this);
    }


    @Override
    public void onCreateSuccessed() {
        hideLoading();
        Application.showToast(R.string.label_group_create_succeed);
        finish();
    }

    @Override
    public RecyclerAdapter<GroupCreateContact.ViewModel> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        hideLoading();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                loadPortait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Application.showToast(R.string.data_rsp_error_unknown);
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private class Adapter extends RecyclerAdapter<GroupCreateContact.ViewModel> {


        @Override
        protected int getItemViewType(int position, GroupCreateContact.ViewModel viewModel) {
            return R.layout.cell_group_create_contact;
        }

        @Override
        protected ViewHolder<GroupCreateContact.ViewModel> onCreateViewHolder(View root, int viewType) {
            return new ViewHodel(root);
        }
    }

    class ViewHodel extends RecyclerAdapter.ViewHolder<GroupCreateContact.ViewModel> {
        @BindView(R.id.im_portrait)
        PortraitView mPprtrait;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.cb_select)
        CheckBox mSelect;

        ViewHodel(View itemView) {
            super(itemView);
        }

        @OnCheckedChanged(R.id.cb_select)
        void onCheckedChanged(boolean checked){
            //对状态进行更改
            mPresenter.changeSelect(mData,checked);
        }
        @Override
        protected void onBind(GroupCreateContact.ViewModel viewModel) {

            mPortrait.setup(Glide.with(GroupCreateActivity.this),viewModel.author);
            mName.setText(viewModel.author.getName());
            mSelect.setChecked(viewModel.isSelect);

        }
    }

}
