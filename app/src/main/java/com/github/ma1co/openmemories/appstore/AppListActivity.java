package com.github.ma1co.openmemories.appstore;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AppListActivity extends WifiActivity {
    private View progressContainer;
    private ListView listView;
    private ArrayAdapter<App> listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        progressContainer = findViewById(R.id.progress_container);
        listView = (ListView) findViewById(R.id.list);

        listViewAdapter = new ArrayAdapter<App>(this, 0) {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                App app = getItem(position);
                AppManager.Status status = AppManager.getStatus(getPackageManager(), app);
                if (view == null)
                    view = View.inflate(getContext(), R.layout.item_app_list, null);
                ((TextView) view.findViewById(R.id.name)).setText(app.name);
                ((TextView) view.findViewById(R.id.desc)).setText(app.desc);
                ((TextView) view.findViewById(R.id.rank)).setText(app.rank + " downloads");
                ((TextView) view.findViewById(R.id.install)).setText(status.toString());
                return view;
            }
        };
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showApp(listViewAdapter.getItem(position));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getWifiState() == WifiState.CONNECTED) {
            if (listViewAdapter.getCount() == 0)
                loadApps();
            else
                listViewAdapter.notifyDataSetChanged();
            sendStats();
        }
    }

    public void loadApps() {
        new AsyncTask<Void, Void, Try<App[]>>() {
            @Override
            protected void onPreExecute() {
                Logger.info("Loading apps");
                progressContainer.setVisibility(View.VISIBLE);
            }

            @Override
            protected Try<App[]> doInBackground(Void... voids) {
                try {
                    return new Try<>(Api.loadApps());
                } catch (Exception e) {
                    return new Try<>(e);
                }
            }

            @Override
            protected void onPostExecute(Try<App[]> result) {
                if (result.isSuccessful()) {
                    setListContent(result.getResult());
                } else {
                    Logger.error("Error loading apps", result.getException());
                    showMessage("Error loading apps");
                }
                progressContainer.setVisibility(View.GONE);
            }
        }.execute();
    }

    public void setListContent(App[] apps) {
        listViewAdapter.clear();
        for (App app : apps)
            listViewAdapter.add(app);
    }

    public void showApp(App app) {
        Intent intent = new Intent(this, AppActivity.class);
        intent.putExtra(AppActivity.EXTRA_APP, app);
        keepWifiOn();
        startActivityForResult(intent, 0);
    }

    public void sendStats() {
        if (Environment.isCamera()) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Logger.info("Sending stats");
                        Api.sendStats(getPackageManager());
                    } catch (Exception e) {
                        Logger.error("Error sending stats", e);
                    }
                    return null;
                }
            }.execute();
        }
    }
}
