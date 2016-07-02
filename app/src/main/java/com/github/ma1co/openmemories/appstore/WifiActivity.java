package com.github.ma1co.openmemories.appstore;

import android.content.Intent;

public class WifiActivity extends BaseActivity {
    @Override
    public void onWifiStateChanged() {
        if (getWifiState() != WifiState.CONNECTED) {
            keepWifiOn();
            startActivity(new Intent(this, ConnectActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (getCallingActivity() != null)
            keepWifiOn();
        super.onBackPressed();
    }
}
