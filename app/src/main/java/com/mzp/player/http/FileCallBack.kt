package com.mzp.player.http

import io.reactivex.disposables.Disposable

/**
 * author : ice-coffee.

 * date : 2018/9/10.

 * description :
 */
interface FileCallBack<T> {
    fun onSuccess()
    fun onProcess(process: Int){}
    fun onSubscribe(d: Disposable)
    fun onErrorFile(t: T){}
    fun onError(msg: String?)
}