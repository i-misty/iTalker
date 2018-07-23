package com.imist.italker.factory.data.helper;

import com.imist.italker.factory.model.db.AppDatabase;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.BaseModel;

import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.Arrays;


/**
 * 数据库的辅助工具类，
 * 辅助完成增删改 ，这样在更换数据库的时候就不用修改其他类只需要更改实现即可；
 */
public class DbHelper {
    private static final DbHelper instance;

    static {
        instance = new DbHelper();
    }

    public DbHelper() {
    }

    //第一种，直接保存一列
    //user.save();
    //2.ModelAdapter 可以保存集合
    /*FlowManager.getModelAdapter(User.class).save(user);

    //3.事务保存
    DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
                    definition.beginTransactionAsync(new ITransaction() {
        @Override
        public void execute (DatabaseWrapper databaseWrapper){
            FlowManager.getModelAdapter(User.class)
                    .save(user);
        }
    }).build().execute();*/

    /*public static void save(final User... users){
        if (users == null || users.length == 0)
            return;
        //获取数据库的管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        //提交一个事物
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<User> adapter = FlowManager.getModelAdapter(User.class);
                //数组转换集合
                adapter.saveAll(Arrays.asList(users));
            }
        }).build().execute();
    }*/

    /**
     *
     * 统一保存单个和保存多个的问题；统一多种数据类型保存问题，一个方法方便维护
     *
     * 这里是异步的保存操作
     *
     * 限定条件是BaseModel DBFlow 的数据库实体继承的基类，防止传入任意类型的数据报错
     * <p>
     * 新增或者修改的统一方法
     *
     * @param tClass  传递一个class信息
     * @param models  这个class对于的实例的数组
     * @param <Model> 这个实例的泛型，限定条件是BaseModel
     */
    public static <Model extends BaseModel> void save(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0)
            return;
        //获取数据库的管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        //提交一个事物
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                //保存
                adapter.saveAll(Arrays.asList(models));
                //唤起通知
                instance.notifySave(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 进行删除数据库的统一方法
     *
     * @param tClass  传递一个class信息
     * @param models  这个class对于的实例的数组
     * @param <Model> 这个实例的泛型，限定条件是BaseModel
     */
    public static <Model extends BaseModel> void delete(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0)
            return;
        //获取数据库的管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        //提交一个事物
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                //删除
                adapter.deleteAll(Arrays.asList(models));
                //唤起通知
                instance.notifyDelete(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 进行通知调用
     *
     * @param tClass
     * @param models
     * @param <Model>
     */
    private final <Model extends BaseModel> void notifySave(final Class<Model> tClass, final Model... models) {
        //todo
    }


    /**
     * 进行通知调用
     *
     * @param tClass
     * @param models
     * @param <Model>
     */
    private final <Model extends BaseModel> void notifyDelete(final Class<Model> tClass, final Model... models) {
        //todo
    }

}
