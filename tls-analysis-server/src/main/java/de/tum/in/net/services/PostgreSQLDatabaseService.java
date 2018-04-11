package de.tum.in.net.services;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.analysis.NetworkStats;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsTestResult;

public class PostgreSQLDatabaseService implements DatabaseService {

  private static final Logger log = LoggerFactory.getLogger(PostgreSQLDatabaseService.class);
  private final String NEW_SESSION =
      "INSERT INTO SESSION (timestamp, interception, network_type, public_ip, default_gw_ip, default_gw_mac, dns_ip, dns_mac, bssid, ssid) "
          + "VALUES (now(), ?, ?::network, ?::INET, ?::INET, ?::macaddr, ?::INET, ?::macaddr, ?::macaddr, ?)";
  private final String INSERT_RESULT =
      "INSERT INTO RESULTS (session_id, test_id, client_ip, server_ip, client_sent, client_rec, server_sent, server_rec, target, target_port)"
          + " VALUES (?, ?, ?::INET, ?::INET, ?, ?, ?, ?, ?, ?)";
  private final String INTERCEPTION_RATE =
      "SELECT interception, COUNT(interception)*100.0 / count(*) over() as rate FROM SESSION WHERE network_type=?::network AND public_ip=?::INET GROUP BY interception";
  private final String TOTAL_COUNT =
      "SELECT COUNT(*) FROM SESSION WHERE network_type=?::network AND public_ip=?::INET";
  private BasicDataSource bds = new BasicDataSource();


  public PostgreSQLDatabaseService(String user, String password, String target) throws IOException {
    bds.setDefaultAutoCommit(false);
    bds.setUrl("jdbc:postgresql://" + target);
    bds.setUsername(user);
    bds.setPassword(password);

  }


  @Override
  public void addTestResult(TlsTestResult result) throws SQLException {

    try (Connection c = bds.getConnection()) {

      try {
        // insert new session and get id
        long sessionId = newSession(c, result);

        int testCounter = 0;
        for (TlsClientServerResult r : result.getClientServerResults()) {
          if (r.isSuccess()) {
            testCounter++;
            PreparedStatement stmt =
                c.prepareStatement(INSERT_RESULT, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, sessionId);
            stmt.setInt(2, testCounter);
            stmt.setString(3, r.getServerResult().getDestination());
            stmt.setString(4, r.getClientResult().getDestination());
            stmt.setBytes(5, r.getClientResult().getSentBytesRaw());
            stmt.setBytes(6, r.getClientResult().getReceivedBytesRaw());
            stmt.setBytes(7, r.getServerResult().getSentBytesRaw());
            stmt.setBytes(8, r.getServerResult().getReceivedBytesRaw());
            stmt.setString(9, r.getHostAndPort().getHost());
            stmt.setInt(10, r.getHostAndPort().getPort());

            stmt.executeUpdate();
          } else {
            // we do not care about unsuccessful connections at the moment
          }
        }
        c.commit();
      } catch (SQLException e) {
        c.rollback();
        throw e;
      }
    }
  }

  private long newSession(Connection c, TlsTestResult result) throws SQLException {
    PreparedStatement s = c.prepareStatement(NEW_SESSION, Statement.RETURN_GENERATED_KEYS);
    s.setBoolean(1, result.anyInterception());
    NetworkId network = result.getNetworkId();
    s.setString(2, network.getType().toString());
    s.setString(3, network.getPublicIp());
    s.setString(4, network.getDefaultGatewayIp());
    s.setString(5, network.getDefaultGatewayMac());
    s.setString(6, network.getDnsIp());
    s.setString(7, network.getDnsMac());
    s.setString(8, network.getBssid());
    s.setString(9, network.getSsid());
    s.executeUpdate();
    ResultSet set = s.getGeneratedKeys();
    set.next();

    return set.getLong(1);
  }

  @Override
  public void close() throws IOException {
    try {
      bds.close();
    } catch (SQLException e) {
      throw new IOException("Could not close database.", e);
    }
  }


  @Override
  public NetworkStats getNetworkStats(NetworkId network) throws SQLException {
    NetworkStats stats = new NetworkStats();
    try (Connection c = bds.getConnection()) {
      try {
        // get total count
        PreparedStatement s = c.prepareStatement(TOTAL_COUNT);
        setNetworkParams(s, network);
        ResultSet r = s.executeQuery();
        r.next();
        int countTotal = r.getInt("count");
        stats.setCountTotal(countTotal);

        // get rate
        s = c.prepareStatement(INTERCEPTION_RATE);
        setNetworkParams(s, network);
        ResultSet set = s.executeQuery();

        while (set.next()) {
          boolean intercepted = set.getBoolean("interception");
          if (intercepted) {
            stats.setInterceptionRateTotal(set.getFloat("rate"));
          }
        }

        return stats;
      } catch (SQLException e) {
        throw e;
      }
    }
  }


  private void setNetworkParams(PreparedStatement s, NetworkId network) throws SQLException {
    s.setString(1, network.getType().toString());
    s.setString(2, network.getPublicIp());
  }

}
