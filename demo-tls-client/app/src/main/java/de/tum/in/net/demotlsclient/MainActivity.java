package de.tum.in.net.demotlsclient;

import android.content.Context;
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
import java.util.List;
import java.util.Set;

/**
 * Created by wohlfart on 11.08.16.
 */
public class MainActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        setContentView(R.layout.activity_main);

        updateListView();

        final Context ctx = this;
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_test_button);
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
        updateListView();
    }

    private void updateListView() {

        final Set<String> sessionIDs = ConfigurationReader.getSessionIDs(this);
        final List<String> testList = new ArrayList<>(sessionIDs);

        final Context ctx = this;
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                final Intent intent = new Intent(ctx, SessionViewActivity.class);
                intent.putExtra("sessionID", testList.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(final View view, final int position) {
                log.debug("Long click on {}", testList.get(position));

            }
        }));
        final ResultsAdapter rAdapter = new ResultsAdapter(testList);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(rAdapter);


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
