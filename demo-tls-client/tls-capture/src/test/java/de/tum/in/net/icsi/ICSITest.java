package de.tum.in.net.icsi;

import org.junit.Test;

import java.util.Arrays;

import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.session.ICSITestSession;
import de.tum.in.net.session.TestSession;

import static org.junit.Assert.*;

/**
 * Created by johannes on 04.04.17.
 */
public class ICSITest{

    @Test
    public void uploadTests() throws Exception{
        ScenarioResult result = null;

        TestSession session = ICSITestSession.newTestSession();
        session.uploadResults(Arrays.asList(result));
    }

}