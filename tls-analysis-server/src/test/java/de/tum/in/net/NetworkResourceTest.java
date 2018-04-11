package de.tum.in.net;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import de.tum.in.net.analysis.NetworkStats;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.NetworkType;
import de.tum.in.net.services.MemoryOnlyDatabaseService;
import de.tum.in.net.util.ClientUtil;

public class NetworkResourceTest {

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
   * This does not test the database functionality, only the correct parsing of request and
   * response.
   */
  @Test
  public void getNetworkStats() throws SQLException {

    NetworkId network = new NetworkId();
    network.setType(NetworkType.WIFI);
    network.setPublicIp("127.0.0.1");

    Response response =
        target.path("network").request().post(Entity.json(new Gson().toJson(network)));
    assertEquals(200, response.getStatus());

    String content = response.readEntity(String.class);
    NetworkStats stats = new Gson().fromJson(content, NetworkStats.class);
    assertNotNull(stats);


  }
}
