package de.tum.in.net.services;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.session.SessionID;

public class PostgreSQLDatabaseService implements DatabaseService {

  private static final Logger log = LoggerFactory.getLogger(PostgreSQLDatabaseService.class);
  private final String NEW_SESSION =
      "INSERT INTO SESSION (timestamp, interception, network_type, public_ip, dns_ip, dns_mac, bssid, ssid) VALUES (now(), ?, ?::network, ?::INET, ?::INET, ?::macaddr, ?::macaddr, ?)";
  private final String INSERT_RESULT =
      "INSERT INTO RESULTS (session_id, test_id, server_ip, client_sent, client_rec, server_sent, server_rec, target, target_port)"
          + " VALUES (?, ?, ?::INET, ?, ?, ?, ?, ?, ?)";
  private BasicDataSource bds = new BasicDataSource();


  public PostgreSQLDatabaseService(String user, String password, String target) throws IOException {
    bds.setDefaultAutoCommit(false);
    bds.setUrl("jdbc:postgresql://" + target);
    bds.setUsername(user);
    bds.setPassword(password);

  }


  @Override
  public SessionID addTestResult(TlsTestResult result) throws SQLException {

    try (Connection c = bds.getConnection()) {

      try {
        // insert new session and get id
        long sessionId = newSession(result);

        int testCounter = 0;
        for (TlsClientServerResult r : result.getClientServerResults()) {
          if (r.isSuccess()) {
            testCounter++;
            PreparedStatement stmt =
                c.prepareStatement(INSERT_RESULT, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, sessionId);
            stmt.setInt(2, testCounter);
            stmt.setString(3, r.getClientResult().getDestination());
            stmt.setBytes(4, r.getClientResult().getSentBytesRaw());
            stmt.setBytes(5, r.getClientResult().getReceivedBytesRaw());
            stmt.setBytes(6, r.getServerResult().getSentBytesRaw());
            stmt.setBytes(7, r.getServerResult().getReceivedBytesRaw());
            stmt.setString(8, r.getHostAndPort().getHost());
            stmt.setInt(9, r.getHostAndPort().getPort());

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

    return null;

  }

  private long newSession(TlsTestResult result) throws SQLException {
    try (Connection c = bds.getConnection()) {
      try {
        PreparedStatement s = c.prepareStatement(NEW_SESSION, Statement.RETURN_GENERATED_KEYS);
        s.setBoolean(1, result.anyInterception());
        NetworkId network = result.getNetworkId();
        s.setString(2, network.getType().toString());

        Set<String> publicIps = result.getClientServerResults().stream()
            .map(r -> r.getServerResult().getDestination()).collect(Collectors.toSet());

        if (publicIps.size() == 0) {
          throw new SQLException("No public IP address found in test results.");
        } else if (publicIps.size() > 1) {
          log.warn("Found more than one public IP address.");
        }
        s.setString(3, publicIps.iterator().next());
        s.setString(4, network.getDnsIp());
        s.setString(5, network.getDnsMac());
        s.setString(6, network.getBssid());
        s.setString(7, network.getSsid());
        s.executeUpdate();
        ResultSet set = s.getGeneratedKeys();
        set.next();

        c.commit();
        return set.getLong(1);
      } catch (SQLException e) {
        c.rollback();
        throw e;
      }
    }

  }

  @Override
  public TlsTestResult getResult(SessionID id) {
    return null;
  }

  @Override
  public void close() throws IOException {
    try {
      bds.close();
    } catch (SQLException e) {
      throw new IOException("Could not close database.", e);
    }
  }

}
