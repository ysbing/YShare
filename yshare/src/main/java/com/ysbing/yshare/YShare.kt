package com.ysbing.yshare

import android.app.Activity
import android.support.v4.app.FragmentActivity
import com.ysbing.yshare_base.YShareConfig
import com.ysbing.yshare_base.YShareConstants
import com.ysbing.yshare_base.YShareInnerListener
import com.ysbing.yshare_base.YShareListener
import com.ysbing.yshare_qq.QQShareListener
import com.ysbing.yshare_wechat.WechatShareListener
import com.ysbing.yshare_weibo.WeiboShareListener

class YShare(
    override val activity: Activity,
    override val shareConfig: YShareConfig
) : YShareInnerListener,
    WechatShareListener,
    QQShareListener,
    WeiboShareListener {

    override val shareInnerListener: YShareInnerListener = this

    private var mShareListener: YShareListener? = null
    private var mShareFragment: YShareFragment? = null
    private var mSupperShareFragment: YShareFragment_v4? = null
    private var isShowShareFragment: Boolean = false

    fun setShareListener(shareListener: YShareListener) {
        mShareListener = shareListener
        if (activity is FragmentActivity) {
            mSupperShareFragment = YShareFragment_v4()
            mSupperShareFragment?.setShareBroadcastReceiver(shareListener)
        } else {
            mShareFragment = YShareFragment()
            mShareFragment?.setShareBroadcastReceiver(shareListener)
        }
        if (isShowShareFragment) {
            showShareFragment()
        }
    }

    override fun getShareListener(): YShareListener? = mShareListener

    override fun showShareFragment() {
        if (activity is FragmentActivity && mSupperShareFragment != null) {
            activity.supportFragmentManager.beginTransaction().add(mSupperShareFragment!!, YShareFragment_v4.TAG)
                .commitAllowingStateLoss()
        } else if (mShareFragment != null) {
            activity.fragmentManager.beginTransaction().add(mShareFragment, YShareFragment.TAG)
                .commitAllowingStateLoss()
        }
        isShowShareFragment = true
    }

    companion object {
        fun setShareConfig(shareConfig: YShareConfig) {
            YShareConstants.setShareConfig(shareConfig)
        }
    }
}
