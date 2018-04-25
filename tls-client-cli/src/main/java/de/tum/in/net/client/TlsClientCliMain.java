package de.tum.in.net.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.client.network.JavaNetworkIdentifier;
import de.tum.in.net.model.MiddleboxCharacterization;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.session.LoggingTestSession;

public class TlsClientCliMain {

  private static final Logger log = LoggerFactory.getLogger(TlsClientCliMain.class);
  private static final HostAndPort DEFAULT_TARGET = new HostAndPort("192.168.178.36", 443);
  private static final String ANALYSIS_HOST = "https://127.0.0.1:3000";

  public static void main(String[] args) {

    log.info("Start TLS interception detection.");

    boolean publishResults = false;
    NetworkIdentifier networkIdentifier;
    String os = System.getProperty("os.name").toLowerCase();
    if ("linux".equals(os)) {
      networkIdentifier = new UbuntuNetworkIdentifier();
      publishResults = true;
    } else {
      log.info(
          "Your OS is currently not supported. Therefore the network will not be identfied and the results won't be published.");
      networkIdentifier = new JavaNetworkIdentifier();
    }

    List<HostAndPort> targets = Arrays.asList(DEFAULT_TARGET);

    ClientWorkflowCallable c = new ClientWorkflowCallable(targets, networkIdentifier);

    ExecutorService exec = Executors.newSingleThreadExecutor();
    Future<TlsTestResult> result = exec.submit(c);

    log.debug("Wait for result...");
    try {
      TlsTestResult testResult = result.get();
      log.info("");
      log.info("TEST RESULT");
      log.info("-----------------------------");
      log.info("Targets: {}", testResult.getClientServerResults().size());
      log.info("Successful connections: {}", testResult.successfulConnections());
      log.info("Intercepted connections: {}", testResult.interceptions());
      if (testResult.anyInterception()) {
        log.info("");
        log.info("MIDDLEBOX CHARACTERIZATION");
        log.info("-----------------------------");
        MiddleboxCharacterization mc = testResult.getMiddleboxCharacterization();
        log.info("TLS Versions: {}", mc.getSupportedTlsVersions());
        log.info("Can connect wrong http host: {}", mc.getCanConnectWrongHttpHost());
        log.info("Can connect wrong sni: {}", mc.getCanConnectWrongSni());
      }

      if (publishResults) {
        TestSession s = new LoggingTestSession(); // new OnlineTestSession(ANALYSIS_HOST);
        AnalysisResult r = s.uploadResult(testResult);
        log.info("");
        log.info("Results:");
        log.info("-----------------------------");
        log.info("späterTM: {}", r);
      }

    } catch (InterruptedException e) {
      log.warn("Interrupt detected, terminate now.");
    } catch (ExecutionException e) {
      log.error("Executor error", e);
    } catch (IOException e) {
      log.error("Could not upload results to analysis server");
    }

    exec.shutdown();

  }

}
