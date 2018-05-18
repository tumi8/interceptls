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
package de.tum.in.net.services;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.analysis.GeneralStatistic;
import de.tum.in.net.analysis.NetworkStats;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.IpAndMac;
import de.tum.in.net.model.Location;
import de.tum.in.net.model.MiddleboxCharacterization;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.NetworkType;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsTestResult;

public class PostgreSQLDatabaseService implements DatabaseService {

  private static final Logger log = LoggerFactory.getLogger(PostgreSQLDatabaseService.class);
  private static final String NEW_SESSION =
      "INSERT INTO SESSION (timestamp, interception, network_type, public_ip, default_gw_ip, default_gw_mac, bssid, ssid, longitude, latitude) "
          + "VALUES (now(), ?, ?::network, ?::INET, ?::INET, ?::macaddr, ?::macaddr, ?, ?, ?)";
  private static final String NEW_CHARACTERIZATION =
      "INSERT INTO CHARACTERIZATION (session_id, can_connect_wrong_http_host, can_connect_wrong_sni, ssl_v3, tls_v10, tls_v11, tls_v12) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?)";
  private static final String NEW_DNS =
      "INSERT INTO DNS (session_id, ip, mac) VALUES (?, ?::INET, ?::macaddr)";
  private static final String INSERT_RESULT =
      "INSERT INTO RESULTS (session_id, test_id, client_ip, server_ip, client_sent, client_rec, server_sent, server_rec, target, target_port)"
          + " VALUES (?, ?, ?::INET, ?::INET, ?, ?, ?, ?, ?, ?)";

  private static final String TOTAL_COUNT = "SELECT COUNT(*) FROM SESSION WHERE %s";
  private static final String INTERCEPTION_RATE =
      "SELECT interception, round(count(*) * 100.0 / sum(count(*)) over(), 1) as rate "
          + "FROM SESSION WHERE %s GROUP BY interception";

  private static final String TOTAL_TEST_COUNT = "SELECT COUNT(*) FROM SESSION";
  private static final String TOTAL_INTERCEPTION_COUNT =
      "SELECT COUNT(*) FROM SESSION WHERE interception=true";

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
        insertDns(c, sessionId, result.getNetworkId().getDns());
        if (result.anyInterception()) {
          MiddleboxCharacterization characterization = result.getMiddleboxCharacterization();
          insertCharacterization(c, sessionId, characterization);
        }

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

  private void insertDns(Connection c, long sessionId, List<IpAndMac> dnsList) throws SQLException {
    for (IpAndMac dns : dnsList) {
      PreparedStatement s = c.prepareStatement(NEW_DNS);
      s.setLong(1, sessionId);
      s.setString(2, dns.getIp());
      s.setString(3, dns.getMac());
      s.executeUpdate();
    }
  }


  private void insertCharacterization(Connection c, long sessionId,
      MiddleboxCharacterization characterization) throws SQLException {
    PreparedStatement s = c.prepareStatement(NEW_CHARACTERIZATION);
    s.setLong(1, sessionId);
    s.setBoolean(2, characterization.getCanConnectWrongHttpHost());
    s.setBoolean(3, characterization.getCanConnectWrongSni());
    s.setBoolean(4, characterization.isSslV3());
    s.setBoolean(5, characterization.isTlsV10());
    s.setBoolean(6, characterization.isTlsV11());
    s.setBoolean(7, characterization.isTlsV12());
    s.executeUpdate();
  }


  private long newSession(Connection c, TlsTestResult result) throws SQLException {
    PreparedStatement s = c.prepareStatement(NEW_SESSION, Statement.RETURN_GENERATED_KEYS);
    s.setBoolean(1, result.anyInterception());
    NetworkId network = result.getNetworkId();
    s.setString(2, network.getType().toString());
    s.setString(3, network.getPublicIp());
    s.setString(4, network.getDefaultGatewayIp());
    s.setString(5, network.getDefaultGatewayMac());
    s.setString(6, network.getBssid());
    s.setString(7, network.getSsid());
    Location l = network.getLocation();
    if (l == null) {
      s.setNull(8, Types.DOUBLE);
      s.setNull(9, Types.DOUBLE);
    } else {
      s.setDouble(8, l.getLongitude());
      s.setDouble(9, l.getLatitude());
    }

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
        PreparedStatement s = withNetworkSelection(c, TOTAL_COUNT, network);
        ResultSet r = s.executeQuery();
        r.next();
        int countTotal = r.getInt("count");
        stats.setCountTotal(countTotal);

        // get rate
        s = withNetworkSelection(c, INTERCEPTION_RATE, network);
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


  private PreparedStatement withNetworkSelection(Connection c, String query, NetworkId network)
      throws SQLException {
    // required params for all networks
    String selection = " network_type=?::network AND public_ip=?::INET AND default_gw_ip=?::INET ";

    // network specific params
    if (NetworkType.WIFI.equals(network.getType())) {
      selection += "AND default_gw_mac=?::macaddr AND bssid=?::macaddr AND ssid=?";
    } else if (NetworkType.ETHERNET.equals(network.getType())) {
      selection += "AND default_gw_mac=?::macaddr ";
    }
    String finalQuery = String.format(query, selection);

    // set parameter
    PreparedStatement s = c.prepareStatement(finalQuery);
    s.setString(1, network.getType().toString());
    s.setString(2, network.getPublicIp());
    s.setString(3, network.getDefaultGatewayIp());

    if (NetworkType.WIFI.equals(network.getType())) {
      s.setString(4, network.getDefaultGatewayMac());
      s.setString(5, network.getBssid());
      s.setString(6, network.getSsid());
    } else if (NetworkType.ETHERNET.equals(network.getType())) {
      s.setString(4, network.getDefaultGatewayMac());
    }

    return s;
  }


  @Override
  public GeneralStatistic getGeneralStatistic() throws SQLException {
    try (Connection c = bds.getConnection()) {

      PreparedStatement s = c.prepareStatement(TOTAL_TEST_COUNT);
      ResultSet rs = s.executeQuery();
      rs.next();
      int totalTestCount = rs.getInt("count");

      s = c.prepareStatement(TOTAL_INTERCEPTION_COUNT);
      rs = s.executeQuery();
      rs.next();
      int totalInterceptionCount = rs.getInt("count");

      return new GeneralStatistic(totalTestCount, totalInterceptionCount);

    }
  }

}
