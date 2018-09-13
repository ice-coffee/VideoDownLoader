package com.mzp.player.http

import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Retrofit
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.Type


/**
 * author : ice-coffee.

 * date : 2018/9/11.

 * description :
 */
class ToByteConvertFactory : Converter.Factory() {

    companion object {
        private val MEDIA_TYPE = MediaType.parse("application/octet-stream")
        private val TAG = "ToByteConvertFactory"


    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<out Annotation>?, methodAnnotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {

        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        if("byte[]".equals("$type")){
            return Converter<ResponseBody, ByteArray> { value ->
                Log.e(TAG, "convert: Converter<ResponseBody, ?>")
                value.bytes()
            }
        }
        return super.responseBodyConverter(type, annotations, retrofit)
    }
}