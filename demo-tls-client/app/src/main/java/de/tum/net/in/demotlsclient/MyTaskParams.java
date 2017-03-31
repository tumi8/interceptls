package de.tum.net.in.demotlsclient;

/**
 * Created by johannes on 22.03.17.
 */

public class MyTaskParams {

    String tls_handshake;
    String tcp_payload;

    MyTaskParams(String tls_handshake, String tcp_payload) {
        this.tls_handshake = tls_handshake;
        this.tcp_payload = tcp_payload;
    }
}
