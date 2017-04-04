package de.tum.net.in.demotlsclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.bouncycastle.util.encoders.Base64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.tum.in.net.demotlsclient.R;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.client.DefaultClientScenario;

/**
 * Created by wohlfart on 11.08.16.
 */
public class MainActivity extends AppCompatActivity {

    // we could executie the scenarios in parallel later
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<ScenarioResult> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                for(ScenarioResult r : results){
                    view.append(r.getDestination() + " " + r.isSuccess() + "\n");
                }
            }
        };

        Scenario defaultScenario = new DefaultClientScenario("www.wikipedia.org", 443);
        Scenario defaultScenario2 = new DefaultClientScenario("www.heise.de", 443);

        List<Scenario> scenarios = Arrays.asList(defaultScenario, defaultScenario2);

        for (Scenario scenario : scenarios ){
            AsyncScenarioTask task = new AsyncScenarioTask(scenario, asyncResult);
            task.executeOnExecutor(executor);
        }


        System.out.println("clicked");
    }




}
