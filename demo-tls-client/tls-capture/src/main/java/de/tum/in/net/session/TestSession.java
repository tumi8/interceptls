package de.tum.in.net.session;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public interface TestSession {

    void uploadResults(Collection<ScenarioResult> results) throws IOException;
}
