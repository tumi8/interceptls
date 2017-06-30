package de.tum.in.net.demotlsclient;

import android.os.AsyncTask;

import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 22.03.17.
 */

public class AsyncTaskPublisher extends AsyncTask<Object, Object, ScenarioResult> {

    private final Scenario scenario;
    private final AsyncResult<ScenarioResult> delegate;

    public AsyncTaskPublisher(final Scenario scenario, final AsyncResult<ScenarioResult> delegate) {
        this.scenario = scenario;
        this.delegate = delegate;
    }

    @Override
    protected ScenarioResult doInBackground(final Object... voids) {
        return scenario.call();
    }

    @Override
    protected void onPostExecute(final ScenarioResult v) {
        delegate.publishResult(v);
    }
}

