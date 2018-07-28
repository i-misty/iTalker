package net.imist.web.italker.push.utils;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;
import net.imist.web.italker.push.bean.api.base.PushModel;
import net.imist.web.italker.push.bean.db.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PushDispatcher {

    private static final String appId = "Rr51sROK4B8FXbq0TUjAF5";
    private static final String appKey = "eurxTdqHECAKgc7s4xtUe9";
    private static final String masterSecret = "2zqRh5hMIY93LBqVlBtsi";
    private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";
    //要搜到的消息的人和内容的列表
    private List<BatchBean> beans = new ArrayList<>();
    private final IGtPush pusher;


    public PushDispatcher() {
        //最根本的发送者
        pusher = new IGtPush(host, appKey, masterSecret);
    }


    /**
     * 添加一条消息
     * @param receiver 接收者
     * @param model  发送的消息
     * @return
     */
    public boolean add(User receiver, PushModel model) {
        if (receiver == null || model == null ||
                Strings.isNullOrEmpty(receiver.getPushId()))
            return false;
        String pushString = model.getPushString();
        if (Strings.isNullOrEmpty(pushString))
            return false;
        BatchBean bean = buildMessage(receiver.getPushId(), pushString);
        beans.add(bean);
        return true;
    }

    private BatchBean buildMessage(String clientId, String text) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(text);
        template.setTransmissionType(0);//这个类型为Int类型，填写1 则自动启动app;

        SingleMessage message = new SingleMessage();
        message.setData(template); //把透传消息设置到单消息魔板中；
        message.setOffline(true);//是否运行离线发送
        message.setOfflineExpireTime(24 * 3600 * 1000);//离线消息时长

        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);
        //返回一个封装
        return new BatchBean(message, target);
    }

    //进行消息的最终发送；
    public boolean submit() {
        IBatch batch = pusher.getBatch();
        boolean haveData = false;
        for (BatchBean bean : beans) {
            try {
                batch.add(bean.message, bean.target);
                haveData = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!haveData) {
            return false;
        }
        IPushResult result = null;
        try {
            result = batch.submit();
        } catch (IOException e) {
            e.printStackTrace();
            //失败的情况下进行重复发送
            try {
                batch.retry();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (result != null) {
            try {
                Logger.getLogger("PushDispatcher")
                        .log(Level.INFO, (String) result.getResponse().get("result"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Logger.getLogger("PushDispatcher")
                .log(Level.WARNING, "推送服务器相应异常");
        return false;
    }


    //给每个人发送消息的一个bean封装；
    private static class BatchBean {
        SingleMessage message;
        Target target;

        public BatchBean(SingleMessage message, Target target) {
            this.message = message;
            this.target = target;
        }
    }
}
