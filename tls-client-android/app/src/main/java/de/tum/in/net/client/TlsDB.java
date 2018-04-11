
package de.tum.in.net.client;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.model.TlsTestResult;


public class TlsDB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tls_db";


    private static final String RESULTS_TABLE = "results";
    private static final String TIMESTAMP_COLUMN = "timestamp";
    private static final String DATA_COLUMN = "data";
    private static final String ANALYSIS_COLUMN = "analysis";


    public TlsDB(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RESULTS_TABLE + " (" +
                TIMESTAMP_COLUMN + " TIMESTAMP PRIMARY KEY," +
                DATA_COLUMN + " TEXT NOT NULL," +
                ANALYSIS_COLUMN + " TEXT)");

    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

    }

    public void saveResultTemp(final TlsTestResult result) {
        // get writable database as we want to write data
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            final ContentValues values = new ContentValues();
            values.put(TIMESTAMP_COLUMN, result.getTimestamp());
            values.put(DATA_COLUMN, new Gson().toJson(result));

            db.insert(RESULTS_TABLE, null, values);
        }

    }

    public Set<String> getTestTimestamps() {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (final Cursor cursor = db.query(RESULTS_TABLE, new String[]{TIMESTAMP_COLUMN}, null, null, null, null, TIMESTAMP_COLUMN)) {
                final Set<String> timestamps = new HashSet<>();

                while (cursor.moveToNext()) {
                    final String id = cursor.getString(cursor.getColumnIndex(TIMESTAMP_COLUMN));
                    timestamps.add(id);
                }

                return timestamps;
            }
        }
    }


    public AndroidTlsResult getAndroidTlsResult(final String timestamp) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (final Cursor cursor = db.query(RESULTS_TABLE, new String[]{TIMESTAMP_COLUMN, DATA_COLUMN, ANALYSIS_COLUMN}, TIMESTAMP_COLUMN + "=?", new String[]{String.valueOf(timestamp)}, null, null, TIMESTAMP_COLUMN)) {
                if (cursor.moveToNext()) {
                    return getAndroidTlsResult(cursor);
                }
            }
        }
        return null;
    }

    public void uploadedResult(final String timestamp, final AnalysisResult analysisResult) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            final ContentValues values = new ContentValues();
            values.put(ANALYSIS_COLUMN, new Gson().toJson(analysisResult));

            db.update(RESULTS_TABLE, values, TIMESTAMP_COLUMN + "=?", new String[]{timestamp});
        }
    }

    public void deleteResult(final String timestamp) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(RESULTS_TABLE, TIMESTAMP_COLUMN + "=?", new String[]{timestamp});
        }
    }

    public List<AndroidTlsResult> getTestResults() {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (final Cursor cursor = db.query(RESULTS_TABLE, new String[]{TIMESTAMP_COLUMN, DATA_COLUMN, ANALYSIS_COLUMN}, null, null, null, null, TIMESTAMP_COLUMN, "50")) {
                final List<AndroidTlsResult> results = new ArrayList<>();

                while (cursor.moveToNext()) {
                    final AndroidTlsResult result = getAndroidTlsResult(cursor);
                    results.add(result);
                }

                return results;
            }
        }
    }

    private AndroidTlsResult getAndroidTlsResult(final Cursor cursor) {
        final String timestamp = cursor.getString(cursor.getColumnIndex(TIMESTAMP_COLUMN));
        final String data = cursor.getString(cursor.getColumnIndex(DATA_COLUMN));
        final TlsTestResult testResult = new Gson().fromJson(data, TlsTestResult.class);

        final String analysis = cursor.getString(cursor.getColumnIndex(ANALYSIS_COLUMN));
        final AnalysisResult analysisResult = analysis == null ? null : new Gson().fromJson(analysis, AnalysisResult.class);

        return new AndroidTlsResult(timestamp, testResult, analysisResult);
    }

    public List<TlsTestResult> getNotUploadedResults() {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            try (final Cursor cursor = db.query(RESULTS_TABLE, new String[]{DATA_COLUMN}, ANALYSIS_COLUMN + " IS NULL", null, null, null, null)) {
                final List<TlsTestResult> results = new ArrayList<>();

                while (cursor.moveToNext()) {
                    final String data = cursor.getString(cursor.getColumnIndex(DATA_COLUMN));
                    final TlsTestResult r = new Gson().fromJson(data, TlsTestResult.class);
                    results.add(r);
                }

                return results;
            }
        }
    }

    /**
     * Deletes all results that are
     * - more than 7 days old and successfully uploaded
     * - more than 14 days old (despite not beeing uploaded).
     * Compresses the DB afterwards.
     */
    public void deleteOldTests() {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.execSQL("DELETE FROM " + RESULTS_TABLE + " WHERE " + TIMESTAMP_COLUMN + "<= date('now','-14 day')");
            db.execSQL("DELETE FROM " + RESULTS_TABLE + " WHERE " + TIMESTAMP_COLUMN + "<= date('now','-7 day') AND " + ANALYSIS_COLUMN + " IS NOT NULL");
            db.execSQL("VACUUM");
        }

    }


    public boolean enoughTimeSinceLastScan() {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            //read last test time from db
            try (final Cursor c = db.query(RESULTS_TABLE, new String[]{TIMESTAMP_COLUMN}, null, null, null, null, TIMESTAMP_COLUMN + " DESC", "1")) {
                String time = null;
                if (c.moveToNext()) {
                    time = c.getString(c.getColumnIndex(TIMESTAMP_COLUMN));
                }
                //we test not more than once in 20 minutes
                return time == null || LocalDateTime.now().minusMinutes(20).isAfter(LocalDateTime.parse(time));
            }
        }
    }

    public boolean isUploaded(final String timestamp) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            //read last test time from db
            try (final Cursor c = db.query(RESULTS_TABLE, new String[]{ANALYSIS_COLUMN}, TIMESTAMP_COLUMN + "=?", new String[]{timestamp}, null, null, null)) {
                boolean uploaded = false;
                if (c.moveToNext()) {
                    uploaded = c.getString(c.getColumnIndex(ANALYSIS_COLUMN)) != null;
                }
                return uploaded;
            }
        }
    }
}
