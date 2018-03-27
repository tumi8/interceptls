package de.tum.in.net.client;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.tum.in.net.client.ClientWorkflowCallable;
import de.tum.in.net.client.HostAndPort;
import de.tum.in.net.model.TlsTestResult;

/**
 * Created by johannes on 30.06.17.
 */

public class TlsTestTask extends AsyncTask<Void, String, TlsTestResult> {

    private static final Logger log = LoggerFactory.getLogger(TlsTestTask.class);
    private final Context ctx;
    private final AsyncResult<TlsTestResult> listener;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();


    public TlsTestTask(final Context ctx, final AsyncResult<TlsTestResult> listener) {
        this.ctx = Objects.requireNonNull(ctx);
        this.listener = Objects.requireNonNull(listener);
    }

    @Override
    protected TlsTestResult doInBackground(final Void... voids) {
        try {
            final List<HostAndPort> targets = ConfigurationReader.getTargets(ctx);
            final ClientWorkflowCallable c = new ClientWorkflowCallable(targets, new AndroidNetworkIdentifier(ctx));

            return exec.submit(c).get();

        } catch (final Exception e) {
            log.error("Could not finish TLS test", e);
        } finally {
            exec.shutdown();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final TlsTestResult result) {
        super.onPostExecute(result);

        if (result != null && result.anySuccessfulConnection()) {
            ResultStorage.saveTemp(ctx, result);

            final Intent i = new Intent(ctx, UploadResultService.class);
            i.putExtra("testResult", result);
            ctx.startService(i);
        }

        listener.publishResult(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        exec.shutdownNow();
    }
}
