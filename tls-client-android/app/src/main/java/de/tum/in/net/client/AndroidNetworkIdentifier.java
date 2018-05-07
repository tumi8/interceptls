package de.tum.in.net.client;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;

import de.tum.in.net.model.IpAndMac;
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

        final ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final LinkProperties lp = cm.getLinkProperties(cm.getActiveNetwork());


        setNetworkState(id, cm);
        setPublicIp(id);
        setDns(id, lp);
        setDefaultGateway(id, lp);

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


    public void setDefaultGateway(final NetworkId id, final LinkProperties lp) {
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

    public void setPublicIp(final NetworkId id) {
        try {
            final URL ipify = new URL("https://api.ipify.org");
            final URLConnection conn = ipify.openConnection();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                final String ip = in.readLine();
                id.setPublicIp(ip);
            }
        } catch (final IOException e) {
            log.warn("could not get public ip", e);
        }

    }
}
