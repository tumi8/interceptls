package de.tum.in.net;

import org.junit.Test;

import de.tum.in.net.scenario.DefaultScenario;
import de.tum.in.net.scenario.ScenarioResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by johannes on 31.03.17.
 */

public class DefaultScenarioTest {

    @Test
    public void todo() {
        int port = 3843;
        String transmit = "hello";

        ServerRunnable server = new ServerRunnable(port, transmit.length());
        Thread serverThread = new Thread(server, "Server");
        serverThread.start();


        DefaultScenario scenario = new DefaultScenario("127.0.0.1", port, transmit.getBytes());
        scenario.run();

        ScenarioResult result = scenario.getResult();
        assertTrue(result.isSuccess());
        assertEquals(transmit, new String(server.getReceivedBytes()));

    }
}
