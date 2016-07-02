package com.github.ma1co.openmemories.appstore;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AppActivity extends WifiActivity {
    public static final String EXTRA_APP = "EXTRA_APP";

    private App app;
    private TextView nameView;
    private TextView descView;
    private TextView releaseVersionView;
    private TextView releaseDescView;
    private Button installButton;
    private Button detailsButton;
    private View progressContainer;
    private ProgressBar progressBar;
    private File spkFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("App details");
        setContentView(R.layout.activity_app);
        nameView = (TextView) findViewById(R.id.name);
        descView = (TextView) findViewById(R.id.desc);
        releaseVersionView = (TextView) findViewById(R.id.releaseVersion);
        releaseDescView = (TextView) findViewById(R.id.releaseDesc);
        installButton = (Button) findViewById(R.id.install);
        detailsButton = (Button) findViewById(R.id.details);
        progressContainer = findViewById(R.id.progress_container);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        installButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadAndInstall();
            }
        });

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetails();
            }
        });

        app = (App) getIntent().getSerializableExtra(EXTRA_APP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppManager.Status status = AppManager.getStatus(getPackageManager(), app);

        nameView.setText(app.name);
        descView.setText(app.desc);
        releaseVersionView.setText("Latest release: " + app.releaseVersion);
        releaseDescView.setText(app.releaseDesc);
        installButton.setText(status.toString());
        detailsButton.setText("Details");
        detailsButton.setVisibility(status != AppManager.Status.NOT_INSTALLED ? View.VISIBLE : View.GONE);
    }

    public void downloadAndInstall() {
        new AsyncTask<Void, Integer, Try<File>>() {
            @Override
            protected void onPreExecute() {
                Logger.info("Installing app " + app.id);
                progressContainer.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
            }

            @Override
            protected Try<File> doInBackground(Void... voids) {
                try {
                    File file = new File(Environment.getTempDir(), "openmemories_appstore.1.spk");
                    file.delete();

                    Pair<Long, InputStream> spk = Api.downloadSpk(app.id);
                    long total = spk.first;
                    InputStream is = spk.second;

                    OutputStream os = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    long read = 0;
                    int n;
                    while ((n = is.read(buffer)) != -1) {
                        os.write(buffer, 0, n);
                        read += n;
                        publishProgress((int) read, (int) total);
                    }
                    os.close();

                    file.setReadable(true, false);
                    return new Try<>(file);
                } catch (Exception e) {
                    return new Try<>(e);
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                progressBar.setIndeterminate(false);
                progressBar.setProgress(values[0]);
                progressBar.setMax(values[1]);
            }

            @Override
            protected void onPostExecute(Try<File> result) {
                if (result.isSuccessful()) {
                    install(result.getResult());
                } else {
                    Logger.error("Error downloading app", result.getException());
                    showMessage("Error downloading app");
                }
                progressContainer.setVisibility(View.GONE);
            }
        }.execute();
    }

    public void install(File file) {
        spkFile = file;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        if (!getPackageName().equals(app.id))
            keepWifiOn();
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int request, int result, Intent intent) {
        if (result == RESULT_OK)
            afterInstall(intent.getIntExtra("com.sony.scalar.dlsys.scalarainstaller.ResultCode", 0));
        else if (result >= RESULT_FIRST_USER)
            afterInstall(result);
        else
            afterInstall(-1);
    }

    public void afterInstall(int result) {
        if (result == 0) {
            showMessage("App installed successfully");
        } else {
            String error = "Code " + result + " (" + getInstallerError(result) + ")";
            Logger.error("Error installing app: " + error);
            showMessage("Error installing app: " + error);
        }
        spkFile.delete();
    }

    public String getInstallerError(int result) {
        switch (result) {
            case 0:
                return "Success";
            case -2:
                return "Not enough storage";
            case -3:
                return "File format error";
            case -5:
                return "Battery level too low";
            case -6:
                return "Battery too hot";
            default:
                return "General error";
        }
    }

    public void showDetails() {
        if (!getPackageName().equals(app.id))
            keepWifiOn();
        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + app.id)));
    }
}
