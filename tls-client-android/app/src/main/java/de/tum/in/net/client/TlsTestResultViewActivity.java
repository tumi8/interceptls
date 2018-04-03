package de.tum.in.net.client;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_tls_test_result_view);

        final String timestamp = getIntent().getStringExtra("timestamp");
        final TlsTestResult testResult = new TlsDB(this).read(timestamp);

        if(testResult!=null){
            final TextView targets = findViewById(R.id.targets);
            targets.setText(String.valueOf(testResult.getClientServerResults().size()));

            final TextView connections = findViewById(R.id.connections);
            connections.setText(String.valueOf(testResult.successfulConnections()));

            final TextView interceptions = findViewById(R.id.interceptions);
            interceptions.setText(String.valueOf(testResult.interceptions()));

            final TextView content = findViewById(R.id.content);
            content.setText(new Gson().toJson(testResult));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
