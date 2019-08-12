package com.ysbing.samples.yshare;

import android.app.Application;
import android.net.Uri;

import com.ysbing.yshare.YShare;
import com.ysbing.yshare_base.YShareConfig;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        YShareConfig shareConfig = YShareConfig.get();
        shareConfig.shareTitle = "我是标题";
        shareConfig.shareUrl = "https://www.baidu.com/";
        shareConfig.shareDes = "我是描述";
        shareConfig.imageUrl = Uri.parse(
                "https://www.baidu.com/img/bd_logo1.png");
        YShare.Companion.setShareConfig(shareConfig);
    }
}
