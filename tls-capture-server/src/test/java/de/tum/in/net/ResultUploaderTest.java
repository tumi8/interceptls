package de.tum.in.net;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.ScenarioResult.ScenarioResultBuilder;

public class ResultUploaderTest {

  @Test
  public void uploaderSavesResultsInCaseOfError() {
    ResultUploader uploader = new ResultUploader(new TestSessionProvider() {

      int i = 0;

      @Override
      public TestSession newTestSession() throws IOException {
        return new TestSession() {


          @Override
          public void uploadHandshake(Collection<ScenarioResult> results) throws IOException {
            i++;

            // first connection cannot be established
            if (i == 1) {
              throw new IOException("no connection");
            }
            // for the second try we expect 2 results
            else if (i == 2) {
              assertEquals(2, results.size());
            }
            // the third try only one result is published
            else if (i == 3) {
              assertEquals(1, results.size());
            }

          }

          @Override
          public String getSessionID() {
            // TODO Auto-generated method stub
            return null;
          }
        };
      }
    });

    ScenarioResult result = new ScenarioResultBuilder("src", "dst").connected();
    uploader.publish(result);

    ScenarioResult result2 = new ScenarioResultBuilder("src2", "dst2").connected();
    uploader.publish(result2);

    ScenarioResult result3 = new ScenarioResultBuilder("src3", "dst3").connected();
    uploader.publish(result3);

  }

}
