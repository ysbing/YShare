package com.ysbing.yshare_base;

import android.support.annotation.Nullable;

/**
 * 内部接口
 */
public interface YShareInnerListener {
    @Nullable
    YShareListener getShareListener();

    void showShareFragment();
}