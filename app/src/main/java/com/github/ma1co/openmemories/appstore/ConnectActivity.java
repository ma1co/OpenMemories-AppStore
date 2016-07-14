package com.github.ma1co.openmemories.appstore;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

public class ConnectActivity extends BaseActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        textView = (TextView) findViewById(R.id.text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWifiEnabled(true);
    }

    @Override
    public void onWifiStateChanged() {
        WifiState state = getWifiState();
        textView.setText(state.toString());
        if (state == WifiState.CONNECTED) {
            keepWifiOn();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        finish();
    }

    public void onSettingsButtonClicked(View view) {
        String action = Environment.isCamera() ? "com.sony.scalar.app.wifisettings.WifiSettings" : Settings.ACTION_WIFI_SETTINGS;
        keepWifiOn();
        startActivity(new Intent(action));
    }
}
