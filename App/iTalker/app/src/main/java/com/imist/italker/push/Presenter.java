package com.imist.italker.push;

import android.text.TextUtils;

public class Presenter  implements IPresenter{
    private  IView myView ;
    public Presenter(IView view){
        this.myView = view;
    }

    @Override
    public void search() {
       String inputString = myView.getInputString();
       if(TextUtils.isEmpty(inputString)){
            return;
       }
       int hashCode = inputString.hashCode();
       IUserServices services = new UserService();
       String result = "Result : "+inputString + "_" +services.search(hashCode);
       myView.setResultString(result);
    }
}
