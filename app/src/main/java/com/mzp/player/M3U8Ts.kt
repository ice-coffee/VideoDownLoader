package com.mzp.player

import java.io.File

/**
 * author : ice-coffee.

 * date : 2018/9/10.

 * description :
 */
data class M3U8Ts(var tsUrl: String? = null,
                  var name: String? = null,
                  var tsTime: Float = 0.toFloat(),
                  var file: File? = null)