package com.ysbing.yshare_base;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@Keep
public class YShareConfig implements Parcelable, Cloneable {
    /**
     * 分享标题
     */
    public String shareTitle;
    ;
    /**
     * 分享链接
     */
    public String shareUrl;
    /**
     * 分享描述
     */
    public String shareDes;
    /**
     * 分享的图片地址
     */
    @JsonAdapter(UriAdapter.class)
    public Uri imageUrl;
    /**
     * 分享纯图
     */
    public boolean justImage = false;
    /**
     * 额外数据
     */
    public Bundle data = new Bundle();
    /**
     * 分享渠道
     */
    public ShareChannel shareChannel = ShareChannel.CHANNEL_ALL;

    @Keep
    public enum ShareChannel {
        /**
         * 所有平台，直接弹出分享对话框
         */
        CHANNEL_ALL,
        /**
         * 分享到QQ
         */
        CHANNEL_QQ,
        /**
         * 分享到QQ空间
         */
        CHANNEL_QZONE,
        /**
         * 分享到微信好友
         */
        CHANNEL_WX_FRIENDS,
        /**
         * 分享到微信朋友圈
         */
        CHANNEL_WX_MOMENTS,
        /**
         * 分享到微信小程序
         */
        CHANNEL_WX_PROGRAM,
        /**
         * 分享到微博
         */
        CHANNEL_WEIBO,
        /**
         * 分享到剪贴板
         */
        CHANNEL_CLIPBOARD
    }

    public enum ShareResult {
        /**
         * 分享成功
         */
        SUCCESS,
        /**
         * 取消分享
         */
        CANCEL,
        /**
         * sdk 分享报错
         */
        SDK_ERROR,
        /**
         * auth 分享报错
         */
        AUTH_ERROR,
        /**
         * 未安装当前产品的App
         */
        ARCH_PACKAGE_NOT_FOUND,
        /**
         * 当前类型，图片不能为空
         */
        ARCH_IMAGE_EMPTY,
        /**
         * 不支持的分享类型
         */
        ARCH_UNSUPPORTED_TYPE,
        /**
         * 未知错误
         */
        ARCH_UNKNOWN
    }

    YShareConfig() {
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("shareTitle", shareTitle);
        jsonObject.addProperty("shareUrl", shareUrl);
        jsonObject.addProperty("shareDes", shareDes);
        jsonObject.addProperty("imageUrl", imageUrl.toString());
        return jsonObject.toString();
    }

    public static YShareConfig get() {
        return YShareConstants.getShareConfig();
    }

    private YShareConfig(Parcel in) {
        shareTitle = in.readString();
        shareUrl = in.readString();
        shareDes = in.readString();
        imageUrl = in.readParcelable(Uri.class.getClassLoader());
        justImage = in.readByte() != 0;
        data = in.readBundle();
        shareChannel = (ShareChannel) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shareTitle);
        dest.writeString(shareUrl);
        dest.writeString(shareDes);
        dest.writeParcelable(imageUrl, flags);
        dest.writeByte((byte) (justImage ? 1 : 0));
        dest.writeBundle(data);
        dest.writeSerializable(shareChannel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<YShareConfig> CREATOR = new Creator<YShareConfig>() {
        @Override
        public YShareConfig createFromParcel(Parcel in) {
            return new YShareConfig(in);
        }

        @Override
        public YShareConfig[] newArray(int size) {
            return new YShareConfig[size];
        }
    };

    @Override
    protected Object clone() throws CloneNotSupportedException {
        YShareConfig yShareConfig = (YShareConfig) super.clone();
        yShareConfig.data = (Bundle) data.clone();
        return yShareConfig;
    }

    @Keep
    private class UriAdapter implements JsonDeserializer<Uri> {
        @Override
        public Uri deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return Uri.parse(json.getAsString());
        }
    }
}