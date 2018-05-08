package de.tum.in.net.client;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.MiddleboxCharacterization;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.NetworkType;
import de.tum.in.net.model.TlsTestResult;

public class TlsTestResultViewActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(TlsTestResultViewActivity.class);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_tls_test_result_view);

        final String timestamp = getIntent().getStringExtra("timestamp");
        setTitle(Util.formatTimestamp(timestamp));
        final TlsDB db = new TlsDB(this);
        final AndroidTlsResult testResult = db.getAndroidTlsResult(timestamp);

        if (testResult != null) {
            showNetwork(testResult);
            showNetworkStats(testResult);

            //show probed hosts
            final RecyclerView recyclerView = findViewById(R.id.targets_recycler_view);

            final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            final TlsClientServerResultAdapter rAdapter = new TlsClientServerResultAdapter(testResult);
            recyclerView.setAdapter(rAdapter);

            showMiddleboxCharacterization(testResult.getTestResult());
        }


    }

    private void showMiddleboxCharacterization(final TlsTestResult testResult) {
        final View characterizationView = findViewById(R.id.middlebox_characterization_view);
        if (testResult.anyInterception()) {
            characterizationView.setVisibility(View.VISIBLE);
            final MiddleboxCharacterization characterization = testResult.getMiddleboxCharacterization();

            //tls versions
            final TextView tlsVersions = findViewById(R.id.tls_versions);
            tlsVersions.setText(characterization.getSupportedTlsVersions().toString());

            //wrong http host
            final TextView connectHttp = findViewById(R.id.can_connect_wrong_http_host);
            connectHttp.setText(String.valueOf(characterization.getCanConnectWrongHttpHost()));

            //wrong sni
            final TextView connectSni = findViewById(R.id.can_connect_wrong_sni);
            connectSni.setText(String.valueOf(characterization.getCanConnectWrongSni()));


        } else {
            characterizationView.setVisibility(View.GONE);
        }
    }

    private void showNetworkStats(final AndroidTlsResult testResult) {
        final View networkStatsView = findViewById(R.id.network_stats_view);
        final View networkStatsViewAlt = findViewById(R.id.network_stats_view_alt);
        if (testResult.isUploaded()) {
            networkStatsView.setVisibility(View.VISIBLE);
            networkStatsViewAlt.setVisibility(View.GONE);

            final TextView testCountView = findViewById(R.id.test_count);
            testCountView.setText(String.valueOf(testResult.getAnalysisResult().getStats().getCountTotal()));

            final TextView interceptionRateView = findViewById(R.id.interception_rate);
            final float interceptionRateTotal = testResult.getAnalysisResult().getStats().getInterceptionRateTotal();
            interceptionRateView.setText(String.valueOf(interceptionRateTotal) + " %");

            if (interceptionRateTotal > 0) {
                interceptionRateView.setTextColor(Color.RED);
            }

        } else {
            networkStatsView.setVisibility(View.GONE);
            networkStatsViewAlt.setVisibility(View.VISIBLE);
        }
    }

    private void showNetwork(final AndroidTlsResult testResult) {
        final NetworkId network = testResult.getTestResult().getNetworkId();
        final TextView networkTextView = findViewById(R.id.network_type);
        networkTextView.setText(network.getType().toString());

        final TextView publicIpTextView = findViewById(R.id.network_public_ip);
        publicIpTextView.setText(network.getPublicIp());

        final TextView dnsTextView = findViewById(R.id.dns);
        dnsTextView.setText(network.getDns().toString());

        final TextView gwIpTextView = findViewById(R.id.gateway_ip);
        gwIpTextView.setText(network.getDefaultGatewayIp());

        final TextView gwMacTextView = findViewById(R.id.gateway_mac);
        gwMacTextView.setText(network.getDefaultGatewayMac());

        final View wifiView = findViewById(R.id.wifi_view);
        if (NetworkType.WIFI.equals(network.getType())) {
            wifiView.setVisibility(View.VISIBLE);

            final TextView wifiSsidTextView = findViewById(R.id.wifi_ssid);
            wifiSsidTextView.setText(network.getSsid());

            final TextView wifiBssidTextView = findViewById(R.id.wifi_bssid);
            wifiBssidTextView.setText(network.getBssid());
        } else {
            wifiView.setVisibility(View.GONE);
        }

        final TextView locationTextView = findViewById(R.id.location);
        locationTextView.setText(String.valueOf(network.getLocation()));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
