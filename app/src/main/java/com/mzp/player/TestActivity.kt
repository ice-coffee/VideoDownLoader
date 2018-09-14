package com.mzp.player

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import java.io.*


/**
 * author : ice-coffee.

 * date : 2018/9/13.

 * description :
 */
class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val filePath = externalCacheDir.absolutePath + File.separator + "test.txt"
        val file = File(filePath)

        btAdd.setOnClickListener {
            FileIOUtils.writeFileFromString(filePath, etAddContent.text.trim().toString()+"\n", true)
        }

        btDelete.setOnClickListener {
            StorageUtil.removeLineFromFile(filePath, etAddContent.text.trim().toString())
        }

        btRead.setOnClickListener {
            tvContent.text = FileIOUtils.readFile2String(filePath)
        }
    }

}