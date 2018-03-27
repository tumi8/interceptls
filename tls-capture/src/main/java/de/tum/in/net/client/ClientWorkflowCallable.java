package de.tum.in.net.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.Scenario;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsTestResult;

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

    return new TlsTestResult(network, results);
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



}
