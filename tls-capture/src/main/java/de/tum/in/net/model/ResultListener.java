package de.tum.in.net.model;

import de.tum.in.net.session.SessionId;

/**
 * Created by johannes on 17.05.17.
 */

public interface ResultListener<T> {

  void publish(SessionId id, T result);
}
