package com.ysbing.yshare_base.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.webkit.ValueCallback
import com.ysbing.glint.download.GlintDownload
import com.ysbing.glint.download.GlintDownloadListener
import com.ysbing.glint.util.Md5Util
import com.ysbing.yshare_base.YShareConfig
import com.ysbing.yshare_base.YShareInnerListener
import okio.Okio
import java.io.File

object ImageUriUtil {

    fun call(
        activity: Activity, _uri: Uri?, shareChannel: YShareConfig.ShareChannel, shareInnerListener: YShareInnerListener?, callback: ValueCallback<File>
    ) {
        fun shareFailed() {
            activity.window.decorView.post {
                shareInnerListener?.shareListener?.onShare(shareChannel,
                    YShareConfig.ShareResult.ARCH_IMAGE_EMPTY, null)
            }
        }

        var uri = _uri
        try {
            if (uri == null) {
                shareFailed()
            }
            if (uri?.scheme == null) {
                uri = Uri.fromFile(File(uri.toString()))
            }
            when (uri?.scheme) {
                "http", "https" -> httpImage(activity, uri.toString(),
                    callback)
                "file" -> fileImage(File(uri.path), callback)
                "asset" -> assetImage(activity, uri.lastPathSegment!!,
                    callback)
                "res" -> resImage(activity, uri.lastPathSegment!!, callback)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            shareFailed()
        }
    }

    private fun httpImage(context: Context, imageUrl: String, callback: ValueCallback<File>) {
        val saveFile = File(context.externalCacheDir, Md5Util.getMD5Str(imageUrl))
        GlintDownload.download(imageUrl, saveFile).execute(object : GlintDownloadListener() {
            override fun onPrepared(fileName: String, contentLength: Long): Boolean {
                if (saveFile.length() == contentLength) {
                    try {
                        onSuccess(saveFile)
                    } catch (e: Throwable) {
                        onFail(e)
                    }
                    return true
                }
                return super.onPrepared(fileName, contentLength)
            }

            override fun onSuccess(result: File) {
                super.onSuccess(result)
                callback.onReceiveValue(result)
            }
        })
    }

    private fun fileImage(imageFile: File, callback: ValueCallback<File>) {
        callback.onReceiveValue(imageFile)
    }

    private fun assetImage(context: Context, assetFile: String, callback: ValueCallback<File>) {
        val saveFile = File(context.externalCacheDir, Md5Util.getMD5Str(assetFile))
        val inputStream = context.assets.open(assetFile)
        Okio.buffer(Okio.sink(saveFile)).writeAll(Okio.source(inputStream))
        inputStream.close()
        callback.onReceiveValue(saveFile)
    }

    private fun resImage(context: Context, resName: String, callback: ValueCallback<File>) {
        val saveFile = File(context.externalCacheDir, Md5Util.getMD5Str(resName))
        val imageResource = context.resources.getIdentifier(resName, "raw", context.packageName)
        if (imageResource > 0) {
            val inputStream = context.resources.openRawResource(imageResource)
            Okio.buffer(Okio.sink(saveFile)).writeAll(Okio.source(inputStream))
            inputStream.close()
            callback.onReceiveValue(saveFile)
        }
    }
}
