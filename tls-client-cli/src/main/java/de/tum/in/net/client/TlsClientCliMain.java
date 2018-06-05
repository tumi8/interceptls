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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.analysis.NetworkStats;
import de.tum.in.net.client.db.MeasurementDb;
import de.tum.in.net.client.db.TextFileMeasurementDb;
import de.tum.in.net.client.network.JavaNetworkIdentifier;
import de.tum.in.net.client.network.MacOSNetworkIdentifier;
import de.tum.in.net.client.network.UbuntuNetworkIdentifier;
import de.tum.in.net.model.MiddleboxCharacterization;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.NetworkType;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.model.TlsConstants;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.session.OnlineTestSession;

public class TlsClientCliMain {

  private static final Logger log = LoggerFactory.getLogger(TlsClientCliMain.class);

  public static void main(final String[] args) {

    log.info("Start TLS interception detection.");

    boolean publishResults = true;
    NetworkIdentifier networkIdentifier;
    final String os = System.getProperty("os.name");
    if ("Linux".equals(os)) {
      networkIdentifier = new UbuntuNetworkIdentifier();
    } else if ("Mac OS X".equals(os)) {
      networkIdentifier = new MacOSNetworkIdentifier();
    } else {
      log.info(os
          + " is currently not supported. Therefore the network will not be identfied and the results won't be published.");
      networkIdentifier = new JavaNetworkIdentifier();
      publishResults = false;
    }

    final List<HostAndPort> targets =
        Arrays.asList(HostAndPort.parse(TlsConstants.TLS_CAPTURE_SERVER_HOST));
    final ClientWorkflowCallable c = new ClientWorkflowCallable(targets, networkIdentifier);

    TlsTestResult testResult = null;
    try {
      testResult = c.call();
    } catch (final Exception e) {
      log.error("Unexpected exception", e);
    }

    // print and publish result
    if (testResult != null) {
      printTestResult(testResult);

      if (testResult.anyInterception()) {
        printMiddleboxCharacterization(testResult.getMiddleboxCharacterization());
      }

      if (publishResults) {
        printNetworkInformation(testResult.getNetworkId());
        publishResults(testResult);
      }
    }

  }

  private static void printNetworkInformation(final NetworkId network) {
    log.info("");
    log.info("Network information");
    log.info("-----------------------------");
    log.info("Type: {}", network.getType());
    log.info("Public IP: {}", network.getPublicIp());
    log.info("DNS: {}", network.getDns());
    log.info("Gateway IP: {}", network.getDefaultGatewayIp());
    log.info("Gateway MAC: {}", network.getDefaultGatewayMac());
    if (NetworkType.WIFI.equals(network.getType())) {
      log.info("WIFI SSID: {}", network.getSsid());
      log.info("WIFI BSSID: {}", network.getBssid());

    }
  }

  private static void printTestResult(final TlsTestResult testResult) {
    log.info("");
    log.info("TEST RESULT");
    log.info("-----------------------------");
    log.info("Targets: {}", testResult.getClientServerResults().size());
    log.info("Successful connections: {}", testResult.successfulConnections());
    log.info("Intercepted connections: {}", testResult.interceptions());
  }

  private static void printMiddleboxCharacterization(final MiddleboxCharacterization mc) {
    log.info("");
    log.info("MIDDLEBOX CHARACTERIZATION");
    log.info("-----------------------------");
    log.info("TLS Versions: {}", mc.getSupportedTlsVersions());
    log.info("Can connect wrong http host: {}", mc.getCanConnectWrongHttpHost());
    log.info("Can connect wrong sni: {}", mc.getCanConnectWrongSni());
  }

  private static void publishResults(final TlsTestResult testResult) {
    if (testResult.anyInterception()) {
      log.info("Your connection is intercepted. Anyway, we at least try to publish the results.");
    }

    final MeasurementDb db = TextFileMeasurementDb.getInstance();
    try {
      final TestSession s = new OnlineTestSession(TlsConstants.TLS_ANALYSIS_URL);
      final AnalysisResult r = s.uploadResult(testResult);
      printAnalysisResult(r);

      uploadResultsFromDatabase(db, s);


    } catch (final IOException e) {
      // analysis server not reachable or no secure connection
      log.warn("Could not upload result to analysis server. Instead it is stored in database.");
      try {
        db.append(testResult);
      } catch (final IOException e1) {
        log.error("Could not store result in database.", e);
      }
    }
  }

  private static void uploadResultsFromDatabase(final MeasurementDb db, final TestSession s) {
    try {
      // in case of success, try to publish the other results
      final List<TlsTestResult> results = db.readAll();
      for (final TlsTestResult result : results) {
        s.uploadResult(result);
      }
      db.deleteAll();

    } catch (final IOException e) {
      log.error("Could not upload temp results.", e);
    }
  }

  private static void printAnalysisResult(final AnalysisResult r) {
    log.info("");
    log.info("Network Stats:");
    log.info("-----------------------------");
    final NetworkStats stats = r.getStats();
    log.info("Test count: {}", stats.getCountTotal());
    log.info("Interception rate: {}", stats.getInterceptionRateTotal());
  }

}
