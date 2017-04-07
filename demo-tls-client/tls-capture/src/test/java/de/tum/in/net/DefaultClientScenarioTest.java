package de.tum.in.net;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.client.DefaultClientScenario;
import de.tum.in.net.scenario.server.DefaultServerScenario;

import static org.junit.Assert.assertEquals;

/**
 * Created by johannes on 31.03.17.
 */

public class DefaultClientScenarioTest {

    @Test
    public void todo() throws Exception {
        int port = 3843;
        String transmit = "hello";

        DefaultServerScenario serverScenario = new DefaultServerScenario(port, transmit.length());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ScenarioResult> serverResult = executor.submit(serverScenario);


        DefaultClientScenario scenario = new DefaultClientScenario("127.0.0.1", port, transmit.getBytes());

        ScenarioResult clientResult = scenario.call();

        while (!serverResult.isDone()) {
            Thread.sleep(20);
        }
        assertEquals(transmit, new String(serverScenario.getReceivedBytes()));

    }
}
