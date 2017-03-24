import com.example.demotlsclient.AsyncResult;
import com.example.demotlsclient.SniTlsClient;
import com.example.demotlsclient.scenario.ScenarioDirectConnection;
import com.example.demotlsclient.scenario.ScenarioResult;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spongycastle.crypto.tls.Certificate;
import org.spongycastle.crypto.tls.DefaultTlsClient;
import org.spongycastle.crypto.tls.ServerOnlyTlsAuthentication;
import org.spongycastle.crypto.tls.TlsAuthentication;
import org.spongycastle.crypto.tls.TlsClient;

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
