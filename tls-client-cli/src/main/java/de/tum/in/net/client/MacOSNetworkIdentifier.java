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
package de.tum.in.net.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.IpAndMac;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.NetworkType;

/*
 * Tested on Ubuntu 17.10.
 */
public class MacOSNetworkIdentifier implements NetworkIdentifier {

  private static final Logger log = LoggerFactory.getLogger(MacOSNetworkIdentifier.class);

  @Override
  public NetworkId identifyNetwork() {
    NetworkId id = new NetworkId();

    setNetworkState(id);
    setDns(id);
    setDefaultGateway(id);

    return id;
  }

  private void setDefaultGateway(NetworkId id) {
    try {
      final List<String> ip = execProcess("route -n get default | grep gateway | awk '{print $2}'");
      if (!ip.isEmpty()) {
        final String gwIp = ip.get(0);
        id.setDefaultGatewayIp(gwIp);
        // extract the mac address of the given gateway
        final List<String> neighborList = execProcess("arp " + gwIp + " | awk '{print $4}'");
        if (neighborList.size() > 0) {
          id.setDefaultGatewayMac(neighborList.get(0));
        }
      }

    } catch (final IOException e) {
      log.warn("Could not determine the default gateway ip", e);
    }

  }

  private void setDns(NetworkId id) {
    try {
      List<String> dnsOutput =
          execProcess("scutil --dns | grep nameserver | awk '{print $3}' | sort | uniq");
      if (!dnsOutput.isEmpty()) {

        for (String dnsIp : dnsOutput) {
          execProcess("ping -c 1 " + dnsIp);
          final List<String> neighborList = execProcess("arp " + dnsIp + " | awk '{print $4}'");
          if (neighborList.size() > 0) {
            id.addDns(new IpAndMac(dnsIp, neighborList.get(0)));
          } else {
            id.addDns(new IpAndMac(dnsIp, null));
          }
        }

      }
    } catch (IOException e) {
      log.warn("Could not determine dns information", e);
    }

  }

  private void setNetworkState(NetworkId id) {
    String airportCmd =
        "/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -I";
    try {
      List<String> ssidOutput =
          execProcess(airportCmd + " | awk '/ SSID/ {print substr($0, index($0, $2))}'");
      if (ssidOutput.isEmpty() || ssidOutput.get(0).isEmpty()) {
        id.setType(NetworkType.ETHERNET);
      } else {
        id.setType(NetworkType.WIFI);
        id.setSsid(ssidOutput.get(0));
        List<String> bssidOutput =
            execProcess(airportCmd + " | awk '/ BSSID/ {print substr($0, index($0, $2))}'");
        if (bssidOutput.size() > 0) {
          id.setBssid(bssidOutput.get(0));
        }
      }

    } catch (IOException e) {
      log.warn("Could not determine network state", e);
    }

  }

  @Override
  public boolean isConnected() {
    return true;
  }

  private List<String> execProcess(final String cmd) throws IOException {
    final String[] pipe = {"/bin/sh", "-c", cmd};

    final Process exec = Runtime.getRuntime().exec(pipe);

    return IOUtils.readLines(exec.getInputStream(), Charset.defaultCharset());
  }


}
