package de.tum.in.net.client;

import de.tum.in.net.model.NetworkId;

public interface NetworkIdentifier {

  boolean isConnected();

  NetworkId identifyNetwork();
}
