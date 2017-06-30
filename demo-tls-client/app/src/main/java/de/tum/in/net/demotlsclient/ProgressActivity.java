package de.tum.in.net.demotlsclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.tum.in.net.session.SessionID;

public class ProgressActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(ProgressActivity.class);
    // we could execute the scenarios in parallel later
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        final TextView textView = (TextView) findViewById(R.id.progressText);

        log.debug("Start test scenarios");

        final Context ctx = this;
        final ProgressActivity progressActivity = this;
        final ExecuteSessionTask task = new ExecuteSessionTask(this, textView, new AsyncResult<SessionID>() {
            @Override
            public void publishResult(final SessionID result) {
                progressActivity.finish();
                final Intent intent = new Intent(ctx, SessionViewActivity.class);
                intent.putExtra("sessionID", result.toString());
                ctx.startActivity(intent);
            }
        });
        task.execute();
    }
}

