package com.ysbing.yshare_wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.ysbing.yshare_base.YShareConfig;
import com.ysbing.yshare_base.YShareConstants;

/**
 * 微信的分享和登录都会来到这里，这个类只处理分享，不会处理登录的逻辑，所以开发者需继续完善登录的逻辑
 */
public class YWXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI mWxApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWxApi = WXAPIFactory.createWXAPI(this, YShareConstants.getWXAppId(), false);
        mWxApi.registerApp(YShareConstants.getWXAppId());
        mWxApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWxApi.handleIntent(intent, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        try {
            if (baseResp == null) {
                return;
            }
            int type = baseResp.getType();
            //发送信息到微信. 暂时用于分享
            if (type == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
                if (baseResp.transaction == null) {
                    return;
                }
                YShareConfig.ShareResult shareResult;
                YShareConfig.ShareChannel shareChannel = null;
                if (baseResp.transaction.startsWith(WxConstants.WXFR_TRAN_SHOW)) {
                    // 微信好友
                    shareChannel = YShareConfig.ShareChannel.CHANNEL_WX_FRIENDS;
                } else if (baseResp.transaction.startsWith(WxConstants.WXMM_TRAN_SHOW)) {
                    // 微信朋友圈
                    shareChannel = YShareConfig.ShareChannel.CHANNEL_WX_MOMENTS;
                } else if (baseResp.transaction.startsWith(WxConstants.WXFR_PROGRAM_TRAN_SHOW)) {
                    // 微信小程序
                    shareChannel = YShareConfig.ShareChannel.CHANNEL_WX_PROGRAM;
                }
                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        shareResult = YShareConfig.ShareResult.SUCCESS;
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        shareResult = YShareConfig.ShareResult.CANCEL;
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        shareResult = YShareConfig.ShareResult.AUTH_ERROR;
                        break;
                    case BaseResp.ErrCode.ERR_SENT_FAILED:
                        shareResult = YShareConfig.ShareResult.SDK_ERROR;
                        break;
                    case BaseResp.ErrCode.ERR_UNSUPPORT:
                        shareResult = YShareConfig.ShareResult.ARCH_UNSUPPORTED_TYPE;
                        break;
                    default:
                        shareResult = YShareConfig.ShareResult.ARCH_UNKNOWN;
                        break;
                }

                Bundle data = new Bundle();
                data.putString("openId", baseResp.openId);
                Intent intent = new Intent(YShareConstants.SHARE_ACTION);
                intent.putExtra(YShareConstants.SHARE_TYPE, shareChannel);
                intent.putExtra(YShareConstants.SHARE_RESULT, shareResult);
                intent.putExtra(YShareConstants.SHARE_DATA, data);
                sendBroadcast(intent);
            }
        } finally {
            if (getClass().getName().equals(YWXEntryActivity.class.getName())) {
                finish();
            }
        }
    }
}

