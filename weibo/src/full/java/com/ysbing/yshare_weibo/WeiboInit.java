package com.ysbing.yshare_weibo;

import android.support.annotation.NonNull;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.ysbing.glint.util.ContextHelper;

class WeiboInit {
    static void init(@NonNull String appKey, @NonNull String redirectUrl, @NonNull String scope) {
        AuthInfo authInfo = new AuthInfo(ContextHelper.getAppContext(), appKey, redirectUrl, scope);
        WbSdk.install(ContextHelper.getAppContext(), authInfo);
    }
}
