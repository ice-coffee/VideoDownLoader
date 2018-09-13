package com.mzp.player.http

import io.reactivex.Observer
import io.reactivex.disposables.Disposable


/**
 * author : ice-coffee.

 * date : 2018/9/10.

 * description :
 */
open class FileObserver<T>(val callback: FileCallBack<T>) : Observer<T> {
    override fun onComplete() {
    }

    override fun onSubscribe(d: Disposable) {
    }

    override fun onNext(t: T) {
        callback.onSuccess(t)
    }

    override fun onError(e: Throwable) {
         callback.onError(e.message)
    }


}
