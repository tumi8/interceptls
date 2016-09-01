package com.example.demotlsclient.StreamDissector;

import android.util.JsonWriter;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by wohlfart on 31.08.16.
 */
public class ServerNameExtension extends TlsExtension {
    char serverNameListLength;
    byte serverNameType;
    char serverNameLength;
    String serverName;

    public ServerNameExtension(char length, DataInputStream in) throws IOException {
        serverNameListLength = in.readChar();
        serverNameType = in.readByte();
        serverNameLength = in.readChar();
        serverName = in.readUTF();
    }

    public JsonWriter toJson(JsonWriter out) throws IOException {
        out.beginObject();
        out.name("type").value("server name");
        out.name("server name").value(serverName);
        out.endObject();
        return out;
    }
}