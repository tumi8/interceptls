package de.tum.in.net.demotlsclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.net.model.TestID;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.State;
import de.tum.in.net.scenario.client.DefaultClientScenario;
import de.tum.in.net.session.OnlineTestSession;
import de.tum.in.net.session.SessionID;

/**
 * Created by johannes on 30.06.17.
 */

public class ExecuteSessionTask extends AsyncTask<Void, String, SessionID> {

    private static final Logger log = LoggerFactory.getLogger(ExecuteSessionTask.class);
    private final Context ctx;
    private final TextView textView;
    private final AsyncResult<SessionID> listener;

    public ExecuteSessionTask(final Context ctx, final TextView textView, final AsyncResult<SessionID> listener) {
        this.ctx = ctx;
        this.textView = textView;
        this.listener = listener;
    }

    @Override
    protected SessionID doInBackground(final Void... voids) {
        final SharedPreferences preferences = ctx.getSharedPreferences("tls", 0);
        final Set<String> conductedTests = preferences.getStringSet("conductedTests", new HashSet<String>());
        final SharedPreferences.Editor edit = preferences.edit();

        SessionID sessionID = null;
        try {

            publishProgress("Start online test session");
            final TestSession session = new OnlineTestSession("https://10.83.81.2:3000");
            sessionID = session.getSessionID();
            ConfigurationReader.addSessionID(ctx, sessionID);
            publishProgress("Got online test session");
            final TestIDIncrementer inc = new TestIDIncrementer(session.getSessionID());

            final TestID testID = inc.next();
            ConfigurationReader.addTestID(ctx, testID);

            //10.0.2.2 is the ip of the machine running the emulator
            final String target = ConfigurationReader.getTargetHost(ctx);
            final Scenario scenario = new DefaultClientScenario(testID, target, 443);

            final Set<String> targets = ConfigurationReader.readHosts(ctx);


            publishProgress("Start scenario");
            final ScenarioResult result = scenario.call();


            edit.putString(testID.toString(), TestLifecycle.CONNECTED.toString());
            edit.apply();


            if (State.CONNECTED.equals(result.getState())) {
                publishProgress("Publish results");

                try {
                    session.uploadHandshake(testID.getCounter(), result);
                    edit.putString(testID.toString(), TestLifecycle.PUBLISHED.toString());
                    edit.apply();


                } catch (final IOException e) {
                    // TODO save and try again later
                    e.printStackTrace();
                }
            } else {
                //error or no_connection
                //TODO

            }

        } catch (final IOException e) {
            log.error("IOError" + e.getMessage(), e);
        }
        return sessionID;
    }

    @Override
    protected void onProgressUpdate(final String... values) {
        super.onProgressUpdate(values);
        textView.setText(values[0]);
    }

    @Override
    protected void onPostExecute(final SessionID v) {
        super.onPostExecute(v);
        listener.publishResult(v);
    }
}
