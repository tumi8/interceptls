package de.tum.in.net.client;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import de.tum.in.net.client.NetworkIdentifier;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.NetworkType;

/**
 * Created by johannes on 22.03.18.
 */

public class AndroidNetworkIdentifier implements NetworkIdentifier {

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

        setNetworkState(id);
        setDns(id);
        setDefaultGateway(id);

        log.error("Identified network: {}", id);
        return id;
    }

    private void setNetworkState(final NetworkId id) {
        final ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        switch (activeNetwork.getType()) {
            case (ConnectivityManager.TYPE_WIFI):
                id.setType(NetworkType.WIFI);
                //get wifi name and BSSID
                final WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                final WifiInfo info = wifiManager.getConnectionInfo();
                if (info != null) {
                    id.setSsid(info.getSSID());
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

    private void setDns(final NetworkId id) {
        try {
            final List<String> ip = execProcess("getprop net.dns1");
            final String dnsIp = ip.get(0);
            if (dnsIp != null && !dnsIp.isEmpty()) {
                id.setDnsIp(dnsIp);
                //extract the mac address of the given dns server
                final List<String> neighborList = execProcess("ip neighbor | grep " + dnsIp + " | cut -d \\  -f 5");
                if (neighborList.size() > 0) {
                    id.setDnsMac(neighborList.get(0));
                }
            }

        } catch (final IOException e) {
            log.warn("Could not determine the dns ip", e);
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


    public void setDefaultGateway(NetworkId id) {
        try {
            final List<String> ip = execProcess("ip route get 1.1.1.1 | cut -d \\  -f 3");
            final String gwIp = ip.get(0);
            if (gwIp != null && !gwIp.isEmpty()) {
                id.setDefaultGatewayIp(gwIp);
                //extract the mac address of the given gateway
                final List<String> neighborList = execProcess("ip neighbor | grep " + gwIp + " | cut -d \\  -f 5");
                if (neighborList.size() > 0) {
                    id.setDefaultGatewayMac(neighborList.get(0));
                }
            }

        } catch (final IOException e) {
            log.warn("Could not determine the default gateway ip", e);
        }
    }
}
