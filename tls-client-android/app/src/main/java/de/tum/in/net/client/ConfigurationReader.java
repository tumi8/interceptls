package de.tum.in.net.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by johannes on 07.04.17.
 */

public class ConfigurationReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationReader.class);

    /**
     * Reads the configuration for the target hosts.
     *
     * @param context
     * @return a Set of targets to probe
     */
    public static List<HostAndPort> getTargets(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        //we need to create a copy, otherwise we modify the original settings
        final Set<String> targetStrings = new HashSet<>(prefs.getStringSet(context.getString(R.string.hosts_default), null));

        final String additionalHosts = prefs.getString(context.getString(R.string.hosts_additional), null);

        if (additionalHosts != null) {
            final String[] hosts = additionalHosts.split("\n");
            for (final String host : hosts) {
                //targets is a set so we do not have any duplicates
                if (!host.isEmpty()) {
                    targetStrings.add(host.trim());
                }
            }
        }

        final List<HostAndPort> targets = new ArrayList<>();
        for (final String target : targetStrings) {

            try {
                final HostAndPort t = HostAndPort.parse(target);
                targets.add(t);
            } catch (final IllegalArgumentException e) {
                log.warn("Illegal host and port string found in settings: {}", target);
            }
        }

        return targets;
    }

    public static boolean isDataCollectionAllowed(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.data_collection), false);
    }

    public static int readServiceTime(final Context ctx) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String serviceTime = prefs.getString(ctx.getString(R.string.background_service), null);
        return Integer.parseInt(serviceTime);
    }

    public static boolean isLocationAllowed(final Context ctx) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(ctx.getString(R.string.location), false);
    }
}
