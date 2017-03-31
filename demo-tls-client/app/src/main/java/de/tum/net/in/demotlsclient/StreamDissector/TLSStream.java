package de.tum.net.in.demotlsclient.StreamDissector;

import android.util.JsonWriter;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wohlfart on 31.08.16.
 */
public class TLSStream implements Dissector {
    List<Dissector> TlsRecordList;

    public TLSStream(DataInputStream data) {
        TlsRecordList = new ArrayList<Dissector>();
        try {
            while (data.available() > 0) {
                TlsRecordList.add(new TLSRecord(data));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        String res = "";
        for (Dissector d: TlsRecordList) {
            res += d.toString();
            res += "\n";
        }
        return res;
    }

    public JsonWriter toJson(JsonWriter out) throws IOException {
        out.beginArray();
        for (Dissector d: TlsRecordList) {
            d.toJson(out);
        }
        out.endArray();
        return out;
    }
}
