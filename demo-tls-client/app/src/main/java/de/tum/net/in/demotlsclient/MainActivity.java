package de.tum.net.in.demotlsclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.tum.in.net.demotlsclient.R;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.client.DefaultClientScenario;

/**
 * Created by wohlfart on 11.08.16.
 */
public class MainActivity extends AppCompatActivity {

    // we could execute the scenarios in parallel later
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<ScenarioResult> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        setContentView(R.layout.activity_main);
    }

    public void buttonClick(View v) {
        final TextView view = (TextView) findViewById(R.id.tview_tls_handshake);
        view.setText("waiting...");
        results.clear();

        AsyncResult<ScenarioResult> asyncResult = new AsyncResult<ScenarioResult>() {
            @Override
            public void publishResult(ScenarioResult result) {
                results.add(result);
                view.setText("");
                for (ScenarioResult r : results) {
                    view.append(r.getDestination() + " " + r.isSuccess() + "\n");
                }
            }
        };

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> targets = prefs.getStringSet(getString(R.string.hosts_default), null);

        List<Scenario> scenarios = new ArrayList<>(targets.size());
        for (String target : targets) {
            scenarios.add(new DefaultClientScenario(target, 443));
        }

        for (Scenario scenario : scenarios) {
            AsyncScenarioTask task = new AsyncScenarioTask(scenario, asyncResult);
            task.executeOnExecutor(executor);
        }


        System.out.println("clicked");
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
