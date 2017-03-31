package de.tum.net.in.demotlsclient;

/**
 * Created by johannes on 22.03.17.
 */

public interface AsyncResult<A, B> {

    void publishProgress(A progress);

    void publishResult(B result);
}
