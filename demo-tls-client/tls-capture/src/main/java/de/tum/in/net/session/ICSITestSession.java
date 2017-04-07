package de.tum.in.net.session;

import java.util.Collection;

import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public class ICSITestSession implements TestSession {

    private final String id;

    private ICSITestSession(String id) {
        this.id = id;
    }


    public static ICSITestSession newTestSession() {
        //TODO fetch id from ICSITestSession
        String id = "TODO";
        return new ICSITestSession(id);
    }

    @Override
    public void uploadResults(Collection<ScenarioResult> results) {


    }
}
