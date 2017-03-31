package com.example.demotlsclient;

import android.os.AsyncTask;
import android.util.JsonWriter;
import android.widget.Button;
import android.widget.TextView;

import com.example.demotlsclient.StreamDissector.Dissector;
import com.example.demotlsclient.StreamDissector.TLSStream;

import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClient;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;
import java.security.SecureRandom;

import de.tum.in.net.SniTlsClient;

/**
 * Created by johannes on 22.03.17.
 */

public class TLSTask extends AsyncTask<Void, MyTaskParams, String> {

    AsyncResult<MyTaskParams, String> delegate;

    public TLSTask(AsyncResult<MyTaskParams, String> delegate) {
        this.delegate = delegate;
    }


    @Override
    protected String doInBackground(Void... voids) {

        publishProgress(new MyTaskParams("connecting...", null));

        try {
            Socket socket = new Socket("198.35.26.96", 443); // wikipedia.org
            //Socket socket = new Socket("192.168.6.214", 4433); // localhost
            publishProgress(new MyTaskParams("connected...", null));

            // setup tap
            ByteArrayOutputStream tap = new ByteArrayOutputStream();
            TeeOutputStream teeOut = new TeeOutputStream(socket.getOutputStream(), tap);
            TeeInputStream teeIn = new TeeInputStream(socket.getInputStream(), tap);

            SecureRandom secureRandom = new SecureRandom();
            TlsClientProtocol tlsClientProtocol = new TlsClientProtocol(teeIn, teeOut, secureRandom);


                /*TlsClient tlsclient = new DefaultTlsClient() {
                    public TlsAuthentication getAuthentication() throws IOException {
                        return new ServerOnlyTlsAuthentication() {
                            public void notifyServerCertificate(Certificate serverCertificate) throws IOException {
                                //validateCertificate(serverCertificate);
                                // TODO: certificate validation
                                // see: https://stackoverflow.com/questions/18065170/how-do-i-do-tls-with-bouncycastle
                                //      https://stackoverflow.com/questions/16490447/validate-tls-server-certificate-with-bouncycastles-lightweight-api
                            }
                        };
                    }
                };*/

            TlsClient tlsclient = new SniTlsClient("google.com") {

                public TlsAuthentication getAuthentication() throws IOException {
                    return new ServerOnlyTlsAuthentication() {
                        @Override
                        public void notifyServerCertificate(Certificate serverCertificate) throws IOException {
                            // TODO
                        }
                    };
                }
            };

            // connect
            tlsClientProtocol.connect(tlsclient);
            publishProgress(new MyTaskParams("finished TLS Handshake...", null));

            // dump TLS handshake transcript from tap
            byte[] hsBuffer = tap.toByteArray();

            // String handshakeHexdump = byteArrayToHex(hsBuffer);
            DataInputStream distream = new DataInputStream(new ByteArrayInputStream(hsBuffer));
            Dissector tlsrec = new TLSStream(distream);
            System.out.println(Hex.toHexString(hsBuffer));

            StringWriter sw = new StringWriter();
            JsonWriter jw = new JsonWriter(sw);
            jw.setIndent("    ");
            tlsrec.toJson(jw);

            //publishProgress(new MyTaskParams(tlsrec.toString(), null));
            publishProgress(new MyTaskParams(sw.toString(), null));
            //publishProgress(new MyTaskParams(tap.toByteArray().toString(), null));

            // send HTTP request
            String http_req = "GET / HTTP/1.1\r\nHost: www.wikipedia.org\r\nConnection: close\r\n\r\n";
            DataOutputStream out = new DataOutputStream(tlsClientProtocol.getOutputStream());
            out.writeBytes(http_req);

            // receive HTTP response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(tlsClientProtocol.getInputStream()));
            String http_resp = "";
            for (int i = 0; i < 50; i++) {
                http_resp += in.readLine() + "\n";
            }

            //System.out.println(http_resp);
            publishProgress(new MyTaskParams(null, http_resp));

        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        return "done";
    }

    @Override
    protected void onProgressUpdate(MyTaskParams... p) {
        delegate.publishProgress(p[0]);
    }

    @Override
    protected void onPostExecute(String v) {
        delegate.publishResult(v);

    }
}

