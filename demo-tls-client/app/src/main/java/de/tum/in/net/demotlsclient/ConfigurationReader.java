package de.tum.in.net.demotlsclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.net.model.TestID;
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
    public static Set<String> readHosts(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final Set<String> targets = prefs.getStringSet(context.getString(R.string.hosts_default), null);

        final String additionalHosts = prefs.getString(context.getString(R.string.hosts_additional), null);
        if (additionalHosts != null) {
            final String[] hosts = additionalHosts.split("\n");
            for (final String host : hosts) {
                //targets is a set so we do not have any duplicates
                if (!host.isEmpty()) {
                    targets.add(host.trim());
                }
            }
        }

        return targets;
    }

    public static String getTargetHost(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.target), null);
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

    public static void addTestID(final Context ctx, final TestID testID) {
        final SharedPreferences tls = ctx.getSharedPreferences("tls", 0);
        Set<String> mapping = tls.getStringSet(testID.getSessionID().toString(), null);
        final SharedPreferences.Editor edit = tls.edit();
        if (mapping == null) mapping = new HashSet<>();
        mapping.add(testID.toString());
        edit.putStringSet(testID.getSessionID().toString(), mapping);
        edit.apply();
    }

    public static Set<String> getTestIDs(final Context ctx, final String sessionID) {
        final SharedPreferences tls = ctx.getSharedPreferences("tls", 0);
        return tls.getStringSet(sessionID, new HashSet<String>());
    }


}
