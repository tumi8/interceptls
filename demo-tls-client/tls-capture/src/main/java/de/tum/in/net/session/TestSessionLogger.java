package de.tum.in.net.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public class TestSessionLogger implements TestSession {

    private static final Logger log = LoggerFactory.getLogger(TestSessionLogger.class);

    @Override
    public void uploadResults(Collection<ScenarioResult> results) throws IOException {
        log.debug("Logging {} results", results.size());
        for(ScenarioResult result : results){
            log.info(result.toString());
        }
    }
}
