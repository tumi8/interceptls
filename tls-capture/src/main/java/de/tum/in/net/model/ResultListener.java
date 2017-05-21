package de.tum.in.net.model;

/**
 * Created by johannes on 17.05.17.
 */

public interface ResultListener<T> {

  void publish(Severity severity, T result);
}
