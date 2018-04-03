package de.tum.in.net.util;

import de.tum.in.net.server.SimpleServerSocket;

public class ServerUtil {

  private ServerUtil() {
    // Utility
  }

  /**
   * Waits for the server to be up and running. May hang indefinitely.
   * 
   * @param srv
   */
  public static void waitForRunning(SimpleServerSocket srv) {
    while (true) {
      if (srv.isRunning()) {
        break;
      }
    }
  }

}
