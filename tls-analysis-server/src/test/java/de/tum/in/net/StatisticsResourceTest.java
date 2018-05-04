package de.tum.in.net;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import de.tum.in.net.analysis.GeneralStatistic;
import de.tum.in.net.services.MemoryOnlyDatabaseService;
import de.tum.in.net.util.ClientUtil;

public class StatisticsResourceTest {

  private HttpServer server;
  private WebTarget target;

  @Before
  public void setUp() throws Exception {
    AnalysisServerConfig conf = AnalysisServerConfig.loadDefault();
    server = AnalysisServerMain.startServer(conf, new MemoryOnlyDatabaseService());

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


  /*
   * This does not test the database functionality, only the correct parsing of the response.
   */
  @Test
  public void getGeneralStats() throws SQLException {

    Response response = target.path("statistic").request().get();
    assertEquals(200, response.getStatus());

    String content = response.readEntity(String.class);
    GeneralStatistic stats = new Gson().fromJson(content, GeneralStatistic.class);
    assertNotNull(stats);

  }
}
