package de.tum.in.net.model;

public final class TlsConstants {

  public static final String REQUEST_METHOD = "GET";
  public static final String REQUEST_URI = "/test";
  public static final String REQUEST_HTTP_VERSION = "HTTP/1.1";

  /**
   * The complete request line including CRLF according to RFC2616 Sec. 5.1.
   */
  public static final String REQUEST_LINE =
      REQUEST_METHOD + " " + REQUEST_URI + " " + REQUEST_HTTP_VERSION + "\r\n";

  private TlsConstants() {
    //
  }

}
