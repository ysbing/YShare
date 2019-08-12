package com.ysbing.yshare_base

import android.app.Activity

/**
 * 内部接口
 */
interface BaseYShareInnerListener {
    val activity: Activity
    val shareInnerListener: YShareInnerListener
    val shareConfig: YShareConfig
}