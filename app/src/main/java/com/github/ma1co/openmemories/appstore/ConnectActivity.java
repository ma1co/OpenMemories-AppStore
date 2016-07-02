package com.github.ma1co.openmemories.appstore;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
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
        try {
            keepWifiOn();
            startActivity(new Intent("com.sony.scalar.app.wifisettings.WifiSettings"));
        } catch (ActivityNotFoundException e) {
            Logger.error("Cannot open wifi settings", e);
            showMessage("Cannot open wifi settings");
        }
    }
}
