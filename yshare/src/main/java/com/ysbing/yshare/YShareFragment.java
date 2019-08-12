package com.ysbing.yshare;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ysbing.yshare_base.YShareConfig;
import com.ysbing.yshare_base.YShareConstants;
import com.ysbing.yshare_base.YShareListener;

public class YShareFragment extends Fragment {
    static final String TAG = YShareFragment.class.getSimpleName();
    private BroadcastReceiver mShareBroadcastReceiver;
    private FragmentManager mFragmentManager;
    private YShareListener mShareListener;
    private Activity mActivity;
    private boolean isJump;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mFragmentManager = getFragmentManager();
        if (mShareListener != null) {
            mShareBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    dismiss();
                    YShareConfig.ShareChannel shareChannel =
                            (YShareConfig.ShareChannel) intent.getSerializableExtra(YShareConstants.SHARE_TYPE);
                    YShareConfig.ShareResult shareResult =
                            (YShareConfig.ShareResult) intent.getSerializableExtra(YShareConstants.SHARE_RESULT);
                    Bundle data = intent.getBundleExtra(YShareConstants.SHARE_DATA);
                    mShareListener.onShare(shareChannel, shareResult, data);
                }
            };
            mActivity.registerReceiver(mShareBroadcastReceiver, new IntentFilter(YShareConstants.SHARE_ACTION));
        } else {
            dismiss();
        }
        mActivity.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                isJump = !isResumed();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        isJump = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isJump) {
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    dismiss();
                    return false;
                }
            });
        }
    }

    private void dismiss() {
        if (mShareBroadcastReceiver != null) {
            try {
                mActivity.unregisterReceiver(mShareBroadcastReceiver);
                mShareBroadcastReceiver = null;
            } catch (Exception ignored) {
            }
        }
        if (isRemoving()) {
            return;
        }
        mFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
    }

    public void setShareBroadcastReceiver(@NonNull YShareListener shareListener) {
        this.mShareListener = shareListener;
    }
}
