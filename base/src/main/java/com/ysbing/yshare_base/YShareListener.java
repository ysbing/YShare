package com.ysbing.yshare_base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 分享监听接口
 */
public interface YShareListener {
    /**
     * @param shareChannel 分享的通道
     * @param shareResult  分享结果码，详看{@link YShareConfig.ShareResult}
     * @param data         附带数据
     */
    void onShare(@NonNull YShareConfig.ShareChannel shareChannel, @NonNull YShareConfig.ShareResult shareResult,
                 @Nullable Bundle data);
}