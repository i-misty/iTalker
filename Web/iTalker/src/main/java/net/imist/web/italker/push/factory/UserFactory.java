package net.imist.web.italker.push.factory;

import net.imist.web.italker.push.bean.db.User;
import net.imist.web.italker.push.utils.Hib;
import net.imist.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

public class UserFactory {

    public static User  findByPhone(String phone){
        return Hib.query(session -> (User) session.createQuery("from User where phone =:inPhone" )
                 .setParameter("inPhone",phone)
                 .uniqueResult());
    }
    public static User  findByName(String name){
        return Hib.query(session -> (User) session.createQuery("from User where name =:name" )
                 .setParameter("name",name)
                 .uniqueResult());
    }
    /**
     * 用户注册
     * @param account
     * @param password
     * @param name
     * @return User
     */
    public static User register(String account,String password,String name){
        account = account.trim();
        //处理密码
        password = encodePassword(password);
        User user = new User();
        user.setName(name);
        //手机号作为账号
        user.setPhone(account);
        user.setPassword(password);
        //进行数据库操作
        Session session = Hib.session();
        session.beginTransaction();
        try{
            session.save(user);
            session.getTransaction().commit();
            return user;
        }catch (Exception e){
            session.getTransaction().rollback();
            return null;
        }
    }
    private static String encodePassword(String password){
        //密码去除首尾空格
        password = password.trim();
        //对MD5非对称加密，加盐更加安全，盐也要储存
        password = TextUtil.getMD5(password);
        //再进行一次base64加密，当然也可以采取加盐方案
        return TextUtil.encodeBase64(password);
    }
}
