package de.tum.net.in.demotlsclient.StreamDissector;

import android.util.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wohlfart on 31.08.16.
 */
public class ClientHello implements Dissector {
    short version;
    byte[] random;
    byte[] sessionId;
    List<Character> cipherSuites;
    List<Byte> compressionMethods;
    List<TlsExtension> extensions;

    public ClientHello(DataInputStream in) throws IOException {
        version = in.readShort();
        random = new byte[32];
        in.readFully(random, 0, 32);

        // session ID
        byte sessionIdLength = in.readByte();
        sessionId = new byte[sessionIdLength];
        in.readFully(sessionId, 0, sessionIdLength);

        // cipher suites
        char cipherSuitesLength = in.readChar();
        cipherSuites = new ArrayList<Character>();
        for (int i = 0; i < cipherSuitesLength / 2; i++) {
            cipherSuites.add(in.readChar());
        }

        // compression methods
        byte compressionMethodsLength = in.readByte();
        compressionMethods = new ArrayList<Byte>();
        for (int i = 0; i < compressionMethodsLength; i++) {
            compressionMethods.add(in.readByte());
        }

        // extensions
        char extensionsLength = in.readChar();
        extensions = new ArrayList<TlsExtension>();
        byte extensionDataRead = 0;
        while (extensionDataRead < extensionsLength) {
            char extensionType = in.readChar();
            System.out.println((int) extensionType);
            char extensionLength = in.readChar();
            byte[] data = new byte[extensionLength];
            in.readFully(data, 0, extensionLength);
            DataInputStream distream = new DataInputStream(new ByteArrayInputStream(data));
            switch (extensionType) {
                case 0:
                    extensions.add(new ServerNameExtension(extensionLength, distream));
            }

            extensionDataRead += 2 + 2 + extensionLength;
        }
    }

    public String versionToString() {
        switch (version) {
            case 771:
                return "TLS1.2";
            default:
                return Integer.toString(version);
        }
    }

    public JsonWriter toJson(JsonWriter out) throws IOException {
        out.beginObject();
        out.name("type").value("client hello");
        out.name("version").value(versionToString());
        out.name("random").value("foo");
        out.name("session id").value("bar");

        out.name("cipher suites").beginArray();
        //out.value(cipherSuites.size());
        for (char cipherSuite : cipherSuites) {
            out.value((int) cipherSuite);
        }
        out.endArray();

        out.name("compression methods").beginArray();
        for (byte compressionMethod : compressionMethods) {
            out.value(Byte.toString(compressionMethod));
        }
        out.endArray();

        out.name("extensions").beginArray();
        //out.value(extensions.size());
        for (TlsExtension extension : extensions) {
            extension.toJson(out);
        }
        out.endArray();

        out.endObject();
        return out;
    }
}
