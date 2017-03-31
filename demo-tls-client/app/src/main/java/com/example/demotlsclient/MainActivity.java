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

import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClient;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.crypto.tls.TlsExtensionsUtils;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

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
import java.security.SecureRandom;
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

        AsyncResult<MyTaskParams, String> asyncResult = new AsyncResult<MyTaskParams, String>() {
            @Override
            public void publishProgress(MyTaskParams p){
                if (p.tls_handshake != null) {
                    ((TextView) findViewById(R.id.tview_tls_handshake)).setText(p.tls_handshake);
                }
                if (p.tcp_payload != null) {
                    ((TextView) findViewById(R.id.tview_tcp_payload)).setText(p.tcp_payload);
                }
            }

            @Override
            public void publishResult(String result) {
                ((Button) findViewById(R.id.button)).setText("done");
            }
        };

        TLSTask myTask = new TLSTask(asyncResult);
        myTask.execute();


        System.out.println("clicked");
    }




}
