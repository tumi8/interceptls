package de.tum.in.net.services;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.tum.in.net.client.HostAndPort;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsResult;
import de.tum.in.net.model.TlsTestResult;

public class PostgreSQLDatabaseServiceTest {

  @Test
  @Ignore
  public void simple() throws Exception {
    TlsResult client = new TlsResult("192.168.0.1", new byte[1], new byte[1]);
    TlsResult server = new TlsResult("192.168.0.1", new byte[1], new byte[1]);

    HostAndPort hostAndPort = HostAndPort.parse("junit.org.xy");
    List<TlsClientServerResult> results =
        Arrays.asList(TlsClientServerResult.connected(hostAndPort, client, server));
    TlsTestResult result = new TlsTestResult(new NetworkId(), results);

    try (DatabaseService db = new PostgreSQLDatabaseService("johannes", "", "localhost:5432/tls")) {
      db.addTestResult(result);

    }

  }

}
