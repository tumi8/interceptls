package de.tum.in.net.demotlsclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SessionViewActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(SessionViewActivity.class);


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_view);

        final String sessionID = getIntent().getStringExtra("sessionID");
        if (sessionID == null) {
            throw new NullPointerException("SessionID must not be null.");
        }

        final Set<String> testIDs = ConfigurationReader.getTestIDs(this, sessionID);
        final List<String> testIDList = new ArrayList<>(testIDs);


        final Context ctx = this;
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                final Intent intent = new Intent(ctx, TestViewActivity.class);
                intent.putExtra("testID", testIDList.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(final View view, final int position) {
                log.debug("Long click on {}", testIDList.get(position));

            }
        }));
        final ResultsAdapter rAdapter = new ResultsAdapter(testIDList);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(rAdapter);


    }

}
