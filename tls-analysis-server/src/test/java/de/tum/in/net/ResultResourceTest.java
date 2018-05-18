/**
 * Copyright © 2018 Johannes Schleger (johannes.schleger@tum.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tum.in.net;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.analysis.ProbedHostAnalysis;
import de.tum.in.net.analysis.TlsState;
import de.tum.in.net.client.HostAndPort;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsResult;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.services.MemoryOnlyDatabaseService;
import de.tum.in.net.util.ClientUtil;

public class ResultResourceTest {

  private HttpServer server;
  private WebTarget target;

  private static byte[] golemClient;
  private static byte[] golemServer;

  private static byte[] sslSplitClientSent;
  private static byte[] sslSplitClientReceived;
  private static byte[] sslSplitServerSent;
  private static byte[] sslSplitServerReceived;

  @BeforeClass
  public static void loadHandshakes() throws IOException {
    golemClient = FileUtils.readFileToByteArray(new File("exampleHandshakes/golem/client.raw"));
    golemServer = FileUtils.readFileToByteArray(new File("exampleHandshakes/golem/server.raw"));

    sslSplitClientSent =
        FileUtils.readFileToByteArray(new File("exampleHandshakes/sslsplit/client.sent.raw"));
    sslSplitClientReceived =
        FileUtils.readFileToByteArray(new File("exampleHandshakes/sslsplit/client.rec.raw"));
    sslSplitServerSent =
        FileUtils.readFileToByteArray(new File("exampleHandshakes/sslsplit/server.sent.raw"));
    sslSplitServerReceived =
        FileUtils.readFileToByteArray(new File("exampleHandshakes/sslsplit/server.rec.raw"));
  }

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
  public void noInterception() {
    TlsResult clientResult = new TlsResult("dst", golemServer, golemClient);
    TlsResult serverResult = new TlsResult("dst", golemClient, golemServer);

    HostAndPort hostAndPort = HostAndPort.parse("junit.org.xy");
    List<TlsClientServerResult> results =
        Arrays.asList(TlsClientServerResult.connected(hostAndPort, clientResult, serverResult));
    TlsTestResult testResult = new TlsTestResult(new NetworkId(), results);

    Response response =
        target.path("result").request().post(Entity.json(new Gson().toJson(testResult)));
    assertEquals(200, response.getStatus());

    String content = response.readEntity(String.class);
    AnalysisResult result = new Gson().fromJson(content, AnalysisResult.class);
    assertNotNull(result);

    List<ProbedHostAnalysis> analysisResults = result.getProbedHosts();
    assertNotNull(analysisResults);
    assertEquals(1, analysisResults.size());

    assertEquals(TlsState.NO_INTERCEPTION, analysisResults.get(0).getTlsState());
  }

  @Test
  public void interception() {
    TlsResult clientResult = new TlsResult("dst", sslSplitClientReceived, sslSplitClientSent);
    TlsResult serverResult = new TlsResult("dst", sslSplitServerReceived, sslSplitServerSent);

    HostAndPort hostAndPort = HostAndPort.parse("junit.org.xy");
    List<TlsClientServerResult> results =
        Arrays.asList(TlsClientServerResult.connected(hostAndPort, clientResult, serverResult));
    TlsTestResult testResult = new TlsTestResult(new NetworkId(), results);

    Response response =
        target.path("result").request().post(Entity.json(new Gson().toJson(testResult)));
    assertEquals(200, response.getStatus());

    String content = response.readEntity(String.class);
    AnalysisResult result = new Gson().fromJson(content, AnalysisResult.class);
    assertNotNull(result);

    List<ProbedHostAnalysis> analysisResults = result.getProbedHosts();
    assertNotNull(analysisResults);
    assertEquals(1, analysisResults.size());

    assertEquals(hostAndPort.toString(), analysisResults.get(0).getTarget().toString());
    assertEquals(TlsState.INTERCEPTION, analysisResults.get(0).getTlsState());

  }

  private List<AnalysisResult> getResult(Response response) {
    String content = response.readEntity(String.class);
    Type listType = new TypeToken<List<AnalysisResult>>() {}.getType();
    return new Gson().fromJson(content, listType);
  }
}
