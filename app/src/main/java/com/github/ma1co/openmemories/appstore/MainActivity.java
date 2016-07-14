package com.github.ma1co.openmemories.appstore;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TabHost tabHost = TabHost.inflate(this);
        setContentView(tabHost);

        tabHost.setOnBeforeTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tag) {
                ((WifiActivity) tabHost.getCurrentView().getContext()).keepWifiOn();
            }
        });

        addTab("appList", "App list", AppListActivity.class);
    }

    protected void addTab(String tag, String label, Class<? extends WifiActivity> activity) {
        TabHost.TabSpec tab = getTabHost().newTabSpec(tag);
        tab.setIndicator(label);
        tab.setContent(new Intent(this, activity));
        getTabHost().addTab(tab);
    }
}
