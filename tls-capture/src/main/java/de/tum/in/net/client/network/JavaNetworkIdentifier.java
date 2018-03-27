package de.tum.in.net.client.network;

import de.tum.in.net.client.NetworkIdentifier;
import de.tum.in.net.model.NetworkId;

public class JavaNetworkIdentifier implements NetworkIdentifier {

  @Override
  public NetworkId identifyNetwork() {
    // try to identify the network with java only
    return new NetworkId();
  }

  @Override
  public boolean isConnected() {
    // TODO Auto-generated method stub
    return true;
  }

}
