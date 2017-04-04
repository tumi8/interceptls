package de.tum.net.in.demotlsclient;

import android.os.AsyncTask;
import android.util.JsonWriter;

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
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.net.in.demotlsclient.StreamDissector.Dissector;
import de.tum.net.in.demotlsclient.StreamDissector.TLSStream;

/**
 * Created by johannes on 22.03.17.
 */

public class AsyncScenarioTask extends AsyncTask<Void, Void, ScenarioResult> {

    private final Scenario scenario;
    private final AsyncResult<ScenarioResult> delegate;

    public AsyncScenarioTask(Scenario scenario, AsyncResult<ScenarioResult> delegate) {
        this.scenario = scenario;
        this.delegate = delegate;
    }

    @Override
    protected ScenarioResult doInBackground(Void... voids) {
        return scenario.call();
    }

    @Override
    protected void onPostExecute(ScenarioResult v) {
        delegate.publishResult(v);
    }
}

