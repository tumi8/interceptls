package de.tum.in.net.client;

import android.content.Context;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.net.model.TlsTestResult;

/**
 * Created by johannes on 07.04.17.
 */

public class ResultStorage {

    private static final Logger log = LoggerFactory.getLogger(ResultStorage.class);
    private static final String RESULT_DIR_NAME = "results";

    public static void saveTemp(final Context ctx, final TlsTestResult result) {
        final String filename = result.getTimestamp().replaceAll(":", "") + ".json.tmp";
        save(ctx, result, filename);
    }

    public static void saveFinal(final Context ctx, final TlsTestResult result) {
        final String filename = result.getTimestamp().replaceAll(":", "") + ".json";
        final File tempFile = new File(ctx.getFilesDir(), filename + ".tmp");
        if (tempFile.exists()) {
            tempFile.renameTo(new File(ctx.getFilesDir(), filename));
        } else {
            save(ctx, result, filename);
        }
    }

    private static void save(final Context ctx, final TlsTestResult result, final String filename) {
        try (final FileOutputStream fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(new Gson().toJson(result).getBytes());
        } catch (final IOException e) {
            log.error("Could not save result");
        }
    }

    public static Set<String> getSessions(final Context ctx) {
        final Set<String> sessions = new HashSet<>();
        for (final File f : ctx.getFilesDir().listFiles()) {
            if (f.getName().endsWith(".json")) {
                sessions.add(f.getName().replace(".json", ""));
            } else if (f.getName().endsWith(".json.tmp")) {
                sessions.add(f.getName().replace(".json.tmp", ""));
            }
        }
        return sessions;
    }

    public static TlsTestResult read(final Context ctx, final String sessionID) {
        String filename = sessionID + ".json";
        final File f = new File(ctx.getFilesDir(), filename);
        if (!f.exists()) {
            filename = filename + ".tmp";
        }

        try {
            final FileInputStream fis = ctx.openFileInput(filename);
            final InputStreamReader r = new InputStreamReader(fis);
            return new Gson().fromJson(r, TlsTestResult.class);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void delete(final Context ctx, final String sessionID) {
        final String filename = sessionID + ".json";
        File f = new File(ctx.getFilesDir(), filename);
        if (!f.exists()) {
            f = new File(ctx.getFilesDir(), filename + ".tmp");
        }
        f.delete();

    }
}
