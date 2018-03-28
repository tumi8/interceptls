package de.tum.in.net.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.Scenario;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.model.TlsTestType;

/**
 * 
 * @author johannes
 */
public class ClientWorkflowCallable implements Callable<TlsTestResult> {

  private final List<HostAndPort> targets;
  private final NetworkIdentifier networkIdentifier;


  public ClientWorkflowCallable(List<HostAndPort> targets, NetworkIdentifier networkIdentifier) {
    this.targets = Objects.requireNonNull(targets);
    this.networkIdentifier = Objects.requireNonNull(networkIdentifier);
  }

  @Override
  public TlsTestResult call() throws Exception {

    // no connection
    if (!networkIdentifier.isConnected()) {
      throw new IOException("No connection to internet.");
    }

    List<TlsClientServerResult> results = connectToHosts(targets);

    // classify network
    NetworkId network = networkIdentifier.identifyNetwork();

    TlsTestResult result = new TlsTestResult(network, results);
    if (result.anyInterception()) {
      // conduct detailed measurements for one host
      TlsClientServerResult r = result.getInterceptedTarget();
      HostAndPort target = r.getHostAndPort();

      Map<TlsTestType, TlsClientServerResult> detailedResults = detailedMeasurements(target);
      result.setDetailedResults(detailedResults);
    }

    return result;

  }

  private static List<TlsClientServerResult> connectToHosts(List<HostAndPort> targets)
      throws Exception {

    List<TlsClientServerResult> results = new ArrayList<>();

    for (HostAndPort t : targets) {
      Scenario scenario = new DefaultHttpsScenario(t);

      TlsClientServerResult result = scenario.call();
      results.add(result);
    }

    return results;
  }

  private static Map<TlsTestType, TlsClientServerResult> detailedMeasurements(HostAndPort target)
      throws Exception {

    Map<TlsTestType, TlsClientServerResult> detailedResults = new HashMap<>();

    // test if middlebox uses SNI to connect to host
    Scenario sni = new DefaultHttpsScenario(target,
        new TlsDetectionClient("definitely.not.existent." + target.getHost()));
    detailedResults.put(TlsTestType.SNI, sni.call());

    Scenario proxy = new DefaultProxyScenario(target);
    detailedResults.put(TlsTestType.PROXY, proxy.call());

    return detailedResults;
  }



}
