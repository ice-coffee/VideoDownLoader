package com.mzp.player

import android.app.Application
import com.mzp.player.http.RetrofitHttpClient

/**
 * author : ice-coffee.

 * date : 2018/9/11.

 * description :
 */
class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitHttpClient.instance.init()
    }
}