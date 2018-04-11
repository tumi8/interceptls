package de.tum.in.net.client;

import java.util.Objects;

public class Util {

    private Util() {
        //utility
    }

    public static String formatTimestamp(final String iso8601) {
        Objects.requireNonNull(iso8601);
        return iso8601.replace("T", " ").substring(0, iso8601.length() - 4);
    }
}
