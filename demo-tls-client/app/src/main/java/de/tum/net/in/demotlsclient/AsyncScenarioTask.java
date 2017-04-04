package de.tum.net.in.demotlsclient;

import android.os.AsyncTask;

import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 22.03.17.
 */

public class AsyncScenarioTask extends AsyncTask<Void, Void, ScenarioResult> {

    private final Scenario scenario;
    private final AsyncResult<ScenarioResult> delegate;

    public AsyncScenarioTask(Scenario scenario, AsyncResult<ScenarioResult> delegate) {
        this.scenario = scenario;
        this.delegate = delegate;
    }

    @Override
    protected ScenarioResult doInBackground(Void... voids) {
        return scenario.call();
    }

    @Override
    protected void onPostExecute(ScenarioResult v) {
        delegate.publishResult(v);
    }
}

