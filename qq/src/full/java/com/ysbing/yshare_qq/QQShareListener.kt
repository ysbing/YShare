package com.ysbing.yshare_qq

import android.content.Intent
import android.webkit.ValueCallback
import com.ysbing.yshare_base.BaseYShareInnerListener
import com.ysbing.yshare_base.utils.ImageUriUtil
import com.ysbing.yshare_base.YShareConfig

/**
 * QQ分享的方法接口
 */
interface QQShareListener : BaseYShareInnerListener {

    fun shareToQQ() {
        shareInnerListener.showShareFragment()
        if (shareConfig.justImage) {
            shareImageAction(YShareConfig.ShareChannel.CHANNEL_QQ)
        } else {
            shareAction(YShareConfig.ShareChannel.CHANNEL_QQ, null)
        }
    }

    fun shareToQzone() {
        shareInnerListener.showShareFragment()
        if (shareConfig.justImage) {
            shareImageAction(YShareConfig.ShareChannel.CHANNEL_QZONE)
        } else {
            shareAction(YShareConfig.ShareChannel.CHANNEL_QZONE, null)
        }
    }

    private fun shareAction(shareChannel: YShareConfig.ShareChannel, localImagePath: String?) {
        val intent = Intent(activity, YShareQQActivity::class.java)
        intent.putExtra(YShareQQActivity.KEY_SHARE_CONFIG, shareConfig)
        intent.putExtra(YShareQQActivity.KEY_SHARE_CHANNEL, shareChannel)
        intent.putExtra(YShareQQActivity.KEY_SHARE_LOCAL_IMAGE_PATH, localImagePath)
        activity.startActivity(intent)
    }

    private fun shareImageAction(shareChannel: YShareConfig.ShareChannel) {
        ImageUriUtil.call(activity, shareConfig.imageUrl, shareChannel, shareInnerListener, ValueCallback {
            shareAction(shareChannel, it.absolutePath)
        })
    }
}
