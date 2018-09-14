package com.mzp.player

import android.net.Uri
import android.text.TextUtils
import org.apache.commons.io.IOUtils
import java.io.*

/**
 * author : ice-coffee.

 * date : 2018/9/10.

 * description :
 */
object M3U8Utils {

    fun parseM3u8Url(url: String, inputStream: InputStream): M3U8? {
        try {
            val reader = BufferedReader(InputStreamReader(inputStream))

            val m3u8 = M3U8()

            val basepath = url.substring(0, url.lastIndexOf("/"))

            val m3U8TsList = arrayListOf<M3U8Ts>()
            //读取每一行
            var lineContent = reader.readLine()
            //时长
            var totalTime = 0f
            //单个时长
            var seconds = 0f

            while (!TextUtils.isEmpty(lineContent)) {

                val lineLength = lineContent.length
                if (lineLength < 200) {
                    if (lineContent.endsWith("m3u8")) {
                        //TODO

                    }

                    if (lineContent.startsWith("#EXTINF:")) {
                        lineContent = lineContent.substring(8)
                        if (lineContent.endsWith(",")) {
                            lineContent = lineContent.substring(0, lineContent.length - 1)
                        }
                        seconds = java.lang.Float.parseFloat(lineContent)
                        totalTime += seconds
                    } else if (lineContent.endsWith("ts")) {
                        if (!lineContent.startsWith("http")) {
                            lineContent = basepath + lineContent
                        }
                        val tsName = lineContent.substring(lineContent.lastIndexOf("/") + 1, lineLength)
                        m3U8TsList.add(M3U8Ts(lineContent, tsName, seconds))
                    }
                }

                //读取下一行
                lineContent = reader.readLine()
            }

            reader.close()

            m3u8.name = url.substring(url.lastIndexOf("/") + 1, url.length)
            m3u8.basePath = basepath
            m3u8.totalTime = totalTime
            m3u8.tsList = m3U8TsList

            return m3u8

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 合并ts文件
     */
    fun mergeM3u8ts(tsList: List<File>, saveFile: File) {

        if (saveFile.isDirectory) {
            Throwable("savefile is directory")
        }

        val fos = FileOutputStream(saveFile)
        for (tsFile in tsList) {
            IOUtils.copyLarge(FileInputStream(tsFile), fos)
        }
        fos.close()
    }
}