package de.tum.in.net;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import de.tum.in.net.services.MapDBDatabaseService;
import de.tum.in.net.session.SessionID;
import de.tum.in.net.util.ClientUtil;

public class SessionResourceTest {

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

  /**
   * Test to see that the message "Got it!" is sent in the response.
   */
  @Test
  public void createNewSessionID() {
    String responseMsg = target.path("session").request().post(null, String.class);
    SessionID id1 = new Gson().fromJson(responseMsg, SessionID.class);

    String responseMsg2 = target.path("session").request().post(null, String.class);
    SessionID id2 = new Gson().fromJson(responseMsg2, SessionID.class);

    assertThat(id1, is(not(id2)));
  }
}
