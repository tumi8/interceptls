package de.tum.in.net;

import org.junit.Test;

import de.tum.in.net.scenario.client.DefaultClientScenario;
import de.tum.in.net.scenario.server.DefaultServerScenario;
import de.tum.in.net.scenario.ScenarioResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by johannes on 31.03.17.
 */

public class DefaultClientScenarioTest {

    @Test
    public void todo() throws Exception {
        int port = 3843;
        String transmit = "hello";

        DefaultServerScenario serverScenario = new DefaultServerScenario(port, transmit.length());
        Thread serverThread = new Thread(serverScenario, "Server");
        serverThread.start();

        DefaultClientScenario scenario = new DefaultClientScenario("127.0.0.1", port, transmit.getBytes());
        scenario.run();

        ScenarioResult result = scenario.getResult();
        assertTrue(result.isSuccess());
        while (serverThread.isAlive()) {
            Thread.sleep(20);
        }
        assertEquals(transmit, new String(serverScenario.getReceivedBytes()));

    }
}
