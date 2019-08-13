## 一、介绍
一个Android轻松集成分享的框架。

## 二、框架特性
* 目前已支持QQ、微信、微博
* 平台选择性接入，避免没用到的平台接入导致体积增大
* 微信分享可以不用复写WXEntryActivity类，框架已经帮你内部处理妥当

## 三、安装
``` gradle
dependencies {
    implementation 'com.ysbing.yshare:yshare:1.0.0'
    implementation 'com.ysbing.yshare:qq:1.0.0'
    implementation 'com.ysbing.yshare:wechat:1.0.0'
    implementation 'com.ysbing.yshare:weibo:1.0.0'
}
```

## 四、快速上手
``` gradle
android {
    defaultConfig {
        ......
        manifestPlaceholders = [
                YSHARE_QQ_APPID         : "123456",//腾讯开放平台注册应用得到的appId
                YSHARE_WECHAT_APPID     : "wxdbf987654321",//微信开放平台注册的appId
                YSHARE_WEIBO_APPKEY      : "123456789",//新浪微博开放平台注册的appKey
                YSHARE_WEIBO_REDIRECT_URL: "https://www.baidu.com/",//新浪微博开放平台第三方应用授权回调页地址，默认为`http://`
                YSHARE_WEIBO_SCOPE       : "email,direct_messages_read,direct_messages_write," +
                        "friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
                        "follow_app_official_microblog,invitation_write"//新浪微博开放平台第三方应用 scope，多个 scope 用逗号分隔
        ]
    }
}
```
配置全局分享参数，也可以对单独分享另外设置
``` java
YShareConfig shareConfig = YShareConfig.get();
shareConfig.shareTitle = "我是标题";
shareConfig.shareUrl = "https://www.baidu.com/";
shareConfig.shareDes = "我是描述";
shareConfig.imageUrl = Uri.parse(
        "https://www.baidu.com/img/bd_logo1.png");
YShare.Companion.setShareConfig(shareConfig);
```
进行分享，详细可以参考实例工程
``` java
YShareConfig shareConfig = YShareConfig.get();
shareConfig.imageUrl = Uri.parse("asset:///share_image.png");
shareConfig.justImage = true;
YShare shareUtil = new YShare(this, shareConfig);
shareUtil.shareToWxFriends();
shareUtil.setShareListener(new YShareListener() {
    @Override
    public void onShare(@NonNull YShareConfig.ShareChannel shareChannel,
                        @NonNull YShareConfig.ShareResult shareResult, @Nullable Bundle data) {
        String log = "shareChannel:" + shareChannel + ",shareResult:" + shareResult;
        Log.i("yshare_info", log);
        Toast.makeText(MainActivity.this, log, Toast.LENGTH_SHORT).show();
    }
});
```
如果你的工程涉及到微信登陆，需要复写WXEntryActivity，然后继承YWXEntryActivity，manifest的写法如下图：
![](https://github.com/ysbing/YShare/wiki/assets/img_hasLoginManifest.png)