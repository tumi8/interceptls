package de.tum.in.net.demotlsclient;

import de.tum.in.net.model.TestId;
import de.tum.in.net.model.TlsTestId;
import de.tum.in.net.session.SessionId;

/**
 * Created by johannes on 16.06.17.
 */

class TlsTestIdIncrementor {
    private final SessionId sessionId;
    private int counter = 0;

    public TlsTestIdIncrementor(final SessionId sessionID) {
        this.sessionId = sessionID;
    }

    public synchronized TlsTestId next() {
        counter++;
        return new TlsTestId(this.sessionId, new TestId(String.valueOf(counter)));
    }
}
