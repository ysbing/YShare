package com.ysbing.yshare_weibo

import android.content.Intent
import android.webkit.ValueCallback
import com.ysbing.yshare_base.BaseYShareInnerListener
import com.ysbing.yshare_base.utils.ImageUriUtil
import com.ysbing.yshare_base.YShareConfig

/**
 * 新浪微博分享的方法接口
 */
interface WeiboShareListener : BaseYShareInnerListener {

    fun shareToWeibo() {
        shareInnerListener.showShareFragment()
        ImageUriUtil.call(activity, shareConfig.imageUrl, YShareConfig.ShareChannel.CHANNEL_WEIBO,
            shareInnerListener, ValueCallback {
            val intent = Intent(activity, YShareWeiboActivity::class.java)
            intent.putExtra(YShareWeiboActivity.KEY_SHARE_CONFIG, shareConfig)
            intent.putExtra(YShareWeiboActivity.KEY_SHARE_LOCAL_IMAGE_PATH, it.absolutePath)
            activity.startActivity(intent)
        })
    }
}
