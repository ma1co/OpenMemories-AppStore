package com.github.ma1co.openmemories.appstore;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AppListAdapter extends ArrayAdapter<App> {
    public AppListAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        App app = getItem(position);
        AppManager.Status status = AppManager.getStatus(getContext().getPackageManager(), app);
        if (view == null)
            view = View.inflate(getContext(), R.layout.item_app_list, null);
        ((TextView) view.findViewById(R.id.name)).setText(app.name);
        ((TextView) view.findViewById(R.id.desc)).setText(app.desc);
        ((TextView) view.findViewById(R.id.rank)).setText(app.rank + " downloads");
        ((TextView) view.findViewById(R.id.install)).setText(status.toString());
        return view;
    }
}
