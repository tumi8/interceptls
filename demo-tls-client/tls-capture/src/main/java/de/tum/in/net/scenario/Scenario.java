package de.tum.in.net.scenario;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by johannes on 31.03.17.
 */
public interface Scenario extends Callable<ScenarioResult> {

    public ScenarioResult call();

}
