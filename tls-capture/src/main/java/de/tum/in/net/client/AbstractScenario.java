package de.tum.in.net.client;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.Scenario;
import de.tum.in.net.model.Step;
import de.tum.in.net.model.TestContext;
import de.tum.in.net.model.TlsAbortException;
import de.tum.in.net.model.TlsClientServerResult;

public abstract class AbstractScenario implements Scenario {

  private static final Logger log = LoggerFactory.getLogger(DefaultHttpsScenario.class);
  private final HostAndPort target;

  public AbstractScenario(HostAndPort target) {
    this.target = Objects.requireNonNull(target);
  }

  public abstract List<Step> getSteps();

  @Override
  public TlsClientServerResult call() {
    log.debug("Connect to {}:{}", target.getHost(), target.getPort());

    try (Socket s = new Socket(target.getHost(), target.getPort())) {
      TestContext ctx = new TestContext(s);
      try {
        for (Step step : getSteps()) {
          step.process(target, ctx);
        }

        // sucessfully connected
        return TlsClientServerResult.connected(target, ctx.getClientResult(),
            ctx.getServerResult());
      } catch (IOException e) {
        log.error("Unexpected exception during test", e);
        return TlsClientServerResult.error(target, ctx.getClientResult());
      }

    } catch (TlsAbortException e) {
      log.error("Exception during TLS negotiation", e);
      return TlsClientServerResult.error(target, e.getTlsResult());
    } catch (IOException e) {
      log.warn("Could not connect to {}", target);
      return TlsClientServerResult.noConnection(target);
    }

  }

}
