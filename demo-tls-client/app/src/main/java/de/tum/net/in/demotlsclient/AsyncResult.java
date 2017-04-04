package de.tum.net.in.demotlsclient;

/**
 * Created by johannes on 22.03.17.
 */

public interface AsyncResult<A> {

    void publishResult(A result);
}
