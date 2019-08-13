package com.ysbing.yshare_weibo

import com.ysbing.yshare_base.BaseYShareInnerListener
import com.ysbing.yshare_base.YShareConstants

/**
 * 新浪微博分享的方法接口
 */
interface WeiboShareListener : BaseYShareInnerListener {
    fun shareToWeibo() {
        throw RuntimeException(YShareConstants.getErrorTip("weibo"))
    }
}