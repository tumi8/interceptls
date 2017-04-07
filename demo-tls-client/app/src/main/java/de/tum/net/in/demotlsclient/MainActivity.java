package de.tum.net.in.demotlsclient;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.tum.in.net.demotlsclient.R;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.client.DefaultClientScenario;
import de.tum.in.net.session.TestSession;
import de.tum.in.net.session.TestSessionLogger;

/**
 * Created by wohlfart on 11.08.16.
 */
public class MainActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

    // we could execute the scenarios in parallel later
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<ScenarioResult> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        setContentView(R.layout.activity_main);
    }

    public void startTestScenarios(View v) {
        log.debug("Start test scenarios");
        results.clear();

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        final Set<String> targets = ConfigurationReader.readHosts(this);
        progressBar.setMax(targets.size());
        progressBar.setVisibility(View.VISIBLE);

        AsyncResult<ScenarioResult> asyncResult = new AsyncResult<ScenarioResult>() {
            @Override
            public void publishResult(ScenarioResult result) {
                results.add(result);
                progressBar.setProgress(results.size());

                if (results.size() == targets.size()) {
                    final TextView titleView = (TextView) findViewById(R.id.textView);
                    final TextView view = (TextView) findViewById(R.id.tview_tls_handshake);
                    view.setText("");
                    for (ScenarioResult r : results) {
                        view.append(r.getDestination() + " " + r.isSuccess() + "\n");
                    }

                    progressBar.setVisibility(View.GONE);
                    titleView.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);

                    // TODO replace with real ICSITestSession publisher
                    log.debug("publishing results");
                    TestSession session = new TestSessionLogger();

                    try {
                        session.uploadResults(results);
                    } catch (IOException e) {
                        // TODO save and try again later
                        e.printStackTrace();
                    }
                }
            }
        };

        List<Scenario> scenarios = new ArrayList<>(targets.size());
        for (String target : targets) {
            scenarios.add(new DefaultClientScenario(target, 443));
        }

        for (Scenario scenario : scenarios) {
            AsyncScenarioTask task = new AsyncScenarioTask(scenario, asyncResult);
            task.executeOnExecutor(executor);
        }

        //TODO read ca-certs from /system/etc/security/cacerts/...


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
