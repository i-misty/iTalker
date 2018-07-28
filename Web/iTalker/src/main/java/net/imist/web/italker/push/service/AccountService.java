package net.imist.web.italker.push.service;

import com.google.common.base.Strings;
import net.imist.web.italker.push.bean.api.account.AccountRspModel;
import net.imist.web.italker.push.bean.api.account.LoginModel;
import net.imist.web.italker.push.bean.api.account.RegisterModel;
import net.imist.web.italker.push.bean.api.base.ResponseModel;
import net.imist.web.italker.push.bean.db.User;
import net.imist.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author iMist
 * //net.imist.web.italker.push.service 搜索处理的报名  api下的所有
 * //127.0.0.1/api/account/...
 * Google 插件库可以用于调试接口
 */

@Path("/account")
public class AccountService extends BaseService {


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求传入json
    @Produces(MediaType.APPLICATION_JSON)  //返回 json
    public ResponseModel<AccountRspModel> login(LoginModel model) {
        if (!LoginModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        User user = UserFactory.login(model.getAccount(), model.getPassword());
        if (user != null) {
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                return bind(user, model.getPushId());
            }
            AccountRspModel rspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        } else {
            return ResponseModel.buildLoginError();
        }
    }


    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求传入json
    @Produces(MediaType.APPLICATION_JSON)  //返回 json
    public ResponseModel<AccountRspModel> register(RegisterModel model) {
        if (!RegisterModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        User user = UserFactory.findByPhone(model.getAccount().trim());
        if (user != null) {
            return ResponseModel.buildHaveAccountError();
        }
        user = UserFactory.findByName(model.getName().trim());
        if (user != null) {
            // 已有用户名
            return ResponseModel.buildHaveNameError();
        }
        user = UserFactory.register(model.getAccount(),
                model.getPassword(),
                model.getName());
        if (user != null) {
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                return bind(user, model.getPushId());
            }
            AccountRspModel rspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        } else {
            return ResponseModel.buildRegisterError();
        }
    }

    //绑定设备id
    @POST
    @Path("/bind/{pushId}")
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求传入json
    @Produces(MediaType.APPLICATION_JSON)  //返回 json
    public ResponseModel<AccountRspModel> bind(@HeaderParam("token") String token, @PathParam("pushId") String pushId) {
        if (Strings.isNullOrEmpty(token) || Strings.isNullOrEmpty(pushId)) {
            return ResponseModel.buildParameterError();
        }
        //User user = UserFactory.findByToken(token);
        //通过token拿到个人信息
        User user = getSelf();
        return bind(user, pushId);
    }

    /**
     * 绑定的操作
     *
     * @param self
     * @param pushId
     * @return
     */
    private ResponseModel<AccountRspModel> bind(User self, String pushId) {

        //进行设备id的绑定操作
        User user = UserFactory.bindPushId(self, pushId);
        //绑定失败可能为null
        if (user == null) {
            //绑定失败服务器异常
            return ResponseModel.buildServiceError();
        }
        //返回当前账户,并且已经绑定了
        AccountRspModel rspModel = new AccountRspModel(user, true);
        return ResponseModel.buildOk(rspModel);
    }
}
