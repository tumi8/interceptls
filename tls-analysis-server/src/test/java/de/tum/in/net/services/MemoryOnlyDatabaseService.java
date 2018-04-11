package de.tum.in.net.services;

import java.io.IOException;
import java.sql.SQLException;

import de.tum.in.net.analysis.NetworkStats;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.TlsTestResult;

public class MemoryOnlyDatabaseService implements DatabaseService {

  @Override
  public void addTestResult(TlsTestResult result) {
    // stub
  }

  @Override
  public void close() throws IOException {
    // stub
  }

  @Override
  public NetworkStats getNetworkStats(NetworkId networkId) throws SQLException {
    // stub
    return new NetworkStats();
  }

}
