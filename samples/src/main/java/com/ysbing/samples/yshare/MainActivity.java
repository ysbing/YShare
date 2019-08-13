package com.ysbing.samples.yshare;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ysbing.yshare.YShare;
import com.ysbing.yshare_base.YShareConfig;
import com.ysbing.yshare_base.YShareListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void share(View view) {
        YShareConfig shareConfig = YShareConfig.get();
        shareConfig.imageUrl = Uri.parse("asset:///share_image.png");
        shareConfig.justImage = true;
        YShare shareUtil = new YShare(this, shareConfig);
        shareUtil.shareToQzone();
        shareUtil.setShareListener(new YShareListener() {
            @Override
            public void onShare(@NonNull YShareConfig.ShareChannel shareChannel,
                                @NonNull YShareConfig.ShareResult shareResult, @Nullable Bundle data) {
                String log = "shareChannel:" + shareChannel + ",shareResult:" + shareResult;
                Log.i("yshare_info", log);
                Toast.makeText(MainActivity.this, log, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
