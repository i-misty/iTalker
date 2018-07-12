package net.imist.web.italker.push;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.imist.web.italker.push.provider.GsonProvider;
import net.imist.web.italker.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

/**
 @author iMist
 */
public class Application extends ResourceConfig {
    public Application() {
        //注册逻辑处理的包名 推荐类名获取更加灵活，防止移动之后无法找到
        //packages("net.imist.web.italker.push.service");
        packages(AccountService.class.getPackage().getName());
        //注册json解析器
        //register(JacksonJsonProvider.class);
        register(GsonProvider.class);
        //注册日志打印输出
        register(Logger.class);
    }
}
