package com.ysbing.yshare_wechat;

import android.support.annotation.Keep;

@Keep
public class WxProgramBean {
    /**
     * 小程序原始id
     */
    public String programId;
    /**
     * 兼容低版本的网页链接
     */
    public String webpageUrl;
    /**
     * 正式版:0，测试版:1，体验版:2
     */
    public int miniprogramType;
    /**
     * 小程序页面路径
     */
    public String path;
}
