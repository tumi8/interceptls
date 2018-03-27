package de.tum.in.net.client;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.tls.TlsClient;

import de.tum.in.net.model.Step;


/**
 * Created by johannes on 31.03.17.
 */

public class DefaultHttpsScenario extends AbstractScenario {

  private final TlsClient client;

  public DefaultHttpsScenario(HostAndPort target) {
    this(target, new TrimmedTlsClient());
  }

  public DefaultHttpsScenario(HostAndPort target, TlsClient client) {
    super(target);
    this.client = Objects.requireNonNull(client, "client must not be null");
  }

  @Override
  public String toString() {
    return DefaultHttpsScenario.class.getName();
  }

  @Override
  public List<Step> getSteps() {
    return Arrays.asList(new TlsStep(client), new HttpStep());
  }
}
