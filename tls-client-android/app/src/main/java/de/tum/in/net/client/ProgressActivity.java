package de.tum.in.net.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.TlsTestResult;

public class ProgressActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(ProgressActivity.class);
    private TlsTestTask task;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        final TextView textView = findViewById(R.id.progressText);

        log.debug("Start test scenarios");

        final ProgressActivity progressActivity = this;
        task = new TlsTestTask(this, new AsyncResult<TlsTestResult>() {
            @Override
            public void publishResult(final TlsTestResult result) {
                progressActivity.finish();
                if (result != null && result.anySuccessfulConnection()) {
                    final Intent intent = new Intent(progressActivity, TlsTestResultViewActivity.class);
                    intent.putExtra("timestamp", result.getTimestamp());
                    progressActivity.startActivity(intent);
                } else {
                    Toast.makeText(progressActivity, "Connection error", Toast.LENGTH_LONG).show();
                }
            }
        });
        task.execute();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (task != null) {
            task.cancel(true);
        }
    }
}

