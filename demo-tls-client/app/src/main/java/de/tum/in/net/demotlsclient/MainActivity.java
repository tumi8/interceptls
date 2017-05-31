package de.tum.in.net.demotlsclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.client.CaClientScenario;
import de.tum.in.net.session.OnlineTestSession;
import de.tum.in.net.util.CertificateUtil;

/**
 * Created by wohlfart on 11.08.16.
 */
public class MainActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

    // we could execute the scenarios in parallel later
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<ScenarioResult> results = new ArrayList<>();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        setContentView(R.layout.activity_main);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void startTestScenarios(final View v) {
        log.debug("Start test scenarios");
        results.clear();

        final List<Scenario> scenarios = new ArrayList<>();

        try {
            final InputStream in = getAssets().open("ca-cert.pem");
            final X509Certificate[] certs = CertificateUtil.readCerts(in);
            final Set<TrustAnchor> trustAnchors = new HashSet<>();
            for (final X509Certificate cert : certs) {
                trustAnchors.add(new TrustAnchor(cert, null));
            }
            scenarios.add(new CaClientScenario("10.0.2.2", 7623, trustAnchors));

            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            final Set<String> targets = ConfigurationReader.readHosts(this);
            progressBar.setMax(scenarios.size());
            progressBar.setVisibility(View.VISIBLE);

            final AsyncResult<ScenarioResult> asyncResult = new AsyncResult<ScenarioResult>() {
                @Override
                public void publishResult(final ScenarioResult result) {
                    results.add(result);
                    progressBar.setProgress(results.size());

                    if (results.size() == scenarios.size()) {
                        final TextView titleView = (TextView) findViewById(R.id.textView);
                        final TextView view = (TextView) findViewById(R.id.tview_tls_handshake);
                        view.setText("");
                        for (final ScenarioResult r : results) {
                            view.append(r.getDestination() + " " + r.getState() + "\n");
                        }

                        progressBar.setVisibility(View.GONE);
                        titleView.setVisibility(View.VISIBLE);
                        view.setVisibility(View.VISIBLE);

                        // TODO replace with real OnlineTestSession publisher
                        log.debug("publishing results");

                        try {
                            //10.0.2.2 is the ip of the machine running the emulator
                            final TestSession session = new OnlineTestSession("http://10.0.2.2:3000");
                            session.uploadHandshake(results);
                        } catch (final IOException e) {
                            // TODO save and try again later
                            e.printStackTrace();
                        }
                    }
                }
            };


            for (final Scenario scenario : scenarios) {
                final AsyncScenarioTask task = new AsyncScenarioTask(scenario, asyncResult);
                task.executeOnExecutor(executor);
            }

        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final CertificateException e) {
            e.printStackTrace();
        }


        //scenarios.add(new DefaultClientScenario("10.0.2.2", 7623));

        //
        //final List<Scenario> scenarios = new ArrayList<>(targets.size());
        //for (final String target : targets) {
        //    scenarios.add(new DefaultClientScenario(target, 443));
        //}
        
        //TODO read ca-certs from /system/etc/security/cacerts/...


    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
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
