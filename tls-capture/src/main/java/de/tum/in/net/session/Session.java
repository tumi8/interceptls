package de.tum.in.net.session;

/**
 * Created by johannes on 13.04.17.
 */

public class Session {

    private final String id;

    public Session(final String id) {
        this.id = id;
    }

    public String getID() {
        return this.id;
    }
}
