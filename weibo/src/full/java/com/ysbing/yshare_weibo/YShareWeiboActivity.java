package com.ysbing.yshare_weibo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;
import com.ysbing.yshare_base.utils.BitmapUtils;
import com.ysbing.yshare_base.YShareConfig;
import com.ysbing.yshare_base.YShareConstants;

/**
 * 用于分享临时建立的Activity,只作用于新浪微博的分享
 * 其他分享不经过此Activity
 *
 * @author ysbing
 */
public class YShareWeiboActivity extends Activity implements WbShareCallback {

    public static final String KEY_SHARE_CONFIG = "key_share_config";
    public static final String KEY_SHARE_LOCAL_IMAGE_PATH = "key_share_local_image_path";

    private static final int THUMB_LENGTH_LIMIT = 32768;
    private static final int DATA_LENGTH_LIMIT = 2097152;

    private WbShareHandler mShareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        WeiboInit.init(YShareConstants.getWeiboAppKey(), YShareConstants.getWeiboRedirectUrl(),
                YShareConstants.getWeiboScope());
        YShareConfig shareConfig = getIntent().getParcelableExtra(KEY_SHARE_CONFIG);
        String localImagePath = getIntent().getStringExtra(KEY_SHARE_LOCAL_IMAGE_PATH);
        if (localImagePath == null) {
            finish();
            return;
        }
        mShareHandler = new WbShareHandler(this);
        mShareHandler.registerApp();
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (shareConfig.justImage) {
            weiboMessage.textObject = getTextObj(shareConfig.shareDes);
            weiboMessage.imageObject = getImageObj(BitmapFactory.decodeFile(localImagePath));
        } else {
            weiboMessage.textObject = getTextObj(shareConfig.shareDes);
            weiboMessage.mediaObject =
                    getWebpageObj(shareConfig.shareTitle, shareConfig.shareDes, shareConfig.shareUrl, localImagePath);
        }
        mShareHandler.shareMessage(weiboMessage, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mShareHandler != null) {
            mShareHandler.doResultIntent(intent, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getExtras() == null) {
            onWbShareCancel();
        } else {
            if (mShareHandler != null) {
                mShareHandler.doResultIntent(data, this);
            }
        }
    }

    private TextObject getTextObj(String summary) {
        TextObject textObject = new TextObject();
        textObject.text = summary;
        return textObject;
    }

    private ImageObject getImageObj(Bitmap thumb) {
        ImageObject imageObject = new ImageObject();
        // 设置大图。 注意：最终压缩过的大图大小不得超过 2MB。 DATA_SIZE = 2097152
        imageObject.setImageObject(BitmapUtils.getZoomImage(thumb, DATA_LENGTH_LIMIT));
        return imageObject;
    }

    private WebpageObject getWebpageObj(String shareTitle, String shareSummary, String shareUrl,
                                        String localImagePath) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        // 设置 Bitmap 类型的图片到视频对象里 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        Bitmap thumb = BitmapFactory.decodeFile(localImagePath);
        mediaObject.setThumbImage(BitmapUtils.getZoomImage(thumb, THUMB_LENGTH_LIMIT));
        mediaObject.title = shareTitle;
        mediaObject.description = shareSummary;
        mediaObject.actionUrl = shareUrl;
        mediaObject.defaultText = shareSummary;
        return mediaObject;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onWbShareSuccess() {
        sendShareBroadcast(YShareConfig.ShareResult.SUCCESS);
    }

    @Override
    public void onWbShareCancel() {
        sendShareBroadcast(YShareConfig.ShareResult.CANCEL);
    }

    @Override
    public void onWbShareFail() {
        sendShareBroadcast(YShareConfig.ShareResult.AUTH_ERROR);
    }

    private void sendShareBroadcast(YShareConfig.ShareResult shareResult) {
        Intent intent = new Intent(YShareConstants.SHARE_ACTION);
        intent.putExtra(YShareConstants.SHARE_TYPE, YShareConfig.ShareChannel.CHANNEL_WEIBO);
        intent.putExtra(YShareConstants.SHARE_RESULT, shareResult);
        sendBroadcast(intent);
        finish();
    }
}
