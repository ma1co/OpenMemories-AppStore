package com.github.ma1co.openmemories.appstore;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class ExternalStoreActivity extends WifiActivity {
    public final int numResults = 20;

    private EditText queryView;
    private ImageButton searchButton;
    private View progressContainer;
    private ListView listView;
    private ArrayAdapter<App> listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_store);
        queryView = (EditText) findViewById(R.id.query);
        searchButton = (ImageButton) findViewById(R.id.search);
        progressContainer = findViewById(R.id.progress_container);
        listView = (ListView) findViewById(R.id.list);

        queryView.setHint("Search " + ExternalStoreApi.name + "...");
        queryView.setOnEditorActionListener((view, action, event) -> {
            if (action == EditorInfo.IME_ACTION_UNSPECIFIED)
                loadApps(queryView.getText().toString());
            return false;
        });

        searchButton.setOnClickListener(view -> loadApps(queryView.getText().toString()));

        listViewAdapter = new AppListAdapter(this);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener((adapterView, view, position, id) -> showApp(listViewAdapter.getItem(position)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        listViewAdapter.notifyDataSetChanged();
    }

    public void loadApps(final String query) {
        new AsyncTask<String, Void, Try<App[]>>() {
            @Override
            protected void onPreExecute() {
                Logger.info("Loading external apps");
                searchButton.setEnabled(false);
                progressContainer.setVisibility(View.VISIBLE);
            }

            @Override
            protected Try<App[]> doInBackground(String... args) {
                try {
                    return new Try<>(ExternalStoreApi.findApps(args[0], numResults));
                } catch (Exception e) {
                    return new Try<>(e);
                }
            }

            @Override
            protected void onPostExecute(Try<App[]> result) {
                if (result.isSuccessful()) {
                    setListContent(result.getResult());
                } else {
                    Logger.error("Error loading external apps", result.getException());
                    showMessage("Error loading apps");
                }
                searchButton.setEnabled(true);
                progressContainer.setVisibility(View.GONE);
            }
        }.execute(query);
    }

    public void setListContent(final App[] apps) {
        listViewAdapter.clear();
        listView.getHandler().post(() -> {
            listView.setSelectionAfterHeaderView();
            for (App app : apps)
                listViewAdapter.add(app);
        });
    }

    public void showApp(App app) {
        Intent intent = new Intent(this, AppActivity.class);
        intent.putExtra(AppActivity.EXTRA_APP, app);
        keepWifiOn();
        startActivityForResult(intent, 0);
    }
}
