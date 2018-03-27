package de.tum.in.net.client;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final Logger log = LoggerFactory.getLogger(SettingsActivity.class);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        final int uniqueJobId = 1;

        final JobScheduler js = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if (getString(R.string.background_service).equals(key)) {
            final String pref = sharedPreferences.getString(key, null);

            if ("0".equals(pref)) {
                js.cancel(uniqueJobId);
            } else {
                final int timeInMinutes = ConfigurationReader.readServiceTime(this);
                final long timeInMillis = timeInMinutes * 60 * 1000;
                log.error("Set TlsJobService service time in min: {}", timeInMinutes);

                final JobInfo job = new JobInfo.Builder(
                        uniqueJobId,
                        new ComponentName(this, TlsJobService.class))
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                        .setPeriodic(timeInMillis)
                        //.setBackoffCriteria(5 * 60 * 1000, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
                        .build();
                js.schedule(job);
            }

        }

    }

}
