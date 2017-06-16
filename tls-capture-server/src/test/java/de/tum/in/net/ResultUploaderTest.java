package de.tum.in.net;

import org.junit.Ignore;
import org.junit.Test;

import de.tum.in.net.model.TlsTestId;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.ScenarioResult.ScenarioResultBuilder;

public class ResultUploaderTest {

  @Ignore
  @Test
  public void uploaderSavesResultsInCaseOfError() {
    ResultUploader uploader = new ResultUploader("bla");


    TlsTestId id = TlsTestId.randomID();

    ScenarioResult result =
        new ScenarioResultBuilder("src", "dst").error(new Throwable(), id.getTestId());
    uploader.publish(id.getTestSessionId(), result);

    ScenarioResult result2 =
        new ScenarioResultBuilder("src2", "dst2").error(new Throwable(), id.getTestId());
    uploader.publish(id.getTestSessionId(), result2);

    ScenarioResult result3 =
        new ScenarioResultBuilder("src3", "dst3").error(new Throwable(), id.getTestId());
    uploader.publish(id.getTestSessionId(), result3);

  }

}
