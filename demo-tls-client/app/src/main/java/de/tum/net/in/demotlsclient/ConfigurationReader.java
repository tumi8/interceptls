package de.tum.net.in.demotlsclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

import de.tum.in.net.demotlsclient.R;

/**
 * Created by johannes on 07.04.17.
 */

public class ConfigurationReader {

    /**
     * Reads the configuration for the target hosts.
     *
     * @param context
     * @return a Set of targets to probe
     */
    public static Set<String> readHosts(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final Set<String> targets = prefs.getStringSet(context.getString(R.string.hosts_default), null);

        final String additionalHosts = prefs.getString(context.getString(R.string.hosts_additional), "");
        final String[] hosts = additionalHosts.split("\n");
        for (final String host : hosts) {
            //targets is a set so we do not have any duplicates
            if (!host.isEmpty()) {
                targets.add(host.trim());
            }
        }

        return targets;
    }
}
