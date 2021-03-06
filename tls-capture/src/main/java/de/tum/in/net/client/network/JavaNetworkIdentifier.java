/**
 * Copyright © 2018 Johannes Schleger (johannes.schleger@tum.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
