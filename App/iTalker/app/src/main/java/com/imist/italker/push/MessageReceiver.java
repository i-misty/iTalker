package com.imist.italker.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.imist.italker.factory.Factory;
import com.imist.italker.factory.data.helper.AccountHelper;
import com.imist.italker.factory.persistence.Account;

/**
 * 个推消息接收器
 */
public class MessageReceiver extends BroadcastReceiver{
    private static final String TAG = MessageReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null){
            return;
        }
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)){
            case PushConsts.GET_CLIENTID :{
                Log.d(TAG,"GET_CLIENTID :" + bundle.toString());
                //当id初始化的时候
                onClientInit(bundle.getString("clientid"));
                break;
            }
            case PushConsts.GET_MSG_DATA:{
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null){
                    String message = new String(payload);
                    Log.d(TAG,"GET_MSG_DATA : " + message);
                    onMessageArrived(message);
                }
                break;
            }
            default:
                Log.d(TAG,"OTHER : " +bundle.toString());
                break;
        }
    }



    /**
     * 当id初始化的时候
     * @param cid 设备id
     */
    private void onClientInit(String cid){
        //设置设备id
        Account.setPushId(cid);
        if (Account.isLogin()){
            //账户登录状态进行一次pushId绑定
            //没有登陆是不能绑定PushId的
            AccountHelper.bindPush(null);
        }
    }

    /**
     * 消息到达时
     * @param message
     */
    private void onMessageArrived(String message) {
        //将消息交给Factory处理
        Factory.dispatchPush(message);
    }
}
