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
public class TLSHandshake implements Dissector {
    byte handshakeType;
    char length;
    Dissector message;

    public TLSHandshake(DataInputStream in) {
        try {
            handshakeType = in.readByte();
            in.skipBytes(1);
            length = in.readChar();
            byte[] payload = new byte[length];
            in.readFully(payload, 0, length);
            DataInputStream distream = new DataInputStream(new ByteArrayInputStream(payload));

            switch (handshakeType) {
                case 1: message = new ClientHello(distream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String handshakeTypeToString() {
        switch (handshakeType) {
            case 0: return "Hello Request";
            case 1: return "Client Hello";
            case 2: return "Server Hello";
            case 11: return "Certificate";
            case 12: return "Server Key Exchange";
            case 14: return "Server Hello Done";
            case 16: return "Client Key Exchange";
        }
        return Byte.toString(handshakeType);
    }

    public JsonWriter toJson(JsonWriter out) throws IOException {
        out.beginObject();
        out.name("type").value("handshake");
        out.name("message");
        if (message != null) { message.toJson(out); }
        else { out.nullValue(); }
        out.endObject();
        return out;
    }
}
