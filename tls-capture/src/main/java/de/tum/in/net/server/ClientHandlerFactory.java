package de.tum.in.net.server;

import java.net.Socket;

/**
 * Created by johannes on 17.05.17.
 */

public interface ClientHandlerFactory {

  Runnable createClientHandler(Socket client);
}
