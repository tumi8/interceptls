package de.tum.in.net;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.tum.in.net.model.ClientHandlerFactory;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.client.DefaultClientScenario;
import de.tum.in.net.scenario.server.BcTlsServerFactory;
import de.tum.in.net.scenario.server.DefaultClientHandlerFactory;
import de.tum.in.net.scenario.server.SimpleServerSocket;
import de.tum.in.net.server.MyResultListener;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by johannes on 31.03.17.
 */

public class DefaultClientScenarioTest {

    ExecutorService executor = Executors.newCachedThreadPool();

    @Test
    public void todo() throws Exception {
        final int port = 3843;
        final String transmit = "hello";

        final MyResultListener listener = new MyResultListener();
        final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory(), listener);
        final SimpleServerSocket socket = new SimpleServerSocket(port, fac, executor);
        executor.submit(socket);
        Thread.sleep(20);

        final DefaultClientScenario scenario = new DefaultClientScenario("127.0.0.1", port, transmit.getBytes());

        final ScenarioResult clientResult = scenario.call();

        while (listener.result == null) {
            Thread.sleep(20);
        }

        assertArrayEquals(clientResult.getReceivedBytes(), listener.result.getSentBytes());
        assertArrayEquals(clientResult.getSentBytes(), listener.result.getReceivedBytes());

    }
}
