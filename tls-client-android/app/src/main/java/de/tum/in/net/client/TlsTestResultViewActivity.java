package de.tum.in.net.client;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsTestResult;

public class TlsTestResultViewActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(TlsTestResultViewActivity.class);
    private final List<TlsClientServerResult> results = new ArrayList<>();
    private final TlsClientServerResultAdapter rAdapter = new TlsClientServerResultAdapter(results);
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_tls_test_result_view);

        final String timestamp = getIntent().getStringExtra("timestamp");
        final TlsTestResult testResult = new TlsDB(this).getTlsTestResult(timestamp);

        if (testResult != null) {
            results.addAll(testResult.getClientServerResults());
            final Context ctx = this;
            recyclerView = findViewById(R.id.targets_recycler_view);

            final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(rAdapter);
        }


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
