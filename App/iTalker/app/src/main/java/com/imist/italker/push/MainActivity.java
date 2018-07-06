package com.imist.italker.push;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.imist.italker.common.Common;
import com.imist.italker.common.app.Activity;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends Activity implements IView{

    @BindView(R.id.text_result)
    TextView mResultText;
    @BindView(R.id.edit_query)
    EditText mInputText;
    IPresenter presenter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();
        presenter = new Presenter(this);
    }

    @OnClick(R.id.btn_submit)
    void onSubmit(){
        presenter.search();
    }

    @Override
    public String getInputString() {
        return mInputText.getText().toString();
    }

    @Override
    public void setResultString(String string) {
        mResultText.setText(string);
    }
}
