package com.mzp.player

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.mzp.player.http.FileCallBack
import com.mzp.player.http.FileObserver
import com.mzp.player.http.RetrofitHttpClient
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * author : ice-coffee.

 * date : 2018/9/10.

 * description :
 */
class NewMainActivity : AppCompatActivity() {

    companion object {
        const val url = "http://doubanzyv1.tyswmp.com/2018/07/07/6H3VsaniTfOtTr3R/playlist.m3u8"
    }

    val permissionArray = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btRequest.setOnClickListener( {
            checkPremm()
        } )
        btMerge.setOnClickListener( {
            try {
                val cacheFile = externalCacheDir
                val resultFile = cacheFile.absolutePath + File.separator + "result.ts"

                val files = cacheFile.listFiles()

                val fileList = Arrays.asList<File>(*files!!)
                try {
                    M3U8Utils.mergeM3u8ts(fileList, File(resultFile))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                Log.e("requestcallback2", "${e.message}")
            }
        } )

    }

    private fun checkPremm() {
        val permissionList: ArrayList<String> = isNecessaryPermissionNotGranted(this)
                as ArrayList<String>
        if (permissionList.isNotEmpty()) {
            val permissionsArray = permissionList.toTypedArray()
            ActivityCompat.requestPermissions(this, permissionsArray, 0)
        } else {
            startDownload()
        }
    }

    private fun test(url: String, callBack: FileCallBack<M3U8Ts>) {
        Observable.defer {
                    Observable.just(RetrofitHttpClient.retrofitApi!!.getGoodsDetails(url).execute().body()!!.byteStream())
                }
                .map {
                    inputStream -> M3U8Utils.parseM3u8Url(url, inputStream)
                }
                .flatMap {
                    m3u8:M3U8 -> Observable.fromIterable<M3U8Ts>(m3u8.tsList)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(FileObserver(callBack))
    }

    private fun test1(url: String, callBack: FileCallBack<File?>) {
        Observable.defer {
            Observable.just(RetrofitHttpClient.retrofitApi!!.getGoodsDetails(url).execute().body()!!.byteStream())
        }
                .map {
                    inputStream -> M3U8Utils.parseM3u8Url(url, inputStream)
                }
                .flatMap {
                    m3u8 -> Observable.fromIterable<M3U8Ts>(m3u8.tsList)
                }
                .map {
                    m3u8ts ->
                    val cacheFilePath = externalCacheDir
                    val tsCachePath: String = cacheFilePath.getAbsolutePath() + File.separator + m3u8ts.name
                    val cacheFile = File(tsCachePath)

                    if (cacheFile.exists()) {
                        cacheFile
                    } else {
                        var inputStream: InputStream? = null

                        try {
                            inputStream = RetrofitHttpClient.retrofitApi!!.getGoodsDetails(m3u8ts.tsUrl!!).execute().body()!!.byteStream()
                            return@map StorageUtil.saveFile(inputStream, cacheFile)
                        } catch (e: Exception) {
                            Log.e("requestcallback1", "${e.message}")
                        }
                        File("")
                    }

                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(FileObserver(callBack))
    }

    private fun downloadFile(url: String, callBack: FileCallBack<() -> File>) {
        Observable.defer {
            Observable.just(RetrofitHttpClient.retrofitApi!!.getGoodsDetails(url).execute().body()!!.byteStream())
        }
                .map {
                    inputStream -> M3U8Utils.parseM3u8Url(url, inputStream)
                }
                .flatMap {
                    m3u8 -> Observable.fromIterable<M3U8Ts>(m3u8.tsList)
                }
                .map {
                    m3u8ts -> {
                        val cacheFile = externalCacheDir
                        val inputStream = RetrofitHttpClient.retrofitApi!!.getGoodsDetails(m3u8ts.tsUrl!!).execute().body()!!.byteStream()
                        val tsCachePath = cacheFile.getAbsolutePath() + File.separator + m3u8ts.name

                        StorageUtil.saveFile(inputStream, File(tsCachePath))
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(FileObserver<() -> File>(callBack))

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startDownload()
    }

    private fun startDownload() {
        test1(url, object : FileCallBack<File?> {

            override fun onSuccess(o:File?) {
                Log.e("requestcallback", "${o?.name}")
            }

            override fun onError(msg: String?) {
                Log.e("requestcallback", "error:$msg")
            }
        })
    }

    fun isNecessaryPermissionNotGranted(context: Context): List<String> {
        val permissionList = ArrayList<String>()
        for (permission in permissionArray) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission)
            }
        }
        return permissionList
    }
}