package de.tum.in.net;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import de.tum.in.net.scenario.Node;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.ScenarioResult.ScenarioResultBuilder;
import de.tum.in.net.services.MapDBDatabaseService;
import de.tum.in.net.util.ClientUtil;

public class ResultResourceTest {

  private HttpServer server;
  private WebTarget target;

  @Before
  public void setUp() throws Exception {
    AnalysisServerConfig conf = AnalysisServerConfig.loadDefault();
    server = Main.startServer(conf, new MapDBDatabaseService(false));

    Client c = ClientUtil.createDefaultTLSClient(conf);

    // uncomment the following line if you want to enable
    // support for JSON in the client (you also have to uncomment
    // dependency on jersey-media-json module in pom.xml and Main.startServer())
    // --
    // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

    target = c.target(conf.getURI());
  }

  @After
  public void tearDown() throws Exception {
    server.stop();
  }


  @Test
  public void uploadAndGetResult() {
    ScenarioResult result = new ScenarioResultBuilder(Node.CLIENT, "source", "dst")
        .sent(new byte[1]).received(new byte[1]).connected();

    Response response =
        target.path("result/a-1").request().put(Entity.json(new Gson().toJson(result)));
    assertEquals(204, response.getStatus());

    Response response2 = target.path("result/a-1").request().get();
    assertEquals(200, response2.getStatus());

    TestResult result2 = new Gson().fromJson(response2.readEntity(String.class), TestResult.class);
    assertTrue(result2.hasClientResult());
    assertEquals(result.getSource(), result2.getClientResult().getSource());
    assertEquals(result.isSuccess(), result2.getClientResult().isSuccess());


  }
}
