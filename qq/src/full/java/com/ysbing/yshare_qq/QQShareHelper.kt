package com.ysbing.yshare_qq

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.ysbing.yshare_base.YShareConfig
import com.ysbing.yshare_base.YShareConstants
import java.util.ArrayList

class QQShareHelper internal constructor(
    private val activity: YShareQQActivity
) {
    private val mTencent: Tencent? = Tencent.createInstance(YShareConstants.getQQAppId(), activity)

    /*Qzone分享要在主线程*/
    fun shareToQzone(params: Bundle, listener: IUiListener?) {
        if (mTencent == null || listener == null) {
            return
        }
        val shareType = params.getInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT)
        if (shareType == QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT) {
            Log.d(TAG, "share to Qzone : $shareType")
            mTencent.shareToQzone(activity, params, listener)
        } else {
            Log.d(TAG, "publish to Qzone : $shareType")
            mTencent.publishToQzone(activity, params, listener)
        }
    }

    // QQ 空间分享 文本 图片url
    fun obtainImageTextBundle(title: String, summary: String, targetUrl: String, imageUrls: ArrayList<String>): Bundle {
        val params = Bundle()
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT)
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title)
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary)
        params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, targetUrl)
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl) //增加标识:android 分享的,就是这么拽
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls)
        return params
    }

    //QQ分享要在主线程进行, 需要设置监听
    fun shareToQQ(params: Bundle, listener: IUiListener?) {
        if (mTencent == null || listener == null) {
            return
        }
        mTencent.shareToQQ(activity, params, listener)
    }

    //QQ分享的基本信息,非纯图片的分享(纯图片仅支持本地图片url),请先调用
    fun obtainQQShareBase(
        justImage: Boolean,
        title: String,
        summary: String,
        targetUrl: String,
        imageUrl: String,
        appName: String
    ): Bundle {
        val params = Bundle()
        if (justImage) {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE)
        } else {
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title)
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary)
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl)
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName)
        }
        if (imageUrl.startsWith("http")) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl)
        } else {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl)
        }
        return params
    }

    fun isSupportShare(): Boolean {
        val var1 = activity.packageManager
        val var2: PackageInfo
        return try {
            var2 = var1.getPackageInfo("com.tencent.mobileqq", 0)
            if (compareVersion(var2.versionName, "4.1") >= 0) {
                true
            } else {
                activity.sendShareBroadcast(YShareConfig.ShareResult.ARCH_UNSUPPORTED_TYPE)
                false
            }
        } catch (e: PackageManager.NameNotFoundException) {
            activity.sendShareBroadcast(YShareConfig.ShareResult.ARCH_PACKAGE_NOT_FOUND)
            false
        }
    }

    private fun compareVersion(var0: String?, var1: String?): Int {
        if (var0 == null && var1 == null) {
            return 0
        } else if (var0 != null && var1 == null) {
            return 1
        } else if (var0 == null) {
            return -1
        } else {
            val var2 = var0.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val var3 = var1!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            try {
                var var4 = 0
                while (var4 < var2.size && var4 < var3.size) {
                    val var5 = Integer.parseInt(var2[var4])
                    val var6 = Integer.parseInt(var3[var4])
                    if (var5 < var6) {
                        return -1
                    }

                    if (var5 > var6) {
                        return 1
                    }
                    ++var4
                }

                return if (var2.size > var4) 1 else if (var3.size > var4) -1 else 0
            } catch (var7: NumberFormatException) {
                return var0.compareTo(var1)
            }
        }
    }

    companion object {
        private const val TAG = "QQShareHelper"
    }
}