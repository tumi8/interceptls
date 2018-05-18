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
package de.tum.in.net.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import de.tum.in.net.client.DefaultHttpsScenario;
import de.tum.in.net.client.HostAndPort;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.util.ServerUtil;

/**
 * Created by johannes on 17.05.17.
 */

public class SimpleServerSocketTest {

  private int port = 52152;
  final ExecutorService exec = Executors.newSingleThreadExecutor();
  final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory());

  @Test(expected = IllegalStateException.class)
  public void cannotStopIfNotStarted() throws Exception {
    final SimpleServerSocket srv = new SimpleServerSocket(port, fac, exec);
    srv.close();
  }

  @Test
  public void canStartAndStop() throws Exception {
    final SimpleServerSocket srv = new SimpleServerSocket(port, fac, exec);
    assertFalse(srv.isRunning());

    final Thread srvThread = new Thread(srv);
    srvThread.start();
    ServerUtil.waitForRunning(srv);

    assertTrue(srv.isRunning());

    srv.close();
    assertFalse(srv.isRunning());
  }

  @Test
  public void tlsClientServerTest() throws Exception {
    final ExecutorService exec = Executors.newCachedThreadPool();
    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory());

    try (final SimpleServerSocket srv = new SimpleServerSocket(port, fac, exec)) {
      final Thread t = new Thread(srv);
      t.start();

      ServerUtil.waitForRunning(srv);
      assertTrue(srv.isRunning());


      HostAndPort target = new HostAndPort("127.0.0.1", port);
      TlsClientServerResult result = new DefaultHttpsScenario(target).call();

      assertNotNull(result.getClientResult());
      assertNotNull(result.getServerResult());

    }


  }
}
