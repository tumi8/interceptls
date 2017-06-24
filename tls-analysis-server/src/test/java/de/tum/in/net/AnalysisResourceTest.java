package de.tum.in.net;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import de.tum.in.net.model.AnalysisResult;
import de.tum.in.net.model.AnalysisResultType;
import de.tum.in.net.scenario.Node;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.ScenarioResult.ScenarioResultBuilder;
import de.tum.in.net.services.MapDBDatabaseService;
import de.tum.in.net.util.ClientUtil;

public class AnalysisResourceTest {

  private HttpServer server;
  private WebTarget target;

  private static byte[] golemClient;
  private static byte[] golemServer;

  @BeforeClass
  public static void loadHandshakes() throws IOException {
    golemClient = FileUtils.readFileToByteArray(new File("exampleHandshakes/golem/client.raw"));
    golemServer = FileUtils.readFileToByteArray(new File("exampleHandshakes/golem/server.raw"));
  }

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
  public void analysis() {
    ScenarioResult clientResult = new ScenarioResultBuilder(Node.CLIENT, "source", "dst")
        .sent(golemClient).received(golemServer).connected();
    ScenarioResult serverResult = new ScenarioResultBuilder(Node.SERVER, "source", "dst")
        .sent(golemServer).received(golemClient).connected();

    Response response =
        target.path("result/a-1").request().put(Entity.json(new Gson().toJson(clientResult)));
    assertEquals(204, response.getStatus());
    response =
        target.path("result/a-1").request().put(Entity.json(new Gson().toJson(serverResult)));
    assertEquals(204, response.getStatus());


    Response response2 = target.path("analysis/a-1").request().get();
    assertEquals(200, response2.getStatus());

    AnalysisResult result = getResult(response2);
    assertNotNull(result);
    assertEquals(AnalysisResultType.NO_INTERCEPTION, result.getType());

  }

  @Test
  public void noServerData() {
    ScenarioResult clientResult = new ScenarioResultBuilder(Node.CLIENT, "source", "dst")
        .sent(golemClient).received(golemServer).connected();

    Response response =
        target.path("result/a-1").request().put(Entity.json(new Gson().toJson(clientResult)));
    assertEquals(204, response.getStatus());


    Response response2 = target.path("analysis/a-1").request().get();
    assertEquals(200, response2.getStatus());

    AnalysisResult result = getResult(response2);
    assertNotNull(result);
    assertEquals(AnalysisResultType.NO_SERVER_RESULT, result.getType());

  }

  @Test
  public void noClientData() {
    ScenarioResult serverResult = new ScenarioResultBuilder(Node.SERVER, "source", "dst")
        .sent(golemServer).received(golemClient).connected();

    Response response =
        target.path("result/a-1").request().put(Entity.json(new Gson().toJson(serverResult)));
    assertEquals(204, response.getStatus());

    Response response2 = target.path("analysis/a-1").request().get();
    assertEquals(200, response2.getStatus());

    AnalysisResult result = getResult(response2);
    assertNotNull(result);
    assertEquals(AnalysisResultType.NO_CLIENT_RESULT, result.getType());

  }

  @Test
  public void noDataAtAll() {

    Response response = target.path("analysis/a-1").request().get();
    assertEquals(200, response.getStatus());

    AnalysisResult result = getResult(response);
    assertNotNull(result);
    assertEquals(AnalysisResultType.NO_CLIENT_NO_SERVER_RESULT, result.getType());

  }

  private AnalysisResult getResult(Response response) {
    String content = response.readEntity(String.class);
    return new Gson().fromJson(content, AnalysisResult.class);
  }
}
