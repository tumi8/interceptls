package de.tum.in.net.demotlsclient;

import de.tum.in.net.model.TestID;
import de.tum.in.net.session.SessionID;

/**
 * Created by johannes on 16.06.17.
 */

class TestIDIncrementer {
    private final SessionID sessionId;
    private int counter = 0;

    public TestIDIncrementer(final SessionID sessionID) {
        this.sessionId = sessionID;
    }

    public synchronized TestID next() {
        counter++;
        return new TestID(this.sessionId, counter);
    }
}
