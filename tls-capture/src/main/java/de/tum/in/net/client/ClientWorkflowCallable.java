package de.tum.in.net.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.bouncycastle.tls.ProtocolVersion;

import de.tum.in.net.model.MiddleboxCharacterization;
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

    TlsTestResult result = new TlsTestResult(network, results);
    // if there was an interception try to characterize the middlebox
    if (result.anyInterception()) {
      // conduct detailed measurements for one host
      TlsClientServerResult r = result.getInterceptedTarget();
      HostAndPort target = r.getHostAndPort();

      MiddleboxCharacterization middlebox = characterizeMiddlebox(target);
      result.setMiddleboxCharacterization(middlebox);
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

  private static MiddleboxCharacterization characterizeMiddlebox(HostAndPort target)
      throws Exception {

    MiddleboxCharacterization.Builder b = new MiddleboxCharacterization.Builder();

    // test if middlebox uses SNI to resolve to host
    Scenario sni = new DefaultHttpsScenario(target,
        new TlsDetectionClient("definitely.not.existent." + target.getHost()));
    TlsClientServerResult r = sni.call();
    b.setUsesSniToResolveHost(!r.isSuccess());

    // test if middlebox uses HTTP host to resolve host
    Scenario fakeHttpHost =
        new FakeHostHttpsScenario(target, "definitely.not.existent." + target.getHost());
    r = fakeHttpHost.call();
    b.setUsesHttpHostToResolveHost(!r.isSuccess());

    // test different TLS versions
    ProtocolVersion[] versions = new ProtocolVersion[] {ProtocolVersion.SSLv3,
        ProtocolVersion.TLSv10, ProtocolVersion.TLSv11, ProtocolVersion.TLSv12};
    for (ProtocolVersion version : versions) {
      Scenario s =
          new DefaultHttpsScenario(target, new VersionedTlsClient(target.getHost(), version));
      r = s.call();
      b.setSupportTlsVersion(version, r.isSuccess());
    }


    return b.build();
  }



}
