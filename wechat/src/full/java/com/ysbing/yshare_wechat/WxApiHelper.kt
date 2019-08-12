package com.ysbing.yshare_wechat

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.ysbing.yshare_base.utils.BitmapUtils
import com.ysbing.yshare_base.YShareConfig
import com.ysbing.yshare_base.YShareConstants
import com.ysbing.yshare_base.YShareInnerListener
import java.io.File

class WxApiHelper private constructor(
    private val activity: Activity, private val shareInnerListener: YShareInnerListener?
) : Handler.Callback {
    private val mApi: IWXAPI

    private val appID: String
        get() = YShareConstants.getWXAppId()

    private val isSupportShareToWxMoments: Boolean
        get() {
            return if (mApi.wxAppSupportAPI >= 0x21020001) {
                true
            } else {
                activity.window.decorView.post {
                    shareInnerListener?.shareListener?.onShare(YShareConfig.ShareChannel.CHANNEL_WX_MOMENTS,
                        YShareConfig.ShareResult.ARCH_UNSUPPORTED_TYPE, null)
                }
                false
            }
        }

    init {
        mApi = WXAPIFactory.createWXAPI(activity, appID, true)
        mApi.registerApp(appID)
    }

    /**
     * 分享的Activity需要清单文件设置 android:exported="true" 需要回调方法可实现IWXAPIEventHandler接口
     */
    private fun share(msg: WXMediaMessage?, scene: Int, transaction: String) {
        //微信share不需要授权登录
        // 构造一个Req
        val req = SendMessageToWX.Req()
        req.transaction = transaction // transaction字段用于唯一标识一个请求
        req.message = msg
        req.scene = scene
        // 调用api接口发送数据到微信
        mApi.sendReq(req)
    }

    /**
     * @param transaction 唯一标识一个请求
     */
    fun share2Friends(msg: WXMediaMessage?, transaction: String) {
        if (isWXAppInstalled(YShareConfig.ShareChannel.CHANNEL_WX_FRIENDS)) {
            share(msg, SendMessageToWX.Req.WXSceneSession, transaction)
        }
    }

    /**
     * @param transaction 唯一标识一个请求
     */
    fun share2Moments(msg: WXMediaMessage?, transaction: String) {
        if (isWXAppInstalled(YShareConfig.ShareChannel.CHANNEL_WX_MOMENTS) && isSupportShareToWxMoments) {
            share(msg, SendMessageToWX.Req.WXSceneTimeline, transaction)
        }
    }

    /**
     * @param transaction 唯一标识一个请求
     */
    fun share2Program(msg: WXMediaMessage, transaction: String) {
        if (isWXAppInstalled(YShareConfig.ShareChannel.CHANNEL_WX_PROGRAM)) {
            val req = SendMessageToWX.Req()
            req.transaction = transaction
            req.message = msg
            req.scene = SendMessageToWX.Req.WXSceneSession  // 目前支持会话
            mApi.sendReq(req)
        }
    }

    private fun isWXAppInstalled(shareChannel: YShareConfig.ShareChannel): Boolean {
        return if (mApi.isWXAppInstalled) {
            true
        } else {
            activity.window.decorView.post {
                shareInnerListener?.shareListener?.onShare(shareChannel,
                    YShareConfig.ShareResult.ARCH_PACKAGE_NOT_FOUND, null)
            }
            false
        }
    }

    fun obtainWebpageMessage(webUrl: String, title: String, description: String, thumb: Bitmap?): WXMediaMessage {
        val webpage = WXWebpageObject()
        webpage.webpageUrl = webUrl
        val msg = WXMediaMessage(webpage)
        msg.title = title
        msg.description = description
        if (thumb != null) {
            msg.thumbData = BitmapUtils.bmpToByteArray(thumb)
        }
        return msg
    }

    fun obtainProgramMessage(
        title: String, description: String, thumb: Bitmap?,
        wxProgramBean: WxProgramBean
    ): WXMediaMessage {
        val msg = WXMediaMessage(getProgramObject(wxProgramBean))
        msg.title = title
        msg.description = description
        if (thumb != null) {
            msg.setThumbImage(thumb)
        }
        return msg
    }

    private fun getProgramObject(wxProgramBean: WxProgramBean): WXMiniProgramObject {
        val miniProgramObj = WXMiniProgramObject()
        miniProgramObj.userName = wxProgramBean.programId     // 小程序原始id
        miniProgramObj.webpageUrl = wxProgramBean.webpageUrl // 兼容低版本的网页链接
        miniProgramObj.miniprogramType = wxProgramBean.miniprogramType// 正式版:0，测试版:1，体验版:2
        miniProgramObj.path = wxProgramBean.path            //小程序页面路径
        return miniProgramObj
    }

    fun obtainImageMessageLocal(path: String, bitmap: Bitmap?): WXMediaMessage? {
        val file = File(path)
        if (!file.exists()) {
            return null
        }
        val imgObj = WXImageObject()
        imgObj.setImagePath(path)

        val msg = WXMediaMessage()
        msg.mediaObject = imgObj

        val bmp: Bitmap = bitmap ?: BitmapFactory.decodeFile(path) ?: return null
        msg.thumbData = BitmapUtils.bmpToByteArray(
            BitmapUtils.getZoomImage(bmp, THUMB_LENGTH_LIMIT.toDouble()))
        return msg
    }

    override fun handleMessage(msg: Message): Boolean {
        return false
    }

    companion object {

        const val THUMB_LENGTH_LIMIT = 32//分享的图片大小限制 32K
        const val THUMB_LENGTH_LIMIT_PROGRAM = 128//分享的图片大小限制 32K

        fun getInstance(activity: Activity, shareInnerListener: YShareInnerListener?): WxApiHelper {
            return WxApiHelper(activity, shareInnerListener)
        }

        /**
         * 获取 微信 分享的 Transaction 用于标识 哪种分享, 或者 决定展示位置
         *
         * @param wxType
         * @return
         */
        fun getShareTransaction(wxType: String): String {
            return wxType + System.currentTimeMillis()
        }
    }
}
