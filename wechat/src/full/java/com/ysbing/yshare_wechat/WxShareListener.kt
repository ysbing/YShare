package com.ysbing.yshare_wechat

import android.graphics.BitmapFactory
import android.webkit.ValueCallback
import com.ysbing.yshare_base.BaseYShareInnerListener
import com.ysbing.yshare_base.utils.BitmapUtils
import com.ysbing.yshare_base.utils.ImageUriUtil
import com.ysbing.yshare_base.YShareConfig
import java.io.File

/**
 * 微信分享的方法接口
 */
interface WxShareListener : BaseYShareInnerListener {

    fun shareToWxFriends() {
        shareInnerListener.showShareFragment()
        if (shareConfig.justImage) {
            shareImageAction(YShareConfig.ShareChannel.CHANNEL_WX_FRIENDS)
        } else {
            shareAction(YShareConfig.ShareChannel.CHANNEL_WX_FRIENDS)
        }
    }

    fun shareToWxMoments() {
        shareInnerListener.showShareFragment()
        if (shareConfig.justImage) {
            shareImageAction(YShareConfig.ShareChannel.CHANNEL_WX_MOMENTS)
        } else {
            shareAction(YShareConfig.ShareChannel.CHANNEL_WX_MOMENTS)
        }
    }

    fun shareToWxProgram(wxProgramBean: WxProgramBean) {
        shareInnerListener.showShareFragment()
        shareAction(YShareConfig.ShareChannel.CHANNEL_WX_PROGRAM, wxProgramBean)
    }

    private fun shareAction(shareChannel: YShareConfig.ShareChannel, wxProgramBean: WxProgramBean? = null) {
        ImageUriUtil.call(activity, shareConfig.imageUrl, shareChannel, shareInnerListener, ValueCallback { file ->
            val helper = WxApiHelper.getInstance(activity, shareInnerListener)
            when (shareChannel) {
                YShareConfig.ShareChannel.CHANNEL_WX_FRIENDS -> {
                    val bitmap = BitmapUtils.getZoomImage(BitmapFactory.decodeFile(file.absolutePath),
                        WxApiHelper.THUMB_LENGTH_LIMIT.toDouble())
                    val message = helper.obtainWebpageMessage(shareConfig.shareUrl,
                        shareConfig.shareTitle, shareConfig.shareDes, bitmap)
                    helper.share2Friends(message, WxApiHelper.getShareTransaction(WxConstants.WXFR_TRAN_SHOW))
                }
                YShareConfig.ShareChannel.CHANNEL_WX_MOMENTS -> {
                    val bitmap = BitmapUtils.getZoomImage(BitmapFactory.decodeFile(file.absolutePath),
                        WxApiHelper.THUMB_LENGTH_LIMIT.toDouble())
                    val message =
                        helper.obtainWebpageMessage(shareConfig.shareUrl, shareConfig.shareDes, shareConfig.shareDes,
                            bitmap)
                    helper.share2Moments(message,
                        WxApiHelper.getShareTransaction(WxConstants.WXMM_TRAN_SHOW))
                }
                YShareConfig.ShareChannel.CHANNEL_WX_PROGRAM -> {
                    wxProgramBean?.let {
                        val bitmap = BitmapUtils.getZoomImage(BitmapFactory.decodeFile(file.absolutePath),
                            WxApiHelper.THUMB_LENGTH_LIMIT_PROGRAM.toDouble())
                        val message = helper.obtainProgramMessage(shareConfig.shareDes,
                            shareConfig.shareDes, bitmap, it)
                        helper.share2Program(message,
                            WxApiHelper.getShareTransaction(WxConstants.WXFR_PROGRAM_TRAN_SHOW))
                    }
                }
                else -> {
                }
            }
        })
    }

    private fun shareImageAction(shareChannel: YShareConfig.ShareChannel) {
        ImageUriUtil.call(activity, shareConfig.imageUrl, shareChannel, shareInnerListener, ValueCallback {
            shareImage(shareChannel, it)
        })
    }

    private fun shareImage(shareChannel: YShareConfig.ShareChannel, imageFile: File) {
        val helper = WxApiHelper.getInstance(activity, shareInnerListener)
        when (shareChannel) {
            YShareConfig.ShareChannel.CHANNEL_WX_FRIENDS -> {
                val message = helper.obtainImageMessageLocal(imageFile.absolutePath, null)
                helper.share2Friends(message, WxApiHelper.getShareTransaction(WxConstants.WXFR_TRAN_SHOW))
            }
            YShareConfig.ShareChannel.CHANNEL_WX_MOMENTS -> {
                val message = helper.obtainImageMessageLocal(imageFile.absolutePath, null)
                helper.share2Moments(message, WxApiHelper.getShareTransaction(WxConstants.WXMM_TRAN_SHOW))
            }
            else -> {
            }
        }
    }
}