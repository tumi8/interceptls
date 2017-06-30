package de.tum.in.net.demotlsclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.net.model.AnalysisResult;
import de.tum.in.net.model.Diff;
import de.tum.in.net.model.TestID;
import de.tum.in.net.session.FixedIdTestSession;
import de.tum.in.net.util.CertificateUtil;

public class TestViewActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(TestViewActivity.class);

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_view);

        final String testID = getIntent().getStringExtra("testID");
        if (testID == null) {
            //start test
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            progressBar.setVisibility(View.VISIBLE);


        } else {
            //show test results
            final SharedPreferences preferences = getSharedPreferences("tls", 0);
            final TestLifecycle cycle = TestLifecycle.valueOf(preferences.getString(testID, null));
            log.error("status: " + cycle);

            final TestID test = TestID.parse(testID);

            try {
                final FixedIdTestSession session = new FixedIdTestSession(test.getSessionID(), "https://10.83.81.2:3000");

                int counter = 0;
                boolean success = false;
                AnalysisResult analysisResult;
                do {
                    counter++;
                    analysisResult = session.getAnalysisResult(test.getCounter());

                    switch (analysisResult.getType()) {
                        case NO_CLIENT_NO_SERVER_RESULT:
                        case NO_CLIENT_RESULT:
                        case NO_SERVER_RESULT:
                            try {
                                Thread.sleep(2000);
                            } catch (final InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;

                        default:
                            success = true;
                            break;

                    }
                } while (!success && counter <= 15);


                final TextView clientResultView = (TextView) findViewById(R.id.resultClientHello);
                clientResultView.setVisibility(View.VISIBLE);
                if (success) {

                    //generalResultsView.append("Intercepted? " + analysisResult.getType() + "\n");

                    for (final Diff diff : analysisResult.getClientHelloDiffs()) {
                        clientResultView.append(diff.toString() + "\n");
                    }
                } else {
                    clientResultView.append("Result? " + analysisResult.getType() + "\n");
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }

        }
    }


    private Set<TrustAnchor> readCaCerts() throws IOException, CertificateException {
        final String caDir = "ca-certs";
        final Set<TrustAnchor> trustAnchors = new HashSet<>();
        for (final String ca : getAssets().list(caDir)) {
            final InputStream in = getAssets().open(caDir + File.separator + ca);
            final X509Certificate caCert = CertificateUtil.readCert(in);
            trustAnchors.add(new TrustAnchor(caCert, null));
        }
        return trustAnchors;
    }
}
