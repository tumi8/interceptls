package de.tum.in.net;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.tum.in.net.model.ClientHandlerFactory;
import de.tum.in.net.model.Severity;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.client.DefaultClientScenario;
import de.tum.in.net.scenario.server.BcTlsServerFactory;
import de.tum.in.net.scenario.server.DefaultClientHandlerFactory;
import de.tum.in.net.scenario.server.SimpleServerSocket;
import de.tum.in.net.server.MyResultListener;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by johannes on 31.03.17.
 */

public class DefaultClientScenarioTest {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Test
    public void testOK() throws Exception {
        final int port = 3843;
        final String transmit = Severity.OK.toString();

        final MyResultListener listener = new MyResultListener();
        final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory(), listener);
        try (final SimpleServerSocket socket = new SimpleServerSocket(port, fac, executor)) {
            executor.submit(socket);
            Thread.sleep(20);

            final DefaultClientScenario scenario = new DefaultClientScenario("127.0.0.1", port, transmit.getBytes());

            final ScenarioResult clientResult = scenario.call();

            while (listener.severity == null) {
                Thread.sleep(20);
            }
            assertTrue(Severity.OK.equals(listener.severity));
        }

    }

    @Test
    public void testNOT_OK() throws Exception {
        final int port = 3843;
        final String transmit = Severity.NOT_OK.toString();

        final MyResultListener listener = new MyResultListener();
        final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory(), listener);
        try (final SimpleServerSocket socket = new SimpleServerSocket(port, fac, executor)) {
            executor.submit(socket);
            Thread.sleep(20);

            final DefaultClientScenario scenario = new DefaultClientScenario("127.0.0.1", port, transmit.getBytes());

            final ScenarioResult clientResult = scenario.call();

            while (listener.severity == null) {
                Thread.sleep(20);
            }
            assertTrue(Severity.NOT_OK.equals(listener.severity));

            //the sent bytes must equal the received bytes
            assertArrayEquals(clientResult.getReceivedBytes(), listener.result.getSentBytes());
            assertArrayEquals(clientResult.getSentBytes(), listener.result.getReceivedBytes());
        }
        
    }
}
