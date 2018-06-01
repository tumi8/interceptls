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

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.List;

import de.tum.in.net.model.IpAndMac;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.NetworkType;

/**
 * Created by johannes on 22.03.18.
 */

public class AndroidNetworkIdentifier implements NetworkIdentifier, LocationListener {

    private static final Logger log = LoggerFactory.getLogger(AndroidNetworkIdentifier.class);
    private final Context ctx;

    public AndroidNetworkIdentifier(final Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean isConnected() {
        final ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public NetworkId identifyNetwork() {
        final NetworkId id = new NetworkId();

        if (ConfigurationReader.isLocationAllowed(ctx)) {
            requestLocationUpdate();
        }

        final ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final LinkProperties lp = cm.getLinkProperties(cm.getActiveNetwork());

        setNetworkState(id, cm);
        setDns(id, lp);
        setDefaultGateway(id, lp);
        if (ConfigurationReader.isLocationAllowed(ctx)) {
            setLocation(id);
        }

        log.error("Identified network: {}", id);
        return id;
    }

    private void setNetworkState(final NetworkId id, final ConnectivityManager cm) {

        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        switch (activeNetwork.getType()) {
            case (ConnectivityManager.TYPE_WIFI):
                id.setType(NetworkType.WIFI);
                //get wifi name and BSSID
                final WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                final WifiInfo info = wifiManager.getConnectionInfo();
                if (info != null) {
                    String ssid = info.getSSID();
                    if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                        ssid = ssid.substring(1, ssid.length() - 1);
                    }
                    id.setSsid(ssid);
                    id.setBssid(info.getBSSID());
                }
                break;
            case (ConnectivityManager.TYPE_MOBILE): {
                id.setType(NetworkType.CELLULAR);
                break;
            }
            default:
                break;
        }


    }

    private void setDns(final NetworkId id, final LinkProperties lp) {

        try {
            final List<InetAddress> dnsIpList = lp.getDnsServers();

            for (final InetAddress dnsIp : dnsIpList) {
                //we need to reach out to them so that there is an entry in the arp table
                final String ip = dnsIp.getHostAddress();
                execProcess("ping -c 1 " + ip);
                final List<String> neighborList = execProcess("ip neighbor | grep " + ip + " | cut -d \\  -f 5");
                if (neighborList.size() > 0) {
                    id.addDns(new IpAndMac(ip, neighborList.get(0)));

                } else {
                    id.addDns(new IpAndMac(ip, null));
                }
            }

        } catch (final IOException e) {
            log.warn("Could not determine the dns information", e);
        }
    }

    private List<String> execProcess(final String cmd) throws IOException {
        final String[] pipe = {
                "/system/bin/sh",
                "-c",
                cmd
        };

        final Process exec = Runtime.getRuntime().exec(pipe);

        return IOUtils.readLines(exec.getInputStream(), Charset.defaultCharset());
    }


    private void setDefaultGateway(final NetworkId id, final LinkProperties lp) {
        String gwIp = null;
        for (final RouteInfo r : lp.getRoutes()) {
            if (r.isDefaultRoute()) {
                final InetAddress ip = r.getGateway();
                gwIp = ip.getHostAddress();
            }
        }

        if (gwIp != null) {
            id.setDefaultGatewayIp(gwIp);
            try {
                //extract the mac address of the given gateway
                final List<String> neighborList = execProcess("ip neighbor | grep " + gwIp + " | cut -d \\  -f 5");
                if (neighborList.size() > 0) {
                    id.setDefaultGatewayMac(neighborList.get(0));
                }

            } catch (final IOException e) {
                log.warn("Could not determine the default gateway mac", e);
            }
        }


    }

    private void requestLocationUpdate() {
        if (ContextCompat.checkSelfPermission(ctx, MainActivity.LOCATION_PERMISSION)
                == PackageManager.PERMISSION_GRANTED) {
            final LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            if (lm != null) {
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                }
                if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
                }
            }
        }
    }

    private void setLocation(final NetworkId id) {
        if (ContextCompat.checkSelfPermission(ctx, MainActivity.LOCATION_PERMISSION)
                == PackageManager.PERMISSION_GRANTED) {
            final LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

            if (lm != null) {
                Location loc = null;
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

                if (loc == null && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                if (loc != null) {
                    id.setLocation(new de.tum.in.net.model.Location(loc.getLongitude(), loc.getLatitude()));
                }
                lm.removeUpdates(this);
            }

        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        //ignore
    }

    @Override
    public void onStatusChanged(final String s, final int i, final Bundle bundle) {
        //ignore
    }

    @Override
    public void onProviderEnabled(final String s) {
        //ignore
    }

    @Override
    public void onProviderDisabled(final String s) {
        //ignore
    }
}
