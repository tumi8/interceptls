package com.example.demotlsclient.StreamDissector;

import android.util.JsonWriter;

import java.io.IOException;

/**
 * Created by wohlfart on 31.08.16.
 */
public interface Dissector {
    public abstract JsonWriter toJson(JsonWriter out) throws IOException;
}
