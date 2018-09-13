package com.mzp.player.http

/**
 * author : ice-coffee.

 * date : 2018/9/10.

 * description :
 */
interface FileCallBack<T> {
    fun onSuccess(t: T)
    fun onError(msg: String?)
}