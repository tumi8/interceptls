package de.tum.in.net.demotlsclient;

import android.os.AsyncTask;

import java.util.concurrent.Callable;

/**
 * Created by johannes on 22.03.17.
 */

public class AsyncScenarioTask<T> extends AsyncTask<Void, Void, T> {

    private final Callable<T> scenario;
    private final AsyncResult<T> delegate;

    public AsyncScenarioTask(final Callable<T> scenario, final AsyncResult<T> delegate) {
        this.scenario = scenario;
        this.delegate = delegate;
    }

    @Override
    protected T doInBackground(final Void... voids) {
        try {
            return scenario.call();
        } catch (final Exception e) {
            throw new RuntimeException("unexcepted error", e);
        }
    }

    @Override
    protected void onPostExecute(final T v) {
        delegate.publishResult(v);
    }
}

