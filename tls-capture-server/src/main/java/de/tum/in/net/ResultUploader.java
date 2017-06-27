package de.tum.in.net;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tum.in.net.model.ResultListener;
import de.tum.in.net.model.TestID;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.session.FixedIdTestSession;

/**
 * Publishes the result to the configured target from CaptureServerConfig. If the upload results in
 * an error, it will try again when the next result arrives.
 * 
 * @author Johannes
 */
public class ResultUploader implements ResultListener<ScenarioResult> {

  private static final Logger log = LogManager.getLogger();
  private final String targetUrl;

  private final ArrayBlockingQueue<SessionResult> unpublishedResults =
      new ArrayBlockingQueue<>(50, true);

  public ResultUploader(String targetUrl) {
    this.targetUrl = Objects.requireNonNull(targetUrl, "targetUrl must be not null");
  }


  @Override
  public void publish(TestID id, ScenarioResult result) {
    log.info("Publish results");
    // if queue is full, remove one element
    if (0 == unpublishedResults.remainingCapacity()) {
      unpublishedResults.poll();
    }
    unpublishedResults.add(new SessionResult(id, result));

    safePublish();
  }

  private void safePublish() {
    synchronized (unpublishedResults) {


      Iterator<SessionResult> it = unpublishedResults.iterator();
      while (it.hasNext()) {
        SessionResult res = it.next();
        try {
          TestSession session = new FixedIdTestSession(res.id.getSessionID(), targetUrl);
          session.uploadHandshake(res.id.getCounter(), res.result);

          log.info("{} succesfully published", res.id.getSessionID());
          it.remove();

        } catch (IOException e) {
          log.warn("Could not upload result.", e);
        }
      }

    }

  }

  protected int getUnpublishedSize() {
    return unpublishedResults.size();
  }

  private class SessionResult {
    TestID id;
    ScenarioResult result;

    public SessionResult(TestID id, ScenarioResult result) {
      this.id = id;
      this.result = result;
    }

  }

}
