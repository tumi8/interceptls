package de.tum.in.net.model;

import java.util.concurrent.Callable;

/**
 * Created by johannes on 31.03.17.
 */
public interface Scenario extends Callable<TlsClientServerResult> {

  /**
   * A scenario always must return a {@link TlsClientServerResult}, which contains the state
   * {@link State}.
   * 
   * @return {@link TlsClientServerResult}
   */
  @Override
  TlsClientServerResult call();

}
