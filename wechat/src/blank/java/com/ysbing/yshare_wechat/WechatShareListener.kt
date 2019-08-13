package com.ysbing.yshare_wechat

import com.ysbing.yshare_base.BaseYShareInnerListener
import com.ysbing.yshare_base.YShareConstants

/**
 * 微信分享的方法接口
 */
interface WechatShareListener : BaseYShareInnerListener {
    fun shareToWxFriends() {
        throw RuntimeException(YShareConstants.getErrorTip("wechat"))
    }

    fun shareToWxMoments() {
        throw RuntimeException(YShareConstants.getErrorTip("wechat"))
    }

    fun shareToWxProgram(wxProgramBean: WxProgramBean) {
        throw RuntimeException(YShareConstants.getErrorTip("wechat"))
    }
}