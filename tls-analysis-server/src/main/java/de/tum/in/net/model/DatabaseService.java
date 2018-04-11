package de.tum.in.net.model;

import java.io.Closeable;
import java.sql.SQLException;

import de.tum.in.net.analysis.NetworkStats;

public interface DatabaseService extends Closeable {

  void addTestResult(TlsTestResult result) throws SQLException;

  NetworkStats getNetworkStats(NetworkId networkId) throws SQLException;
}
