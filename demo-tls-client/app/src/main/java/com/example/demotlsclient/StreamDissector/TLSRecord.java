package com.example.demotlsclient.StreamDissector;

import android.util.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by wohlfart on 31.08.16.
 */
public class TLSRecord implements Dissector {
    byte contentType;
    short version;
    short length;
    Dissector data;

    public TLSRecord(DataInputStream in) {
        try {
            contentType = in.readByte();
            version = in.readShort();
            length = in.readShort();
            byte[] payload = new byte[length];
            in.readFully(payload, 0, length);
            DataInputStream distream = new DataInputStream(new ByteArrayInputStream(payload));

            switch (contentType) {
                case 22: data = new TLSHandshake(distream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String contentTypeToString() {
        switch (contentType) {
            case 20: return "Change Cipher Spec";
            case 21: return "Alert";
            case 22: return "Handshake";
            case 23: return "Data";
        }
        return Byte.toString(contentType);
    }

    public String versionToString() {
        switch (version) {
            case 771: return "TLS1.2";
            default: return Integer.toString(version);
        }
    }

    public JsonWriter toJson(JsonWriter out) throws IOException {
        out.beginObject();
        out.name("type").value("record");
        out.name("version").value(versionToString());
        out.name("data");
        if (data != null) { data.toJson(out); }
        else { out.nullValue(); }
        out.endObject();
        return out;
    }
}
