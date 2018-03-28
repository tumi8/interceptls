package de.tum.in.net.client;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.tls.TlsClient;

import de.tum.in.net.model.Step;


/**
 * Created by johannes on 31.03.17.
 */

public class DefaultProxyScenario extends AbstractScenario {

  private final TlsClient client;

  public DefaultProxyScenario(HostAndPort target) {
    this(target, new TlsDetectionClient(target.getHost()));
  }

  public DefaultProxyScenario(HostAndPort target, TlsClient client) {
    super(target);
    this.client = Objects.requireNonNull(client, "client must not be null");
  }

  @Override
  public String toString() {
    return DefaultProxyScenario.class.getName();
  }

  @Override
  public List<Step> getSteps() {
    return Arrays.asList((Step) new TlsStep(client));
  }
}
