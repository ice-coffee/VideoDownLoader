package com.mzp.player.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * author : ice-coffee.

 * date : 2018/9/10.

 * description :
 */
class RetrofitHttpClient : HttpClient {

    companion object {
        var retrofit: Retrofit? = null
        var retrofitApi: RetrofitApi? = null
        val instance: HttpClient by lazy { RetrofitHttpClient() }
    }

    override fun init() {

        val mOkHttpClient = OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
                .build()

        retrofit = Retrofit.Builder()
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getBaseHost())
                .build()

        retrofitApi = retrofit?.create(RetrofitApi::class.java)
    }

    fun getBaseHost(): String {
        return "https://www.baidu.com/"
    }

}