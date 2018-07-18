package net.imist.web.italker.push.provider;

import com.google.common.base.Strings;
import net.imist.web.italker.push.bean.api.base.ResponseModel;
import net.imist.web.italker.push.bean.db.User;
import net.imist.web.italker.push.factory.UserFactory;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;


/**
 * 用于所有的请求接口的过滤和拦截
 */
@Provider
public class AuthRequestFilter implements ContainerRequestFilter {

    // 实现接口的过滤方法
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String relationPath = ((ContainerRequest) requestContext).getPath(false);
        if (relationPath.startsWith("account/login")
                || relationPath.startsWith("account/register")) {
            return;
        }
        String token = requestContext.getHeaders().getFirst("token");
        if (!Strings.isNullOrEmpty(token)) {

            // 查询自己的信息
            final User self = UserFactory.findByToken(token);
            if (self != null) {
                //给当前请求添加一个上下文
                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        //User 实现 Principal接口
                        return self;
                    }

                    @Override
                    public boolean isUserInRole(String role) {
                        //可以在这里写入用户的权限，role是权限名
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        //默认false即可 HTTPS
                        return false;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        //不用理会
                        return null;
                    }
                });
                return;
            }
        }
        ResponseModel model = ResponseModel.buildAccountError();
        //构建一个返回
        Response response = Response.status(Response.Status.OK)
                .entity(model)
                .build();
        //停止一个请求的继续下发，调用该方法后直接返回请求
        //不会进入到Service
        requestContext.abortWith(response);
    }
}
