package de.tum.in.net.client;

import android.os.AsyncTask;

import de.tum.in.net.model.Scenario;
import de.tum.in.net.model.TlsClientServerResult;

/**
 * Created by johannes on 22.03.17.
 */

public class AsyncTaskPublisher extends AsyncTask<Object, Object, TlsClientServerResult> {

    private final Scenario scenario;
    private final AsyncResult<TlsClientServerResult> delegate;

    public AsyncTaskPublisher(final Scenario scenario, final AsyncResult<TlsClientServerResult> delegate) {
        this.scenario = scenario;
        this.delegate = delegate;
    }

    @Override
    protected TlsClientServerResult doInBackground(final Object... voids) {
        return scenario.call();
    }

    @Override
    protected void onPostExecute(final TlsClientServerResult v) {
        delegate.publishResult(v);
    }
}

