package de.tum.in.net;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tum.in.net.model.ResultListener;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;

public class ResultUploader implements ResultListener<ScenarioResult> {

  private static final Logger log = LogManager.getLogger();
  private final CaptureServerConfig conf;

  public ResultUploader(CaptureServerConfig conf) {
    this.conf = Objects.requireNonNull(conf, "conf must be not null");
  }

  @Override
  public void publish(ScenarioResult result) {

    try {
      TestSession session = conf.getNewTestSession();
      session.uploadHandshake(Arrays.asList(result));
    } catch (IOException e) {
      log.error("Could not upload results.", e);
      // TODO save test results and try again later
    }

  }

}
