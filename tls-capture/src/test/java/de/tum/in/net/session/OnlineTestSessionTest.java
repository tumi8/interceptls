package de.tum.in.net.session;

import org.junit.Ignore;
import org.junit.Test;

import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.Node;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.ScenarioResult.ScenarioResultBuilder;

/**
 * Created by johannes on 13.04.17.
 */

public class OnlineTestSessionTest {

  @Test
  @Ignore // this test requires the analysis server to be running locally
  public void test() throws Exception {
    final TestSession session = new OnlineTestSession("http://127.0.0.1:3000");
    final ScenarioResult result = new ScenarioResultBuilder(Node.CLIENT, "source", "destination")
        .sent(new byte[5]).received(new byte[5]).connected();
    session.uploadHandshake(5, result);
  }
}
