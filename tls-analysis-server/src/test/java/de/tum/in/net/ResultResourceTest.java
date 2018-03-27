package de.tum.in.net;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import de.tum.in.net.client.HostAndPort;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsResult;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.services.MemoryOnlyDatabaseService;
import de.tum.in.net.session.SessionID;
import de.tum.in.net.util.ClientUtil;

public class ResultResourceTest {

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


  @Test
  public void uploadAndGetResult() {
    TlsResult client = new TlsResult("dst", new byte[1], new byte[1]);
    TlsResult server = new TlsResult("dst", new byte[1], new byte[1]);

    HostAndPort hostAndPort = new HostAndPort("junit.org.xy");
    List<TlsClientServerResult> results =
        Arrays.asList(TlsClientServerResult.connected(hostAndPort, client, server));
    TlsTestResult testResult = new TlsTestResult(new NetworkId(), results);

    Response response =
        target.path("result").request().post(Entity.json(new Gson().toJson(testResult)));
    assertEquals(200, response.getStatus());

    String content = response.readEntity(String.class);
    SessionID id = new Gson().fromJson(content, SessionID.class);
    assertNotNull(id);


  }
}
