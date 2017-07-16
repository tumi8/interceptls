package de.tum.in.net.demotlsclient;

import android.content.SharedPreferences;
import android.graphics.Color;
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
import java.util.Map;
import java.util.Set;

import de.tum.in.net.model.AnalysisResult;
import de.tum.in.net.model.AnalysisResultType;
import de.tum.in.net.model.Diff;
import de.tum.in.net.model.TestID;
import de.tum.in.net.model.TlsMessageDiff;
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

            final TextView generalResult = (TextView) findViewById(R.id.result_general);

            try {
                final FixedIdTestSession session = new FixedIdTestSession(test.getSessionID(), "https://141.40.254.119:3000");
                final AnalysisResult analysisResult = session.getAnalysisResult(test.getCounter());

                generalResult.setText(analysisResult.getType() + "\n");
                final TextView clientResultView = (TextView) findViewById(R.id.resultClientHello);

                if (AnalysisResultType.INTERCEPTION.equals(analysisResult.getType())) {
                    generalResult.setTextColor(Color.parseColor("#FF0000"));
                    final TlsMessageDiff diffClientHello = analysisResult.getClientHelloDiff();

                    Diff version = diffClientHello.getVersionDiff();
                    final TextView versionView = (TextView) findViewById(R.id.tls_version_client);
                    versionView.setText(version.toString());

                    for (final Map.Entry<String, Diff> diff : diffClientHello.getExtensionsDiff().entrySet()) {
                        clientResultView.append(diff.getKey() + ": " + diff.getValue() + "\n");
                    }

                    final TlsMessageDiff diffServerHello = analysisResult.getServerHelloDiff();

                    version = diffClientHello.getVersionDiff();
                    final TextView versionViewServer = (TextView) findViewById(R.id.tls_version_server);
                    versionViewServer.setText(version.toString());

                    final TextView serverResultView = (TextView) findViewById(R.id.resultServerHello);
                    for (final Map.Entry<String, Diff> diff : diffServerHello.getExtensionsDiff().entrySet()) {
                        serverResultView.append(diff.getKey() + ": " + diff.getValue() + "\n");
                    }

                    final TlsMessageDiff certDiff = analysisResult.getCertificateDiff();
                    final TextView certResultView = (TextView) findViewById(R.id.resultCertificate);
                    certResultView.setText(certDiff.getCertChainDiff().toString());
                }

            } catch (final IOException e) {
                log.error("Could not get analysis result", e);
                generalResult.setText("Error, " + e.getMessage());
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
