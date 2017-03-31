package de.tum.net.in.demotlsclient;

import de.tum.net.in.demotlsclient.AsyncResult;
import de.tum.net.in.demotlsclient.scenario.ScenarioDirectConnection;
import de.tum.in.net.scenario.ScenarioResult;

import static org.junit.Assert.*;
import org.junit.Test;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClient;

import java.io.IOException;

/**
 * Created by johannes on 22.03.17.
 */
//@RunWith(AndroidJUnit4.class)
public class ScenarioDirectConnectionTest {

    private AsyncResult<Void, ScenarioResult> publisher = new AsyncResult<Void, ScenarioResult>() {
        @Override
        public void publishProgress(Void progress) {

        }

        @Override
        public void publishResult(ScenarioResult result) {

        }
    };

    private TlsClient client = new DefaultTlsClient() {
        @Override
        public TlsAuthentication getAuthentication() throws IOException {
            return new ServerOnlyTlsAuthentication() {
                @Override
                public void notifyServerCertificate(Certificate serverCertificate) throws IOException {

                }
            };
        }
    };

    @Test
    public void simple() throws Exception{

        ScenarioDirectConnection test = new ScenarioDirectConnection(publisher, "www.wikipedia.org", 443, client);
        ScenarioResult result = test.doInBackground();

        assertTrue(result.isSuccess());
        
    }

}
