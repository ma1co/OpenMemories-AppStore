package com.github.ma1co.openmemories.appstore;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
        queryView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int action, KeyEvent event) {
                if (action == EditorInfo.IME_ACTION_UNSPECIFIED)
                    loadApps(queryView.getText().toString());
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadApps(queryView.getText().toString());
            }
        });

        listViewAdapter = new AppListAdapter(this);
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
        listView.getHandler().post(new Runnable() {
            @Override
            public void run() {
                listView.setSelectionAfterHeaderView();
                for (App app : apps)
                    listViewAdapter.add(app);
            }
        });
    }

    public void showApp(App app) {
        Intent intent = new Intent(this, AppActivity.class);
        intent.putExtra(AppActivity.EXTRA_APP, app);
        keepWifiOn();
        startActivityForResult(intent, 0);
    }
}
