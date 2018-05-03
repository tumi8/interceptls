package de.tum.in.net.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import de.tum.in.net.model.TlsConstants;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        final TextView general = findViewById(R.id.general);
        general.append(" " + TlsConstants.TLS_INFORMATION_SERVER_URL);

        final TextView versionTextView = findViewById(R.id.app_version);
        versionTextView.setText("Version: " + BuildConfig.VERSION_NAME);

    }
}
