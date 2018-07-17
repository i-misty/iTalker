package com.imist.italker.factory.net;

import com.imist.italker.common.Common;
import com.imist.italker.factory.Factory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    public static Retrofit getRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit.Builder builder = new Retrofit.Builder();
        //设置电脑连接
        return builder.baseUrl(Common.Constance.API_URL)
                //设置client
                .client(client)
                //  设置json解析器
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
                .build();
    }

    /**
     * 返回一个请求的代理
     * @return
     */
    public static RemoteService remote(){
        return Network.getRetrofit().create(RemoteService.class);
    }
}
