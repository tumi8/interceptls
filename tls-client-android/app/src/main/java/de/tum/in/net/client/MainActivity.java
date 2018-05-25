/**
 * Copyright © 2018 Johannes Schleger (johannes.schleger@tum.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tum.in.net.client;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    private static final int ACCESS_LOCATION_PERMISSION = 1;
    private static final int ACCESS_LOCATION_PERMISSION_AND_START_TEST = 2;
    public static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private List<AndroidTlsResult> testList = new ArrayList<>();
    private AndroidTlsResultAdapter rAdapter = new AndroidTlsResultAdapter(testList);
    private RecyclerView recyclerView;
    private Activity ctx;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        setContentView(R.layout.activity_main);

        DbCleanJobService.init(this);
        TlsJobService.init(this);

        ctx = this;
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                final String timestamp = testList.get(position).getTimestamp();
                final Intent intent = new Intent(ctx, TlsTestResultViewActivity.class);
                intent.putExtra("timestamp", timestamp);
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
                checkPermission(true);
            }
        });

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        checkPermission(false);
    }

    private void checkPermission(final boolean startTest) {
        if (ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request permission

            final int permissionId = startTest ? ACCESS_LOCATION_PERMISSION_AND_START_TEST : ACCESS_LOCATION_PERMISSION;
            if (ActivityCompat.shouldShowRequestPermissionRationale(ctx,
                    LOCATION_PERMISSION)) {
                // Show an explanation to the user *asynchronously*
                final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage(R.string.check_permission_location)
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int id) {
                                ActivityCompat.requestPermissions(ctx,
                                        new String[]{LOCATION_PERMISSION},
                                        permissionId);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int id) {
                                // User cancelled the dialog, nothing to do
                            }
                        });

                builder.create().show();

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(ctx,
                        new String[]{LOCATION_PERMISSION},
                        permissionId);
            }

            //callback is handled in onRequestPermissionsResult()

        } else {
            //we have the permission, start test
            if (startTest) {
                startActivity(new Intent(ctx, ProgressActivity.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final String[] permissions, final int[] grantResults) {
        switch (requestCode) {
            case ACCESS_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do nothing
                } else {
                    //ask again
                    checkPermission(false);
                }
                break;

            case ACCESS_LOCATION_PERMISSION_AND_START_TEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startActivity(new Intent(ctx, ProgressActivity.class));
                } else {
                    // permission denied, we cannot start the test without the permission
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ctx = this;

        final List<AndroidTlsResult> timestamps = new TlsDB(this).getTestResults();
        testList = new ArrayList<>(timestamps);
        rAdapter = new AndroidTlsResultAdapter(testList);
        recyclerView.setAdapter(rAdapter);
        Collections.sort(testList, new Comparator<AndroidTlsResult>() {
            @Override
            public int compare(final AndroidTlsResult r1, final AndroidTlsResult r2) {
                return r2.getTimestamp().compareTo(r1.getTimestamp());
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
            case R.id.license:
                startActivity(new Intent(this, LicenseActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
