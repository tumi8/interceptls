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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsResult;
import de.tum.in.net.server.BcTlsServerFactory;
import de.tum.in.net.server.ClientHandlerFactory;
import de.tum.in.net.server.DefaultClientHandlerFactory;
import de.tum.in.net.server.SimpleServerSocket;
import de.tum.in.net.util.ServerUtil;

/**
 * Created by johannes on 31.03.17.
 */
public class DefaultHttpsScenarioTest {

  private final ExecutorService executor = Executors.newCachedThreadPool();

  @Test
  public void testOK() throws Exception {
    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory());
    try (final SimpleServerSocket socket = new SimpleServerSocket(0, fac, executor)) {
      executor.submit(socket);
      ServerUtil.waitForRunning(socket);

      HostAndPort target = new HostAndPort("127.0.0.1", socket.getLocalPort());
      final DefaultHttpsScenario scenario = new DefaultHttpsScenario(target);

      final TlsClientServerResult result = scenario.call();
      TlsResult clientResult = result.getClientResult();
      TlsResult serverResult = result.getServerResult();


      assertTrue(result.isSuccess());

      // the sent bytes must equal the received bytes
      assertArrayEquals(clientResult.getReceivedBytesRaw(), serverResult.getSentBytesRaw());
      assertArrayEquals(clientResult.getSentBytesRaw(), serverResult.getReceivedBytesRaw());

    }

  }

}
