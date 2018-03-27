package de.tum.in.net.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tum.in.net.client.HostAndPort;
import de.tum.in.net.session.SessionID;

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
    public static List<HostAndPort> getTargets(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        //we need to create a copy, otherwise we modify the original settings
        final Set<String> targetStrings = new HashSet<>(prefs.getStringSet(context.getString(R.string.hosts_default), null));

        LoggerFactory.getLogger("blubbbb").error("targets: {}", targetStrings);

        final String additionalHosts = prefs.getString(context.getString(R.string.hosts_additional), null);
        LoggerFactory.getLogger("blubbbb").error("additional targets: {}", additionalHosts);

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
            if (target.contains(":")) {
                final String[] splittedTarget = target.split(":");
                if (splittedTarget.length == 2) {
                    try {
                        final int port = Integer.parseInt(splittedTarget[1]);
                        targets.add(new HostAndPort(splittedTarget[0], port));
                    } catch (final NumberFormatException e) {
                        //silently ignore
                    }

                }

            } else {
                targets.add(new HostAndPort(target));
            }
        }

        return targets;
    }

    public static String getAnalysisHostUrl(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.analysis_server_url), null);
    }

    public static void addSessionID(final Context ctx, final SessionID id) {
        final SharedPreferences tls = ctx.getSharedPreferences("tls", 0);
        Set<String> sessionIDs = tls.getStringSet("sessionIDs", null);
        final SharedPreferences.Editor edit = tls.edit();
        if (sessionIDs == null) sessionIDs = new HashSet<>();
        sessionIDs.add(id.toString());
        edit.putStringSet("sessionIDs", sessionIDs);
        edit.apply();
    }

    public static Set<String> getSessionIDs(final Context ctx) {
        final SharedPreferences tls = ctx.getSharedPreferences("tls", 0);
        return tls.getStringSet("sessionIDs", new HashSet<String>());
    }

//    public static void addTestID(final Context ctx, final TestID testID) {
//        final SharedPreferences tls = ctx.getSharedPreferences("tls", 0);
//        Set<String> mapping = tls.getStringSet(testID.getSessionID().toString(), null);
//        final SharedPreferences.Editor edit = tls.edit();
//        if (mapping == null) mapping = new HashSet<>();
//        mapping.add(testID.toString());
//        edit.putStringSet(testID.getSessionID().toString(), mapping);
//        edit.apply();
//    }

    public static Set<String> getTestIDs(final Context ctx, final String sessionID) {
        final SharedPreferences tls = ctx.getSharedPreferences("tls", 0);
        return tls.getStringSet(sessionID, new HashSet<String>());
    }

    public static int readServiceTime(final Context ctx) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String serviceTime = prefs.getString(ctx.getString(R.string.background_service), null);
        return Integer.parseInt(serviceTime);
    }
}
