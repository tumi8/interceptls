package de.tum.in.net;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tum.in.net.model.ResultListener;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Publishes the result to the configured target from CaptureServerConfig. If the upload results in
 * an error, it will try again when the next result arrives.
 * 
 * @author Johannes
 */
public class ResultUploader implements ResultListener<ScenarioResult> {

  private static final Logger log = LogManager.getLogger();
  private final TestSessionProvider prov;

  private final ArrayBlockingQueue<ScenarioResult> unpublishedResults =
      new ArrayBlockingQueue<>(50, true);

  public ResultUploader(TestSessionProvider prov) {
    this.prov = Objects.requireNonNull(prov, "prov must be not null");
  }

  @Override
  public void publish(ScenarioResult result) {
    // if queue is full, remove one element
    if (0 == unpublishedResults.remainingCapacity()) {
      unpublishedResults.poll();
    }
    unpublishedResults.add(result);

    safePublish();
  }

  private void safePublish() {
    synchronized (unpublishedResults) {

      try {
        TestSession session = prov.newTestSession();
        session.uploadHandshake(unpublishedResults);

        // success, we can remove the results
        unpublishedResults.clear();
      } catch (IOException e) {
        log.debug("Could not upload results.", e);
      }
    }

  }

}
