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
import de.tum.in.net.model.TestSession;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.session.LoggingTestSession;

public class TlsClientCmdMain {

  private static final Logger log = LoggerFactory.getLogger(TlsClientCmdMain.class);

  public static void main(String[] args) {

    log.info("Start TLS detection.");

    List<HostAndPort> targets = Arrays.asList(new HostAndPort("127.0.0.1", 7623));
    // String analysisHost = "https://127.0.0.1:3000";

    ClientWorkflowCallable c = new ClientWorkflowCallable(targets, new JavaNetworkIdentifier());

    ExecutorService exec = Executors.newSingleThreadExecutor();
    Future<TlsTestResult> result = exec.submit(c);

    log.debug("Wait for result...");
    try {
      TlsTestResult testResult = result.get();
      log.info("-----------------------------");
      log.info("TEST RESULT");
      log.info("-----------------------------");
      log.info("Targets: {}", testResult.getClientServerResults().size());
      log.info("Successful connections: {}", testResult.successfulConnections());
      log.info("Intercepted connections: {}", testResult.interceptions());

      TestSession s = new LoggingTestSession();
      AnalysisResult r = s.uploadResult(testResult);
      log.info("Results:");
      log.info("-----------------------------");
      log.info("späterTM: {}", r);

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
