package de.tum.in.net.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import de.tum.in.net.AnalysisServerConfig;
import de.tum.in.net.session.AnalysisTlsContext;

public class ClientUtil {

  public static Client createDefaultTLSClient(AnalysisServerConfig conf)
      throws KeyManagementException, NoSuchAlgorithmException {
    SSLContext ctx = AnalysisTlsContext.createContext();

    return ClientBuilder.newBuilder().hostnameVerifier(AnalysisTlsContext.getHostnameVerifier())
        .sslContext(ctx).build();

  }

}
