
package de.tum.in.net.client;




import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tum.in.net.model.TlsTestResult;


public class TlsDB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tls_db";


    private static final String RESULTS_TABLE = "results";
    private static final String TIMESTAMP_COLUMN = "timestamp";
    private static final String UPLOADED_COLUMN = "uploaded";
    private static final String DATA_COLUMN = "data";


    public TlsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ RESULTS_TABLE +" (" +
                TIMESTAMP_COLUMN + " TIMESTAMP PRIMARY KEY," +
                UPLOADED_COLUMN + " BOOLEAN NOT NULL," +
                DATA_COLUMN + " TEXT NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void saveResultTemp(TlsTestResult result) {
        // get writable database as we want to write data
        try(SQLiteDatabase db = this.getWritableDatabase()){
            ContentValues values = new ContentValues();
            values.put(TIMESTAMP_COLUMN, result.getTimestamp());
            values.put(UPLOADED_COLUMN, false);
            values.put(DATA_COLUMN, new Gson().toJson(result));

            db.insert(RESULTS_TABLE, null, values);
        }

    }

    public Set<String> getTestTimestamps() {
        try(SQLiteDatabase db = this.getReadableDatabase()){
            Cursor cursor =db.query(RESULTS_TABLE, new String[]{TIMESTAMP_COLUMN}, null, null, null, null, TIMESTAMP_COLUMN);

            Set<String> timestamps = new HashSet<>();

            while(cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(TIMESTAMP_COLUMN));
                timestamps.add(id);
            }

            return timestamps;
        }
    }


    public TlsTestResult read(String timestamp) {
        try(SQLiteDatabase db = this.getReadableDatabase()){
            Cursor cursor =db.query(RESULTS_TABLE, new String[]{DATA_COLUMN}, TIMESTAMP_COLUMN +"=?", new String[]{String.valueOf(timestamp)}, null, null, TIMESTAMP_COLUMN);

            if(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(DATA_COLUMN));
                return new Gson().fromJson(data, TlsTestResult.class);
            }
        }

        return null;
    }

    public void uploadedResult(String timestamp) {
        try(SQLiteDatabase db = this.getWritableDatabase()){
            ContentValues values = new ContentValues();
            values.put(UPLOADED_COLUMN, true);

            db.update(RESULTS_TABLE, values, TIMESTAMP_COLUMN +"=?", new String[]{timestamp});
        }
    }

    public void deleteResult(String timestamp) {
        try(SQLiteDatabase db = this.getWritableDatabase()){
            db.delete(RESULTS_TABLE, TIMESTAMP_COLUMN +"=?", new String[]{timestamp});
        }
    }

    public List<AndroidTlsResult> getTestResults() {
        try(SQLiteDatabase db = this.getReadableDatabase()){
            Cursor cursor =db.query(RESULTS_TABLE, new String[]{TIMESTAMP_COLUMN, UPLOADED_COLUMN, DATA_COLUMN}, null, null, null, null, TIMESTAMP_COLUMN, "50");

            List<AndroidTlsResult> results = new ArrayList<>();

            while(cursor.moveToNext()){
                String timestamp = cursor.getString(cursor.getColumnIndex(TIMESTAMP_COLUMN));
                boolean uploaded = cursor.getInt(cursor.getColumnIndex(UPLOADED_COLUMN)) > 0;
                String data = cursor.getString(cursor.getColumnIndex(DATA_COLUMN));
                TlsTestResult r = new Gson().fromJson(data, TlsTestResult.class);

                AndroidTlsResult result = new AndroidTlsResult(timestamp, uploaded, r);
                results.add(result);
            }

            return results;
        }
    }

    public List<TlsTestResult> getNotUploadedResults() {
        try(SQLiteDatabase db = this.getReadableDatabase()){
            Cursor cursor =db.query(RESULTS_TABLE, new String[]{DATA_COLUMN}, UPLOADED_COLUMN+"=0", null, null, null, null);

            List<TlsTestResult> results = new ArrayList<>();

            while(cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(DATA_COLUMN));
                TlsTestResult r = new Gson().fromJson(data, TlsTestResult.class);
                results.add(r);
            }

            return results;
        }
    }
}
