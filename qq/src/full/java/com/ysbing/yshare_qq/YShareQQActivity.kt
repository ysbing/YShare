package com.ysbing.yshare_qq

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle

import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import com.ysbing.yshare_base.YShareConfig
import com.ysbing.yshare_base.YShareConstants

import java.util.ArrayList

/**
 * 用于分享临时建立的Activity,只作用于QQ和QQ空间的分享
 * 其他分享不经过此Activity
 *
 * @author ysbing
 */
class YShareQQActivity : Activity() {

    private val mQQShareListener = ShareListener()
    private var mShareChannel: YShareConfig.ShareChannel? = null
    private var mShareConfig: YShareConfig? = null
    private var mLocalImagePath: String? = null
    private val mShareHelper: QQShareHelper by lazy { QQShareHelper(this) }

    private val applicationName: String
        get() {
            var packageManager: PackageManager? = null
            var applicationInfo: ApplicationInfo? = null
            try {
                packageManager = applicationContext.packageManager
                applicationInfo = packageManager!!.getApplicationInfo(packageName, 0)
            } catch (ignored: PackageManager.NameNotFoundException) {
            }
            return packageManager!!.getApplicationLabel(applicationInfo) as String
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        //分享类型
        mShareChannel = intent.getSerializableExtra(KEY_SHARE_CHANNEL) as YShareConfig.ShareChannel
        mShareConfig = intent.getParcelableExtra(KEY_SHARE_CONFIG)
        mLocalImagePath = intent.getStringExtra(KEY_SHARE_LOCAL_IMAGE_PATH)
        if (mShareChannel == null || mShareConfig == null) {
            finish()
            return
        }
        if (!mShareHelper.isSupportShare()) {
            return
        }
        when (mShareChannel) {
            YShareConfig.ShareChannel.CHANNEL_QQ -> shareToQQ(mShareConfig!!.justImage)
            YShareConfig.ShareChannel.CHANNEL_QZONE -> shareToQzone()
            else -> finish()
        }
    }

    private fun shareToQQ(justImage: Boolean) {
        val params: Bundle = if (justImage) {
            mShareHelper.obtainQQShareBase(true, mShareConfig!!.shareTitle,
                mShareConfig!!.shareDes,
                mShareConfig!!.shareUrl,
                mLocalImagePath!!,
                applicationName)
        } else {
            mShareHelper.obtainQQShareBase(false, mShareConfig!!.shareTitle,
                mShareConfig!!.shareDes,
                mShareConfig!!.shareUrl,
                mShareConfig!!.imageUrl.toString(),
                applicationName)
        }
        mShareHelper.shareToQQ(params, mQQShareListener)
    }

    private fun shareToQzone() {
        val imageUrls = ArrayList<String>()
        mLocalImagePath?.let {
            imageUrls.add(it)
        }
        val params = mShareHelper.obtainImageTextBundle(mShareConfig!!.shareTitle,
            mShareConfig!!.shareDes, mShareConfig!!.shareUrl, imageUrls)
        mShareHelper.shareToQzone(params, mQQShareListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_QQ_SHARE, Constants.REQUEST_QZONE_SHARE -> Tencent.onActivityResultData(requestCode,
                resultCode, data, mQQShareListener)
            else -> {
            }
        }
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    //QQ空间分享的的Listener
    private inner class ShareListener : IUiListener {
        override fun onComplete(o: Any) {
            sendShareBroadcast(YShareConfig.ShareResult.SUCCESS)
        }

        override fun onError(uiError: UiError) {
            if (uiError.errorCode == -4) {
                sendShareBroadcast(YShareConfig.ShareResult.AUTH_ERROR)
            } else {
                sendShareBroadcast(YShareConfig.ShareResult.ARCH_UNKNOWN)
            }
        }

        override fun onCancel() {
            sendShareBroadcast(YShareConfig.ShareResult.CANCEL)
        }
    }

    fun sendShareBroadcast(shareResult: YShareConfig.ShareResult) {
        val intent = Intent(YShareConstants.SHARE_ACTION)
        intent.putExtra(YShareConstants.SHARE_TYPE, mShareChannel)
        intent.putExtra(YShareConstants.SHARE_RESULT, shareResult)
        sendBroadcast(intent)
        finish()
    }

    companion object {
        const val KEY_SHARE_CONFIG = "key_share_config"
        const val KEY_SHARE_CHANNEL = "key_share_channel"
        const val KEY_SHARE_LOCAL_IMAGE_PATH = "key_share_local_image_path"
    }
}
