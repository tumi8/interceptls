package de.tum.in.net.scenario;

import java.util.concurrent.Callable;

/**
 * Created by johannes on 31.03.17.
 */
public interface Scenario extends Callable<ScenarioResult> {

    @Override
    ScenarioResult call();

}
