package net.imist.web.italker.push.service;

import net.imist.web.italker.push.bean.api.base.ResponseModel;
import net.imist.web.italker.push.bean.api.message.MessageCreateModel;
import net.imist.web.italker.push.bean.card.MessageCard;

import net.imist.web.italker.push.bean.db.Message;
import net.imist.web.italker.push.bean.db.User;
import net.imist.web.italker.push.factory.MessageFactory;
import net.imist.web.italker.push.factory.PushFactory;
import net.imist.web.italker.push.factory.UserFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * 消息发送的入口
 */
@Path("/msg")
public class MessageService extends BaseService {
    //发送一条消息到服务器
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<MessageCard> pushMessage(MessageCreateModel model){
        if (!MessageCreateModel.check(model)){
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        //查询是否在数据库中已经有了
        Message message = MessageFactory.findById(model.getId());
        if (message != null){
            return ResponseModel.buildOk(new MessageCard(message));
        }
        if (model.getReceiverType() == Message.RECEIVER_TYPE_GROUP){
            return pushToGroup(self,model);
        }else {
            return pushToUser(self,model);
        }


    }

    private ResponseModel<MessageCard> pushToUser(User sender, MessageCreateModel model) {
        User receiver = UserFactory.findById(model.getReceiverId());
        if (receiver == null){
            return ResponseModel.buildNotFoundUserError("can't find receiver user");
        }
        if (receiver.getId().equalsIgnoreCase(sender.getId())){
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        Message message = MessageFactory.add(sender,receiver,model);
        return buildAndPushResponse(sender,message);
    }



    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel model) {
        // todo Group group = GroupFactory.findById;
        return null;
    }

    //推送并构建返回信息
    private ResponseModel<MessageCard> buildAndPushResponse(User sender, Message message) {
        if (message == null){
            //存储数据库失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //进行推送
        PushFactory.pushNewMessage(sender,message);
        return ResponseModel.buildOk(new MessageCard(message));
    }

}
