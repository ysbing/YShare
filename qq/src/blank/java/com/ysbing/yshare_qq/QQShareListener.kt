package com.ysbing.yshare_qq

import com.ysbing.yshare_base.BaseYShareInnerListener
import com.ysbing.yshare_base.YShareConstants

/**
 * QQ分享的方法接口
 */
interface QQShareListener : BaseYShareInnerListener {
    fun shareToQQ() {
        throw RuntimeException(YShareConstants.getErrorTip("qq"))
    }

    fun shareToQzone() {
        throw RuntimeException(YShareConstants.getErrorTip("qq"))
    }
}