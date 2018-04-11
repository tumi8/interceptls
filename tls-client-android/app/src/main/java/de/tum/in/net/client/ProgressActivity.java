package de.tum.in.net.client;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(ProgressActivity.class);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_progress);

        final ProgressActivity progressActivity = this;

        final Intent i = new Intent(this, TlsService.class);
        i.putExtra("force", true);
        i.putExtra("resultReceiver", new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(final int resultCode, final Bundle resultData) {
                progressActivity.finish();

                if (resultCode == 0) {
                    final Intent intent = new Intent(progressActivity, TlsTestResultViewActivity.class);
                    intent.putExtra("timestamp", resultData.getString("timestamp"));
                    progressActivity.startActivity(intent);
                } else {
                    Toast.makeText(progressActivity, "Connection error", Toast.LENGTH_LONG).show();
                }
            }
        });
        this.startService(i);

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
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

