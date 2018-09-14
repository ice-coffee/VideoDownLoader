package com.mzp.player

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.mzp.player.http.FileCallBack
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

/**
 * author : ice-coffee.

 * date : 2018/9/10.

 * description :
 */
class NewMainActivity : AppCompatActivity() {

    companion object {
        const val url = "http://doubanzyv1.tyswmp.com/2018/07/07/6H3VsaniTfOtTr3R/playlist.m3u8"
    }

    //Android/data/com.android.framework/cache/夺命鲨
    //Android/data/com.android.framework/cache/夺命鲨/****.m3u8
    //Android/data/com.android.framework/cache/夺命鲨/tscache/****.ts
    private lateinit var videoCacheDir: String
    //Android/data/com.android.framework/files/夺命鲨.ts
    private lateinit var videosaveFile: String

    private val videoDirName = "夺命鲨"
    private val videoFileName = "夺命鲨.ts"

    private val permissionArray = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoCacheDir = externalCacheDir.absolutePath + File.separator + videoDirName
        videosaveFile = getExternalFilesDir(videoDirName).absolutePath + File.separator + videoFileName

        btRequest.setOnClickListener( {
            checkPremm()
        } )

        btMerge.setOnClickListener( {
            M3U8Manager.mergeM3u8(videoCacheDir, videosaveFile)
        } )

        jumpTest.setOnClickListener( {
            startActivity(Intent(this, TestActivity::class.java))
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startDownload()
    }

    private fun startDownload() {
        M3U8Manager.downloadM3u8(url, videoCacheDir, object : FileCallBack<MutableList<M3U8Ts>?> {

            override fun onSuccess(o:MutableList<M3U8Ts>?) {
                Log.e("requestcallback_sus", "${o?.size}")
            }

            override fun onError(msg: String?) {
                Log.e("requestcallback_err", "error:$msg")
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