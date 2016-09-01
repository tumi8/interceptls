package com.example.demotlsclient;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.demotlsclient.StreamDissector.Dissector;
import com.example.demotlsclient.StreamDissector.TLSRecord;
import com.example.demotlsclient.StreamDissector.TLSStream;

import org.spongycastle.crypto.tls.Certificate;
import org.spongycastle.crypto.tls.DefaultTlsClient;
import org.spongycastle.crypto.tls.ServerOnlyTlsAuthentication;
import org.spongycastle.crypto.tls.TlsAuthentication;
import org.spongycastle.crypto.tls.TlsClient;
import org.spongycastle.crypto.tls.TlsClientProtocol;
import org.spongycastle.crypto.tls.TlsExtensionsUtils;
import org.spongycastle.util.io.TeeInputStream;
import org.spongycastle.util.io.TeeOutputStream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Hashtable;

/**
 * Created by wohlfart on 11.08.16.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonClick(View v) {
        TLSTestTask myTask = new TLSTestTask();
        myTask.execute();

        System.out.println("clicked");
    }

    private class TLSTestTask extends AsyncTask<Void, MyTaskParams, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            publishProgress(new MyTaskParams("connecting...", null));

            try {
                Socket socket = new Socket("198.35.26.96", 443); // wikipedia.org
                //Socket socket = new Socket("192.168.6.214", 4433); // localhost
                publishProgress(new MyTaskParams("connected...", null));

                // setup tap
                ByteArrayOutputStream tap = new ByteArrayOutputStream();
                TeeOutputStream teeOut = new TeeOutputStream(socket.getOutputStream(), tap);
                TeeInputStream teeIn = new TeeInputStream(socket.getInputStream(), tap);

                java.security.SecureRandom secureRandom = new java.security.SecureRandom();
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
                    @Override
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
                byte[] hsBuffer = new byte[4096];
                hsBuffer = tap.toByteArray();
                String handshakeHexdump = byteArrayToHex(hsBuffer);
                DataInputStream distream = new DataInputStream(new ByteArrayInputStream(hsBuffer));
                Dissector tlsrec = new TLSStream(distream);
                //System.out.println(handshakeHexdump);

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
            return null;
        }

        @Override
        protected void onProgressUpdate(MyTaskParams... p) {
            if (p[0].tls_handshake != null) {
                ((TextView) findViewById(R.id.tview_tls_handshake)).setText(p[0].tls_handshake);
            }
            if (p[0].tcp_payload != null) {
                ((TextView) findViewById(R.id.tview_tcp_payload)).setText(p[0].tcp_payload);
            }
        }

        @Override
        protected void onPostExecute(Void v) {
            ((Button) findViewById(R.id.button)).setText("done");
            System.out.println("finished");
        }
    }

    private class MyTaskParams {
        String tls_handshake;
        String tcp_payload;

        MyTaskParams(String tls_handshake, String tcp_payload) {
            this.tls_handshake = tls_handshake;
            this.tcp_payload = tcp_payload;
        }
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
