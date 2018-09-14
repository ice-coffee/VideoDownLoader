package com.mzp.player

import java.util.ArrayList

/**
 * author : ice-coffee.

 * date : 2018/9/10.

 * description :
 */
data class M3U8(var basePath: String = "",
                var name: String = "",
                var tsList: List<M3U8Ts> = ArrayList(),
                var totalTime: Float = 0.toFloat())