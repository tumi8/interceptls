package de.tum.in.net.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.TlsTestResult;

public class TlsTestResultViewActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(TlsTestResultViewActivity.class);


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tls_test_result_view);

        final String sessionID = getIntent().getStringExtra("sessionID");
        final TlsTestResult testResult;
        if (sessionID == null) {
            testResult = (TlsTestResult) getIntent().getSerializableExtra("testResult");
        } else {
            testResult = ResultStorage.read(this, sessionID);
        }


        final TextView targets = findViewById(R.id.targets);
        targets.setText(String.valueOf(testResult.getClientServerResults().size()));

        final TextView connections = findViewById(R.id.connections);
        connections.setText(String.valueOf(testResult.successfulConnections()));

        final TextView interceptions = findViewById(R.id.interceptions);
        interceptions.setText(String.valueOf(testResult.interceptions()));

        final TextView content = findViewById(R.id.content);
        content.setText(new Gson().toJson(testResult));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
