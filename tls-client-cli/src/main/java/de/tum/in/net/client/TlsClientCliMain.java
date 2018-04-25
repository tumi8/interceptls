package de.tum.in.net.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.client.network.JavaNetworkIdentifier;
import de.tum.in.net.model.MiddleboxCharacterization;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.session.OnlineTestSession;

public class TlsClientCliMain {

  private static final Logger log = LoggerFactory.getLogger(TlsClientCliMain.class);
  private static final HostAndPort DEFAULT_TARGET = new HostAndPort("192.168.178.36", 443);
  private static final String ANALYSIS_HOST = "https://127.0.0.1:3000";

  public static void main(String[] args) {

    log.info("Start TLS interception detection.");

    boolean publishResults = true;
    NetworkIdentifier networkIdentifier;
    String os = System.getProperty("os.name");
    if ("Linux".equals(os)) {
      networkIdentifier = new UbuntuNetworkIdentifier();
    } else {
      log.info(os
          + " is currently not supported. Therefore the network will not be identfied and the results won't be published.");
      networkIdentifier = new JavaNetworkIdentifier();
      publishResults = false;
    }

    List<HostAndPort> targets = Arrays.asList(DEFAULT_TARGET);
    ClientWorkflowCallable c = new ClientWorkflowCallable(targets, networkIdentifier);

    TlsTestResult testResult = null;
    try {
      testResult = c.call();
    } catch (Exception e) {
      log.error("Unexpected exception", e);
    }

    // print and publish result
    if (testResult != null) {
      printTestResult(testResult);

      if (testResult.anyInterception()) {
        printMiddleboxCharacterization(testResult.getMiddleboxCharacterization());
      }

      if (publishResults) {
        publishResults(testResult);
      }
    }

  }

  private static void printTestResult(TlsTestResult testResult) {
    log.info("");
    log.info("TEST RESULT");
    log.info("-----------------------------");
    log.info("Targets: {}", testResult.getClientServerResults().size());
    log.info("Successful connections: {}", testResult.successfulConnections());
    log.info("Intercepted connections: {}", testResult.interceptions());
  }

  private static void printMiddleboxCharacterization(MiddleboxCharacterization mc) {
    log.info("");
    log.info("MIDDLEBOX CHARACTERIZATION");
    log.info("-----------------------------");
    log.info("TLS Versions: {}", mc.getSupportedTlsVersions());
    log.info("Can connect wrong http host: {}", mc.getCanConnectWrongHttpHost());
    log.info("Can connect wrong sni: {}", mc.getCanConnectWrongSni());
  }

  private static void publishResults(TlsTestResult testResult) {
    if (testResult.anyInterception()) {
      log.info("Your connection is intercepted. Anyway, we at least try to publish the results.");
    }
    try {
      TestSession s = new OnlineTestSession(ANALYSIS_HOST);
      AnalysisResult r = s.uploadResult(testResult);
      printAnalysisResult(r);

    } catch (IOException e) {
      // analysis server not reachable or no secure connection
      log.warn("Could not upload results to analysis server.");
    }
  }



  private static void printAnalysisResult(AnalysisResult r) {
    log.info("");
    log.info("Results:");
    log.info("-----------------------------");
    log.info("späterTM: {}", r);
  }

}
