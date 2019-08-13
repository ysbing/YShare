package com.ysbing.yshare_base;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ysbing.glint.util.ContextHelper;
import com.ysbing.yshare_base.utils.ParcelableUtil;

import java.io.File;
import java.io.IOException;

import okio.BufferedSource;
import okio.Okio;

/**
 * 分享的常量定义
 *
 * @author ysbing
 * 创建于 2018/6/13
 */
public class YShareConstants {

    public final static String SHARE_ACTION = "share_action";
    public final static String SHARE_TYPE = "share_type";
    public final static String SHARE_RESULT = "share_result";
    public final static String SHARE_DATA = "share_data";

    private final static String PREFS_FILE_NAME = "YShareConfig.data";

    private static YShareConfig sShareConfig;
    private static String sQQAppId;
    private static String sWXAppId;
    private static String sWeiboAppKey;
    private static String sWeiboRedirectUrl;
    private static String sWeiboScope;

    public static void setShareConfig(@NonNull YShareConfig shareConfig) {
        sShareConfig = shareConfig;
        byte[] bytes = ParcelableUtil.marshall(shareConfig);
        File file = new File(ContextHelper.getAppContext().getCacheDir(), PREFS_FILE_NAME);
        try {
            Okio.buffer(Okio.sink(file)).write(bytes).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static YShareConfig getShareConfig() {
        if (sShareConfig == null) {
            File file = new File(ContextHelper.getAppContext().getCacheDir(), PREFS_FILE_NAME);
            try {
                BufferedSource bufferedSource = Okio.buffer(Okio.source(file));
                byte[] bytes = bufferedSource.readByteArray();
                bufferedSource.close();
                sShareConfig = ParcelableUtil.unmarshall(bytes, YShareConfig.CREATOR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (sShareConfig == null) {
            sShareConfig = new YShareConfig();
        }
        try {
            return (YShareConfig) sShareConfig.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return sShareConfig;
    }

    private static String getMetaData(@NonNull String key) {
        try {
            ApplicationInfo appInfo = ContextHelper.getAppContext().getPackageManager()
                    .getApplicationInfo(ContextHelper.getAppContext().getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.get(key).toString();
        } catch (Exception ignored) {
            throw new RuntimeException("Please configure the manifestPlaceholders parameter correctly in Gradle.");
        }
    }

    public static String getQQAppId() {
        if (TextUtils.isEmpty(sQQAppId)) {
            sQQAppId = getMetaData("YSHARE_QQ_APPID");
        }
        return sQQAppId;
    }

    public static String getWXAppId() {
        if (TextUtils.isEmpty(sWXAppId)) {
            sWXAppId = getMetaData("YSHARE_WECHAT_APPID");
        }
        return sWXAppId;
    }

    public static String getWeiboAppKey() {
        if (TextUtils.isEmpty(sWeiboAppKey)) {
            sWeiboAppKey = getMetaData("YSHARE_WEIBO_APPKEY");
        }
        return sWeiboAppKey;
    }

    public static String getWeiboRedirectUrl() {
        if (TextUtils.isEmpty(sWeiboRedirectUrl)) {
            sWeiboRedirectUrl = getMetaData("YSHARE_WEIBO_REDIRECT_URL");
        }
        return sWeiboRedirectUrl;
    }

    public static String getWeiboScope() {
        if (TextUtils.isEmpty(sWeiboScope)) {
            sWeiboScope = getMetaData("YSHARE_WEIBO_SCOPE");
        }
        return sWeiboScope;
    }

    public static String getErrorTip(String module) {
        return String.format("\n*********************** If you need to use it," +
                        "gradle dependencies:\"implementation \'com.ysbing.yshare:%1$s:%2$s\'\"***********************",
                module, BuildConfig.VERSION_NAME);
    }
}