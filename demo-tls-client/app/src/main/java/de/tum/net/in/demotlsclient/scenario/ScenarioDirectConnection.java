package de.tum.net.in.demotlsclient.scenario;

import android.os.AsyncTask;


import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.Tap;
import de.tum.net.in.demotlsclient.AsyncResult;

import org.bouncycastle.crypto.tls.TlsClient;
import org.bouncycastle.crypto.tls.TlsClientProtocol;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;

/**
 * Created by johannes on 22.03.17.
 */

public class ScenarioDirectConnection extends AsyncTask<Void, Void, ScenarioResult> {

    private final AsyncResult<Void, ScenarioResult> delegate;
    private final String destination;
    private final int port;
    private final TlsClient client;

    public ScenarioDirectConnection(AsyncResult<Void, ScenarioResult> delegate, String destination, int port, TlsClient client){
        this.delegate = delegate;
        this.destination = destination;
        this.port = port;
        this.client = client;
    }

    @Override
    public ScenarioResult doInBackground(Void... p){

        try(Socket s = new Socket(destination, port)){

            Tap tap = new Tap(s.getInputStream(), s.getOutputStream());

            //connect in blocking mode
            TlsClientProtocol tlsClientProtocol = new TlsClientProtocol(tap.getIn(), tap.getOut(), new SecureRandom());
            tlsClientProtocol.connect(client);

            //we are now connected, therefore we can publish the captured bytes
            return new ScenarioResult(tap.getInputBytes(), tap.getOutputytes());

        }catch (IOException e){
            return new ScenarioResult("Error in connection to " + destination, e);
        }

    }

    @Override
    protected void onPostExecute(ScenarioResult r) {
        delegate.publishResult(r);
    }

}
