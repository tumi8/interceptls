package de.tum.in.net.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.NetworkType;

public class UbuntuNetworkIdentifier implements NetworkIdentifier {
	
	private static final Logger log = LoggerFactory.getLogger(UbuntuNetworkIdentifier.class);

	@Override
	public NetworkId identifyNetwork() {
		NetworkId id = new NetworkId();
		
		setNetworkState(id);
		setPublicIp(id);
		setDns(id);
		setDefaultGateway(id);
		System.err.println(id);
		return id;
	}

	private void setPublicIp(final NetworkId id) {
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


	private void setDefaultGateway(NetworkId id) {
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

	private void setDns(NetworkId id) {
		try{
			List<String> dnsOutput = execProcess("nmcli dev show | grep DNS | awk '/DNS/ {print $2}'");
			String dnsIp = dnsOutput.get(0);
			if(dnsIp != null & !dnsIp.isEmpty()) {
				id.setDnsIp(dnsOutput.get(0));
				//extract the mac address of the given dns server
                final List<String> neighborList = execProcess("ip neighbor | grep " + dnsIp + " | cut -d \\  -f 5");
                if (neighborList.size() > 0) {
                    id.setDnsMac(neighborList.get(0));
                }

			}
		}catch(IOException e) {
			log.warn("Could not determine dns information", e);
		}
		
	}

	private void setNetworkState(NetworkId id) {
		try {
			List<String> ssidOutput = execProcess("iwgetid -r");
			if(ssidOutput.isEmpty() || ssidOutput.get(0).isEmpty()) {
				id.setType(NetworkType.ETHERNET);
			}else {
				id.setType(NetworkType.WIFI);
				id.setSsid(ssidOutput.get(0));
				List<String> bssidOutput = execProcess("iwgetid -a -r");
				if(bssidOutput.size() > 0) {
					id.setBssid(bssidOutput.get(0));
				}
			}
			
			
		
		}catch(IOException e) {
			log.warn("Could not determine network state", e);
		}	
		
	}

	@Override
	public boolean isConnected() {
		return true;
	}
	
	private List<String> execProcess(final String cmd) throws IOException {
        final String[] pipe = {
                "/bin/sh",
                "-c",
                cmd
        };

        final Process exec = Runtime.getRuntime().exec(pipe);

        return IOUtils.readLines(exec.getInputStream(), Charset.defaultCharset());
    }


}
