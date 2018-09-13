package com.mzp.player.http

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * author : ice-coffee.

 * date : 2018/9/10.

 * description :
 */
interface RetrofitApi {

    //首页合作伙伴
    @GET()
    fun getGoodsDetails(@Url url: String): Call<ResponseBody>
}