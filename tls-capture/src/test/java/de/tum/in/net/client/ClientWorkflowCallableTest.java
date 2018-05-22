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
package de.tum.in.net.client;

import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.tum.in.net.client.network.JavaNetworkIdentifier;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.server.BcTlsServerFactory;
import de.tum.in.net.server.ClientHandlerFactory;
import de.tum.in.net.server.DefaultClientHandlerFactory;
import de.tum.in.net.server.SimpleServerSocket;
import de.tum.in.net.util.ServerUtil;

public class ClientWorkflowCallableTest {

  private final int port = 53847;
  private final ExecutorService exec = Executors.newSingleThreadExecutor();
  private SimpleServerSocket srv;


  @Before
  public void startServer() throws Exception {
    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory());

    srv = new SimpleServerSocket(port, fac, exec);
    final Thread t = new Thread(srv);
    t.start();

    ServerUtil.waitForRunning(srv);
  }

  @After
  public void shutdownServer() throws Exception {
    if (srv != null)
      srv.close();
  }

  @Test
  public void simple() throws Exception {
    List<HostAndPort> targets = Arrays.asList(new HostAndPort("127.0.0.1", port));

    ClientWorkflowCallable c = new ClientWorkflowCallable(targets, new JavaNetworkIdentifier());
    TlsTestResult result = c.call();

    assertFalse(result.anyInterception());

  }



}
