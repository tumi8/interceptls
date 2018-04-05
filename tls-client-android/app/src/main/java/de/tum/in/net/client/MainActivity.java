package de.tum.in.net.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wohlfart on 11.08.16.
 */
public class MainActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);
    private List<AndroidTlsResult> testList = new ArrayList<>();
    private ResultsAdapter rAdapter = new ResultsAdapter(testList);
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        setContentView(R.layout.activity_main);

        DbCleanJobService.init(this);

        final Context ctx = this;
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                final Intent intent = new Intent(ctx, TlsTestResultViewActivity.class);
                intent.putExtra("timestamp", testList.get(position).getTimestamp());
                startActivity(intent);
            }

            @Override
            public void onLongClick(final View view, final int position) {
                final AndroidTlsResult result = testList.get(position);

                final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage("Delete " + result.getTimestamp() + "?")
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int id) {
                                final AndroidTlsResult sessionID = testList.remove(position);
                                new TlsDB(ctx).deleteResult(sessionID.getTimestamp());
                                rAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int id) {
                                // User cancelled the dialog, nothing to do
                            }
                        });

                builder.create().show();

            }


        }));

        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(rAdapter);

        final FloatingActionButton fab = findViewById(R.id.start_test_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivity(new Intent(ctx, ProgressActivity.class));
            }
        });

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }


    @Override
    public void onResume() {
        super.onResume();

        final List<AndroidTlsResult> timestamps = new TlsDB(this).getTestResults();
        testList = new ArrayList<>(timestamps);
        rAdapter = new ResultsAdapter(testList);
        recyclerView.setAdapter(rAdapter);
        Collections.sort(testList, new Comparator<AndroidTlsResult>() {
            @Override
            public int compare(final AndroidTlsResult r1, final AndroidTlsResult r2) {
                return r1.getTimestamp().compareTo(r2.getTimestamp());
            }
        });
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
