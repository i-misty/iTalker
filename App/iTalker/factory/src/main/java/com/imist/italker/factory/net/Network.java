package com.imist.italker.factory.net;

import android.text.TextUtils;

import com.imist.italker.common.Common;
import com.imist.italker.factory.Factory;
import com.imist.italker.factory.persistence.Account;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    private static Network instance;

    private static Retrofit retrofit;

    private OkHttpClient client;

    static {
        instance = new Network();
    }

    private Network() {
    }

    public static OkHttpClient getClient(){
        if (instance.client == null){
            instance.client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            //拿到请求
                            Request original = chain.request();
                            //重新进行build
                            Request.Builder builder = original.newBuilder();
                            if (!TextUtils.isEmpty(Account.getToken())) {
                                builder.addHeader("token", Account.getToken());
                            }
                            //这里可以不写
                            builder.addHeader("Content-Type", "application/json");
                            Request newRequest = builder.build();
                            return chain.proceed(newRequest);
                        }
                    }).build();
        }
        return instance.client;
    }

    public static Retrofit getRetrofit() {
        if (retrofit != null) {
            return retrofit;
        }
        OkHttpClient client = getClient();

        Retrofit.Builder builder = new Retrofit.Builder();
        //设置电脑连接
        retrofit = builder.baseUrl(Common.Constance.API_URL)
                //设置client
                .client(client)
                //  设置json解析器
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
                .build();
        return retrofit;
    }

    /**
     * 返回一个请求的代理
     *
     * @return
     */
    public static RemoteService remote() {
        return Network.getRetrofit().create(RemoteService.class);
    }
}
